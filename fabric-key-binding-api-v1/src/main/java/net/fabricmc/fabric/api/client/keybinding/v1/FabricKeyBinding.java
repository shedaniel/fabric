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

package net.fabricmc.fabric.api.client.keybinding.v1;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import com.google.common.base.Preconditions;

import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.impl.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.impl.client.keybinding.StickyFabricKeyBinding;

/**
 * Expanded version of {@link KeyBinding} for use by Fabric mods.
 *
 * <p>*ALL* built FabricKeyBindings are automatically registered!</p>
 *
 * <pre><code>
 * FabricKeyBinding left = FabricKeyBinding.builder()
 * 			.id(new Identifier("example", "left"))
 * 			.key(InputUtil.Type.KEYSYM, Keys.Left)
 * 			.build();
 * FabricKeyBinding right = FabricKeyBinding.builder()
 * 			.id(new Identifier("example", "right"))
 * 			.key(InputUtil.Type.KEYSYM, Keys.Right)
 * 			.build();
 * </code></pre>
 */
public class FabricKeyBinding extends ModdedKeyBinding {
	private final Identifier id;

	protected FabricKeyBinding(Identifier id, String translationKey, InputUtil.Type type, int code, String category) {
		super(translationKey, type, code, category);
		this.id = id;
	}

	/**
	 * Original identifier used to register this key.
	 *
	 * <p>May be different from the {@link #getId()}.</p>
	 */
	public Identifier getIdentifier() {
		return id;
	}

	/**
	 * Creates a new builder for constructing custom key bindings.
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private static final int UNKNOWN_KEY = InputUtil.UNKNOWN_KEYCODE.getKeyCode();

		private InputUtil.Type type = InputUtil.Type.KEYSYM;
		private Identifier id = null;
		private String translationKey;
		private BooleanSupplier toggleFlagSupplier = null;
		private int key = UNKNOWN_KEY;
		private String category = KeyCategories.MISC;

		private Builder() {
		}

		/**
		 * Sets the key binding id and translation key for this builder.
		 * <br>
		 * Key bindings will be assigned a translation key of the format "key.{namespace}.{path}"
		 *
		 * @param id Unique identifier for the bound key.
		 */
		public Builder id(Identifier id) {
			this.id = Objects.requireNonNull(id, "Keybinding's id can not be null!");
			this.translationKey = String.format("key.%s.%s", id.getNamespace(), id.getPath());
			return this;
		}

		/**
		 * Sets the translation key for the key's category. {@link KeyCategories} for
		 * all possible values, vanilla values.
		 *
		 * @param category The category under which key bindings created by this builder will be grouped.
		 */
		public Builder category(String category) {
			this.category = Objects.requireNonNull(category, "Keybinding's category can not be null!");
			return this;
		}

		/**
		 * Sets the default key to be used for the key binding created using this builder.
		 *
		 * @param type The key's type. Maybe be one of [{@link InputUtil.Type#KEYSYM} (keyboard), {@link InputUtil.Type#SCANCODE}, {@link InputUtil.Type#MOUSE}]
		 * @param key  The default key code. Must be a valid key. May not be -1.
		 */
		public Builder key(InputUtil.Type type, int key) {
			Preconditions.checkState(key != UNKNOWN_KEY, "UNKNOWN is not a valid key code.");
			this.type = Objects.requireNonNull(type, "Keybinding's type can not be null!");
			this.key = key;
			return this;
		}

		/**
		 * Sets the default key to be used for the key binding to unbound.
		 */
		public Builder unbound() {
			this.type = InputUtil.Type.KEYSYM;
			this.key = UNKNOWN_KEY;
			return this;
		}

		/**
		 * Sets this builder to create sticky keybindings that will toggle their state when pressed.
		 */
		public Builder sticky() {
			return sticky(() -> true);
		}

		/**
		 * Sets a sticking action to be used by the constructed key binding which can be used to switch between
		 * a sticky (toggle) and a regular key binding.
		 *
		 * @param toggleFlagSupplier A getter function to determine whether to toggle or not. True for toggling behaviour, false otherwise.
		 */
		public Builder sticky(BooleanSupplier toggleFlagSupplier) {
			this.toggleFlagSupplier = toggleFlagSupplier;
			return this;
		}

		/**
		 * Returns a key binding with a matching configuration to that of this builder.
		 *
		 * <p>Implementation Note:</p>
		 *
		 * <p>At current this returns a <i>new</i> key binding that modders should
		 * hold onto for their own use, though this may change in the future.</p>
		 */
		public FabricKeyBinding build() {
			Objects.requireNonNull(id, "Keybindings should be created with an identifier.");
			FabricKeyBinding binding;

			if (toggleFlagSupplier == null) {
				binding = new FabricKeyBinding(id, translationKey, type, key, category);
			} else {
				binding = new StickyFabricKeyBinding(id, translationKey, type, key, category, toggleFlagSupplier);
			}

			KeyBindingRegistry.INSTANCE.registerKeyBinding(binding);

			return binding;
		}
	}
}
