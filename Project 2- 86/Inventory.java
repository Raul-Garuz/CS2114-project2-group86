import java.time.LocalDateTime;

/**
 * The store's rules: stock, checkout, totals and reports. Never reads
 * input or prints; reports problems by throwing exceptions.
 *
 * @author Thomas Cherian 
 * @version October 2026
 */
public class Inventory
{
    private final LinkedChain<Item> stock;
    private final LinkedChain<Sale> sales;
    private int totalUnits;

    /**
     * Creates an inventory with no stock and no sales.
     */
    public Inventory()
    {
        stock = new LinkedChain<Item>();
        sales = new LinkedChain<Sale>();
        totalUnits = 0;
    }

    /**
     * Adds one batch of a product.
     *
     * @throws IllegalArgumentException if a value is invalid or the
     *         product is already in stock at a different price
     */
    public void addStock(String name, int quantity, double unitPrice)
    {
        Item batch = new Item(name, quantity, unitPrice);
        double current = priceOf(batch.getName());
        if (current > 0
            && Double.compare(current, batch.getUnitPrice()) != 0)
        {
            throw new IllegalArgumentException(String.format(
                "%s is already priced at $%.2f.", batch.getName(), current));
        }
        stock.add(batch);
        totalUnits += quantity;
    }

    /**
     * @return the product's units across all batches (0 if none)
     */
    public int getStock(String name)
    {
        String key = Item.normalize(name);
        int units = 0;
        for (Item batch : stockArray())
        {
            if (batch.getName().equals(key))
            {
                units += batch.getQuantity();
            }
        }
        return units;
    }

    /**
     * Returns a cart line priced from stock. Changes nothing.
     *
     * @throws IllegalStateException if the inventory is empty
     * @throws IllegalArgumentException if the product is missing, there
     *         isn't enough of it, or the quantity is invalid
     */
    public Item quote(String name, int quantity)
    {
        String key = Item.normalize(name);
        if (stock.isEmpty())
        {
            throw new IllegalStateException("Inventory is empty.");
        }
        int have = getStock(key);
        if (have == 0)
        {
            throw new IllegalArgumentException("Item not found: " + key);
        }
        if (have < quantity)
        {
            throw new IllegalArgumentException(
                "Only " + have + " " + key + " in stock.");
        }
        return new Item(key, quantity, priceOf(key));
    }

    /**
     * Checks the whole cart, then removes the units and records a Sale
     * stamped with the current time. All-or-nothing: if any line fails,
     * it throws and nothing changes.
     *
     * @throws IllegalArgumentException if the cart is empty or any
     *         product lacks enough stock
     */
    public Sale checkout(LinkedChain<Item> cart)
    {
        return checkout(cart, LocalDateTime.now());
    }

    /**
     * Same as checkout(cart), with a fixed time so tests are repeatable.
     */
    Sale checkout(LinkedChain<Item> cart, LocalDateTime time)
    {
        if (cart == null || cart.isEmpty())
        {
            throw new IllegalArgumentException("The cart is empty.");
        }
        Item[] lines = cart.toArray(new Item[cart.size()]);

        // 1. check every product first (repeated lines are added up)
        LinkedChain<String> checked = new LinkedChain<String>();
        for (Item line : lines)
        {
            String key = line.getName();
            if (!checked.contains(key))
            {
                checked.add(key);
                int wanted = 0;
                for (Item other : lines)
                {
                    if (other.getName().equals(key))
                    {
                        wanted += other.getQuantity();
                    }
                }
                int have = getStock(key);
                if (have == 0)
                {
                    throw new IllegalArgumentException(
                        "Item not found: " + key);
                }
                if (have < wanted)
                {
                    throw new IllegalArgumentException(
                        "Only " + have + " " + key + " in stock.");
                }
            }
        }

        // 2. only now change anything
        for (Item line : lines)
        {
            take(line.getName(), line.getQuantity());
        }
        Sale sale = new Sale(cart, time);
        sales.add(sale);
        return sale;
    }
    
        /**
     * Stocks the store with a starting set of products, so the program
     * has something to sell as soon as it starts. Adapted from the
     * sample data in our earlier GroceryInventory prototype.
     *
     * @throws IllegalStateException if the inventory already has stock
     */
    public void loadStartingStock()
    {
        if (!stock.isEmpty())
        {
            throw new IllegalStateException(
                "Starting stock can only be loaded into an empty inventory.");
        }
        addStock("bananas", 30, 0.29);
        addStock("apples", 50, 0.79);
        addStock("cheese", 15, 4.99);
        addStock("milk", 20, 3.49);
        addStock("bread", 25, 2.99);
    }

    public int getTotalUnits()
    {
        return totalUnits;
    }

    /**
     * @return one line per product ("milk: 48 @ $3.49"), or
     *         "Inventory is empty."
     */
    public String getInventoryReport()
    {
        if (stock.isEmpty())
        {
            return "Inventory is empty.";
        }
        LinkedChain<String> listed = new LinkedChain<String>();
        StringBuilder report = new StringBuilder();
        for (Item batch : stockArray())
        {
            String key = batch.getName();
            if (!listed.contains(key))
            {
                listed.add(key);
                report.append(String.format("%s: %d @ $%.2f\n",
                    key, getStock(key), batch.getUnitPrice()));
            }
        }
        return report.toString();
    }

    /**
     * @return the revenue of every sale, rounded to cents
     */
    public double getSalesTotal()
    {
        double total = 0;
        for (Sale sale : salesArray())
        {
            total += sale.getTotal();
        }
        return Math.round(total * 100) / 100.0;
    }

    public int getSaleCount()
    {
        return sales.size();
    }

    /**
     * @return every receipt, or "No sales yet."
     */
    public String getSalesReport()
    {
        if (sales.isEmpty())
        {
            return "No sales yet.";
        }
        StringBuilder report = new StringBuilder();
        for (Sale sale : salesArray())
        {
            report.append(sale).append("\n");
        }
        return report.toString();
    }

    // ---------------- stretch goal: popularity ----------------

    /**
     * @return units of a product sold across all sales (0 if none)
     */
    public int getUnitsSold(String name)
    {
        String key = Item.normalize(name);
        int units = 0;
        for (Sale sale : salesArray())
        {
            units += sale.getUnitsSold(key);
        }
        return units;
    }

    /**
     * @return products ranked by units sold, or "No sales yet."
     */
    public String getBestSellerReport()
    {
        if (sales.isEmpty())
        {
            return "No sales yet.";
        }
        LinkedChain<String> names = new LinkedChain<String>();
        for (Sale sale : salesArray())
        {
            for (Item line : sale.getItems())
            {
                if (!names.contains(line.getName()))
                {
                    names.add(line.getName());
                }
            }
        }
        String[] products = names.toArray(new String[names.size()]);
        int[] units = new int[products.length];
        for (int i = 0; i < products.length; i++)
        {
            units[i] = getUnitsSold(products[i]);
        }
        boolean[] ranked = new boolean[products.length];
        StringBuilder report = new StringBuilder();
        for (int rank = 1; rank <= products.length; rank++)
        {
            int best = -1;
            for (int i = 0; i < products.length; i++)
            {
                if (!ranked[i] && (best == -1 || units[i] > units[best]))
                {
                    best = i;
                }
            }
            ranked[best] = true;
            report.append(String.format("%d. %s: %d sold\n",
                rank, products[best], units[best]));
        }
        return report.toString();
    }

    // ---------------- helpers ----------------

    private Item[] stockArray()
    {
        return stock.toArray(new Item[stock.size()]);
    }

    private Sale[] salesArray()
    {
        return sales.toArray(new Sale[sales.size()]);
    }

    /**
     * @return the price of a product in stock, or -1 if not in stock
     */
    private double priceOf(String key)
    {
        for (Item batch : stockArray())
        {
            if (batch.getName().equals(key))
            {
                return batch.getUnitPrice();
            }
        }
        return -1;
    }

    /**
     * Removes units from batches, replacing a partly used batch with a
     * smaller one (Items are immutable). Only called after checking.
     */
    private void take(String key, int quantity)
    {
        int needed = quantity;
        while (needed > 0)
        {
            Item batch = null;
            for (Item candidate : stockArray())
            {
                if (candidate.getName().equals(key))
                {
                    batch = candidate;
                    break;
                }
            }
            stock.remove(batch);
            int used = Math.min(needed, batch.getQuantity());
            if (batch.getQuantity() > used)
            {
                stock.add(batch.withQuantity(batch.getQuantity() - used));
            }
            needed -= used;
            totalUnits -= used;
        }
    }
}
