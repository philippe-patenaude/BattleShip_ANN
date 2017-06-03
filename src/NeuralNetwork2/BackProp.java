package NeuralNetwork2;

import NeuralNetwork2.*;
import java.util.*;

/**
 *
 * @author Philippe
 */
public class BackProp {

    public static FFNetwork backProp(RaterBP rater, OnGenerationTick2 ticker, ActivationFunction func, ActivationFunction outputFunc, double acceptableErrorValue, int numberOfIterations, double learningRate, double momentum, int inputCount, int outputCount, int... hiddenCount) {

        FFNetwork net = FFNetwork.create(func, outputFunc, inputCount, outputCount, hiddenCount);

        net = doLoop(rater, ticker, acceptableErrorValue, numberOfIterations, learningRate, momentum, net);

        return net;
    }

    public static FFNetwork backProp(RaterBP rater, OnGenerationTick2 ticker, double acceptableErrorValue, int numberOfIterations, double learningRate, double momentum, FFNetwork seedNetwork) {
        
//        FFNetwork net = new FFNetwork();
//
//        for (int k = 0; k < seedNetwork.getLayers().size()-1; k++) {
//            net.addLayer(new FFLayer(seedNetwork.getLayers().get(k).getNeurons().length, seedNetwork.getLayers().get(k+1).getNeurons().length, seedNetwork.getLayers().get(k).getActivationFunction()));
//        }
//        int length = seedNetwork.getLayers().get(seedNetwork.getLayers().size()-1).getNeurons().length;
//        net.addLayer(new FFLayer(length,
//                    length,
//                    seedNetwork.getLayers().get(seedNetwork.getLayers().size()-1).getActivationFunction()));
//        for (int lyrI = 0; lyrI < net.getLayers().size(); lyrI++) {
//            FFLayer lyr1, lyr2;
//            lyr1 = net.getLayers().get(lyrI);
//            lyr2 = seedNetwork.getLayers().get(lyrI);
//            for (int i = 0; i < lyr1.getNeurons().length; i++) {
//                lyr1.getNeurons()[i].copy(lyr2.getNeurons()[i]);
//            }
//        }

        FFNetwork net = FFNetwork.createCopy(seedNetwork);

        net = doLoop(rater, ticker, acceptableErrorValue, numberOfIterations, learningRate, momentum, net);

        return net;

    }

    private static FFNetwork doLoop(RaterBP rater, OnGenerationTick2 ticker, double acceptableErrorValue, int numberOfIterations, double learningRate, double momentum, FFNetwork net) {

        double error;
        int iteration = 0;
        do {
            // do backpropagation here...

            // use rater to get error values
            double[] errors = rater.rate(net);
            if (errors.length != net.getLayers().get(net.getLayers().size()-1).getNeurons().length) {
                System.out.println("Error: the size of the error array is not the same size as the output layer of the neuron being rated.");
                return null;
            }

            // calculate ending conditions
            error = 0;
            for (int i = 0; i < errors.length; i++) {
                error += errors[i]*errors[i];
            }
            error /= errors.length;

            net.fitness = error;

            ticker.tick(net, iteration);

            // go one layer at a time from back to front

            ArrayList<FFLayer> lyrs = net.getLayers();

            // on the output layer calculate: delta = -error*derivative(sum)
            // store the deltas
            FFLayer outputLayer = lyrs.get(lyrs.size()-1);
            Neuron[] nrns = outputLayer.getNeurons();
            for (int i = 0; i < nrns.length; i++) {
                nrns[i].delta = -errors[i]*outputLayer.getActivationFunction().getDerivative(nrns[i].sum);
            }

            // next for each hidden layer(not including biases or inputs) calculate the deltas: delta = derivative(sum)*(weight1*delta1+weight2*delta2...weightN*deltaN)
            // calculate the gradient: gradient = output*delta
            // calculate the change in weights
            FFLayer hiddenLayer;
            for (int lyrI = lyrs.size()-2; lyrI >= 0; lyrI--) {
                hiddenLayer = lyrs.get(lyrI);
                nrns = hiddenLayer.getNeurons();
                for (int i = 0; i < nrns.length; i++) {
                    Neuron n = nrns[i];
                    double val;
                    val = outputLayer.getActivationFunction().getDerivative(n.sum);
                    double sum = 0;
                    for (int wi = 0; wi < n.weights.length; wi++) {
                        double delta = lyrs.get(lyrI+1).getNeurons()[wi].delta;
                        sum += n.weights[wi]*delta;
//                        n.gradients[wi] = n.output*delta;
//                        n.changeInWeight[wi] = -learningRate*n.gradients[wi]+momentum*n.changeInWeight[wi];
//                        n.weights[wi] += n.changeInWeight[wi];
                    }
                    n.delta = sum*val;
                }
            }
            // update weights
            for (int lyrI = lyrs.size()-2; lyrI >= 0; lyrI--) {
                hiddenLayer = lyrs.get(lyrI);
                nrns = hiddenLayer.getNeurons();
                for (int i = 0; i < nrns.length; i++) {
                    Neuron n = nrns[i];
                    for (int wi = 0; wi < n.weights.length; wi++) {
                        double delta = lyrs.get(lyrI+1).getNeurons()[wi].delta;
                        n.gradients[wi] = n.output*delta;
                        n.changeInWeight[wi] = -learningRate*n.gradients[wi]+momentum*n.changeInWeight[wi];
                        n.weights[wi] += n.changeInWeight[wi];
                    }
                }
            }

            iteration++;

        } while (error > acceptableErrorValue && iteration < numberOfIterations);

        return net;

    }

}
