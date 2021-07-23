package daniarachid.donation.Notification;

public class RequestData {
    String user, body , requestId, title, sent;
    private Integer icon;


    public RequestData(String user, String body, String requestId, String title, String sent, Integer icon) {
        this.user = user;
        this.body = body;
        this.requestId = requestId;
        this.title = title;
        this.sent = sent;
        this.icon = icon;
    }





    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }



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

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }



    public RequestData() {
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
