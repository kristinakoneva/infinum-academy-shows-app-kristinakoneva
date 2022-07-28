package infinumacademy.showsapp.kristinakoneva

import android.content.Context
import android.content.SharedPreferences
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
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinumacademy.showsapp.kristinakoneva.databinding.DialogAddReviewBinding
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowDetailsBinding

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


        displayLoadingScreen()
        initBackButtonFromToolbar()
        initReviewsRecycler()
        initAddReviewButton()
        showReviews()
        displayShow()
    }

    private fun displayLoadingScreen() {
        viewModel.apiCallInProgress.observe(viewLifecycleOwner) { isApiInProgress ->
            binding.loadingProgressOverlay.isVisible = isApiInProgress
        }
    }

    private fun displayShow() {
        viewModel.getShow(args.showId)
        viewModel.showLiveData.observe(viewLifecycleOwner) { show ->
            binding.showName.text = show.title
            binding.showDesc.text = show.description
            binding.showImg.load(show.imageUrl)
        }
        setReviewsStatus()
    }

    private fun showReviews() {
        viewModel.fetchReviewsAboutShow(args.showId)
        viewModel.showLiveData.observe(viewLifecycleOwner) { show ->
            binding.groupShowReviews.isVisible = show.noOfReviews != 0
            binding.noReviews.isVisible = show.noOfReviews == 0
        }
    }

    private fun setReviewsStatus() {
        viewModel.showLiveData.observe(viewLifecycleOwner) { show ->
            val numOfReviews = show.noOfReviews
            val averageRating = show.averageRating
            binding.ratingStatus.rating = String.format("%.2f", averageRating).toFloat()
            binding.reviewsStatus.text = getString(R.string.review_status, numOfReviews, averageRating)
        }
    }

    /*
    private fun setReviewsStatus() {
        val numOfReviews = viewModel.reviewsListLiveData.value?.size
        val averageRating = viewModel.getAverageReviewsRating().toFloat()
        viewModel.reviewsListLiveData.observe(viewLifecycleOwner){ reviewsList->
            if(reviewsList.isNotEmpty()){
                binding.ratingStatus.rating = String.format("%.2f", averageRating).toFloat()
                binding.reviewsStatus.text = getString(R.string.review_status, numOfReviews, averageRating)
            }
        }
    }*/

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
            showAddReviewBottomSheet()
        }
    }

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
            val rating = bottomSheetBinding.rbRating.rating.toInt()
            val comment = bottomSheetBinding.etComment.text.toString()
            val showId = args.showId
            viewModel.addReview(rating, comment, showId)
            displayShow()
            populateRecyclerView()
            showReviews()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

