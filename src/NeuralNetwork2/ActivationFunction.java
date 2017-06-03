package NeuralNetwork2;

/**
 *
 * @author Philippe
 */
public interface ActivationFunction {

    public double run(double input);

    public double getDerivative(double input);

}
