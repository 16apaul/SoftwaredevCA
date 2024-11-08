import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class CardGame {
    private int gameID; // used to identify what game is running when multiple are
    private int numberOfPlayers; // userinput
    public static String deckLocation = "Deck.txt"; // Used in the test file and is default location
    ArrayList<String> bigDeck = BigDeck.loadDeck(deckLocation);
    private Deck deck;
    private Player player;
    public static List<Player> players;

    public CardGame(int numberOfPlayers, String deckLocation ) {
        this.numberOfPlayers = numberOfPlayers;
        bigDeck.add(deckLocation); 
        for (int i = 1; i <= numberOfPlayers; i++) { // i is the card preference and playerID 
            List<Integer> startingHand = new ArrayList<>();

            for (int j = 1; j <= 4; j++) { // inner loop for the getting the starting hand
                startingHand.add(((j*numberOfPlayers)-(numberOfPlayers-1))+i-1); // starting hand gets assigned in a round robin fashion where each player takes every nth card from the deck 
           
            }
            System.out.println(i+":current id");
            player = new Player(i,i);
            player.setStartingHand(startingHand);
            players.add(player);

        }
    }

    public static void main(String[] args) {
        ArrayList<String> deck = BigDeck.loadDeck("Deck.txt");

        /*  Print the ArrayList to verify contents
        for (String line : deck) {
            System.out.println(line);
        }*/
        CardGame cardGame = new CardGame(2, deckLocation);

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

        public List<String> setStartingHand( List<Integer> cards) {
            for (int card : cards) {
                startingHand.add(bigDeck.get(card));
                System.out.println("Player ID:"+playerID+" got card"+ card);
            }

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

