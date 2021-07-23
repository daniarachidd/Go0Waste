package daniarachid.donation.Notification;

public class Data {
    String user, body, title, sent;
    private Integer icon;
    int type;
    String requestId, requestedItemTitle;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public  Data(String user, Integer icon, String body, String title, String sent, int type) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.type = type;
    }

    //try this
    public  Data(String user, Integer icon, String body, String title, String sent, String requestId, String requestedItemTitle, int type) {
        this.user = user;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sent = sent;
        this.requestId = requestId;
        this.requestedItemTitle =  requestedItemTitle;
        this.type = type;
    }
    public Data() {}

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }



}
