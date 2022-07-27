package infinumacademy.showsapp.kristinakoneva

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinumacademy.showsapp.kristinakoneva.databinding.DialogAddReviewBinding
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowDetailsBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import model.Show

class ShowDetailsFragment : Fragment() {

    private var _binding: FragmentShowDetailsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ReviewsAdapter

    private val args by navArgs<ShowDetailsFragmentArgs>()

    private val viewModel by viewModels<ShowDetailsViewModel>()

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(Constants.SHOWS_APP, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        showReviews()
        displayShow(args.show)
        initBackButtonFromToolbar()
        initReviewsRecycler()
        initAddReviewButton()
        setReviewsStatus()
        viewModel.fetchReviewsAboutShow(args.show)
    }

    private fun displayShow(show: Show) {
        binding.showName.text = show.title
        binding.showDesc.text = show.description
        binding.showImg.load(show.imageUrl)
    }

    private fun showReviews(){
        viewModel.reviewsListLiveData.observe(viewLifecycleOwner){reviewsList->
            binding.groupShowReviews.isVisible = reviewsList.isNotEmpty()
            binding.noReviews.isVisible = reviewsList.isEmpty()
        }
    }
    private fun setReviewsStatus() {
        val numOfReviews = viewModel.reviewsListLiveData.value?.size
        val averageRating = viewModel.getAverageReviewsRating().toFloat()
        viewModel.reviewsListLiveData.observe(viewLifecycleOwner){ reviewsList->
            if(reviewsList.isNotEmpty()){
                binding.ratingStatus.rating = String.format("%.2f", averageRating).toFloat()
                binding.reviewsStatus.text = getString(R.string.review_status, numOfReviews, averageRating)
            }
        }
    }

    private fun initBackButtonFromToolbar() {
        binding.showDetailsToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun populateRecyclerView() {
        viewModel.reviewsListLiveData.observe(viewLifecycleOwner) { reviewsList ->
            adapter.addAllItems(reviewsList)
        }
    }

    private fun initReviewsRecycler() {
        adapter = ReviewsAdapter(listOf())

        populateRecyclerView()

        binding.reviewsRecycler.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.reviewsRecycler.adapter = adapter

        binding.reviewsRecycler.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )
    }

    private fun initAddReviewButton() {
        binding.btnWriteReview.setOnClickListener {
            //showAddReviewBottomSheet()
        }

        setReviewsStatus()
    }

    /*
    private fun showAddReviewBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogAddReviewBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.rbRating.setOnRatingBarChangeListener { _, _, _ ->
            bottomSheetBinding.btnSubmitReview.isEnabled = true
        }
        bottomSheetBinding.btnCloseDialog.setOnClickListener {
            dialog.dismiss()
        }

        bottomSheetBinding.btnSubmitReview.setOnClickListener {
            addReviewToList(bottomSheetBinding.rbRating.rating.toDouble(), bottomSheetBinding.etComment.text.toString())
            showReviews()
            setReviewsStatus()
            dialog.dismiss()
        }

        dialog.show()
    }

     */

    /*
    private fun addReviewToList(rating: Double, comment: String) {
        val username = sharedPreferences.getString(Constants.USERNAME, getString(R.string.username_placeholder))
        viewModel.addReviewToList(rating, comment, username!!)
        populateRecyclerView()
        setReviewsStatus()
    }
        */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

