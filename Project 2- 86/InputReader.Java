import java.util.Scanner;

/**
 * Reads one value from a Scanner and re-prompts until the text is in
 * the right format. Always reads whole lines, so a bad entry never
 * leaves a stray newline behind.
 *
 * @author Karthik Mittadhoddi 
 * @version October 2026
 */
public class InputReader
{
    private InputReader()
    {
        // static helpers only
    }

    /**
     * @return a whole number from min to max
     * @throws java.util.NoSuchElementException at end of input
     */
    public static int readInt(Scanner in, String prompt, int min, int max)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = in.nextLine().trim();
            try
            {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max)
                {
                    return value;
                }
                System.out.println(
                    "Enter a number from " + min + " to " + max + ".");
            }
            catch (NumberFormatException e)
            {
                System.out.println("Enter a whole number.");
            }
        }
    }

    /**
     * @return a finite price from 0.01 to 10,000
     */
    public static double readPrice(Scanner in, String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = in.nextLine().trim();
            try
            {
                double value = Double.parseDouble(line);
                // parseDouble accepts "NaN", and NaN fails every
                // comparison, so it needs its own check
                if (!Double.isNaN(value) && value >= Item.MIN_PRICE
                    && value <= Item.MAX_PRICE)
                {
                    return value;
                }
                System.out.println(
                    "Price must be between $0.01 and $10,000.00.");
            }
            catch (NumberFormatException e)
            {
                System.out.println("Enter a price like 3.49.");
            }
        }
    }

    /**
     * @return a non-blank line, trimmed
     */
    public static String readName(Scanner in, String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = in.nextLine().trim();
            if (!line.isEmpty())
            {
                return line;
            }
            System.out.println("Item name can't be empty.");
        }
    }

    /**
     * @return true for y/yes, false for n/no (any case)
     */
    public static boolean readYesNo(Scanner in, String prompt)
    {
        while (true)
        {
            System.out.print(prompt);
            String line = in.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes"))
            {
                return true;
            }
            if (line.equals("n") || line.equals("no"))
            {
                return false;
            }
            System.out.println("Answer y or n.");
        }
    }
}
