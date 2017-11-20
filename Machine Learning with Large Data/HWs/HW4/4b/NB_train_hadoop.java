import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;

import java.io.IOException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Owner on 11/19/2017.
 */
public class NB_train_hadoop {

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, IntWritable> {
        private static IntWritable count = new IntWritable(0);
        private static Text word = new Text();

        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<Text, IntWritable> outputCollector, Reporter reporter) throws IOException {

            final int[] totalCount = {0};
            HashMap<String, Integer> labelCounts = new HashMap<>();
            HashMap<String, Integer> labelWordCounts = new HashMap<>();

            String labelFrame = "Y=%s";
            String keyFrame = labelFrame + ",W=%s";

            String line = text.toString();
            String[] parts = line.split("\t");
            for (String label : parts[0].split(",")) {
                if (label.endsWith("CAT")) {
                    totalCount[0]++;
                    String newLabel = String.format(labelFrame, label);
                    labelCounts.put(newLabel, labelCounts.getOrDefault(newLabel, 0) + 1);
                    String[] words = tokenize(parts[1]);
                    Arrays.stream(words).forEach(
                            word -> {
                                String newLabelWord = String.format(keyFrame, label, word);
                                labelWordCounts.put(newLabelWord,
                                        labelWordCounts.getOrDefault(newLabelWord, 0) + 1);
                            }
                    );
                    String newLabelWord = String.format(keyFrame, label, "*");
                    labelWordCounts.put(newLabelWord, labelWordCounts.getOrDefault(newLabelWord, 0) + words.length);
                }
            }

            word.set("Y=*\t");
            count.set(totalCount[0]);
            outputCollector.collect(word, count);

            printMap(labelCounts, outputCollector);
            printMap(labelWordCounts, outputCollector);

        }

        private static void printMap(HashMap<String, Integer> map, OutputCollector<Text, IntWritable> outputCollector) throws IOException {
            for (String key : map.keySet()) {
                word.set(key);
                count.set(map.get(key));
                outputCollector.collect(word, count);
            }
        }

        private static String[] tokenize(String cur_doc) {
            return Arrays.stream(cur_doc.split("\\s+"))
                    .map(word -> word.replaceAll("\\W", ""))
                    .filter(word -> word.length() > 0)
                    .toArray(String[]::new);
        }
    }


    public static class Reduce extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

        @Override
        public void reduce(Text text, Iterator<IntWritable> iterator, OutputCollector<Text, IntWritable> outputCollector, Reporter reporter) throws IOException {
            int sum = 0;
            while (iterator.hasNext()) {
                sum += iterator.next().get();
            }
            outputCollector.collect(text, new IntWritable(sum));
        }

    }


    public static void run(String input, String output, String num) throws ClassNotFoundException,
            IOException, InterruptedException {

        JobConf conf = new JobConf(NB_train_hadoop.class);
        conf.setJobName("NB");
        conf.setNumReduceTasks(Integer.parseInt(num));

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(IntWritable.class);
        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);
        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(input));
        FileOutputFormat.setOutputPath(conf, new Path(output));

        JobClient.runJob(conf);
    }
}
