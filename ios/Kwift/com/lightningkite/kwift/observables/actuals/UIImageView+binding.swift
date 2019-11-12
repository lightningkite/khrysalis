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


public extension UIImageView {
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

    func loadImageData(_ imageData: ObservableProperty<ImageData?>){
        imageData.addAndRunWeak(self) {(self, it) in
            self.image = it
        }
    }

    func loadImageData(_ image: ImageData){
        self.image = image
    }

    func loadImageData(image: ImageData){
        self.image = image
    }

    func loadImageData(imageData: ObservableProperty<ImageData?>){
        imageData.addAndRunWeak(self) {(self, it) in
            self.image = it
        }
    }
    
    func loadImageReference(imageReference: ImageReference?){
        if let image = imageReference{
            URLSession.shared.dataTask(with: image, completionHandler: { data, response, error in
                DispatchQueue.main.async {
                    if let data = data {
                        let newImage = UIImage(data: data)
                        self.image = newImage
                    }
                }
            }).resume()
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
