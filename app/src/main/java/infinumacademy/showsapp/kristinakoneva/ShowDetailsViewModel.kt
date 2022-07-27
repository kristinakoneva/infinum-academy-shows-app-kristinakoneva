package infinumacademy.showsapp.kristinakoneva

import android.telecom.Call
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.CreateReviewRequest
import model.CreateReviewResponse
import model.Review
import model.ReviewsResponse
import model.Show
import networking.ApiModule
import retrofit2.Callback
import retrofit2.Response

class ShowDetailsViewModel : ViewModel() {

    private val _reviewsListLiveData = MutableLiveData<List<Review>>(listOf())
    val reviewsListLiveData: LiveData<List<Review>> = _reviewsListLiveData


    //fun addReviewToList(rating: Double, comment: String, username: String) {
   //     val review = Review(rating, comment, username)
    //    _reviewsListLiveData.value = _reviewsListLiveData.value?.plus(review)
   // }

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

    fun fetchReviewsAboutShow(show: Show){
        ApiModule.retrofit.fetchReviewsAboutShow(Integer.parseInt(show.id)).enqueue(object: Callback<ReviewsResponse>{
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