package nl.enjarai.showmeyourskin.fake;

import net.minecraft.client.network.AbstractClientPlayerEntity;

public interface FakePlayerEntityRendererState {
    void show_me_your_skin$setPlayer(AbstractClientPlayerEntity player);
    AbstractClientPlayerEntity show_me_your_skin$getPlayer();
}
