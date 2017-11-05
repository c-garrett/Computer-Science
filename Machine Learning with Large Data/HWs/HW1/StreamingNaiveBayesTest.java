import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * Created by Owner on 11/3/2017.
 */
public class StreamingNaiveBayesTest {

    private static PrintWriter pw = new PrintWriter(System.out);

    private static HashMap<String, Integer> labelCount = new HashMap<>();
    private static HashMap<String, Integer> labelWordCount = new HashMap<>();

    private static int m = 1;

    private static int totalCount;

    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while ((line = stdin.readLine()) != null) {
            String[] tokens = line.split("\t");
            parseToken(tokens[0], tokens[1]);
        }
        stdin.close();
        parseTestFile(args[0]);
        pw.flush();
        pw.close();
    }

    static void parseToken(String labelString, String countString) {

        int count = Integer.parseInt(countString);
        if (labelString.contains(",")) {
            String[] parts = labelString.split(",");
            String word = parts[1].split("=")[1];
            String key = createLabelWordKey(parts[0].split("=")[1],word);
            labelWordCount.put(key, labelWordCount.getOrDefault(key, 0) + count);
            return;
        }
        labelCount.put(labelString.split("=")[1], count);
    }

    static void parseTestFile(String testDocument) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(new FileInputStream(testDocument)));
        String line;

        totalCount = labelCount.get("*");

        while ((line = stdin.readLine()) != null) {
            String[] split_document = line.split("\t");
            StringBuilder s = new StringBuilder();
            for (int i = 1; i < split_document.length; i++) {
                s.append(split_document[i]);
            }
            pw.println(classify(tokenizeDoc(s.toString())));
        }
        stdin.close();
    }

    static String classify(Vector<String> featureVector){

        double maxScore = Double.NEGATIVE_INFINITY;
        String labelPrediction = null;

        for(String label: labelCount.keySet()){

            if(label.equals("*"))
                continue;

            // Compute the right side of the classification score
            double nRight = labelCount.get(label) + (m);
            double dRight = totalCount + (m*4);

            double score = Math.log(nRight / dRight);

            // Compute the left side of the classification score
            double dicSize = labelWordCount.get(createLabelWordKey(label,"*"));
            dicSize += featureVector.size();

            for(String word: featureVector){

                double count = m;
                count += labelWordCount.getOrDefault(createLabelWordKey(label,word), 0);
                score += Math.log(count * 1 / dicSize);

            }

            // Determine the best classification score
            if(score > maxScore){
                maxScore = score;
                labelPrediction = label;
            }

        }

        return labelPrediction + "\t" + maxScore;

    }

    static String createLabelWordKey(String label, String word){
        return label + " ^ " + word;
    }

    // This function turns documents into features
    // Steps:
    //  1. Split document into words
    //  2. Add words that are cleaned to tokens
    //  3. Return the tokens
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
