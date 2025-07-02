package nl.enjarai.showmeyourskin.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import nl.enjarai.showmeyourskin.fake.FakePlayerEntityRendererState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements FakePlayerEntityRendererState {
    @Unique
    private AbstractClientPlayerEntity player;

    @Override
    public AbstractClientPlayerEntity show_me_your_skin$getPlayer() {
        return player;
    }
    @Override
    public void show_me_your_skin$setPlayer(AbstractClientPlayerEntity player) {
        this.player = player;
    }
}
