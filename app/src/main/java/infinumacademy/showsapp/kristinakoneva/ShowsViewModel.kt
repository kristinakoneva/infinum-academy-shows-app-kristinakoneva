package infinumacademy.showsapp.kristinakoneva


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Show
import model.ShowsResponse
import networking.ApiModule
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShowsViewModel : ViewModel() {

    private val _showsListLiveData = MutableLiveData<Array<Show>>()
    val showsListLiveData: LiveData<Array<Show>> = _showsListLiveData

    private val _showEmptyStateLiveData = MutableLiveData(false)
    val showEmptyStateLiveData: LiveData<Boolean> = _showEmptyStateLiveData

    private val _listShowsResultLiveData = MutableLiveData(false)
    val listShowsResultLiveData: LiveData<Boolean> = _listShowsResultLiveData



    fun fetchShows(){
        ApiModule.retrofit.fetchShows().enqueue(object: Callback<ShowsResponse>{
            override fun onResponse(call: Call<ShowsResponse>, response: Response<ShowsResponse>) {
                _listShowsResultLiveData.value = response.isSuccessful
                if(response.isSuccessful){
                    _showsListLiveData.value = response.body()!!.shows
                }
            }

            override fun onFailure(call: Call<ShowsResponse>, t: Throwable) {
                _listShowsResultLiveData.value = false
            }

        })
    }

    fun resetEmptyState() {
        _showEmptyStateLiveData.value = !_showEmptyStateLiveData.value!!
    }
}