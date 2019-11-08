//
//  KwiftViewController.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/21/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit


open class KwiftViewController: UIViewController, UINavigationControllerDelegate {
    
    open var main: ViewGenerator
    public init(_ main: ViewGenerator){
        self.main = main
        super.init(nibName: nil, bundle: nil)
    }
    required public init?(coder: NSCoder) {
        self.main = ViewGenerator.Default()
        super.init(coder: coder)
    }
    
    weak var innerView: UIView!
    
    public var defaultBackgroundColor: UIColor = .white
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        
        self.view = UIView(frame: .zero)
        self.view.backgroundColor = defaultBackgroundColor
        
        let m = main.generate(ViewDependency(self))
        innerView = m
        self.view.addSubview(innerView)
        
        if let main = main as? EntryPoint, let stack = main.mainStack {
            stack.addAndRunWeak(self) { (self, value) in
                self.refreshBackingColor()
            }
        }
        
        showDialogEvent.addWeak(self){ (this, request) in
            let dep = ViewDependency(self)
            let message = request.string.get(dependency: dep)
            let alert = UIAlertController(title: "", message: message, preferredStyle: .alert)
            if let confirmation = request.confirmation {
                alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { (action) in
                    confirmation()
                }))
                alert.addAction(UIAlertAction(title: "Cancel", style: .default, handler: { (action) in
                    
                }))
            } else {
                alert.addAction(UIAlertAction(title: "OK", style: .default, handler: { (action) in
                    
                }))
            }
            this.present(alert, animated: true, completion: {})
        }
    
        addKeyboardObservers()
        hideKeyboardWhenTappedAround()
    }
    
    private var first = true
    public func refreshBackingColor() {
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.01, execute: {
            if self.first {
                self.first = false
                self.view.layer.backgroundColor = self.getBackingColor(self.innerView) ?? self.defaultBackgroundColor.cgColor
            } else {
                UIView.animate(withDuration: 0.25, animations: {
                    self.view.layer.backgroundColor = self.getBackingColor(self.innerView) ?? self.defaultBackgroundColor.cgColor
                })
            }
        })
    }
    private func getBackingColor(_ view: UIView) -> CGColor? {
        for child in view.subviews.reversed() {
            if let backing = getBackingColor(child), !child.isHidden, child.includeInLayout, child.frame.origin == .zero {
                return backing
            }
        }
        if let backing = view.layer.backgroundColor, backing.alpha > 0.1 {
            return backing
        }
        return nil
    }
    
    override open func viewDidLayoutSubviews() {
        guard let innerView = innerView, let selfView = self.view else { return }
        innerView.frame.origin.y = UIApplication.shared.statusBarFrame.height
        innerView.frame.size.width = self.view.frame.size.width
        if #available(iOS 11.0, *) {
            let window = UIApplication.shared.keyWindow
            let bottomPadding = window?.safeAreaInsets.bottom ?? 0
            innerView.frame.size.height = self.view.frame.size.height - innerView.frame.origin.y - bottomPadding
        } else {
            innerView.frame.size.height = self.view.frame.size.height - innerView.frame.origin.y
        }
        innerView.layoutSubviews()
    }
    
    override open func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        removeKeyboardObservers()
    }
    
}
