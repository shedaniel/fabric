package net.fabricmc.fabric.api.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface InGameHudRenderCallback {
	public static final Event<InGameHudRenderCallback> EVENT = EventFactory.createArrayBacked(InGameHudRenderCallback.class,
		(listeners) -> {
			return (screenWidth, screenHeight, delta) -> {
				for (InGameHudRenderCallback event : listeners) {
					event.render(screenWidth, screenHeight, delta);
				}
			};
		}
	);

	void render(int screenWidth, int screenHeight, float delta);
}
