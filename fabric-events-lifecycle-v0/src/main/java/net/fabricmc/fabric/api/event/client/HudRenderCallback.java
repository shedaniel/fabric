package net.fabricmc.fabric.api.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface HudRenderCallback {
	public static final Event<HudRenderCallback> EVENT = EventFactory.createArrayBacked(HudRenderCallback.class,
		listeners -> (screenWidth, screenHeight, delta) -> {
			for (HudRenderCallback event : listeners) {
				event.render(screenWidth, screenHeight, delta);
			}
		}
	);

	void render(int screenWidth, int screenHeight, float delta);
}
