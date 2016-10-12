package sg.edu.nus.comp.cs3205.c3.sms;

public class SMSMessage {

    private String from;
    private String to;
    private String text;

    SMSMessage(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
    }

}
