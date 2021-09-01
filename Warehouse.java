import java.util.ArrayList;

public class Warehouse {
    private int amount;
    private String materialName;
    private static ArrayList<Warehouse> warehouses = new ArrayList<Warehouse>();

    Warehouse(String materialName, int amount) {
        this.amount = amount;
        setMaterialName(materialName);
        warehouses.add(this);
    }

    public void increaseMaterial(int amount) {
        this.amount += amount;
    }

    private void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public static ArrayList<Warehouse> getWarehouses() {
        return warehouses;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void decreaseMaterial(int amount) {
        this.amount -= amount;
    }

    public static Warehouse getWarehouseByName(String name) {
        for (Warehouse warehouse : warehouses)
            if (warehouse.getMaterialName().equals(name))
                return warehouse;
        return null;
    }
}
