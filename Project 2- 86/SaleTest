import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for Sale: one normal and one bad-input case per public method.
 *
 * @author Raul Garuz (drafted with GenAI - see README)
 * @version October 2026
 */
public class SaleTest
{
    private Item m2;
    private Item e1;
    private LinkedChain<Item> cart;
    private LocalDateTime t;

    /**
     * K = cart with m2 then e1; t = 2026-10-06 17:05:12, as in the
     * spec's test plan.
     */
    @BeforeEach
    public void setUp()
    {
        m2 = new Item("milk", 2, 3.49);
        e1 = new Item("eggs", 1, 2.99);
        cart = new LinkedChain<Item>();
        cart.addToEnd(m2);
        cart.addToEnd(e1);
        t = LocalDateTime.of(2026, 10, 6, 17, 5, 12);
    }

    @Test
    public void testConstructor()
    {
        Sale sale = new Sale(cart, t);
        assertEquals(2, sale.getLineCount());
        assertEquals(LocalDateTime.of(2026, 10, 6, 17, 5),
            sale.getSaleTime());
    }

    @Test
    public void testConstructorBad()
    {
        assertThrows(IllegalArgumentException.class,
            () -> new Sale(new LinkedChain<Item>(), t));
        assertThrows(IllegalArgumentException.class,
            () -> new Sale(null, t));
        assertThrows(IllegalArgumentException.class,
            () -> new Sale(cart, null));
    }

    @Test
    public void testSaleKeepsItsOwnCopy()
    {
        Sale sale = new Sale(cart, t);
        cart.addToEnd(new Item("bread", 1, 2.50));
        assertEquals(2, sale.getLineCount());
    }

    @Test
    public void testGetItemsInOrder()
    {
        Item[] lines = new Sale(cart, t).getItems();
        assertEquals(2, lines.length);
        assertEquals(m2, lines[0]);
        assertEquals(e1, lines[1]);
    }

    @Test
    public void testGetItemsReturnsCopy()
    {
        Sale sale = new Sale(cart, t);
        Item[] lines = sale.getItems();
        lines[0] = null;
        assertEquals(m2, sale.getItems()[0]);
    }

    @Test
    public void testGetUnitsSold()
    {
        Sale sale = new Sale(cart, t);
        assertEquals(2, sale.getUnitsSold("milk"));
        assertEquals(2, sale.getUnitsSold(" MILK "));
    }

    @Test
    public void testGetUnitsSoldMissingAndRepeated()
    {
        assertEquals(0, new Sale(cart, t).getUnitsSold("bread"));
        cart.addToEnd(m2); // the same line scanned twice
        assertEquals(4, new Sale(cart, t).getUnitsSold("milk"));
    }

    @Test
    public void testGetTotal()
    {
        assertEquals(9.97, new Sale(cart, t).getTotal(), 0.0001);
    }

    @Test
    public void testGetTotalNoFloatingPointError()
    {
        LinkedChain<Item> one = new LinkedChain<Item>();
        one.add(new Item("x", 3, 0.10));
        assertEquals(0.30, new Sale(one, t).getTotal(), 0.0001);
    }

    @Test
    public void testEqualsAnyOrderSameMinute()
    {
        LinkedChain<Item> reversed = new LinkedChain<Item>();
        reversed.addToEnd(e1);
        reversed.addToEnd(m2);
        Sale a = new Sale(cart, t);
        Sale b = new Sale(reversed, t.plusSeconds(30));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testNotEquals()
    {
        Sale a = new Sale(cart, t);
        assertNotEquals(a, new Sale(cart, t.plusMinutes(1)));
        assertFalse(a.equals("sale"));
        assertFalse(a.equals(null));

        // same number of lines, different counts of each item
        LinkedChain<Item> x = new LinkedChain<Item>();
        x.add(m2);
        x.add(m2);
        x.add(e1);
        LinkedChain<Item> y = new LinkedChain<Item>();
        y.add(m2);
        y.add(e1);
        y.add(e1);
        assertNotEquals(new Sale(x, t), new Sale(y, t));
    }

    @Test
    public void testToString()
    {
        String receipt = new Sale(cart, t).toString();
        assertTrue(receipt.startsWith("Sale 2026-10-06T17:05"));
        assertTrue(receipt.contains("milk x2"));
        assertTrue(receipt.contains("eggs x1"));
        assertTrue(receipt.contains("TOTAL $9.97"));
    }

    @Test
    public void testToStringOneLine()
    {
        LinkedChain<Item> one = new LinkedChain<Item>();
        one.add(e1);
        String receipt = new Sale(one, t).toString();
        assertEquals(3, receipt.split("\n").length); // header, line, total
    }
}
