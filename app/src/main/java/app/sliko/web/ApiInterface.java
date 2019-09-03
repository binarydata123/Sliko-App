package app.sliko.web;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("register")
    Call<ResponseBody> ep_register(@Field("fullname") String fullname,
                                   @Field("email") String email,
                                   @Field("phone") String phone_number,
                                   @Field("password") String password,
                                   @Field("role") String role,
                                   @Field("social_id") String social_id,
                                   @Field("fcm_token") String fcm_token,
                                   @Field("favourite_team") String favourite_team,
                                   @Field("play_position") String playPosition,
                                   @Field("height") String height,
                                   @Field("weight") String weight,
                                   @Field("footedness") String footedness);
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> ep_login(@Field("email") String email,
                                @Field("password") String password,
                                @Field("fcm_token") String fcm_token,
                                @Field("role") String role);

    @FormUrlEncoded
    @POST("stadium/single_detail")
    Call<ResponseBody> ep_stadium_detail(@Field("user_id") String user_id);

    @GET("playPostionList")
    Call<ResponseBody> ep_playerPosition();

    @FormUrlEncoded
    @POST("stadium/mapStadiumlist")
    Call<ResponseBody> ep_homeListing(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("stadium/getSuggestionsList")
    Call<ResponseBody> ep_addressSuggestions(@Field("term") String term);

    @FormUrlEncoded
    @POST("stadium/getAddrLatLong")
    Call<ResponseBody> ep_getLatLng(@Field("address") String address);

    @FormUrlEncoded
    @POST("forgetpassword")
    Call<ResponseBody> ep_forgetpassword(@Field("email") String email);

    @FormUrlEncoded
    @POST("resetpassword")
    Call<ResponseBody> ep_resetPassword(@Field("email") String email,
                                        @Field("new_password") String new_password);

    @FormUrlEncoded
    @POST("user/single_detail")
    Call<ResponseBody> ep_getUserProfile(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("pitchreview/getReviews")
    Call<ResponseBody> ep_allReviews(@Field("user_id") String user_id);

    @Multipart
    @POST("stadium/create")
    Call<ResponseBody> createStadium(
            @Part MultipartBody.Part[] multipleStadiumImages,
            @Part("stadium_name") RequestBody stadium_name,
            @Part("user_id") RequestBody user_id,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("lat") RequestBody lat,
            @Part("long") RequestBody lng);


    @FormUrlEncoded
    @POST("resetpassword")
    Call<ResponseBody> ep_bookingList(@Field("user_id") String user_id,
                                        @Field("stadium_id") String stadium_id);

    @FormUrlEncoded
    @POST("resetpassword")
    Call<ResponseBody> ep_ownerBookingList(@Field("email") String email,
                                        @Field("new_password") String new_password);
}
