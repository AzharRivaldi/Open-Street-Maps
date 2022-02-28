package com.azhar.osm

import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapController
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import java.io.IOException
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {
    var modelMainList: MutableList<ModelMain> = ArrayList()
    lateinit var mapController: MapController
    lateinit var overlayItem: ArrayList<OverlayItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))

        val geoPoint = GeoPoint(-6.3035467, 106.8693513)
        mapView.setMultiTouchControls(true)
        mapView.controller.animateTo(geoPoint)
        mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE)
        mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        mapController = mapView.controller as MapController
        mapController.setCenter(geoPoint)
        mapController.zoomTo(15)

        getLocationMarker()
    }

    //get lat long
    private fun getLocationMarker() {
            try {
                val stream = assets.open("sample_maps.json")
                val size = stream.available()
                val buffer = ByteArray(size)
                stream.read(buffer)
                stream.close()
                val strContent = String(buffer, StandardCharsets.UTF_8)
                try {
                    val jsonObject = JSONObject(strContent)
                    val jsonArrayResult = jsonObject.getJSONArray("results")
                    for (i in 0 until jsonArrayResult.length()) {
                        val jsonObjectResult = jsonArrayResult.getJSONObject(i)
                        val modelMain = ModelMain()
                        modelMain.strName = jsonObjectResult.getString("name")
                        modelMain.strVicinity = jsonObjectResult.getString("vicinity")

                        //get lat long
                        val jsonObjectGeo = jsonObjectResult.getJSONObject("geometry")
                        val jsonObjectLoc = jsonObjectGeo.getJSONObject("location")
                        modelMain.latLoc = jsonObjectLoc.getDouble("lat")
                        modelMain.longLoc = jsonObjectLoc.getDouble("lng")
                        modelMainList.add(modelMain)
                    }
                    initMarker(modelMainList)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } catch (ignored: IOException) {
                Toast.makeText(
                    this@MainActivity,
                    "Oops, ada yang tidak beres. Coba ulangi beberapa saat lagi.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun initMarker(modelList: List<ModelMain>) {
        for (i in modelList.indices) {
            overlayItem = ArrayList()
            overlayItem.add(
                OverlayItem(
                    modelList[i].strName, modelList[i].strVicinity, GeoPoint(
                        modelList[i].latLoc, modelList[i].longLoc
                    )
                )
            )
            val info = ModelMain()
            info.strName = modelList[i].strName
            info.strVicinity = modelList[i].strVicinity

            val marker = Marker(mapView)
            marker.icon = resources.getDrawable(R.drawable.ic_place)
            marker.position = GeoPoint(modelList[i].latLoc, modelList[i].longLoc)
            marker.relatedObject = info
            marker.infoWindow = CustomInfoWindow(mapView)
            marker.setOnMarkerClickListener { item, arg1 ->
                item.showInfoWindow()
                true
            }

            mapView.overlays.add(marker)
            mapView.invalidate()
        }
    }

    public override fun onResume() {
        super.onResume()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if (mapView != null) {
            mapView.onResume()
        }
    }

    public override fun onPause() {
        super.onPause()
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        if (mapView != null) {
            mapView.onPause()
        }
    }

}