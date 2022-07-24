package infinumacademy.showsapp.kristinakoneva

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowsBinding
import infinumacademy.showsapp.kristinakoneva.databinding.DialogChangeProfilePhotoOrLogoutBinding
import model.Show

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private val viewModel by viewModels<ShowsViewModel>()

    private val args by navArgs<ShowsFragmentArgs>()

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences("ShowsApp", Context.MODE_PRIVATE)
    }

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
            viewModel.showEmptyStateLiveData.observe(viewLifecycleOwner) { showEmptyState ->
                if (showEmptyState) {
                    hideShows()
                } else {
                    showShows()
                }
                viewModel.resetVisibility(binding)
            }

        }

        binding.btnDialogChangeProfilePicOrLogout.setOnClickListener {
            openDialogForChangingProfilePicOrLoggingOut()
        }
    }

    private fun openDialogForChangingProfilePicOrLoggingOut() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogChangeProfilePhotoOrLogoutBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.emailAddress.text = sharedPreferences.getString(EMAIL,getString(R.string.example_email))
        bottomSheetBinding.btnChangeProfilePhoto.setOnClickListener {
            // TODO: implement choosing a photo from gallery or camera
        }

        bottomSheetBinding.btnLogout.setOnClickListener {
            showAreYouSureAlertDialog(dialog)
        }
        dialog.show()
    }

    private fun showAreYouSureAlertDialog(bottomSheetDialog: BottomSheetDialog) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure that you want to logout?")

        builder.setPositiveButton(getString(R.string.logout)) { dialog, _ ->
            sharedPreferences.edit {
                putBoolean(REMEMBER_ME, false)
                putString(USERNAME, "username")
            }
            dialog.dismiss()
            bottomSheetDialog.dismiss()
            findNavController().navigate(R.id.toLoginFragment)
        }

        builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showShows() {
        viewModel.showsListLiveData.observe(viewLifecycleOwner) { showsList ->
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