package sg.edu.nus.comp.cs3205.common.data.sms;

public class SMSMessage {

    private String from;
    private String to;
    private String text;

    public SMSMessage(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

}
