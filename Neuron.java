/**
 * Biometric_EarRecognition
 * Author: Mark Gapasin
 * Class defines a neuron for the neural network
 */

import java.util.ArrayList;

public class Neuron {

    ////////// fields //////////
    private boolean act;
    private int triggered = 0;
    private int link = 0;
    private double nueronIn = 0;
    private double neuronOut = 0;
    private double sum;
    private ArrayList<Edges> connections;

    ////////// constructor //////////
    public Neuron(boolean act){
        this.act = act;
        this.connections = new ArrayList<Edges>();
    }

    ////////// methods //////////

    /**
     * Sets links of neuron
     * @param e Neuron of interest
     * @param wt Weight of edge
     */
    public void setLinks(Neuron e, double wt){
        Edges n = new Edges(e, wt);
        this.connections.add(n);
        e.addLinks();
    }

    /**
     * Adds a link to the neuron
     */
    public void addLinks() { this.link++; }

    /**
     * Get neuron input
     * @return Input value
     */
    public double getNeuronInput(){
        return this.nueronIn;
    }

    /**
     * Get neuron output
     * @return Output value
     */
    public double getNeuronOutput(){
        return this.neuronOut;
    }

    /**
     * Gets the edges
     * @return List of Edges that are connected to neuron
     */
    public ArrayList<Edges> getConnections(){ return this.connections; }

    /**
     * Checks each edges that is connected to neuron
     */
    public void test(){
        for(Edges n : this.connections){
            if(this.act){
                n.getNeuron().input(Perceptron.activation(this.sum) * n.getWt());
            }
            else{
                n.getNeuron().input(this.sum * n.getWt());
            }
        }
        if(this.act){
            this.neuronOut = Perceptron.activation(this.sum);
        }
        else{
            this.neuronOut = this.sum;
        }
        this.sum = 0.0;
        this.triggered = 0;
    }

    /**
     * Input to neuron
     * @param input value to input
     */
    public void input(double input){
        this.triggered++;
        this.sum = sum + input;
        if(this.triggered >= this.link){
            this.nueronIn = sum;
            test();
        }
    }
}