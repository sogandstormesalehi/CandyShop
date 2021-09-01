import java.util.ArrayList;

public class Transaction {
    private int id;
    private int customerId;
    private int amount;
    private int discountCode;
    private int discountPrice;
    private int finalPayment;
    private boolean isAccepted;
    private static int idCounter = 0;
    private static ArrayList<Transaction> transactions = new ArrayList<>();

    Transaction(int customerId, int amount, int discountCode) {
        this.customerId = customerId;
        this.amount = amount;
        this.discountCode = discountCode;
        this.setId();
        transactions.add(this);
    }

    private void setId() {
        this.id = ++idCounter;
    }

    public int getId() {
        return id;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public void exchangeMoney() {

    }

    public int getDiscountPrice() {
        return discountPrice;
    }

    public boolean isTransactionAccepted() {
        return isAccepted;
    }

    public int getDiscountCode() {
        return discountCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setFinalPayment(int finalPayment) {
        this.finalPayment = finalPayment;
    }

    public int getFinalPayment() {
        return finalPayment;
    }

    public static Transaction getTransactionByID(int id) {
        for (Transaction currentTransaction : transactions) {
            if (currentTransaction.getId() == id)
                return currentTransaction;
        }
        return null;
    }

    public static ArrayList<Transaction> getTransactions() {
        return transactions;
    }
}
