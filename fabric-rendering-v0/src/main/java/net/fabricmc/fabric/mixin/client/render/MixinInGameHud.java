package net.fabricmc.fabric.mixin.client.render;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.hud.InGameHud;

import net.fabricmc.fabric.api.client.event.HudRenderCallback;

@Mixin(InGameHud.class)
public class MixinInGameHud {
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;disableLighting()V", ordinal = 0, shift = At.Shift.BY, by = -3))
	public void render(float delta, CallbackInfo callbackInfo) {
		HudRenderCallback.EVENT.invoker().render(delta);
	}
}
