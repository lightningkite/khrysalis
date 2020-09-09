//
//  UISegmentedControl+addIndicator.swift
//  KhrysalisTemplate
//
//  Created by Joseph Ivie on 9/4/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit


extension UIView {
    fileprivate func find(_ filter: (UIView)->Bool) -> UIView? {
        if filter(self) {
            return self
        }
        for s in subviews {
            if let found = s.find(filter) {
                return found
            }
        }
        return nil
    }
}

extension UISegmentedControl {
    public func materialTabStyle(color: UIColor) {
        if #available(iOS 13.0, *) {
            selectedSegmentTintColor = .clear
        }
        tintColor = .clear
        backgroundColor = .clear
        
        let imageBounds = CGRect(x: 0, y: 0, width: 1, height: 1)
        UIGraphicsBeginImageContextWithOptions(imageBounds.size, false, 1)
        UIColor.clear.setFill()
        UIRectFill(imageBounds)
        let clearImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        setBackgroundImage(clearImage, for: .normal, barMetrics: .default)
        setBackgroundImage(clearImage, for: .selected, barMetrics: .default)
        setBackgroundImage(clearImage, for: .highlighted, barMetrics: .default)
        setBackgroundImage(clearImage, for: [.highlighted, .selected], barMetrics: .default)
        setDividerImage(clearImage, forLeftSegmentState: .normal, rightSegmentState: .normal, barMetrics: .default)

        
        
        setBackgroundImage(clearImage, for: .normal, barMetrics: UIBarMetrics.default)
        setBackgroundImage(clearImage, for: .focused, barMetrics: UIBarMetrics.default)
        setBackgroundImage(clearImage, for: .highlighted, barMetrics: UIBarMetrics.default)
        setBackgroundImage(clearImage, for: .selected, barMetrics: UIBarMetrics.default)
        addIndicator(color: color)
    }
    public func getSegment(index: Int) -> UIView? {
        let title = self.titleForSegment(at: index)
        for s in subviews {
            if (s.find { ($0 as? UILabel)?.text == title }) != nil {
                return s
            }
        }
        return nil
    }
    public func addIndicator(color: UIColor, size: CGFloat = 4){
        let buttonBar = UIView()
        // This needs to be false since we are using auto layout constraints
        buttonBar.translatesAutoresizingMaskIntoConstraints = false
        buttonBar.backgroundColor = color
        addSubview(buttonBar)

        let getNewBounds: ()->CGRect = { [weak self, weak buttonBar] in
            guard
                let self = self,
                self.numberOfSegments > 0, self.selectedSegmentIndex >= 0,
                self.selectedSegmentIndex < self.numberOfSegments,
                let segment = self.getSegment(index: self.selectedSegmentIndex)
                else { return CGRect.zero }
            let newBounds = CGRect(
                x: segment.frame.origin.x,
                y: self.bounds.size.height - size,
                width: segment.frame.size.width,
                height: size
            )
            print("New bounds \(newBounds)")
            return newBounds
        }

        buttonBar.frame = getNewBounds()

        var midAnimation = false

        self.addOnLayoutSubviews { [weak self, weak buttonBar] in
            guard let buttonBar = buttonBar else { return }
            let newBounds = getNewBounds()
            if newBounds != buttonBar.frame {
                midAnimation = true
                UIView.animate(withDuration: 0.3, animations: {
                    buttonBar.frame = newBounds
                }, completion: { _ in
                    midAnimation = false
                })
            }
        }

        self.addAction(for: .valueChanged, action: { [weak self, weak buttonBar] in
            guard let buttonBar = buttonBar else { return }
            let newBounds = getNewBounds()
            if newBounds != buttonBar.frame {
                midAnimation = true
                UIView.animate(withDuration: 0.3, animations: {
                    buttonBar.frame = newBounds
                }, completion: { _ in
                    midAnimation = false
                })
            }
        })
    }
}
