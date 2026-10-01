public class GroceryInventory {

    // Inner node: one product
    private static class ProductNode {
        String name;
        int quantity;
        ProductNode next;

        ProductNode(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }

    // Outer node: one category containing a product list
    private static class CategoryNode {
        String name;
        CategoryNode prev;
        CategoryNode next;
        ProductNode productHead;

        CategoryNode(String name) {
            this.name = name;
        }
    }

    private CategoryNode head;
    private CategoryNode tail;

    // Add a category to the end of the outer list
    public void addCategory(String name) {
        CategoryNode newCategory = new CategoryNode(name);

        if (head == null) {
            head = newCategory;
            tail = newCategory;
        }
        else {
            tail.next = newCategory;
            newCategory.prev = tail;
            tail = newCategory;
        }
    }

    // Find a category by walking through the outer list
    private CategoryNode findCategory(String name) {
        CategoryNode current = head;

        while (current != null) {
            if (current.name.equalsIgnoreCase(name)) {
                return current;
            }

            current = current.next;
        }

        return null;
    }

    // Add a product to the front of a category's inner list
    public void addProduct(String categoryName,
        String productName, int quantity) {

        CategoryNode category = findCategory(categoryName);

        if (category == null) {
            throw new IllegalArgumentException("Category not found");
        }

        ProductNode newProduct =
            new ProductNode(productName, quantity);

        newProduct.next = category.productHead;
        category.productHead = newProduct;
    }

    // Traverse the outer list, then each inner list
    public void printInventory() {
        CategoryNode category = head;

        while (category != null) {
            System.out.println(category.name + ":");

            ProductNode product = category.productHead;

            while (product != null) {
                System.out.println("  " + product.name
                    + ": " + product.quantity);

                product = product.next;
            }

            category = category.next;
        }
    }

    public static void main(String[] args) {
        GroceryInventory store = new GroceryInventory();

        store.addCategory("Produce");
        store.addCategory("Dairy");
        store.addCategory("Bakery");

        store.addProduct("Produce", "Bananas", 30);
        store.addProduct("Produce", "Apples", 50);

        store.addProduct("Dairy", "Cheese", 15);
        store.addProduct("Dairy", "Milk", 20);

        store.addProduct("Bakery", "Bread", 25);

        store.printInventory();
    }
}
    
}
