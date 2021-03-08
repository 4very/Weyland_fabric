package sommea.weyland;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.StatusEffectSpriteManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;



@Environment(EnvType.CLIENT)
public class HUD {
    private MinecraftClient client;

    public HUD() {
        client = MinecraftClient.getInstance();
        HudRenderCallback.EVENT.register((__, ___) -> {
            this.render();
        });
    }

    private void render() {
        final PlayerEntity player = client.player;
        final TextRenderer textRenderer = client.textRenderer;
        final MatrixStack matrixStack = new MatrixStack();
        
        if (player == null) return;


        RenderSystem.enableBlend();

        List<Runnable> statusEffectsRunnables = Lists.newArrayListWithExpectedSize(1);

        final int spriteSize = 18;

        String formattedDuration = "testing";

        final int x = 3;
        final int y = 21;


        statusEffectsRunnables.add(() -> {
            final float textYOffset = spriteSize / 2f - textRenderer.fontHeight / 2.5f;
            int color = 0xFF5555;
            textRenderer.draw(matrixStack, formattedDuration, x + spriteSize + 3, y + textYOffset, color);
        });
        statusEffectsRunnables.forEach(Runnable::run);

    }

}