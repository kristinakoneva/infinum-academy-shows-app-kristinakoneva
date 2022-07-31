package db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import model.User

@Entity(tableName = "review")
data class ReviewEntity(
    @ColumnInfo(name = "id") @PrimaryKey val id: String,
    @ColumnInfo(name = "comment") val comment: String,
    @ColumnInfo(name = "rating") val rating: Int,
    @ColumnInfo(name = "showId") val showId: String,
    @ColumnInfo(name = "userId") val userId: String,
    @ColumnInfo(name = "userEmail") val userEmail: String,
    @ColumnInfo(name = "userImageUrl") val userImageUrl: String
)
