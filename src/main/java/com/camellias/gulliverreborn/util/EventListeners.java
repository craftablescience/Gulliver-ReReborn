package com.camellias.gulliverreborn.util;

import com.artemis.artemislib.compatibilities.sizeCap.ISizeCap;
import com.artemis.artemislib.compatibilities.sizeCap.SizeCapPro;
import com.camellias.gulliverreborn.config.GulliverRebornConfig;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class EventListeners {
    @SubscribeEvent
    public static void onPlayerFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            if (GulliverRebornConfig.SCALED_FALL_DAMAGE)
                event.setDistance(event.getDistance() / (player.height * 0.6F));
            if (player.height < 0.45F)
                event.setDistance(0);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        World world = event.getEntityLiving().world;

        AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
        AxisAlignedBB footStompBox = new AxisAlignedBB(
                entityBoundingBox.minX,
                entityBoundingBox.minY,
                entityBoundingBox.minZ,
                entityBoundingBox.maxX,
                ((entityBoundingBox.maxY - entityBoundingBox.minY) / 4f) + entityBoundingBox.minY,
                entityBoundingBox.maxZ);

        for (EntityLivingBase entities : world.getEntitiesWithinAABB(EntityLivingBase.class, footStompBox)) {
            if (!entity.isSneaking() && GulliverRebornConfig.GIANTS_CRUSH_ENTITIES) {
                if (entity.height / entities.height >= 4 && entities.getRidingEntity() != entity) {
                    entities.attackEntityFrom(CrushingDamageSource.causeCrushingDamage(entity), entity.height - entities.height);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onTargetEntity(LivingSetAttackTargetEvent event) {
        if (event.getTarget() instanceof EntityPlayer && event.getEntityLiving() instanceof EntityLiving && GulliverRebornConfig.SMALL_IS_INVISIBLE_TO_NONCATS_OR_NONSPIDERS) {
            EntityPlayer player = (EntityPlayer) event.getTarget();
            EntityLiving entity = (EntityLiving) event.getEntityLiving();

            if (!((entity instanceof EntitySpider || entity instanceof EntityOcelot)) && player.height <= 0.45F)
                entity.setAttackTarget(null);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        World world = event.player.world;

        player.stepHeight = player.height / 3F;
        if (player.height > 1.8f)
            player.jumpMovementFactor *= (player.height / 1.8F) * GulliverRebornConfig.JUMP_MODIFIER_STRENGTH;
        else
            player.jumpMovementFactor *= (player.height / 1.8f);

        if (player.height < 0.9F) {
            BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            float ratio = (player.height / 1.8F) / 2;

            if (block instanceof BlockRedFlower
                    || (state == Blocks.DOUBLE_PLANT.getDefaultState().withProperty(BlockDoublePlant.VARIANT, BlockDoublePlant.EnumPlantType.ROSE)
                    && GulliverRebornConfig.ROSES_HURT))
                player.attackEntityFrom(DamageSource.CACTUS, 1);

            if (!player.capabilities.isFlying
                    && GulliverRebornConfig.PLANTS_SLOW_SMALL_DOWN
                    && ((block instanceof BlockBush)
                    || (block instanceof BlockCarpet)
                    || (block instanceof BlockReed)
                    || (block instanceof BlockSnow)
                    || (block instanceof BlockWeb)
                    || (block instanceof BlockSoulSand))) {
                player.motionX *= ratio;
                if (block instanceof BlockWeb)
                    player.motionY *= ratio;
                player.motionZ *= ratio;
            }
        }

        if (player.height <= 0.45F) {
            EnumFacing facing = player.getHorizontalFacing();
            BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
            IBlockState state = world.getBlockState(pos.add(0, 0, 0).offset(facing));
            Block block = state.getBlock();

            if (ClimbingHandler.canClimb(player, facing)
                    && GulliverRebornConfig.CLIMB_SOME_BLOCKS
                    && ((block instanceof BlockDirt)
                    || (block instanceof BlockGrass)
                    || (block instanceof BlockMycelium)
                    || (block instanceof BlockLeaves)
                    || (block instanceof BlockSand)
                    || (block instanceof BlockSoulSand)
                    || (block instanceof BlockConcretePowder)
                    || (block instanceof BlockFarmland)
                    || (block instanceof BlockGrassPath)
                    || (block instanceof BlockGravel)
                    || (block instanceof BlockClay))
                    && player.collidedHorizontally) {
                if (!player.isSneaking())
                    player.motionY = 0.1D;
                else
                    player.motionY = 0.0D;
            }

            for (ItemStack stack : player.getHeldEquipment()) {
                if (stack.getItem() == Items.SLIME_BALL || stack.getItem() == Item.getItemFromBlock(Blocks.SLIME_BLOCK) && GulliverRebornConfig.CLIMB_WITH_SLIME) {
                    if (ClimbingHandler.canClimb(player, facing) && player.collidedHorizontally) {
                        if (!player.isSneaking())
                            player.motionY = 0.1D;
                        else
                            player.motionY = 0.0D;
                    }
                }

                if (stack.getItem() == Items.PAPER && GulliverRebornConfig.GLIDE_WITH_PAPER && !player.onGround) {
                    player.jumpMovementFactor = 0.02F * 1.75F;
                    player.fallDistance = 0;

                    if (player.motionY < 0D)
                        player.motionY *= 0.6D;

                    if (player.isSneaking())
                        player.jumpMovementFactor *= 3.50F;

                    for (double blockY = player.posY; !player.isSneaking() &&
                            ((world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.AIR) ||
                                    (world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.LAVA) ||
                                    (world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.FIRE) ||
                                    (world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.LIT_FURNACE) ||
                                    (world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.MAGMA)) &&
                            player.posY - blockY < 25;
                         blockY--) {
                        if ((world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.LAVA) ||
                                (world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.FIRE) ||
                                (world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.LIT_FURNACE) ||
                                (world.getBlockState(new BlockPos(player.posX, blockY, player.posZ)).getBlock() == Blocks.MAGMA) &&
                                        GulliverRebornConfig.HOT_BLOCKS_GIVE_LIFT)
                            player.motionY += MathHelper.clamp(0.07D, Double.MIN_VALUE, 0.1D);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof EntityLivingBase) {
            EntityLivingBase target = (EntityLivingBase) event.getTarget();
            EntityPlayer player = event.getEntityPlayer();

            if (target.height / 2 >= player.height && GulliverRebornConfig.RIDE_BIG_ENTITIES) {
                for (ItemStack stack : player.getHeldEquipment()) {
                    if (stack.getItem() == Items.STRING) {
                        player.startRiding(target);
                    }
                }
            }

            if(target.height * 2 <= player.height && GulliverRebornConfig.PICKUP_SMALL_ENTITIES)
                target.startRiding(player);

            if (player.getHeldItemMainhand().isEmpty() && player.isBeingRidden() && player.isSneaking()) {
                for (Entity entities : player.getPassengers()) {
                    if (entities instanceof EntityLivingBase)
                        entities.dismountRidingEntity();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJump(LivingEvent.LivingJumpEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer && GulliverRebornConfig.JUMP_MODIFIER) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            float jumpHeight;
            if (player.height > 2f)
                jumpHeight = (player.height / 1.8F) * GulliverRebornConfig.JUMP_MODIFIER_STRENGTH;
            else
                jumpHeight = (player.height / 1.8F);

            jumpHeight = MathHelper.clamp(jumpHeight, 0.65F, jumpHeight);
            player.motionY *= jumpHeight;

            if ((player.isSneaking() || player.isSprinting()) && player.height < 1.8F)
                player.motionY = 0.42F;
        }
    }

    @SubscribeEvent
    public static void onHarvest(PlayerEvent.BreakSpeed event) {
        EntityPlayer player = event.getEntityPlayer();
        if (GulliverRebornConfig.HARVEST_MODIFIER)
            event.setNewSpeed(event.getOriginalSpeed() * (player.height / 1.8F));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onFOVChange(FOVUpdateEvent event) {
        if (event.getEntity() != null) {
            EntityPlayer player = event.getEntity();
            GameSettings settings = Minecraft.getMinecraft().gameSettings;
            PotionEffect speed = player.getActivePotionEffect(MobEffects.SPEED);
            float fov = settings.fovSetting / settings.fovSetting; // wtf!?

            if (player.isSprinting())
                event.setNewfov(speed != null ? fov + ((0.1F * (speed.getAmplifier() + 1)) + 0.15F) : fov + 0.1F);
            else
                event.setNewfov(speed != null ? fov + (0.1F * (speed.getAmplifier() + 1)) : fov);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        float scale = player.height / 1.8F;
        if (player.height > 1.8F) {
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 1)
                GL11.glTranslatef(0, 0, -scale * 2);
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2)
                GL11.glTranslatef(0, 0, scale * 2);
        }
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onEntityRenderPre(RenderLivingEvent.Pre event) {
        if (GulliverRebornConfig.DO_ADJUSTED_RENDER) {
            final EntityLivingBase entity = event.getEntity();
            if (entity.hasCapability(SizeCapPro.sizeCapability, null)) {
                final ISizeCap cap = entity.getCapability(SizeCapPro.sizeCapability, null);
                if (cap != null && cap.getTrans()) {
                    float scale = entity.height / cap.getDefaultHeight();
                    if (scale < 0.4F) {
                        GlStateManager.pushMatrix();
                        GlStateManager.scale(scale * 2.5F, 1, scale * 2.5F);
                        GlStateManager.translate(
                                event.getX() / scale * 2.5F - event.getX(),
                                event.getY() / scale * 2.5F - event.getY(),
                                event.getZ() / scale * 2.5F - event.getZ());
                    }
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onLivingRenderPost(RenderLivingEvent.Post event) {
        if (GulliverRebornConfig.DO_ADJUSTED_RENDER && event.getEntity().hasCapability(SizeCapPro.sizeCapability, null)) {
            final ISizeCap cap = event.getEntity().getCapability(SizeCapPro.sizeCapability, null);
            if (cap != null && cap.getTrans() && (event.getEntity().height / cap.getDefaultHeight() < 0.4F))
                GlStateManager.popMatrix();
        }
    }
}
