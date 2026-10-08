package com.endertweeks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.endertweeks.module.Xray;

import net.minecraft.world.level.block.state.BlockBehaviour;

/** Makes every block count as see-through for the renderer's occlusion culling while Xray is on. */
@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class XrayStateMixin {
	@Inject(method = "isSolidRender", at = @At("HEAD"), cancellable = true)
	private void endertweeks$notSolid(CallbackInfoReturnable<Boolean> cir) {
		if (Xray.active) {
			cir.setReturnValue(false);
		}
	}
}
