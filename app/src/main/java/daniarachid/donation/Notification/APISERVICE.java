package daniarachid.donation.Notification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APISERVICE {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAhJLUCKI:APA91bF2U4O-I_3sPmc9tqAr9BjARkHSbQqkSfET4Yo2izXVwIDmX-aC_6StGe17SXdWpo0EqjQ0JY10yXkb0EFVawHEdaM5TBqtZLUXvK6hwBVugUm7tPQ_y5DLPgW4E2a1_lh_l0sZ"
    })

    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);


}
