package sommea.weyland.systems;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;



@Environment(EnvType.CLIENT)
public class HUD {
    private MinecraftClient client;

    public HUD() {
        client = MinecraftClient.getInstance();
        HudRenderCallback.EVENT.register((__, ___) -> {
            this.render();
        });
    }

    private int maxStam = 500;
    private int stamDelay = 200;
    private int wait;
    private int stam = maxStam;

    private void render() {
        final PlayerEntity player = client.player;
        final TextRenderer textRenderer = client.textRenderer;
        final MatrixStack matrixStack = new MatrixStack();

        if (player == null) return;


        RenderSystem.enableBlend();

        List<Runnable> statusEffectsRunnables = Lists.newArrayListWithExpectedSize(1);

        if(player.isSprinting()){
            stam--;
            wait = stamDelay;
        } else {
            wait--;
        }
        if (stam <= 0 && player.isSprinting()){
            player.setSprinting(false);
        }
        if(!player.isSprinting() && stam < maxStam && wait <= 0){
            stam++;
        }
        String buildStr = "";
        for (int i = 0; i < Math.ceil(stam/20F); i++) {
            buildStr += "|";
        }

        final String overlayStr = buildStr;

        statusEffectsRunnables.add(() -> {
            textRenderer.draw(matrixStack, overlayStr, 15, 15, 0x000000);
        });
        statusEffectsRunnables.forEach(Runnable::run);

    }

}