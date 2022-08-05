package infinumacademy.showsapp.kristinakoneva.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfilePhotoResponse(
    @SerialName("user") val user: User
)
