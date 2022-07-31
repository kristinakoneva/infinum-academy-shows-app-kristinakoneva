package infinumacademy.showsapp.kristinakoneva.shows_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import db.ShowEntity
import db.ShowsAppDatabase
import java.util.concurrent.Executors
import model.Show
import model.ShowsResponse
import model.TopRatedShowsResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsViewModel(
    private val database: ShowsAppDatabase
) : ViewModel() {

    private val _showsListLiveData = MutableLiveData<List<Show>>()
    var showsListLiveData: LiveData<List<Show>> = _showsListLiveData

    private val _showEmptyStateLiveData = MutableLiveData(false)
    val showEmptyStateLiveData: LiveData<Boolean> = _showEmptyStateLiveData

    private val _listShowsResultLiveData = MutableLiveData(true)
    val listShowsResultLiveData: LiveData<Boolean> = _listShowsResultLiveData

    private val _listTopRatedShowsResultLiveData = MutableLiveData(true)
    val listTopRatedShowsResultLiveData: LiveData<Boolean> = _listTopRatedShowsResultLiveData

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

    fun updateShowTopRated(isChecked: Boolean) {
        _showTopRatedLiveData.value = isChecked
    }

    fun fetchShows() {
        _apiCallInProgress.postValue(true)
        _apiCallForFetchingShowsInProgress.postValue(true)
        ApiModule.retrofit.fetchShows().enqueue(object : Callback<ShowsResponse> {
            override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {
                _listShowsResultLiveData.value = response.isSuccessful
                if (response.isSuccessful) {
                    _showsListLiveData.value = response.body()?.shows
                    saveShowsToDatabase(response.body()?.shows)
                    _showEmptyStateLiveData.value = false
                }
                _apiCallForFetchingShowsInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingTopRatedShowsInProgress.value
            }

            override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {
                _listShowsResultLiveData.value = false
                _apiCallForFetchingShowsInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingTopRatedShowsInProgress.value
            }

        })
    }

    fun updateEmptyState(value: Boolean) {
        _showEmptyStateLiveData.value = value
    }

    fun fetchTopRatedShows() {
        _apiCallInProgress.postValue(true)
        _apiCallForFetchingTopRatedShowsInProgress.postValue(true)
        ApiModule.retrofit.fetchTopRatedShows().enqueue(object : Callback<TopRatedShowsResponse> {
            override fun onResponse(call: Call<TopRatedShowsResponse>, response: Response<TopRatedShowsResponse>) {
                _listTopRatedShowsResultLiveData.value = response.isSuccessful
                if (response.isSuccessful) {
                    _topRatedShowsListLiveData.value = response.body()!!.shows
                    _showEmptyStateLiveData.value = false
                }
                _apiCallForFetchingTopRatedShowsInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowsInProgress.value
            }

            override fun onFailure(call: Call<TopRatedShowsResponse>, t: Throwable) {
                _listTopRatedShowsResultLiveData.value = false
                _apiCallForFetchingTopRatedShowsInProgress.value = false
                _apiCallInProgress.value = _apiCallForFetchingShowsInProgress.value
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
        var data = emptyList<ShowEntity>()
        showsListLiveData = database.showDao().getAllShows().map { list ->
            data = list
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

        // TODO: set showEmptyStateLiveData to true when there aren't any shows in the database
        //_showEmptyStateLiveData.value = data.isEmpty()
    }


}