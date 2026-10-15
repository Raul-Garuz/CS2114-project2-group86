import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * One completed checkout (a receipt): the items sold and the time.
 *
 * @author Raul Garuz (drafted with GenAI - see README)
 * @version October 2026
 */
public class Sale
{
    private final LinkedChain<Item> itemsSold;
    private final LocalDateTime saleTime;

    /**
     * Creates a sale from a copy of the given items.
     *
     * @param items    the cart lines; not null or empty
     * @param saleTime when the sale happened; truncated to the minute
     * @throws IllegalArgumentException if items is null or empty, or
     *         saleTime is null
     */
    public Sale(LinkedChain<Item> items, LocalDateTime saleTime)
    {
        if (items == null || items.isEmpty())
        {
            throw new IllegalArgumentException(
                "A sale needs at least one item.");
        }
        if (saleTime == null)
        {
            throw new IllegalArgumentException("A sale needs a time.");
        }
        itemsSold = new LinkedChain<Item>();
        Item[] lines = items.toArray(new Item[items.size()]);
        // add() puts each line at the front, so walking backwards keeps
        // the original order in O(k) instead of O(k^2) with addToEnd()
        for (int i = lines.length - 1; i >= 0; i--)
        {
            itemsSold.add(lines[i]);
        }
        this.saleTime = saleTime.truncatedTo(ChronoUnit.MINUTES);
    }

    /**
     * @return when the sale happened, to the minute
     */
    public LocalDateTime getSaleTime()
    {
        return saleTime;
    }

    /**
     * @return the number of lines on this receipt
     */
    public int getLineCount()
    {
        return itemsSold.size();
    }

    /**
     * @return a copy of the lines on this receipt, in order
     */
    public Item[] getItems()
    {
        return itemsSold.toArray(new Item[itemsSold.size()]);
    }

    /**
     * @param name a product name
     * @return units of that product in this sale (0 if none)
     */
    public int getUnitsSold(String name)
    {
        String key = Item.normalize(name);
        int units = 0;
        for (Item line : getItems())
        {
            if (line.getName().equals(key))
            {
                units += line.getQuantity();
            }
        }
        return units;
    }

    /**
     * @return the sum of the line subtotals, rounded to cents
     */
    public double getTotal()
    {
        double total = 0;
        for (Item line : getItems())
        {
            total += line.getSubtotal();
        }
        return Math.round(total * 100) / 100.0;
    }

    /**
     * Two sales are equal when their times match and they hold the same
     * items the same number of times, in any order.
     *
     * @param other the object to compare with
     * @return true if equal
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof Sale))
        {
            return false;
        }
        Sale that = (Sale)other;
        if (!saleTime.equals(that.saleTime)
            || getLineCount() != that.getLineCount())
        {
            return false;
        }
        for (Item line : getItems())
        {
            if (itemsSold.count(line) != that.itemsSold.count(line))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @return a hash code consistent with equals (ignores line order)
     */
    @Override
    public int hashCode()
    {
        int sum = 0;
        for (Item line : getItems())
        {
            sum += line.hashCode();
        }
        return 31 * saleTime.hashCode() + sum;
    }

    /**
     * @return a printable receipt: time, one line per item, and the total
     */
    @Override
    public String toString()
    {
        StringBuilder receipt = new StringBuilder("Sale " + saleTime + "\n");
        for (Item line : getItems())
        {
            receipt.append("  ").append(line).append("\n");
        }
        receipt.append(String.format("  TOTAL $%.2f", getTotal()));
        return receipt.toString();
    }
}
