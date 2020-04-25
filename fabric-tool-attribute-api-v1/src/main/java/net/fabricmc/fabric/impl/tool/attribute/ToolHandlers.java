package net.fabricmc.fabric.impl.tool.attribute;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.ShearsItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.Tag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.fabricmc.fabric.mixin.tool.attribute.MiningToolItemAccessor;

/**
 * Entrypoint to register the default tool handlers.
 */
public class ToolHandlers implements ModInitializer {
	@Override
	public void onInitialize() {
		ToolManagerImpl.general().register(new ModdedToolsModdedBlocksToolHandler());
		ToolManagerImpl.general().register(new VanillaToolsModdedBlocksToolHandler());
		ToolManagerImpl.general().register(new VanillaToolsVanillaBlocksToolHandler());
		ToolManagerImpl.tag(FabricToolTags.PICKAXES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						Items.WOODEN_PICKAXE,
						Items.STONE_PICKAXE,
						Items.IRON_PICKAXE,
						Items.DIAMOND_PICKAXE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.AXES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						Items.WOODEN_AXE,
						Items.STONE_AXE,
						Items.IRON_AXE,
						Items.DIAMOND_AXE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SHOVELS).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						Items.WOODEN_SHOVEL,
						Items.STONE_SHOVEL,
						Items.IRON_SHOVEL,
						Items.DIAMOND_SHOVEL
				)
		));
		ToolManagerImpl.tag(FabricToolTags.HOES).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						Items.WOODEN_HOE,
						Items.STONE_HOE,
						Items.IRON_HOE,
						Items.DIAMOND_HOE
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SWORDS).register(new ModdedMiningToolsVanillaBlocksToolHandler(
				ImmutableList.of(
						Items.WOODEN_SWORD,
						Items.STONE_SWORD,
						Items.IRON_SWORD,
						Items.DIAMOND_SWORD
				)
		));
		ToolManagerImpl.tag(FabricToolTags.SHEARS).register(new ShearsVanillaBlocksToolHandler());
	}

	/**
	 * This handler handles items that are an subclass of {@link DynamicAttributeTool} by comparing their mining level
	 * using {@link DynamicAttributeTool#getMiningLevel(Tag, BlockState, ItemStack, LivingEntity)} and the block mining level.
	 *
	 * <p>Only applicable to modded blocks that are registered, as only they have the registered required mining level.</p>
	 */
	private static class ModdedToolsModdedBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

				if (entry != null) {
					int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
					int requiredMiningLevel = entry.getMiningLevel(tag);

					return requiredMiningLevel >= 0 && miningLevel >= 0 && miningLevel >= requiredMiningLevel ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				float multiplier = ((DynamicAttributeTool) stack.getItem()).getMiningSpeedMultiplier(tag, state, stack, user);
				if (multiplier != 1f) return TypedActionResult.success(multiplier);
			}

			return TypedActionResult.pass(1f);
		}
	}

	/**
	 * This handler handles items that are not a subclass of {@link DynamicAttributeTool} by
	 * comparing their mining level using {@link ToolMaterial#getMiningLevel()} and the block mining level.
	 *
	 * <p>Only applicable to modded blocks that are registered, as only they have the registered required mining level.</p>
	 */
	private static class VanillaToolsModdedBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

				if (entry != null) {
					int miningLevel = stack.getItem() instanceof ToolItem ? ((ToolItem) stack.getItem()).getMaterial().getMiningLevel() : -1;
					int requiredMiningLevel = entry.getMiningLevel(tag);
					return requiredMiningLevel >= 0 && miningLevel >= 0 && miningLevel >= requiredMiningLevel ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				float multiplier = stack.getItem() instanceof MiningToolItem ? ((MiningToolItemAccessor) stack.getItem()).getMiningSpeed() : stack.getItem().getMiningSpeed(stack, state);
				if (multiplier != 1f) return TypedActionResult.success(multiplier);
			}

			return TypedActionResult.pass(1f);
		}
	}

	/**
	 * This handler handles items that are not a subclass of {@link DynamicAttributeTool} by
	 * using the vanilla {@link Item#isEffectiveOn(BlockState)}.
	 *
	 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
	 */
	private static class VanillaToolsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				ToolManagerImpl.Entry entry = ToolManagerImpl.entryNullable(state.getBlock());

				if (entry == null) {
					return stack.getItem().isEffectiveOn(state) || stack.getItem().getMiningSpeed(stack, state) != 1f ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				float miningSpeed = stack.getItem().getMiningSpeed(stack, state);

				if (miningSpeed != 1f) {
					return TypedActionResult.success(miningSpeed);
				}
			}

			return TypedActionResult.pass(1f);
		}
	}

	/**
	 * This handler handles items that are a subclass of {@link DynamicAttributeTool} by using the
	 * vanilla {@link Item#isEffectiveOn(BlockState)} with a custom fake tool material to use the mining level
	 * from {@link DynamicAttributeTool#getMiningLevel(Tag, BlockState, ItemStack, LivingEntity)}.
	 *
	 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
	 */
	private static class ModdedMiningToolsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		private final FakeAdaptableToolMaterial fakeMaterial = new FakeAdaptableToolMaterial();
		private final ImmutableList<Item> vanillaItems;

		private ModdedMiningToolsVanillaBlocksToolHandler(ImmutableList<Item> vanillaItems) {
			this.vanillaItems = vanillaItems;
		}

		private ToolItem getVanillaItem(int miningLevel) {
			if (miningLevel < 0) return (ToolItem) vanillaItems.get(0);
			if (miningLevel >= vanillaItems.size()) return (ToolItem) vanillaItems.get(vanillaItems.size() - 1);
			return (ToolItem) vanillaItems.get(miningLevel);
		}

		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				if (ToolManagerImpl.entryNullable(state.getBlock()) != null) {
					// Block is a modded block, and we should ignore it
					return ActionResult.PASS;
				}

				// Gets the mining level from our modded tool
				int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
				if (miningLevel < 0) return ActionResult.PASS;

				ToolItem vanillaItem = getVanillaItem(miningLevel);
				boolean effective = vanillaItem.isEffectiveOn(state) || vanillaItem.getMiningSpeed(new ItemStack(vanillaItem), state) != 1f;
				return effective ? ActionResult.SUCCESS : ActionResult.PASS;
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (stack.getItem() instanceof DynamicAttributeTool) {
				// Gets the mining level from our modded tool
				int miningLevel = ((DynamicAttributeTool) stack.getItem()).getMiningLevel(tag, state, stack, user);
				if (miningLevel < 0) return null;

				float moddedToolSpeed = ((DynamicAttributeTool) stack.getItem()).getMiningSpeedMultiplier(tag, state, stack, user);
				ToolItem vanillaItem = getVanillaItem(miningLevel);
				float miningSpeed;

				// Fake the vanilla items' mining speed if needed
				if (((MiningToolItemAccessor) vanillaItem).getMiningSpeed() != moddedToolSpeed) {
					float tempMiningSpeed = ((MiningToolItemAccessor) vanillaItem).getMiningSpeed();
					((MiningToolItemAccessor) vanillaItem).setMiningSpeed(moddedToolSpeed);
					miningSpeed = vanillaItem.getMiningSpeed(stack, state);
					((MiningToolItemAccessor) vanillaItem).setMiningSpeed(tempMiningSpeed);
				} else {
					miningSpeed = vanillaItem.getMiningSpeed(stack, state);
				}

				return miningSpeed != 1f ? TypedActionResult.success(miningSpeed) : TypedActionResult.pass(1f);
			}

			return TypedActionResult.pass(1f);
		}
	}

	/**
	 * This handler handles items that are registered in the {@link FabricToolTags#SHEARS} by using the
	 * vanilla {@link Item#isEffectiveOn(BlockState)} using the vanilla shears or the item itself if the item
	 * is a subclass of {@link ShearsItem}.
	 *
	 * <p>Only applicable to items that are not a subclass of {@link DynamicAttributeTool}</p>
	 * <p>Only applicable to blocks that are vanilla or share the material that is handled by their vanilla tool.</p>
	 */
	private static class ShearsVanillaBlocksToolHandler implements ToolManagerImpl.ToolHandler {
		private final Item vanillaItem = Items.SHEARS;

		@Override
		public ActionResult isEffectiveOn(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			if (ToolManagerImpl.entryNullable(state.getBlock()) != null) {
				// Block is a modded block, and we should ignore it
				return ActionResult.PASS;
			}

			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				if (!(stack.getItem() instanceof ShearsItem)) {
					return vanillaItem.isEffectiveOn(state) || vanillaItem.getMiningSpeed(new ItemStack(vanillaItem), state) != 1f ? ActionResult.SUCCESS : ActionResult.PASS;
				} else {
					return stack.getItem().isEffectiveOn(state) || stack.getItem().getMiningSpeed(stack, state) != 1f ? ActionResult.SUCCESS : ActionResult.PASS;
				}
			}

			return ActionResult.PASS;
		}

		@Override
		public TypedActionResult<Float> getMiningSpeedMultiplier(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
			float speed = 1f;

			if (!(stack.getItem() instanceof DynamicAttributeTool)) {
				if (!(stack.getItem() instanceof ShearsItem)) {
					speed = vanillaItem.getMiningSpeed(new ItemStack(vanillaItem), state);
				} else {
					speed = stack.getItem().getMiningSpeed(stack, state);
				}
			}

			return speed != 1f ? TypedActionResult.success(speed) : TypedActionResult.pass(1f);
		}
	}

	/**
	 * Fake tool material in which their mining level can be modified.
	 */
	private static class FakeAdaptableToolMaterial implements ToolMaterial {
		private int miningLevel = 0;

		@Override
		public int getDurability() {
			return 0;
		}

		@Override
		public float getMiningSpeed() {
			return 0;
		}

		@Override
		public float getAttackDamage() {
			return 0;
		}

		@Override
		public int getMiningLevel() {
			return miningLevel;
		}

		@Override
		public int getEnchantability() {
			return 0;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.EMPTY;
		}
	}
}
