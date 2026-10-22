import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for InputReader: one normal and one bad-input case per public
 * method. Input comes from a Scanner built from a String, and printed
 * prompts are captured so the re-prompt messages can be checked.
 *
 * @author Karthik Mittadhoddi (drafted with GenAI - see README)
 * @version October 2026
 */
public class InputReaderTest
{
    private PrintStream originalOut;
    private ByteArrayOutputStream output;

    /**
     * Captures System.out before each test.
     */
    @BeforeEach
    public void setUp()
    {
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    /**
     * Restores System.out after each test.
     */
    @AfterEach
    public void tearDown()
    {
        System.setOut(originalOut);
    }

    /**
     * @return a Scanner that reads the given lines
     */
    private Scanner input(String text)
    {
        return new Scanner(text);
    }

    // ---------------- readInt ----------------

    @Test
    public void testReadInt()
    {
        assertEquals(5, InputReader.readInt(input("5\n"), "Qty: ", 1, 1000));
        assertEquals(1000,
            InputReader.readInt(input(" 1000 \n"), "Qty: ", 1, 1000));
    }

    @Test
    public void testReadIntNotANumber()
    {
        int value = InputReader.readInt(
            input("abc\n2.5\n1,000\n99999999999\n7\n"), "Qty: ", 1, 1000);
        assertEquals(7, value);
        assertTrue(output.toString().contains("Enter a whole number."));
    }

    @Test
    public void testReadIntOutOfRange()
    {
        int value = InputReader.readInt(input("0\n1001\n-5\n9\n"),
            "Qty: ", 1, 1000);
        assertEquals(9, value);
        assertTrue(output.toString().contains("Enter a number from 1 to 1000."));
    }

    @Test
    public void testReadIntEndOfInput()
    {
        assertThrows(NoSuchElementException.class,
            () -> InputReader.readInt(input(""), "Qty: ", 1, 1000));
    }

    // ---------------- readPrice ----------------

    @Test
    public void testReadPrice()
    {
        assertEquals(3.49, InputReader.readPrice(input("3.49\n"), "$"),
            0.0001);
        assertEquals(0.01, InputReader.readPrice(input("0.01\n"), "$"),
            0.0001);
    }

    @Test
    public void testReadPriceNotANumber()
    {
        double value = InputReader.readPrice(input("abc\n2\n"), "$");
        assertEquals(2.0, value, 0.0001);
        assertTrue(output.toString().contains("Enter a price like 3.49."));
    }

    @Test
    public void testReadPriceOutOfRange()
    {
        double value = InputReader.readPrice(
            input("NaN\nInfinity\n0\n-1\n10000.01\n2\n"), "$");
        assertEquals(2.0, value, 0.0001);
        assertTrue(output.toString().contains(
            "Price must be between $0.01 and $10,000.00."));
    }

    // ---------------- readName ----------------

    @Test
    public void testReadName()
    {
        assertEquals("Milk", InputReader.readName(input("  Milk  \n"), "Name: "));
        assertEquals("7up", InputReader.readName(input("7up\n"), "Name: "));
    }

    @Test
    public void testReadNameBlank()
    {
        String name = InputReader.readName(input("   \neggs\n"), "Name: ");
        assertEquals("eggs", name);
        assertTrue(output.toString().contains("Item name can't be empty."));
    }

    @Test
    public void testReadNameNoLetterOrTooLong()
    {
        String name = InputReader.readName(
            input("3\n" + "x".repeat(31) + "\nbread\n"), "Name: ");
        assertEquals("bread", name);
        String printed = output.toString();
        assertTrue(printed.contains("Item name must contain at least one letter."));
        assertTrue(printed.contains("Item name must be 30 characters or fewer."));
    }

    // ---------------- readYesNo ----------------

    @Test
    public void testReadYesNo()
    {
        assertTrue(InputReader.readYesNo(input("Y\n"), "? "));
        assertTrue(InputReader.readYesNo(input("yes\n"), "? "));
        assertFalse(InputReader.readYesNo(input("n\n"), "? "));
        assertFalse(InputReader.readYesNo(input("NO\n"), "? "));
    }

    @Test
    public void testReadYesNoBad()
    {
        assertFalse(InputReader.readYesNo(input("maybe\n\nn\n"), "? "));
        assertTrue(output.toString().contains("Answer y or n."));
    }
}
