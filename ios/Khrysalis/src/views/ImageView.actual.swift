//
//  UIImageView+ext.swift
//
//  Created by Joseph Ivie on 10/9/19.
//

import Foundation
import UIKit
import AlamofireImage
import Alamofire


public extension UIImageView {
    func setImageResource(_ drawableMaker: Drawable) {
        self.image = drawableMaker.makeLayer(self).toImage()
        self.notifyParentSizeChanged()
    }
    func setImageBitmap(_ bitmap: UIImage) {
        self.image = bitmap
        self.notifyParentSizeChanged()
    }
}

//--- ImageView.loadImage(Image?)
//--- ImageView.loadImageAlt(Image?)

public extension UIImageView {
    static var loadingProgressTintColor: UIColor?
    static var loadingTrackTintColor: UIColor?

    func af_setImageProgress(
        withURL url: URL,
        placeholderImage: UIImage? = nil,
        imageTransition: ImageTransition = .noTransition,
        runImageTransitionIfCached: Bool = false,
        completion: (() -> Void)? = nil
        ) {
        post {
            self.image = nil
            let activityIndicatorView = UIProgressView(progressViewStyle: UIProgressView.Style.default)
            activityIndicatorView.progressTintColor = UIImageView.loadingProgressTintColor
            activityIndicatorView.trackTintColor = UIImageView.loadingTrackTintColor
            activityIndicatorView.setProgress(0, animated: true)
            activityIndicatorView.center.x = self.frame.size.width / 2
            activityIndicatorView.center.y = self.frame.size.height / 2
            self.addSubview(activityIndicatorView)
            weak var weakAIV = activityIndicatorView

            var filter: ImageFilter? = nil
            if self.frame.size.width != 0 && self.frame.size.height != 0 {
                switch self.contentMode {
                case .scaleToFill:
                    filter = ScaledToSizeFilter(size: self.frame.size)
                case .scaleAspectFill:
                filter = AspectScaledToFillSizeFilter(size: self.frame.size)
                case .scaleAspectFit:
                filter = AspectScaledToFitSizeFilter(size: self.frame.size)
                default:
                    filter = nil
                }
            }

            self.af_setImage(
                withURL: url,
                placeholderImage: placeholderImage,
                filter: filter,
                progress: { progress -> Void in
                    weakAIV?.setProgress(Float(progress.fractionCompleted), animated: true)
                    return
            },
                imageTransition: imageTransition,
                runImageTransitionIfCached: runImageTransitionIfCached,
                completion: { result in
                    if let error = result.error {
                        print("ERROR LOADING IMAGE: \(error.localizedDescription)")
                    }
                    weakAIV?.removeFromSuperview()
                    completion?()
            }
            )
        }
    }

    func loadImage(_ image: Image?) -> Void {
        switch(image){
        case let image as ImageReference:
            if let url = URL(string: image.uri.absoluteString) {
                af_setImageProgress(withURL: url, placeholderImage: nil, imageTransition: ImageTransition.noTransition, runImageTransitionIfCached: false, completion: nil)
            }
        case let image as ImageResource:
            setImageResource(image.resource)
        case let image as ImageBitmap:
            self.image = image.bitmap
        case let image as ImageRaw:
            break
        case let image as ImageRemoteUrl:
            if let url = URL(string: image.url) {
                af_setImageProgress(withURL: url, placeholderImage: nil, imageTransition: ImageTransition.noTransition, runImageTransitionIfCached: false, completion: nil)
            }
        default:
            break
        }
    }
    func loadImage(image: Image?) -> Void {
        return loadImage(image)
    }
}
