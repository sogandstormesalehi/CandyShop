import java.util.HashMap;

public class Confectionary {
    private int balance;
    private static HashMap<Integer, Integer> discounts = new HashMap<>();

    Confectionary() {
        setBalance(0);
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getBalance() {
        return balance;
    }

    public void increaseBalance(int balance) {
        this.balance += balance;
    }

    public static boolean isDiscountExists(int code) {
        return (discounts.containsKey(code));
    }

    public static void addDiscount(int code, int price) {
        discounts.put(code, price);
    }

    public static int getDiscountPriceByCode(int code) {
        return discounts.get(code);
    }
}
