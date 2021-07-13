package daniarachid.donation.Notification;

public class Data {
    String user, body, title, sent;
    private Integer icon;

    public  Data(String user, Integer icon, String body, String title, String sent) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sent = sent;
    }

    public Data() {}

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }



}
