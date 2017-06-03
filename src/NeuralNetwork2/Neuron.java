package NeuralNetwork2;

import java.util.ArrayList;

/**
 *
 * @author Philippe
 */
public class Neuron {

    public double threshold = 0;
    public double output = 0;
    public double[] weights = null;

    // for backpropagation
    public double delta = 0;
    public double sum = 0;
    public double[] gradients = null;
    public double[] changeInWeight = null;

    public Neuron() {}

    public Neuron(int weightCount) {
        weights = new double[weightCount];
        gradients = new double[weightCount];
        changeInWeight = new double[weightCount];
    }

    public void copy(Neuron n2) {
        if (weights.length != n2.weights.length) {
            System.out.println("Error in Neuron/copy(Neuron): neurons must have the same number of weights");
            return;
        }
        threshold = n2.threshold;
        //sum = n2.sum;
        for (int k = 0; k < weights.length; k++) {
            weights[k] = n2.weights[k];
        }
    }

}
