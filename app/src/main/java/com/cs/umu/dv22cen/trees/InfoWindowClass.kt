package com.cs.umu.dv22cen.trees

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

/**
 * InfoWindow for makrers clicked in google map.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
class MyInfoWindowAdapter(mContext: Context) : GoogleMap.InfoWindowAdapter {
    private var mWindow: View = LayoutInflater.from(mContext).inflate(R.layout.info_window, null)

    /**
     * Sets the text of info window.
     * @param marker The marker pressed.
     */
    @SuppressLint("SetTextI18n")
    private fun setInfoWindowText(marker: Marker) {
        val title = marker.title
        val snippet = marker.snippet
        val titleLayout = mWindow.findViewById<TextView>(R.id.title)
        val type = mWindow.findViewById<TextView>(R.id.type)
        val species = mWindow.findViewById<TextView>(R.id.species)
        val namn = mWindow.findViewById<TextView>(R.id.namn)
        val placement = mWindow.findViewById<TextView>(R.id.placement)
        val date = mWindow.findViewById<TextView>(R.id.date)
        val valuesList: List<String> = snippet!!.split(",")

        if (!TextUtils.isEmpty(title)) {
            titleLayout.text = title
            type.text = "Typ: " + valuesList[0]
            species.text = "Art: " + valuesList[1]
            namn.text = "Namn: " + valuesList[2]
            placement.text = "Placering: " + valuesList[3]
            date.text = "Planterades: " + valuesList[4]
        }
    }

    /**
     * Returns the info window.
     * @param p0 The marker pressed.
     * @return View info window.
     */
    override fun getInfoWindow(p0: Marker): View? {
        if(p0.title != "Du"){
            setInfoWindowText(p0)
            return mWindow
        }
        return null
    }

    /**
     * Returns the info window contents.
     * @param p0 The marker pressed.
     * @return View info window.
     */
    override fun getInfoContents(p0: Marker): View? {
        if(p0.title != "Du"){
            setInfoWindowText(p0)
            return mWindow
        }
        return null
    }
}