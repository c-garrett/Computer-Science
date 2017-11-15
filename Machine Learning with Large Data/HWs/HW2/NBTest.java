import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * Created by Owner on 11/3/2017.
 */
public class NBTest {

    private static PrintWriter pw = new PrintWriter(System.out);
    private static HashMap<String, Integer> labelCount = new HashMap<>();
    private static HashMap<String, Integer> labelWordCount = new HashMap<>();
    private static HashSet<String> wordSet = new HashSet<>();
    private static int m = 1;
    private static int totalCount;
    private static int vocSize = 0;

    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        HashSet<String> hashSet = readTestSet(args[0]);

        stdin.lines().forEach(
                line -> {
                    String[] tokens = line.split("\t");
                    parseToken(tokens[0], tokens[1], hashSet);
                }
        );

        stdin.close();
        parseTestFile(args[0]);
        pw.flush();
        pw.close();
    }

    static void parseToken(String labelString, String countString, HashSet hashSet) {
        int count = Integer.parseInt(countString);
        if (labelString.contains(",")) {
            String[] parts = labelString.split(",");
            String word = parts[1].split("=")[1];
            wordSet.add(word);
            if (!hashSet.contains(word) && !word.equals("*")) {
                return;
            }
            String key = createLabelWordKey(parts[0].split("=")[1], word);
            labelWordCount.put(key, labelWordCount.getOrDefault(key, 0) + count);
            return;
        }
        labelCount.put(labelString.split("=")[1], count);
    }

    static void parseTestFile(String testDocument) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(new FileInputStream(testDocument)));
        totalCount = labelCount.get("*");
        vocSize = wordSet.size() - labelCount.size();
        wordSet = null;
        stdin.lines().forEach(
                line -> {
                    String[] split_document = line.split("\t");
                    StringBuilder s = new StringBuilder();
                    for (int i = 1; i < split_document.length; i++) {
                        s.append(split_document[i]);
                    }
                    pw.println(classify(tokenizeDoc(s.toString())));
                }
        );
        stdin.close();
    }

    private static HashSet<String> readTestSet(String test) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(test)));
        HashSet<String> set = new HashSet<>();
        reader.lines().forEach(
                line -> set.addAll(tokenizeDoc(line.split("\t")[1]))
        );
        return set;
    }

    static String classify(Vector<String> featureVector) {

        double maxScore = Double.NEGATIVE_INFINITY;
        String labelPrediction = null;

        for (String label : labelCount.keySet()) {
            if (label.equals("*"))
                continue;
            // Compute the right side of the classification score
            double nRight = labelCount.get(label) + (m);
            double dRight = totalCount + (m * 4);
            double score = Math.log(nRight / dRight);
            // Compute the left side of the classification score
            double dicSize = labelWordCount.get(createLabelWordKey(label, "*"));
            dicSize += vocSize;
            for (String word : featureVector) {
                double count = m;
                count += labelWordCount.getOrDefault(createLabelWordKey(label, word), 0);
                score += Math.log(count * 1 / dicSize);
            }
            // Determine the best classification score
            if (score > maxScore) {
                maxScore = score;
                labelPrediction = label;
            }
        }

        return labelPrediction + "\t" + maxScore;

    }

    static String createLabelWordKey(String label, String word) {
        return label + " ^ " + word;
    }

    static Vector<String> tokenizeDoc(String cur_doc) {
        String[] words = cur_doc.split("\\s+");
        Vector<String> tokens = new Vector<>();
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("\\W", "");
            if (words[i].length() > 0) {
                tokens.add(words[i]);
            }
        }
        return tokens;
    }

}
