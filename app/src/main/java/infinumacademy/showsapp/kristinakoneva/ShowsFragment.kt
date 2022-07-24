package infinumacademy.showsapp.kristinakoneva

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import android.provider.Settings
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowsBinding
import infinumacademy.showsapp.kristinakoneva.databinding.DialogChangeProfilePhotoOrLogoutBinding
import infinumacademy.showsapp.kristinakoneva.databinding.DialogChooseChangingPofilePhotoMethodBinding
import model.Show

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private val viewModel by viewModels<ShowsViewModel>()

    private val args by navArgs<ShowsFragmentArgs>()

    private lateinit var sharedPreferences: SharedPreferences

    private val CAMERA_REQUEST_CODE = 1
    private val GALLERY_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(SHOWS_APP, Context.MODE_PRIVATE)
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
            openDialogForChoosingChangingProfilePhotoMethod()
            dialog.dismiss()
        }

        bottomSheetBinding.btnLogout.setOnClickListener {
            showAreYouSureAlertDialog(dialog)
        }
        dialog.show()
    }

    private fun openDialogForChoosingChangingProfilePhotoMethod(){
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogChooseChangingPofilePhotoMethodBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.imgBtnCamera.setOnClickListener {
            cameraCheckPermission()

            dialog.dismiss()
        }

        bottomSheetBinding.imgBtnGallery.setOnClickListener {
            galleryCheckPermission()

            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showAreYouSureAlertDialog(bottomSheetDialog: BottomSheetDialog) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.logout))
        builder.setMessage("Are you sure that you want to logout?")

        builder.setPositiveButton(getString(R.string.logout)) { dialog, _ ->
            sharedPreferences.edit {
                putBoolean(REMEMBER_ME, false)
                putString(USERNAME, getString(R.string.username_placeholder))
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






    private fun galleryCheckPermission() {

        Dexter.withContext(requireContext()).withPermission(
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(
                    requireContext(),
                    "You have denied the storage permission to select an image.",
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    private fun cameraCheckPermission() {

        Dexter.withContext(requireContext())
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA).withListener(

                object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {

                            if (report.areAllPermissionsGranted()) {
                                camera()
                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>?,
                        p1: PermissionToken?) {
                        showRotationalDialogForPermission()
                    }

                }
            ).onSameThread().check()
    }


    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            when (requestCode) {

                CAMERA_REQUEST_CODE -> {

                    val bitmap = data?.extras?.get("data") as Bitmap
                    


                    binding.btnDialogChangeProfilePicOrLogout.load(bitmap) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }
                }

                GALLERY_REQUEST_CODE -> {

                    binding.btnDialogChangeProfilePicOrLogout.load(data?.data) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }

                }
            }

        }

    }


    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(requireContext())
            .setMessage("It looks like you have turned off the permissions"
                + "required for this feature. They can be enabled under App settings.")

            .setPositiveButton("Go TO SETTINGS") { _, _ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireContext().packageName, null)
                    intent.data = uri
                    startActivity(intent)

                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }

            .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }



}






