package g7q12020;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimulationSpace {
    private static BufferedWriter file;
    private Particle bigParticle;
    private List<Particle> particles;
    private double length;
    private final int FRAMES_PER_SECOND = 60;
    private final double PASO = 1.0/FRAMES_PER_SECOND;
    private long collisionsCounter;
    private Map<Long, Double> thirdSectionSpeedAcum;
    private long thirdSectionBreakpoints;
    private double time;

    public SimulationSpace(double length, Particle bigParticle, List<Particle> particles) {
        this.bigParticle = bigParticle;
        this.length = length;
        this.particles = particles;
        this.collisionsCounter = 0;
        this.thirdSectionSpeedAcum = new HashMap<>();
        thirdSectionBreakpoints = 0;
        this.time = 0;

    }

    public void simulate(double totalTime, boolean generateOvitoFiles, boolean lastThirdVelocities, boolean dcm) throws IOException {
        int f = 0;
        double lastThird = totalTime*(1/3.0);
        if(generateOvitoFiles) {
            file = new BufferedWriter(new FileWriter("output.txt"));
        }

        while (time < totalTime) {
            Collision nextCrash = calculateNextCollision();
            double nextCrashTime = nextCrash.getTime();
            if (totalTime - time < nextCrashTime) { return; }

            time += nextCrashTime;
            for (Particle p : particles) {
                p.moveX(nextCrashTime);
                p.moveY(nextCrashTime);
            }

            if(generateOvitoFiles) {
                while (f < (int) Math.floor(time/PASO))  {
                    writeOnFile(f, time);
                    f++;
                }
            }

            if (lastThirdVelocities && time < lastThird)  {
                for (Particle p : particles) {
                    if (thirdSectionSpeedAcum.containsKey(p.getId())) {
                        double d = thirdSectionSpeedAcum.get(p.getId()) + p.getSpeedModule();
                        thirdSectionSpeedAcum.put(p.getId(), d);
                    } else {
                        Double a = 0.0;
                        thirdSectionSpeedAcum.put(p.getId(), a);
                    }
                }
                thirdSectionBreakpoints++;
            }

            collisionsCounter++;
            if(nextCrash.isParticlesCollision()) {
                ParticlesCrash particlesCrash = (ParticlesCrash) nextCrash;
                particlesCrash.crash();

            } else {
                WallCrash wallCrash = (WallCrash) nextCrash;
                wallCrash.crash();
                if(dcm && wallCrash.getA().isBig()) {
                    break;
                }
            }

        }
        if(generateOvitoFiles) {
            file.close();
        }
    }

    private Collision calculateNextCollision() {
        final List<Collision> collisions = new ArrayList<>();
        collisions.add(null);
        for(Particle p : particles){
            double timeToCollide;
            List<Particle> parts = particles.stream().filter(q -> q.getId() <= p.getId()).collect(Collectors.toList());
            for (Particle q : parts) {
                timeToCollide =  p.getTimeToParticleCollision(q);
                if (collisions.get(0) == null || timeToCollide < collisions.get(0).getTime()) {
                    collisions.add(0, new ParticlesCrash(p, q, timeToCollide));
                }
            }

            Wall wall;
            double timeToVerticalWallCollision = p.getTimeToVerticalWallCollision(length);
            double timeToHorizontalWallCollision = p.getTimeToHorizontalWallCollision(length);

            if (timeToHorizontalWallCollision >= timeToVerticalWallCollision) {
                timeToCollide = timeToVerticalWallCollision;
                wall =  Wall.VERTICAL;
            } else {
                timeToCollide = timeToHorizontalWallCollision;
                wall = Wall.HORIZONTAL;
            }
            if  (collisions.get(0) == null || timeToCollide < collisions.get(0).getTime()){
                collisions.add(0,new WallCrash(p, wall, timeToCollide));
            }

        }
        return collisions.get(0);
    }

    public void writeOnFile(int frameNumber, double time) throws IOException  {
        double d = time- frameNumber*PASO;
        file.write(String.valueOf(particles.size() + 4));
        file.newLine();
        file.write(String.valueOf(frameNumber));
        file.newLine();
        file.write(  "-1 0 0 0.005 0\n");
        file.write(  "-2 0 " +length+ " 0.005 0\n");
        file.write(  "-3 " +length+ " 0 0.005 0\n");
        file.write(  "-4 " +length+ " "+length+ " 0.005 0\n");
    
        for (Particle p : particles)  {

            file.write(  p.getId() + " " +
                    (- (p.getVx()*d) + p.getX()) + " " +
                    (- (p.getVy()*d) + p.getY()) + " " +
                    p.getRadius() + " " +
            p.getSpeedModule());
            file.newLine();
        }
    }

    public long getCollisionsCounter() {
        return collisionsCounter;
    }

    public Map<Long, Double> getThirdSectionSpeedAcum() {
        return thirdSectionSpeedAcum;
    }

    public long getThirdSectionBreakpoints() {
        return thirdSectionBreakpoints;
    }

    public double getTime() {
        return time;
    }
}
