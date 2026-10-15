import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for Inventory: one normal and one bad-input case per public
 * method, following the Inventory rows of our test plan.
 *
 * @author Thomas Cherian 
 * @version October 2026
 */
public class InventoryTest
{
    private Inventory s;
    private Inventory e;
    private LocalDateTime t;

    /**
     * S = two batches of 24 milk at $3.49 and one batch of 12 eggs at
     * $2.99 (48 milk, 60 units, no sales); E = a new, empty inventory;
     * t = 2026-10-06 17:05:12.
     */
    @BeforeEach
    public void setUp()
    {
        s = new Inventory();
        s.addStock("milk", 24, 3.49);
        s.addStock("milk", 24, 3.49);
        s.addStock("eggs", 12, 2.99);
        e = new Inventory();
        t = LocalDateTime.of(2026, 10, 6, 17, 5, 12);
    }

    /**
     * Builds a cart from alternating names and quantities, priced from s.
     */
    private LinkedChain<Item> cart(Object... nameQty)
    {
        LinkedChain<Item> cart = new LinkedChain<Item>();
        for (int i = 0; i < nameQty.length; i += 2)
        {
            cart.addToEnd(s.quote((String)nameQty[i], (Integer)nameQty[i + 1]));
        }
        return cart;
    }

    // ---------------- constructor ----------------

    @Test
    public void testConstructor()
    {
        assertEquals(0, e.getTotalUnits());
        assertEquals(0, e.getSaleCount());
    }

    @Test
    public void testConstructorEmptyReports()
    {
        assertEquals("Inventory is empty.", e.getInventoryReport());
        assertEquals("No sales yet.", e.getSalesReport());
    }

    // ---------------- addStock ----------------

    @Test
    public void testAddStock()
    {
        e.addStock("Milk", 24, 3.49);
        assertEquals(24, e.getStock("milk"));
        assertEquals(24, e.getTotalUnits());
    }

    @Test
    public void testAddStockDifferentPrice()
    {
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> s.addStock("milk", 5, 3.29));
        assertEquals("milk is already priced at $3.49.", ex.getMessage());
        assertEquals(48, s.getStock("milk"));
        assertEquals(60, s.getTotalUnits());
    }

    @Test
    public void testAddStockBadValues()
    {
        assertThrows(IllegalArgumentException.class,
            () -> e.addStock("milk", 0, 3.49));
        assertThrows(IllegalArgumentException.class,
            () -> e.addStock("milk", 1001, 3.49));
        assertThrows(IllegalArgumentException.class,
            () -> e.addStock("milk", 1, Double.NaN));
        assertThrows(IllegalArgumentException.class,
            () -> e.addStock("   ", 1, 1.00));
        assertThrows(IllegalArgumentException.class,
            () -> e.addStock("123", 1, 1.00));
        assertEquals(0, e.getTotalUnits());
    }

    // ---------------- getStock ----------------

    @Test
    public void testGetStockSumsBatches()
    {
        assertEquals(48, s.getStock("MILK"));
        assertEquals(12, s.getStock(" eggs "));
    }

    @Test
    public void testGetStockMissing()
    {
        assertEquals(0, s.getStock("bread"));
        assertEquals(0, e.getStock("milk"));
        assertThrows(IllegalArgumentException.class, () -> s.getStock(""));
    }

    // ---------------- quote ----------------

    @Test
    public void testQuote()
    {
        Item line = s.quote("milk", 30);
        assertEquals(new Item("milk", 30, 3.49), line);
        assertEquals(48, s.getStock("milk")); // quoting changes nothing
    }

    @Test
    public void testQuoteNotEnough()
    {
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> s.quote("milk", 49));
        assertEquals("Only 48 milk in stock.", ex.getMessage());
    }

    @Test
    public void testQuoteMissingAndEmpty()
    {
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> s.quote("bread", 1));
        assertEquals("Item not found: bread", ex.getMessage());
        Exception empty = assertThrows(IllegalStateException.class,
            () -> e.quote("milk", 1));
        assertEquals("Inventory is empty.", empty.getMessage());
        assertThrows(IllegalArgumentException.class, () -> s.quote("milk", 0));
    }

    // ---------------- checkout ----------------

    @Test
    public void testCheckout()
    {
        Sale sale = s.checkout(cart("milk", 30, "eggs", 2), t);
        assertEquals(110.68, sale.getTotal(), 0.0001);
        assertEquals(18, s.getStock("milk"));
        assertEquals(10, s.getStock("eggs"));
        assertEquals(28, s.getTotalUnits());
        assertEquals(1, s.getSaleCount());
    }

    @Test
    public void testCheckoutWithCurrentTime()
    {
        Sale sale = s.checkout(cart("eggs", 1));
        assertNotNull(sale.getSaleTime());
        assertEquals(11, s.getStock("eggs"));
    }

    @Test
    public void testCheckoutRepeatedLinesTooMany()
    {
        // each line is fine alone, but together they need 50 of 48
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> s.checkout(cart("milk", 25, "milk", 25), t));
        assertEquals("Only 48 milk in stock.", ex.getMessage());
        assertEquals(48, s.getStock("milk"));
        assertEquals(60, s.getTotalUnits());
        assertEquals(0, s.getSaleCount());
    }

    @Test
    public void testCheckoutAllOrNothing()
    {
        // eggs is fine but bread is missing, so eggs must not be removed
        LinkedChain<Item> cart = cart("eggs", 2);
        cart.addToEnd(new Item("bread", 1, 2.99));
        assertThrows(IllegalArgumentException.class,
            () -> s.checkout(cart, t));
        assertEquals(12, s.getStock("eggs"));
        assertEquals(0, s.getSaleCount());
    }

    @Test
    public void testCheckoutEmptyCart()
    {
        Exception ex = assertThrows(IllegalArgumentException.class,
            () -> s.checkout(new LinkedChain<Item>(), t));
        assertEquals("The cart is empty.", ex.getMessage());
        assertThrows(IllegalArgumentException.class,
            () -> s.checkout(null, t));
    }

    // ---------------- loadStartingStock ----------------

    @Test
    public void testLoadStartingStock()
    {
        e.loadStartingStock();
        assertEquals(140, e.getTotalUnits());
        assertEquals(20, e.getStock("milk"));
        assertEquals(50, e.getStock("apples"));
    }

    @Test
    public void testLoadStartingStockNotEmpty()
    {
        assertThrows(IllegalStateException.class, () -> s.loadStartingStock());
        assertEquals(60, s.getTotalUnits());
    }

    // ---------------- getTotalUnits ----------------

    @Test
    public void testGetTotalUnits()
    {
        assertEquals(60, s.getTotalUnits());
    }

    @Test
    public void testGetTotalUnitsAfterFailedCheckout()
    {
        assertThrows(IllegalArgumentException.class,
            () -> s.checkout(cart("milk", 40, "milk", 40), t));
        assertEquals(60, s.getTotalUnits());
    }

    // ---------------- getInventoryReport ----------------

    @Test
    public void testGetInventoryReport()
    {
        String report = s.getInventoryReport();
        assertTrue(report.contains("milk: 48 @ $3.49"));
        assertTrue(report.contains("eggs: 12 @ $2.99"));
        // milk has two batches but is listed once
        assertEquals(report.indexOf("milk"), report.lastIndexOf("milk"));
    }

    @Test
    public void testGetInventoryReportEmpty()
    {
        assertEquals("Inventory is empty.", e.getInventoryReport());
    }

    // ---------------- getSalesTotal / getSaleCount / getSalesReport ----

    @Test
    public void testGetSalesTotal()
    {
        s.checkout(cart("milk", 30, "eggs", 2), t);
        s.checkout(cart("milk", 2), t);
        assertEquals(117.66, s.getSalesTotal(), 0.0001);
    }

    @Test
    public void testGetSalesTotalNoSales()
    {
        assertEquals(0.0, s.getSalesTotal(), 0.0001);
    }

    @Test
    public void testGetSaleCount()
    {
        s.checkout(cart("milk", 1), t);
        s.checkout(cart("eggs", 1), t);
        assertEquals(2, s.getSaleCount());
    }

    @Test
    public void testGetSaleCountAfterFailedCheckout()
    {
        assertThrows(IllegalArgumentException.class,
            () -> s.checkout(cart("milk", 30, "milk", 30), t));
        assertEquals(0, s.getSaleCount());
    }

    @Test
    public void testGetSalesReport()
    {
        s.checkout(cart("milk", 30, "eggs", 2), t);
        String report = s.getSalesReport();
        assertTrue(report.contains("Sale 2026-10-06T17:05"));
        assertTrue(report.contains("TOTAL $110.68"));
    }

    @Test
    public void testGetSalesReportNoSales()
    {
        assertEquals("No sales yet.", s.getSalesReport());
    }

    // ---------------- stretch goal: popularity ----------------

    @Test
    public void testGetUnitsSold()
    {
        s.checkout(cart("milk", 30), t);
        s.checkout(cart("milk", 2, "eggs", 1), t);
        assertEquals(32, s.getUnitsSold("MILK"));
        assertEquals(1, s.getUnitsSold("eggs"));
    }

    @Test
    public void testGetUnitsSoldNeverSold()
    {
        assertEquals(0, s.getUnitsSold("milk"));
        assertEquals(0, s.getUnitsSold("bread"));
    }

    @Test
    public void testGetBestSellerReport()
    {
        s.checkout(cart("eggs", 2), t);
        s.checkout(cart("milk", 30), t);
        String report = s.getBestSellerReport();
        assertTrue(report.startsWith("1. milk: 30 sold"));
        assertTrue(report.contains("2. eggs: 2 sold"));
    }

    @Test
    public void testGetBestSellerReportNoSales()
    {
        assertEquals("No sales yet.", s.getBestSellerReport());
    }
}
