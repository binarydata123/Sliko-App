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
                                   @Field("fcm_token") String fcm_token);
    @FormUrlEncoded
    @POST("login")
    Call<ResponseBody> ep_login(@Field("email") String email,
                                @Field("password") String password,
                                @Field("fcm_token") String fcm_token,
                                @Field("role") String role);

    @FormUrlEncoded
    @POST("stadium/single_detail")
    Call<ResponseBody> ep_stadium_detail(@Field("user_id") String user_id);

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

    @Multipart
    @POST("stadium/create")
    Call<ResponseBody> createStadium(
            @Part MultipartBody.Part[] choir_profile,
            @Part("name") RequestBody user_id,
            @Part("user_id") RequestBody group_name,
            @Part("description") RequestBody group_desc,
            @Part("address") RequestBody group_city,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng);


    @FormUrlEncoded
    @POST("resetpassword")
    Call<ResponseBody> ep_bookingList(@Field("email") String email,
                                        @Field("new_password") String new_password);

    @FormUrlEncoded
    @POST("resetpassword")
    Call<ResponseBody> ep_ownerBookingList(@Field("email") String email,
                                        @Field("new_password") String new_password);
}
