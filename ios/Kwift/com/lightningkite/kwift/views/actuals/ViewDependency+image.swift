//
//  ViewDependency+image.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/21/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit


public extension ViewDependency {
    
    private static let delegateExtension = ExtensionProperty<ViewDependency, ImageDelegate>()
    private var imageDelegate: ImageDelegate {
        if let existing = ViewDependency.delegateExtension.get(self) {
            return existing
        }
        let new = ImageDelegate()
        ViewDependency.delegateExtension.set(self, new)
        return new
    }
    
    func requestImageGallery(onResult: @escaping (ImageReference) -> Void) {
        if UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum){
            let imageDelegate = self.imageDelegate
            imageDelegate.onImagePicked = onResult
            imageDelegate.prepareGallery()
            self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
        }
    }
    func requestImageCamera(onResult: @escaping (ImageReference) -> Void) {
        if UIImagePickerController.isSourceTypeAvailable(.camera){
            let imageDelegate = self.imageDelegate
            imageDelegate.onImagePicked = onResult
            imageDelegate.prepareCamera()
            self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
        }
    }
    
    func loadImage(_ imageReference: ImageReference, onResult: @escaping (ImageData?)->Void){
        URLSession.shared.dataTask(with: imageReference, completionHandler: { data, response, error in
            DispatchQueue.main.async {
                if let data = data {
                    onResult(UIImage(data: data))
                } else {
                    onResult(nil)
                }
            }
        }).resume()
    }
}

private class ImageDelegate : NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    var imagePicker = UIImagePickerController()
    var onImagePicked: ((ImageReference)->Void)? = nil
    
    func prepareGallery(){
        imagePicker.delegate = self
        imagePicker.sourceType = .savedPhotosAlbum
        imagePicker.allowsEditing = true
    }
    
    func prepareCamera(){
        imagePicker.delegate = self
        imagePicker.cameraCaptureMode = .photo
        imagePicker.sourceType = .camera
        imagePicker.cameraDevice = .front
        imagePicker.allowsEditing = true
    }
    
    public func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        var maybeImage: URL? = nil
        if #available(iOS 11.0, *) {
            maybeImage = info[.imageURL] as? URL
        }
        guard let image = maybeImage else { return }
        picker.dismiss(animated: true, completion: {
            self.onImagePicked?(image)
            self.onImagePicked = nil
        })
    }
}
