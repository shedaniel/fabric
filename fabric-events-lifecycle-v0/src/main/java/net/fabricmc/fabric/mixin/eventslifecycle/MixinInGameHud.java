package net.fabricmc.fabric.mixin.eventslifecycle;

import net.fabricmc.fabric.api.event.client.InGameHudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {
	@Shadow
	@Final
	private MinecraftClient client;

	@Inject(method = "render", at = @At("RETURN"))
	public void render(float delta, CallbackInfo callbackInfo) {
		InGameHudRenderCallback.EVENT.invoker().render(client.window.getScaledWidth(), client.window.getScaledHeight(), delta);
	}
}
