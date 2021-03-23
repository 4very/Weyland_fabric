package sommea.weyland;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.weblite.objc.Client;
import io.netty.handler.logging.LogLevel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.server.ServerStartCallback;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TeleportCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.FeatureConfig;
import sommea.weyland.systems.HUD;
import sommea.weyland.world.features.spawnAltarFeature;

@Environment(EnvType.CLIENT)
public class Weyland implements ModInitializer {
	public static final String MOD_ID = "weyland";
	public static Logger LOGGER = LogManager.getLogger();
	public HUD hud;
	private static MinecraftClient client = MinecraftClient.getInstance();

    public static void log(Level level, String message) {
        LOGGER.log(level, "[" + MOD_ID + "] " + message);
    }




	@Override
	public void onInitialize() {
		hud = new HUD();
		registerFeatures();
		registerWorld();
		

		// if client.world.getDimension().
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		System.out.println("Hello Fabric world!");
	}


	// features 
	private static final Feature<DefaultFeatureConfig> SPAWN_ALTAR_FEATURE = new spawnAltarFeature(DefaultFeatureConfig.CODEC);

	// configured features
	public static final ConfiguredFeature<?, ?> SPAWN_ALTAR = SPAWN_ALTAR_FEATURE.configure(FeatureConfig.DEFAULT);
	

	private void registerFeatures() {
		Registry.register(Registry.FEATURE, new Identifier("weyland", "spawn_altar"), SPAWN_ALTAR_FEATURE);
		RegistryKey<ConfiguredFeature<?, ?>> spawnAltar = RegistryKey.of(Registry.CONFIGURED_FEATURE_WORLDGEN, new Identifier("weyland", "spawn_altar"));
		Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, spawnAltar.getValue(), SPAWN_ALTAR);
	}


	// region dimension stuff
	public static final Identifier MAIN_DIM_ID = new Identifier(Weyland.MOD_ID, "main");
	public static final RegistryKey<World> WEYLAND_WORLD = RegistryKey.of(Registry.DIMENSION, Weyland.MAIN_DIM_ID);
	
	private void registerWorld(){
		Registry.register(Registry.CHUNK_GENERATOR, MAIN_DIM_ID, VoidChunkGenerator.CODEC);
		// ServerStartCallback.EVENT.register(server -> {
		// 	// MinecraftClient client = MinecraftClient.getInstance();
		// 	// if (client.player.world.getRegistryKey().equals(World.OVERWORLD)){
		// 	TeleportTarget target = new TeleportTarget(Vec3d.ZERO, new Vec3d(1, 64, 1), 45f, 60f);
		// 	FabricDimensions.teleport(client.player, server.getWorld(World.OVERWORLD), target);
		// 	Weyland.log(Level.INFO, "STARTED");
		// 		// client.player.moveToWorld(server.getWorld(Weyland.WEYLAND_WORLD_KEY));
		// 	// }
		// });

	}

}
