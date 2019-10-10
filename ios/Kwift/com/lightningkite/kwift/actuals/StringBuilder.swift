//
//  StringBuilder.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/10/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation

public class StringBuilder {
    private var underlying: String = ""
    
    public init(){}
    
    @discardableResult
    public func append(_ string: String) -> StringBuilder {
        underlying.append(string)
        return self
    }
    
    @discardableResult
    public func appendln(_ string: String) -> StringBuilder {
        underlying.append(string)
        underlying.append("\n")
        return self
    }
    
    public func toString() -> String {
        return underlying
    }
}
