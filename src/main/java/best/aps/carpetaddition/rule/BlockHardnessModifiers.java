package best.aps.carpetaddition.rule;

import best.aps.carpetaddition.CarpetAPSAdditionSettings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

public class BlockHardnessModifiers {
    /**
     * 深板岩和与深板岩硬度相同的变种
     */
    private static final List<Block> DEEPSLATE = List.of(
            // 深板岩
            Blocks.DEEPSLATE,
            // 雕纹深板岩
            Blocks.CHISELED_DEEPSLATE,
            // 磨制深板岩
            Blocks.POLISHED_DEEPSLATE,
            // 磨制深板岩台阶
            Blocks.POLISHED_DEEPSLATE_SLAB,
            // 磨制深板岩楼梯
            Blocks.POLISHED_DEEPSLATE_STAIRS,
            // 磨制深板岩墙
            Blocks.POLISHED_DEEPSLATE_WALL,
            // 深板岩砖台阶
            Blocks.DEEPSLATE_BRICK_SLAB,
            // 深板岩砖楼梯
            Blocks.DEEPSLATE_BRICK_STAIRS,
            // 深板岩砖墙
            Blocks.DEEPSLATE_BRICK_WALL
    );

    /**
     * 深板岩圆石和与深板岩圆石硬度相同的变种
     */
    private static final List<Block> COBBLED_DEEPSLATE = List.of(
            // 深板岩圆石
            Blocks.COBBLED_DEEPSLATE,
            // 深板岩圆石台阶
            Blocks.COBBLED_DEEPSLATE_SLAB,
            // 深板岩圆石楼梯
            Blocks.COBBLED_DEEPSLATE_STAIRS,
            // 深板岩圆石墙
            Blocks.COBBLED_DEEPSLATE_WALL,
            // 裂纹深板岩砖
            Blocks.DEEPSLATE_BRICKS,
            // 裂纹深板岩瓦
            Blocks.DEEPSLATE_TILES,
            // 深板岩瓦台阶
            Blocks.DEEPSLATE_TILE_SLAB,
            // 深板岩瓦楼梯
            Blocks.DEEPSLATE_TILE_STAIRS,
            // 深板岩瓦墙
            Blocks.DEEPSLATE_TILE_WALL,
            // 裂纹深板岩砖
            Blocks.CRACKED_DEEPSLATE_BRICKS,
            // 裂纹深板岩瓦
            Blocks.CRACKED_DEEPSLATE_TILES
    );

    // 获取方块硬度
    public static float getHardness(Block block, BlockGetter world, BlockPos pos, float defaultValue) {
        // 易碎深板岩
        if (CarpetAPSAdditionSettings.softDeepslate) {
            // 深板岩
            if (DEEPSLATE.contains(block)) {
                return Blocks.STONE.defaultDestroyTime();
            }
            // 深板岩圆石
            if (COBBLED_DEEPSLATE.contains(block)) {
                return Blocks.COBBLESTONE.defaultDestroyTime();
            }
        }
        return defaultValue;
    }
}
