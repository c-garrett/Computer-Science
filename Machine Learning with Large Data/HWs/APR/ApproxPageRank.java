import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class ApproxPageRank {

    BufferedReader br;
    final String file_path;
    final String seed;
    final double alpha;
    final double epsilon;
    final HashMap<String, Double> p_approximation;
    final HashMap<String, Double> residual;

    sweepWrapper optimalSet;

    private ApproxPageRank(String file_path, String seed, double alpha, double epsilon) {
        this.file_path = file_path;
        this.seed = seed;
        this.alpha = alpha;
        this.epsilon = epsilon;

        this.p_approximation = new HashMap<>();
        this.residual = new HashMap<>();

    }

    private void printConductanceGraph() {
        StringBuilder s = new StringBuilder("low-conductance subgraph\n");
        for (String node : optimalSet.optimalSet) {
            s.append(node).append("\t").append(this.p_approximation.get(node)).append("\n");
        }
        System.out.println(s.toString());
    }

    private void printApr() {
        StringBuilder s = new StringBuilder("Approximate page rank\n");
        List<Entry<String, Double>> page_rank = new LinkedList<>(p_approximation.entrySet());
        page_rank.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        page_rank.forEach(x -> s.append(x.getKey()).append(":\t").append(x.getValue()).append("\n"));
        System.out.println(s.toString());
    }

    private class sweepWrapper {
        HashSet<String> optimalSet;
        double optimalScore;

        int volume;
        int boundary;

        sweepWrapper(HashSet<String> set, double score, int volume, int boundary) {
            this.optimalSet = set;
            this.optimalScore = score;
            this.volume = volume;
            this.boundary = boundary;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder();
            optimalSet.iterator().forEachRemaining(
                    x -> s.append(x).append("\n")
            );
            return s.toString();
        }
    }

    private sweepWrapper sweepOperation(String key, sweepWrapper wrapper) throws IOException {

        this.br = new BufferedReader(new FileReader(this.file_path));

        HashSet<String> conductance = new HashSet<>();
        conductance.addAll(wrapper.optimalSet);
        conductance.add(key);

        int volume = wrapper.volume;
        int boundary = wrapper.boundary;

        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.split("\t");
            if (tokens[0].equals(key)) {
                volume += -1 + tokens.length;
                for (int i = 1; i < tokens.length; i++) {
                    if (!conductance.contains(tokens[i])) {
                        boundary += 1;
                    } else {
                        boundary -= 1;
                    }
                }
                break;
            }

        }

        double newValue = boundary * 1.0 / volume;
        if (newValue < wrapper.optimalScore) {
            wrapper.optimalScore = newValue;
            wrapper.optimalSet.add(key);
            wrapper.volume = volume;
            wrapper.boundary = boundary;
        }

        return wrapper;
    }

    private void calculateSubGraph() throws IOException {
        List<Entry<String, Double>> page_rank = new LinkedList<>(p_approximation.entrySet());
        page_rank.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        this.optimalSet = new sweepWrapper(new HashSet<>(), Double.MAX_VALUE, 0, 0);

        for (Entry<String, Double> entry : page_rank) {
            this.optimalSet = sweepOperation(entry.getKey(), optimalSet);
        }

    }

    private void push(String[] parts, double ru, double alpha) {

        this.p_approximation.put(parts[0], this.p_approximation.getOrDefault(parts[0], 0.0)
                + alpha * this.residual.get(parts[0]));

        int o_degree = -1 + parts.length;
        this.residual.put(parts[0], (1 - alpha) * ru / 2);
        for (int i = 1; i < parts.length; i++) {
            this.residual.put(parts[i], this.residual.getOrDefault(parts[i], 0.0) + (1 - alpha) * ru / (2 * o_degree));
        }

    }

    private void apr() throws FileNotFoundException {

        this.p_approximation.clear();
        this.residual.clear();

        this.residual.put(seed, 1.0);

        final boolean[] pushed = {false};
        do {
            pushed[0] = false;
            this.br = new BufferedReader(new FileReader(this.file_path));
            this.br.lines().forEach(
                    line -> {
                        String[] parts = line.split("\t");
                        if (this.residual.containsKey(parts[0])) {
                            double r_value = this.residual.get(parts[0]);
                            double o_degree = -1 + parts.length;
                            if (r_value / o_degree > epsilon) {
                                push(parts, this.residual.get(parts[0]), alpha);
                                pushed[0] = true;
                            }
                        }
                    }
            );
        } while (pushed[0]);

    }

    public static void main(String[] args) throws IOException {

        String file_path = args[0];
        String seed = args[1];
        double alpha = Double.parseDouble(args[2]);
        double epsilon = Double.parseDouble(args[3]);

        ApproxPageRank controller = new ApproxPageRank(file_path, seed, alpha, epsilon);
        controller.apr();
        controller.calculateSubGraph();

        controller.printApr();
        controller.printConductanceGraph();

    }

}
