package g7q12020;

import org.apache.commons.cli.*;

public class ProgramParameters {
    private int N;
    private Double L;
    private Double time;


    public void parse (String[] commandLineArgs) {
        Options options = new Options();

        Option nParticles = new Option("N", true, "Number of small particles. Integer.");
        nParticles.setRequired(true);
        options.addOption(nParticles);

        Option areaSide = new Option("L",  true, "Length of area side. Double.");
        areaSide.setRequired(true);
        options.addOption(areaSide);

        Option t = new Option("time",  true, "Time. Double.");
        t.setRequired(true);
        options.addOption(t);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, commandLineArgs);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("Particles generator", options);
            System.exit(1);
        }

        N = Integer.parseInt(cmd.getOptionValue("N"));
        L = Double.parseDouble(cmd.getOptionValue("L"));
        time = Double.parseDouble(cmd.getOptionValue("time"));

    }

    public int getN() {
        return N;
    }

    public Double getL() {
        return L;
    }

    public Double getTime() {
        return time;
    }

}
