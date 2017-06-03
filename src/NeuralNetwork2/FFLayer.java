package NeuralNetwork2;

/**
 *
 * @author Philippe
 */
public class FFLayer {

    private ActivationFunction func;
    private Neuron[] neurons;
    private Neuron bias;

    public FFLayer(int neuronCount, int nextLayerNeuronCount, ActivationFunction func) {
        
        this.func = func;

        neurons = new Neuron[neuronCount];
        for (int i = 0; i < neuronCount; i++) {
            neurons[i] = new Neuron(nextLayerNeuronCount);
        }

        bias = new Neuron(nextLayerNeuronCount);
        bias.output = 1;
        bias.sum = 1;

    }

    public ActivationFunction getActivationFunction() {
        return func;
    }

    public double getSum(int forNeuron) {

        double sum = 0;

        for (int i = 0; i < neurons.length; i++) {
            //if (neurons[i].sum >= neurons[i].threshold) {
                sum += (neurons[i].output*neurons[i].weights[forNeuron]);
            //}
        }

//        sum += bias.output * bias.weights[forNeuron];

//        return func.run(sum);
        return sum;

    }

    public Neuron[] getNeurons() {
        return neurons;
    }

    public Neuron getBias() {
        return bias;
    }

}
