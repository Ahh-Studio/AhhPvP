package com.aiden.pvp.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Illusioner.class)
public abstract class IllusionerEntityMixin {
    @Inject(
            method = "performRangedAttack",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/ProjectileUtil;getMobArrow(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;FLnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/projectile/AbstractArrow;",
                    shift = At.Shift.BY, by = 2
            )
    )
    public void injectedShootAt(LivingEntity target, float pullProgress, CallbackInfo ci, @Local AbstractArrow abstractArrow) {
        if (abstractArrow instanceof Arrow arrowEntity) {
            arrowEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
        }
    }
}
