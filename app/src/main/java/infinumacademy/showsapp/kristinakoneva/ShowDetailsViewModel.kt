package infinumacademy.showsapp.kristinakoneva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Review


class ShowDetailsViewModel : ViewModel() {

    private val _reviewsListLiveData = MutableLiveData<List<Review>>(listOf())
    val reviewsListLiveData: LiveData<List<Review>> = _reviewsListLiveData


    fun addReviewToList(rating: Double, comment: String, username: String) {
        val review = Review(rating, comment, username)
        _reviewsListLiveData.value = _reviewsListLiveData.value?.plus(review)
    }

    fun getAverageReviewsRating(): Double {
        var total = 0.0
        for (review in _reviewsListLiveData.value!!) {
            total += review.rating
        }
        return total / _reviewsListLiveData.value!!.size.toDouble()
    }


}