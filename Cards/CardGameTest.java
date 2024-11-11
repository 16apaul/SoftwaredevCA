import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class CardGameTest {
    int numberOfPlayers = 2; // test how many people you want playing
    
    String filePath = "deck.txt"; // put the file location here to test
    ArrayList<String> deck = CardGame.Pack.loadDeck(filePath);

    @Test

    public void testLoadDeck_FileFormatted() throws IOException {
        // Specify the path to the existing deck file
         // Update this if the file is located elsewhere

        // Load the deck from the specified file

        // Print the result

        for (String card : deck) {

            try {
                Integer.parseInt(card); // integer is number
            } catch (NumberFormatException e) {
                fail("not all cards in pack are numbers");
            }
        }

        assertNotNull(deck);
        assertFalse("The deck should not be empty", deck.isEmpty());

        // Example assertions; modify these based on the actual content of deck.txt
        assertTrue("Number of card in deck should be a multiple of 8",deck.size() % 8 == 0);

    }

    @Test
    public void testNumberOfplayers(){


    
        assertTrue("Number of card in deck should be  8* number of players",deck.size() == numberOfPlayers *8);


    }

    @Test
    public void TestSplitPack(){

        CardGame.Pack.SplitDeck(numberOfPlayers);
        assertTrue("Decks created is not same as players created" , CardGame.decks.size() ==numberOfPlayers);


    }

    @Test
    public void TestCardsInEachDeck(){
        

        for (CardGame.Deck deck :CardGame.decks ) {
            assertTrue("Make sure 4 cards in each deck", deck.getCards().size() == 4);
            for (int i = 0; i < 4; i++) {
                try {
                    Integer.parseInt(deck.getCards().get(i)); // integer is number
                } catch (NumberFormatException e) {
                    fail("not all cards in deck are numbers");
                }
            }
            

        }


    }

    @Before
    public void setUp() throws Exception {
        CardGame.numberOfPlayers = numberOfPlayers;
        CardGame.deckLocation = filePath;

        

    }

    @After
    public void tearDown() throws Exception {
    }
}
