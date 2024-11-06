import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class CardGameTest {


    @Test
    public void testLoadDeck_FileExists() throws IOException {
        // Specify the path to the existing deck file
        String filePath = CardGame.deckLocation;  // Update this if the file is located elsewhere

        // Load the deck from the specified file
        ArrayList<String> deck = CardGame.BigDeck.loadDeck(filePath);

        // Print the result

        for (String card : deck) {

            try {
                int number = Integer.parseInt(card); // integer is number
            } catch (NumberFormatException e) {
                fail("not all cards in deck are numbers");
            }
        }

        assertNotNull(deck);
        assertFalse("The deck should not be empty", deck.isEmpty());

        // Example assertions; modify these based on the actual content of deck.txt
        assertTrue("",deck.size() % 8 == 0);

    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
}