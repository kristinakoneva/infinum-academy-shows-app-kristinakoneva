package infinumacademy.showsapp.kristinakoneva

import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.bottomsheet.BottomSheetDialog
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
import networking.ApiModule
import networking.SessionManager

class ShowsFragment : Fragment() {

    private var _binding: FragmentShowsBinding? = null

    private val binding get() = _binding!!

    private lateinit var adapter: ShowsAdapter

    private val viewModel by viewModels<ShowsViewModel>()

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiModule.initRetrofit(requireContext())
        sessionManager = SessionManager(requireContext())
        sharedPreferences = requireContext().getSharedPreferences(Constants.SHOWS_APP, Context.MODE_PRIVATE)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        displayLoadingScreen()
        showProfilePhoto()
        initShowsRecycler()
        viewModel.fetchShows()
        viewModel.fetchTopRatedShows()
        displayState()
        initListeners()
    }

    private fun displayLoadingScreen() {
        viewModel.apiCallInProgress.observe(viewLifecycleOwner) { isApiInProgress ->
            binding.loadingProgressOverlay.isVisible = isApiInProgress
        }
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

        binding.chipTopRated.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateShowTopRated(isChecked)
        }
    }

    private fun openDialogForChangingProfilePicOrLoggingOut() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetBinding = DialogChangeProfilePhotoOrLogoutBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)

        // User Interface
        val profilePhotoPath = getProfilePhotoPath()
        if (profilePhotoPath != null) {
            try {
                val f = File(profilePhotoPath)
                val b = BitmapFactory.decodeStream(FileInputStream(f))
                bottomSheetBinding.profilePhoto.load(b) {
                    transformations(CircleCropTransformation())
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } else {
            bottomSheetBinding.profilePhoto.load(R.drawable.profile_photo)
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
            takeImage()
            showProfilePhoto()
            dialog.dismiss()
        }

        bottomSheetBinding.imgBtnGallery.setOnClickListener {
            selectImageFromGallery()
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
                putString(Constants.EMAIL, null)
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
        viewModel.showTopRatedLiveData.observe(viewLifecycleOwner) { showTopRatedShows ->
            if (showTopRatedShows) {
                viewModel.topRatedShowsListLiveData.observe(viewLifecycleOwner) { topRatedShows ->
                    adapter.addAllItems(topRatedShows)
                }
            } else {
                viewModel.listShowsResultLiveData.observe(viewLifecycleOwner) { isSuccessful ->
                    if (isSuccessful) {
                        viewModel.showsListLiveData.observe(viewLifecycleOwner) { showsList ->
                            adapter.addAllItems(showsList)
                        }
                    } else {
                        // Toast.makeText(requireContext(), "Fetching shows was unsuccessful", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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
        val directions = ShowsFragmentDirections.toShowDetailsFragment(show.id.toInt())
        findNavController().navigate(directions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun saveToInternalStorage(bitmap: Bitmap, emailAsFileName: String): String? {

        val wrapper = ContextWrapper(requireContext().applicationContext)
        var file = wrapper.getDir(getString(R.string.images), Context.MODE_PRIVATE)
        file = File(file, emailAsFileName)

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

    private fun getProfilePhotoPath(): String? {
        val email = sharedPreferences.getString(Constants.EMAIL, null)
        val profilePhotoPath = sharedPreferences.getString(email, null)
        return profilePhotoPath
    }

    private fun showProfilePhoto() {
        val profilePhotoPath = getProfilePhotoPath()
        if (profilePhotoPath != null) {
            loadImageFromStorage(profilePhotoPath)
        } else {
            binding.btnDialogChangeProfilePicOrLogout.load(R.drawable.btn_profile_photo)
        }
    }

    private fun saveProfilePhoto(uri: Uri) {
        val bitmap = getBitmapFromURI(requireContext(), uri)
        val email = sharedPreferences.getString(Constants.EMAIL, null)
        val ppPath = saveToInternalStorage(bitmap!!, email!!)
        sharedPreferences.edit {
            putString(email, ppPath)
        }
    }

    private val takeImageResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            latestTmpUri?.let { uri ->
                saveProfilePhoto(uri)
                binding.btnDialogChangeProfilePicOrLogout.load(uri) {
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            saveProfilePhoto(uri)
            binding.btnDialogChangeProfilePicOrLogout.load(uri) {
                transformations(CircleCropTransformation())
            }
        }
    }

    private var latestTmpUri: Uri? = null

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireActivity().applicationContext, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
    }

    private fun getBitmapFromURI(context: Context, uri: Uri?): Bitmap? {
        try {
            val input = context.contentResolver.openInputStream(uri!!) ?: return null
            return BitmapFactory.decodeStream(input)
        } catch (e: FileNotFoundException) {
        }
        return null
    }

    /*
    private fun rotateImageIfNecessary(path: String): Bitmap?{
        val bitmap = BitmapFactory.decodeFile(path)
        val file = File(path)
        val exif = ExifInterface(file.absoluteFile.toString())
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()

        when(orientation){
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
        }

        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0,0 , bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }*/


}






