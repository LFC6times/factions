package io.icker.factions.mixin;

import io.icker.factions.FactionsMod;
import io.icker.factions.config.Config;
import io.icker.factions.event.FactionEvents;
import io.icker.factions.event.PlayerInteractEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends LivingEntity {

    @Shadow @Final private static Logger LOGGER;

    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "onDeath")
    public void onDeath(DamageSource source, CallbackInfo info) {
        Entity entity = source.getSource();
        if (entity == null || !entity.isPlayer()) return;
        Entity attacker = source.getAttacker();
        if(attacker != null) {
            FactionsMod.LOGGER.info("attacker not null");
            if (attacker.isPlayer()) {
                FactionsMod.LOGGER.info(attacker.getName().asString());
                FactionsMod.LOGGER.info("yo that's the attacker it's a real player");
                if (!attacker.getName().asString().equals(entity.getName().asString())) {
                    FactionsMod.LOGGER.info("ayo it knows it's a player attaker that's not itself");
                    FactionEvents.killedAPlayer((ServerPlayerEntity) attacker, (ServerPlayerEntity) entity);
                }
            }
        }

        FactionEvents.playerDeath((ServerPlayerEntity) (Object) this);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo info) {
        if (age % Config.TICKS_FOR_POWER != 0 || age == 0) return;
        FactionEvents.powerTick((ServerPlayerEntity) (Object) this);
    }

    @Inject(at = @At("HEAD"), method = "attack", cancellable = true)
    private void attack(Entity target, CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        if (target.isPlayer() && PlayerInteractEvents.preventFriendlyFire(player, (ServerPlayerEntity) target)) {
            info.cancel();
        }

        if (!target.isLiving() && !PlayerInteractEvents.actionPermitted(target.getBlockPos(), world, player)) {
            info.cancel();
        }
    }
}