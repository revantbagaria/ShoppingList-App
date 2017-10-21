package hu.ait.assignment2_shoppinglist.data;

import hu.ait.assignment2_shoppinglist.R;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Item extends RealmObject  {
    public enum ItemType {
        FOOD_BEVERAGE(0, R.mipmap.food_beverage),
        CLOTHES(1, R.mipmap.clothes), PERSONAL_CARE(2, R.mipmap.personal_care),
        OTHERS(3, R.mipmap.others);

        private int value_type;
        private int iconId;

        private ItemType(int value, int iconId) {
            this.value_type = value;
            this.iconId = iconId;
        }

        public int getValue() {
            return value_type;
        }

        public int getIconId() {
            return iconId;
        }

        public static ItemType fromInt(int value) {
            for (ItemType p : ItemType.values()) {
                if (p.value_type == value) {
                    return p;
                }
            }
            return FOOD_BEVERAGE;
        }
    }

    @PrimaryKey
    private String itemID;

    private String itemName;
    private String description;
    private String estim_price;
    private int itemType;
    private boolean done;

    public Item() {

    }

    public Item(String itemName, boolean done, String description, String estim_price, int itemType) {
        this.itemName = itemName;
        this.description = description;
        this.estim_price = estim_price;
        this.itemType = itemType;
        this.done = done;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEstim_price() { return estim_price; }

    public void setEstim_price(String estim_price) { this.estim_price = estim_price; }

    public ItemType getItemType() {
        return ItemType.fromInt(itemType);
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getItemID() {
        return itemID;
    }
}
