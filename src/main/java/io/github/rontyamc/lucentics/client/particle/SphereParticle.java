package io.github.rontyamc.lucentics.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;

public class SphereParticle extends TextureSheetParticle {
    SpriteSet sprites;

    protected SphereParticle(ClientLevel level, double x, double y, double z,
                             double xd, double yd, double zd,
                             float red, float green, float blue,
                             SpriteSet sprites) {
        super(level, x, y, z, xd, yd, zd);
        this.xd = xd;
        this.yd = yd;
        this.zd = zd;
        this.sprites = sprites;
        this.setColor(red, green, blue);
        this.quadSize = 0.1F;
        this.lifetime = 20;
        this.gravity = 0.0F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SphereParticleOptions> {
        private final SpriteSet sprites;
        public Provider(SpriteSet sprites) { this.sprites = sprites; }

        @Override
        public Particle createParticle(SphereParticleOptions options, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new SphereParticle(level, x, y, z, xd, yd, zd, options.red(), options.green(), options.blue(), sprites);
        }
    }
}
