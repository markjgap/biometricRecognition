/**
 * Biometric_EarRecognition Project
 * Mark Gapasin
 * This class reads in user inputs and word documents/files
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class ReadFile {

    private static Scanner scannerInput;

    /**
     * the main interface that check if files are able to be opened or not
     * @param fileName is the name of the file being opened
     * @return is a File class object of the file being opened
     */
    public Scanner openFile(String fileName){
        try{
            scannerInput = new Scanner(new File(fileName));
        }
        catch (Exception e){
            System.out.println("Error! Could not open file: " + fileName);
        }
        return scannerInput;
    }

    /** Reads file where the first three lines help set up the structure of the net training
     *  example: 101	(dimension of input pattern)
     *           3	    (Dimension of output pattern (# of persons))
     *           2	    (Number of ear images to per person)
     *           6       (Total number of ears)
     *  Remaining integers are the inputs to train and what the outputs should be.
     *  @return List of integers
     */
    public ArrayList<Integer> readStructure() {
        ArrayList<Integer> contents;
        contents = new ArrayList<Integer>();
        int dimensions;

        // get structure of training
        for (int i = 0; i < 4; i++) {
            if (scannerInput.hasNextInt()) {
                dimensions = scannerInput.nextInt();
                contents.add(dimensions);
            } else {
                scannerInput.nextLine();
                dimensions = scannerInput.nextInt();
                contents.add(dimensions);
            }
        }
        return contents;
    }

    /**
     *  Reads file reads in the inputs and outputs to the net
     *  Remaining integers are the inputs to train and what the outputs should be.
     *  @return List of Characters where each Character Object contains its input and output
     */
    public ArrayList<Perceptron> readfile(){
        ArrayList<Integer> contents = new ArrayList<Integer>();
        ArrayList<Double> input = new ArrayList<Double>();
        Perceptron perceptron;
        ArrayList<Perceptron> perceptronList = new ArrayList<Perceptron>();
        String nameOfEarOwner;
        double number;

        contents = this.readStructure();
        perceptron = new Perceptron(contents.get(0), contents.get(1));

        scannerInput.nextLine();

        // set up perceptron objects into a list
        for (int i=0; i<contents.get(3); i++) {
            perceptron.setInputSize(contents.get(0));
            perceptron.setOutputSize(contents.get(1));
            perceptron.setTrainingSize(contents.get(3));

            // read input and output values
            while (scannerInput.hasNextDouble()) {
                number = scannerInput.nextDouble();
                input.add(number);
            }
            nameOfEarOwner = scannerInput.next();
            perceptron.setEarOwner(nameOfEarOwner);

            // Alert user if dimensions are not equal
            if(input.size()-contents.get(1) != contents.get(0)){
                System.out.println("Error: Dimensions are uneven. Can not train with different sizes.");
                System.exit(0);
            }
            int z = 0; // output index
            for (int x = 0; x < input.size(); x++) {

                // set inputs to perceptron
                if(x < perceptron.getInputSize()){
                    perceptron.setInputs(x, input.get(x));
                }
                // set target for perceptron
                else {
                    perceptron.setOutputs(z, input.get(x));
                    z++;
                }
            }

            perceptronList.add(perceptron);
            input = new ArrayList<Double>();
            perceptron = new Perceptron(contents.get(0), contents.get(1));
        }
        return perceptronList;
    }

    /**
     * Closes file properly
     */
    public void closeFile(){
        scannerInput.close();
    }
}