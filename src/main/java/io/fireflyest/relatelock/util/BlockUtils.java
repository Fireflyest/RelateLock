package io.fireflyest.relatelock.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.TrapDoor;

/**
 * 方块相关工具类
 * @author Fireflyest
 * @since 1.0
 */
public final class BlockUtils {
    
    private BlockUtils() {
        // 工具类
    }

    /**
     * 获取第一视角左侧面
     * @param face 当前朝向
     * @return 左侧面
     */
    public static BlockFace leftFace(@Nonnull BlockFace face) {
        return switch (face) {
            case SOUTH -> BlockFace.EAST;
            case EAST -> BlockFace.NORTH;
            case NORTH -> BlockFace.WEST;
            case WEST -> BlockFace.SOUTH;
            default -> BlockFace.SELF;
        };
    }

    /**
     * 获取第一视角右侧面
     * @param face 当前朝向
     * @return 右侧面
     */
    public static BlockFace rightFace(@Nonnull BlockFace face) {
        return switch (face) {
            case SOUTH -> BlockFace.WEST;
            case EAST -> BlockFace.SOUTH;
            case NORTH -> BlockFace.EAST;
            case WEST -> BlockFace.NORTH;
            default -> BlockFace.SELF;
        };
    }

    /**
     * 获取另一扇门
     * 
     * @param block 门方块
     * @return 另一扇门方块
     */
    @Nullable
    public static Block anotherDoor(@Nonnull Block block) {
        Block another = null;
        if (block.getBlockData() instanceof Door door) {
            another = switch (door.getHinge()) {
                case LEFT -> block.getRelative(BlockUtils.rightFace(door.getFacing()));
                case RIGHT -> block.getRelative(BlockUtils.leftFace(door.getFacing()));
                default -> null;
            };
        }
        return another;
    }

    /**
     * 获取另一扇活板门
     * 
     * @param block 活板门方块
     * @return 另一扇活板门方块
     */
    @Nullable
    public static Block anotherTrapDoor(@Nonnull Block block) {
        Block another = null;
        if (block.getBlockData() instanceof TrapDoor trapDoor) {
            another = block.getRelative(trapDoor.getFacing());
        }
        return another;
    }

    /**
     * 获取另一半箱子
     * 
     * @param block 箱子方块
     * @return 另一半箱子方块
     */
    @Nullable
    public static Block anotherChest(@Nonnull Block block) {
        Block another = null;
        if (block.getBlockData() instanceof Chest chest) {
            another = switch (chest.getType()) {
                case LEFT -> block.getRelative(BlockUtils.rightFace(chest.getFacing()));
                case RIGHT -> block.getRelative(BlockUtils.leftFace(chest.getFacing()));
                case SINGLE -> null;
                default -> null;
            };
        }
        return another;
    }

    /**
     * 获取附着方块
     * @param block 方块
     * @return 附着方块
     */
    @Nullable
    public static Block blockAttach(@Nonnull Block block) {
        Block attach = null;
        if (block.getBlockData() instanceof Directional directional) {
            attach = block.getRelative(directional.getFacing().getOppositeFace());
        }
        return attach;
    }

}
