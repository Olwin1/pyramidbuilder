package com.olwin.pyramidbuilder;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;


public class PyramidBuilderClient implements ClientModInitializer {
	private static KeyBinding keyBinding;

	private void buildPyramid() {
		Identifier polishedSandstoneStairsId = new Identifier("minecraft", "polished_sandstone_stairs");
		MinecraftClient client = MinecraftClient.getInstance();
HitResult hit = client.crosshairTarget;
 
switch(hit.getType()) {
    case MISS:
        //nothing near enough
        break; 
    case BLOCK:
        BlockHitResult blockHit = (BlockHitResult) hit;
        BlockPos blockPos = blockHit.getBlockPos();
        BlockState blockState = client.world.getBlockState(blockPos);
		client.player.sendMessage(Text.literal("Block Found"), false);
	Block block = blockState.getBlock();
	if (Block.getBlockFromItem(block.asItem()).equals(Block.getBlockFromItem(Blocks.SMOOTH_SANDSTONE_STAIRS.asItem()))) {
    // The block is polished sandstone stairs
    System.out.println("This block is polished sandstone stairs!");
} else {
    // The block is not polished sandstone stairs
    System.out.println("This block is not polished sandstone stairs.");
}
        break; 
    case ENTITY:
        EntityHitResult entityHit = (EntityHitResult) hit;
        Entity entity = entityHit.getEntity();
		client.player.sendMessage(Text.literal("Entity?"), false);

        break; 
}
	}

	@Override
	public void onInitializeClient() {
		keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.olwin.pyramid", // The translation key of the keybinding's name
				InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
				GLFW.GLFW_KEY_RIGHT_ALT, // The keycode of the key
				"default.olwin.pyramid" // The translation key of the keybinding's category.
		));
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyBinding.wasPressed()) {
				client.player.sendMessage(Text.literal("Key 1 was pressed!"), false);
				buildPyramid();
			}
		});
		// This entrypoint is suitable for setting up client-specific logic, such as
		// rendering.
	}
}