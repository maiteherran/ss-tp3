package g7q12020;

import java.io.IOException;
import java.util.Map;

public class Main  {

    private static  ProgramParameters parameters;

    public static void main(String[] args) throws IOException {
        boolean generateOvitoFiles = true;
        boolean lastThirdVelocities = false; //punto 2
        boolean dcm = false; //punto 3

        parameters = new ProgramParameters();
        parameters.parse(args);

        ParticlesGenerator initialParticlesGenerator = new ParticlesGenerator(parameters.getN(), parameters.getL());
        SimulationSpace simulationSpace = initialParticlesGenerator.generate();

        simulationSpace.simulate(parameters.getTime(), generateOvitoFiles, lastThirdVelocities, dcm);

        System.out.println(parameters.getN() + " particles");
        System.out.println(simulationSpace.getTime() + " seconds"); //le pido el time al simultation space pq si estoy en el caso de dcm, corta cuando la partícula grande choca contra la pared

        //Para el punto 1
        long numberOfCollisions = simulationSpace.getCollisionsCounter();
        System.out.println("Number of collisions: " + numberOfCollisions);

        double collisionFrequency = numberOfCollisions/simulationSpace.getTime();
        System.out.println("Collision frequency: " + collisionFrequency + " per sec");


        //para el punto 2
        double thirdSectionAmountOfIterations = simulationSpace.getThirdSectionBreakpoints();
        Map<Long, Double> thirdSectionAcumVelocitiesPerParticle = simulationSpace.getThirdSectionSpeedAcum();

        //para el punto 3


    }

}
