//
//  UIImageView+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit
import Alamofire
import AlamofireImage


extension UIImageView {
    func af_setImageProgress(
        withURL url: URL,
        placeholderImage: UIImage? = nil,
        imageTransition: ImageTransition = .noTransition,
        runImageTransitionIfCached: Bool = false,
        completion: ((DataResponse<UIImage>) -> Void)? = nil
        ) {
        let activityIndicatorView = UIProgressView(progressViewStyle: UIProgressView.Style.default)
        activityIndicatorView.setProgress(0, animated: true)
        activityIndicatorView.center.x = self.frame.size.width / 2
        activityIndicatorView.center.y = self.frame.size.height / 2
        self.addSubview(activityIndicatorView)
        weak var weakAIV = activityIndicatorView
        af_setImage(
            withURL: url,
            placeholderImage: placeholderImage,
            progress: { progress -> Void in
                weakAIV?.setProgress(Float(progress.fractionCompleted), animated: true)
                return
        },
            imageTransition: imageTransition,
            runImageTransitionIfCached: runImageTransitionIfCached,
            completion: { result in
                weakAIV?.removeFromSuperview()
                completion?(result)
        }
        )
    }
    
    func loadUrl(_ imageUrl: String?) {
        return loadUrl(imageUrl: imageUrl)
    }
    func loadUrl(imageUrl: String?) {
        if let imageUrl = imageUrl, let url = URL(string: imageUrl) {
            af_setImageProgress(withURL: url, placeholderImage: nil, imageTransition: .noTransition, runImageTransitionIfCached: false, completion: nil)
        }
    }
    
    func loadUrl(_ imageUrl: ObservableProperty<String?>) {
        loadUrl(imageUrl: imageUrl)
    }
    func loadUrl(imageUrl: ObservableProperty<String?>) {
        imageUrl.addAndRunWeak(self) { (self, it) in
            self.loadUrl(it)
        }
    }
    
    func loadUrlNotNull(_ imageUrl: ObservableProperty<String>) {
        loadUrlNotNull(imageUrl: imageUrl)
    }
    func loadUrlNotNull(imageUrl: ObservableProperty<String>) {
        imageUrl.addAndRunWeak(self) { (self, it) in
            if it.isBlank() {
                self.loadUrl(nil)
            } else {
                self.loadUrl(it)
            }
        }
    }
}
