package io.github.rontyamc.lucentics.blocks.pedestals.pedestal_ritual;

import io.github.rontyamc.lucentics.blocks.pedestals.PedestalBehavior;
import io.github.rontyamc.lucentics.client.particle.GlowParticleOptions;
import io.github.rontyamc.lucentics.common.BaseBlockEntity;
import io.github.rontyamc.lucentics.common.behavior.BehaviorType;
import io.github.rontyamc.lucentics.common.dict.Colors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class PedestalRitualBehavior extends PedestalBehavior {
    public static final BehaviorType<PedestalRitualBehavior> TYPE = new BehaviorType<>("pedestal_ritual");

    private static final RandomSource RANDOM_SOURCE = RandomSource.create();

    public PedestalRitualBehavior(BaseBlockEntity be) {
        super(be);
    }

    @Override
    public void consume(int count) {
        super.consume(count);

        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = getPos();

        float p = Mth.lerp(RANDOM_SOURCE.nextFloat(), 0.5f, 1.0f);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 0.1f, p);

        int rgb = Colors.WHITE.getColorCode();
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        double Sx = pos.getX() + 0.5;
        double Sy = pos.getY() + 0.8;
        double Sz = pos.getZ() + 0.5;

        double Tx = Sx + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);
        double Ty = pos.getY() + 1.0;
        double Tz = Sz + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        serverLevel.sendParticles(new GlowParticleOptions(r,g,b), Sx, Sy, Sz, 5, Vx, Vy, Vz, 0.01);
    }

    @Override
    public void damageItem(int count) {
        boolean hold = !getContent().isEmpty();
        super.damageItem(count);

        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = getPos();

        float p = Mth.lerp(RANDOM_SOURCE.nextFloat(), 0.5f, 1.0f);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 0.1f, p);

        if (hold&& getContent().isEmpty()) level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 0.5f, 1.0f);;

        int rgb = Colors.RED.getColorCode();
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        double Sx = pos.getX() + 0.5;
        double Sy = pos.getY() + 0.8;
        double Sz = pos.getZ() + 0.5;

        double Tx = Sx + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);
        double Ty = pos.getY() + 1.0;
        double Tz = Sz + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        serverLevel.sendParticles(new GlowParticleOptions(r,g,b), Sx, Sy, Sz, 5, Vx, Vy, Vz, 0.01);
    }

    @Override
    public void catalyst() {
        super.catalyst();

        Level level = getWorld();
        if (!(level instanceof ServerLevel serverLevel)) return;

        BlockPos pos = getPos();

        float p = Mth.lerp(RANDOM_SOURCE.nextFloat(), 0.5f, 1.0f);
        level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 0.1f, p);

        int rgb = Colors.LIGHT_BLUE.getColorCode();
        float r = ((rgb >> 16) & 0xFF) / 255f;
        float g = ((rgb >> 8) & 0xFF) / 255f;
        float b = (rgb & 0xFF) / 255f;

        double Sx = pos.getX() + 0.5;
        double Sy = pos.getY() + 0.8;
        double Sz = pos.getZ() + 0.5;

        double Tx = Sx + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);
        double Ty = pos.getY() + 1.0;
        double Tz = Sz + Mth.lerp(RANDOM_SOURCE.nextDouble(), -0.2, 0.2);

        double Vx = Tx - Sx;
        double Vy = Ty - Sy;
        double Vz = Tz - Sz;

        serverLevel.sendParticles(new GlowParticleOptions(r,g,b), Sx, Sy, Sz, 5, Vx, Vy, Vz, 0.01);
    }

    @Override
    public BehaviorType<?> getType() {
        return TYPE;
    }
}
