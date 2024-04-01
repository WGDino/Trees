package com.cs.umu.dv22cen.trees

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

/**
 * ViewModel for Umeå Trees.
 * Holds data for business logic.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
class treeViewModel : ViewModel(){
    private var currentTreeArray = ArrayList<responseObject>()
    private var currentMarkers = ArrayList<Marker>()
    private var edgeCoords = ArrayList<LatLng>()
    private var isRequesting = false
    private var totalTrees: Int = 0
    private var radius: Int = 50
    private var amount: Int = 20

    /**
     * Sets the amount of trees shown on map.
     * @param value New amount.
     */
    fun setAmount(value: Int){
        amount = value
    }

    /**
     * Gets the amount of trees shown on map.
     * @return amount in Int
     */
    fun getAmount(): Int {
        return amount
    }

    /**
     * Sets the radius where trees can be caught.
     * @param value New radius.
     */
    fun setRadius(value: Int){
        radius = value
    }

    /**
     * Gets the radius trees can get caught in.
     * @return radius in Int
     */
    fun getRadius(): Int {
        return radius
    }

    /**
     * Sets the amount of total trees.
     * @param value New total.
     */
    fun setTotalTrees(value: Int){
        totalTrees = value
    }

    /**
     * Gets amount of total trees.
     * @return Total trees INT
     */
    fun getTotalTrees(): Int {
        return totalTrees
    }

    /**
     * Sets isRequesting or not.
     * @param value true/false
     */
    fun setIsRequesting(value: Boolean){
        isRequesting = value
    }

    /**
     * Checks if isRequesting.
     * @return true if requesting/false.
     */
    fun isRequesting(): Boolean {
       return isRequesting
    }

    /**
     * Creates edges for area where trees are.
     */
    fun createEdge(){
        edgeCoords.add(LatLng(63.60058801627824, 19.90512209328035))
        edgeCoords.add(LatLng(63.637458984908186, 19.87140091076691))
        edgeCoords.add(LatLng(63.884630136196584, 20.032408116193817))
        edgeCoords.add(LatLng(63.90963719553976, 20.56794576006403))
        edgeCoords.add(LatLng(63.814108255731824, 20.516780194224896))
    }

    /**
     * Gets the edge of area where trees ares.
     * @param index Index of edge.
     * @return Coordinates of start for edge.
     */
    fun getEdge(index: Int): LatLng {
        return edgeCoords[index]
    }

    /**
     * Inserts marker.
     * @param index Index where inserted is placed.
     * @param marker The marker.
     */
    fun insertMarker(index: Int,marker: Marker){
        currentMarkers.add(index,marker)
    }

    /**
     * Gets a marker.
     * @param index Index of marker.
     * @return The marker.
     */
    fun getMarker(index: Int): Marker {
        return currentMarkers[index]
    }

    /**
     * Removes a marker.
     * @param index Index of marker.
     */
    fun removeMarker(index: Int){
        currentMarkers.removeAt(index)
    }

    /**
     * Removes all markers.
     */
    fun clearMarkers(){
        currentMarkers.clear()
    }

    /**
     * Inserts a tree.
     * @param coordinate Coordinates of tree.
     * @param treeType Type of tree string.
     * @param treeName Name of tree string.
     * @param treeSpecies Species of tree string.
     * @param treePlacement Placement of tree string.
     * @param date Date of plantation string.
     * @param info Info about tree string.
     * @param index Index of tree.
     */
    fun insertTree(coordinate: LatLng, treeType: String, treeSpecies: String, treeName: String, treePlacement: String, date: String, info: String, index:Int) {
        currentTreeArray.add(responseObject(coordinate, treeType, treeName, treeSpecies, treePlacement, date, info, index))
    }

    /**
     * Removes all trees.
     */
    fun clearTrees(){
        currentTreeArray.clear()
    }

    /**
     * Gets currentTreeArray.
     * @return CurrentTreeArray.
     */
    fun getCurrentTreeArray(): ArrayList<responseObject> {
        return currentTreeArray
    }

    /**
     * Gets a tree.
     * @param index Index of tree.
     * @return Tree as responseObject.
     */
    fun getTree(index: Int): responseObject {
        return currentTreeArray[index]
    }

    /**
     * Gets the amount of trees.
     * @return amount as Int.
     */
    fun getAmountTrees(): Int {
        return currentTreeArray.size
    }
}