import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashSet;

/**
 * Created by Owner on 11/16/2017.
 */
public class MessageGenerator {

    static PrintWriter pw = new PrintWriter(System.out);

    static HashSet<String> messages = new LinkedHashSet<>();

    private static final int javaHeapSizeMBs = 128;
    private static final int heapThreshold = 1;

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        in.readLine();
        in.lines().forEach(
                line -> {
                    String bigram = line.split(",")[0];
                    String message = bigramMessage(bigram);
                    if(message != null){
                        pw.println(message);
                    }
                    //                    checkMemory();
                }
        );
        pw.flush();
        pw.close();
    }

    static String bigramMessage(String bigram) {
        String[] parts = bigram.split("\\s");
        if(parts.length != 2){
            return null;
        }
        String message = "%s,%s";
        return String.format(message, parts[0], bigram) + "\n" +
                String.format(message, parts[1], bigram);
    }

//    private static void checkMemory() {
//        if (Runtime.getRuntime().freeMemory() < (javaHeapSizeMBs * (heapThreshold)) * 1048576) {
//            messages.forEach(
//                    message -> pw.println(message)
//            );
//            messages.clear();
//        }
//    }

}
