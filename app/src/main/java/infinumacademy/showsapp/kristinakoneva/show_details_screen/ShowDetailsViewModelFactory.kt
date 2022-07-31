package infinumacademy.showsapp.kristinakoneva.show_details_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import db.ShowsAppDatabase
import java.lang.IllegalArgumentException

class ShowDetailsViewModelFactory(val database: ShowsAppDatabase): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        if(modelClass.isAssignableFrom(ShowDetailsViewModel::class.java)){
            return ShowDetailsViewModel(database) as T
        }
        throw IllegalArgumentException("Cannot work with non ShowDetailsViewModel classes!")
    }
}