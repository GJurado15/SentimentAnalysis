import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class SentimentAnalysis {

    public static void main(String[] args) {
        HashTable<String, Integer> sentiments = new HashTable<>(16);

        // load word,score pairs from the sentiment file
        try (Scanner file = new Scanner(new File("sentiments.txt"))) {
            while (file.hasNextLine()) {
                String line = file.nextLine().trim();
                int comma = line.lastIndexOf(',');
                if (comma < 0) {
                    continue;
                }
                String word = line.substring(0, comma).trim().toLowerCase();
                int score = Integer.parseInt(line.substring(comma + 1).trim());
                sentiments.put(word, score);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Could not find sentiments.txt");
            return;
        }

        // read input until the user types END
        Scanner in = new Scanner(System.in);
        System.out.println("Enter text:");

        int wordCount = 0;
        int total = 0;
        String previous = null;   // previous word, used to match two-word phrases

        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line.trim().equals("END")) {
                break;
            }

            // strip punctuation, lower-case, and split on whitespace
            String[] words = line.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }
                wordCount++;

                Integer score = sentiments.get(word);
                if (score != null) {
                    total += score;
                }

                if (previous != null) {
                    Integer phraseScore = sentiments.get(previous + " " + word);
                    if (phraseScore != null) {
                        total += phraseScore;
                    }
                }
                previous = word;
            }
        }
        in.close();

        double average = wordCount == 0 ? 0 : (double) total / wordCount;
        System.out.println("Number of words: " + wordCount);
        System.out.println("Total sentiment: " + total);
        System.out.printf("Average sentiment: %.2f%n", average);
    }
}
