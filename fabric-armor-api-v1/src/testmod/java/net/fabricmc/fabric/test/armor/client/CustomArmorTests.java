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

package net.fabricmc.fabric.test.armor.client;

import java.util.Collections;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.armor.v1.CustomModeledArmor;
import net.fabricmc.fabric.api.client.armor.v1.CustomTexturedArmor;

@Environment(EnvType.CLIENT)
public class CustomArmorTests implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		Registry.register(Registry.ITEM, new Identifier("fabric-armor-api-v1-testmod:custom_modeled_armor"),
				new CustomModeledArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT)));
		Registry.register(Registry.ITEM, new Identifier("fabric-armor-api-v1-testmod:custom_textured_armor"),
				new CustomTexturedArmorItem(ArmorMaterials.DIAMOND, EquipmentSlot.CHEST, new Item.Settings().group(ItemGroup.COMBAT)));
	}

	private static class CustomModeledArmorItem extends ArmorItem implements CustomModeledArmor, CustomTexturedArmor {
		private CustomArmorModel model = new CustomArmorModel(1.0F);

		CustomModeledArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
			super(material, slot, settings);
		}

		@Override
		public BipedEntityModel<LivingEntity> getCustomArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, BipedEntityModel<LivingEntity> defaultModel) {
			return model;
		}

		@Override
		public String getCustomArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, String suffix) {
			return "fabric-armor-api-v1-testmod:thing/i_have_a_cube.png";
		}
	}

	private static class CustomArmorModel extends BipedEntityModel<LivingEntity> {
		private final ModelPart part;

		CustomArmorModel(float scale) {
			super(scale, 0, 1, 1);
			part = new ModelPart(this, 0, 0);
			part.addCuboid(-5F, 0F, 2F, 10, 10, 10);
			part.setPivot(0F, 0F, 0F);
			part.mirror = true;
		}

		@Override
		protected Iterable<ModelPart> getBodyParts() {
			return Collections.singleton(part);
		}

		@Override
		protected Iterable<ModelPart> getHeadParts() {
			return Collections::emptyIterator;
		}
	}

	private static class CustomTexturedArmorItem extends ArmorItem implements CustomTexturedArmor {
		CustomTexturedArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
			super(material, slot, settings);
		}

		@Override
		public String getCustomArmorTexture(LivingEntity entity, ItemStack stack, EquipmentSlot slot, boolean secondLayer, String suffix) {
			return "fabric-armor-api-v1-testmod:thing/amazing.png";
		}
	}
}
