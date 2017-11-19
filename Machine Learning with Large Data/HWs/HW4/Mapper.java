
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;


public class Mapper {

    // Takes input coming from the stdin and outputs (key,value) pairs

    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        final int[] totalCount = {0};
        HashMap<String, Integer> labelCounts = new HashMap<>();
        HashMap<String, Integer> labelWordCounts = new HashMap<>();

        String labelFrame = "Y=%s";
        String keyFrame = labelFrame + ",W=%s";

        stdin.lines().forEach(
                line -> {
                    String[] parts = line.split("\t");
                    for (String label : parts[0].split(",")) {
                        if (label.endsWith("CAT")) {
                            totalCount[0]++;
                            String newLabel = String.format(labelFrame, label);
                            labelCounts.put(newLabel, labelCounts.getOrDefault(newLabel, 0) + 1);
                            String[] words = tokenize(parts[1]);
                            Arrays.stream(words).forEach(
                                    word -> {
                                        String newLabelWord = String.format(keyFrame, label, word);
                                        labelWordCounts.put(newLabelWord,
                                                labelWordCounts.getOrDefault(newLabelWord, 0) + 1);
                                    }
                            );
                            String newLabelWord = String.format(keyFrame, label, "*");
                            labelWordCounts.put(newLabelWord, labelWordCounts.getOrDefault(newLabelWord, 0) + words.length);
                        }
                    }
                }
        );

        stdin.close();

        pw.println("Y=*\t" + totalCount[0]);
        printMap(labelCounts, pw);
        printMap(labelWordCounts, pw);

        pw.flush();
        pw.close();

    }

    static void printMap(HashMap<String, Integer> map, PrintWriter pw) {
        map.keySet().forEach(key -> pw.println(key + "\t" + map.get(key)));
    }

    static String[] tokenize(String cur_doc) {
        return Arrays.stream(cur_doc.split("\\s+"))
                .map(word -> word.replaceAll("\\W", ""))
                .filter(word -> word.length() > 0)
                .toArray(String[]::new);
    }
}
