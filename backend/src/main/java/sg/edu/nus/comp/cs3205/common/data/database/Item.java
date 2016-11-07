package sg.edu.nus.comp.cs3205.common.data.database;

import java.util.HashMap;
import java.util.Map;

public class Item {

    private final int id;
    private final String name;
    private final int quantity;
    private final String comment;

    public Item(int id, String name, int quantity, String comment) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.comment = comment;
    }

    public static Map<String, String> getItemMap(Item item) {
        Map<String, String> map = new HashMap<>();
        map.put("id", String.valueOf(item.getId()));
        map.put("name", item.getName());
        map.put("quantity", String.valueOf(item.getQuantity()));
        map.put("comment", item.getComment());
        return map;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getComment() {
        return comment;
    }

}
