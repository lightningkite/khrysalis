//
//  QRCodeGenerator.swift
//  KhrysalisQR
//
//  Created by Brady Svedin on 8/10/20.
//  Copyright © 2020 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit
import CoreImage
import RxSwift
import Khrysalis


//func generateBarCode(text:String, width:Int = 200, height:Int = 200) -> Single<Image>{
//
//
//    let data = text.data(using: String.Encoding.ascii)
//
//    if let filter = CIFilter(name: "CIQRCodeGenerator") {
//        filter.setValue(data, forKey: "inputMessage")
//        let transform = CGAffineTransform(scaleX: 3, y: 3)
//
//        if let output = filter.outputImage?.transformed(by: transform) {
//            return Single.just(UIImage(ciImage: output).asImage())
//        }
//    }
//    return Single.error(Exception("Could not generate a barcode."))
//
//}


public func generateBarCode(text:String, width:Int = 200, height:Int = 200) -> Single<Image>{
    
    let data = text.data(using: .isoLatin1)
    guard let filter = CIFilter(name: "CIQRCodeGenerator") else {
        return Single.error(Exception("Could not generate a barcode."))
    }
    filter.setValue(data, forKey: "inputMessage")
    guard var ciImage = filter.outputImage else {
        return Single.error(Exception("Could not generate a barcode."))
    }

    let imageSize = ciImage.extent.integral
    let outputSize = CGSize(width:width, height: height)
    ciImage = ciImage.transformed(by:CGAffineTransform(scaleX: outputSize.width/imageSize.width, y: outputSize.height/imageSize.height))

    return Single.just(UIImage(ciImage: ciImage).asImage())
}
