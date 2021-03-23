package sommea.weyland.systems;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;



@Environment(EnvType.CLIENT)
public class HUD {
    private MinecraftClient client;

    public HUD() {
        client = MinecraftClient.getInstance();
        HudRenderCallback.EVENT.register((__, ___) -> {
            this.render();
        });
    }

    public int maxStam = 500;
    private int stamDelay = 200;
    public float stamRecharge = 1;
    private int wait;
    private int stam = maxStam;

    private int maxCarry = 300;
    private int carry = maxCarry;

    public int maxHealth = 40;
    // private ArrayList<Pair<Item,Integer>> foods = new ArrayList<Pair<Item,Integer>>();
    

    private UUID stamUUID = UUID.randomUUID();
    private UUID healthUUID = UUID.randomUUID();

    private void render() {

        final PlayerEntity player = client.player;
        final TextRenderer textRenderer = client.textRenderer;
        final MatrixStack matrixStack = new MatrixStack();

        if (player == null) return;


        RenderSystem.enableBlend();

        List<Runnable> uiRunnables = Lists.newArrayListWithExpectedSize(10);

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
            stam += stamRecharge;
        }
        String buildStr = "";
        for (int i = 0; i < Math.ceil(stam/20F); i++) {
            buildStr += "|";
        }

        final String stamStr = buildStr;

        uiRunnables.add(() -> {
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
        uiRunnables.add(() -> {
            textRenderer.draw(matrixStack, carryStr, 15, 30, 0x00FFFF);
        });

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
            EntityAttributeModifier mod = new EntityAttributeModifier(stamUUID,"encumbered", encumPer, EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            attrib.removeModifier(stamUUID);
            attrib.addPersistentModifier(mod);

            if (player.getVelocity().x * player.getVelocity().z != 0){
                stam--;
                wait = stamDelay;
            }
        } else {
            attrib.removeModifier(stamUUID);
        }
        
        // statusEffectsRunnables.add(() -> {
        //     textRenderer.draw(matrixStack, String.valueOf(tempStr), 15, 60, 0x00FFFF);
        // });
        // statusEffectsRunnables.add(() -> {
        //     textRenderer.draw(matrixStack, String.valueOf(player.getVelocity().x * player.getVelocity().z), 15, 75, 0x00FFFF);
        // });
        
        
        EntityAttributeInstance attribHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        // attribHealth.setBaseValue(20);
        attribHealth.removeModifier(healthUUID);
        // attribHealth.addPersistentModifier(modHealth);
        // player.setAbsorptionAmount(40);
        uiRunnables.add(() -> {
                textRenderer.draw(matrixStack, String.valueOf(attribHealth.getValue()), 15, 45, 0x00FFFF);
            });

        // for (int i = 0; i < foods.size(); i++) {
        //     final int x = i*15 + 5;
        //     final int index = i;

        //     uiRunnables.add(() -> {
        //         client.getItemRenderer().renderInGuiWithOverrides(new ItemStack(foods.get(index).getL()), x, 60);
        //     });
        // }
        // if ( player.isUsingItem() ){
        //     Item curr_item = player.getActiveItem().getItem();
        //     if (curr_item.isFood()){
        //        if(foods.size() < 2){
        //             if(foods.size() == 2) { 
        //                 player.getHungerManager().setFoodLevel(20); 
        //             }
        //             foods.add(new Pair<Item,Integer>(curr_item,1000));
        //             player.getActiveItem().decrement(1);
        //             player.stopUsingItem();


        //         }
        //     }
        // }
        // if(foods.size() < 2){
        //     player.getHungerManager().setFoodLevel(19); 
        // }
        // for (Iterator<Pair<Item,Integer>> iterator = foods.iterator(); iterator.hasNext(); ){
        //     Pair<Item,Integer> food = iterator.next();
        //     food.setR(food.getR()-1);
        //     if (food.getR() == 0) {
        //         iterator.remove();
        //     }
        // }
        
        // int y = 80;
        // for (int i = 0; i < foods.size(); i++) {
        //     final int val = i;
        //     final int y2 = y;
        //     uiRunnables.add(() -> {
        //         textRenderer.draw(matrixStack, String.valueOf(val) + ": " + String.valueOf(foods.get(val).getR()), 15, y2, 0x00FFFF);
        //     }); 
        //     y += 15;
        // }
       
     
        uiRunnables.forEach(Runnable::run);
    }

}

