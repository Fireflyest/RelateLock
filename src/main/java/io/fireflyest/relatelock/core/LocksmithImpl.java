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
import org.bukkit.block.Sign;
import org.bukkit.block.TileState;
import org.bukkit.block.data.Openable;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;
import com.google.common.base.Objects;
import io.fireflyest.relatelock.Print;
import io.fireflyest.relatelock.RelateLock;
import io.fireflyest.relatelock.bean.Lock;
import io.fireflyest.relatelock.cache.ConfirmOrganism;
import io.fireflyest.relatelock.cache.LocationOrganism;
import io.fireflyest.relatelock.cache.LockOrganism;
import io.fireflyest.relatelock.config.Config;
import io.fireflyest.relatelock.core.api.Locksmith;
import io.fireflyest.relatelock.util.BlockUtils;
import io.fireflyest.relatelock.util.YamlUtils;
import net.milkbowl.vault.economy.Economy;

/**
 * 锁匠实现类
 * @author Fireflyest
 * @since 1.0
 */
public class LocksmithImpl implements Locksmith {

    private final Config config;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd HH:mm");

    public static final String EMPTY_SIGN_LOC = "The lock exist, but the signLocation is null!";
    public static final String EMPTY_LOCK_LOCS = "The lock exist, but the locations is null!";
    public static final String EMPTY_LOCK = "The lock don't exist!";

    public static final String DESC_SIGN = "sign";
    public static final String DESC_MAIN_SIGN = "main_sign";

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

    /**
     * 玩家开锁确认
     */
    private final ConfirmOrganism confirmOrg = new ConfirmOrganism("confirm");

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
        } else if (attachBlock.getBlockData() instanceof Openable) { //门，可能是多个上下分方块
            Print.RELATE_LOCK.debug("LocksmithImpl.lock() -> openable");
            relate = new OpenableRelate(signBlock, attachBlock);
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
        String desc = "";
        final Lock lock = this.getLock(location);
        if (lock == null) {
            return access;
        }
        if (locationOrg.scard(location) > 1) { // 主牌子
            access = Objects.equal(lock.getOwner(), uid);
            desc = DESC_MAIN_SIGN;
            this.addLog(lock, name, "E", access, desc);
        } else if (locationOrg.scard(location) == 1) { // 关联方块
            if (location.getBlock().getBlockData() instanceof WallSign) { // 牌子
                access = Objects.equal(lock.getOwner(), uid);
                desc = DESC_SIGN;
                this.addLog(lock, name, "E", access, desc);
            } else { // 其他方块
                boolean canUse = false;
                access = lock.getOwner().equals(uid) || this.useAccess(player, lock);
                desc = location.getBlock().getType().name().toLowerCase();
                if (access) {
                    canUse = this.useLocation(location);
                }
                if (canUse) {
                    this.addLog(lock, name, "U", access, desc);
                }
            }
        }
        return access;
    }

    @Override
    public boolean destroy(@Nonnull Location location, @Nonnull String uid, @Nonnull String name) {
        boolean access = true;
        String desc = "";
        final Lock lock = this.getLock(location);
        if (lock == null) {
            return access;
        }
        if (locationOrg.scard(location) == 1) { // 关联方块
            final Location signLocation = locationOrg.get(location);
            final boolean destroyMainSign;
            Validate.notNull(signLocation, EMPTY_SIGN_LOC);
            if (location.getBlock().getBlockData() instanceof WallSign) { // 牌子
                access = Objects.equal(lock.getOwner(), uid);
                destroyMainSign = false;
                desc = DESC_SIGN;
            } else { // 其他方块
                access = Objects.equal(lock.getOwner(), uid) && locationOrg.scard(signLocation) < 3;
                destroyMainSign = true;
                desc = location.getBlock().getType().name().toLowerCase();
            }

            this.addLog(lock, name, "D", access, desc);
            
            if (access) {
                if (destroyMainSign) { // 方块连同牌子一起破坏
                    this.unlock(signLocation);
                    lockOrg.del(signLocation);
                } else { // 破坏牌子
                    this.destroySign(lock, location);
                    this.unlockLocation(location, signLocation);
                }
            }   
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
        } else if (block.getBlockData() instanceof TrapDoor) {
            access = this.placeTrapDoor(block, uid);
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
        // 重置共享玩家
        lock.getShare().clear();

        // 其他
        final Location signLocation = locationOrg.get(location);
        Validate.notNull(signLocation, EMPTY_SIGN_LOC);
        final Set<Location> locations = locationOrg.smembers(signLocation);
        Validate.notNull(locations, EMPTY_LOCK_LOCS);
        for (Location relateLocation : locations) {
            if (location.equals(relateLocation)) {
                continue;
            }
            final Block block = relateLocation.getBlock();
            if (block.getState() instanceof Sign sign) {
                for (String line : sign.getLines()) {
                    this.lineUpdate(lock, line);
                }
            }
        }

        // 更新
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
     * 玩家使用上锁物品
     * 
     * @param player 玩家
     * @param lock 锁
     * @return 是否允许
     */
    private boolean useAccess(@Nonnull Player player, @Nonnull Lock lock) {
        final String uid = player.getUniqueId().toString();
        boolean access = false;
        if (lock.getType().equals(config.lockString())) {
            access = lock.getShare().contains(uid);
        } else if (lock.getType().equals(config.lockPasswordString())) {
            // 
        } else if (lock.getType().equals(config.lockFeeString())) {
            if (confirmOrg.exist(player)) {
                final Economy economy = RelateLock.getPlugin().getEconomy();
                final double fee = NumberConversions.toDouble(lock.getData());
                if (economy.has(player, fee)) {
                    economy.withdrawPlayer(player, fee);
                    economy.depositPlayer(this.getOfflinePlayer(uid), fee);
                    access = true;
                    player.performCommand("money");
                }
            } else {
                confirmOrg.setex(player, 1000 * 10, "fee");
                player.sendMessage("🔒 已被上锁，十秒内再次右键花费" + lock.getData() + "即可使用");
            }
        } else if (lock.getType().equals(config.lockTokenString())
                && lock.getData().startsWith(YamlUtils.DATA_PATH)) {
            final ItemStack hand = player.getInventory().getItemInMainHand();
            final ItemStack token = YamlUtils.deserializeItemStack(lock.getData());
            if (hand.isSimilar(token) && hand.getAmount() >= token.getAmount()) {
                hand.setAmount(hand.getAmount() - token.getAmount());
                access = true;
            }
        }
        return access;
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
    private boolean useLocation(@Nonnull Location location) {
        boolean canUse = false;

        final Location signLocation = locationOrg.get(location);
        Validate.notNull(signLocation, EMPTY_SIGN_LOC);

        final Set<Location> locations = locationOrg.smembers(signLocation);
        if (locations == null) {
            return canUse;
        }


        final Block clickBlock = location.getBlock();
        if (clickBlock.getBlockData() instanceof Chest) { // 箱子
            canUse = true;
        } else if (clickBlock.getBlockData() instanceof Openable) { // 可开关方块
            canUse = true;
            Boolean isOpen = null;
            for (Location useLocation : locations) {
                final Block block = useLocation.getBlock();
                if (block.getBlockData() instanceof Openable openable) {
                    if (isOpen == null) {
                        isOpen = !openable.isOpen();
                    }
                    openable.setOpen(isOpen);
                    block.setBlockData(openable);
                    Print.RELATE_LOCK.debug("LocksmithImpl.useLocation() -> open:{}", isOpen);
                }
            }
        } else if (clickBlock.getState() instanceof TileState) { // 实体方块
            canUse = true;
        }

        return canUse;
    }

    /**
     * 牌子破坏后更新共享
     * 
     * @param location 位置
     * @param uid 玩家UUID
     */
    private void destroySign(@Nonnull Lock lock, @Nonnull Location location) {
        if (location.getBlock().getState() instanceof Sign sign) {
            for (String line : sign.getLines()) {
                this.lineUpdate(lock, line, true);
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
     * 放置活板门
     * 
     * @param block 方块
     * @param uid 玩家uuid
     * @return 是否可放置
     */
    private boolean placeTrapDoor(@Nonnull Block block, @Nonnull String uid) {
        boolean access = true;
        final Block anotherDoor = BlockUtils.anotherTrapDoor(block);
        if (anotherDoor != null && anotherDoor.getBlockData() instanceof TrapDoor
                                && this.isLocationLocked(anotherDoor.getLocation())) {
            final Location signLocation = locationOrg.get(anotherDoor.getLocation());
            Validate.notNull(signLocation, EMPTY_SIGN_LOC);
            final Lock lock = lockOrg.get(signLocation);
            Validate.notNull(lock, EMPTY_LOCK);
            access = Objects.equal(lock.getOwner(), uid);
            if (access) {
                Print.RELATE_LOCK.debug("LocksmithImpl.place() -> trap door");
                this.lockLocation(block.getLocation(), signLocation);
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
     * 
     * @param lock 锁
     * @param line 行
     * @param destroy 是否破坏
     * @return 更新后的行
     */
    private String lineUpdate(@Nonnull Lock lock, @Nonnull String line, boolean destroy) {
        final String[] entries = StringUtils.split(line, ',');
        final StringBuilder sb = new StringBuilder();
        for (String entry : entries) {
            final OfflinePlayer offlinePlayer = this.getOfflinePlayer(entry);
            if (offlinePlayer != null) { // 文本是玩家名称
                final String uid = offlinePlayer.getUniqueId().toString();
                if (destroy) {
                    lock.getShare().remove(uid);
                } else {
                    if (!lock.getShare().contains(uid)) {
                        sb.append(",").append(offlinePlayer.getName());
                        lock.getShare().add(uid);
                    } 
                }
            } else { // 非玩家名称直接加回去
                sb.append(",").append(entry);
            }
        }
        return StringUtils.removeStart(sb.toString(), ",");
    }

    /**
     * 牌子内容更新
     * 
     * @param lock 锁
     * @param line 行
     * @return 更新后的行
     */
    private String lineUpdate(@Nonnull Lock lock, @Nonnull String line) {
        return this.lineUpdate(lock, line, false);
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
     * 获取离线玩家
     * 
     * @param playerName 玩家名称
     * @return 离线玩家
     */
    @Nullable
    private OfflinePlayer getOfflinePlayer(String playerName) {
        OfflinePlayer player = Bukkit.getPlayerExact(playerName);
        // 如果玩家离线
        if (player == null) {
            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (playerName.equals(offlinePlayer.getName())) {
                    player = offlinePlayer;
                }
            }
        }
        return player;
    }

    /**
     * 添加锁操作日志
     * 
     * @param lock 锁
     * @param uid 玩家UUID
     * @param doWhat 玩家操作
     * @param access 是否允许
     */
    private void addLog(@Nonnull Lock lock, 
                        @Nonnull String who, 
                        String doWhat, 
                        boolean access,
                        String desc) {
        lock.getLog().add("[§7" + LocalDateTime.now().format(formatter) + "§r] " 
                              + who 
                              + (access ? " §a" : " §c")
                              + doWhat + "§r:"
                              + desc);
    }

}
