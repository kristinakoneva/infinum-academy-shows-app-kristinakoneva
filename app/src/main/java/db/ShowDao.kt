package db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ShowDao {

    @Query("SELECT * FROM show")
    fun getAllShows() : LiveData<List<ShowEntity>>

    @Query("SELECT * FROM show WHERE id IS :showId")
    fun getShow(showId: Int) : LiveData<ShowEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllShows(superheroes: List<ShowEntity>)
}