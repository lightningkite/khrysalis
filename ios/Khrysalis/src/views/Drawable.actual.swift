//
//  drawable.swift
//  Khrysalis
//
//  Created by Joseph Ivie on 10/17/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit
import AlamofireImage
import Alamofire

public class Drawable: Equatable, Hashable {
    
    public init(_ f: @escaping (View?)->CALayer) {
        self.makeLayer = f
    }
    
    public static func == (lhs: Drawable, rhs: Drawable) -> Bool {
        return lhs.id == rhs.id
    }
    
    public let id: Int64 = Random.Default.INSTANCE.nextLong()
    public let makeLayer: (View?)->CALayer
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(id)
    }
    
    public func makeView(parent: UIView? = nil) -> UIView {
        let layer = makeLayer(parent)
        let view = UIView(frame: layer.bounds)
        view.clipsToBounds = true
        layer.frame = view.bounds
        view.layer.addSublayer(layer)
        return view
    }
}
