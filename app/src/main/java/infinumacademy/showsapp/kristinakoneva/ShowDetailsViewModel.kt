package infinumacademy.showsapp.kristinakoneva

import android.telecom.Call
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.CreateReviewRequest
import model.CreateReviewResponse
import model.DisplayShowResponse
import model.Review
import model.ReviewsResponse
import model.Show
import networking.ApiModule
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {

    private val _reviewsListLiveData = MutableLiveData<List<Review>>(listOf())
    val reviewsListLiveData: LiveData<List<Review>> = _reviewsListLiveData

    private val _show = MutableLiveData<Show>()
    val show: LiveData<Show> = _show


    fun getAverageReviewsRating(): Double {
        return if(_reviewsListLiveData.value != null){
            var total = 0.0
            for (review in _reviewsListLiveData.value!!) {
                total += review.rating
            }
            total / _reviewsListLiveData.value!!.size.toDouble()
        } else{
            0.0
        }
    }

    fun getShow(showId: Int){
        ApiModule.retrofit.displayShow(showId).enqueue(object: Callback<DisplayShowResponse>{
            override fun onResponse(call: retrofit2.Call<DisplayShowResponse>, response: Response<DisplayShowResponse>) {
                if(response.isSuccessful){
                    _show.value = response.body()!!.show
                }
            }

            override fun onFailure(call: retrofit2.Call<DisplayShowResponse>, t: Throwable) {
               // TODO("Not yet implemented")
            }

        })
    }
    fun addReview(rating: Int, comment: String?, showId: Int){
        val request = CreateReviewRequest(
            rating = rating,
            comment = comment,
            showId = showId
        )
        ApiModule.retrofit.createReview(request).enqueue(object: Callback<CreateReviewResponse>{
            override fun onResponse(call: retrofit2.Call<CreateReviewResponse>, response: Response<CreateReviewResponse>) {
                if(response.isSuccessful){
                    _reviewsListLiveData.value = _reviewsListLiveData.value!! + response.body()!!.review
                }
            }

            override fun onFailure(call: retrofit2.Call<CreateReviewResponse>, t: Throwable) {
                // TODO("Not yet implemented")
            }

        })
    }

    fun fetchReviewsAboutShow(showId: Int){
        ApiModule.retrofit.fetchReviewsAboutShow(showId).enqueue(object: Callback<ReviewsResponse>{
            override fun onResponse(call: retrofit2.Call<ReviewsResponse>, response: Response<ReviewsResponse>) {
                if(response.isSuccessful){
                    _reviewsListLiveData.value = response.body()!!.reviews
                }
            }

            override fun onFailure(call: retrofit2.Call<ReviewsResponse>, t: Throwable) {
               // TODO("Not yet implemented")
            }

        })
    }


}