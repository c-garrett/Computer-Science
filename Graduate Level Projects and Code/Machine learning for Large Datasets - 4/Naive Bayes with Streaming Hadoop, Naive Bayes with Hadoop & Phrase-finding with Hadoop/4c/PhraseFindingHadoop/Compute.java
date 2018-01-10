import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Job;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by Owner on 11/16/2017.
 */

public class Compute {

    private static Long BBT;
    private static Long BFT;
    private static Long UBT;
    private static Long UFT;

    public static class Map extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {

        @Override
        public void map(LongWritable longWritable, Text text, OutputCollector<Text, Text> outputCollector, Reporter reporter) throws IOException {

            String line = text.toString();
            String[] parts = line.split("\\t");

            switch (parts.length) {
                case 2:
                    outputCollector.collect(new Text(parts[0]), new Text(parts[1]));
                    return;
                case 3:
                    outputCollector.collect(new Text(parts[0]), new Text(parts[1] + "\t" + parts[2]));
            }


        }
    }

    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text text, Iterator<Text> iterator, OutputCollector<Text, Text> outputCollector, Reporter reporter) throws IOException {

            while (iterator.hasNext()) {
                String current = iterator.next().toString();
                if (current.contains("Bx")) {
                    String[] currentParts = current.split("\\s");
                    Phrase.setX(Double.parseDouble(currentParts[0].split("=")[1]),
                            Double.parseDouble(currentParts[1].split("=")[1]));
                } else if (current.contains("By")) {
                    String[] currentParts = current.split("\\s");
                    Phrase.setY(Double.parseDouble(currentParts[0].split("=")[1]),
                            Double.parseDouble(currentParts[1].split("=")[1]));
                } else {
                    String [] currentParts = current.split("\\s");
                    Phrase.setXY(Double.parseDouble(currentParts[0]),
                            Double.parseDouble(currentParts[1]));
                }
            }

            Phrase.calculate(BBT, BFT, UBT, UFT);
            outputCollector.collect(text, new Text(Phrase.output()));
        }

        public void configure(JobConf job){
            BBT = Long.parseLong(job.get("BBT"));
            BFT = Long.parseLong(job.get("BFT"));
            UBT = Long.parseLong(job.get("UBT"));
            UFT = Long.parseLong(job.get("UFT"));
        }

        private static double informativeness(double biBg, double biFg, double biBgTotal, double biFgTotal) {
            double p = (biFg + 1) / biFgTotal;
            double q = (biBg + 1) / biBgTotal;
            return pointWiseKL(p, q);
        }

        private static double phraseness(double biFg, double biFgTotal, double uniFgX, double uniFgY, double uniFgTotal) {
            double p = (biFg + 1) / biFgTotal;
            double q = ((uniFgX + 1) / uniFgTotal) * ((uniFgY + 1) / uniFgTotal);
            return pointWiseKL(p, q);
        }

        private static double pointWiseKL(double P, double Q) {
            return P * Math.log(P / Q);
        }

        private static double phraseScore(double a, double b) {
            return a + b;
        }

        static class Phrase {

            static double xBg;
            static double xFg;
            static double yBg;
            static double yFg;
            static double xyBg;
            static double xyFg;

            static double informativeness;
            static double phraseness;
            static double score;

            static void setX(double bg, double fg) {
                xBg = bg;
                xFg = fg;
            }

            static void setY(double bg, double fg) {
                yBg = bg;
                yFg = fg;
            }

            static void setXY(double bg, double fg) {
                xyBg = bg;
                xyFg = fg;
            }

            static void calculate(double biTotalBg, double biTotalFg, double uniTotalBg, double uniTotalFg) {
                informativeness = informativeness(xyBg, xyFg, biTotalBg, biTotalFg);
                phraseness = phraseness(xyFg, biTotalFg, xFg, yFg, uniTotalFg);
                score = phraseScore(informativeness, phraseness);
            }

            public static String output(){
                StringBuilder s = new StringBuilder();
                s.append(score)
                        .append("\t")
                        .append(phraseness)
                        .append("\t")
                        .append(informativeness)
                        .append("\n");
                return s.toString();
            }

        }
    }

    public static void run(String bigramInputPath, String combinedInputPath, String outputPath, HashMap<String, Long> doc_totals) throws InterruptedException, IOException, ClassNotFoundException {

        JobConf conf = new JobConf(Compute.class);
        conf.setJobName("Computation");
        // Set the totals that were calculated
        conf.set("BBT", String.valueOf(doc_totals.get("BBT")));
        conf.set("BFT", String.valueOf(doc_totals.get("BFT")));
        conf.set("UBT", String.valueOf(doc_totals.get("UBT")));
        conf.set("UFT", String.valueOf(doc_totals.get("UFT")));

        conf.setOutputKeyClass(Text.class);
        conf.setOutputValueClass(Text.class);

        conf.setMapperClass(Map.class);
        conf.setReducerClass(Reduce.class);

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        FileInputFormat.setInputPaths(conf, new Path(bigramInputPath), new Path(combinedInputPath));
        FileOutputFormat.setOutputPath(conf, new Path(outputPath));

        Job job = new Job(conf);
        job.waitForCompletion(true);

    }
}
