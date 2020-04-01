package g7q12020;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class ParticlesGenerator {
    private long N;
    private Double L;

    private final static Double MIN_RADIUS = 0.0;
    private final static Double MAX_RADIUS = 0.0;
    private final static Double MIN_ANGLE = 0.0;
    private final static Double MAX_ANGLE = 2*Math.PI;
    private final static Double MIN_VEL_MOD = 0.0;
    private final static double MAX_VEL_MOD = 0.1;

    private final static double defaultSpeedModule = 0.03;

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    public ParticlesGenerator(long N, Double L) {
        this.N = N;
        this.L = L;
    }

    public SimulationSpace generate() {
        Random rand = new Random();
        List<Particle> initialParticlesDisposition = new ArrayList<>();

        Particle bigParticle = new Particle(atomicInteger.incrementAndGet()-1, L/2, L/2, 0.0, 0.0, 0.05, 100, true);
        initialParticlesDisposition.add(bigParticle);

        while (initialParticlesDisposition.size() != N) {
            Particle p = new Particle(
                    atomicInteger.incrementAndGet()-1,
                    Math.random()*(L - 2*0.005) + 0.005,
                    Math.random()*(L - 2*0.005) + 0.005,
                    Math.random() * MAX_VEL_MOD,
                    Math.random()*(MAX_ANGLE - MIN_ANGLE) + MIN_ANGLE,
                    0.005,
                    0.1,
                    false
            );
            boolean dontOverlap = initialParticlesDisposition.stream().noneMatch(particle ->
                    Math.sqrt(Math.pow(particle.x - p.x, 2) + Math.pow(particle.y - p.y, 2)) < particle.radius + p.radius);
            if (dontOverlap) {
                initialParticlesDisposition.add(p);
            }
        }
        return new SimulationSpace(L, bigParticle, initialParticlesDisposition);
    }
}
