package io.fireflyest.relatelock;

import org.bukkit.block.BlockFace;

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
    public static BlockFace leftFace(BlockFace face) {
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
    public static BlockFace rightFace(BlockFace face) {
        return switch (face) {
            case SOUTH -> BlockFace.WEST;
            case EAST -> BlockFace.SOUTH;
            case NORTH -> BlockFace.EAST;
            case WEST -> BlockFace.NORTH;
            default -> BlockFace.SELF;
        };
    }

}
