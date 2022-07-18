package model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class Show(
    val id: Int,
    val name: String,
    val description: String,
    @DrawableRes val imageResourceId: Int
) : Parcelable
