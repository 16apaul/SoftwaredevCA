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
    public static int numberOfPlayers; // userinput
    public static String deckLocation; // Used in the test file and is default location
    ArrayList<String> pack;
    private Deck deck;
    private Player player;
    public static List<Player> players = new ArrayList<>();
    public static List<Deck> decks = new ArrayList<>();

    public CardGame() {
        Scanner scanner = new Scanner(System.in); // scanner to get user input

        System.out.print("Enter number of players in game: ");
        numberOfPlayers = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter deck location: ");
        deckLocation = scanner.nextLine();

        scanner.close();
        pack = Pack.loadDeck(deckLocation);
        pack.add(deckLocation);
        for (int i = 1; i <= numberOfPlayers; i++) { // i is the card preference and playerID, first loop goes through
                                                     // number of players
            List<Integer> startingHand = new ArrayList<>(); // gets the index of what cards should be gotten
            System.out.println(i + " is current loop");
            for (int j = 1; j <= 4; j++) { // inner loop for the getting the starting hand, second loop for assigning
                                           // cards
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

            Pack.SplitDeck(numberOfPlayers); // splits the remaining cards into the decks

        }
    }

    public static void main(String[] args) {

        
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
                startingHand.add(pack.get(card - 1));// get card -1 since index starts at 0
                System.out.println("Player ID:" + playerID + " Chose card:" + card + " from the deck and got "
                        + pack.get(card - 1));
            }
            WriteToFile("Player" + playerID + " Output", "Player Starting hand is:", false);
            WriteArrayToFile("Player" + playerID + " Output", cards, true);
        }
    }

    public static class Deck {
        private int deckID;
        private int numberOfCards = 8 * numberOfPlayers;
        private List<String> cards = new ArrayList<>(); // stores string because makes it easire to out it

        public Deck(int deckID) {
            this.deckID = deckID;
        }

        public void addCard(String cardNumber) {
            cards.add(cardNumber); // should add card to the top of the deck, The most recent card in the list

        }

        public void removeCard() {
            cards.removeLast(); // should remove card to the top of the deck, The most recent card in the list

        }

        public List<String> getCards() {
            return cards;

        }

    }

    class Pack {
        public static List<Deck> Decks = new ArrayList<>();

        // Method to load a deck from a file and return it as an ArrayList
        public static ArrayList<String> loadDeck(String filePath) throws IllegalArgumentException {
            ArrayList<String> pack = new ArrayList<>();

            

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    pack.add(line); // Add each line to the ArrayList
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (pack.size() == numberOfPlayers*8 ){

                return pack; // the contents of the deck never changes throughout the game

            } else{

                throw new IllegalArgumentException("The deck size should be 8 * number of players inputted"); // error if not
            }
        

        }

        public static void SplitDeck(Integer numberOfPlayers) { // splits the big deck into smaller decks of the
                                                                // required size without accounting for starting hand
            ArrayList<String> startingDeck = new ArrayList<>();
            ArrayList<String> remainingCards = new ArrayList<>();
            Deck deck;
            List<Integer> startingCards = new ArrayList<>(); // gets index of what cards should get drawn from big deck

            startingDeck = Pack.loadDeck(deckLocation);

            for (int index = (startingDeck.size() / 2) + 1; index <= startingDeck.size(); index++) { // loop to get the
                                                                                                     // remaining cards
                                                                                                     // after the
                                                                                                     // starting hands
                                                                                                     // has been
                                                                                                     // assigned
                System.out.println("remaining cards are:" + startingDeck.get(index - 1));
                remainingCards.add(startingDeck.get(index - 1));
            }
            // loop should go through the remaing cards and split it to the decks
            for (int i = 1; i <= numberOfPlayers; i++) { // first loop shold create as many decks as players
                deck = new Deck(i); 
                decks.add(deck);
                System.out.println("created deck with ID:" + i);
                for (int j = 1; j <= 4; j++) { // second loop assigns them cards
                    startingCards.add(((j * numberOfPlayers) - (numberOfPlayers - 1)) + i - 2); // deck gets assigned
                                                                                                // cards in a round
                                                                                                // robin fashion
                    String card = remainingCards.get(((j * numberOfPlayers) - (numberOfPlayers - 1)) + i - 2);
                    deck.addCard(card);
                    System.out.println("card:"+ card + " has been successfully added to deck:"+ i);


                }

            }

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

            System.out.println("File written successfully");

        } catch (IOException e) {
            // Handle any potential exceptions
            System.out.println("An error occurred while writing to the file");
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

            System.out.println("Array written to file successfully");

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file");
            e.printStackTrace();
        }
    }

}
