package db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities=[
        ShowEntity::class,
        ReviewEntity::class
    ],
    version = 1
)
abstract class ShowsAppDatabase: RoomDatabase() {
    companion object {

        @Volatile
        private var INSTANCE: ShowsAppDatabase? = null

        fun getDatabase(context: Context): ShowsAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val database = Room.databaseBuilder(
                    context,
                    ShowsAppDatabase::class.java,
                    "shows_app_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = database
                database
            }
        }
    }

    abstract fun showDao(): ShowDao
    abstract fun reviewDao(): ReviewDao
}