package infinumacademy.showsapp.kristinakoneva

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinumacademy.showsapp.kristinakoneva.databinding.DialogAddReviewBinding
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowDetailsBinding
import model.Review


class ShowDetailsFragment : Fragment(){

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ReviewsAdapter

    private val args by navArgs<ShowDetailsFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding =  FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displayShow()
        initBackButtonFromToolbar()
        initReviewsRecycler()
        initAddReviewButton()
        setReviewsStatus()
    }


    private fun getAverageReviewsRating(): Double {
        var total=0.0
        for(review in adapter.getAllItems()){
            total+=review.rating
        }
        return total/adapter.getAllItems().count().toDouble()
    }

    private fun setReviewsStatus(){
        val numOfReviews = adapter.getAllItems().count()
        val averageRating = getAverageReviewsRating()
        binding.ratingStatus.rating = String.format("%.2f",averageRating.toFloat()).toFloat()
        binding.reviewsStatus.text = getString(R.string.review_status,numOfReviews,averageRating.toFloat())

    }

    private fun initBackButtonFromToolbar(){
        binding.showDetailsToolbar.setNavigationOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun displayShow(){
        val show = args.show
        binding.showName.text = show.name
        binding.showDesc.text = show.description
        binding.showImg.setImageResource(show.imageResourceId)
    }

    private fun initReviewsRecycler(){
        adapter = ReviewsAdapter(emptyList())

        binding.reviewsRecycler.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL,false)

        binding.reviewsRecycler.adapter = adapter

        binding.reviewsRecycler.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
    }

    private fun initAddReviewButton(){
        binding.btnWriteReview.setOnClickListener{
            showAddReviewBottomSheet()
        }
        setReviewsStatus()
    }
    private fun showAddReviewBottomSheet(){
        val dialog = BottomSheetDialog(requireContext())
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
            showReviews()
            setReviewsStatus()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showReviews(){
        binding.groupShowReviews.isVisible = true
        binding.noReviews.isVisible = false
    }

    private fun addReviewToList(rating: Double, comment: String){
        val username = args.username
        adapter.addItem(Review(rating,comment,username))
        setReviewsStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}