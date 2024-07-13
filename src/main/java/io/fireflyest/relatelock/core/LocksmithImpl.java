package io.fireflyest.relatelock.core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dropper;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.google.common.base.Objects;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.cache.LocationOrganism;
import io.fireflyest.relatelock.cache.LockOrganism;
import io.fireflyest.relatelock.config.Config;
import io.fireflyest.relatelock.core.api.Locksmith;
import io.fireflyest.relatelock.util.BlockUtils;

/**
 * 锁匠实现类
 * @author Fireflyest
 * @since 1.0
 */
public class LocksmithImpl implements Locksmith {

    private final Config config;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd HH:mm");

    public static final String EMPTY_SIGN_LOC = "The lock exist, but the signLocation is null!";
    public static final String EMPTY_LOCK = "The lock don't exist!";

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

    public LocksmithImpl(@Nonnull Config config) {
        this.config = config;
    }

    /**
     * 加载数据
     * @param plugin 插件
     */
    public void load(@Nonnull JavaPlugin plugin, @Nullable String entry) {
        if (entry == null) {
            entry = "latest";
        }
        this.locationOrg.load(plugin, entry);
        this.lockOrg.load(plugin, entry);
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
    public void save(@Nonnull JavaPlugin plugin, @Nullable String entry) {
        if (entry == null) {
            entry = "latest";
        }
        this.locationOrg.save(plugin, entry);
        this.lockOrg.save(plugin, entry);
    }

    /**
     * 整理日志，删除较久的日志
     * 
     * @param lock 锁
     */
    public void trimLogs(@Nonnull Lock lock) {
        lock.getLog().removeIf(log -> {
            final LocalDateTime time = LocalDateTime.parse(log.substring(3, 15), formatter);
            return Duration.between(time, LocalDateTime.now()).toDays() > 7;
        });
    }

    @Override
    public boolean lock(@Nonnull Block signBlock, @Nonnull Lock lock) {
        // 获取被贴方块
        final Block attachBlock = BlockUtils.blockAttach(signBlock);
        if (attachBlock == null || !(signBlock.getBlockData() instanceof WallSign)) {
            return false;
        }
        
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
        } else if (attachBlock.getState() instanceof TileState) { // 方块实体
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
        final Set<Location> locations = locationOrg.smembers(signLocation);
        if (locations != null) {
            final Iterator<Location> iterator = locations.iterator();
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
    public boolean use(@Nonnull Location location, @Nonnull Player player) {
        final String uid = player.getUniqueId().toString();
        final String name = player.getName();

        boolean access = true;
        final Lock lock = this.getLock(location);
        if (lock == null) {
            return access;
        }
        if (locationOrg.scard(location) > 1) { // 主牌子
            access = Objects.equal(lock.getOwner(), uid);
            this.addLog(lock, name, "use", access);
        } else if (locationOrg.scard(location) == 1) { // 关联方块
            if (location.getBlock().getBlockData() instanceof WallSign) { // 牌子
                access = Objects.equal(lock.getOwner(), uid);
            } else { // 其他方块
                access = Objects.equal(lock.getOwner(), uid)
                                    || lock.getShare().contains(uid)
                                    || lock.getManager().contains(uid);
                if (access) {
                    this.useLocation(location);
                }
            }
            // 日志
            this.addLog(lock, name, "use", access);
        }
        return access;
    }

    @Override
    public boolean destroy(@Nonnull Location location, @Nonnull String uid, @Nonnull String name) {
        boolean access = true;
        final Lock lock = this.getLock(location);
        if (lock == null) {
            return access;
        }
        if (locationOrg.scard(location) == 1) { // 关联方块
            // 获取锁
            final Location signLocation = locationOrg.get(location);
            Validate.notNull(signLocation, EMPTY_SIGN_LOC);
            // 多关联的情况下需要先解锁再破坏
            if (locationOrg.scard(signLocation) > 2) {
                access = false;
                this.addLog(lock, name, "destroy", access);
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
            this.addLog(lock, name, "destroy", access);
        } else if (locationOrg.scard(location) > 1) { // 主牌子
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
    public boolean place(@Nonnull Block block, @Nonnull String uid) {
        boolean access = true;

        if (block.getBlockData() instanceof Chest) {
            access = this.placeChest(block, uid);
        } else if (block.getBlockData() instanceof Door) {
            access = this.placeDoor(block, uid);
        } else if (block.getBlockData() instanceof WallSign) {
            access = this.placeSign(block, uid);
        } else if (block.getState() instanceof Dropper) {
            access = this.placeDropper(block, uid);
        }

        return access;
    }

    @Override
    public boolean isLocationLocked(@Nonnull Location location) {
        final Set<Location> lockedSet = locationMap.get(location.getChunk());
        return lockedSet != null && lockedSet.contains(location);
    }

    @Override
    public String[] signChange(@Nonnull Location location, 
                               @Nonnull String uid, 
                               @Nonnull String[] lines) {

        final Lock lock = this.getLock(location);
        if (lock == null) {
            return lines;
        }
        if (locationOrg.scard(location) == 1) { // 牌子
            for (int i = 0; i < lines.length; i++) {
                lines[i] = this.lineUpdate(lock, lines[i]);
            }
        } else if (locationOrg.scard(location) > 1) { // 主牌子
            lines[0] = "🔒 §l" + this.getPlayerName(lock.getOwner());
            lines[1] = this.lineUpdate(lock, lines[1]); 
            lines[2] = this.lineUpdate(lock, lines[2]); 
            lines[3] = this.lineUpdate(lock, lines[3]); 
        }

        return lines;
    }

    @Override
    @Nullable
    public Lock getLock(@Nonnull Location location) {
        Lock lock = null;
        if (locationOrg.scard(location) == 1) { // 关联方块
            final Location signLocation = locationOrg.get(location);
            Validate.notNull(signLocation, EMPTY_SIGN_LOC);
            lock = lockOrg.get(signLocation);
        } else if (locationOrg.scard(location) > 1) { // 主牌子
            lock = lockOrg.get(location);
        }
        return lock;
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
        Validate.notNull(signLocation, EMPTY_SIGN_LOC);

        final Set<Location> locations = locationOrg.smembers(signLocation);
        if (locations == null) {
            return;
        }


        final Block clickBlock = location.getBlock();
        if (clickBlock.getBlockData() instanceof Door) { //门
            Boolean isOpen = null;
            for (Location useLocation : locations) {
                final Block block = useLocation.getBlock();
                if (block.getBlockData() instanceof Door door) {
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

    /**
     * 放置箱子
     * 
     * @param block 方块
     * @param uid 玩家uuid
     * @return 是否可放置
     */
    private boolean placeChest(@Nonnull Block block, @Nonnull String uid) {
        boolean access = true;
        final Block anotherChest = BlockUtils.anotherChest(block);
        if (anotherChest != null && anotherChest.getBlockData() instanceof Chest
                                 && this.isLocationLocked(anotherChest.getLocation())) {
            final Location signLocation = locationOrg.get(anotherChest.getLocation());
            Validate.notNull(signLocation, EMPTY_SIGN_LOC);
            final Lock lock = lockOrg.get(signLocation);
            Validate.notNull(lock, EMPTY_LOCK);
            access = Objects.equal(lock.getOwner(), uid);
            if (access) {
                Print.RELATE_LOCK.debug("LocksmithImpl.place() -> chest");
                this.lockLocation(block.getLocation(), signLocation);
            }
        }
        return access;
    }

    /**
     * 放置门
     * 
     * @param block 方块
     * @param uid 玩家uuid
     * @return 是否可放置
     */
    private boolean placeDoor(@Nonnull Block block, @Nonnull String uid) {
        boolean access = true;
        final Block anotherDoor = BlockUtils.anotherDoor(block);
        if (anotherDoor != null && anotherDoor.getBlockData() instanceof Door
                                && this.isLocationLocked(anotherDoor.getLocation())) {
            final Location signLocation = locationOrg.get(anotherDoor.getLocation());
            Validate.notNull(signLocation, EMPTY_SIGN_LOC);
            final Lock lock = lockOrg.get(signLocation);
            Validate.notNull(lock, EMPTY_LOCK);
            access = Objects.equal(lock.getOwner(), uid);
            if (access) {
                Print.RELATE_LOCK.debug("LocksmithImpl.place() -> door");
                final BisectedRelate relate = new BisectedRelate(null, block);
                for (Block relateBlock : relate.getRelateBlocks()) {
                    this.lockLocation(relateBlock.getLocation(), signLocation);
                }
            }
        }
        return access;
    }

    /**
     * 放置牌子
     * 
     * @param block 方块
     * @param uid 玩家uuid
     * @return 是否可放置
     */
    private boolean placeSign(@Nonnull Block block, @Nonnull String uid) {
        boolean access = true;
        final Block attachBlock = BlockUtils.blockAttach(block);
        if (attachBlock != null && this.isLocationLocked(attachBlock.getLocation())) {
            final Location signLocation = locationOrg.get(attachBlock.getLocation());
            Validate.notNull(signLocation, EMPTY_SIGN_LOC);
            final Lock lock = lockOrg.get(signLocation);
            Validate.notNull(lock, EMPTY_LOCK);
            access = Objects.equal(lock.getOwner(), uid);
            if (access) {
                Print.RELATE_LOCK.debug("LocksmithImpl.place() -> sign");
                this.lockLocation(block.getLocation(), signLocation);
            }
        }
        return access;
    }

    /**
     * 放置漏斗
     * 
     * @param block 方块
     * @param uid 玩家uuid
     * @return 是否可放置
     */
    private boolean placeDropper(@Nonnull Block block, @Nonnull String uid) {
        boolean access = true;
        final Block attachBlock = block.getRelative(BlockFace.UP);
        if (attachBlock != null && this.isLocationLocked(attachBlock.getLocation())) {
            final Location signLocation = locationOrg.get(attachBlock.getLocation());
            Validate.notNull(signLocation, EMPTY_SIGN_LOC);
            final Lock lock = lockOrg.get(signLocation);
            Validate.notNull(lock, EMPTY_LOCK);
            access = Objects.equal(lock.getOwner(), uid);
            if (access) {
                Print.RELATE_LOCK.debug("LocksmithImpl.place() -> sign");
                this.lockLocation(block.getLocation(), signLocation);
            }
        }
        return access;
    }

    /**
     * 牌子内容更新
     * @param lock 锁
     * @param line 行
     * @return 更新后的行
     */
    private String lineUpdate(@Nonnull Lock lock, @Nonnull String line) {
        // 注释
        if (StringUtils.contains(line, '[') && StringUtils.contains(line, ']')) {
            return line;
        }
        // 添加玩家
        final String[] entries = StringUtils.split(line, ',');
        final StringBuilder sb = new StringBuilder();
        for (String entry : entries) {
            sb.append(this.updateShare(lock, entry));
        }
        return StringUtils.removeStart(sb.toString(), ",");
    }

    /**
     * 更新共享
     * 
     * @param lock 锁
     * @param entry 输入的玩家名称
     * @return 更新的玩家名称
     */
    private StringBuilder updateShare(@Nonnull Lock lock, @Nonnull String entry) {
        final StringBuilder sb = new StringBuilder();
        String playerName = null;
        String uid = null;
        if (entry.startsWith(config.managerSymbol())) { // 管理
            playerName = StringUtils.removeStart(entry, config.managerSymbol());
            uid = this.getPlayerUid(playerName);
            if (uid != null) {
                sb.append(",").append(config.managerSymbol())
                  .append("§r").append(playerName);
                lock.getManager().add(uid);
            }
        } else if (entry.startsWith(config.removeSymbol())) { // 移除
            playerName = StringUtils.removeStart(entry, config.removeSymbol());
            uid = this.getPlayerUid(playerName);
            if (uid != null) {
                lock.getShare().remove(uid);
                lock.getManager().remove(uid);
            }
        } else { // 默认共享
            playerName = StringUtils.removeStart(entry, config.shareSymbol());
            uid = this.getPlayerUid(playerName);
            if (uid != null) {
                sb.append(",").append(config.shareSymbol())
                  .append("§r").append(playerName);
                lock.getShare().add(uid);
            }
        }
        return sb;
    }

    /**
     * 根据玩家UUID获取玩家名称
     * @param uid 玩家UUID
     * @return 玩家名称
     */
    private String getPlayerName(String uid) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uid));
        return offlinePlayer.getName();
    }

    /**
     * 获取玩家UUID
     * @param playerName 玩家名称
     * @return 玩家UUID
     */
    @Nullable
    private String getPlayerUid(String playerName) {
        final Player player = Bukkit.getPlayerExact(playerName);
        // 如果玩家离线
        if (player == null) {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (playerName.equals(offlinePlayer.getName())) {
                    return offlinePlayer.getUniqueId().toString();
                }
            }
            return null;
        }
        return player.getUniqueId().toString();
    }

    /**
     * 添加锁操作日志
     * 
     * @param lock 锁
     * @param uid 玩家UUID
     * @param doWhat 玩家操作
     * @param access 是否允许
     */
    private void addLog(@Nonnull Lock lock, @Nonnull String who, String doWhat, boolean access) {
        lock.getLog().add("[§7" + LocalDateTime.now().format(formatter) + "§r] " 
                              + who 
                              + (access ? " §a" : " §c")
                              + doWhat + "§r");
    }

}
