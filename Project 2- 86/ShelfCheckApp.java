import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The text menu. Builds the cart for a sale and prints results and
 * error messages; holds no rules of its own.
 *
 * @author Karthik Mittadhoddi (drafted with GenAI - see README)
 * @version October 2026
 */
public class ShelfCheckApp
{
    private final Inventory inventory;
    private final Scanner in;

    /**
     * @param inventory the store's data
     * @param in        where input comes from (System.in, or a String
     *                  in tests)
     */
    public ShelfCheckApp(Inventory inventory, Scanner in)
    {
        this.inventory = inventory;
        this.in = in;
    }

    /**
     * public so the JVM can call it; static so it runs before any
     * object exists.
     */
    public static void main(String[] args)
    {
        new ShelfCheckApp(new Inventory(), new Scanner(System.in)).run();
    }

    /**
     * Runs the menu until the user quits or input ends (Ctrl+D).
     */
    public void run()
    {
        System.out.println("ShelfCheck - Grocery Stock Tracker");
        try
        {
            boolean running = true;
            while (running)
            {
                printMenu();
                int choice = InputReader.readInt(in, "Choose: ", 0, 7);
                running = handle(choice);
            }
        }
        catch (NoSuchElementException e)
        {
            // end of input: fall through and exit cleanly
        }
        System.out.println("Goodbye.");
    }

    private void printMenu()
    {
        System.out.println();
        System.out.println("1 Add stock     2 New sale      3 Look up item");
        System.out.println("4 Inventory     5 Total units   6 Sales summary");
        System.out.println("7 Best sellers  0 Quit");
    }

    /**
     * @return false when the user chose Quit
     */
    private boolean handle(int choice)
    {
        switch (choice)
        {
            case 1:
                addStock();
                break;
            case 2:
                newSale();
                break;
            case 3:
                lookUp();
                break;
            case 4:
                System.out.print(inventory.getInventoryReport() + "\n");
                break;
            case 5:
                System.out.println("Total units in the store: "
                    + inventory.getTotalUnits());
                break;
            case 6:
                System.out.print(inventory.getSalesReport() + "\n");
                System.out.printf("Sales: %d, revenue: $%.2f%n",
                    inventory.getSaleCount(), inventory.getSalesTotal());
                break;
            case 7:
                System.out.print(inventory.getBestSellerReport() + "\n");
                break;
            default:
                return false; // 0 = Quit
        }
        return true;
    }

    private void addStock()
    {
        String name = InputReader.readName(in, "Item name: ");
        int quantity = InputReader.readInt(in, "Quantity (1-1000): ",
            1, Item.MAX_QUANTITY);
        double price = InputReader.readPrice(in, "Unit price: $");
        try
        {
            inventory.addStock(name, quantity, price);
            System.out.println("Added " + quantity + " "
                + Item.normalize(name) + ".");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void newSale()
    {
        if (inventory.getTotalUnits() == 0)
        {
            System.out.println("Inventory is empty.");
            return;
        }
        LinkedChain<Item> cart = new LinkedChain<Item>();
        boolean adding = true;
        while (adding)
        {
            String name = InputReader.readName(in, "Item name: ");
            int quantity = InputReader.readInt(in, "Quantity (1-1000): ",
                1, Item.MAX_QUANTITY);
            try
            {
                Item line = inventory.quote(name, quantity);
                cart.addToEnd(line); // keeps receipt lines in scan order
                System.out.println("Added: " + line);
            }
            catch (IllegalArgumentException | IllegalStateException e)
            {
                System.out.println(e.getMessage());
            }
            adding = InputReader.readYesNo(in, "Add another item? (y/n): ");
        }
        if (cart.isEmpty())
        {
            System.out.println("Sale cancelled: the cart is empty.");
            return;
        }
        if (!InputReader.readYesNo(in, "Check out? (y/n): "))
        {
            System.out.println("Sale cancelled.");
            return;
        }
        try
        {
            System.out.println(inventory.checkout(cart));
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private void lookUp()
    {
        String name = InputReader.readName(in, "Item name: ");
        try
        {
            System.out.println(Item.normalize(name) + ": "
                + inventory.getStock(name) + " in stock");
        }
        catch (IllegalArgumentException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
