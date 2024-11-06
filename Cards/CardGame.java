import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CardGame {
    private int gameID;
    private int numberOfPlayers;
    public static String deckLocation = "Deck.txt";
    ArrayList<String> bigDeck = BigDeck.loadDeck(deckLocation);
    private Deck deck;
    private Player player;

    public CardGame(int numberOfPlayers, String deckLocation ) {
        this.numberOfPlayers = numberOfPlayers;
        this.deck = new Deck(1);
        this.player = new Player(1, player.setStartingHand(), 1);
    }

    public static void main(String[] args) {
        ArrayList<String> deck = BigDeck.loadDeck("Deck.txt");

        // Print the ArrayList to verify contents
        for (String line : deck) {
            System.out.println(line);
        }

    }


    class Player {
        private int PlayerID;
        // deckOfDrawID
        // deckOfDiscardID
        private List<Integer> startingHand = new ArrayList<Integer>();
        private int cardPreference;


        public Player(int playerID, List<Integer> startingHand, int cardPreference) {


            this.cardPreference = cardPreference;
            this.PlayerID = playerID;
        }

        public List<Integer> setStartingHand() {
            startingHand.add(1);
            startingHand.add(1);
            startingHand.add(1);
            return startingHand;

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
                    deck.add(line);  // Add each line to the ArrayList
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return deck;
        }
    }

}

