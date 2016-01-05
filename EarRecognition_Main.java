/**
 * Biometric_EarRecognition
 * Author: Mark Gapasin
 * Main class that runs the biometric ear recognition program
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

class EarRecognition_Main{

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int inputInt;
        int numOfEarsPerPerson = 0;
        int numOfPersons = 0;
        ArrayList<String> earOwner = new ArrayList<String>();
        ArrayList<String> trainingImgs = new ArrayList<String>();
        ArrayList<Integer> structure = new ArrayList<Integer>();
        ArrayList<Perceptron> perceptronList = new ArrayList<Perceptron>();
        Perceptron mlp = new Perceptron();
        ArrayList<Perceptron> perceptronTrainList = new ArrayList<Perceptron>();
        int outSize = 0;
        double[] actual = new double[1];

        String inputString = new String();
        boolean go = false;
        while(!go) {
            try {
                System.out.println("\n================================================");
                System.out.println("BIOMETRIC EAR RECOGNITION NEURAL NETWORK SYSTEM");
                System.out.println("================================================");
                System.out.println("\nSelect from menu: \n" +
                                   "  1 - To train \n" +
                                   "  2 - To test\n" +
                                   "  3 - To create an input file to train or test\n" +
                                   "  0 - Quit");
                inputInt = input.nextInt();

                switch (inputInt) {

                    // train network
                    case 1:
                        System.out.println("\n========");
                        System.out.println("TRAINING");
                        System.out.println("========");
                        System.out.println("Enter train file (include extension):");
                        String trainingFile = input.next();
                        // read input file
                        System.out.println("\nTraining in progress...");
                        ReadFile readTrain = new ReadFile();
                        readTrain.openFile(trainingFile);
                        // set up for training
                        perceptronTrainList = readTrain.readfile();
                        readTrain.closeFile();
                        // train
                        outSize = perceptronTrainList.get(0).getOutputSize();
                        mlp = new Perceptron(101, 5, outSize, 1, 0.2);
                        // max epoch 150000
                        for(int i = 0; i <= 150000; i++) {
                            for (Perceptron perceptron : perceptronTrainList) {
                                mlp.train(perceptron.getInput(), perceptron.getOutput());
                            }
                        }
                        System.out.println("Training completed\n");
                        break;

                    // test network
                    case 2:
                        if (mlp.getInputNeurons()==null) {
                            System.out.println("\nERROR: Network has not been trained yet!");
                        }
                        else {
                        System.out.println("\n=======");
                        System.out.println("TESTING");
                        System.out.println("=======");
                        System.out.println("Enter test file (include extension):");
                        String testingFile = input.next();
                        System.out.println("\nTesting in progress...");
                        // read input file
                        ReadFile readTest = new ReadFile();
                        readTest.openFile(testingFile);
                        // set up for testing
                        ArrayList<Perceptron> perceptronTestList = readTest.readfile();
                        readTest.closeFile();

                        for (Perceptron perceptron2 : perceptronTestList) {
                            System.out.println("Classifying:");
                            for (int j = 0; j < perceptron2.getInputSize(); j++) {
                                System.out.print(perceptron2.getInput(j) + " ");
                            }
                            System.out.println("\nOutput: ");
                            double[] y_output = new double[perceptron2.getInputSize()];
                            for (int k = 0; k < perceptron2.getInputSize(); k++) {
                                y_output[k] = perceptron2.getInput(k);
                            }
                            actual = new double[outSize];
                            for (int m = 0; m < outSize; m++) {
                                actual[m] = perceptron2.activationFunction(mlp.classify(y_output))[m];
                                //System.out.print((int) actual[m] + " ");
                                System.out.print((mlp.classify(y_output))[m]+" ");
                                }
                            for(Perceptron perceptron: perceptronTrainList){
                                Object[] arr1 = {perceptron.getOutput()};
                                Object[] arr2 = {actual};
                                if(Arrays.deepEquals(arr1, arr2)){
                                    System.out.println("-- Test ears closest match: " + perceptron.getEarOwner());
                                    break;
                                }
                            }
                            System.out.println();
                        }
                    }
                        break;

                    // create input file
                    case 3:
                        System.out.println("\n==================");
                        System.out.println("CREATE INPUT FILE");
                        System.out.println("==================");
                        System.out.println("Enter number of unique ears:");
                        numOfPersons = input.nextInt();
                        System.out.println("Enter number of images per unique ear:");
                        numOfEarsPerPerson = input.nextInt();
                        input.nextLine();
                        try{
                            earOwner = new ArrayList<String>();
                            trainingImgs = new ArrayList<String>();
                            for(int i = 0; i < numOfPersons; i++){
                                System.out.println("Name of ear(s) owner for set "+(i+1)+" :");
                                earOwner.add(input.nextLine());
                                System.out.println("Please enter " + earOwner.get(i) + "'s ear image(s) " +
                                        "Separate each image by a single space (include extension)");
                                while(inputString.equals("")) {
                                    inputString = input.nextLine();
                                }
                                String[] in = inputString.split(" ");
                                if(in.length != numOfEarsPerPerson){
                                    System.out.println("Error: Number of images required for person " +
                                            (i+1) + " should be "+ numOfEarsPerPerson);
                                    System.exit(0);
                                }
                                else {
                                    trainingImgs.add(inputString);
                                    inputString = "";
                                }
                            }
                            // find edges of image
                            ProcessImage img = new ProcessImage();
                            ArrayList<ArrayList<Double>> trainInput = img.run(trainingImgs);
                            System.out.println("Please enter file name to save results to (include extension)");
                            String fileNameString = input.next();
                            ProcessImage trainFile = new ProcessImage();
                            trainFile.saveInputFile(fileNameString, trainInput, numOfPersons, numOfEarsPerPerson, earOwner);
                        }catch(InputMismatchException e){
                            System.out.println("Error: Input needs to be an integer value of 1 or greater.");
                            input.nextLine();
                            inputInt = 1;
                            break;
                        }

                        break;

                    // quit program
                    case 0:
                        System.out.print("Exited program.\n");
                        System.exit(0);

                    default:
                        System.out.println("Invalid input.");
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input!");
                go = true;
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
