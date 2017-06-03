package NeuralNetwork2;

import java.util.*;
import java.io.*;

/**
 *
 * @author Philippe
 */
public class FFNetwork implements Comparable<FFNetwork> {

    public static ActivationFunction tanh = new ActivationFunction() {
        public double run(double in) {
            return Math.tanh(in);
        }
        public double getDerivative(double in) {
            return (1-Math.pow(run(in), 2));
        }
    };

    public static ActivationFunction sigmoid = new ActivationFunction() {
        public double run(double in) {
            return 1.0/(1.0+Math.exp(-in));
        }
        public double getDerivative(double in) {
            double val = run(in);
            return (val*(1-val));
        }
    };

    public static ActivationFunction linear = new ActivationFunction() {
        public double run(double in) {
            return in;
        }
        public double getDerivative(double in) {
            return 0;
        }
    };

    public static ActivationFunction step1 = new ActivationFunction() {
        public double run(double in) {
            if (in >= 0) {
                return 1;
            } else {
                return 0;
            }
        }
        public double getDerivative(double in) {
            return 0;
        }
    };

    public static ActivationFunction step2 = new ActivationFunction() {
        public double run(double in) {
            if (in >= 0) {
                return 1;
            } else {
                return -1;
            }
        }
        public double getDerivative(double in) {
            return 0;
        }
    };

    public static ActivationFunction sin = new ActivationFunction() {

        public double run(double input) {
            return Math.sin(input);
        }
        public double getDerivative(double in) {
            return 0;
        }

    };

    public static final String AF_TANH = "AF_TANH";
    public static final String AF_SIGMOID = "AF_SIGMOID";
    public static final String AF_LINEAR = "AF_LINEAR";

    public static String activationFunctionToString(ActivationFunction af) {
        if (af == tanh) {
            return AF_TANH;
        } else if (af == sigmoid) {
            return AF_SIGMOID;
        } else if (af == linear) {
            return AF_LINEAR;
        } else {
            return "AF_OTHER";
        }
    }

    public static ActivationFunction stringToActivationFunction(String str) {
        if (str.equalsIgnoreCase(AF_TANH)) {
            return tanh;
        } else if (str.equalsIgnoreCase(AF_SIGMOID)) {
            return sigmoid;
        } else if (str.equalsIgnoreCase(AF_LINEAR)) {
            return linear;
        } else {
            return null;
        }
    }

    public static FFNetwork createCopy(FFNetwork seedNetwork) {

        FFNetwork net = new FFNetwork();

        ArrayList<FFLayer> lyrs = seedNetwork.getLayers();

//        for (int k = 0; k < lyrs.size()-1; k++) {
//            net.addLayer(new FFLayer(lyrs.get(k).getNeurons().length, lyrs.get(k+1).getNeurons().length, lyrs.get(k).getActivationFunction()));
//        }
//        int length = lyrs.get(lyrs.size()-1).getNeurons().length;
//        net.addLayer(new FFLayer(length,
//                    length,
//                    lyrs.get(lyrs.size()-1).getActivationFunction()));
//
//        for (int lyrI = 0; lyrI < net.getLayers().size(); lyrI++) {
//            FFLayer lyr1, lyr2;
//            lyr1 = net.getLayers().get(lyrI);
//            lyr2 = lyrs.get(lyrI);
//            for (int i = 0; i < lyr1.getNeurons().length; i++) {
//                lyr1.getNeurons()[i].copy(lyr2.getNeurons()[i]);
//            }
//        }

        int[] hiddenLayers = null;
        if (lyrs.size()-2 > 0) {
            hiddenLayers = new int[lyrs.size()-2];
        }
        for (int k = 1; k < lyrs.size()-1; k++) {
            hiddenLayers[k-1] = lyrs.get(k).getNeurons().length;
        }

        net = FFNetwork.create(lyrs.get(0).getActivationFunction(),
                                         lyrs.get(lyrs.size()-1).getActivationFunction(),
                                         lyrs.get(0).getNeurons().length,
                                         lyrs.get(lyrs.size()-1).getNeurons().length,
                                         hiddenLayers);

        for (int lyrI = 0; lyrI < lyrs.size(); lyrI++) {
            Neuron[] nrns1 = net.getLayers().get(lyrI).getNeurons();
            Neuron[] nrns2 = lyrs.get(lyrI).getNeurons();
            for (int n = 0; n < nrns1.length; n++) {
                nrns1[n].copy(nrns2[n]);
            }
            net.getLayers().get(lyrI).getBias().copy(lyrs.get(lyrI).getBias());
        }

        return net;

    }

    public static FFNetwork create(ActivationFunction func, ActivationFunction outputFunc, int inputCount, int outputCount, int... hiddenCount) {

        FFNetwork net = new FFNetwork();

        int layerCount;
        if (hiddenCount == null) {
            layerCount = 0;
        } else {
            layerCount = hiddenCount.length + 2;
        }
        if (hiddenCount != null && hiddenCount.length > 0) {
            net.addLayer(new FFLayer(inputCount, hiddenCount[0], func));
        } else {
            net.addLayer(new FFLayer(inputCount, outputCount, func));
        }
        for (int k = 1; k < layerCount-1; k++) {
            if (k == layerCount-2) {
                net.addLayer(new FFLayer(hiddenCount[k-1], outputCount, func));
            } else {
                net.addLayer(new FFLayer(hiddenCount[k-1], hiddenCount[k-1+1], func));
            }
        }
        net.addLayer(new FFLayer(outputCount, /*outputCount*/0, outputFunc));

        for (int k = 0; k < net.getLayers().size(); k++) {
            randomizeLayer(net.getLayers().get(k));
        }

        return net;

    }

    private static void randomizeLayer(FFLayer lyr) {
        Neuron[] nrns = lyr.getNeurons();
        for (int i = 0; i < nrns.length; i++) {
            for (int k = 0; k < nrns[i].weights.length; k++) {
                nrns[i].weights[k] = 10-Math.random()*20;
            }
        }
        Neuron bias = lyr.getBias();
        for (int k = 0; k < bias.weights.length; k++) {
            bias.weights[k] = 1-Math.random()*2;
        }

    }

    public static void saveToFile(String filename, FFNetwork net) {
        
        PrintWriter pw;
        
        try {
            
            pw = new PrintWriter(filename);
            printToFile(pw, net);

        } catch (FileNotFoundException e) {
            System.out.println("Error: file " + filename + " not found");
            return;
        }

    }
    
    private static void printToFile(PrintWriter out, FFNetwork net) {

//        for (FFLayer f : net.layers) {
//            // print layer neuron count
//            int size = f.getNeurons().length + 1;
//            out.println(size);
//            // print neuron weights
//            for (int i = 0; i < f.getNeurons().length; i++) {
//                String weights = "";
//                for (int k = 0; k < f.getNeurons()[i].weights.length; k++) {
//                    weights += f.getNeurons()[i].weights[k] + ",";
//                }
//                out.println(weights);
//            }
//            // print bias weights
//            String weights = "";
//            for (int k = 0; k < 1; k++) {
//                weights += f.getBias().weights[k];
//            }
//            out.println(weights);
//
//        }

        String lyrOutput = "";
        for (FFLayer f : net.layers) {
            lyrOutput += f.getNeurons().length + ",";
            lyrOutput += activationFunctionToString(f.getActivationFunction());
            if (f != net.layers.get(net.layers.size()-1)) {
                lyrOutput += ",";
            }
        }

        out.println(lyrOutput);
        
        for (FFLayer f : net.layers) {
            for (Neuron n : f.getNeurons()) {
                String output = "";
                for (double w : n.weights) {
                    output += w + ",";
                }
                out.println(output);
            }
            String output = "";
            for (double w : f.getBias().weights) {
                output += w + ",";
            }
            out.println(output);
        }

        out.flush();
        
    }

    public static FFNetwork loadFromFile(String filename) {

        Scanner s;
        FFNetwork net = new FFNetwork();

        try {

            s = new Scanner(new File(filename));

            String line;
    //        do {
                // number of neurons in each layer and the activation function for each layer
                line = s.nextLine();
                String[] lyrs = line.split(",");
                for (int i = 0; i < lyrs.length; i += 2) {
                    FFLayer lyr;
                    if (i != lyrs.length-2) {
                        lyr = new FFLayer(Integer.parseInt(lyrs[i]), Integer.parseInt(lyrs[i+2]), stringToActivationFunction(lyrs[i+1]));
                    } else {
                        //lyr = new FFLayer(Integer.parseInt(lyrs[i]), Integer.parseInt(lyrs[i]), stringToActivationFunction(lyrs[i+1]));
                        lyr = new FFLayer(Integer.parseInt(lyrs[i]), 0, stringToActivationFunction(lyrs[i+1]));
                    }
                    net.addLayer(lyr);
                    for (int k = 0; k < lyr.getNeurons().length; k++) {
                        line = s.nextLine();
                        String[] weights = line.split(",");
                        for (int j = 0; j < lyr.getNeurons()[k].weights.length; j++) {
                            lyr.getNeurons()[k].weights[j] = Double.parseDouble(weights[j]);
                        }
                    }
                    line = s.nextLine();
                    String[] weights = line.split(",");
                    for (int j = 0; j < lyr.getBias().weights.length; j++) {
                        lyr.getBias().weights[j] = Double.parseDouble(weights[j]);
                    }

                }

    //        } while (s.hasNext());

            return net;

        } catch (FileNotFoundException ex) {
            System.out.println("Error: " + ex.getMessage());
            return null;
        }

    }

    private ArrayList<FFLayer> layers = new ArrayList<FFLayer>();
    public double fitness;

//    private Random rand = new Random();

    public FFNetwork() {

    }

    public void addLayer(FFLayer layer) {
        layers.add(layer);
    }

    public ArrayList<FFLayer> getLayers() {
        return layers;
    }

    public double[] fire(double[] inputs) {

        if (inputs.length != layers.get(0).getNeurons().length) {
            System.out.println("Error in FFNetwork/run(double[]): there should be the same amount of inputs as neurons in the input layer");
        }

        double[] out = new double[layers.get(layers.size()-1).getNeurons().length];

        Neuron[] inNrns = layers.get(0).getNeurons();
        for (int k = 0; k < inputs.length; k++) {
            inNrns[k].output = inputs[k];
            inNrns[k].sum = inputs[k];
        }

        for (int i = 1; i < layers.size(); i++) {
            for (int k = 0; k < layers.get(i).getNeurons().length; k++) {
                Neuron n = layers.get(i).getNeurons()[k];
                n.output = 0;
            }
        }

        for (int i = 1; i < layers.size(); i++) { // for each layer

            Neuron[] nrns = layers.get(i).getNeurons();

            for (int k = 0; k < nrns.length; k++) { // get the values for the next layer
                 double sum = layers.get(i-1).getSum(k); // note: this gets the previous layer
                 ActivationFunction func = layers.get(i-1).getActivationFunction();
//                 if (func.run(sum) >= nrns[k].threshold) {
//                    nrns[k].sum = sum;
                    nrns[k].sum = sum;
                    nrns[k].output = func.run(sum);
//                    nrns[k].sum = (1.0/(1.0+Math.exp(-sum)));
//                    nrns[k].sum = Math.tanh(sum);
//                    nrns[k].sum = 3.6*sum*sum*sum + 9.2*sum*sum-6.8*sum+4.1;
//                    nrns[k].sum = sum*func.run(sum);
//                    nrns[k].sum = Math.tanh(sum) * Math.pow(func.run(sum), 2)*sum*sum;
//                    nrns[k].sum = Math.sin(sum) + Math.sin(1.1*sum);
//                    nrns[k].sum = -1.1*sum*sum*sum + 2.2*sum*sum + 4.8*sum + -9.9;
//                    nrns[k].sum = 1;
//                    nrns[k].sum = sum*sum*sum*sum*sum*sum*sum*sum*sum;
//                    nrns[k].sum = Math.random()*sum;
//                 }
//                 } else {
//                    nrns[k].sum = sum*func.run(sum);
//                     nrns[k].sum = 0;
//                 }
//                 double value = Math.tanh(sum);
//                 double value = func.run(sum);
//                 double diff = Math.abs(value-nrns[k].threshold);
//                 nrns[k].sum = sum*diff;
//                 rand.setSeed((int)sum);
//                 nrns[k].sum = Math.pow(rand.nextInt() * rand.nextDouble(), 3);
            }

        }

        Neuron[] outNrns = layers.get(layers.size()-1).getNeurons();
        for (int i = 0; i < out.length; i++) {
            //out[i] = layers.get(layers.size()-1).getActivationFunction().run(outNrns[i].output*outNrns[i].weights[i]);
            out[i] = layers.get(layers.size()-1).getActivationFunction().run(outNrns[i].sum);
        }

        return out;

    }

    public int compareTo(FFNetwork o) {
        return (int)(1000000d*(-o.fitness+fitness));
//        return (int)(1000000d*(o.fitness-fitness));
    }
    
}