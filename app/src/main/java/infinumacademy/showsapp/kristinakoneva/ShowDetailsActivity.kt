package infinumacademy.showsapp.kristinakoneva

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinumacademy.showsapp.kristinakoneva.databinding.ActivityShowDetailsBinding
import infinumacademy.showsapp.kristinakoneva.databinding.DialogAddReviewBinding
import model.Review
import model.Show


class ShowDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowDetailsBinding
    private lateinit var adapter: ShowDetailsAdapter
    private var reviewsList = listOf<Review>()

    companion object{
        const val USERNAME = "USERNAME"
        const val SHOW = "SHOW"
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity,ShowDetailsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        displayShow()
        getUsername()
        btnGoBack()
        initReviewsRecycler()
        initAddReviewButton()
        setReviewsStatus()
    }

    private fun getUsername(): String? {
        return intent.getStringExtra(USERNAME)
    }

    private fun getAverageReviewsRating(): Double {
        var total=0.0
        for(review in reviewsList){
            total+=review.rating
        }
        return total/reviewsList.count().toDouble()
    }

    private fun setReviewsStatus(){
        val numOfReviews = reviewsList.count()
        val averageRating = getAverageReviewsRating()
        binding.ratingStatus.rating = String.format("%.2f",averageRating.toFloat()).toFloat()
        binding.reviewsStatus.text =  String.format("%d REVIEWS, %.2f AVERAGE",numOfReviews,averageRating.toFloat())

    }


    private fun btnGoBack(){
        binding.btnGoBack.setOnClickListener{
            val intent = ShowsActivity.buildIntent(this)
            startActivity(intent)
        }
    }

    private fun displayShow(){
        val show = getIntent ().getExtras()?.getParcelable<Show>(SHOW) as Show
        binding.showName.text = show.name
        binding.showDesc.text = show.description
        binding.showImg.setImageResource(show.imageResourceId)
    }

    private fun initReviewsRecycler(){
        adapter = ShowDetailsAdapter(reviewsList)

        binding.reviewsRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)

        binding.reviewsRecycler.adapter = adapter

        binding.reviewsRecycler.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }

    private fun initAddReviewButton(){
        binding.btnWriteReview.setOnClickListener{
            showAddReviewBottomSheet()
        }
        setReviewsStatus()
    }
    private fun showAddReviewBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.rbRating.setOnRatingBarChangeListener { _, _, _ ->
            bottomSheetBinding.btnSubmitReview.isEnabled = true
        }
        bottomSheetBinding.btnCloseDialog.setOnClickListener{
            dialog.dismiss()
        }

        bottomSheetBinding.btnSubmitReview.setOnClickListener {
            addReviewToList(bottomSheetBinding.rbRating.rating.toDouble(),bottomSheetBinding.etComment.text.toString())
            dialog.dismiss()
            showReviews()
            setReviewsStatus()
        }

        dialog.show()
    }

    private fun showReviews(){
        binding.groupShowReviews.isVisible = true
        binding.noReviews.isVisible = false
    }

    private fun addReviewToList(rating: Double, comment: String){
        val username = getUsername()
        adapter.addItem(Review(rating,comment,username))
        reviewsList = reviewsList + Review(rating,comment,username)
        setReviewsStatus()
    }
}