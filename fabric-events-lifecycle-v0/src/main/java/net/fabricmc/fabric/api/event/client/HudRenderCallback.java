package net.fabricmc.fabric.api.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface HudRenderCallback {
	/** Called after rendering the status effects overlay when rendering the hud. */
	public static final Event<HudRenderCallback> EVENT = EventFactory.createArrayBacked(HudRenderCallback.class,
		listeners -> delta -> {
			for (HudRenderCallback event : listeners) {
				event.render(delta);
			}
		}
	);

	/**
	 * Called when the hud is rendered, which is displayed in game, in a world.
	 *
	 * @param delta the number of ticks passed since the last tick
	 */
	void render(float delta);
}
