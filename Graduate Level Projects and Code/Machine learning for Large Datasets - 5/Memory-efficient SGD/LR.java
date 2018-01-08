package LR;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

public class LR {

    private static BufferedReader br;
    private static PrintWriter pr;

    private static int K;

    private static int vocab_size;
    private static double learning_rate;
    private static double regularization;
    private static int max_iterations;
    private static int training_size;
    private static String file_path;

    private static int overflow = 20;

    private static Hashtable<String, int[]> A;
    private static Hashtable<String, double[]> B;

    public static void main(String[] args) throws FileNotFoundException {

        vocab_size = Integer.parseInt(args[0]);
        learning_rate = Double.parseDouble(args[1]);
        regularization = Double.parseDouble(args[2]);
        max_iterations = Integer.parseInt(args[3]);
        training_size = Integer.parseInt(args[4]);
        file_path = args[5];

        br = new BufferedReader(new InputStreamReader(System.in));
        pr = new PrintWriter(System.out);

        initialization();
        K = 0;

        br.lines().forEach(
                line -> {

                    K += 1;

                    String[] parts = line.split("\t");
                    String[] labels = parts[0].split(",");
                    Vector<String> attributes = tokenizeDoc(parts[1]);

                    HashSet<String> label_set = new HashSet<>();
                    label_set.addAll(Arrays.asList(labels));

                    int[] indice = new int[attributes.size()];
                    int i = 0;
                    for (String attribute : attributes) {
                        indice[i] = hashTrick(attribute);
                        i += 1;
                    }

                    int y;
                    for (String key : B.keySet()) {

                        int[] Akey = A.get(key);
                        double[] Bkey = B.get(key);

                        double p = 0;
                        for (int index : indice) {
                            p += Bkey[index];
                        }
                        p = sigmoid(p);

                        y = label_set.contains(key) ? 1 : 0;

                        for (int j = 0; j < attributes.size(); j++) {
                            int index = indice[j];
                            Bkey[index] = simulate_regularization(Bkey[index], Akey[index]);
                            Bkey[index] += regularization * (y - p);
                            Akey[index] = K;
                        }

                        A.put(key, Akey);
                        B.put(key, Bkey);

                    }

                }
        );

        for (String key : B.keySet()) {
            double[] Bkey = B.get(key);
            int[] Akey = A.get(key);
            for (int j = 0; j < vocab_size; j++) {
                Bkey[j] = simulate_regularization(Bkey[j], Akey[j]);
            }
            B.put(key, Bkey);
        }

        br = new BufferedReader(new FileReader(file_path));
        br.lines().forEach(
                line -> {

                    StringBuilder s = new StringBuilder();
                    String[] parts = line.split("\t");
                    String[] labels = parts[0].split(",");
                    String[] attributes = parts[1].split(" ");

                    for(String key: B.keySet()){
                        double score = 0;
                        double [] Bkey = B.get(key);
                        for(String attribute: attributes){
                            score += Bkey[hashTrick(attribute)];
                        }
                        s.append(key).append("\t").append(sigmoid(score)).append(",");
                    }

                    pr.println(s.toString());

                }
        );

        pr.flush();
        pr.close();

    }

    private static void initialization() {
        String[] labels = {"nl", "el", "ru", "sl", "pl", "ca", "fr", "tr", "hu", "de", "hr", "es", "ga", "pt"};
        A = new Hashtable<>();
        B = new Hashtable<>();
        for (String label : labels) {
            A.put(label, new int[vocab_size]);
            B.put(label, new double[vocab_size]);
        }
    }

    private static double simulate_regularization(double x, int y) {
        return x * Math.pow(1 - 2 * learning_rate * regularization, K - y);
    }


    protected static double sigmoid(double score) {
        if (score > overflow)
            score = overflow;
        else if (score < -overflow)
            score = -overflow;
        double exp = Math.exp(score);
        return exp / (1 + exp);
    }

    private static int hashTrick(String word) {
        int id = word.hashCode() % vocab_size;
        if (id < 0) {
            id += vocab_size;
        }
        return id;
    }

    private static Vector<String> tokenizeDoc(String cur_doc) {
        String[] words = cur_doc.split("\\s+|_");
        Vector<String> tokens = new Vector<String>();
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("\\W", "");
            if (words[i].length() > 0) {
                tokens.add(words[i]);
            }
        }
        return tokens;
    }

}
