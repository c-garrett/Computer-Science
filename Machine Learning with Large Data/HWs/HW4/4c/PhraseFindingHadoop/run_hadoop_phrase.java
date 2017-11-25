import java.io.*;
import java.util.HashMap;

/**
 * Created by Owner on 11/19/2017.
 */
public class run_hadoop_phrase {

    private enum Document_Totals {UBT, UFT, BBT, BFT}
    private static HashMap<String, Long> doc_totals = new HashMap<>();


    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {

        // Aggregate the totals for the unigram and bigram phrases, and store the totals.
        Aggregate.run(args[0], args[2]+"unigram");
        doc_totals.put(Document_Totals.UBT.toString(), Aggregate.backgroundCount);
        doc_totals.put(Document_Totals.UFT.toString(), Aggregate.foregroundCount);

        Aggregate.run(args[1], args[2]+"bigram");
        doc_totals.put(Document_Totals.BBT.toString(), Aggregate.backgroundCount);
        doc_totals.put(Document_Totals.BFT.toString(), Aggregate.foregroundCount);

        // Create the Messagae_unigrams
        Message_Unigram.run(args[2] + "unigram", args[2] + "bigram", args[4]);

        // Compute the scores for the phrases
        Compute.run(args[2] + "bigram", args[4], args[5], doc_totals);

    }
}
