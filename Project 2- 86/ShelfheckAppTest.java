import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for ShelfCheckApp: each test feeds the menu a scripted session
 * from a String and checks what was printed and what the inventory holds.
 *
 * @author Karthik Mittadhoddi (drafted with GenAI - see README)
 * @version October 2026
 */
public class ShelfCheckAppTest
{
    private PrintStream originalOut;
    private InputStream originalIn;
    private ByteArrayOutputStream output;
    private Inventory inventory;

    /**
     * Captures System.out and starts each test with the starting stock.
     */
    @BeforeEach
    public void setUp()
    {
        originalOut = System.out;
        originalIn = System.in;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        inventory = new Inventory();
        inventory.loadStartingStock();
    }

    /**
     * Restores System.out and System.in after each test.
     */
    @AfterEach
    public void tearDown()
    {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /**
     * Runs the menu with the given typed lines.
     *
     * @return everything the app printed
     */
    private String run(String typed)
    {
        new ShelfCheckApp(inventory, new Scanner(typed)).run();
        return output.toString();
    }

    // ---------------- constructor + run ----------------

    @Test
    public void testRunShowsMenuAndQuits()
    {
        String printed = run("0\n");
        assertTrue(printed.contains("ShelfCheck - Grocery Stock Tracker"));
        assertTrue(printed.contains("1 Add stock"));
        assertTrue(printed.contains("Goodbye."));
    }

    @Test
    public void testRunEndOfInput()
    {
        // Ctrl+D: no more input, the app exits cleanly instead of crashing
        assertTrue(run("").contains("Goodbye."));
    }

    @Test
    public void testRunBadMenuChoice()
    {
        String printed = run("x\n12\n0\n");
        assertTrue(printed.contains("Enter a whole number."));
        assertTrue(printed.contains("Enter a number from 0 to 9."));
        assertTrue(printed.contains("Goodbye."));
    }

    // ---------------- 1 Add stock ----------------

    @Test
    public void testAddStock()
    {
        String printed = run("1\neggs\n12\n2.99\n0\n");
        assertTrue(printed.contains("Added 12 eggs."));
        assertEquals(12, inventory.getStock("eggs"));
    }

    @Test
    public void testAddStockBadInput()
    {
        // bad name, bad quantity, NaN price, then a clashing price for milk
        String printed = run("1\n123\nmilk\nabc\n5\nNaN\n3.29\n0\n");
        assertTrue(printed.contains("Item name must contain at least one letter."));
        assertTrue(printed.contains("Enter a whole number."));
        assertTrue(printed.contains("Price must be between"));
        assertTrue(printed.contains("milk is already priced at $3.49."));
        assertEquals(20, inventory.getStock("milk"));
    }

    // ---------------- 2 New sale ----------------

    @Test
    public void testNewSale()
    {
        String printed = run("2\nmilk\n2\ny\nbread\n1\nn\ny\n0\n");
        assertTrue(printed.contains("TOTAL $9.97"));
        assertEquals(18, inventory.getStock("milk"));
        assertEquals(24, inventory.getStock("bread"));
        assertEquals(1, inventory.getSaleCount());
    }

    @Test
    public void testNewSaleNotEnoughStock()
    {
        String printed = run("2\nmilk\n50\nn\n0\n");
        assertTrue(printed.contains("Only 20 milk in stock."));
        assertTrue(printed.contains("Sale cancelled: the cart is empty."));
        assertEquals(0, inventory.getSaleCount());
    }

    @Test
    public void testNewSaleCancelled()
    {
        String printed = run("2\nmilk\n2\nn\nn\n0\n");
        assertTrue(printed.contains("Sale cancelled."));
        assertEquals(20, inventory.getStock("milk"));
    }

    @Test
    public void testNewSaleEmptyInventory()
    {
        inventory = new Inventory();
        assertTrue(run("2\n0\n").contains("Inventory is empty."));
    }

    @Test
    public void testNewSaleLowStockAlert()
    {
        String printed = run("2\nmilk\n16\nn\ny\n0\n");
        assertTrue(printed.contains("LOW STOCK ALERT:"));
        assertTrue(printed.contains("milk: 4 left (minimum 5)"));
    }

    // ---------------- 3 Look up item ----------------

    @Test
    public void testLookUp()
    {
        assertTrue(run("3\n MILK \n0\n").contains("milk: 20 in stock"));
    }

    @Test
    public void testLookUpMissing()
    {
        assertTrue(run("3\nsoap\n0\n").contains("soap: 0 in stock"));
    }

    // ---------------- 4-7 reports ----------------

    @Test
    public void testReports()
    {
        String printed = run("2\nmilk\n2\nn\ny\n4\n5\n6\n7\n0\n");
        assertTrue(printed.contains("milk: 18 @ $3.49"));
        assertTrue(printed.contains("Total units in the store: 138"));
        assertTrue(printed.contains("Sales: 1, revenue: $6.98"));
        assertTrue(printed.contains("1. milk: 2 sold"));
    }

    @Test
    public void testReportsWithNoSales()
    {
        String printed = run("6\n7\n0\n");
        assertTrue(printed.contains("No sales yet."));
        assertTrue(printed.contains("Sales: 0, revenue: $0.00"));
    }

    // ---------------- 8 Low stock / 9 Set minimum ----------------

    @Test
    public void testSetMinimumAndLowStock()
    {
        String printed = run("9\nmilk\n25\n8\n0\n");
        assertTrue(printed.contains("Alert set: warn when milk drops below 25."));
        assertTrue(printed.contains("milk: 20 left (minimum 25)"));
    }

    @Test
    public void testSetMinimumBad()
    {
        String printed = run("8\n9\nsoap\n5\n9\nmilk\n0\n3\n0\n");
        assertTrue(printed.contains("Nothing is low."));
        assertTrue(printed.contains("Item not found: soap"));
        assertTrue(printed.contains("Enter a number from 1 to 1000."));
        assertEquals(3, inventory.getMinimum("milk"));
    }

    // ---------------- main ----------------

    @Test
    public void testMainLoadsStartingStock()
    {
        System.setIn(new ByteArrayInputStream("5\n0\n".getBytes()));
        ShelfCheckApp.main(new String[0]);
        assertTrue(output.toString().contains("Total units in the store: 140"));
    }

    @Test
    public void testMainEndOfInput()
    {
        System.setIn(new ByteArrayInputStream(new byte[0]));
        ShelfCheckApp.main(new String[0]);
        assertTrue(output.toString().contains("Goodbye."));
    }
}
