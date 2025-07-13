package nl.enjarai.showmeyourskin.pond;

import net.minecraft.client.network.AbstractClientPlayerEntity;

public interface PlayerEntityRenderStateDuck {
    void show_me_your_skin$setPlayer(AbstractClientPlayerEntity player);
    AbstractClientPlayerEntity show_me_your_skin$getPlayer();
}
