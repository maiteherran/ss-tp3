package g7q12020;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main  {

    private static  ProgramParameters parameters;

    public static void main(String[] args) throws IOException {
        boolean generateOvitoFiles = false;
        boolean lastThirdVelocities = false; //punto 2
        boolean dcm = true;


        parameters = new ProgramParameters();
        parameters.parse(args);
        for(int j = 1;j<= 1;j++) {
//            List<Double> dcm = new ArrayList<>(); //punto 4
//            for (int i = 10; i <= 300; i += 10) {
                ParticlesGenerator initialParticlesGenerator = new ParticlesGenerator(parameters.getN(), parameters.getL());
                SimulationSpace simulationSpace = initialParticlesGenerator.generate();
                simulationSpace.simulate(parameters.getTime(), generateOvitoFiles, lastThirdVelocities, j);

                //System.out.println(parameters.getN() + " particles");
                //System.out.println(simulationSpace.getTime() + " seconds"); //le pido el time al simultation space pq si estoy en el caso de dcm, corta cuando la partícula grande choca contra la pared

                //Para el punto 1
                long numberOfCollisions = simulationSpace.getCollisionsCounter();
                System.out.println("Number of collisions: " + numberOfCollisions);

                double collisionFrequency = numberOfCollisions / simulationSpace.getTime();
                System.out.println("Collision frequency: " + collisionFrequency + " per sec");


                //para el punto 2
//                double thirdSectionAmountOfIterations = simulationSpace.getThirdSectionBreakpoints();
//                Map<Long, Double> thirdSectionAcumVelocitiesPerParticle = simulationSpace.getThirdSectionSpeedAcum();
//
//                //para el punto 3
//                List<Double> xBigParticle = simulationSpace.getBigParticleXPositions();
//                List<Double> yBigParticle = simulationSpace.getBigParticleYPositions();

//            }
//            writeDistancesOnFile(dcm,j);
        }
    }

    public static void writeDistancesOnFile(List<Double> dcm,int j) throws IOException {
        BufferedWriter file = new BufferedWriter(new FileWriter("dcmsmall"+j+".txt"));
        int iaux = 0;
        file.write(0 + " " + 0.0 + "\n");
        for (Double aDouble : dcm) {
            iaux += 10;
            file.write(iaux + " " + aDouble + "\n");
        }
        file.close();
    }

}
