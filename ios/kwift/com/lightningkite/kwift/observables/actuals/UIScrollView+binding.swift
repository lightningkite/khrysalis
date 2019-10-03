//
//  UIScrollView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/4/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

extension UIScrollView {
    
//    func bind<T>(
//        _ items: Array<T>,
//        _ showIndex: MutableObservableProperty<Int32> = StandardObservableProperty(0),
//        _ makeView: (T)->UIView
//        ) {
//        bind(items: items, showIndex: showIndex, makeView: makeView)
//    }
//    func bind<T>(
//        items: Array<T>,
//        showIndex: MutableObservableProperty<Int32> = StandardObservableProperty(0),
//        makeView: (T)->UIView
//    ) {
//        let containerView = UIView()
//
//        let layout: ()->Void = { [weak self, weak containerView] in
//            guard let containerView = containerView, let self = self else { return }
//            var index = 0
//            for view in containerView.subviews {
//                view.frame = CGRect(
//                    x: CGFloat(index) * self.frame.width,
//                    y: 0,
//                    width: self.frame.width,
//                    height: self.frame.height
//                )
//                view.flex.layout()
//                index += 1
//            }
//            containerView.frame = CGRect(
//                x: 0,
//                y: 0,
//                width: self.frame.width * CGFloat(containerView.subviews.count),
//                height: self.frame.height
//            )
//            self.contentSize.width = containerView.frame.width
//            self.contentSize.height = containerView.frame.height
//        }
//
//        self.addSubview(containerView)
//        for item in items {
//            containerView.addSubview(makeView(item))
//        }
//        layout()
//        self.addOnLayoutSubviews { layout() }
//
//        //Handle Scrolling
//        var suppressRead = false
//        var suppressWrite = false
//        showIndex.addAndRunWeak(self) { this, value in
//            guard !suppressRead else { return }
//            suppressWrite = true
//            UIView.animate(withDuration: 0.3, animations: {
//                let offset = CGFloat(value) * this.frame.size.width
//                this.contentOffset.x = offset
//                print("Animating to offset \(offset)")
//            }, completion: { _ in
//                suppressWrite = false
//            })
//        }
//        let dg = IndexScrollDelegate { [weak showIndex] in
//            guard !suppressWrite else { return }
//            suppressRead = true
//            showIndex?.value = Int32($0)
//            suppressRead = false
//        }
//        self.retain(as: "IndexScrollDelegate", item: dg)
//        self.delegate = dg
//    }
//
//    class IndexScrollDelegate : NSObject, UIScrollViewDelegate {
//        var action: (Int) -> Void
//        init(_ action: @escaping (Int) -> Void) {
//            self.action = action
//        }
//        func scrollViewDidScroll(_ scrollView: UIScrollView) {
//            action(Int((scrollView.contentOffset.x / scrollView.frame.size.width).rounded()))
//        }
//    }
}
