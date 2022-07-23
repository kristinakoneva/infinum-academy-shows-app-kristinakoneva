package infinumacademy.showsapp.kristinakoneva

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowsBinding
import model.Show

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private val viewModel by viewModels<ShowsViewModel>()

    private val args by navArgs<ShowsFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initShowsRecycler()
        showShows()
        initListeners()
    }

    private fun initListeners() {
        binding.btnShowHideEmptyState.setOnClickListener {
            viewModel.showEmptyStateLiveData.observe(viewLifecycleOwner){showEmptyState->
                if (showEmptyState) {
                    hideShows()
                } else {
                    showShows()
                }
                viewModel.resetVisibility(binding)
            }

        }

        binding.btnLogout.setOnClickListener {
            findNavController().navigate(R.id.toLoginFragment)
        }
    }


    private fun showShows() {
        viewModel.showsListLiveData.observe(viewLifecycleOwner){ showsList ->
            adapter.addAllItems(showsList)
        }
    }

    private fun hideShows() {
        adapter.addAllItems(emptyList())
    }

    private fun initShowsRecycler() {
        adapter = ShowsAdapter(emptyList()) { show ->
            showDetailsAbout(show)
        }

        binding.showsRecycler.layoutManager = LinearLayoutManager(requireContext())

        binding.showsRecycler.adapter = adapter
    }

    private fun showDetailsAbout(show: Show) {
        val username = args.username
        val directions = ShowsFragmentDirections.toShowDetailsFragment(username, show)
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}