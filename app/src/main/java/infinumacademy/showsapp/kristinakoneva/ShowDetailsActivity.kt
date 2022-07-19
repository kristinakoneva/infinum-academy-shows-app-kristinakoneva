package infinumacademy.showsapp.kristinakoneva

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
        fun buildIntent(activity: Activity): Intent {
            return Intent(activity,ShowDetailsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityShowDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val show = getIntent ().getExtras()?.getParcelable<Show>("SHOW") as Show
        binding.showName.text = show.name
        binding.showDesc.text = show.description
        binding.showImg.setImageResource(show.imageResourceId)

        binding.btnGoBack.setOnClickListener{
            val intent = ShowsActivity.buildIntent(this)
            startActivity(intent)
        }

        initReviewsRecycler()
        initAddReviewButton()

    }
    private fun initReviewsRecycler(){
        adapter = ShowDetailsAdapter(reviewsList)

        binding.reviewsRecycler.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)

        binding.reviewsRecycler.adapter = adapter
    }
    private fun initAddReviewButton(){
        binding.btnWriteReview.setOnClickListener{
            showAddReviewBottomSheet()
        }
    }
    private fun showAddReviewBottomSheet(){
        val dialog = BottomSheetDialog(this)
        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.btnSubmitReview.setOnClickListener {
            addReviewToList(bottomSheetBinding.rbRating.rating.toDouble(),bottomSheetBinding.etComment.text.toString())
            dialog.dismiss()
        }
        dialog.show()
    }
    private fun addReviewToList(rating: Double, comment: String){
        adapter.addItem(Review(rating,comment))
    }
}