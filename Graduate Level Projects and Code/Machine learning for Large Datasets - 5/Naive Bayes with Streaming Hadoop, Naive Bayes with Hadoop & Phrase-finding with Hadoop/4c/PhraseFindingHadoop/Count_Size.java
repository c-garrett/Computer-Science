import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Job;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by Owner on 11/19/2017.
 */

public class Count_Size {

    private enum Totals {
        UFT, UBT, BFT, BBT
    }

    static Text key = new Text();
    static LongWritable value = new LongWritable(0);

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, LongWritable> {

        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<Text, LongWritable> outputCollector, Reporter reporter) throws IOException {
            String[] lineParts = text.toString().split("\\t");
            if (lineParts[0].split("\\s").length == 1) {
                outputTotals(Totals.UBT.toString(), Long.parseLong(lineParts[1]), outputCollector);
                outputTotals(Totals.UFT.toString(), Long.parseLong(lineParts[2]), outputCollector);
            } else {
                outputTotals(Totals.BBT.toString(), Long.parseLong(lineParts[1]), outputCollector);
                outputTotals(Totals.BFT.toString(), Long.parseLong(lineParts[2]), outputCollector);
            }
        }

    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, LongWritable, Text, LongWritable> {

        @Override
        public void reduce(Text text, Iterator<LongWritable> iterator, OutputCollector<Text, LongWritable> outputCollector, Reporter reporter) throws IOException {
            long total = 0L;
            while (iterator.hasNext()) {
                total += iterator.next().get();
            }
            outputTotals(text.toString(),total, outputCollector);
        }

    }

    private static void outputTotals(String keyWrite, long countWrite,
                                     OutputCollector<Text, LongWritable> outputCollector) throws IOException {
        key.set(keyWrite);
        value.set(countWrite);
        outputCollector.collect(key, value);
    }

    public static void run(String unigramInputPath, String bigramInputPath, String countOutPath)
            throws ClassNotFoundException, IOException, InterruptedException {

        JobConf conf = new JobConf(Count_Size.class);
        conf.setJobName("Count_Size");

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(LongWritable.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(unigramInputPath), new Path(bigramInputPath));
        FileOutputFormat.setOutputPath(conf, new Path(countOutPath));

        Job job = new Job(conf);
        job.waitForCompletion(true);
    }

}