/**
 * Biometric_EarRecognition
 * Author: Mark Gapasin
 * Class used for creating a multilayer perceptron
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Perceptron {

    ////////// fields //////////
    private double[][] weights;
    private int inputSize;
    private double[] inputList;
    private double[] outputList;
    private int outputSize;
    private int trainingSize;
    private String earOwner;
    private double[] biasWeights;
    private double learningrate;
    private ArrayList<ArrayList<Neuron>> hidden;
    private ArrayList<Neuron> input;
    private ArrayList<Neuron> output;
    private HashMap<Neuron,Integer> inputIndex, outputIndex;
    private HashMap<Integer,HashMap<Neuron,Integer>> hiddenIndex;


    ////////// constructors //////////
    public Perceptron(int inputLength, int outputLength){
        biasWeights = new double[outputLength];
        ArrayList<Double> rand = randInt(outputLength);
        // randomize bias weights
        for(int i = 0; i < outputLength; i++){
            biasWeights[i] = rand.get(i);
        }
        inputList = new double[inputLength];
        outputList = new double[outputLength];
        // randomize weights
        weights = new double[inputLength][outputLength];
        for(int row =0; row < inputLength; row++){
            rand = randInt(outputLength);
            for(int col = 0; col < outputLength; col++){
                weights[row][col] = rand.get(col);
            }
        }
    }

    public Perceptron(int input, int hiddenNeurons, int output, int totalHiddenLayers, double learningRate){
        this.input = new ArrayList<Neuron>();
        this.output = new ArrayList<Neuron>();
        this.learningrate = learningRate;
        hiddenIndex = new HashMap<Integer,HashMap<Neuron,Integer>>();
        inputIndex = new HashMap<Neuron,Integer>();
        outputIndex = new HashMap<Neuron,Integer>();
        hidden = new ArrayList<ArrayList<Neuron>>();
        //Input
        for(int i = 1; i <= input; i++){ this.input.add(new Neuron(false)); }
        for(Neuron i : this.input){ this.inputIndex.put(i, this.input.indexOf(i));}
        // Hidden Layer
        for(int i = 1; i <= totalHiddenLayers; i++){
            ArrayList<Neuron> a = new ArrayList<Neuron>();
            for(int j = 1; j <= hiddenNeurons; j++){
                a.add(new Neuron(true));
            }
            this.hidden.add(a);
        }
        for(ArrayList<Neuron> neuron : this.hidden){
            HashMap<Neuron,Integer> put = new HashMap<Neuron,Integer>();
            for(Neuron neuron2 : neuron){ put.put(neuron2, neuron.indexOf(neuron2)); }
            this.hiddenIndex.put(this.hidden.indexOf(neuron), put);
        }
        //Output Layer
        for(int i = 1; i <= output; i++){ this.output.add(new Neuron(true)); }
        for(Neuron neuronOut : this.output){
            this.outputIndex.put(neuronOut, this.output.indexOf(neuronOut));
        }

        for(Neuron neuron : this.input){
            for(Neuron hidden : this.hidden.get(0)){
                neuron.setLinks(hidden, Math.random() * (Math.random() > 0.5 ? 1 : -1));
            }
        }
        for(int i = 1; i < this.hidden.size(); i++){
            for(Neuron hidden : this.hidden.get(i-1)){
                for(Neuron hto : this.hidden.get(i)){
                    hidden.setLinks(hto, Math.random() *(Math.random() > 0.5 ? 1 : -1));
                }
            }
        }
        for(Neuron hidden :this.hidden.get(this.hidden.size()-1)){
            for(Neuron neuronOut : this.output){
                hidden.setLinks(neuronOut, Math.random() * (Math.random() > 0.5 ? 1 : -1));
            }
        }
    }
    public Perceptron(){}

    ////////// methods //////////
    /**
     * Sets input layer length
     * @param n length to set input to
     */
    public void setInputSize(int n) { inputSize = n; }

    /**
     * Gets input layer length
     * @return length of input layer
     */
    public int getInputSize() { return inputSize; }

    /**
     * Sets output layer size
     * @param n length to set output to
     */
    public void setOutputSize(int n) { outputSize = n; }

    /**
     * Gets output layer length
     * @return length of output
     */
    public int getOutputSize() { return outputSize; }

    /**
     * Sets total number of ears to train
     * @param n number of ears being trained
     */
    public void setTrainingSize(int n) { trainingSize = n; }

    /**
     * Gets the total ears trained
     * @return the total number of ears being trained
     */
    public int getTrainingSize() { return trainingSize; }

    /**
     * Sets input at a specified index
     * @param i index to set input
     * @param val value to set input
     */
    public void setInputs(int i, double val) { inputList[i] = val; }

    /**
     * Gets input at a specified index
     * @param in index to get input
     * @return
     */
    public double getInput(int in) {return inputList[in]; }

    /**
     * Gets all the inputs
     * @return an array of inputs values
     */
    public double[] getInput() { return inputList; }

    /**
     * Sets output at a specified index
     * @param i index to set output
     * @param val value to set output
     */
    public void setOutputs(int i, double val) { outputList[i] = val; }

    /**
     * Gets all the outputs
     * @return an array of output values
     */
    public double[] getOutput() { return outputList; }

    /**
     * Sets the name of the ear's owner
     * @param name the name of ear's owner
     */
    public void setEarOwner(String name) { earOwner = name; }

    /**
     * Gets the name of the ear's owner
     * @return name of the ear's owner
     */
    public String getEarOwner() { return earOwner; }

    /**
     * Gets a specified weight
     * @param row x position
     * @param col y position
     * @return the weights at that specified position
     */
    public double getWeights(int row, int col) { return weights[row][col]; }

    /**
     * Gets the bias weight
     * @param index the index of the bias weight
     * @return the bias weight
     */
    public double getBiasWeights(int index) { return biasWeights[index]; }

    /**
     * Get inputs to neurons
     * @return an array of neuron inputs
     */
    public ArrayList<Neuron> getInputNeurons() { return input; }

    /**
     * Trains a set of inputs using a multilayer perceptron with backpropagations
     * @param input the values into the input layer
     * @param target known output values
     */
    public void train(double[] input, double[] target){
        for(int i = 0; i < input.length; i++){
            this.input.get(i).input(input[i]);
        }
         backpropagate(target);
    }

    /**
     * A bipolar step function
     * @param val input values to calculate
     * @return
     */
    public int [] activationFunction(double[] val){
        int maxIndex = 0;
        double maxVal = 0.0;
        for(int i = 0; i < val.length; i++){
            if(val[i] > maxVal){
                maxIndex = i;
                maxVal = val[i];
            }
        }
        int[] y_out = new int[val.length];
        for(int j = 0 ; j < val.length; j++){
            if (j!=maxIndex) {
                y_out[j] = 0;
            }
            else y_out[j] = 1;
        }
        return  y_out;
    }

    /**
     * Softplus activation function
     * @param val input value to calculate
     * @return a value between 0 and 1
     */
    public static double activation(double val){
        return 1.0/(1+Math.pow(Math.E, -val));
    }

    /**
     * Returns a random numbers between -0.5 and 0.5, inclusive.
     * @param dimensionOutput of weights needed for every input
     * @return Integer between -0.5 and 0.5, inclusive
     */
    public static ArrayList<Double> randInt(int dimensionOutput) {
        Random r = new Random();
        Double randomNum;
        ArrayList<Double> randomList = new ArrayList<Double>();
        for(int i=0; i<dimensionOutput; i++) {
            randomNum = -0.5 + (0.5 - (-0.5)) * r.nextDouble();
            randomList.add(randomNum);
        }
        return randomList;
    }

    /**
     * The method calculates the gradient of a loss function with respect to all the weights in the network.
     * The gradient is fed to the optimization method which in turn uses it to update the weights,
     * in an attempt to minimize the loss function.
     * @param exp The expected value of the input
     */
    private void backpropagate(double[] exp){
        double[] error = new double[this.output.size()];
        // Hidden to Output
        int x = 0;
        for(Neuron out : this.output){
            error[x] = out.getNeuronOutput()*
                    (1.0-out.getNeuronOutput())*
                    (exp[this.outputIndex.get(out)]-out.getNeuronOutput());
            x++;
        }
        for(Neuron hidden : this.hidden.get(this.hidden.size()-1)){
            for(Edges s : hidden.getConnections()){
                double w = s.getWt();
                s.setWt(w + this.learningrate *
                        hidden.getNeuronOutput() *
                        error[this.outputIndex.get(s.getNeuron())]);
            }
        }
        double[] outError = error.clone();
        error = new double[this.hidden.get(0).size()];
        // Hidden to Hidden
        for(int i = this.hidden.size()-1; i > 0; i--){
            x = 0;
            for(Neuron hidden : this.hidden.get(i)){
                double pVal = hidden.getNeuronOutput()*(1-hidden.getNeuronOutput());
                double kVal = 0;
                for(Edges s : hidden.getConnections()){
                    if(i == this.hidden.size()-1){
                        kVal = kVal + outError[this.outputIndex.get(s.getNeuron())]*s.getWt();
                    }
                    else{
                        kVal = kVal + error[this.hiddenIndex.get(i+1).get(s.getNeuron())]*s.getWt();
                    }
                }
                error[x] = pVal * kVal;
                x++;
            }
            for(Neuron hidden : this.hidden.get(i-1)){
                for(Edges edge : hidden.getConnections()){
                    double w = edge.getWt();
                    int index = this.hiddenIndex.get(i).get(edge.getNeuron());
                    edge.setWt(w + this.learningrate * error[index] * hidden.getNeuronInput());
                }
            }
        }
        // Input to Hidden
        x = 0;
        double[] t = error.clone();
        for(Neuron hidden : this.hidden.get(0)){
            double p = hidden.getNeuronOutput()*(1.0-hidden.getNeuronOutput());
            double k = 0;
            for(Edges edge : hidden.getConnections()){
                if(this.hidden.size() == 1){
                    k = k+edge.getWt() * outError[this.outputIndex.get(edge.getNeuron())];
                }
                else{
                    k = k+edge.getWt()*error[this.hiddenIndex.get(1).get(edge.getNeuron())];
                }
            }
            t[x] = k*p;
            x++;
        }
        for(Neuron input : this.input){
            for(Edges edge : input.getConnections()){
                double w = edge.getWt();
                edge.setWt(w + this.learningrate *
                        t[this.hiddenIndex.get(0).get(edge.getNeuron())] * input.getNeuronInput());
            }
        }
    }

        /**
         * Tests an input values in relation to the stored values.
         * @param input Input to be classified
         * @return The classification of the input
         */
        public double[] classify(double[] input) {
            for(int i = 0; i < input.length; i++){
                this.input.get(i).input(input[i]);
            }
            double[] j = new double[this.output.size()];
            for(int i = 0; i < j.length; i++){
                j[i] = this.output.get(i).getNeuronOutput();
            }
            return j;
        }


}
