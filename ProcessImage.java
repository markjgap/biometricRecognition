/**
 * Biometric_EarRecognition
 * Author: Mark Gapasin
 * Class finds featured points on ear to recognize
 */

import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ProcessImage {

    /**
     * Method checks if files are able to be opened or not
     * @param fileName is the name of the file being opened
     * @return is a File class object of the file being opened
     */
    public Scanner openFile(String fileName){
        Scanner input = new Scanner(System.in);
        try{
            input = new Scanner(new File(fileName));
        }
        catch (Exception e){
            System.out.println("Error! Could not open file: " + fileName);

        }
        return input;
    }

    public ArrayList<ArrayList<Double>> run(ArrayList<String> filesList) throws IOException {
        ArrayList<Double> normalVal = new ArrayList<Double>();
        ArrayList<Double> distance = new ArrayList<Double>();
        ArrayList<ArrayList<Integer>> points = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Double>> normalizedList = new ArrayList<ArrayList<Double>>();

        for(String n: filesList) {
            ArrayList<String> namesList = new ArrayList<String>();
            String[] f = n.split(" ");
            for(int i = 0; i <f.length; i++){
                namesList.add(f[i]);
            }

            for(String name : namesList) {
                // find edges
                Canny_Edge_Detector ced = new Canny_Edge_Detector();
                // set parameters for edge detection
                ced.setGaussianKernelRadius((float) 4.0);
                ced.setHighThreshold((float) 3.0);
                ced.setLowThreshold((float) 1.0);
                try {
                    ij.ImagePlus cannyImg = new ImagePlus(name);
                    ImagePlus pic = ced.process(cannyImg);

                    // save image
                    String[] parts = name.split("\\.");
                    FileSaver outputFile = new FileSaver(pic);
                    // save edge image
                    outputFile.saveAsJpeg(parts[0] + "-EdgeOutput.jpg");
                    // create binary text of image
                    outputFile.saveAsText(parts[0] + ".txt");
                    // find featured points
                    points = getFeaturePoints(parts[0] + ".txt");
                    // find euclidean distance
                    distance = euclideanDistance(points);
                    // normalize values
                    normalVal = normalize(distance);
                    normalizedList.add(normalVal);

                } catch (Exception e) {
                    System.out.println("Error: Could not open file: " + name);
                    System.exit(0);
                }
            }
        }
        return normalizedList;
     }

    public ArrayList<ArrayList<Integer>> getFeaturePoints(String fileName){

        Set<Integer> guide = new HashSet<Integer>();
        for(int i = 58; i <= 258; i+=2){
            guide.add(i);
        }
        ArrayList<ArrayList<Integer>> position = new ArrayList<ArrayList<Integer>>();
        Scanner input = openFile(fileName);
        String line;
        for(int row = 0; row < 333; row++) {
            line = input.nextLine();
            int col = 1;
            if (guide.contains(row)) {
                while (input.nextInt()!=255) {
                    col++;
                }
                ArrayList<Integer> pos = new ArrayList<Integer>();
                pos.add(col); // x position
                pos.add(row); // y position
                position.add(pos);
            }
        }
        return position;
    }

    public ArrayList<Double> euclideanDistance(ArrayList<ArrayList<Integer>> positions){
        ArrayList<Integer> center = new ArrayList<Integer>();
        ArrayList<Double> euclidDist = new ArrayList<Double>();
        center.add(126);
        center.add(156);
        for(ArrayList<Integer> xy : positions){
            int difference = xy.get(0) - center.get(0);
            double squareX = Math.pow(difference, 2);
            difference = xy.get(1) - center.get(1);
            double squareY = Math.pow(difference, 2);
            double value = squareX + squareY;
            double root = Math.sqrt(value);
            euclidDist.add(root);
        }
        return euclidDist;
    }

    public ArrayList<Double> normalize(ArrayList<Double> euclideanDist){
        ArrayList<Double> normalizedValues = new ArrayList<Double>();
        double min = euclideanDist.get(0);
        double max = euclideanDist.get(0);
        // set max and min values
        for(Double val : euclideanDist){
            if (val < min){
                min = val;
            }
            if(val > max){
                max = val;
            }
        }
        // normalize values between 0 and 1
        for(Double val : euclideanDist){
            double normal = (val - min)/(max - min);
            normalizedValues.add(normal);
        }
        return normalizedValues;
    }

    public void saveInputFile(String fileName, ArrayList<ArrayList<Double>> inputsList,
                              int numOfPersons, int numOfEarsPerPerson, ArrayList<String> earOwner){
        try{
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(101 + "\t\tDimension of input pattern\n");
            bufferedWriter.write(numOfPersons + "\t\tDimension of output pattern (# of persons)\n");
            bufferedWriter.write(numOfEarsPerPerson + "\t\tNumber of ear images to per person\n");
            bufferedWriter.write(numOfEarsPerPerson*numOfPersons + ("\t\tTotal number of ears\n\n"));
            int count = 0;
            int index = 0;
            for(ArrayList<Double> inputs : inputsList) {
                // save inputs
                for (Double in : inputs) {

                    DecimalFormat df = new DecimalFormat("#0.0000");
                    String val = df.format(in);

                    bufferedWriter.write(val + " ");
                }
                bufferedWriter.write("\n");

                // save target
                ArrayList<Integer> target = new ArrayList<Integer>();
                for (int i = 0; i < numOfPersons; i++) {
                    target.add(0);
                }
                target.set(index, 1);
                count++;
                for(int val : target){
                    bufferedWriter.write(val + " ");
                }
                bufferedWriter.write("\n" + earOwner.get(index));
                bufferedWriter.write("\n\n");
                if (count == numOfEarsPerPerson) {
                    index++;
                    count = 0;
                }
            }
            bufferedWriter.close();
            fileWriter.close();
            System.out.println("-- Saved file " + fileName +"\n");
        }catch(Exception e){
            System.out.println("Error writing input file " + fileName);
        }
    }

    public void saveWeightsFile(String fileName, ArrayList<Perceptron> perceptronList){
                                //int numOfPersons, int numOfEarsPerPerson){
        try{
            FileWriter fileWriter = new FileWriter(fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(101 + "\t\tDimension of input pattern (last row per set is bias weights)\n");
            bufferedWriter.write(perceptronList.get(0).getOutputSize() +
                                 "\t\tDimension of output pattern (# of persons)\n");
            bufferedWriter.write(perceptronList.get(0).getOutputSize()*perceptronList.get(0).getTrainingSize()
                                 + "\t\tNumber of ear images to per person\n");
            bufferedWriter.write(perceptronList.get(0).getTrainingSize() + ("\t\tTotal number of ears\n\n"));
            int count = 0;
            int index = 0;
            for(Perceptron perceptron : perceptronList) {
                for(int row = 0; row < perceptron.getInputSize(); row++){
                    for(int col = 0; col < perceptron.getOutputSize(); col++){
                        bufferedWriter.write(perceptron.getWeights(row, col) + " ");
                    }
                    bufferedWriter.write("\n");
                }
                bufferedWriter.write("\n");
                // write bias
                for(int i = 0; i < perceptron.getOutputSize(); i++){
                    bufferedWriter.write(perceptron.getBiasWeights(i) + " ");
                }
                bufferedWriter.write("\n");
                //write class name
                bufferedWriter.write(perceptron.getEarOwner()+ "\n\n");
            }
            bufferedWriter.close();
            fileWriter.close();
            System.out.println("-- Saved file " + fileName +"\n");
        }catch(Exception e){
            System.out.println("Error writing weights file " + fileName);
        }
    }

}
