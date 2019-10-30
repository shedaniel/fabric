package net.fabricmc.fabric.api.client.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface HudRenderCallback {
	/**
	 * Called after rendering the status effects overlay when rendering the hud.
	 */
	Event<HudRenderCallback> EVENT = EventFactory.createArrayBacked(HudRenderCallback.class, (listeners) ->
			(delta) -> {
		for (HudRenderCallback event : listeners) {
			event.render(delta);
		}
	});

	/**
	 * Called when the hud is rendered, which is displayed in game, in a world.
	 * See {@link net.minecraft.util.math.MathHelper#lerp(float, float, float)} for interpolating.
	 */
	void render(float delta);
}
