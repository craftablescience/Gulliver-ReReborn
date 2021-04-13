package com.camellias.gulliverreborn.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

import static com.camellias.gulliverreborn.GulliverReborn.MODID;

public class CrushingDamageSource {
    public static DamageSource causeCrushingDamage(EntityLivingBase entity) {
        return new EntityDamageSource(MODID + ".crushing", entity);
    }
}
