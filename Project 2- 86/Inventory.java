public class Inventory {
        String name;
        int quantity;
        ProductNode next;



    public static void main(String[] args) {

        System.out.println("Welcome to ShelfCheck please choose a number from the menu");
    }
    ProductNode(String name, int quantity){
        this.name= name;
        this.quantity= quantity; 
        
    }
    private static class CategoryNode{ 
        String name; 
        CategoryNode prev; 
        CategoryNode next; 
        ProductNode productHead; 


    }

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


    
}
