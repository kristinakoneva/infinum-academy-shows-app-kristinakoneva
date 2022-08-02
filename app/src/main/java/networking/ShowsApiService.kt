package networking

import model.CreateReviewRequest
import model.CreateReviewResponse
import model.DisplayShowResponse
import model.LoginRequest
import model.LoginResponse
import model.RegisterRequest
import model.RegisterResponse
import model.ReviewsResponse
import model.ShowsResponse
import model.TopRatedShowsResponse
import model.UpdateProfilePhotoResponse
import model.UserInfoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ShowsApiService {
    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("/users/sign_in")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @GET("/shows")
    fun fetchShows(): Call<ShowsResponse>

    @GET("/shows/{show_id}/reviews")
    fun fetchReviewsAboutShow(@Path("show_id") showId: Int): Call<ReviewsResponse>

    @POST("/reviews")
    fun createReview(@Body request: CreateReviewRequest): Call<CreateReviewResponse>

    @GET("/shows/{id}")
    fun displayShow(@Path("id") showId: Int): Call<DisplayShowResponse>

    @GET("/shows/top_rated")
    fun fetchTopRatedShows(): Call<TopRatedShowsResponse>

    @Multipart
    @PUT("/users")
    fun updateProfilePhoto(@Part image: MultipartBody.Part): Call<UpdateProfilePhotoResponse>

    // not needed for now
    @GET("/users/me")
    fun getMyUserInfo(): Call<UserInfoResponse>
}