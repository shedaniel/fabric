package net.fabricmc.fabric.mixin.eventslifecycle;

import net.fabricmc.fabric.api.event.client.HudRenderCallback;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud {

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderStatusEffectOverlay()V", shift = At.Shift.AFTER))
	public void render(float delta, CallbackInfo callbackInfo) {
		HudRenderCallback.EVENT.invoker().render(delta);
	}

}
