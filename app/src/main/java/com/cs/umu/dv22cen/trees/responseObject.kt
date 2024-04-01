package com.cs.umu.dv22cen.trees

import com.google.android.gms.maps.model.LatLng

/**
 * Dataclass for trees as responseObjects from Umeå Kommun API.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
data class responseObject(var coordinate : LatLng, val treeType: String, var treeSpecies: String,
                          var treeName: String, var treePlacement: String, var date: String, var info: String, var index:Int) {
}