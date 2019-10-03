//
//  UISegmentedControl+addIndicator.swift
//  KwiftTemplate
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
    
    func getSegment(index: Int) -> UIView? {
        let title = self.titleForSegment(at: index)
        for s in subviews {
            if (s.find { ($0 as? UILabel)?.text == title }) != nil {
                return s
            }
        }
        return nil
    }
    func addIndicator(color: UIColor, size: CGFloat = 4){
        let buttonBar = UIView()
        // This needs to be false since we are using auto layout constraints
        buttonBar.translatesAutoresizingMaskIntoConstraints = false
        buttonBar.backgroundColor = color
        addSubview(buttonBar)
        
        let getNewBounds: ()->CGRect = { [weak self, weak buttonBar] in
            guard
                let self = self,
                let buttonBar = buttonBar,
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
            guard let self = self, let buttonBar = buttonBar else { return }
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
            guard let self = self, let buttonBar = buttonBar else { return }
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
