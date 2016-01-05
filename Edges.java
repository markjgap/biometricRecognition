/**
 * Biometric_EarRecognition
 * Author: Mark Gapasin
 * Class used to connect neurons to each other
 */

public class Edges {

    ////////// fields //////////
    private Neuron neuron;
    private double wt;

    ////////// constructor //////////
    public Edges(Neuron neuron, double wt){
        this.neuron = neuron;
        this.wt = wt;
    }

    ////////// methods //////////

    /**
     * Gets neuron object
     * @return neuron object
     */
    public Neuron getNeuron(){
        return this.neuron;
    }

    /**
     * Gets neuron weights
     * @return a double value which represents weight
     */
    public double getWt(){
        return this.wt;
    }

    /**
     * Sets neuron weights
     * @param w weight value to set
     */
    public void setWt(double w){
        this.wt = w;
    }

}
