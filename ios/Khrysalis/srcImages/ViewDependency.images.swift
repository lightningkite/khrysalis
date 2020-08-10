//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import Alamofire
import AlamofireImage
import Photos
import AVKit
import MapKit
import EventKitUI
import DKImagePickerController


//--- ViewDependency
public extension ViewDependency {
    //--- ViewDependency image helpers
    private static let delegateExtension = ExtensionProperty<ViewDependency, ImageDelegate>()
    private var imageDelegate: ImageDelegate {
        if let existing = ViewDependency.delegateExtension.get(self) {
            return existing
        }
        let new = ImageDelegate()
        ViewDependency.delegateExtension.set(self, new)
        return new
    }

    //--- ViewDependency.requestImageGallery((URL)->Unit)
    public func requestImageGallery(callback: @escaping (URL) -> Void) {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            self.requestImageGalleryRaw(callback: callback)
        } else {
            PHPhotoLibrary.requestAuthorization {_ in
                DispatchQueue.main.async {
                    self.requestImageGalleryRaw(callback: callback)
                }
            }
        }
    }
    private func requestImageGalleryRaw(callback: @escaping (URL) -> Void) {
        if UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum){
            let imageDelegate = self.imageDelegate
            imageDelegate.onImagePicked = callback
            imageDelegate.prepareGallery()
            self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
        }
    }

    //--- ViewDependency.requestImageCamera(Boolean, (URL)->Unit)

    //--- ViewDependency.requestVideoGallery((URL)->Unit)
    func requestVideoGallery(_ callback: (URL) -> Void) -> Void {
        TODO()
    }

    //--- ViewDependency.requestVideosGallery((List<URL>)->Unit)
    func requestVideosGallery(_ callback: (Array<URL>) -> Void) -> Void {
        TODO()
    }

    //--- ViewDependency.requestVideoCamera(Boolean, (URL)->Unit)
    func requestVideoCamera(_ front: Bool, _ callback: (URL) -> Void) -> Void {
        TODO()
    }
    func requestVideoCamera(front: Bool, callback: (URL) -> Void) -> Void {
        return requestVideoCamera(front, callback)
    }

    //--- ViewDependency.requestMediasGallery((List<URL>)->Unit)
    func requestMediasGallery(_ callback: (Array<URL>) -> Void) -> Void {
        TODO()
    }
    //--- ViewDependency.requestMediaGallery((URL)->Unit)
    func requestMediaGallery(_ callback: (URL) -> Void) -> Void {
        TODO()
    }

    //--- ViewDependency.requestImagesGallery((List<URL>)->Unit)
    public func requestImagesGallery(callback: @escaping (Array<URL>) -> Void) -> Void {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            self.requestImagesGalleryRaw(callback: callback)
        } else {
            PHPhotoLibrary.requestAuthorization {_ in
                DispatchQueue.main.async {
                    self.requestImagesGalleryRaw(callback: callback)
                }
            }
        }
    }
    private func requestImagesGalleryRaw(callback: @escaping (Array<URL>) -> Void) {
        let pickerController = DKImagePickerController()
        pickerController.assetType = .allPhotos
        pickerController.didSelectAssets = { (assets: [DKAsset]) in
            print("didSelectAssets")
            print(assets)
            //Select Assets
            var result: Array<URL> = []
            var remaining = assets.count
            print("Assets remaining: \(remaining)")
            for item in assets {
                getUrl(editedImage: nil, originalImage: nil, asset: item.originalAsset, callback: { url in
                    remaining -= 1
                    print("Assets remaining: \(remaining)")
                    if let url = url {
                        result.append(url)
                    } else {
                        //... dunno how to handle error
                    }
                    if remaining == 0 {
                        print("Finish")
                        callback(result)
                    }
                })
            }
        }
        self.parentViewController.present(pickerController, animated: true){}
    }

    //--- ViewDependency.requestImageCamera((URL)->Unit)
    public func requestImageCamera(front:Bool = false, callback: @escaping (URL) -> Void) {
        DispatchQueue.main.async {
            if AVCaptureDevice.authorizationStatus(for: .video) == .authorized {
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    DispatchQueue.main.async {
                        if granted {
                            if PHPhotoLibrary.authorizationStatus() == .authorized {
                                self.requestImageCameraRaw(front:front, callback: callback)
                            } else {
                                PHPhotoLibrary.requestAuthorization {_ in
                                    self.requestImageCameraRaw(front:front, callback: callback)
                                }
                            }
                        }
                    }
                }
            } else {
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    DispatchQueue.main.async {
                        if granted {
                            if PHPhotoLibrary.authorizationStatus() == .authorized {
                                self.requestImageCameraRaw(front:front, callback: callback)
                            } else {
                                PHPhotoLibrary.requestAuthorization {_ in
                                    self.requestImageCameraRaw(front:front, callback: callback)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private func requestImageCameraRaw(front:Bool, callback: @escaping (URL) -> Void) {
        DispatchQueue.main.async {
            if UIImagePickerController.isSourceTypeAvailable(.camera){
                let imageDelegate = self.imageDelegate
                imageDelegate.onImagePicked = callback
                imageDelegate.prepareCamera(front: front)
                self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
            }
        }
    }
}

//--- Image helpers

private class ImageDelegate : NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

    var imagePicker = UIImagePickerController()
    var onImagePicked: ((URL)->Void)? = nil

    func prepareGallery(){
        imagePicker.delegate = self
        imagePicker.sourceType = .photoLibrary
        imagePicker.allowsEditing = false
    }

    func prepareCamera(front:Bool){
        imagePicker.delegate = self
        imagePicker.sourceType = .camera
        imagePicker.cameraCaptureMode = .photo
        if front{
            imagePicker.cameraDevice = .front
        }else{
            imagePicker.cameraDevice = .rear
        }
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
                print("Image retrieved directly using .imageURL")
                DispatchQueue.main.async {
                    picker.dismiss(animated: true, completion: {
                        self.onImagePicked?(image)
                        self.onImagePicked = nil
                    })
                }
                return
            }
        }
        
        getUrl(editedImage: info[.editedImage] as? UIImage, originalImage: info[.originalImage] as? UIImage, asset: info[.phAsset] as? PHAsset, callback: { url in
            if let url = url {
                DispatchQueue.main.async {
                    picker.dismiss(animated: true, completion: {
                        self.onImagePicked?(url)
                        self.onImagePicked = nil
                    })
                }
            }
        })
    }
}

fileprivate func getUrl(editedImage: UIImage?, originalImage: UIImage?, asset: PHAsset?, callback: @escaping (URL?)->Void) {
    if let editedImage = editedImage {
        if let url = editedImage.saveTemp() {
            print("Image retrieved using save due to edit")
            callback(url)
        } else {
            print("Image retrieval failed")
            callback(nil)
        }
    } else if let asset = asset {
        asset.getURL(completionHandler: { url in
            if let url = url {
                print("Image retrieved using asset")
                callback(url)
            } else {
                //That failed, let's just save the image
                if let originalImage = originalImage, let url = originalImage.saveTemp() {
                    print("Image retrieved using save as backup")
                    callback(url)
                } else {
                    print("Image retrieval failed")
                    callback(nil)
                }
            }
        })
    } else {
        //That failed, let's just save the image
        if let originalImage = originalImage, let url = originalImage.saveTemp() {
            print("Image retrieved using save as backup")
            callback(url)
        } else {
            print("Image retrieval failed")
            callback(nil)
        }
    }
}

// save
extension UIImage {
    
    func saveTemp() -> URL? {
        let id = UUID().uuidString
        let tempDirectoryUrl = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("temp-khrysalis-photos-\(id)")
        guard let url2 = self.save(at: tempDirectoryUrl) else {
            return nil
        }
        print(url2)
        return url2
    }

    func save(at directory: FileManager.SearchPathDirectory,
              pathAndImageName: String,
              createSubdirectoriesIfNeed: Bool = true,
              compressionQuality: CGFloat = 1.0)  -> URL? {
        do {
        let documentsDirectory = try FileManager.default.url(for: directory, in: .userDomainMask,
                                                             appropriateFor: nil,
                                                             create: false)
        return save(at: documentsDirectory.appendingPathComponent(pathAndImageName),
                    createSubdirectoriesIfNeed: createSubdirectoriesIfNeed,
                    compressionQuality: compressionQuality)
        } catch {
            print("-- Error: \(error)")
            return nil
        }
    }

    func save(at url: URL,
              createSubdirectoriesIfNeed: Bool = true,
              compressionQuality: CGFloat = 1.0)  -> URL? {
        do {
            if createSubdirectoriesIfNeed {
                try FileManager.default.createDirectory(at: url.deletingLastPathComponent(),
                                                        withIntermediateDirectories: true,
                                                        attributes: nil)
            }
            guard let data = jpegData(compressionQuality: compressionQuality) else { return nil }
            try data.write(to: url)
            return url
        } catch {
            print("-- Error: \(error)")
            return nil
        }
    }
}

// load from path

extension UIImage {
    convenience init?(fileURLWithPath url: URL, scale: CGFloat = 1.0) {
        do {
            let data = try NSData(contentsOf: url)
            self.init(data: data! as Data, scale: scale)
        } catch {
            print("-- Error: \(error)")
            return nil
        }
    }
}

extension PHAsset {

    func getURL(completionHandler : @escaping ((_ responseURL : URL?) -> Void)){
        if self.mediaType == .image {
            let options: PHContentEditingInputRequestOptions = PHContentEditingInputRequestOptions()
            options.canHandleAdjustmentData = {(adjustmeta: PHAdjustmentData) -> Bool in
                return true
            }
            self.requestContentEditingInput(with: options, completionHandler: {(contentEditingInput: PHContentEditingInput?, info: [AnyHashable : Any]) -> Void in
                completionHandler(contentEditingInput!.fullSizeImageURL as URL?)
            })
        } else if self.mediaType == .video {
            let options: PHVideoRequestOptions = PHVideoRequestOptions()
            options.version = .original
            PHImageManager.default().requestAVAsset(forVideo: self, options: options, resultHandler: {(asset: AVAsset?, audioMix: AVAudioMix?, info: [AnyHashable : Any]?) -> Void in
                if let urlAsset = asset as? AVURLAsset {
                    let localVideoUrl: URL = urlAsset.url as URL
                    completionHandler(localVideoUrl)
                } else {
                    completionHandler(nil)
                }
            })
        }
    }
}
