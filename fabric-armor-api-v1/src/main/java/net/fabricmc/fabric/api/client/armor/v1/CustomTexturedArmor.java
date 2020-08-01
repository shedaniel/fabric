/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.fabricmc.fabric.api.client.armor.v1;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * Implement this interface on an {@link net.minecraft.item.ArmorItem} which you would like to have a custom textured armor model.
 */
public interface CustomTexturedArmor {
	/**
	 * Gets the armor texture identifier in string, to be converted to {@link Identifier}.
	 *
	 * @param entity      The entity equipping the armor
	 * @param stack       The item stack of the armor
	 * @param slot        The slot which the armor is in
	 * @param secondLayer Whether the texture should use the second layer, only true when the {@code slot} is {@link EquipmentSlot#LEGS}
	 * @param suffix      The nullable suffix of the texture, may be "overlay" in vanilla when the item is {@link net.minecraft.item.DyeableArmorItem}
	 * @return the custom armor texture identifier, return null to use the vanilla ones. Defaulted to the item's registry id.
	 */
	/* @Nullable */
	@Environment(EnvType.CLIENT)
	default String getCustomArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, /* @Nullable */  String suffix) {
		Identifier id = Registry.ITEM.getId(stack.getItem());
		return id.getNamespace() + "textures/models/armor/" + id.getPath() + "_layer_" + (secondLayer ? 2 : 1) + (suffix == null ? "" : "_" + suffix) + ".png";
	}
}
