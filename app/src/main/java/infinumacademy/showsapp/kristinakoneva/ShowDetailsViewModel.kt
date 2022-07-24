package infinumacademy.showsapp.kristinakoneva

import android.content.res.Resources
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowDetailsBinding
import model.Review
import model.Show

class ShowDetailsViewModel : ViewModel() {

    private val _reviewsListLiveData = MutableLiveData<List<Review>>(listOf())
    val reviewsListLiveData: LiveData<List<Review>> = _reviewsListLiveData

    private val _showReviewsLiveData = MutableLiveData(reviewsListLiveData.value!!.isNotEmpty())
    // val showReviewsLiveData: LiveData<Boolean> = _showReviewsLiveData

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

    fun showReviews(binding: FragmentShowDetailsBinding) {
        _showReviewsLiveData.value = _reviewsListLiveData.value!!.isNotEmpty()
        binding.groupShowReviews.isVisible = _showReviewsLiveData.value!!
        binding.noReviews.isVisible = !_showReviewsLiveData.value!!
    }

    fun displayShow(binding: FragmentShowDetailsBinding, show: Show) {
        binding.showName.text = show.name
        binding.showDesc.text = show.description
        binding.showImg.setImageResource(show.imageResourceId)
    }

    fun setReviewsStatus(binding: FragmentShowDetailsBinding, reviewStatus: String) {
        if (_reviewsListLiveData.value!!.isNotEmpty()) {
            binding.ratingStatus.rating = String.format("%.2f", getAverageReviewsRating().toFloat()).toFloat()
            binding.reviewsStatus.text = reviewStatus
            //binding.reviewsStatus.text = Resources.getSystem().getString(R.string.review_status,_reviewsListLiveData.value?.size,getAverageReviewsRating().toFloat())
        }
    }


}