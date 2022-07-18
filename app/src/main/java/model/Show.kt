package model

import androidx.annotation.DrawableRes

data class Show(
    val id: Int,
    val name: String,
    val description: String,
    @DrawableRes val imageResourceId: Int
)
