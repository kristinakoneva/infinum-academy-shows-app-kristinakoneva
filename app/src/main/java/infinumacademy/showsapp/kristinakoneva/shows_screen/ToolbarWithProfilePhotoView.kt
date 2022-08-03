package infinumacademy.showsapp.kristinakoneva.shows_screen

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.Toolbar
import androidx.core.content.withStyledAttributes
import coil.load
import coil.transform.CircleCropTransformation
import infinumacademy.showsapp.kristinakoneva.R
import infinumacademy.showsapp.kristinakoneva.databinding.ViewToolbarWithProfilePhotoBinding

class ToolbarWithProfilePhotoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): Toolbar(context, attrs, defStyleAttr) {
    var binding: ViewToolbarWithProfilePhotoBinding
    init{
        binding = ViewToolbarWithProfilePhotoBinding.inflate(LayoutInflater.from(context),this)
    }

   fun setProfilePhoto(imageUrl: String?){
        if (imageUrl != null) {
            binding.btnDialogChangeProfilePicOrLogout.load(imageUrl) {
                transformations(CircleCropTransformation())
            }
        } else {
            binding.btnDialogChangeProfilePicOrLogout.load(R.drawable.btn_profile_photo)
        }
    }

}