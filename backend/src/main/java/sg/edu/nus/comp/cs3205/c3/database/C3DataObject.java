package sg.edu.nus.comp.cs3205.c3.database;

public class C3DataObject {

    private final int id;
    private final String name;
    private final int quantity;
    private final String comment;

    C3DataObject(int id, String name, int quantity, String comment) {
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
