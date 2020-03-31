package g7q12020;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

    public SimulationSpace(double length, Particle bigParticle, List<Particle> particles) {
        this.bigParticle = bigParticle;
        this.length = length;
        this.particles = particles;
        this.collisionsCounter = 0;
        this.thirdSectionSpeedAcum = new HashMap<>();
        thirdSectionBreakpoints = 0;

    }

    public void simulate(double totalTime, boolean generateOvitoFiles, boolean lastThirdVelocities, boolean dcm) throws IOException {
        int f = 0;
        file = new BufferedWriter(new FileWriter("output.txt"));
        double time = 0;
        

        while (time < totalTime) {
            Crash nextCrash = calculateNextCollision();
            double nextCrashTime = nextCrash.getTime();
            if (totalTime - time < nextCrashTime) { return; }
            
            for (Particle p : particles) {
                p.move(nextCrashTime);
            }
            
            time += nextCrashTime;


            if(generateOvitoFiles) {
                while (f < (int) Math.floor(time/PASO))  {
                    writeOnFile(f, time);
                    f++;
                }
            }

            double lastThird = totalTime*(1/3.0);

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

            if(nextCrash.getWall() == null){
                nextCrash.getA().crashVx(nextCrash.elasticXcollision());
                nextCrash.getA().crashVy(nextCrash.elasticYcollision());
                nextCrash.getB().crashVx(-nextCrash.elasticXcollision());
                nextCrash.getB().crashVy(-nextCrash.elasticYcollision());

            } else {
                if(nextCrash.getWall().equals(Wall.VERTICAL)){
                    nextCrash.getA().invertVx();
                } else {
                    nextCrash.getA().invertVy();
                }
                nextCrash.getA().setCrashed(true);
            }
            collisionsCounter++;
        }
        file.close();
    }

    private Crash calculateNextCollision() {
        Crash firstCollision = null;

        for(Particle p : particles){
            double timeToVerticalWallCollision;
            double timeToHorizontalWallCollision;
            double timeToCollide;
            Wall wall;

            timeToVerticalWallCollision = getTimeToWallCollision(p.getX(), p.getRadius(), p.getVx());
            timeToHorizontalWallCollision = getTimeToWallCollision(p.getY(), p.getRadius(), p.getVy());


            if (timeToHorizontalWallCollision >= timeToVerticalWallCollision) {
                timeToCollide = timeToVerticalWallCollision;
                wall =  Wall.VERTICAL;
            } else {
                timeToCollide = timeToHorizontalWallCollision;
                wall = Wall.HORIZONTAL;
            }
            if  (firstCollision == null || timeToCollide < firstCollision.getTime()){
                firstCollision = new Crash(p, wall, timeToCollide);
            }

            List<Particle> parts = particles.stream().filter(q -> q.getId() <= p.getId()).collect(Collectors.toList());
            for (Particle q : parts) {
                timeToCollide = getTimeToParticleCollision(p, q);
                if (timeToCollide < firstCollision.getTime()) {
                    firstCollision = new Crash(p, q, timeToCollide);
                }
            }
        }

        return firstCollision;
    }

    private double getTimeToWallCollision(double pos, double radius, double vel) {
        if (vel > 0){
            return (length - pos - radius ) * (1/vel);
        } else if (vel < 0) {
            return (- pos + radius ) * (1/vel);
        }
        return Double.MAX_VALUE;
    }


    private double getTimeToParticleCollision(Particle p, Particle q) {
        double time;
        double dRadius = p.getRadius() + q.getRadius();
        double dX = p.getX()-q.getX();
        double dVx = p.getVx()-q.getVx();
        double dY = p.getY()-q.getY();
        double dVy = p.getVy()-q.getVy();
        double dvdr = dVy*dY+dVx*dX;

        if (dvdr >= 0) {
            return Double.MAX_VALUE;
        }

        double dRr = Math.pow(dY, 2) + Math.pow(dX, 2);
        double dVv = Math.pow(dVy, 2) + Math.pow(dVx, 2);
        double d = Math.pow(dvdr, 2) - dVv * (dRr - Math.pow(dRadius, 2));

        if (d < 0) {
            return Double.MAX_VALUE;
        }
        time = -(dvdr + Math.sqrt(d))/dVv;
        return time;
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
                    p.getRadius());
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
}
