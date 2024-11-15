import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CardGame extends Thread {
    public static int numberOfPlayers; // userinput
    public static String deckLocation; // Used in the test file and is default location
    ArrayList<String> pack;
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
            List<String> startingHand = new ArrayList<>(); // gets the index of what cards should be gotten
            System.out.println(i + " is current loop");
            for (int j = 1; j <= 4; j++) { // inner loop for the getting the starting hand, second loop for assigning
                                           // cards
                int index = ((j * numberOfPlayers) - (numberOfPlayers - 1)) + i - 1;
                startingHand.add(String.valueOf(index)); // starting hand gets
                                                         // assigned in a round robin
                                                         // fashion where each player
                                                         // takes every nth card from
                                                         // the deck
                System.out.println(index);
            }
            System.out.println(i + ":current id");
            player = new Player(i, i);
            player.setStartingHand(startingHand);
            
            players.add(player);
            System.out.println("added player");

            

        }
        Pack.SplitDeck(numberOfPlayers); // splits the remaining cards into the decks and creates the decks

        for (Player player : players) {
            System.out.println(player.getStartingHand().size());
        }

        for (Player player : players) { // start the threads
            player.startDrawDiscardThread();
        }
        for (Player player : players) { // join the threads
            try {
                player.joinDrawDiscardThread();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       if (player.playerWon == true){
        for (int i = 1; i <= numberOfPlayers; i++) {
            WriteToFile("Deck" + i + " Output", "final cards are:", false);                 
            WriteArrayToFile("Deck"+i+" Output",decks.get(i-1).getCards() ,true); 
        }}
       

    }
    

    public static void main(String[] args) {

        CardGame cardGame1 = new CardGame();
        
        
    }

    class Player {
        private int playerID; // starts at 1
        // deckOfDrawID
        // deckOfDiscardID
        private List<String> startingHand = new ArrayList<>();
        private int cardPreference;
        private static volatile boolean playerWon = false;  // Shared flag for all threads
        private Thread drawDiscardThread;


        
        public Player(int playerID, int cardPreference) {

            this.cardPreference = cardPreference;
            this.playerID = playerID;
        }

        public void setStartingHand(List<String> cards) {
            for (String card : cards) {
                System.out.println("Player ID:" + playerID + " Chose card:" + card + " from the pack and got "
                        + pack.get(Integer.parseInt(card) - 1));
                        this.startingHand.add(pack.get(Integer.parseInt(card) - 1)) ;
            }
            System.out.println(startingHand.size()+": is starting hand size");
            WriteToFile("Player" + playerID + " Output", "Player Starting hand is: ", false);
            WriteArrayToFile("Player" + playerID + " Output", startingHand, true);
        }

        public List<String> getStartingHand() {
            return new ArrayList<>(startingHand);
        }

        public void startDrawDiscardThread() { // draws from their left which will be their ID
            // then discards to their right the last player

            

           drawDiscardThread = new Thread(() -> {
                int deckOfDrawID = playerID; // what deck player draws from
                int deckOfDiscardID = playerID + 1; // what deck player discads to
                if (deckOfDiscardID > numberOfPlayers) { // discards to deck 1 if deck exceeds player count
                    deckOfDiscardID = 1;

                }
                List<String> hand = getStartingHand();
                String currentCard;
                Deck currentDeck;
                String deckCard; // remove from deck add to player
                String playerCard; // remove from player add to deck

                

                synchronized (this) { 
                    if (startingHand.isEmpty()) {
                        System.out.println("Error: Starting hand is empty for player " + playerID);
                        return;
                    }
                }
                while (playerWon == false) {

                    
                    boolean allCardsMatch = hand.stream().allMatch(card -> card.equals(hand.get(0)));

                    if (allCardsMatch) {
                        System.out.println("Player " + playerID + " has won!");
                        WriteToFile("Player" + playerID + " output","Winning hand is: ",true);

                        playerWon= true;
                        break;
                    }


                    for (int index = 0; index < 4; index++) { // loop goes through the cards
                        
                       
                        currentCard = hand.get(index); // get the specidied card
                        if (currentCard.equals(String.valueOf(cardPreference))) { // do nothing if card preference
                            System.out.println("this is running");
                        } else {
                            System.out.println(currentCard + cardPreference);

                            synchronized (decks.get(deckOfDrawID - 1)) {
                                currentDeck = decks.get(deckOfDrawID - 1);// get deck player draws from
                                deckCard = currentDeck.getCards().getLast(); // gets the last card of the deck
                                WriteToFile("Player" + playerID + " output",
                                        "Player " + playerID + " draws from deck " + deckOfDrawID + " and draws "
                                                + deckCard,
                                        true);
                                currentDeck.removeCard(); // removes the last card from the deck
                                hand.add(deckCard); // places card to hand


                            }

                            synchronized (decks.get(deckOfDiscardID - 1)) {
                                currentDeck = decks.get(deckOfDiscardID - 1);// get deck player draws from

                                playerCard = hand.get(index); // stores card that player discards
                                WriteToFile("Player" + playerID + " output",
                                        "Player " + playerID + " discards to deck " + deckOfDiscardID + " and discards "
                                                + playerCard,
                                        true);
                                hand.remove(index); // removes the card
                                currentDeck.addCard(playerCard); // places that card in the deck at the bottom

                            }

                        }
                        allCardsMatch = hand.stream().allMatch(card -> card.equals(hand.get(0)));

                        if (allCardsMatch) {
                            System.out.println("Player " + playerID + " has won!");
                            WriteToFile("Player" + playerID + " output","Winning hand is: ",true);
    
                            playerWon= true;
                            break;
                        }
                    }

                    
                }
                WriteToFile("Player" + playerID + " output", "final hand:",true);

                WriteArrayToFile("Player" + playerID + " output", hand,true);
                System.out.println("Thread has stopped for ID:" + playerID);

            });
            drawDiscardThread.start();
            
            
        }
        public void joinDrawDiscardThread() throws InterruptedException {
            if (drawDiscardThread != null) {
                drawDiscardThread.join();
            }}
        

        public int getPlayerID() {
            return playerID;
        }

        public int getCardPreference() {
            return cardPreference;
        }

    
    }

    public static class Deck {
        private int deckID;
        private List<String> cards = new ArrayList<>(); // stores string because makes it easier to out it

        public Deck(int deckID) {
            this.deckID = deckID;
        }

        public synchronized void addCard(String cardNumber) {
            cards.add(0, cardNumber); // should add card to the bottom of the deck, The most recent card in the list

        }

        public synchronized void removeCard() {
            cards.removeLast(); // should remove card to the top of the deck, The most recent card in the list

        }

        public List<String> getCards() {
            return cards;

        }

        public int getDeckID() {
            return deckID;

        }

    }

    class Pack {
        public static List<Deck> Decks = new ArrayList<>();

        // Method to load a deck from a file and return it as an ArrayList
        public static ArrayList<String> loadDeck(String filePath) {
            ArrayList<String> pack = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    pack.add(line); // Add each line to the ArrayList
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return pack; // the contents of the deck never changes throughout the game

        }

        public static void SplitDeck(Integer numberOfPlayers) { // splits the big deck into smaller decks of the
                                                                // required size without accounting for starting hand
            List<String> startingDeck = new ArrayList<>();
            List<String> remainingCards = new ArrayList<>();
            Deck deck;

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
                    // deck gets assigned cards in a round robin fashion
                    String card = remainingCards.get(((j * numberOfPlayers) - (numberOfPlayers - 1)) + i - 2);
                    deck.addCard(card);
                    System.out.println("card:" + card + " has been successfully added to deck:" + i);

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

            System.out.println("File written successfully "+fileName);

        } catch (IOException e) {
            // Handle any potential exceptions
            System.out.println("An error occurred while writing to the file");
            e.printStackTrace();
        }
    }

    public static void WriteArrayToFile(String fileName, List<String> array, boolean append) {
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
            bufferedWriter.newLine();

            System.out.println("Array written to file successfully "+fileName);

        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        System.out.println(player.getPlayerID() + " is running in a separate thread.");

        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }

}
