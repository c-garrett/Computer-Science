import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by Owner on 11/16/2017.
 */
public class MessageUnigramCombiner {
    public static void main(String [] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(System.out);

        pw.println(in.readLine());

        final String[] prevKey = new String[1];
        final Integer[] values = new Integer[2];
        in.lines().forEach(
                line -> {
                    if(line.contains("=")){
                        String [] parts = line.split(",");
                        prevKey[0] = parts[0];
                        values[0] = Integer.parseInt(parts[1].split("=")[1]);
                        values[1] = Integer.parseInt(parts[2].split("=")[1]);
                    }else{
                        if(line.split(",")[1].split(" ")[0].equals(prevKey[0])){
                            pw.println(String.format("%s, Bx=%d,Cx=%d",line.split(",")[1],values[0],values[1]));
                        }else{
                            pw.println(String.format("%s, By=%d,Cy=%d",line.split(",")[1],values[0],values[1]));
                        }
                    }
                }
        );
        pw.flush();
        pw.close();
    }
}
