package sg.edu.nus.comp.cs3205.c2.data;

public class Payload {
    public String action = null;
    public String data = null;
    public String error = null;
    public String id = null;
    public String input = null;
    public String sign = null;

    public Payload setAction(String action) {
        this.action = action;
        return this;
    }
}
