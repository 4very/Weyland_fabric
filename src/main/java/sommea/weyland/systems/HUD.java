package sommea.weyland.systems;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;



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

    private int maxCarry = 250;
    private int carry = maxCarry;

    private UUID attrUUID = UUID.randomUUID();

    private void render() {
        final PlayerEntity player = client.player;
        final TextRenderer textRenderer = client.textRenderer;
        final MatrixStack matrixStack = new MatrixStack();

        if (player == null) return;


        RenderSystem.enableBlend();

        List<Runnable> statusEffectsRunnables = Lists.newArrayListWithExpectedSize(5);

        // region sprinting
        if(player.isSprinting()){
            stam--;
            wait = stamDelay;
        } else {
            wait--;
        }
        if (stam <= 0 && player.isSprinting()){
            player.setSprinting(false);
        }
        if(!player.isSprinting() && stam < maxStam && wait <= 0 && carry < maxCarry){
            stam++;
        }
        String buildStr = "";
        for (int i = 0; i < Math.ceil(stam/20F); i++) {
            buildStr += "|";
        }

        final String stamStr = buildStr;

        statusEffectsRunnables.add(() -> {
            textRenderer.draw(matrixStack, stamStr, 15, 15, 0x00FFFF);
        });
        // endregion

        buildStr = "";
        carry = 0;
        for (ListIterator<ItemStack> iter = player.inventory.main.listIterator(); iter.hasNext(); ){
            ItemStack elt = iter.next();
            
            Item item = elt.getItem();
            if (item != Items.AIR){
                carry += (elt.getCount() * 5);
            }
        }
        

        final String carryStr =  String.valueOf(carry) + " / " + String.valueOf(maxCarry);
        // final String carryStr = buildStr;
        statusEffectsRunnables.add(() -> {
            textRenderer.draw(matrixStack, carryStr, 15, 30, 0x00FFFF);
        });

        final String tempStr;
        final String tempStr2;
        double encumPer;
        
        EntityAttributeInstance attrib = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if(carry >= maxCarry){

            player.setSprinting(false);

            if (stam <= 0){
                encumPer = -1D;
            }
            else {
                encumPer = Math.max((1D-((double)(carry - maxCarry) / (double)maxCarry))-1D,-1D);
            }
            EntityAttributeModifier mod = new EntityAttributeModifier(attrUUID,"encumbered", encumPer, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            attrib.removeModifier(attrUUID);
            attrib.addPersistentModifier(mod);

            if (player.getVelocity().x * player.getVelocity().z != 0){
                stam--;
                wait = stamDelay;
            }

            tempStr = String.valueOf(encumPer);
            tempStr2 = String.valueOf(-attrib.getValue()*encumPer);
        } else {
            tempStr = String.valueOf("None");
            tempStr2 = String.valueOf("None");
            attrib.removeModifier(attrUUID);

        }
        // statusEffectsRunnables.add(() -> {
        //     textRenderer.draw(matrixStack, String.valueOf(attrib.getValue()), 15, 45, 0x00FFFF);
        // });
        // statusEffectsRunnables.add(() -> {
        //     textRenderer.draw(matrixStack, String.valueOf(tempStr), 15, 60, 0x00FFFF);
        // });
        // statusEffectsRunnables.add(() -> {
        //     textRenderer.draw(matrixStack, String.valueOf(player.getVelocity().x * player.getVelocity().z), 15, 75, 0x00FFFF);
        // });
        statusEffectsRunnables.forEach(Runnable::run);
        
    }

}

