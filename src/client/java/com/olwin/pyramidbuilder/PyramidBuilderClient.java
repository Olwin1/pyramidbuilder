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
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PyramidBuilderClient implements ClientModInitializer {
	private static KeyBinding keyBinding;
	Identifier polishedSandstoneStairsId = new Identifier("minecraft", "polished_sandstone_stairs");
	MinecraftClient client = MinecraftClient.getInstance();

	private void startActualBuilding(Block block, BlockPos blockPos, Block nextBlock, BlockPos nextBlockPos,
			Direction direction) {
		ItemStack targetBlock = new ItemStack(Items.SMOOTH_SANDSTONE_STAIRS);
		PlayerInventory playerInventory = client.player.getInventory();
		System.out.println("starting actual building");
		if (playerInventory.contains(targetBlock)) {
			// The block is in the player's inventory, proceed to placement
			System.out.println("Player has blok");
			if (Block.getBlockFromItem(nextBlock.asItem()).equals(Block.getBlockFromItem(Blocks.AIR.asItem()))) {
				// Place the block at the player's position
				// client.world.setBlockState(nextBlockPos,
				// client.world.getBlockState(blockPos));
				BlockHitResult blockHitResult = new BlockHitResult(
						// client.player.getCameraPosVec(1.0f),
						new Vec3d(nextBlockPos.getX() + 0.5, nextBlockPos.getY() + 0.5, nextBlockPos.getZ() + 0.5),
						client.player.getHorizontalFacing(),
						nextBlockPos,
						false);
				BlockPos nextBlockPosSecond = nextBlockPos.up().offset(direction.rotateClockwise(Direction.Axis.Y));
				BlockPos nextBlockPosThird = nextBlockPos.offset(direction.rotateCounterclockwise(Direction.Axis.Y))
						.down();

				BlockHitResult blockHitResultSecond = new BlockHitResult(
						// client.player.getCameraPosVec(1.0f),
						new Vec3d(nextBlockPosSecond.getX() + 0.5, nextBlockPosSecond.getY() + 0.5,
								nextBlockPosSecond.getZ() + 0.5),
						client.player.getHorizontalFacing(),
						nextBlockPosSecond,
						false);
				BlockHitResult blockHitResultThird = new BlockHitResult(
						// client.player.getCameraPosVec(1.0f),
						new Vec3d(nextBlockPosThird.getX() + 0.5, nextBlockPosThird.getY() + 0.5,
								nextBlockPosThird.getZ() + 0.5),
						client.player.getHorizontalFacing(),
						nextBlockPosThird,
						false);

				// Use the player's interaction manager to simulate a block placement
				client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHitResult);
				client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHitResultSecond);
				client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHitResultThird);
				client.player.move(MovementType.SELF, new Vec3d(nextBlockPos.getX() - blockPos.getX(),
						nextBlockPos.getY() - blockPos.getY(), nextBlockPos.getZ() - blockPos.getZ()));
			}
		} else {
			System.out.println("Player Does nOt have blok");
			// The block is not in the player's inventory, handle this case accordingly
			return;
		}
	}

	private void buildPyramid() {
		HitResult hit = client.crosshairTarget;

		switch (hit.getType()) {
			case MISS:
				// nothing near enough
				break;
			case BLOCK:
				BlockHitResult blockHit = (BlockHitResult) hit;
				BlockPos blockPos = blockHit.getBlockPos();
				Direction direction = blockHit.getSide();
				// Use the offset method to get the position of the block on the side
				BlockPos adjacentBlockPos = blockPos.offset(direction);

				Block nextBlock = client.world.getBlockState(adjacentBlockPos).getBlock();
				BlockState blockState = client.world.getBlockState(blockPos);
				client.player.sendMessage(Text.literal("Block Found"), false);
				Block block = blockState.getBlock();
				if (Block.getBlockFromItem(block.asItem())
						.equals(Block.getBlockFromItem(Blocks.SMOOTH_SANDSTONE_STAIRS.asItem()))) {
					// The block is polished sandstone stairs
					System.out.println("This block is polished sandstone stairs!");
					startActualBuilding(block, blockPos, nextBlock, adjacentBlockPos, direction);

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