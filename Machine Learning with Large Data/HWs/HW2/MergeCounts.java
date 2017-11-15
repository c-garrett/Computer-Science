
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by Owner on 11/12/2017.
 */
public class MergeCounts {

    static PrintWriter pw = new PrintWriter(System.out);
    static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    static String prevLabel = null;

    static int labelCount;

    public static void main(String [] args) throws IOException {

        reader.lines().forEach(
                line -> {
                    String label = line.split("\t")[0];
                    int count = Integer.parseInt(line.split("\t")[1]);
                    if(label.equals(prevLabel)){
                        labelCount += count;
                    }else{
                        printCombine(prevLabel, labelCount);
                        prevLabel = label;
                        labelCount = count;
                    }
                }
        );

        reader.close();
        printCombine(prevLabel,labelCount);
        pw.flush();
        pw.close();
    }

    static void printCombine(String label, int count){
        if(label != null) {
            pw.println(String.format("%s\t%d", label, count));
        }
    }

}
