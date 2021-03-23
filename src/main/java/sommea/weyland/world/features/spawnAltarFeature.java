package sommea.weyland.world.features;

import java.util.Random;

import com.mojang.serialization.Codec;

import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.Heightmap;

public class spawnAltarFeature extends Feature<DefaultFeatureConfig> {
    public spawnAltarFeature(Codec<DefaultFeatureConfig> config) {
        super(config);
      }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, DefaultFeatureConfig config) {
        BlockPos topPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, pos);
        Direction offset = Direction.NORTH;
     
        for (int y = 1; y <= 15; y++) {
          offset = offset.rotateYClockwise();
          world.setBlockState(topPos.up(y).offset(offset), Blocks.STONE.getDefaultState(), 3);
        }
     
        return true;
    }
    
}
