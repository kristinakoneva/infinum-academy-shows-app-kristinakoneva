package networking

import model.LoginRequest
import model.LoginResponse
import model.RegisterRequest
import model.RegisterResponse
import model.ReviewsResponse
import model.ShowsResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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
}