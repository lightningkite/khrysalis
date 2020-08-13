//
//  UIView+post.swift
//  Khrysalis
//
//  Created by Brady on 8/11/20.
//

import Foundation

public extension UIView {
    func post(_ action: @escaping () -> Void){
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.01, execute: action)
    }
}
