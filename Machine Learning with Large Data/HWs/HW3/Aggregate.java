
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by Owner on 11/15/2017.
 */
public class Aggregate {

    private static final int javaHeapSizeMBs = 128;
    private static final int heapThreshold = 1;
    //    private static final int heapThreshold = .3;
    private static final int backgroundCorpusStart = 1960;
    private static final int backgroundCorpusEnd = 1989;
    private static final int foregroundCorpusStart = 1990;
    private static final int foregroundCorpusEnd = 1999;

    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final PrintWriter pw = new PrintWriter(System.out);
    private static boolean unigram;

    public static void main(String[] args) throws IOException {

        LineData.totals[0] = 0L;
        LineData.totals[1] = 0L;

        unigram = Integer.parseInt(args[0]) == 0;
        in.lines().forEach(Aggregate::parseInputLine);
        in.close();
        LineData.printTotals();
        pw.flush();
        pw.close();

    }

    private static void parseInputLine(String line) {
        String[] splitLine = line.split("\\s+");
        String text;
        int decade;
        int count;
        if (unigram) {
            text = splitLine[0];
            decade = Integer.parseInt(splitLine[1]);
            count = Integer.parseInt(splitLine[2]);
        } else {
            text = splitLine[0] + " " + splitLine[1];
            decade = Integer.parseInt(splitLine[2]);
            count = Integer.parseInt(splitLine[3]);
        }
        if (decade >= backgroundCorpusStart && decade <= backgroundCorpusEnd) {
            LineData.addB(text, count);
        } else if (decade >= foregroundCorpusStart && decade <= foregroundCorpusEnd) {
            LineData.addF(text, count);
            checkMemory();
        }
    }

    private static void checkMemory() {
        if (Runtime.getRuntime().freeMemory() < (javaHeapSizeMBs * (heapThreshold)) * 1048576) {
            LineData.print(unigram);
            LineData.clear();
        }
    }

    static class LineData {

        static final HashMap<String, Integer> b = new HashMap<>();
        static final HashMap<String, Integer> f = new HashMap<>();

        static final String unigramFrame = "%s, Bx=%d,Cx=%d";
        static final String bigramFrame = "%s, 1xy=%d,1xy=%d";

        static final Long [] totals = new Long[2];

        static void addB(String key, int count) {
            b.put(key, b.getOrDefault(key, 0) + count);
            totals[0] += (long)count;
        }

        static void addF(String key, int count) {
            f.put(key, f.getOrDefault(key, 0) + count);
            totals[1] += (long)count;
        }

        static void clear() {
            b.clear();
            f.clear();
        }

        static void print(boolean unigram) {
            for (String key : b.keySet()) {
                pw.println(String.format(unigram ? unigramFrame : bigramFrame,
                        key, b.getOrDefault(key,0), f.getOrDefault(key,0)));
            }
        }

        static void printTotals(){
            pw.println(String.format("1%s,TotalBg=%d,TotalFg=%d",unigram ? "Uni" : "Bi",totals[0], totals[1]));
        }

    }

}
