import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Owner on 11/3/2017.
 * Limited memory implementation
 */
public class NBTrain {

    static PrintWriter pw = new PrintWriter(System.out);
    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    static HashMap<String, Integer> counts = new HashMap<>();

    static int javaHeapSizeMBs = 128;

    static String labelString = "Y=%s";
    static String labelWordString = "%s,W=%s";

    static int totalCount = 0;


    public static void main(String[] args) throws IOException {
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
                totalCount++;
                String newLabel = String.format(labelString,label);
                // Increment the count of the label
                incrementCount(newLabel);
                // Increment the count of label all words *
                incrementCount(newLabel, "*", words.size());
                // Increment the counts of label words
                for (String word : words) {
                    incrementCount(newLabel, word);
                }
            }

            // Print the HashMap if the less than 80 percent of memory is available
            if(Runtime.getRuntime().freeMemory() < (javaHeapSizeMBs * (.8)) * 1048576 ){
            	printMap(counts);
                counts.clear();
            }
        }
        reader.close();
        pw.println("Y=*\t"+totalCount);
        printMap(counts);
        pw.flush();
        pw.close();
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

    static void printMap(HashMap<String, Integer> map) {
        for (String key : map.keySet()) {
            pw.println(key + "\t" + map.get(key));
        }
    }

    static void incrementCount(String label){
        counts.put(label,
                counts.getOrDefault(label, 0)
                        + 1);
    }

    static void incrementCount(String label, String word){
        counts.put(String.format(labelWordString,label,word),
                counts.getOrDefault(String.format(labelWordString,label,word),0) +
                        1);

    }

    static void incrementCount(String label, String word, int increment){
        counts.put(String.format(labelWordString, label, word),
                counts.getOrDefault(String.format(labelWordString, label, word),0) +
                        increment);
    }

}
