package infinumacademy.showsapp.kristinakoneva.show_details_screen

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

    private val _showLiveData = MutableLiveData<Show>()
    val showLiveData: LiveData<Show> = _showLiveData

    private val _apiCallForFetchingShowInProgress = MutableLiveData(false)
    // val apiCallForFetchingShowInProgress: LiveData<Boolean> = _apiCallForFetchingShowInProgress

    private val _apiCallForFetchingReviewsInProgress = MutableLiveData(false)
    // val apiCallForFetchingReviewsInProgress: LiveData<Boolean> = _apiCallForFetchingReviewsInProgress

    private val _apiCallForCreatingReviewInProgress = MutableLiveData(false)
    // val apiCallForCreatingReviewInProgress: LiveData<Boolean> = _apiCallForCreatingReviewInProgress

    private val _apiCallInProgress = MutableLiveData(false)
    val apiCallInProgress: LiveData<Boolean> = _apiCallInProgress

    private val _getShowResultLiveData = MutableLiveData(true)
    val getShowResultLiveData: LiveData<Boolean> = _getShowResultLiveData

    private val _fetchReviewsResultLiveData = MutableLiveData(true)
    val fetchReviewsLiveData: LiveData<Boolean> = _fetchReviewsResultLiveData

    /*
    fun getAverageReviewsRating(): Double {
        return if (_reviewsListLiveData.value != null) {
            var total = 0.0
            for (review in _reviewsListLiveData.value!!) {
                total += review.rating
            }
            total / _reviewsListLiveData.value!!.size.toDouble()
        } else {
            0.0
        }
    }*/

    fun getShow(showId: Int) {
        _apiCallForFetchingShowInProgress.value = true
        _apiCallInProgress.value = true
        ApiModule.retrofit.displayShow(showId).enqueue(object : Callback<DisplayShowResponse> {
            override fun onResponse(call: retrofit2.Call<DisplayShowResponse>, response: Response<DisplayShowResponse>) {
                _getShowResultLiveData.value = response.isSuccessful
                if (response.isSuccessful) {
                    _showLiveData.value = response.body()?.show
                    _apiCallForFetchingShowInProgress.value = false
                    _apiCallInProgress.value = _apiCallForFetchingReviewsInProgress.value!! || _apiCallForCreatingReviewInProgress.value!!
                }
            }

            override fun onFailure(call: retrofit2.Call<DisplayShowResponse>, t: Throwable) {
                _getShowResultLiveData.value = false
                _apiCallForFetchingShowInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingReviewsInProgress.value!! || _apiCallForCreatingReviewInProgress.value!!
            }

        })
    }

    fun addReview(rating: Int, comment: String?, showId: Int) {
        _apiCallForCreatingReviewInProgress.value = true
        _apiCallInProgress.value = true
        val request = CreateReviewRequest(
            rating = rating,
            comment = comment,
            showId = showId
        )
        ApiModule.retrofit.createReview(request).enqueue(object : Callback<CreateReviewResponse> {
            override fun onResponse(call: retrofit2.Call<CreateReviewResponse>, response: Response<CreateReviewResponse>) {
                if (response.isSuccessful) {
                    response.body()?.review?.let { review ->
                        _reviewsListLiveData.value = _reviewsListLiveData.value?.plus(review)
                    }
                }
                _apiCallForCreatingReviewInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowInProgress.value!! || _apiCallForFetchingReviewsInProgress.value!!
            }

            override fun onFailure(call: retrofit2.Call<CreateReviewResponse>, t: Throwable) {
                _apiCallForCreatingReviewInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowInProgress.value!! || _apiCallForFetchingReviewsInProgress.value!!
            }

        })
    }

    fun fetchReviewsAboutShow(showId: Int) {
        _apiCallForFetchingReviewsInProgress.value = true
        _apiCallInProgress.value = true
        ApiModule.retrofit.fetchReviewsAboutShow(showId).enqueue(object : Callback<ReviewsResponse> {
            override fun onResponse(call: retrofit2.Call<ReviewsResponse>, response: Response<ReviewsResponse>) {
                _fetchReviewsResultLiveData.value = response.isSuccessful
                if (response.isSuccessful) {
                    _reviewsListLiveData.value = response.body()?.reviews
                }
                _apiCallForFetchingReviewsInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowInProgress.value!! || _apiCallForCreatingReviewInProgress.value!!
            }

            override fun onFailure(call: retrofit2.Call<ReviewsResponse>, t: Throwable) {
                _fetchReviewsResultLiveData.value = false
                _apiCallForFetchingReviewsInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowInProgress.value!! || _apiCallForCreatingReviewInProgress.value!!
            }

        })
    }

    fun checkApiInProgress(){
        _apiCallInProgress.value =
            _apiCallForFetchingShowInProgress.value!! || _apiCallForFetchingReviewsInProgress.value!! || _apiCallForCreatingReviewInProgress.value!!
    }

}