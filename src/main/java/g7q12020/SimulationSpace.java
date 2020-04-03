package g7q12020;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SimulationSpace {
    private static BufferedWriter file;
    private List<Particle> particles;
    private double length;
    private final int FRAMES_PER_SECOND = 60;
    private final double PASO = 1.0 / FRAMES_PER_SECOND;
    private long collisionsCounter;
    private Map<Long, Double> thirdSectionSpeedAcum;
    private long thirdSectionBreakpoints;
    private double time;
    private List<Double> bigParticleXPositions;
    private List<Double> bigParticleYPositions;
    private List<Double> timeCrashes;
    private Map<Double,Double> dcm;

    public SimulationSpace(double length, List<Particle> particles) {
        this.length = length;
        this.particles = particles;
        this.collisionsCounter = 0;
        this.thirdSectionSpeedAcum = new HashMap<>();
        this.thirdSectionBreakpoints = 0;
        this.time = 0;
        this.bigParticleXPositions = new ArrayList<>();
        this.bigParticleYPositions = new ArrayList<>();
        this.timeCrashes = new ArrayList<>();
        this.dcm = new HashMap<>();
    }

    public void simulate(double totalTime, boolean generateOvitoFiles, boolean lastThirdVelocities, int dcm) throws IOException {
        int f = 0;
        double lastThird = totalTime * (2 / 3.0);
        if (generateOvitoFiles) {
            file = new BufferedWriter(new FileWriter("output.txt"));
        }
        boolean first4 = false;
        double x = 0;
        double y = 0;

        while (time < totalTime) {
            Collision nextCrash = calculateNextCollision();
            double nextCrashTime = nextCrash.getTime();
            if (totalTime - time < nextCrashTime) {
                break;
            }
            timeCrashes.add(nextCrashTime * 10000);

            time += nextCrashTime;
            for (Particle p : particles) {
                if (p.isBig()) {
                    bigParticleXPositions.add(p.getX());
                    bigParticleYPositions.add(p.getY());
                }
                p.moveX(nextCrashTime);
                p.moveY(nextCrashTime);
            }

            if (generateOvitoFiles) {
                while (f < (int) Math.floor(time / PASO)) {
                    writeOnFile(f, time);
                    f++;
                }
            }

            if (lastThirdVelocities && time >= lastThird) {
                for (Particle p : particles) {
                    if (thirdSectionSpeedAcum.containsKey(p.getId())) {
                        double d = thirdSectionSpeedAcum.get(p.getId()) + p.getSpeedModule();
                        thirdSectionSpeedAcum.put(p.getId(), d);
                    } else {
                        thirdSectionSpeedAcum.put(p.getId(), p.getSpeedModule());
                    }
                }
                thirdSectionBreakpoints++;
            }

            collisionsCounter++;
            if (nextCrash.isParticlesCollision()) {
                ParticlesCrash particlesCrash = (ParticlesCrash) nextCrash;
                particlesCrash.crash();

            } else {
                WallCrash wallCrash = (WallCrash) nextCrash;
                wallCrash.crash();
                if (dcm > 0 && wallCrash.getA().isBig()) {
                    break;
                }
            }
            if(dcm >/*=*/ 1 && time >= totalTime/2){
                Particle bigParticle /*= particles.get(1);*/ = particles.stream()
                        .filter(Particle::isBig)
                        .collect(Collectors.toList())
                        .get(0);
                if(!first4){
                    x = bigParticle.getX();
                    y = bigParticle.getY();
                    first4 = true;
                }
                this.dcm.put(time,Math.pow(bigParticle.getX() - x, 2) + Math.pow(bigParticle.getY() - y, 2));
            }
        }
        if (generateOvitoFiles) {
            file.close();
        }
        for (Particle p : particles) {
            if (thirdSectionSpeedAcum.get(p.getId()) != null) {
                thirdSectionSpeedAcum.put(p.getId(), thirdSectionSpeedAcum.get(p.getId()) / thirdSectionBreakpoints);
            }
        }

        writeBPPositionsOnFile(dcm);
    }

    private Collision calculateNextCollision() {
        final List<Collision> collisions = new ArrayList<>();
        collisions.add(null);
        for (Particle p : particles) {
            double timeToCollide;
            //List<Particle> parts = particles.stream().filter(q -> q.getId() <= p.getId()).collect(Collectors.toList());
            for (Particle q : particles) {
                timeToCollide = p.getTimeToParticleCollision(q);
                if (collisions.get(0) == null || timeToCollide < collisions.get(0).getTime()) {
                    collisions.add(0, new ParticlesCrash(p, q, timeToCollide));
                }
            }

            Wall wall;
            double timeToVerticalWallCollision = p.getTimeToVerticalWallCollision(length);
            double timeToHorizontalWallCollision = p.getTimeToHorizontalWallCollision(length);

            if (timeToHorizontalWallCollision >= timeToVerticalWallCollision) {
                timeToCollide = timeToVerticalWallCollision;
                wall = Wall.VERTICAL;
            } else {
                timeToCollide = timeToHorizontalWallCollision;
                wall = Wall.HORIZONTAL;
            }
            if (collisions.get(0) == null || timeToCollide < collisions.get(0).getTime()) {
                collisions.add(0, new WallCrash(p, wall, timeToCollide));
            }

        }
        return collisions.get(0);
    }

    public void writeOnFile(int frameNumber, double time) throws IOException {
        double d = time - frameNumber * PASO;
        file.write(String.valueOf(particles.size() + 4));
        file.newLine();
        file.write(String.valueOf(frameNumber));
        file.newLine();
        file.write("-1 0 0 0.005 0\n");
        file.write("-2 0 " + length + " 0.005 0\n");
        file.write("-3 " + length + " 0 0.005 0\n");
        file.write("-4 " + length + " " + length + " 0.005 0\n");

        for (Particle p : particles) {

            file.write(p.getId() + " " +
                    (-(p.getVx() * d) + p.getX()) + " " +
                    (-(p.getVy() * d) + p.getY()) + " " +
                    p.getRadius() + " " +
                    p.getSpeedModule());
            file.newLine();
        }
    }

    public void writeBPPositionsOnFile(int j) throws IOException {
        if (j < 1)
            return;
        BufferedWriter file = new BufferedWriter(new FileWriter("graph3v05n200.txt"));

        for (int i = 0; i < bigParticleXPositions.size(); i++) {
            file.write(bigParticleXPositions.get(i) + " " + bigParticleYPositions.get(i) + "\n");
        }

//        file.write(timeCrashes
//                .stream().map(Object::toString)
//                .collect(Collectors.joining("\n")));

//        file.write(thirdSectionSpeedAcum.values()
//                .stream().map(Object::toString)
//                .collect(Collectors.joining("\n")));

//        BufferedWriter file = new BufferedWriter(new FileWriter("dcm" + j + ".txt"));
//        for (Map.Entry<Double,Double> entry:this.dcm.entrySet()) {
//            file.write(entry.getKey() + " " + entry.getValue() + "\n");
//        }
//        file.close();

//        List<Double> iV = new ArrayList<>();
//        for(Particle p : particles){
//            iV.add(p.getSpeedModule()*10000);
//        }
//        file.write(iV
//                .stream().map(Object::toString)
//                .collect(Collectors.joining("\n")));


        file.close();

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

    public List<Double> getBigParticleXPositions() {
        return bigParticleXPositions;
    }

    public List<Double> getBigParticleYPositions() {
        return bigParticleYPositions;
    }

    public List<Double> getTimeCrashes() {
        return timeCrashes;
    }

    public Map<Double,Double> getDcm() {
        return dcm;
    }
}
