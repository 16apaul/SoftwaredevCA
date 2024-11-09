import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CardGame {
    private int gameID; // used to identify what game is running when multiple are
    private int numberOfPlayers; // userinput
    public static String deckLocation; // Used in the test file and is default location
    ArrayList<String> bigDeck;
    private Deck deck;
    private Player player;
    public static List<Player> players = new ArrayList<>();;

    public CardGame() {
        Scanner scanner = new Scanner(System.in); // scanner to get user input

        System.out.print("Enter number of players in game: ");
        numberOfPlayers = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter deck location: ");
        deckLocation = scanner.nextLine();

        scanner.close();
        bigDeck = BigDeck.loadDeck(deckLocation);
        bigDeck.add(deckLocation);
        for (int i = 1; i <= numberOfPlayers; i++) { // i is the card preference and playerID
            List<Integer> startingHand = new ArrayList<>();
            System.out.println(i + " is current loop");
            for (int j = 1; j <= 4; j++) { // inner loop for the getting the starting hand
                startingHand.add(((j * numberOfPlayers) - (numberOfPlayers - 1)) + i - 1); // starting hand gets
                                                                                           // assigned in a round robin
                                                                                           // fashion where each player
                                                                                           // takes every nth card from
                                                                                           // the deck

            }
            System.out.println(i + ":current id");
            player = new Player(i, i);
            player.setStartingHand(startingHand);
            players.add(new Player(i, i));
            System.out.println("added player");

        }
    }

    public static void main(String[] args) {

        /*
         * Print the ArrayList to verify contents
         * for (String line : deck) {
         * System.out.println(line);
         * }
         */
        CardGame cardGame = new CardGame();

    }

    class Player {
        private int playerID; // starts at 1
        // deckOfDrawID
        // deckOfDiscardID
        private List<String> startingHand = new ArrayList<String>();
        private int cardPreference;

        public Player(int playerID, int cardPreference) {

            this.cardPreference = cardPreference;
            this.playerID = playerID;
        }

        public void setStartingHand(List<Integer> cards) {
            for (int card : cards) {
                startingHand.add(bigDeck.get(card - 1));// get card -1 since index starts at 0
                System.out.println("Player ID:" + playerID + " Chose card:" + card + " from the deck and got "
                        + bigDeck.get(card - 1));
            }
            WriteToFile("Player" + playerID+ " Output", "Player Starting hand is:", false);
            WriteArrayToFile("Player" + playerID+ " Output", cards, true);
        }
    }

    class Deck {
        private int deckID;
        private int numberOfCards = 8 * numberOfPlayers;

        public Deck(int deckID) {
            this.deckID = deckID;
        }

        class card {
            private int cardValue;

            public card(int cardValue) {
                this.cardValue = cardValue;

            }

        }
    }

    class BigDeck {

        // Method to load a deck from a file and return it as an ArrayList
        public static ArrayList<String> loadDeck(String filePath) {
            ArrayList<String> deck = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    deck.add(line); // Add each line to the ArrayList
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return deck; // the contents of the deck never changes throughout the game
        }
    }

    public static void WriteToFile(String fileName, String message, boolean append) { // filename should be playerID
                                                                                      // output or DeckID output

        // Create a File object
        File file = new File(fileName);

        try {
            // Create a FileWriter to write to the file
            FileWriter fileWriter = new FileWriter(file, append);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write(message);
            bufferedWriter.newLine(); 

            bufferedWriter.close();

            System.out.println("File written successfully!");

        } catch (IOException e) {
            // Handle any potential exceptions
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void WriteArrayToFile(String fileName, List<Integer> array, boolean append) {
        try (FileWriter fileWriter = new FileWriter(fileName, append);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            // Loop through the array and write each element to the file on the same line
            for (int i = 0; i < array.size(); i++) {
                bufferedWriter.write(String.valueOf(array.get(i)));

                // Add a space or comma between elements, but not after the last element
                if (i < array.size() - 1) {
                    bufferedWriter.write(", ");
                }
            }

            System.out.println("Array written to file successfully!");

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

}
