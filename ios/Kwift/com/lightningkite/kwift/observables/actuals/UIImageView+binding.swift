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

    func loadImage(_ image: Image?) {
        switch(image){
        case let image as ImageReference:
            af_setImageProgress(withURL: URL(string: image.uri.absoluteString)!, placeholderImage: nil, imageTransition: .noTransition, runImageTransitionIfCached: false, completion: nil)
        case let image as ImageBitmap:
            self.image = image.bitmap
        case let image as ImageRaw:
            break
        case let image as ImageRemoteUrl:
            af_setImageProgress(withURL: URL(string: image.url)!, placeholderImage: nil, imageTransition: .noTransition, runImageTransitionIfCached: false, completion: nil)
        default:
            break
        }
    }

    func bindImage(_ image: ObservableProperty<Image?>){
        image.addAndRunWeak(self) {(self, it) in
            self.loadImage(it)
        }
    }
}
