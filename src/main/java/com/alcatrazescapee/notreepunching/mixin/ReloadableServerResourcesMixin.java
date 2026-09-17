package com.alcatrazescapee.notreepunching.mixin;

import com.alcatrazescapee.notreepunching.common.recipes.ModRecipes;
import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin
{
    @Inject(method = "updateRegistryTags()V", at = @At("RETURN"))
    private void afterLoadTagsOnServer(CallbackInfo ci)
    {
        ModRecipes.injectRecipes((ReloadableServerResources) (Object) this);
    }
}
