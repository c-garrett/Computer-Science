import java.io.*;
import java.util.HashMap;

/**
 * Created by Owner on 11/19/2017.
 */
public class run_hadoop_phrase {

    private static HashMap<String, Long> doc_totals = new HashMap<>();

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {

        Aggregate.run(args[0], args[2]+"unigram");

        // These are the total for the unigram files
        Long UBT_Count = Aggregate.backgroundCount;
        Long UFT_Count = Aggregate.foregroundCount;

        Aggregate.run(args[1], args[2]+"bigram");

        // These are the total for the bigram files
        Long BBT_Count = Aggregate.backgroundCount;
        Long BFT_Count = Aggregate.foregroundCount;

        // Obtain the counts for the total counts for the unigram and bigram file
        Count_Size.run(args[2] + "unigram", args[2] + "bigram", args[3]);

        // Obtain the total counts
        new BufferedReader(new FileReader(args[3] + "\\part-00000"))
                .lines()
                    .forEach(
                        line -> {
                            String [] parts = line.split("\\t");
                            doc_totals.put(parts[0], Long.parseLong(parts[1]));
                        }
        );

        // Create the Messagae_unigrams
        Message_Unigram.run(args[2] + "unigram", args[2] + "bigram", args[4]);

        // Compute the scores for the phrases
        Compute.run(args[2] + "bigram", args[4], args[5], doc_totals);


    }
}
