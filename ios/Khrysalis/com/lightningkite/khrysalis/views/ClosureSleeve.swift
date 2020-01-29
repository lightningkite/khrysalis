//
//  ClosureSleeve.swift
//  PennyProfit
//
//  Created by Joseph Ivie on 1/2/19.
//  Copyright © 2019 Shane Thompson. All rights reserved.
//

import Foundation
import UIKit

class ClosureSleeve {
    let closure: () -> ()

    init(attachTo: NSObject, id: String = "[\(arc4random())]", closure: @escaping () -> ()) {
        self.closure = closure
        attachTo.retain(as: id, item: self)
    }

    init(closure: @escaping () -> ()) {
        self.closure = closure
    }

    @objc public func invoke() {
        closure()
    }
}

public extension UIControl {
    func addAction(for controlEvents: UIControl.Event = .primaryActionTriggered, id: String = "[\(arc4random())]", action: @escaping () -> ()) {
        let sleeve = ClosureSleeve(attachTo: self, id: id, closure: action)
        addTarget(sleeve, action: #selector(ClosureSleeve.invoke), for: controlEvents)
    }

    func addOnStateChange(retainer: NSObject, id:UInt32 = arc4random(), action: @escaping (UIControl.State)->Void) -> UInt32 {
        retainer.retain(as: "onStateChange-isHighlighted-\(id)", item: observe(\.isHighlighted, options: [.old, .new]) { (provider, changes) in
            action(provider.state)
        })
        retainer.retain(as: "onStateChange-isSelected-\(id)", item: observe(\.isSelected, options: [.old, .new]) { (provider, changes) in
            action(provider.state)
        })
        retainer.retain(as: "onStateChange-isEnabled-\(id)", item: observe(\.isEnabled, options: [.old, .new]) { (provider, changes) in
            action(provider.state)
        })
        UIControl.checkOnStateChange(retainer: retainer, id: id)
        return id
    }
    static func removeOnStateChange(retainer: NSObject, id: UInt32) {
        retainer.unretain("onStateChange-isHighlighted-\(id)")
        retainer.unretain("onStateChange-isSelected-\(id)")
        retainer.unretain("onStateChange-isEnabled-\(id)")
    }
    static func checkOnStateChange(retainer: NSObject, id: UInt32){
        assert(retainer.checkRetained(as: "onStateChange-isHighlighted-\(id)") != nil)
        assert(retainer.checkRetained(as: "onStateChange-isSelected-\(id)") != nil)
        assert(retainer.checkRetained(as: "onStateChange-isEnabled-\(id)") != nil)
    }
    func removeOnStateChange(retainer: NSObject, id: UInt32) {
        UIControl.removeOnStateChange(retainer: retainer, id: id)
    }
}

public extension UIGestureRecognizer {
    func addAction(action: @escaping () -> ()) -> Self {
        let sleeve = ClosureSleeve(attachTo: self, closure: action)
        addTarget(sleeve, action: #selector(ClosureSleeve.invoke))
        return self
    }
}

public extension UIBarButtonItem {
    convenience init(title: String?, style: UIBarButtonItem.Style, action: @escaping () -> ()) {
        let sleeve = ClosureSleeve(closure: action)
        self.init(title: title, style: style, target: sleeve, action: #selector(ClosureSleeve.invoke))
        objc_setAssociatedObject(self, "[\(arc4random())]", sleeve, .OBJC_ASSOCIATION_RETAIN)
    }
}

public extension NSObject {
    private static var anything = ExtensionProperty<NSObject, Dictionary<String, Any>>()
    var extensions: Dictionary<String, Any>? {
        get {
            return NSObject.anything.get(self)
        }
    }

    func retain<T>(as string: String = "[\(arc4random())]", item: T) {
        NSObject.anything.modify(self, defaultValue: [:]) { box in
            box.value[string] = item
        }
    }
    func checkRetained(as string: String = "[\(arc4random())]") -> Any? {
        return NSObject.anything.get(self)?[string]
    }
    func unretain(_ string: String = "[\(arc4random())]") {
        NSObject.anything.modify(self, defaultValue: [:]) { box in
            box.value[string] = nil
        }
    }
}
