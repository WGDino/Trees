package com.cs.umu.dv22cen.trees

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


/**
 * Main activity of android app for Umeå municipality trees
 * Handles the GUI with Map and AppBar.
 * When pressing back the app always quits.
 * Owns an instance of the database where trees are stored.
 * Does API:requests using volley.
 * @author Christoffer Eriksson dv22cen
 * @since 2024-03-19
 */
class MainActivity : AppCompatActivity(), OnMapReadyCallback{
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var currentLocation: Location
    private lateinit var map: GoogleMap
    private lateinit var locationCallback: LocationCallback
    private lateinit var catchButton: Button
    private val viewModel: treeViewModel by viewModels()

    protected val REQUEST_CHECK_SETTINGS = 0x1

    private lateinit var database:TreeDatabase
    private lateinit var treeDao: TreeDao
    private lateinit var queue: RequestQueue

    /**
     * Runs when the activity is created
     * Creates all local variables for GUI Views and sets onClickListener.
     * Creates the database and map.
     * @param savedInstanceState The possible saved data since the activity last ran
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)
        viewModel.createEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        catchButton = findViewById(R.id.catch_button)
        catchButton.visibility = INVISIBLE
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.title = "Umeå Träd"
        queue = Volley.newRequestQueue(this)

        val intent = intent
        if(intent.hasExtra("com.cs.umu.dv22cen.trees.radius") && intent.hasExtra("com.cs.umu.dv22cen.trees.amount"))
        {
            viewModel.setRadius(intent.getIntExtra("com.cs.umu.dv22cen.trees.radius", 50))
            viewModel.setAmount(intent.getIntExtra("com.cs.umu.dv22cen.trees.amount", 20))
        }

        database = Room.databaseBuilder(
            this,
            TreeDatabase::class.java, "tree_database.db"
        ).fallbackToDestructiveMigration().build()
        treeDao = database.treeDao()

        setLocationCallback()
        mapFragment.getMapAsync(this)

        setCatchListener()
    }

    /**
     * Runs when the activity is paused.
     */
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    /**
     * Runs when the activity is resumed.
     */
    override fun onResume() {
        super.onResume()
        if (viewModel.isRequesting()) checkLocationSettings()
    }

    /**
     * Sets the callback which is run when a new location is collected.
     */
    private fun setLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                catchButton.visibility = INVISIBLE
                for (location in p0.locations) {
                    map.clear()
                    val polylineOptions = PolylineOptions()
                        .add(viewModel.getEdge(0))
                        .add(viewModel.getEdge(1))
                        .add(viewModel.getEdge(2))
                        .add(viewModel.getEdge(3))
                        .add(viewModel.getEdge(4))
                        .add(viewModel.getEdge(0))
                    map.addPolyline(polylineOptions)
                    currentLocation = location
                    val current = LatLng(location.latitude, location.longitude)
                    map.addMarker(
                        MarkerOptions()
                            .position(current)
                            .title("Du")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    )
                    map.addCircle(
                        CircleOptions()
                            .center(LatLng(location.latitude, location.longitude))
                            .radius(viewModel.getRadius().toDouble())
                            .strokeColor(Color.YELLOW)
                            .fillColor(TRANSPARENT)
                    )
                    getTrees(location.latitude, location.longitude)
                }
            }
        }
    }

    /**
     * Sets the onClickListener of the catchButton.
     */
    private fun setCatchListener(){
        catchButton.setOnClickListener {
            val trees = viewModel.getCurrentTreeArray()
            val treesCaught = ArrayList<responseObject>()
            for (x in trees){
                val treeCoordinates = x.coordinate
                val treeLocation = Location("")
                treeLocation.latitude = treeCoordinates.latitude
                treeLocation.longitude = treeCoordinates.longitude

                if(treeLocation.distanceTo(currentLocation) < viewModel.getRadius()){
                    treesCaught.add(x)
                }
            }
            lifecycleScope.launch {
                insertDb(treesCaught)
            }
        }
    }

    /**
     * Runs when the AppBar is created.
     * @param menu1 The menu
     * @return true if success
     */
    override fun onCreateOptionsMenu(menu1: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.toolbar, menu1)
        return true
    }

    /**
     * Runs when an item in the menu is pressed.
     * @param item The pressed item
     * @return boolean True if success reading what clicked/else false
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.treeDex -> {
            val myIntent: Intent = Intent(
                this,
                listDataBase::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", viewModel.getTotalTrees())
            this.startActivity(myIntent)
            true
        }

        R.id.action_settings -> {
            val myIntent: Intent = Intent(
                this,
                settings::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", viewModel.getTotalTrees())
            this.startActivity(myIntent)
            true
        }

        R.id.help ->{
            val myIntent: Intent = Intent(
                this,
                help::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", viewModel.getTotalTrees())
            this.startActivity(myIntent)
            true
        }

        R.id.award ->{
            val myIntent: Intent = Intent(
                this,
                awards::class.java
            )
            myIntent.putExtra("com.cs.umu.dv22cen.trees.total", viewModel.getTotalTrees())
            this.startActivity(myIntent)
            true
        }

        else -> {
            false
        }
    }

    /**
     * Runs when the map is ready
     * @param googleMap The map.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setInfoWindowAdapter(MyInfoWindowAdapter(this))
        startLocation(map)
    }

    /**
     * Gets the starting location of the phone.
     * Adds a marker at current position.
     * @param googleMap The map
     */
    private fun startLocation(googleMap: GoogleMap){
        var lat = 0.0
        var long = 0.0
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    currentLocation = location
                }
                val current = location?.let { LatLng(location.latitude, it.longitude) }
                current?.let {
                    moveToCurrentLocation(current)
                    lat = location.latitude
                    long = location.longitude
                    MarkerOptions()
                        .position(it)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                        .title("Du")
                }?.let {
                    googleMap.addMarker(
                        it
                    )
                }
                if(!viewModel.isRequesting()){
                    checkLocationSettings()
                }
            }
    }

    /**
     * Runs when permissions are requested.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocation(map)
            } else {
                Toast.makeText(this@MainActivity,
                    "Applikationen fungerar inte utan platstjänster",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Pans the camera on map to current location.
     * @param currentLocation Current coordinates.
     */
    private fun moveToCurrentLocation(currentLocation: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
        map.animateCamera(CameraUpdateFactory.zoomIn())
        map.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null)
    }

    /**
     * Gets trees from API.
     * @param lat The latitude currently
     * @param long The longitude currently
     */
    private fun getTrees(lat: Double, long: Double){
        viewModel.clearTrees()
        viewModel.clearMarkers()
            val url1 = "https://opendata.umea.se/api/explore/v2.1/catalog/datasets/trad-som-forvaltas-av-gator-och-parker/records?order_by=distance(geo_point_2d%2C%20geom%27POINT("
            val url2 = "%20"
            val url3 = ")%27)Asc&limit="
            val url4 = viewModel.getAmount().toString()
            val url = url1 + long + url2 + lat + url3 + url4
            val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    val total = response.getInt("total_count")
                    viewModel.setTotalTrees(total)
                    val array = response.getJSONArray("results")
                    if(array.length() > 0){
                        for(index in 0..< array.length()){
                            val responseApi: JSONObject = array.getJSONObject(index)
                            insertTrees(responseApi, index)
                        }
                    }
                    if(viewModel.isRequesting()){
                        //My understanding of this is that the job will complete and be collected
                        // but the scope will exist for future jobs to be put on it until the
                        // activity dies
                        lifecycleScope.launch {
                            checkDbMarkers(map)
                        }
                    }
                },
                { error ->
                    Toast.makeText(this@MainActivity,
                        error.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            )

            // Access the RequestQueue through your singleton class.
            queue.add(jsonObjectRequest)

    }

    /**
     * Inserts trees into viewModel
     * @param responseApi The tree info response.
     * @param index The index of tree.
     */
    private fun insertTrees(responseApi: JSONObject, index: Int){
        val coords = responseApi.getJSONObject("geo_point_2d")
        val longitude = coords.optDouble("lon")
        val latitude = coords.optDouble("lat")
        val treeType = responseApi.optString("lov_eller_barrtrad_1_1_1")
        val treeSpecies = responseApi.optString("tradart_vetenskap_namn_1_1_2")
        val treeName = responseApi.optString("tradart_svenskt_namn_1_1_3")
        val treePlacement = responseApi.optString("gatu_eller_parktrad_1_4_4")
        val date = responseApi.optString("planteringsdatum_6_1_1")
        val info = "$treeType,$treeSpecies,$treeName,$treePlacement,$date"

        viewModel.insertTree(LatLng(latitude, longitude), treeType, treeSpecies, treeName, treePlacement, date, info, index)
    }

    /**
     * Inserts trees into database.
     * @param treesCaught ArrayList of trees being added.
     */
    private suspend fun insertDb(treesCaught: ArrayList<responseObject>) {
        val markers = ArrayList<Marker>()
        for(x in treesCaught){
            val treeInsert = TreeDB(x.coordinate.latitude, x.coordinate.longitude, x.treeType,
                x.treeSpecies, x.treeName, x.treePlacement, x.date, x.info)
            treeDao.insert(treeInsert)
            markers.add(viewModel.getMarker(x.index))
        }
        withContext (Dispatchers.Main) {
            colourMarker(map, markers)
        }
    }

    /**
     * Checks if trees of markers are in database.
     * @param googleMap The map.
     */
    private suspend fun checkDbMarkers(googleMap: GoogleMap){
        val size = viewModel.getAmountTrees()
        for (index in 0..<size){
            val tree = viewModel.getTree(index)
            val lat = tree.coordinate.latitude
            val lon = tree.coordinate.longitude
            val treeFromDB = treeDao.findByCoord(lat,lon)
            var treeMarker: Marker?

            withContext (Dispatchers.Main) {
                if(treeFromDB == null){
                    treeMarker = googleMap.addMarker(
                        MarkerOptions()
                            .position(tree.coordinate)
                            .title("Tree $index")
                            .snippet(tree.info)
                    )
                }

                else{
                    treeMarker = googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(treeFromDB.lat, treeFromDB.lon))
                            .title("Tree $index")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .snippet(treeFromDB.info)
                    )
                }

                if (treeMarker != null) {
                    viewModel.insertMarker(index, treeMarker!!)
                }
                catchButton.visibility = VISIBLE
            }
        }

    }

    /**
     * Changes colour of marker.
     * @param map The map.
     * @param markers ArrayList of markers being coloured.
     */
    private fun colourMarker(map: GoogleMap, markers: ArrayList<Marker>){
        for(x in 0..<markers.size){
            markers[x].remove()
            val tree = viewModel.getTree(x)
            val info = tree.info
            val treeMarker = map.addMarker(
                MarkerOptions()
                    .position(tree.coordinate)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title("Tree $x")
                    .snippet(info)
            )
            viewModel.removeMarker(x)
            if (treeMarker != null) {
                viewModel.insertMarker(x, treeMarker)
            }
        }
    }

    /**
     * Checks the settings of phone.
     */
    private fun checkLocationSettings(){
        val locationRequest = LocationRequest.Builder(10000)
            .setMinUpdateDistanceMeters(20F)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMinUpdateIntervalMillis(10000)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            viewModel.setIsRequesting(true)
            startLocationUpdates(locationRequest)
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(this@MainActivity,
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(this@MainActivity,
                        sendEx.toString(),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Starts Location updates.
     * @param locationRequest The request being run.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates(locationRequest: LocationRequest) {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    /**
     * Stops location requests.
     */
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        viewModel.setIsRequesting(false)
    }
}
