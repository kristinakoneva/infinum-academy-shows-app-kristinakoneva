package infinumacademy.showsapp.kristinakoneva.shows_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import db.ShowEntity
import db.ShowsAppDatabase
import infinumacademy.showsapp.kristinakoneva.UserInfo
import java.io.File
import java.util.concurrent.Executors
import model.Show
import model.ShowsResponse
import model.TopRatedShowsResponse
import model.UpdateProfilePhotoResponse
import networking.ApiModule
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsViewModel(
    private val database: ShowsAppDatabase
) : ViewModel() {

    private val _showsListLiveData = MutableLiveData<List<Show>>()
    var showsListLiveData: LiveData<List<Show>> = _showsListLiveData

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

    fun updateShowTopRated(isChecked: Boolean) {
        _showTopRatedLiveData.value = isChecked
    }

    fun fetchShows() {
        _apiCallInProgress.postValue(true)
        _apiCallForFetchingShowsInProgress.postValue(true)
        ApiModule.retrofit.fetchShows().enqueue(object : Callback<ShowsResponse> {
            override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {

                if (response.isSuccessful) {
                    _showsListLiveData.value = response.body()?.shows
                    saveShowsToDatabase(response.body()?.shows)
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

    fun fetchTopRatedShows() {
        _apiCallInProgress.postValue(true)
        _apiCallForFetchingTopRatedShowsInProgress.postValue(true)
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

    private fun saveShowsToDatabase(shows: List<Show>?) {
        Executors.newSingleThreadExecutor().execute {
            database.showDao().insertAllShows(shows?.map { show ->
                ShowEntity(show.id, show.averageRating, show.description, show.imageUrl, show.noOfReviews, show.title)
            } ?: listOf())
        }
    }

    fun fetchShowsFromDatabase() {
        showsListLiveData = database.showDao().getAllShows().map { list ->
            list.map { showEntity ->
                Show(
                    showEntity.id,
                    showEntity.averageRating,
                    showEntity.description,
                    showEntity.imageUrl,
                    showEntity.noOfReviews,
                    showEntity.title
                )
            }
        }
    }

    // used for setting the empty state if database is empty
    fun getShowsFromDB(): LiveData<List<ShowEntity>> {
        return database.showDao().getAllShows()
    }

}