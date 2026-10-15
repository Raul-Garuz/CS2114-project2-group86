import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for Item: one normal and one bad-input case per public method.
 *
 * @author Raul Garuz (drafted with GenAI - see README)
 * @version October 2026
 */
public class ItemTest
{
    private Item m2;

    /**
     * m2 = 2 milk at $3.49, as in the spec's test plan.
     */
    @BeforeEach
    public void setUp()
    {
        m2 = new Item("milk", 2, 3.49);
    }

    @Test
    public void testConstructorNormal()
    {
        Item item = new Item("  Whole Milk ", 12, 3.49);
        assertEquals("whole milk", item.getName());
        assertEquals(12, item.getQuantity());
        assertEquals(3.49, item.getUnitPrice(), 0.0001);
    }

    @Test
    public void testConstructorBoundariesAccepted()
    {
        assertEquals(1, new Item("x", 1, 0.01).getQuantity());
        assertEquals(1000, new Item("x", 1000, 10000.00).getQuantity());
        assertEquals(30, new Item("x".repeat(30), 1, 1.00)
            .getName().length());
    }

    @Test
    public void testConstructorBadQuantity()
    {
        assertThrows(IllegalArgumentException.class,
            () -> new Item("milk", 0, 3.49));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("milk", 1001, 3.49));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("milk", -5, 3.49));
    }

    @Test
    public void testConstructorBadPrice()
    {
        assertThrows(IllegalArgumentException.class,
            () -> new Item("milk", 1, Double.NaN));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("milk", 1, Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("milk", 1, 0));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("milk", 1, -1));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("milk", 1, 10000.01));
    }

    @Test
    public void testConstructorBadName()
    {
        assertThrows(IllegalArgumentException.class,
            () -> new Item("   ", 1, 1.00));
        assertThrows(IllegalArgumentException.class,
            () -> new Item(null, 1, 1.00));
        assertThrows(IllegalArgumentException.class,
            () -> new Item("x".repeat(31), 1, 1.00));
    }

    @Test
    public void testNormalize()
    {
        assertEquals("whole milk", Item.normalize("  Whole   Milk "));
    }

    @Test
    public void testNormalizeBad()
    {
        assertThrows(IllegalArgumentException.class,
            () -> Item.normalize(null));
        assertThrows(IllegalArgumentException.class,
            () -> Item.normalize(""));
    }

    @Test
    public void testGetters()
    {
        assertEquals("milk", m2.getName());
        assertEquals(2, m2.getQuantity());
        assertEquals(3.49, m2.getUnitPrice(), 0.0001);
    }

    @Test
    public void testPriceRoundedToCents()
    {
        assertEquals(3.50, new Item("x", 1, 3.499).getUnitPrice(), 0.0001);
    }

    @Test
    public void testGetSubtotal()
    {
        assertEquals(6.98, m2.getSubtotal(), 0.0001);
    }

    @Test
    public void testGetSubtotalNoFloatingPointError()
    {
        // 3 * 0.10 in plain double math is 0.30000000000000004
        assertEquals(0.30, new Item("x", 3, 0.10).getSubtotal(), 0.0001);
    }

    @Test
    public void testWithQuantity()
    {
        Item five = m2.withQuantity(5);
        assertEquals(5, five.getQuantity());
        assertEquals("milk", five.getName());
        assertEquals(3.49, five.getUnitPrice(), 0.0001);
        assertEquals(2, m2.getQuantity()); // original unchanged
    }

    @Test
    public void testWithQuantityBad()
    {
        assertThrows(IllegalArgumentException.class,
            () -> m2.withQuantity(0));
    }

    @Test
    public void testEqualsAndHashCode()
    {
        Item same = new Item("MILK ", 2, 3.49);
        assertEquals(m2, same);
        assertEquals(m2.hashCode(), same.hashCode());
    }

    @Test
    public void testNotEquals()
    {
        assertNotEquals(m2, new Item("milk", 2, 3.29));
        assertNotEquals(m2, new Item("milk", 3, 3.49));
        assertNotEquals(m2, new Item("eggs", 2, 3.49));
        assertFalse(m2.equals("milk"));
        assertFalse(m2.equals(null));
    }

    @Test
    public void testToString()
    {
        assertEquals("milk x2 @ $3.49 = $6.98", m2.toString());
    }

    @Test
    public void testToStringRoundedPrice()
    {
        assertTrue(new Item("x", 1, 3.499).toString().contains("$3.50"));
    }
}
