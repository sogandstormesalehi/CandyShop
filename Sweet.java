import java.util.ArrayList;
import java.util.HashMap;

public class Sweet {
    private String name;
    private int price;
    private int amount;
    private HashMap<String, Integer> materials = new HashMap<>();
    private static ArrayList<Sweet> sweets = new ArrayList<>();

    Sweet(String name, int price, HashMap<String, Integer> materials) {
        this.name = name;
        this.price = price;
        this.materials = materials;
        materials.put(this.name, this.price);
        sweets.add(this);
    }

    private void setMaterials(HashMap<String, Integer> materials) {
        this.materials = materials;
    }

    public HashMap<String, Integer> getMaterials() {
        return materials;
    }

    public static ArrayList<Sweet> getSweets() {
        return sweets;
    }

    public String getName() {
        return name;
    }

    public void increaseSweet(int amount) {
        this.amount += amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getPrice() {
        return price;
    }

    public void decreaseMaterialOfSweetFromWarehouse(int amount) {
        this.amount -= amount;
    }

    public static Sweet getSweetByName(String name) {
        for (Sweet sweet : sweets)
            if (sweet.getName().equals(name))
                return sweet;
        return null;
    }
}
