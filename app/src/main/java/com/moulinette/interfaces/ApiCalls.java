package com.moulinette.interfaces;


import com.google.gson.JsonElement;
import com.moulinette.models.change_password.ChangePasswordRequest;
import com.moulinette.models.change_password.ChangePasswordResponce;
import com.moulinette.models.draws.ContestResponce;
import com.moulinette.models.fb_login.FBLoginRequest;
import com.moulinette.models.forget_password.ForgotpasswdRequest;
import com.moulinette.models.forget_password.ForgotpasswdResponse;
import com.moulinette.models.login.LoginRequest;
import com.moulinette.models.login.LoginResponse;
import com.moulinette.models.profile.GetProfileResponse;
import com.moulinette.models.profile.UpdateProfileRequest;
import com.moulinette.models.quiz.QuizTypeRequest;
import com.moulinette.models.quiz.QuizTypeResponce;
import com.moulinette.models.quiz.play_board.QuizResponce;
import com.moulinette.models.quiz.play_board.QuizSubmitionRequest;
import com.moulinette.models.quiz.play_board.QuizSubmitionResponce;
import com.moulinette.models.quiz.results.QuizResultResponce;
import com.moulinette.models.signup.SignupRequest;
import com.moulinette.models.signup.SignupResponse;
import com.moulinette.models.sms.SM;
import com.moulinette.models.sms.SMSPostRequest;
import com.moulinette.models.sms.short_codes.GetShortCodeRequest;
import com.moulinette.models.sms.short_codes.GetShortCodeResponce;
import com.moulinette.models.vote.VoteSubmitRequest;
import com.moulinette.models.vote.VoteTypeResponce;
import com.moulinette.models.vote.contest.VoteContestResponce;
import com.moulinette.models.vote.results.VoteResultsReseponce;
import com.moulinette.models.vote.submit.VoteSubmitReseponce;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;



public interface ApiCalls {


 //###############App User Section###################
 @POST("authentication/registration/")
 Call<SignupResponse> signUp(@Header("Accept-Language") String language, @Body SignupRequest request);

 @GET()
 Call<GetProfileResponse> getProfile(@Url String url);

 @POST("facebooklogin/loginfacebook/")
 Call<JsonElement> fblogin(@Header("Content-Type") String content_type,
                           @Body FBLoginRequest request);

 @POST("authentication/login/")
 Call<JsonElement> login(@Header("Content-Type") String content_type,
                         @Body LoginRequest request);

 @POST("authentication/forgetpassword/")
 Call<ForgotpasswdResponse> forgotPassword(@Body ForgotpasswdRequest request);


 @POST("authentication/deleteuser/")
 Call<QuizTypeRequest> deleteAccount(@Body QuizTypeRequest request);

 @PUT("authentication/user/")
 Call<GetProfileResponse> updateUser(@Body UpdateProfileRequest request);

 @PUT("authentication/changepassword/")
 Call<ChangePasswordResponce> changePassword(@Body ChangePasswordRequest request);

 //###############App User Section###################


 //###############Quiz Section###################

 @POST("authentication/listquiz/")
 Call<QuizTypeResponce> getQuizTypes(@Body QuizTypeRequest request);

 @POST("authentication/getresultquiz/")
 Call<QuizResultResponce> getQuizResults(@Body QuizSubmitionRequest request);

 @GET
 Call<QuizResponce> getQuiz(@Url String url);

 @POST("authentication/submitresult/")
 Call<QuizSubmitionResponce> submitQuiz(@Body QuizSubmitionRequest body);

 //###############Quiz Section###################


 //###############Vote Section###################

 @GET
 Call<VoteContestResponce> getContestant(@Url String url);

 @POST("Voteapi/listvotes/")
 Call<VoteTypeResponce> getVoteContestTypes(@Body QuizSubmitionRequest request);

 @POST("Voteapi/submitvote/")
 Call<VoteSubmitReseponce> submitVote(@Body VoteSubmitRequest body);

 @POST("Voteapi/getresultvote/")
 Call<VoteResultsReseponce> getVoteResults(@Body QuizSubmitionRequest request);

 @POST("challengeapi/getchallenge/")
 Call<ContestResponce> getContestResults(@Body QuizSubmitionRequest request);

 //###############Vote Section###################


 //###############SMS Section###################


 @POST("Challengeusernumber/smsraffle/")
 Call<QuizSubmitionResponce> postSMS(@Body SMSPostRequest body);

 @POST("Challengeusernumber/shortCode/")
 Call<GetShortCodeResponce> getShortCodes(@Body GetShortCodeRequest body);


 //###############SMS Section###################


//
// @DELETE()
// Call<JsonElement> deleteAccount(@Header("Accept-Language") String language, @Url String url, @Header("Authorization") String authorization);
//
//
// @GET("/api/v0.1/base/categories/")
// Call<SongCatResponse> getCategories(@Header("Accept-Language") String language, @Header("Authorization") String authorization);
//
// @GET()
// Call<SongResponse> getSongs(@Header("Accept-Language") String language, @Url String url, @Header("Authorization") String authorization);
//
// @GET("base/recent-songs/")
// Call<GetRecentResponce> getRecent(@Header("Accept-Language") String language, @Header("Authorization") String authorization);
//



//
// @DELETE
// Call<FeedResponce> deleteFeed(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Url String url);
//
// @POST()
// Call<JsonElement> refreshToken(@Header("Accept-Language") String language, @Url String url, @Body RefreshTockenRequest request);
//
//
// @POST
// Call<AddLikeResponse> addLike(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Url String url);
//
// @POST("account/emotion-like/")
// Call<AddLikeTypeResponce> addLikeType(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Body AddLikeTypeRequest request);
//
//
// @POST
// Call<AddFollowResponce> addFollow(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Url String url);
//
// @GET
// Call<GetCommentResponce> getComments(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Url String url);
//
// @POST("/api/v0.1/account/comments/")
// Call<Result> addComment(@Header("Accept-Language") String language, @Header("Authorization") String authorization,
//                         @Body AddCommentRequest request);
//
// @GET()
// Call<UserProfileResponce> getUserProfile(@Header("Accept-Language") String language, @Url String url, @Header("Authorization") String authorization);
//
//
// @GET()
// Call<List<FAQResponce>> getFaq(@Header("Accept-Language") String language, @Url String url, @Header("Authorization") String authorization);
//
// @GET
// Call<List<SettingsResponce>> getSettings(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Url String url);
//
// @PATCH
// Call<SettingUpdateResponce> updateSettings(@Header("Accept-Language") String language, @Url String url, @Header("Authorization") String authorization,
//                                            @Body SettingsResponce request);
//
//
// @GET
// Call<List<PlansResponce>> getPlans(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Url String url);
//
// @GET
// Call<FeedResponce> getModrationFeeds(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Url String url);
//
// @POST("account/moderation/")
// Call<FeedModrationResponce> acceptReject(@Header("Accept-Language") String language, @Header("Authorization") String authorization, @Body FeedModrationRequest request);
//
// @POST("base/redeem-voucher/")
// Call<VoucherResponce> applyVoucher(@Header("Accept-Language") String language, @Header("Authorization") String authorization,
//                                    @Body VoucherRequest request);



// @Multipart
// @POST("updateProfile")
// Call<JsonElement> updateUser(
//         @Part("id") RequestBody id,
//         @Part("name") RequestBody age,
//         @Part("phone") RequestBody country,
//         @Part("email") RequestBody email,
//         @Part("date_of_birth") RequestBody firstname,
//         @Part("password") RequestBody gender,
//         @Part MultipartBody.Part image);


// @Multipart
// @POST("account/feeds/")
// Call<JsonElement> uploadFeeds(@Header("Accept-Language") String language, @Header("Authorization") String authorization,
//                               @Part("is_approved") RequestBody title,
//                               @Part("is_audio ") RequestBody is_audio,
//                               @Part("book") RequestBody book,
//                               @Part MultipartBody.Part files);


}