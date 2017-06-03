package NeuralNetwork2;

import java.util.Arrays;
import java.util.Collections;
import java.util.ArrayList;

/**
 *
 * @author Philippe
 */
public class Evolve2 {

    private static FFNetwork[] population;
    private static long count;

    public static FFNetwork evolve(int popSize, Rater2 rater, OnGenerationTick2 ticker, ActivationFunction func, ActivationFunction outputFunc, int loopCount, double percentFitness, int inputCount, int outputCount, int... hiddenCount) {

        // initialize
        
        population = new FFNetwork[popSize];

        for (int i = 0; i < popSize; i++) {
            population[i] = new FFNetwork();

            int layerCount = hiddenCount.length + 2;
            if (hiddenCount.length > 0) {
                population[i].addLayer(new FFLayer(inputCount, hiddenCount[0], func));
            } else {
                population[i].addLayer(new FFLayer(inputCount, outputCount, func));
            }
            for (int k = 1; k < layerCount-1; k++) {
                if (k == layerCount-2) {
                    population[i].addLayer(new FFLayer(hiddenCount[k-1], outputCount, func));
                } else {
                    population[i].addLayer(new FFLayer(hiddenCount[k-1], hiddenCount[k-1+1], func));
                }
            }
            population[i].addLayer(new FFLayer(outputCount, outputCount, outputFunc));

            for (int k = 0; k < population[i].getLayers().size(); k++) {
                randomizeLayer(population[i].getLayers().get(k));
            }

        }

        return doLoop(popSize, rater, ticker, loopCount, percentFitness);

    }

    public static FFNetwork evolve(int popSize, Rater2 rater, OnGenerationTick2 ticker, int loopCount, double percentFitness, FFNetwork seedNetwork) {

//        population = new FFNetwork[popSize];
//
//        for (int i = 0; i < popSize; i++) {
//
//            ArrayList<FFLayer> lyrs = seedNetwork.getLayers();
//
//            int[] hiddenLayers = null;
//            if (lyrs.size()-2 > 0) {
//                hiddenLayers = new int[lyrs.size()-2];
//            }
//            for (int k = 1; k < lyrs.size()-1; k++) {
//                hiddenLayers[k-1] = lyrs.get(k).getNeurons().length;
//            }
//
//            population[i] = FFNetwork.create(lyrs.get(0).getActivationFunction(),
//                                             lyrs.get(lyrs.size()-1).getActivationFunction(),
//                                             lyrs.get(0).getNeurons().length,
//                                             lyrs.get(lyrs.size()-1).getNeurons().length,
//                                             hiddenLayers);
////            population[i] = new FFNetwork();
////            for (int k = 0; k < seedNetwork.getLayers().size()-1; k++) {
////                population[i].addLayer(new FFLayer(seedNetwork.getLayers().get(k).getNeurons().length, seedNetwork.getLayers().get(k+1).getNeurons().length, seedNetwork.getLayers().get(k).getActivationFunction()));
////            }
////            int length = seedNetwork.getLayers().get(seedNetwork.getLayers().size()-1).getNeurons().length;
////            population[i].addLayer(new FFLayer(length,
////                    length,
////                    seedNetwork.getLayers().get(seedNetwork.getLayers().size()-1).getActivationFunction()));
//            if (i == 0) {
//                intertwine(population[i], seedNetwork, seedNetwork);
//            } else {
//                if (Math.random() < 0.5) {
//                    copyWithRandomChange(population[i], seedNetwork);
//                } else {
//                    copyWithRandomChange2(population[i], seedNetwork);
//                }
//            }
//
//        }

        
        population = new FFNetwork[popSize];
        
        population[0] = FFNetwork.createCopy(seedNetwork);

        for (int i = 1; i < popSize; i++) {
            ArrayList<FFLayer> lyrs = seedNetwork.getLayers();

            int[] hiddenLayers = null;
            if (lyrs.size()-2 > 0) {
                hiddenLayers = new int[lyrs.size()-2];
            }
            for (int k = 1; k < lyrs.size()-1; k++) {
                hiddenLayers[k-1] = lyrs.get(k).getNeurons().length;
            }
            population[i] = FFNetwork.create(lyrs.get(0).getActivationFunction(),
                                             lyrs.get(lyrs.size()-1).getActivationFunction(),
                                             lyrs.get(0).getNeurons().length,
                                             lyrs.get(lyrs.size()-1).getNeurons().length,
                                             hiddenLayers);
        }

        return doLoop(popSize, rater, ticker, loopCount, percentFitness);

    }
    
    private static FFNetwork doLoop(int popSize, Rater2 rater, OnGenerationTick2 ticker, int loopCount, double percentFitness) {
        
        // loop
        
        count = 0;

        do {
            
            for (int i = 0; i < popSize; i++) {
                population[i].fitness = rater.rate(population[i]);
            }

            //Collections.shuffle(Arrays.asList(population));
            Arrays.sort(population);

            ticker.tick(population[0], count);

//            double selectionFraction = 9.0/10.0;
//            double selectionFraction = 5.0/10.0;
            double selectionFraction = 1.0/10.0;
            selectionFraction = Math.max(selectionFraction, 1.0/(double)population.length);
            int finalPopSize = (int)(popSize*selectionFraction);
            for (int i = finalPopSize; i < popSize; i++) {
                double randMutation = Math.random();
                if (randMutation < 0.3) {
                    int rand1, rand2;
                    rand1 = (int)(Math.random()*finalPopSize);
                    rand2 = (int)(Math.random()*finalPopSize);
                    intertwine(population[i], population[rand1], population[rand2]);
                } else if (randMutation < 0.6) {
                    int rand;
                    rand = (int)(Math.random()*finalPopSize);
                    copyWithRandomChangeMulti(population[i], population[rand], 1000, (int)(Math.random()*10));
//                    copyWithRandomChange2(population[i], population[rand]);
                } else {
                    int rand;
                    rand = (int)(Math.random()*finalPopSize);
                    copyWithRandomChangeMulti(population[i], population[rand], 2, (int)(Math.random()*10));
//                    copyWithRandomChange(population[i], population[rand]);
                }
            }

            count++;

        } while (population[0].fitness > percentFitness && count < loopCount);

        return population[0];
        
    }

    public static long getCount() {
        return count;
    }

    public static FFNetwork[] getPopulation() {
        return population;
    }

    private static void intertwine(FFNetwork net0, FFNetwork net1, FFNetwork net2) {

        for (int i = 0; i < net0.getLayers().size(); i++) {
            intertwineUtility(net0.getLayers().get(i), net1.getLayers().get(i), net2.getLayers().get(i));
        }

    }

    private static void intertwineUtility(FFLayer layer1, FFLayer layer2, FFLayer layer3) {

        for (int i = 0; i < layer1.getNeurons().length + 1; i++) {
            Neuron n, n1, n2;
            if (i == layer1.getNeurons().length) { // for the bias
                n = layer1.getBias();
                n1 = layer2.getBias();
                n2 = layer3.getBias();
            } else {
                n = layer1.getNeurons()[i];
                n1 = layer2.getNeurons()[i];
                n2 = layer3.getNeurons()[i];
            }
            for (int k = 0; k < n.weights.length; k++) {
                if (Math.random() < 0.5) {
                    n.weights[k] = n1.weights[k];
                } else {
                    n.weights[k] = n2.weights[k];
                }
            }
//            if (Math.random() < 0.5) {
//                n.threshold = n1.threshold;
//            } else {
//                n.threshold = n2.threshold;
//            }
        }

    }

    private static void copyWithRandomChange(FFNetwork net1, FFNetwork net2) {

        for (int i = 0; i < net1.getLayers().size(); i++) {

            copyNeuronList(net1.getLayers().get(i).getNeurons(), net2.getLayers().get(i).getNeurons());

            Neuron bias1 = net1.getLayers().get(i).getBias();
            Neuron bias2 = net2.getLayers().get(i).getBias();
            bias1.copy(bias2);

        }

        // make a random change
        int randLyr = (int)(Math.random()*net1.getLayers().size());

            Neuron[] nrns = net1.getLayers().get(randLyr).getNeurons();

            // choose neuron to change
            int randNrn = (int)(Math.floor(Math.random()*(nrns.length + 1)));
            Neuron nrn;
            if (randNrn == nrns.length) { // select the bias
                nrn = net1.getLayers().get(randLyr).getBias();
            } else {
                nrn = nrns[randNrn];
            }

//            double rand2 = Math.random();
//            if (rand2 < 0.2 && randNrn != nrns.length) { // change threshold but not for the bias
////                nrn.threshold += (0.5 - Math.random());
//                nrn.threshold = (Math.random()*1);
//            } else { // change an weight
                // choose weight to change
//                if (nrn.weights.length > 0) {
//                    int randWgt = (int)(Math.floor(Math.random()*nrn.weights.length));
//                    nrn.weights[randWgt] = nrn.weights[randWgt] + (0.5 - Math.random());
//                }
                if (nrn.weights.length > 0) {
                    int randWgt = (int)(Math.floor(Math.random()*nrn.weights.length));
                    nrn.weights[randWgt] = nrn.weights[randWgt] + (1 - 2*Math.random());
                }
//                if (nrn.weights.length > 0) {
//                    int randWgt = (int)(Math.floor(Math.random()*nrn.weights.length));
//                    nrn.weights[randWgt] = nrn.weights[randWgt] * (10.0 - Math.random()*20.0);
//                }
//            }

    }

    private static void copyWithRandomChange2(FFNetwork net1, FFNetwork net2) {

        for (int i = 0; i < net1.getLayers().size(); i++) {

            copyNeuronList(net1.getLayers().get(i).getNeurons(), net2.getLayers().get(i).getNeurons());

            Neuron bias1 = net1.getLayers().get(i).getBias();
            Neuron bias2 = net2.getLayers().get(i).getBias();
            bias1.copy(bias2);

        }

        // make a random change
        int randLyr = (int)(Math.random()*net1.getLayers().size());

            Neuron[] nrns = net1.getLayers().get(randLyr).getNeurons();

            // choose neuron to change
            int randNrn = (int)(Math.floor(Math.random()*(nrns.length + 1)));
            Neuron nrn;
            if (randNrn == nrns.length) { // select the bias
                nrn = net1.getLayers().get(randLyr).getBias();
            } else {
                nrn = nrns[randNrn];
            }

            // choose weight to change
//            if (nrn.weights.length > 0) {
//                int randWgt = (int)(Math.floor(Math.random()*nrn.weights.length));
//                nrn.weights[randWgt] = nrn.weights[randWgt] + (50 - 100*Math.random());
//            }
            if (nrn.weights.length > 0) {
                int randWgt = (int)(Math.floor(Math.random()*nrn.weights.length));
                nrn.weights[randWgt] = nrn.weights[randWgt] * (100.0 - 200.0*Math.random());
            }

    }

    public static void copyWithRandomChangeMulti(FFNetwork net1, FFNetwork net2, double range, int numberOfChanges) {

        for (int i = 0; i < net1.getLayers().size(); i++) {

            copyNeuronList(net1.getLayers().get(i).getNeurons(), net2.getLayers().get(i).getNeurons());

            Neuron bias1 = net1.getLayers().get(i).getBias();
            Neuron bias2 = net2.getLayers().get(i).getBias();
            bias1.copy(bias2);

        }

        // make a random change
        for (int i = 0; i < numberOfChanges; i++) {
            int randLyr = (int)(Math.random()*net1.getLayers().size());

            Neuron[] nrns = net1.getLayers().get(randLyr).getNeurons();

            // choose neuron to change
            int randNrn = (int)(Math.floor(Math.random()*(nrns.length + 1)));
            Neuron nrn;
            if (randNrn == nrns.length) { // select the bias
                nrn = net1.getLayers().get(randLyr).getBias();
            } else {
                nrn = nrns[randNrn];
            }

            if (nrn.weights.length > 0) {
                int randWgt = (int)(Math.floor(Math.random()*nrn.weights.length));
                nrn.weights[randWgt] = nrn.weights[randWgt] * (range - 2.0*range*Math.random());
            }
        }

    }

    private static void copyWithRandomChangeAdd(FFNetwork net1, FFNetwork net2, double range, int numberOfChanges) {

        for (int i = 0; i < net1.getLayers().size(); i++) {

            copyNeuronList(net1.getLayers().get(i).getNeurons(), net2.getLayers().get(i).getNeurons());

            Neuron bias1 = net1.getLayers().get(i).getBias();
            Neuron bias2 = net2.getLayers().get(i).getBias();
            bias1.copy(bias2);

        }

        // make a random change
        for (int i = 0; i < numberOfChanges; i++) {
            int randLyr = (int)(Math.random()*net1.getLayers().size());

            Neuron[] nrns = net1.getLayers().get(randLyr).getNeurons();

            // choose neuron to change
            int randNrn = (int)(Math.floor(Math.random()*(nrns.length + 1)));
            Neuron nrn;
            if (randNrn == nrns.length) { // select the bias
                nrn = net1.getLayers().get(randLyr).getBias();
            } else {
                nrn = nrns[randNrn];
            }

            if (nrn.weights.length > 0) {
                int randWgt = (int)(Math.floor(Math.random()*nrn.weights.length));
                nrn.weights[randWgt] = nrn.weights[randWgt] + (range - 2.0*range*Math.random());
            }
        }

    }

    private static void copyNeuronList(Neuron[] list, Neuron[] list2) {
        for (int i = 0; i < list.length; i++) {
            list[i].copy(list2[i]);
        }
    }

    private static void randomizeLayer(FFLayer lyr) {
        Neuron[] nrns = lyr.getNeurons();
        double range1 = 10;
        double range2 = 1;
        for (int i = 0; i < nrns.length; i++) {
//            nrns[i].threshold = Math.random()*1;
            for (int k = 0; k < nrns[i].weights.length; k++) {
                nrns[i].weights[k] = range1-Math.random()*2*range1;
            }
        }
        Neuron bias = lyr.getBias();
//        bias.threshold = Math.random();
        for (int k = 0; k < bias.weights.length; k++) {
            bias.weights[k] = range2-Math.random()*2*range2;
        }

    }

}
