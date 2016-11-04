package sg.edu.nus.comp.cs3205.common.data.database;

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
