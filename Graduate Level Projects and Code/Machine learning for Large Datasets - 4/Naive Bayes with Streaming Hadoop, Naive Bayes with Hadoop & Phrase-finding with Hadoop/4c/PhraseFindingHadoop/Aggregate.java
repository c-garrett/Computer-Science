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
public class Aggregate {

    private static String stopwords = "i,the,to,and,a,an,of,it,you,that,in,my,is,was,for";

    public static Long backgroundCount = 0L;
    public static Long foregroundCount = 0L;

    private enum MyCounters{B_COUNT, F_COUNT, B_VOL, F_VOL}

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        private static Text word = new Text();
        private static Text countWrite = new Text();

        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<Text, Text> outputCollector, Reporter reporter) throws IOException {
            String [] parts = text.toString().split("\t");
            String [] words = parts[0].split("\\s");
            for(String word: words){
                if(stopwords.contains(word)){
                    return;
                }
            }
            Long decadeCount = Long.parseLong(parts[2]);
            word.set(parts[0]);
            countWrite.set(Long.parseLong(parts[1]) > 1969 ? "b:"+decadeCount : "f:"+decadeCount);
            outputCollector.collect(word,countWrite);
        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text text, Iterator<Text> iterator, OutputCollector<Text, Text> outputCollector, Reporter reporter) throws IOException {
            Long [] b = {0L};
            Long [] f = {0L};
            iterator.forEachRemaining(
                    value -> {
                        String [] parts = value.toString().split(":");
                        if(parts[0].equals("b")){
                            b[0] += Long.parseLong(parts[1]);
                        }else{
                            f[0] += Long.parseLong(parts[1]);
                        }
                    }
            );
            outputCollector.collect(text, new Text(b[0] + "\t" + f[0]));
            reporter.incrCounter(MyCounters.B_COUNT, b[0]);
            reporter.incrCounter(MyCounters.F_COUNT, f[0]);

//            // what is the volume of the map corpus being used for
//            reporter.incrCounter(MyCounters.B_VOL, b[0] != 0 ? 1 : 0);
//            reporter.incrCounter(MyCounters.F_VOL, f[0] != 0 ? 1 : 0);
        }

    }
//
//    public void configure(JobConf job) {
//        unigram = job.get("doc").equals("0");
//    }

    public static void run(String inputFile, String outputPath) throws ClassNotFoundException, IOException, InterruptedException {

        // Set up the job configuration

        JobConf conf = new JobConf(Aggregate.class);
        conf.setJobName("Aggregate");

        // Output collector class
        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

//        conf.set("doc", args[2]);

        FileInputFormat.setInputPaths(conf, new Path(inputFile));
        FileOutputFormat.setOutputPath(conf, new Path(outputPath));

        Job job = new Job(conf);

        // Monitor the job wait for the completion and obtain the results of the counters

        job.waitForCompletion(true);

        backgroundCount = job.getCounters().findCounter(MyCounters.B_COUNT).getValue();
        foregroundCount = job.getCounters().findCounter(MyCounters.F_COUNT).getValue();

    }

}