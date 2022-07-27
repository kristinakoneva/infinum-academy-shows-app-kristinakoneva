package infinumacademy.showsapp.kristinakoneva

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
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
import infinumacademy.showsapp.kristinakoneva.databinding.DialogChangeProfilePhotoOrLogoutBinding
import infinumacademy.showsapp.kristinakoneva.databinding.DialogChooseChangingPofilePhotoMethodBinding
import infinumacademy.showsapp.kristinakoneva.databinding.FragmentShowsBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import model.Show

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private val viewModel by viewModels<ShowsViewModel>()

    private val args by navArgs<ShowsFragmentArgs>()

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val CAMERA_REQUEST_CODE = 1
        const val GALLERY_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = requireContext().getSharedPreferences(Constants.SHOWS_APP, Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showProfilePhoto()
        initShowsRecycler()
        displayState()
        initListeners()
    }

    private fun displayState() {
        viewModel.showEmptyStateLiveData.observe(viewLifecycleOwner) { showEmptyState ->
            if (showEmptyState) {
                hideShows()
            } else {
                showShows()
            }
        }
    }

    private fun initListeners() {
        binding.btnShowHideEmptyState.setOnClickListener {
            viewModel.resetEmptyState()
            displayState()
        }

        binding.btnDialogChangeProfilePicOrLogout.setOnClickListener {
            openDialogForChangingProfilePicOrLoggingOut()
        }
    }

    private fun openDialogForChangingProfilePicOrLoggingOut() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogChangeProfilePhotoOrLogoutBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        // User Interface
        if (sharedPreferences.getBoolean(Constants.PROFILE_PHOTO_CHANGED, false)) {
            try {
                val f = File(sharedPreferences.getString(Constants.PROFILE_PHOTO, getString(R.string.default_text))!!)
                val b = BitmapFactory.decodeStream(FileInputStream(f))
                bottomSheetBinding.profilePhoto.load(b) {
                    transformations(CircleCropTransformation())
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
        bottomSheetBinding.emailAddress.text = sharedPreferences.getString(Constants.EMAIL, getString(R.string.example_email))

        // Listeners
        bottomSheetBinding.btnChangeProfilePhoto.setOnClickListener {
            openDialogForChoosingChangingProfilePhotoMethod()
            dialog.dismiss()
        }

        bottomSheetBinding.btnLogout.setOnClickListener {
            showAreYouSureAlertDialog(dialog)
        }

        dialog.show()
    }

    private fun openDialogForChoosingChangingProfilePhotoMethod() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogChooseChangingPofilePhotoMethodBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)


        bottomSheetBinding.imgBtnCamera.setOnClickListener {
            cameraCheckPermission()
            showProfilePhoto()
            dialog.dismiss()
        }

        bottomSheetBinding.imgBtnGallery.setOnClickListener {
            galleryCheckPermission()
            showProfilePhoto()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showAreYouSureAlertDialog(bottomSheetDialog: BottomSheetDialog) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.logout))
        builder.setMessage(getString(R.string.logging_out_alert_message))

        builder.setPositiveButton(getString(R.string.logout)) { dialog, _ ->
            sharedPreferences.edit {
                putBoolean(Constants.REMEMBER_ME, false)
                putString(Constants.USERNAME, getString(R.string.username_placeholder))
                putBoolean(Constants.PROFILE_PHOTO_CHANGED, false)
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
        binding.showsEmptyState.isVisible = false
        binding.showsRecycler.isVisible = true
    }

    private fun hideShows() {
        adapter.addAllItems(emptyList())
        binding.showsEmptyState.isVisible = true
        binding.showsRecycler.isVisible = false
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
                    getString(R.string.gallery_denied_permission_message),
                    Toast.LENGTH_SHORT
                ).show()
                showRotationalDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?, p1: PermissionToken?
            ) {
                showRotationalDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = getString(R.string.intent_type_image)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun cameraCheckPermission() {

        Dexter.withContext(requireContext())
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(

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
                        p1: PermissionToken?
                    ) {
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

                    val bitmap = data?.extras?.get(getString(R.string.data)) as Bitmap

                    val ppPath = saveToInternalStorage(bitmap)
                    sharedPreferences.edit {
                        putString(Constants.PROFILE_PHOTO, ppPath)
                        putBoolean(Constants.PROFILE_PHOTO_CHANGED, true)
                    }

                }

                GALLERY_REQUEST_CODE -> {

                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, data?.data)
                    val ppPath = saveToInternalStorage(bitmap)
                    sharedPreferences.edit {
                        putString(Constants.PROFILE_PHOTO, ppPath)
                        putBoolean(Constants.PROFILE_PHOTO_CHANGED, true)
                    }

                }
            }
            showProfilePhoto()
        }


    }

    private fun showRotationalDialogForPermission() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.denied_permissions_message))

            .setPositiveButton(getString(R.string.go_to_settings)) { _, _ ->

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

    private fun saveToInternalStorage(bitmap: Bitmap): String? {

        val wrapper = ContextWrapper(requireContext().applicationContext)
        var file = wrapper.getDir(getString(R.string.images), Context.MODE_PRIVATE)
        file = File(file, getString(R.string.profile_photo_jpg))

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun loadImageFromStorage(path: String) {
        try {
            val f = File(path)
            val b = BitmapFactory.decodeStream(FileInputStream(f))
            binding.btnDialogChangeProfilePicOrLogout.load(b) {
                transformations(CircleCropTransformation())
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun showProfilePhoto() {
        val isPhotoChanged = sharedPreferences.getBoolean(Constants.PROFILE_PHOTO_CHANGED, false)
        if (isPhotoChanged) {
            loadImageFromStorage(sharedPreferences.getString(Constants.PROFILE_PHOTO, getString(R.string.default_text))!!)
        }
    }


}






