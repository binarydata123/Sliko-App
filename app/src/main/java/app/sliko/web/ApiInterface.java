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

public interface ApiInterface {
    @FormUrlEncoded
    @POST("user_register")
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


    @FormUrlEncoded
    @POST("pitch/single_detail")
    Call<ResponseBody> ep_pitchDetail(@Field("id") String id);

    @FormUrlEncoded
    @POST("stadium/delete")
    Call<ResponseBody> ep_deleteStadium(@Field("id") String id,
                                        @Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("pitch/delete")
    Call<ResponseBody> ep_deletePitch(@Field("id") String id,
                                      @Field("stadium_id") String stadium_id);

    @GET("playPostionList")
    Call<ResponseBody> ep_playerPosition();

    @FormUrlEncoded
    @POST("stadium/mapStadiumlist")
    Call<ResponseBody> ep_homeListing(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("logout")
    Call<ResponseBody> ep_logout(@Field("user_id") String user_id);

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
    Call<ResponseBody> ep_resetPassword(@Field("user_id") String user_id,
            @Field("email") String email,
                                        @Field("old_password") String old_password,
                                        @Field("new_password") String new_password);

    @FormUrlEncoded
    @POST("user/single_detail")
    Call<ResponseBody> ep_getUserProfile(@Field("user_id") String user_id);

    @FormUrlEncoded
    @POST("pitchreview/getReviews")
    Call<ResponseBody> ep_reviewsForPitch(@Field("user_id") String user_id,
                                          @Field("stadium_id") String stadium_id,
                                          @Field("pitch_id") String pitch_id);

    @FormUrlEncoded
    @POST("pitchreview/getReviews")
    Call<ResponseBody> ep_reviewsAll(@Field("user_id") String user_id,
                                     @Field("stadium_id") String stadium_id);

    @Multipart
    @POST("stadium/create")
    Call<ResponseBody> createStadium(
            @Part MultipartBody.Part[] multipleStadiumImages,
            @Part("stadium_name") RequestBody stadium_name,
            @Part("user_id") RequestBody user_id,
            @Part("description") RequestBody description,
            @Part("address") RequestBody address,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("slot_intervel") RequestBody slot_intervel,
            @Part("open_time") RequestBody open_time,
            @Part("close_time") RequestBody close_time,
            @Part("check_mon") RequestBody check_mon,
            @Part("check_tue") RequestBody check_tue,
            @Part("check_wed") RequestBody check_wed,
            @Part("check_thu") RequestBody check_thu,
            @Part("check_fri") RequestBody check_fri,
            @Part("check_sat") RequestBody check_sat,
            @Part("check_sun") RequestBody check_sun
    );

    @Multipart
    @POST("user/edit_profile")
    Call<ResponseBody> editProfile(
            @Part MultipartBody.Part multipleStadiumImages,
            @Part("user_id") RequestBody user_id,
            @Part("fullname") RequestBody fullname,
            @Part("address") RequestBody address,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("phone") RequestBody phone,
            @Part("height") RequestBody height,
            @Part("weight") RequestBody weight,
            @Part("favourite_team") RequestBody favourite_team,
            @Part("play_position") RequestBody play_position,
            @Part("footedness") RequestBody footedness
    );

    @Multipart
    @POST("user/edit_profile")
    Call<ResponseBody> editProfileWithoutImage(
            @Part("user_id") RequestBody user_id,
            @Part("fullname") RequestBody fullname,
            @Part("address") RequestBody address,
            @Part("lat") RequestBody lat,
            @Part("lng") RequestBody lng,
            @Part("phone") RequestBody phone,
            @Part("height") RequestBody height,
            @Part("weight") RequestBody weight,
            @Part("favourite_team") RequestBody favourite_team,
            @Part("play_position") RequestBody play_position,
            @Part("footedness") RequestBody footedness
    );

    @Multipart
    @POST("pitch/create")
    Call<ResponseBody> createPitch(
            @Part MultipartBody.Part[] pitchMultipleImages,
            @Part("user_id") RequestBody user_id,
            @Part("stadium_id") RequestBody stadium_id,
            @Part("pitch_name") RequestBody pitch_name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price
    );

    @Multipart
    @POST("pitch/update")
    Call<ResponseBody> updatePitch(
            @Part MultipartBody.Part[] pitchMultipleImages,
            @Part("id") RequestBody id,
            @Part("stadium_id") RequestBody stadium_id,
            @Part("pitch_name") RequestBody pitch_name,
            @Part("description") RequestBody description,
            @Part("price") RequestBody price
    );

    @FormUrlEncoded
    @POST("pitchbooking/lists")
    Call<ResponseBody> ep_bookingList(@Field("user_id") String user_id,
                                      @Field("stadium_id") String stadium_id,
                                      @Field("pitch_id") String pitch_id,
                                      @Field("start_date") String start_date,
                                      @Field("end_date") String end_date
    );

    @FormUrlEncoded
    @POST("pitchbooking/getAllPitchBookedlist")
    Call<ResponseBody> ep_userPitchBooking(@Field("user_id") String user_id,
                                           @Field("stadium_id") String stadium_id,
                                           @Field("booking_date") String booking_date);

    @FormUrlEncoded
    @POST("pitchbooking/create")
    Call<ResponseBody> ep_createBooking(@Field("user_id") String user_id,
                                        @Field("cost") String cost,
                                        @Field("booking_date") String booking_date,
                                        @Field("time") String time,
                                        @Field("stadium_id") String stadium_id,
                                        @Field("pitch_id") String pitch_ida
    );
}
