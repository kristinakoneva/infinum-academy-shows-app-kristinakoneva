package infinumacademy.showsapp.kristinakoneva

import android.app.Application
import db.ShowsAppDatabase

class ShowsApplication : Application() {

    val database by lazy {
        ShowsAppDatabase.getDatabase(this)
    }

}