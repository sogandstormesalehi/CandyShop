import java.util.*;
import java.lang.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProgramController {

    public static Confectionary confectionary;
    Scanner scanner = new Scanner(System.in);

    public void run() {
        String command = scanner.nextLine();
        command = command.trim();
        if (!(command.equals("create confectionary") || command.equals("end"))) {
            System.out.println("invalid command");
            run();
            return;
        }
        if (command.equals("create confectionary")) {
            this.confectionary = new Confectionary();
        }
        if (command.equals("end")) return;
        while (true) {
            String commands = scanner.nextLine();
            if (getCommandMatcher(commands, "add customer id [1-9][0-9]* name ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z])").find()) {
                addCustomer(commands);
            } else if (getCommandMatcher(commands, "increase balance customer [1-9][0-9]* amount [1-9][0-9]*").find()) {
                chargeCustomerBalance(commands);
            } else if (getCommandMatcher(commands, "add warehouse material ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) amount [1-9][0-9]*").find()) {
                addWarehouse(commands);
            } else if (getCommandMatcher(commands, "increase warehouse material ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) amount [1-9][0-9]*").find()) {
                increaseWarehouseMaterial(commands);
            } else if (getCommandMatcher(commands, "add sweet name ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) price [1-9][0-9]* materials: " +
                    "((?:([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) [0-9]+[, ]*)+)").find()) {
                addSweet(commands);
            } else if (getCommandMatcher(commands, "increase sweet ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) amount [1-9][0-9]*").find()) {
                increaseSweet(commands);
            } else if (getCommandMatcher(commands, "add discount code [1-9][0-9]* price [1-9][0-9]*").find()) {
                addDiscount(commands);
            } else if (getCommandMatcher(commands, "add discount code code [1-9][0-9]* to customer id [1-9][0-9]*").find()) {
                addDiscountToCustomer(commands);
            } else if (getCommandMatcher(commands, "sell sweet ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) amount [1-9][0-9]* to customer [1-9][0-9]*").find()) {
                sellSweet(commands);
            } else if (getCommandMatcher(commands, "accept transaction [1-9][0-9]*").find()) {
                acceptTransAction(commands);
            } else if (getCommandMatcher(commands, "print transactions list").find())
                printTransActions();
            else if (getCommandMatcher(commands, "print income").find())
                printIncome();
            else if (getCommandMatcher(commands, "end").find())
                break;
            else System.out.println("invalid command");
        }
    }

    private void printIncome() {
        System.out.println(confectionary.getBalance());
    }

    private void printTransActions() {
        ArrayList<Transaction> toPrint = Transaction.getTransactions();
        for (int i = 0; i < toPrint.size(); i++) {
            int paymentAmount = Transaction.getTransactionByID(toPrint.get(i).getId()).getAmount();
            if (Transaction.getTransactionByID(toPrint.get(i).getId()).isTransactionAccepted())
                System.out.println("transaction " +
                        toPrint.get(i).getId() + ": " + Transaction.getTransactionByID(toPrint.get(i).getId()).getCustomerId()
                        + " " + paymentAmount + " "
                        + Transaction.getTransactionByID(toPrint.get(i).getId()).getDiscountCode()
                        + " " + Transaction.getTransactionByID(toPrint.get(i).getId()).getFinalPayment());
        }
    }

    private void acceptTransAction(String commands) {
        String regex = "accept transaction ([1-9][0-9]*)";
        Matcher matcher = getCommandMatcher(commands, regex);
        matcher.find();
        int id = Integer.parseInt(matcher.group(1));
        if ((Transaction.getTransactionByID(id) == null) || (Transaction.getTransactionByID(id).isTransactionAccepted())) {
            System.out.println("no waiting transaction with this id was found");
            return;
        }
        Transaction.getTransactionByID(id).setAccepted(true);
        Customer.getCustomerByID(Transaction.getTransactionByID(id).getCustomerId()).decreaseBalance(Transaction.getTransactionByID(id).getFinalPayment());
        confectionary.increaseBalance(Transaction.getTransactionByID(id).getFinalPayment());
    }

    private void sellSweet(String commands) {
        String regex = "sell sweet ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) amount ([1-9][0-9]*) to customer ([1-9][0-9]*)";
        Matcher matcher = getCommandMatcher(commands, regex);
        matcher.find();
        String sweetName = matcher.group(1);
        int amount = Integer.parseInt(matcher.group(2));
        int id = Integer.parseInt(matcher.group(3));
        if (basicError(sweetName, id, amount)) return;
        int moneyWeHave = Customer.getCustomerByID(id).getBalance();
        int moneyWePay = Sweet.getSweetByName(sweetName).getPrice() * amount;
        if (moneyWeHave < moneyWePay) {
            if (Confectionary.isDiscountExists(Customer.getCustomerByID(id).getDiscountCode())
                    && Customer.getCustomerByID(id).getDiscountCode() != -1) {
                if (moneyWePay - moneyWeHave > Confectionary.getDiscountPriceByCode(Customer.getCustomerByID(id).getDiscountCode())) {
                    System.out.println("customer has not enough money");
                    return;
                } else if (moneyWePay >= Confectionary.getDiscountPriceByCode(Customer.getCustomerByID(id).getDiscountCode())) {
                    moneyWePay -= Confectionary.getDiscountPriceByCode(Customer.getCustomerByID(id).getDiscountCode());
                } else {
                    moneyWePay = 0;
                }
            } else {
                System.out.println("customer has not enough money");
                return;
            }
        } else if (moneyWeHave >= moneyWePay) {
            if (Confectionary.isDiscountExists(Customer.getCustomerByID(id).getDiscountCode())
                    && Customer.getCustomerByID(id).getDiscountCode() != -1) {
                if (moneyWePay >= Confectionary.getDiscountPriceByCode(Customer.getCustomerByID(id).getDiscountCode())) {
                    moneyWePay -= Confectionary.getDiscountPriceByCode(Customer.getCustomerByID(id).getDiscountCode());
                } else moneyWePay = 0;
            }
        }
        handleDiscount(sweetName, amount, id, moneyWePay);
    }

    private void handleDiscount(String sweetName, int amount, int id, int moneyWePay) {
        int discountCode = Customer.getCustomerByID(id).getDiscountCode();
        if (!Confectionary.isDiscountExists(Customer.getCustomerByID(id).getDiscountCode())) discountCode = -1;
        int amountToAdd = amount * Sweet.getSweetByName(sweetName).getPrice();
        Transaction transaction = new Transaction(id, amountToAdd, discountCode);
        int beforeAmount = Sweet.getSweetByName(sweetName).getAmount();
        Sweet.getSweetByName(sweetName).setAmount(beforeAmount - amount);
        System.out.println("transaction " + transaction.getId() + " successfully created");
        if (moneyWePay < 0) moneyWePay = 0;
        Transaction.getTransactionByID(transaction.getId()).setFinalPayment(moneyWePay);
        if (Confectionary.isDiscountExists(Customer.getCustomerByID(id).getDiscountCode()))
            Customer.getCustomerByID(id).setDiscountCode(-1);
    }

    private void addDiscountToCustomer(String commands) {
        String regex = "add discount code code ([1-9][0-9]*) to customer id ([1-9][0-9]*)";
        Matcher matcher = getCommandMatcher(commands, regex);
        matcher.find();
        int code = Integer.parseInt(matcher.group(1));
        int id = Integer.parseInt(matcher.group(2));
        if (!Confectionary.isDiscountExists(code)) {
            System.out.println("discount code not found");
            return;
        }
        if (Customer.getCustomerByID(id) == null) {
            System.out.println("customer not found");
            return;
        }
        Customer.getCustomerByID(id).setDiscountCode(code);

    }

    private void addDiscount(String commands) {
        String regex = "add discount code ([1-9][0-9]*) price ([1-9][0-9]*)";
        Matcher matcher = getCommandMatcher(commands, regex);
        matcher.find();
        int code = Integer.parseInt(matcher.group(1));
        int price = Integer.parseInt(matcher.group(2));
        if (Confectionary.isDiscountExists(code)) System.out.println("discount with this code already exists");
        else
            Confectionary.addDiscount(code, price);
    }

    private void increaseSweet(String commands) {
        String regex = "increase sweet ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) amount ([1-9][0-9]*)";
        Matcher matcher = getCommandMatcher(commands, regex);
        matcher.find();
        String sweetName = matcher.group(1);
        HashMap<String, Integer> notEnough = new HashMap<>();
        Integer amount = Integer.parseInt(matcher.group(2));
        if (Sweet.getSweetByName(sweetName) == null) {
            System.out.println("sweet not found");
            return;
        }
        HashMap<String, Integer> hashMap = Sweet.getSweetByName(sweetName).getMaterials();
        hashMap.remove(sweetName);
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            String currentMaterial = entry.getKey();
            int amountWeHave = Warehouse.getWarehouseByName(currentMaterial).getAmount();
            int amountWeNeed = amount * (entry.getValue());
            if (amountWeNeed > amountWeHave) notEnough.put(currentMaterial, amountWeHave);
            else Warehouse.getWarehouseByName(currentMaterial).setAmount(amountWeHave - amountWeNeed);
        }
        if (!notEnough.isEmpty()) {
            int keys = notEnough.size();
            int whereNow = 0;
            System.out.print("insufficient material(s): ");
            for (Map.Entry<String, Integer> entry : notEnough.entrySet()) {
                whereNow++;
                if (whereNow == keys) System.out.println(entry.getKey());
                else System.out.print(entry.getKey() + " ");
            }
        } else Sweet.getSweetByName(sweetName).increaseSweet(amount);
    }

    private void addSweet(String commands) {
        String regex = "add sweet name ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) price ([1-9][0-9]*) materials: ((?:([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) [0-9]+[, ]*)+)";
        Matcher matcher = getCommandMatcher(commands, regex);
        matcher.find();
        boolean willCreate = true;
        String sweetName = matcher.group(1);
        int price = Integer.parseInt(matcher.group(2));
        String holdMaterials = matcher.group(3) + ", ";
        HashMap<String, Integer> hashMap = new HashMap<>();
        StringBuilder allMaterials = new StringBuilder();
        Pattern patternOfMaterials = Pattern.compile("([A-Za-z]+[\\sA-Za-z]*[A-Za-z]+|[A-Za-z]) ([0-9]+)[, ]");
        Matcher matcherForMaterials = patternOfMaterials.matcher(holdMaterials);
        while (matcherForMaterials.find()) {
            String material = matcherForMaterials.group(1);
            material = material.trim();
            Integer amount = Integer.parseInt(matcherForMaterials.group(2));
            if (Warehouse.getWarehouseByName(material) != null) hashMap.put(material, amount);
            else {
                allMaterials.append(material).append(" ");
                willCreate = false;
            }
            holdMaterials = holdMaterials.replaceFirst(matcherForMaterials.group(1) + " " + matcherForMaterials.group(2), "");
            matcherForMaterials = patternOfMaterials.matcher(holdMaterials);
        }
        if (!willCreate) {
            allMaterials.setLength(allMaterials.length() - 1);
            System.out.println("not found warehouse(s): " + allMaterials);
            return;
        } else {
            new Sweet(sweetName, price, hashMap);
        }
    }


    private static Matcher getCommandMatcher(String string, String regex) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(string);
    }

    private void increaseWarehouseMaterial(String commands) {
        Pattern pattern = Pattern.compile("increase warehouse material ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) amount ([1-9][0-9]*)");
        Matcher matcher = pattern.matcher(commands);
        matcher.find();
        String materialName = matcher.group(1);
        int amount = Integer.parseInt(matcher.group(2));
        if (Warehouse.getWarehouseByName(materialName) == null) System.out.println("warehouse not found");
        else Warehouse.getWarehouseByName(materialName).increaseMaterial(amount);
    }

    private void addWarehouse(String commands) {
        Pattern pattern = Pattern.compile("add warehouse material ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z]) amount ([1-9][0-9]*)");
        Matcher matcher = pattern.matcher(commands);
        matcher.find();
        String materialName = matcher.group(1);
        int amount = Integer.parseInt(matcher.group(2));
        if (Warehouse.getWarehouseByName(materialName) != null)
            System.out.println("warehouse having this material already exists");
        else {
            Warehouse warehouse = new Warehouse(materialName, amount);
        }
    }

    private void chargeCustomerBalance(String commands) {
        String regex = "increase balance customer ([1-9][0-9]*) amount ([1-9][0-9]*)";
        Matcher matcher = getCommandMatcher(commands, regex);
        matcher.find();
        int id = Integer.parseInt(matcher.group(1));
        int amount = Integer.parseInt(matcher.group(2));
        if (Customer.getCustomerByID(id) == null) System.out.println("customer not found");
        else Customer.getCustomerByID(id).increaseCustomerBalance(amount);
    }

    private void addCustomer(String commands) {
        String regex = "add customer id ([1-9][0-9]*) name ([A-Za-z]+[A-Za-z\\s]*[A-Za-z]+|[A-Za-z])";
        Matcher matcher = getCommandMatcher(commands, regex);
        matcher.find();
        String name = matcher.group(2);
        int id = Integer.parseInt(matcher.group(1));
        if (Customer.getCustomerByID(id) != null) {
            System.out.println("customer with this id already exists");
            return;
        }
        Customer customer = new Customer(name, id);
    }

    private static boolean basicError(String sweetName, int id, int amount) {
        if (Sweet.getSweetByName(sweetName) == null) {
            System.out.println("sweet not found");
            return true;
        }
        if (Objects.requireNonNull(Sweet.getSweetByName(sweetName)).getAmount() < amount) {
            System.out.println("insufficient sweet");
            return true;
        }
        if (Customer.getCustomerByID(id) == null) {
            System.out.println("customer not found");
            return true;
        }
        return false;
    }
}
