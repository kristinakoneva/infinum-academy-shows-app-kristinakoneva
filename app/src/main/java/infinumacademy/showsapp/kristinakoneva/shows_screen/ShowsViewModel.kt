package infinumacademy.showsapp.kristinakoneva.shows_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import infinumacademy.showsapp.kristinakoneva.UserInfo
import java.io.File
import model.Show
import model.ShowsResponse
import model.TopRatedShowsResponse
import model.UpdateProfilePhotoResponse
import model.User
import networking.ApiModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsViewModel : ViewModel() {

    private val _showsListLiveData = MutableLiveData<List<Show>>()
    val showsListLiveData: LiveData<List<Show>> = _showsListLiveData

    private val _showEmptyStateLiveData = MutableLiveData(false)
    val showEmptyStateLiveData: LiveData<Boolean> = _showEmptyStateLiveData

    private val _listShowsResultLiveData = MutableLiveData(true)
    val listShowsResultLiveData: LiveData<Boolean> = _listShowsResultLiveData

    private val _listTopRatedShowsResultLiveData = MutableLiveData(true)
    val listTopRatedShowsResultLiveData: LiveData<Boolean> = _listTopRatedShowsResultLiveData

    private val _updateProfilePhotoResultLiveData = MutableLiveData(true)
    val updateProfilePhotoResultLiveData: LiveData<Boolean> = _updateProfilePhotoResultLiveData

    private val _topRatedShowsListLiveData = MutableLiveData<List<Show>>()
    val topRatedShowsListLiveData: LiveData<List<Show>> = _topRatedShowsListLiveData

    private val _showTopRatedLiveData = MutableLiveData(false)
    val showTopRatedLiveData: LiveData<Boolean> = _showTopRatedLiveData

    private val _apiCallInProgress = MutableLiveData(false)
    val apiCallInProgress: LiveData<Boolean> = _apiCallInProgress

    private val _apiCallForFetchingShowsInProgress = MutableLiveData(false)
    // val apiCallForFetchingShowsInProgress: LiveData<Boolean> = _apiCallForFetchingShowsInProgress

    private val _apiCallForFetchingTopRatedShowsInProgress = MutableLiveData(false)
    // val apiCallForFetchingTopRatedShowsInProgress: LiveData<Boolean> = _apiCallForFetchingTopRatedShowsInProgress

    private val _apiCallForUpdatingProfilePhotoInProgress = MutableLiveData(false)
    // val apiCallForUpdatingProfilePhotoInProgress: LiveData<Boolean> = _apiCallForUpdatingProfilePhotoInProgress

    init {
        fetchShows()
        fetchTopRatedShows()
    }

    fun updateShowTopRated(isChecked: Boolean) {
        _showTopRatedLiveData.value = isChecked
    }

    private fun fetchShows() {
        _apiCallInProgress.value = true
        _apiCallForFetchingShowsInProgress.value = true
        ApiModule.retrofit.fetchShows().enqueue(object : Callback<ShowsResponse> {
            override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {
                if (response.isSuccessful) {
                    _showsListLiveData.value = response.body()?.shows
                }
                _listShowsResultLiveData.value = response.isSuccessful
                _apiCallForFetchingShowsInProgress.value = false
                _apiCallInProgress.value =
                    _apiCallForFetchingTopRatedShowsInProgress.value!! || _apiCallForUpdatingProfilePhotoInProgress.value!!
            }

            override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {
                _listShowsResultLiveData.value = false
                _apiCallForFetchingShowsInProgress.value = false
                _apiCallInProgress.value =
                    _apiCallForFetchingTopRatedShowsInProgress.value!! || _apiCallForUpdatingProfilePhotoInProgress.value!!
            }

        })
    }

    fun resetEmptyState() {
        _showEmptyStateLiveData.value = !_showEmptyStateLiveData.value!!
    }

    private fun fetchTopRatedShows() {
        _apiCallInProgress.value = true
        _apiCallForFetchingTopRatedShowsInProgress.value = true
        ApiModule.retrofit.fetchTopRatedShows().enqueue(object : Callback<TopRatedShowsResponse> {
            override fun onResponse(call: Call<TopRatedShowsResponse>, response: Response<TopRatedShowsResponse>) {
                if (response.isSuccessful) {
                    _topRatedShowsListLiveData.value = response.body()?.shows
                }
                _listTopRatedShowsResultLiveData.value = response.isSuccessful
                _apiCallForFetchingTopRatedShowsInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowsInProgress.value!! || _apiCallForUpdatingProfilePhotoInProgress.value!!
            }

            override fun onFailure(call: Call<TopRatedShowsResponse>, t: Throwable) {
                _listTopRatedShowsResultLiveData.value = false
                _apiCallForFetchingTopRatedShowsInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowsInProgress.value!! || _apiCallForUpdatingProfilePhotoInProgress.value!!
            }

        })
    }

    fun updateProfilePhoto(fileName: String, imagePath: String) {
        _apiCallInProgress.value = true
        _apiCallForUpdatingProfilePhotoInProgress.value = true
        val requestBody =
            MultipartBody.Part.createFormData("image", "$fileName.jpg", File(imagePath).asRequestBody("multipart/form-data".toMediaType()))

        ApiModule.retrofit.updateProfilePhoto(requestBody).enqueue(object : Callback<UpdateProfilePhotoResponse> {
            override fun onResponse(call: Call<UpdateProfilePhotoResponse>, response: Response<UpdateProfilePhotoResponse>) {
                if (response.isSuccessful) {
                    UserInfo.imageUrl = response.body()?.user?.imageUrl
                }
                _updateProfilePhotoResultLiveData.value = response.isSuccessful
                _apiCallForUpdatingProfilePhotoInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowsInProgress.value!! || _apiCallForFetchingTopRatedShowsInProgress.value!!
            }

            override fun onFailure(call: Call<UpdateProfilePhotoResponse>, t: Throwable) {
                _updateProfilePhotoResultLiveData.value = false
                _apiCallForUpdatingProfilePhotoInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowsInProgress.value!! || _apiCallForFetchingTopRatedShowsInProgress.value!!
            }
        })
    }
}