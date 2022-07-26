package infinumacademy.showsapp.kristinakoneva

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import model.Show

class ShowsViewModel : ViewModel() {

    private val _showsListLiveData = MutableLiveData(
        listOf(
            Show(
                0,
                "The Office",
                "The Office is an American mockumentary sitcom television series that depicts " +
                    "the everyday work lives of office employees in the Scranton, Pennsylvania, branch of the fictional " +
                    "Dunder Mifflin Paper Company. It aired on NBC from March 24, 2005, to May 16, 2013, lasting a total of nine seasons.",
                R.drawable.the_office
            ),
            Show(
                1,
                "Stranger Things",
                "In 1980s Indiana, a group of young friends witness supernatural forces and secret government exploits. " +
                    "As they search for answers, the children unravel a series of extraordinary mysteries.",
                R.drawable.stranger_things
            ),
            Show(
                2,
                "Krv nije voda",
                "Lorem ipsum dolor sit amet. Sit voluptatibus vitae qui quis minus non dignissimos autem! " +
                    "Qui cupiditate tempore rem perspiciatis galisum et quia nihil rem consequatur quia aut quia saepe.",
                R.drawable.krv_nije_voda
            )
        )
    )
    val showsListLiveData: LiveData<List<Show>> = _showsListLiveData

    private val _showEmptyStateLiveData = MutableLiveData(false)
    val showEmptyStateLiveData: LiveData<Boolean> = _showEmptyStateLiveData

    fun resetEmptyState() {
        _showEmptyStateLiveData.value = !_showEmptyStateLiveData.value!!
    }
}