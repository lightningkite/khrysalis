//
//  drawable.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/17/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit
import AlamofireImage
import Alamofire

public typealias Drawable = (View?)->CALayer

public func downloadDrawable(
    url: String,
    width: Int? = nil,
    height: Int? = nil,
    onResult: @escaping (Drawable?)->Void
) {
    downloadDrawable(url, width, height, onResult)
}
public func downloadDrawable(
    _ url: String,
    _ width: Int? = nil,
    _ height: Int? = nil,
    _ onResult: @escaping (Drawable?)->Void
) {
    Alamofire.request(url).responseImage { response in
        if var image = response.value {
            if let width = width, let height = height {
                image = image.af_imageAspectScaled(toFit: CGSize(width: width, height: height))
            }
            onResult({ _ in CAImageLayer(image) })
        } else {
            onResult(nil)
        }
    }
}

public func checkedDrawable(
    checked: @escaping Drawable,
    normal: @escaping Drawable
) -> Drawable {
    return checkedDrawable(checked, normal)
}
public func checkedDrawable(
    _ checked: @escaping Drawable,
    _ normal: @escaping Drawable
) -> Drawable {
    return { view in
        let layer = CALayer()
        
        let checkedLayer = checked(view)
        let normalLayer = normal(view)
        
        layer.addOnStateChange(view) { [unowned layer] state in
            layer.sublayers?.forEach { $0.removeFromSuperlayer() }
            if state.contains(.selected) {
                layer.addSublayer(checkedLayer)
            } else {
                layer.addSublayer(normalLayer)
            }
        }
        
        return layer
    }
}

public func setSizeDrawable(drawable: @escaping Drawable, width: Int, height: Int) -> Drawable {
    return setSizeDrawable(drawable, width, height)
}
public func setSizeDrawable(_ drawable: @escaping Drawable, _ width: Int, _ height: Int) -> Drawable {
    return { view in
        let existing = drawable(view)
        existing.bounds.size = CGSize(width: width, height: height)
        return existing
    }
}
