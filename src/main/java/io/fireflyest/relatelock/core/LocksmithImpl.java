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
import org.bukkit.block.Chest;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Directional;
import com.google.common.base.Objects;
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


    @Override
    public boolean lock(@Nonnull Block signBlock, @Nonnull Lock lock) {
        // 获取被贴方块
        final Directional directional = ((Directional) signBlock.getBlockData());
        final Block attachBlock = signBlock.getRelative(directional.getFacing().getOppositeFace());
        
        // 获取关联
        final Relate relate;
        if (attachBlock.getState() instanceof Chest) { // 箱子
            relate = new ChestRelate(signBlock, attachBlock);
        } else if (attachBlock.getState() instanceof TileState) { // 除了箱子外的可操作方块
            relate = new TileRelate(signBlock, attachBlock);
        } else { // 上锁贴着方块附近的方块
            relate = new BlockRelate(signBlock, attachBlock);
        }

        // 判断是否全可锁
        for (Block relateBlock : relate.getRelateBlocks()) {
            if (this.isLocationLocked(relateBlock.getLocation())) {
                return false;
            }
        }
        
        // 添加锁
        final Location signLocation = signBlock.getLocation();
        this.lockLocation(signLocation, signLocation);
        lockOrg.set(signLocation, lock);
        
        // 上锁所有方块
        for (Block relateBlock : relate.getRelateBlocks()) {
            this.lockLocation(relateBlock.getLocation(), signLocation);
        }
        return true;
    }

    @Override
    public void unlock(@Nonnull Location signLocation) {
        // 解除所有关联方块位置锁
        final Set<Location> smembers = locationOrg.smembers(signLocation);
        if (smembers != null) {
            final Iterator<Location> iterator = locationOrg.smembers(signLocation).iterator();
            while (iterator.hasNext()) {
                final Location next = iterator.next();
                if (!next.equals(signLocation)) {
                    this.unlockLocation(next, signLocation);
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
            // 判断是否有权限
            access = Objects.equal(lock.getOwner(), uid)
                  || lock.getShare().contains(uid)
                  || lock.getManager().contains(uid);
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
            final Lock lock = lockOrg.get(signLocation);
            // 判断是否有权限
            access = Objects.equal(lock.getOwner(), uid);
            // 解除锁
            if (access) {
                this.unlockLocation(location, signLocation);
            }   
            // 日志
            lock.getLog().add(LocalDate.now().toString() + " " + name + " destroy:" + access);
        } else if (locationOrg.scard(location) > 1) { // 牌子
            // 获取锁
            final Lock lock = lockOrg.get(location);
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
        final Set<Location> lockedSet = locationMap.computeIfAbsent(chunk, k -> new HashSet<>());
        lockedSet.add(location);
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
        final Set<Location> lockedSet = locationMap.computeIfAbsent(chunk, k -> new HashSet<>());
        lockedSet.remove(location);
        // 解关联
        locationOrg.del(location);
        locationOrg.srem(signLocation, location);
    }

}
