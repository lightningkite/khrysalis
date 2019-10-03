import Foundation
//package com.test


func testWeakRef(){
    var x: Int = 0
    weak var weakX: Int? = x
    print(weakX)
}
