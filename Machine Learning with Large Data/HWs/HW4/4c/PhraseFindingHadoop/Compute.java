import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapreduce.Job;

import java.io.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by Owner on 11/16/2017.
 */

public class Compute {

    private static Phrase phrase;

    private enum Document_Totals {UBT, UFT, BBT, BFT}

    private static Long BBT;
    private static Long BFT;
    private static Long UBT;
    private static Long UFT;

    static PriorityQueue<Phrase> pq = new PriorityQueue<>(new ScoreComparator());

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

    public static class Reduce extends MapReduceBase implements Reducer<Text, Text, Text, LongWritable> {

        @Override
        public void reduce(Text text, Iterator<Text> iterator, OutputCollector<Text, LongWritable> outputCollector, Reporter reporter) throws IOException {

            while (iterator.hasNext()) {
                phrase = new Phrase(text.toString());
                String current = iterator.next().toString();
                if (current.contains("Bx")) {
                    String[] currentParts = current.split("\\s");
                    phrase.setX(Double.parseDouble(currentParts[0].split("=")[1]),
                            Double.parseDouble(currentParts[1].split("=")[1]));
                } else if (current.contains("By")) {
                    String[] currentParts = current.split("\\s");
                    phrase.setY(Double.parseDouble(currentParts[0].split("=")[1]),
                            Double.parseDouble(currentParts[1].split("=")[1]));
                } else {
                    String [] currentParts = current.split("\\s");
                    phrase.setXY(Double.parseDouble(currentParts[0]),
                            Double.parseDouble(currentParts[1]));
                }
            }

            phrase.calculate(BBT, BFT, UBT, UFT);
            pq.add(phrase);
        }
    }

    public static void run(String bigramInputPath, String combinedInputPath, String outputPath, HashMap<String, Long> doc_totals) throws InterruptedException, IOException, ClassNotFoundException {

        BBT = doc_totals.get(Document_Totals.BBT.toString());
        BFT = doc_totals.get(Document_Totals.BFT.toString());
        UBT = doc_totals.get(Document_Totals.UBT.toString());
        UFT = doc_totals.get(Document_Totals.UFT.toString());

        JobConf conf = new JobConf(Compute.class);
        conf.setJobName("Computation");

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

        FileWriter out = new FileWriter(outputPath + "\\results.txt");

        while(!pq.isEmpty()){
            out.write(pq.poll().toString());
        }
        out.flush();

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

        final String phrase;

        double xBg;
        double xFg;
        double yBg;
        double yFg;
        double xyBg;
        double xyFg;

        double informativeness;
        double phraseness;
        double score;

        Phrase(String phrase) {
            this.phrase = phrase;
        }

        void setX(double bg, double fg) {
            xBg = bg;
            xFg = fg;
        }

        void setY(double bg, double fg) {
            yBg = bg;
            yFg = fg;
        }

        void setXY(double bg, double fg) {
            xyBg = bg;
            xyFg = fg;
        }

        void calculate(double biTotalBg, double biTotalFg, double uniTotalBg, double uniTotalFg) {
            informativeness = informativeness(xyBg, xyFg, biTotalBg, biTotalFg);
            phraseness = phraseness(xyFg, biTotalFg, xFg, yFg, uniTotalFg);
            score = phraseScore(informativeness, phraseness);
        }

        public String toString(){
            StringBuilder s = new StringBuilder();
            s.append(phrase)
                    .append("\t")
                    .append(score)
                    .append("\t")
                    .append(phraseness)
                    .append("\t")
                    .append(informativeness)
                    .append("\n");
            return s.toString();
        }

    }

    static class ScoreComparator implements Comparator<Phrase> {
        @Override
        public int compare(Phrase x, Phrase y) {
            if (x.score == y.score) {
                return 0;
            } else if (x.score > y.score) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
