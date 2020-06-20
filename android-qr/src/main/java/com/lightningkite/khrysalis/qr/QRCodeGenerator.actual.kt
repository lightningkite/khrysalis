package com.lightningkite.khrysalis.qr

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.lightningkite.khrysalis.Image
import com.lightningkite.khrysalis.asImage
import io.reactivex.Single


//TODO: Allow setup for a type of barcode, for now it's limited to QR,
//need to find out how this works for ios before deciding.
fun generateBarCode(text:String, width:Int = 200, height:Int = 200): Single<Image> {
    val formatWriter = MultiFormatWriter()
    return try{
        val bitMatrix:BitMatrix = formatWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
        val barcodeEncoder = BarcodeEncoder()
        val code:Bitmap = barcodeEncoder.createBitmap(bitMatrix)
        Single.just(code.asImage())
    } catch (e:WriterException){
        Single.error<Image>(e)
    }
}