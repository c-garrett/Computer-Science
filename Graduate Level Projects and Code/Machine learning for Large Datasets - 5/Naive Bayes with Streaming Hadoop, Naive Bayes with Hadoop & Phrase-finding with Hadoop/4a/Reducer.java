package Assignment4a;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by Owner on 11/18/2017.
 */
public class Reducer {

    static PrintWriter pw = new PrintWriter(System.out);

    public static void main(String[] args) throws IOException {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        final String[] label = {null};
        final Long[] count = {null};

        stdin.lines().forEach(
                line -> {
                    String[] parts = line.split("\t");
                    if (parts[0].equals(label[0])) {
                        parts[1] += Long.parseLong(parts[1]);
                    } else {
                        output(label[0], count[0]);
                        label[0] = parts[0];
                        count[0] = Long.parseLong(parts[1]);
                    }
                }
        );

        stdin.close();

        pw.flush();
        pw.close();
    }

    static void output(String label, Long count) {
        if (label != null) {
            pw.println(label + "\t" + count);
        }
    }

}
