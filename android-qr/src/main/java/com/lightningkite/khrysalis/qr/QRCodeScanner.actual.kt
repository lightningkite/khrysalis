package com.lightningkite.khrysalis.qr

import android.view.SurfaceHolder
import android.view.SurfaceView
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.lightningkite.khrysalis.delay
import com.lightningkite.khrysalis.views.ViewDependency
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import java.io.IOException

fun SurfaceView.bindBarcodeScan(dependency: ViewDependency): Observable<String> {
    val out = PublishSubject.create<String>()
    dependency.requestPermission(android.Manifest.permission.CAMERA) {
        if (it) {
            var barcodeDetector: BarcodeDetector = BarcodeDetector.Builder(dependency.context)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build()
            var cameraSource: CameraSource = CameraSource
                .Builder(dependency.context, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build()
            this.holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

                }

                override fun surfaceDestroyed(holder: SurfaceHolder?) {
                    cameraSource.stop()
                }

                override fun surfaceCreated(holder: SurfaceHolder?) {
                    try {
                        cameraSource.start(holder)
                    } catch (ie: IOException) {
                        ie.printStackTrace()
                        println("Whoops - ${ie.message}")
                    }
                }
            })

            if (this.isAttachedToWindow){
                try {
                    cameraSource.start(this.holder)
                } catch (ie: IOException) {
                    ie.printStackTrace()
                    println("Whoops - ${ie.message}")
                }
            }
            barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
                override fun release() {}

                override fun receiveDetections(results: Detector.Detections<Barcode>?) {
                    results?.detectedItems?.takeIf { it.size() > 0 }?.let { codes ->
                        codes.get(codes.keyAt(0))
                    }?.rawValue?.let { value ->
                        out.onNext(value)
                    }
                }
            })
        }
    }
    return out
        .observeOn(AndroidSchedulers.mainThread())
}