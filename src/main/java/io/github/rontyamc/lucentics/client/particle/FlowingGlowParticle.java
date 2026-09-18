package io.github.rontyamc.lucentics.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class FlowingGlowParticle extends TextureSheetParticle {
    private final SpriteSet sprites;
    private final NonNullList<Vec3> waypoints = NonNullList.create();
    private final int duration;

    protected FlowingGlowParticle(ClientLevel level, List<Vec3> waypoints, Vec3 delta, float red, float blue, float green, int duration, float scale, SpriteSet sprites) {
        super(level, waypoints.getFirst().x, waypoints.getFirst().y, waypoints.getFirst().z);
        this.sprites = sprites;
        this.duration = duration;
        this.xd = delta.x();
        this.yd = delta.y();
        this.zd = delta.z();
        this.setColor(red, green, blue);
        this.quadSize = scale;
        this.lifetime = this.duration;
        this.gravity = 0.0f;
        this.setSpriteFromAge(sprites);

        for (Vec3 vec3 : waypoints) {
            double x = vec3.x + this.xd;
            double y = vec3.y + this.yd;
            double z = vec3.z + this.zd;
            this.waypoints.add(new Vec3(x, y, z));
        }
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);

        double progress = Math.clamp((double) this.age / this.lifetime, 0.0, 1.0);
        Vec3 pos = pointOnPath(progress);
        this.setPos(pos.x, pos.y, pos.z);
    }

    private Vec3 pointOnPath(double progress) {
        List<Double> length = new ArrayList<>();
        for (int i = 0; i < this.waypoints.size() - 1; i++) {
            length.add(waypoints.get(i).distanceTo(waypoints.get(i + 1)));
        }
        double total = length.stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 1.0E-6) return waypoints.getLast();

        double travelled = progress * total;

        for (int i = 0; i < this.waypoints.size() - 1; i++) {
            if (travelled <= length.get(i)) return waypoints.get(i).lerp(waypoints.get(i + 1), length.get(i) <= 1.0E-6 ? 1.0 : travelled / length.get(i));
            travelled -= length.get(i);
        }
        return waypoints.get(-2).lerp(waypoints.getLast(), length.getLast() <= 1.0E-6 ? 1.0 : travelled / length.getLast());
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<FlowingGlowParticleOptions> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(FlowingGlowParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new FlowingGlowParticle(level, options.waypoints(), options.delta(), options.red(), options.green(), options.blue(),
                    options.duration(), options.scale(), sprites);
        }
    }
}
