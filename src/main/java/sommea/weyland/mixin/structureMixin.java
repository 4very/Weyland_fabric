package sommea.weyland.mixin;


import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.level.ServerWorldProperties;
import sommea.weyland.Weyland;

@Mixin(MinecraftServer.class)
public class structureMixin {

    @Inject(at = @At("TAIL"), method = "setupSpawn", locals=LocalCapture.CAPTURE_FAILSOFT)
    private static void setupSpawn(ServerWorld world, ServerWorldProperties serverWorldProperties, boolean bonusChest, boolean debugWorld, boolean bl, CallbackInfo info, ChunkGenerator chunkGenerator) {
        Weyland.log(Level.INFO, "Generating Spawn Altar in dimension "+serverWorldProperties.getLevelName());
        ConfiguredFeature<?, ?> altar = Weyland.SPAWN_ALTAR;
        altar.generate(world, chunkGenerator, world.random, new BlockPos(serverWorldProperties.getSpawnX(), serverWorldProperties.getSpawnY(), serverWorldProperties.getSpawnZ()));
    }
}

