import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Created by Owner on 11/16/2017.
 */

public class PhraseGenerator {

    private static Phrase phrase;

    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        PriorityQueue<Phrase> pq = new PriorityQueue<>(new ScoreComparator());

        String[] biValues = in.readLine().split(",");
        String[] uiValues = in.readLine().split(",");
        Long biTotalBg = Long.parseLong(biValues[1].split("=")[1]);
        Long biTotalFg = Long.parseLong(biValues[2].split("=")[1]);
        Long uiTotalBg = Long.parseLong(uiValues[1].split("=")[1]);
        Long uiTotalFg = Long.parseLong(uiValues[2].split("=")[1]);

        in.lines().forEach(
                line -> {
                    String[] partsString = line.split(",");
                    if (partsString[1].split("=")[0].contains("1")) {
                        phrase = new Phrase(partsString[0]);
                        phrase.setXY(Double.parseDouble(partsString[1].split("=")[1]),
                                Double.parseDouble(partsString[2].split("=")[1])
                        );
                    } else if (partsString[1].split("=")[0].contains("Bx")) {
                        phrase.setX(Double.parseDouble(partsString[1].split("=")[1]),
                                Double.parseDouble(partsString[2].split("=")[1])
                        );
                    } else {
                        phrase.setY(Double.parseDouble(partsString[1].split("=")[1]),
                                Double.parseDouble(partsString[2].split("=")[1])
                        );
                        phrase.calculate(biTotalBg, biTotalFg, uiTotalBg, uiTotalFg);
                        pq.add(phrase);
                        if(pq.size() > 20){
                            pq.poll();
                        }
                    }
                }
        );

        Phrase [] phrases = new Phrase[20];
        for(int i = 0; i < 20; i++){
            phrases[i] = pq.poll();
        }
        StringBuilder s = new StringBuilder();
        for(int i = phrases.length - 1; i >= 0; i--){
            phrase = phrases[i];
            s.append(phrase.phrase)
                    .append("\t")
                    .append(phrase.score)
                    .append("\t")
                    .append(phrase.phraseness)
                    .append("\t")
                    .append(phrase.informativeness);
            if(i != 0){
                s.append("\n");
            }
        }

        pw.println(s.toString());

        pw.flush();
        pw.close();
    }

    private static double informativeness(double biBg, double biFg, double biBgTotal, double biFgTotal) {
        double p = (biFg + 1) / biFgTotal;
        double q = (biBg + 1) / biBgTotal;
        return pointWiseKL(p, q);
    }

    private static double phraseness(double biFg, double biFgTotal, double uniFgX, double uniFgY, double uniFgTotal) {
        double p = (biFg + 1) / biFgTotal;
        double q = ((uniFgX + 1) / uniFgTotal) * ((uniFgY+1) / uniFgTotal);
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

        Phrase(String phrase){
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

    }

    static class ScoreComparator implements Comparator<Phrase> {
        @Override
        public int compare(Phrase x, Phrase y) {
            if (x.score == y.score) {
                return 0;
            } else if (x.score > y.score) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
