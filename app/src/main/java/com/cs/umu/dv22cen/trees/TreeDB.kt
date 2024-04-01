package com.cs.umu.dv22cen.trees
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import io.reactivex.annotations.NonNull

/**
 * Tree relation class
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
@Entity(primaryKeys = ["lat", "lon"])
data class TreeDB(
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "type") val type: String?,
    @ColumnInfo(name = "species") val species: String?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "placement") val placement: String?,
    @ColumnInfo(name = "date") val date: String?,
    @ColumnInfo(name = "info") val info: String?
)
