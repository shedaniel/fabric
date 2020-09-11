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

package net.fabricmc.fabric.impl.conditionalrecipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.conditionalrecipe.v1.RecipeConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.fabricmc.loader.util.version.VersionPredicateParser;

public class DefaultRecipeConditions implements ModInitializer {
	@Override
	public void onInitialize() {
		RecipeConditions.register(new Identifier("fabric:impossible"), (recipeId, element) -> false);
		RecipeConditions.register(new Identifier("fabric:always"), (recipeId, element) -> true);
		RecipeConditions.register(new Identifier("fabric:boolean"), (recipeId, element) -> element.getAsBoolean());
		RecipeConditions.register(new Identifier("fabric:or"), (recipeId, element) -> {
			JsonArray conditions = element.getAsJsonArray();

			if (conditions.size() == 0) {
				throw new IllegalArgumentException("Json array conditions for \"fabric:or\" is empty!");
			}

			for (JsonElement condition : conditions) {
				if (RecipeConditions.evaluate(recipeId, condition.getAsJsonObject())) {
					return true;
				}
			}

			return false;
		});
		RecipeConditions.register(new Identifier("fabric:and"), (recipeId, element) -> {
			JsonArray conditions = element.getAsJsonArray();

			if (conditions.size() == 0) {
				throw new IllegalArgumentException("Json array conditions for \"fabric:and\" is empty!");
			}

			for (JsonElement condition : conditions) {
				if (!RecipeConditions.evaluate(recipeId, condition.getAsJsonObject())) {
					return false;
				}
			}

			return true;
		});
		RecipeConditions.register(new Identifier("fabric:mod"), (recipeId, element) -> {
			JsonObject object = element.getAsJsonObject();

			for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
				String modId = entry.getKey();
				Optional<Version> version = FabricLoader.getInstance().getModContainer(modId).map(ModContainer::getMetadata).map(ModMetadata::getVersion);

				if (!version.isPresent()) {
					return false;
				} else {
					JsonElement versionMatcher = entry.getValue();
					List<String> versionsToMatch;

					if (versionMatcher.isJsonPrimitive()) {
						versionsToMatch = Collections.singletonList(versionMatcher.getAsString());
					} else if (versionMatcher.isJsonArray()) {
						versionsToMatch = new ArrayList<>();

						for (JsonElement jsonElement : versionMatcher.getAsJsonArray()) {
							versionsToMatch.add(jsonElement.getAsString());
						}
					} else {
						throw new RuntimeException("Dependency version range must be a string or string array!");
					}

					boolean matched = false;

					for (String match : versionsToMatch) {
						try {
							if (VersionPredicateParser.matches(version.get(), match)) {
								matched = true;
								break;
							}
						} catch (VersionParsingException e) {
							e.printStackTrace();
							return false;
						}
					}

					if (!matched) {
						return false;
					}
				}
			}

			return true;
		});
	}
}
