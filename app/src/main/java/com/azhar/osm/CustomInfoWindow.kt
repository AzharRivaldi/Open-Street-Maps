package com.azhar.osm

import kotlinx.android.synthetic.main.layout_tooltip.view.*
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

/**
 * Created by Azhar Rivaldi on 24-02-2022
 * Youtube Channel : https://bit.ly/2PJMowZ
 * Github : https://github.com/AzharRivaldi
 * Twitter : https://twitter.com/azharrvldi_
 * Instagram : https://www.instagram.com/azhardvls_
 * Linkedin : https://www.linkedin.com/in/azhar-rivaldi
 */

class CustomInfoWindow(mapView: MapView?) : InfoWindow(R.layout.layout_tooltip, mapView) {

    override fun onClose() {
        //by default, do nothing
    }

    override fun onOpen(item: Any) {
        val marker = item as Marker
        val infoWindowData = marker.relatedObject as ModelMain

        val tvNamaLokasi = mView.tvNamaLokasi
        val tvAlamat = mView.tvAlamat
        val imageClose = mView.imageClose

        tvNamaLokasi.text = infoWindowData.strName
        tvAlamat.text = infoWindowData.strVicinity
        imageClose.setOnClickListener {
            marker.closeInfoWindow()
        }

    }
}