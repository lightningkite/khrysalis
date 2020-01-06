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
    
    static public let refreshBackgroundColorEvent = StandardEvent<Void>()
    
    weak var backgroundLayerBottom: UIView!
    weak var innerView: UIView!
    
    public var defaultBackgroundColor: UIColor = .white
    
    override open func viewDidLoad() {
        super.viewDidLoad()
        
        self.view = UIView(frame: .zero)
        self.view.backgroundColor = defaultBackgroundColor
        
        let bottom = UIView(frame: .zero)
        bottom.backgroundColor = defaultBackgroundColor
        self.view.addSubview(bottom)
        backgroundLayerBottom = bottom
        
        let m = main.generate(ViewDependency(self))
        innerView = m
        self.view.addSubview(innerView)
        
        if let main = main as? EntryPoint, let stack = main.mainStack {
            stack.addAndRunWeak(self) { (self, value) in
                self.refreshBackingColor()
            }
        }
        var lastOccurrance = Date()
        KwiftViewController.refreshBackgroundColorEvent.addWeak(self) { (self, value) in
            let now = Date()
            if now.timeIntervalSince(lastOccurrance) > 1 {
                lastOccurrance = now
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
                self.backgroundLayerBottom.layer.backgroundColor = self.getBackingColorBottom(self.innerView, currentY: 0, height: self.innerView.bounds.size.height-1) ?? self.defaultBackgroundColor.cgColor
            } else {
                UIView.animate(withDuration: 0.25, animations: {
                    self.view.layer.backgroundColor = self.getBackingColor(self.innerView) ?? self.defaultBackgroundColor.cgColor
                    self.backgroundLayerBottom.layer.backgroundColor = self.getBackingColorBottom(self.innerView, currentY: 0, height: self.innerView.bounds.size.height-1) ?? self.defaultBackgroundColor.cgColor
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
        if let backing = view.layer.guessBackingColor(), backing.alpha != 0.0 {
            return backing
        }
        return nil
    }
    private func getBackingColorBottom(_ view: UIView, currentY: CGFloat, height: CGFloat) -> CGColor? {
        for child in view.subviews.reversed() {
            if !child.isHidden,
                child.includeInLayout,
                child.frame.origin.y + child.frame.size.height >= height,
                let backing = getBackingColorBottom(
                    child,
                    currentY: currentY + child.frame.origin.y,
                    height: height
                ) {
                return backing
            }
        }
        if let backing = view.layer.guessBackingColor(), backing.alpha != 0.0 {
            return backing
        }
        return nil
    }
    
    override open func viewWillLayoutSubviews() {
        layout()
    }
    
    open func layout(){
        guard
            let innerView = innerView,
            let backgroundLayerBottom = backgroundLayerBottom,
            let selfView = self.view
            else { return }
        backgroundLayerBottom.frame = CGRect(
            origin: CGPoint(
                x: selfView.bounds.origin.x,
                y: selfView.bounds.origin.y + selfView.bounds.size.height/2
            ),
            size: CGSize(
                width: selfView.bounds.size.width,
                height: selfView.bounds.size.height/2
            )
        )
        innerView.frame.origin.y = UIApplication.shared.statusBarFrame.height
        innerView.frame.size.width = self.view.frame.size.width
        if #available(iOS 11.0, *) {
            let window = UIApplication.shared.keyWindow
            let bottomPadding = window?.safeAreaInsets.bottom ?? 0
            innerView.frame.size.height = self.view.frame.size.height - innerView.frame.origin.y - bottomPadding - keyboardHeight
        } else {
            innerView.frame.size.height = self.view.frame.size.height - innerView.frame.origin.y - keyboardHeight
        }
        innerView.layoutSubviews()
    }
    
    override open func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        removeKeyboardObservers()
    }
    
    var keyboardHeight: CGFloat = 0
    
    /// Asks the system to resign all first responders (usually input fields), which effectively
    /// causes the keyboard to dismiss itself.
    func resignAllFirstResponders() {
        view.endEditing(true)
    }
    
    func hideKeyboardWhenTappedAround() {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(dismissKeyboard))
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard() {
        resignAllFirstResponders()
    }
    
    func addKeyboardObservers() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillChangeFrame),
            name: UIResponder.keyboardWillChangeFrameNotification,
            object: nil
        )
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(keyboardWillHide),
            name: UIResponder.keyboardWillHideNotification,
            object: nil
        )
    }
    
    /// Remove observers that were added previously.
    func removeKeyboardObservers() {
        NotificationCenter.default.removeObserver(
            self,
            name: UIResponder.keyboardWillChangeFrameNotification,
            object: self.view.window
        )
        NotificationCenter.default.removeObserver(
            self,
            name: UIResponder.keyboardWillHideNotification,
            object: self.view.window
        )
    }
    
    
    /// Method's notified when the keyboard is about to be shown or change its size.
    ///
    /// - Parameter notification: System keyboard notification
    @objc func keyboardWillChangeFrame(notification: NSNotification) {
        if
            let window = view.window,
            let responder = view.firstResponder,
            let userInfo = notification.userInfo,
            let keyboardFrameValue = userInfo[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue,
            let keyboardAnimationDuration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber
        {
            let keyboardHeight = keyboardFrameValue.cgRectValue.height
            UIView.animate(
                withDuration: keyboardAnimationDuration.doubleValue,
                animations: {
                    self.keyboardHeight = keyboardHeight
                    self.layout()
                }
            )
            DispatchQueue.main.asyncAfter(deadline: .now() + 0.1, execute: {
                if let view = UIResponder.current as? UIView {
                    view.scrollToMe(animated: true)
                }
            })
        }
    }
    
    /// Method's notified when the keyboard is about to be dismissed.
    ///
    /// - Parameter notification: System keyboard notification
    @objc func keyboardWillHide(notification: NSNotification) {
        if
            let window = self.view.window,
            let userInfo = notification.userInfo,
            let animationDuration = userInfo[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber
        {
            UIView.animate(
                withDuration: animationDuration.doubleValue,
                animations: {
                    self.keyboardHeight = 0
                    self.layout()
            }
            )
        }
    }
}
