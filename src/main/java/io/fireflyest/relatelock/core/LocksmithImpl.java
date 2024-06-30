package io.fireflyest.relatelock.core;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.base.Objects;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.cache.LocationOrganism;
import io.fireflyest.relatelock.cache.LockOrganism;
import io.fireflyest.relatelock.core.api.Locksmith;

/**
 * 锁匠实现类
 * @author Fireflyest
 * @since 1.0
 */
public class LocksmithImpl implements Locksmith {

    /**
     * 所有上锁的位置
     */
    private final Map<Chunk, Set<Location>> locationMap = new HashMap<>();

    /**
     * 要保存的数据，牌子位置保存锁数据，其他位置保存牌子位置
     */
    private final LocationOrganism locationOrg = new LocationOrganism("relate");

    /**
     * 牌子的位置对应的一个锁
     */
    private final LockOrganism lockOrg = new LockOrganism("lock");

    public LocksmithImpl() {
        //
    }

    /**
     * 加载数据
     * @param plugin 插件
     */
    public void load(JavaPlugin plugin) {
        this.locationOrg.load(plugin);
        this.lockOrg.load(plugin);
        // 方块上锁
        for (Location location : this.locationOrg.keySet()) {
            final Chunk chunk = location.getChunk();
            locationMap.computeIfAbsent(chunk, k -> new HashSet<>()).add(location);
        }
    }

    /**
     * 保存数据
     * @param plugin 插件
     */
    public void save(JavaPlugin plugin) {
        this.locationOrg.save(plugin);
        this.lockOrg.save(plugin);
    }

    @Override
    public boolean lock(@Nonnull Block signBlock, @Nonnull Lock lock) {
        // 获取被贴方块
        final Directional directional = ((Directional) signBlock.getBlockData());
        final Block attachBlock = signBlock.getRelative(directional.getFacing().getOppositeFace());
        
        // 获取关联
        final Relate relate;
        if (attachBlock.getBlockData() instanceof Chest) { // 箱子
            Print.RELATE_LOCK.debug("LocksmithImpl.lock() -> chest");
            relate = new ChestRelate(signBlock, attachBlock);
        } else if (attachBlock.getBlockData() instanceof Door) { //门，可能是多个上下分方块
            Print.RELATE_LOCK.debug("LocksmithImpl.lock() -> door");
            relate = new DoorRelate(signBlock, attachBlock);
        } else if (attachBlock.getBlockData() instanceof Bisected) { // 上下分方块
            Print.RELATE_LOCK.debug("LocksmithImpl.lock() -> bisected");
            relate = new BisectedRelate(signBlock, attachBlock);
        } else if (attachBlock.getState() instanceof TileState) { // 其他可更新方块
            Print.RELATE_LOCK.debug("LocksmithImpl.lock() -> tile");
            relate = new TileRelate(signBlock, attachBlock);
        } else { // 上锁贴着方块附近的方块
            Print.RELATE_LOCK.debug("LocksmithImpl.lock() -> block");
            relate = new BlockRelate(signBlock, attachBlock);
        }

        final Set<Block> relateBlocks = relate.getRelateBlocks();

        // 无可锁方块
        if (relateBlocks.isEmpty()) {
            return false;
        }

        // 判断是否全可锁
        for (Block relateBlock : relateBlocks) {
            if (this.isLocationLocked(relateBlock.getLocation())) {
                return false;
            }
        }
        
        // 添加锁
        final Location signLocation = signBlock.getLocation();
        this.lockLocation(signLocation, signLocation);
        lockOrg.set(signLocation, lock);
        
        // 上锁所有方块
        for (Block relateBlock : relateBlocks) {
            this.lockLocation(relateBlock.getLocation(), signLocation);
        }
        return true;
    }

    @Override
    public void unlock(@Nonnull Location signLocation) {
        // 解除所有关联方块位置锁
        final Set<Location> smembers = locationOrg.smembers(signLocation);
        if (smembers != null) {
            final Iterator<Location> iterator = smembers.iterator();
            while (iterator.hasNext()) {
                final Location next = iterator.next();
                if (!next.equals(signLocation)) {
                    // 解锁
                    final Chunk chunk = next.getChunk();
                    locationMap.computeIfAbsent(chunk, k -> new HashSet<>()).remove(next);
                    // 解关联
                    locationOrg.del(next);
                    iterator.remove();
                }
            }
        }
        this.unlockLocation(signLocation, signLocation);
    }

    @Override
    public boolean use(@Nonnull Location location, @Nonnull String uid, @Nonnull String name) {
        boolean access = true;
        if (this.isLocationLocked(location) && locationOrg.scard(location) == 1) {
            // 获取锁
            final Location signLocation = locationOrg.get(location);
            final Lock lock = lockOrg.get(signLocation);
            if (lock == null) {
                return access;
            }
            // 判断是否有权限
            access = Objects.equal(lock.getOwner(), uid)
                  || lock.getShare().contains(uid)
                  || lock.getManager().contains(uid);
            if (access) {
                this.useLocation(location);
            }
            // 日志
            lock.getLog().add(LocalDate.now().toString() + " " + name + " use:" + access);
        }
        return access;
    }

    @Override
    public boolean destroy(@Nonnull Location location, @Nonnull String uid, @Nonnull String name) {
        boolean access = true;
        if (locationOrg.scard(location) == 1) { // 关联方块
            // 获取锁
            final Location signLocation = locationOrg.get(location);
            // 多关联的情况下需要先解锁再破坏
            if (locationOrg.scard(signLocation) > 2) {
                return false;
            }
            final Lock lock = lockOrg.get(signLocation);
            if (lock == null) {
                return access;
            }
            // 判断是否有权限
            access = Objects.equal(lock.getOwner(), uid);
            // 解除锁
            if (access) {
                this.unlockLocation(location, signLocation);
                this.unlockLocation(signLocation, signLocation);
            }   
            // 日志
            lock.getLog().add(LocalDate.now().toString() + " " + name + " destroy:" + access);
        } else if (locationOrg.scard(location) > 1) { // 牌子
            // 获取锁
            final Lock lock = lockOrg.get(location);
            if (lock == null) {
                return access;
            }
            // 判断是否有权限
            access = Objects.equal(lock.getOwner(), uid);
            if (access) {
                this.unlock(location);
                lockOrg.del(location);
            }
        }
        return access;
    }

    @Override
    public boolean isLocationLocked(@Nonnull Location location) {
        final Set<Location> lockedSet = locationMap.get(location.getChunk());
        return lockedSet != null && lockedSet.contains(location);
    }

    /**
     * 上锁方块
     * @param location 方块位置
     * @param signLocation 牌子位置
     */
    private void lockLocation(@Nonnull Location location, @Nonnull Location signLocation) {
        // 锁
        final Chunk chunk = location.getChunk();
        locationMap.computeIfAbsent(chunk, k -> new HashSet<>()).add(location);
        // 关联
        locationOrg.set(location, signLocation);
        locationOrg.sadd(signLocation, location);
    }

    /**
     * 解锁方块
     * @param location 方块位置
     * @param signLocation 牌子位置
     */
    private void unlockLocation(@Nonnull Location location, @Nonnull Location signLocation) {
        // 解锁
        final Chunk chunk = location.getChunk();
        locationMap.computeIfAbsent(chunk, k -> new HashSet<>()).remove(location);
        // 解关联
        locationOrg.del(location);
        locationOrg.srem(signLocation, location);
    }

    /**
     * 使用方块
     * @param location 方块位置
     */
    private void useLocation(@Nonnull Location location) {
        final Location signLocation = locationOrg.get(location);

        Boolean isOpen = null;
        for (Location useLocation : locationOrg.smembers(signLocation)) {
            final Block block = useLocation.getBlock();
            if (block.getBlockData() instanceof Door door) { //门
                if (isOpen == null) {
                    isOpen = !door.isOpen();
                }
                door.setOpen(isOpen);
                block.setBlockData(door);
                Print.RELATE_LOCK.debug("LocksmithImpl.useLocation() -> door open:{}", isOpen);
            }
        }
    }

}
