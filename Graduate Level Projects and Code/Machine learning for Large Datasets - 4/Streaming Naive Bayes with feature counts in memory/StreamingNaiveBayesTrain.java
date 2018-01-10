import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Owner on 11/3/2017.
 */
public class StreamingNaiveBayesTrain {

    static PrintWriter pw = new PrintWriter(System.out);

    public static void main(String[] args) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        HashMap<String, Integer> labelCount = new HashMap<>();
        HashMap<String, Integer> labelWordCount = new HashMap<>();
        int totalCount = 0;

        // This function reads in documents and counts
        // Steps:
        //  1. Read in a line
        //      a. Split the line on tab
        //      b. Obtain the first part of the split as the labels array
        //      c. Obtain the tokens from the remaining parts
        //  2. Obtain the counts of labels, and labels and words
        String line;
        while ((line = reader.readLine()) != null) {

            String[] split_document = line.split("\t");
            String[] labels = split_document[0].split(",");
            StringBuilder s = new StringBuilder();
            for (int i = 1; i < split_document.length; i++) {
                s.append(split_document[i]);
            }

            Vector<String> words = tokenizeDoc(s.toString());

            for (String label : labels) {
                int label_length = label.length();
                if (!label.substring(label_length - 3, label_length).equals("CAT")) {
                    continue;
                }
                totalCount++;

                String newLabel = "Y=" + label;
                labelCount.put(newLabel, labelCount.getOrDefault(newLabel, 0) + 1);

                String newLabelAny = newLabel + ",W=*";
                labelWordCount.put(newLabelAny, labelWordCount.getOrDefault(newLabelAny, 0) + words.size());

                for (String word : words) {
                    String newLabelWord = newLabel + ",W=" + word;
                    labelWordCount.put(newLabelWord, labelWordCount.getOrDefault(newLabelWord, 0) + 1);
                }
            }
        }

        pw.println("Y=*\t" + totalCount);
        printMap(labelCount);
        printMap(labelWordCount);

        pw.flush();
        pw.close();

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

    static void printMap(HashMap<String, Integer> map) {
        for (String key : map.keySet()) {
            pw.println(key + "\t" + map.get(key));
        }
    }

}
