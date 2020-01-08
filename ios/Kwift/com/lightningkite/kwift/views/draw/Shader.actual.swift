//
//  Shader.actual.swift
//  Kwift
//
//  Created by Joseph Ivie on 1/4/20.
//

import Foundation
import CoreGraphics


//--- ShaderValue
public typealias ShaderValue = (CGContext)->Void

//--- Shader
public enum Shader {
    public enum TileMode {
        case CLAMP, REPEAT, MIRROR
    }
}





