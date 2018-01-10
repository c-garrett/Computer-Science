import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by Owner on 11/22/2017.
 */
public class Message_Unigram {

    private static Text wordWrite = new Text();
    private static Text countWrite = new Text();

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<Text, Text> outputCollector, Reporter reporter) throws IOException {
            String line = text.toString();
            String[] parts = line.split("\\t");
            String word = parts[0];
            if (word.split("\\s").length == 1) {
                wordWrite.set(parts[0]);
                countWrite.set(parts[1] + "\t" + parts[2]);
                outputCollector.collect(wordWrite, countWrite);
            } else {
                String[] wordParts = parts[0].split("\\s");
                wordWrite.set(wordParts[0]);
                countWrite.set(parts[0]);
                outputCollector.collect(wordWrite, countWrite);
                wordWrite.set(wordParts[1]);
                outputCollector.collect(wordWrite, countWrite);
            }
        }

    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text text, Iterator<Text> iterator, OutputCollector<Text, Text> outputCollector, Reporter reporter) throws IOException {

            Long[] counts = new Long[2];
            counts[0] = 0L;
            counts[1] = 0L;

            HashSet<String> bigrams = new HashSet<>();
            String key = text.toString();

            while (iterator.hasNext()) {
                String currentValue = iterator.next().toString();
                if (currentValue.contains(key)) {
                    bigrams.add(currentValue);
                } else {
                    String[] currentValueParts = currentValue.split("\t");
                    counts[0] += Long.parseLong(currentValueParts[0]);
                    counts[1] += Long.parseLong(currentValueParts[1]);
                }
            }

            final String xOutputFrame = String.format("Bx=%s\tCx=%s", String.valueOf(counts[0]), String.valueOf(counts[1]));
            final String yOutputFrame = String.format("By=%s\tCy=%s", String.valueOf(counts[0]), String.valueOf(counts[1]));

            for (String bigram : bigrams) {
                String[] words = bigram.split("\\s");
                if (words[0].equals(key)) {
                    outputCollector.collect(new Text(bigram), new Text(xOutputFrame));
                } else {
                    outputCollector.collect(new Text(bigram), new Text(yOutputFrame));
                }
            }

        }
    }

    public static void run(String unigramInputPath, String bigramInputPath, String outputPath) throws IOException, ClassNotFoundException, InterruptedException {

        JobConf conf = new JobConf(Message_Unigram.class);
        conf.setJobName("Message_Unigram");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(unigramInputPath), new Path(bigramInputPath));
        FileOutputFormat.setOutputPath(conf, new Path(outputPath));

        Job job = new Job(conf);
        job.waitForCompletion(true);

    }
}
