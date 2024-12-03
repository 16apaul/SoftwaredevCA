import java.util.ArrayList;
import java.util.InputMismatchException;
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
    ArrayList<String> pack; // stores contents of pack
    private Player player; // player class that will be created andd added to the players list
    public static List<Player> players = new ArrayList<>(); // stores all players in the game
    public static List<Deck> decks = new ArrayList<>(); // stores all decks in the game
    boolean validInput = false; // boolean to check if user entered valid input
    boolean validPack = false; // to check if user entered valid pack

    public CardGame() {
        Scanner scanner = new Scanner(System.in); // scanner to get user input
        while (!validInput) {

            try { // this try statement makes sure user only enters numbers and the numbers are
                  // above 0
                System.out.print("Enter number of players in game: ");
                numberOfPlayers = scanner.nextInt();
                if (numberOfPlayers <= 0) {
                    System.out.println("Number of players must be greater than 0.");
                } else {
                    validInput = true; // Exit the loop if input is valid
                }

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.next(); // Clear the invalid input from the scanner
            }
        }

        scanner.nextLine();

        while (!validPack) {
            System.out.print("Enter deck location: ");
            deckLocation = scanner.nextLine(); // Read user input
            pack = Pack.loadDeck(deckLocation); // Load the pack

            // Check if the pack is empty
            if (pack == null || pack.isEmpty()) {
                System.out.println("Invalid pack: pack is empty or does not exist.");
                continue; // Restart the loop
            }

            // Check if the pack has the correct number of cards
            if (pack.size() != 8 * numberOfPlayers) {
                System.out.println("Invalid pack: number of cards should be 8 * number of players.");
                continue; // Restart the loop
            }

            // Check if all cards in the pack are integers
            boolean allCardsAreIntegers = true;
            for (String card : pack) {
                try {
                    Integer.parseInt(card); // check if card is integer
                } catch (NumberFormatException e) {
                    System.out.println("Invalid pack: not all cards in the pack are integers.");
                    allCardsAreIntegers = false;
                    break; // Exit the loop if one card is invalid
                }
            }

            // If checks pass, mark the pack as valid
            if (allCardsAreIntegers) {
                validPack = true;
                System.out.println("Pack is valid!");
            }
        }

        scanner.close();
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
        if (player.playerWon == true) {
            for (int i = 1; i <= numberOfPlayers; i++) {
                WriteToFile("Deck" + i + " Output", "final cards are:", false); // if any player won all the final cards
                                                                                // of deck is shown
                WriteArrayToFile("Deck" + i + " Output", decks.get(i - 1).getCards(), true);
            }
        }

    }

    public static void main(String[] args) {

        CardGame cardGame1 = new CardGame();

    }

    class Player {
        private int playerID; // starts at 1
        private List<String> startingHand = new ArrayList<>(); // The hand players starts with
        private int cardPreference;
        private static volatile boolean playerWon = false; // Shared flag for all threads
        private Thread drawDiscardThread;

        public Player(int playerID, int cardPreference) {

            this.cardPreference = cardPreference;
            this.playerID = playerID;
        }

        public void setStartingHand(List<String> cards) {
            for (String card : cards) {
                System.out.println("Player ID:" + playerID + " Chose card:" + card + " from the pack and got "
                        + pack.get(Integer.parseInt(card) - 1));
                this.startingHand.add(pack.get(Integer.parseInt(card) - 1));
            }
            System.out.println(startingHand.size() + ": is starting hand size");
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
                String currentCard; // card that the player is deciding to keep or discard
                Deck currentDeck; // deck that the player is drawing from or discarding to
                String deckCard; // remove from deck add to player
                String playerCard; // remove from player add to deck

                synchronized (this) {
                    if (startingHand.isEmpty()) {
                        System.out.println("Error: Starting hand is empty for player " + playerID);
                        return;
                    }
                }
                while (playerWon == false) {

                    boolean allCardsMatch = hand.stream().allMatch(card -> card.equals(hand.get(0)));// if card match
                                                                                                     // the first index

                    if (allCardsMatch) {
                        System.out.println("Player " + playerID + " has won!");
                        WriteToFile("Player" + playerID + " output", "Winning hand is: ", true);

                        playerWon = true;
                        break;
                    }

                    for (int index = 0; index < 4; index++) { // loop goes through the cards in player hand

                        currentCard = hand.get(index); // get the specified card
                        if (currentCard.equals(String.valueOf(cardPreference))) { // do nothing if card preference

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
                                if (index < 3) {
                                    hand.add(index + 1, deckCard); // places card to hand between where card is going to
                                                                   // be
                                                                   // removed and next card of the player this makes
                                                                   // sure
                                                                   // players don't
                                                                   // hold on to a card indefinately

                                } else if (index == 3) { // if last card add it on the end
                                    hand.add(deckCard);
                                }

                            }

                            synchronized (decks.get(deckOfDiscardID - 1)) {
                                currentDeck = decks.get(deckOfDiscardID - 1);// get deck player draws from

                                playerCard = hand.get(index); // stores card that player discards
                                WriteToFile("Player" + playerID + " output",
                                        "Player " + playerID + " discards to deck " + deckOfDiscardID + " and discards "
                                                + playerCard,
                                        true);
                                hand.remove(index); // removes the card from the index
                                currentDeck.addCard(playerCard); // places that card in the deck at the bottom

                            }

                        }
                        allCardsMatch = hand.stream().allMatch(card -> card.equals(hand.get(0)));

                        if (allCardsMatch) {
                            System.out.println("Player " + playerID + " has won!");
                            WriteToFile("Player" + playerID + " output", "Winning hand is: ", true); // writes to the
                                                                                                     // specified file
                                                                                                     // who won

                            playerWon = true;
                            break;
                        }
                    }

                }
                WriteToFile("Player" + playerID + " output", "final hand:", true);

                WriteArrayToFile("Player" + playerID + " output", hand, true);
                System.out.println("Thread has stopped for ID:" + playerID);

            });
            drawDiscardThread.start();

        }

        public void joinDrawDiscardThread() throws InterruptedException {
            if (drawDiscardThread != null) {
                drawDiscardThread.join();
            }
        }

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
        public void addStartingCard(String cardNumber){ // this is run at the start of the game to add cards to the deck in a round robin
            cards.add(cardNumber);

        }
        public synchronized void addCard(String cardNumber) {
            cards.add(0, cardNumber); // should add card to the bottom of the deck when removed from player's hand 

        }

        public synchronized void removeCard() {
            cards.removeLast(); // should remove card to the top of the deck when player want to draw

        }

        public List<String> getCards() {
            return cards;

        }

        public int getDeckID() {
            return deckID;

        }

    }

    class Pack {

        // Method to load a deck from a file and return it as an ArrayList
        public static ArrayList<String> loadDeck(String filePath) {
            boolean validPack = false;
            ArrayList<String> pack = new ArrayList<>(); // reads file a puts all values in one arraylist

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    pack.add(line); // Add each line to the ArrayList
                }
            } catch (IOException e) {
                System.out.println("Enter valid path");
                e.printStackTrace();
            }

            return pack; // the contents of the deck never changes throughout the game

        }

        public static void SplitDeck(Integer numberOfPlayers) { // splits the pack into smaller decks of the
                                                                // required size without accounting for the starting hand of the players
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
                    deck.addStartingCard(card);
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

            System.out.println("File written successfully " + fileName);

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

            System.out.println("Array written to file successfully " + fileName);

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
