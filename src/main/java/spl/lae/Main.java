package spl.lae;
import java.io.IOException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
    if (args.length != 3) {
        System.out.println("user input must be <number of threads> <path/to/input/file> <path/to/output/file>");
        return;
    }

    int numberOfThreads;
    try {
        numberOfThreads = Integer.parseInt(args[0]);
    } catch (NumberFormatException e) {
        System.out.println("numberOfThreads must be an integer");
        return;
    }

    ComputationNode computationRoot = null;

    try {
      InputParser iParser = new InputParser();
      computationRoot = iParser.parse(args[1]);
    }

    catch(Exception e) {
      OutputWriter.write(e.getMessage(), args[2]);
      return;
    }

    LinearAlgebraEngine LAE = new LinearAlgebraEngine(numberOfThreads);

    double[][] matrix = LAE.run(computationRoot).getMatrix();
    OutputWriter.write(matrix, args[2]);

}
}