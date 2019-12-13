//
//  ViewDependency+image.swift
//  Kwift
//
//  Created by Joseph Ivie on 10/21/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import Foundation
import UIKit
import Photos


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
    
    func requestImageGallery(onResult: @escaping (Uri) -> Void) {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            self.requestImageGalleryRaw(onResult: onResult)
        } else {
            PHPhotoLibrary.requestAuthorization {_ in
                self.requestImageGalleryRaw(onResult: onResult)
            }
        }
    }
    func requestImageCamera(onResult: @escaping (Uri) -> Void) {
        if AVCaptureDevice.authorizationStatus(for: .video) == .authorized {
            AVCaptureDevice.requestAccess(for: .video) { granted in
                if granted {
                    if PHPhotoLibrary.authorizationStatus() == .authorized {
                        self.requestImageCameraRaw(onResult: onResult)
                    } else {
                        PHPhotoLibrary.requestAuthorization {_ in
                            self.requestImageCameraRaw(onResult: onResult)
                        }
                    }
                }
            }
        } else {
            AVCaptureDevice.requestAccess(for: .video) { granted in
                if granted {
                    if PHPhotoLibrary.authorizationStatus() == .authorized {
                        self.requestImageCameraRaw(onResult: onResult)
                    } else {
                        PHPhotoLibrary.requestAuthorization {_ in
                            self.requestImageCameraRaw(onResult: onResult)
                        }
                    }
                }
            }
        }
    }
    private func requestImageGalleryRaw(onResult: @escaping (Uri) -> Void) {
        if UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum){
            let imageDelegate = self.imageDelegate
            imageDelegate.onImagePicked = onResult
            imageDelegate.prepareGallery()
            self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
        }
    }
    private func requestImageCameraRaw(onResult: @escaping (Uri) -> Void) {
        if UIImagePickerController.isSourceTypeAvailable(.camera){
            let imageDelegate = self.imageDelegate
            imageDelegate.onImagePicked = onResult
            imageDelegate.prepareCamera()
            self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
        }
    }
}

private class ImageDelegate : NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {
    
    var imagePicker = UIImagePickerController()
    var onImagePicked: ((Uri)->Void)? = nil
    
    func prepareGallery(){
        imagePicker.delegate = self
        imagePicker.sourceType = .savedPhotosAlbum
        imagePicker.allowsEditing = false
    }
    
    func prepareCamera(){
        imagePicker.delegate = self
        imagePicker.sourceType = .camera
        imagePicker.cameraCaptureMode = .photo
        imagePicker.cameraDevice = .front
        imagePicker.allowsEditing = false
    }
    
//    @objc public func handleResult(image: UIImage, didFinishSavingWithError error: NSError?, contextInfo: UnsafeMutableRawPointer?) {
//        if error == nil {
//            imagePicker.dismiss(animated: true, completion: {
//                image.file
////                self.onImagePicked?(URL(fileURLWithPath: path, isDirectory: false))
//                self.onImagePicked = nil
//            })
//        }
//    }
    
    public func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        if #available(iOS 11.0, *) {
            if let image = info[.imageURL] as? URL {
                DispatchQueue.main.async {
                    picker.dismiss(animated: true, completion: {
                        self.onImagePicked?(image)
                        self.onImagePicked = nil
                    })
                }
                return
            }
        }
        let image = info[.originalImage] as! UIImage
        print(image)
        var localId: String = ""
        PHPhotoLibrary.shared().performChanges({
            let r = PHAssetChangeRequest.creationRequestForAsset(from: image)
            localId = r.placeholderForCreatedAsset!.localIdentifier
        }, completionHandler: { (success, error) in
            if !success {
                print(error)
                DispatchQueue.main.async {
                    picker.dismiss(animated: true, completion: {
                        self.onImagePicked = nil
                    })
                }
            } else {
                let assetResult = PHAsset.fetchAssets(withLocalIdentifiers: [localId], options: nil)
                let asset = assetResult.firstObject!
                PHImageManager.default().requestImageData(for: asset, options: nil) { (data, string, orientation, map) in
                    let fileUrl = map!["PHImageFileURLKey"] as! URL
                    DispatchQueue.main.async {
                        picker.dismiss(animated: true, completion: {
                            self.onImagePicked?(fileUrl)
                            self.onImagePicked = nil
                        })
                    }
                }
            }
        })
//        UIImageWriteToSavedPhotosAlbum(image, self, #selector(handleResult(image:didFinishSavingWithError:contextInfo:)), nil)
    }
}
