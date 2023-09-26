import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class RefactorReadFile {

    public static void main(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                String s = "";
                BufferedReader br = new BufferedReader(new FileReader(args[i]));
                String line = br.readLine();
                while (line != null) {
                    s = s + line + "\n";
                    line = br.readLine();
                }
                String[] words = s.split("\\s|\\(|\\)|\\.|\\[|\\]|,|\\+|;|\\\\|\"|!|\\||/|=|\\*|@|<|>");
                ArrayList<String> uniqueWords = new ArrayList<>();
                for (int j = 0; j < words.length; j++) {
                    String testedWord = words[j];
                    int count = 0;
                    for (int k = 0; k < words.length; k++) {
                        if (testedWord.equals(words[k]))
                            count++;
                    }
                    if (count == 1 && !uniqueWords.contains(testedWord))
                        uniqueWords.add(testedWord);
                }
                Collections.sort(uniqueWords);

                for (int j = 0; j < uniqueWords.size(); j++) {
                    System.out.println(uniqueWords.get(j));
                }
                br.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            //невозможно
        }
    }
}
