import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Tests for StockMinimum: one normal and one bad-input case per public
 * method.
 *
 * @author Raul Garuz (drafted with GenAI - see README)
 * @version October 2026
 */
public class StockMinimumTest
{
    @Test
    public void testConstructor()
    {
        StockMinimum rule = new StockMinimum(" Milk ", 5);
        assertEquals("milk", rule.getName());
        assertEquals(5, rule.getMinimum());
    }

    @Test
    public void testConstructorBad()
    {
        assertThrows(IllegalArgumentException.class,
            () -> new StockMinimum("milk", 0));
        assertThrows(IllegalArgumentException.class,
            () -> new StockMinimum("milk", 1001));
        assertThrows(IllegalArgumentException.class,
            () -> new StockMinimum("", 5));
        assertThrows(IllegalArgumentException.class,
            () -> new StockMinimum("123", 5));
    }

    @Test
    public void testIsLow()
    {
        StockMinimum rule = new StockMinimum("milk", 5);
        assertTrue(rule.isLow(4));
        assertTrue(rule.isLow(0));
    }

    @Test
    public void testIsLowAtOrAboveMinimum()
    {
        StockMinimum rule = new StockMinimum("milk", 5);
        assertFalse(rule.isLow(5));
        assertFalse(rule.isLow(20));
    }

    @Test
    public void testEqualsAndHashCode()
    {
        StockMinimum a = new StockMinimum("milk", 5);
        StockMinimum b = new StockMinimum("MILK", 5);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testNotEquals()
    {
        StockMinimum a = new StockMinimum("milk", 5);
        assertNotEquals(a, new StockMinimum("milk", 6));
        assertNotEquals(a, new StockMinimum("eggs", 5));
        assertFalse(a.equals("milk"));
        assertFalse(a.equals(null));
    }

    @Test
    public void testToString()
    {
        assertEquals("milk: minimum 5", new StockMinimum("milk", 5).toString());
    }
}
