import java.util.ArrayList;

public class Customer {
    private static ArrayList<Customer> allCustomers = new ArrayList<>();
    private String name;
    private int id;
    private int balance;
    private int discountCode;

    Customer(String name, int id) {
        this.name = name;
        this.id = id;
        allCustomers.add(this);
    }

    public String getName() {
        return name;
    }



    public int getID() {
        return id;
    }

    public static Customer getCustomerByID(int id) {
        for (Customer customer : allCustomers) {
            if (customer.getID() == id)
                return customer;
        }
        return null;
    }

    public void increaseCustomerBalance(int balance) {
        this.balance += balance;
    }

    public void decreaseBalance(int balance) {
        this.balance -= balance;
    }

    public void setDiscountCode(int discountCode) {
        this.discountCode = discountCode;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public int getDiscountCode() {
        return discountCode;
    }

    private static void setAllCustomers(ArrayList<Customer> allCustomers) {
        Customer.allCustomers = allCustomers;
    }

    public static ArrayList<Customer> getAllCustomers() {
        return allCustomers;
    }
}
