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

    private func withLibraryPermission(action: @escaping ()->Void) {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            action()
        } else {
            PHPhotoLibrary.requestAuthorization {_ in
                DispatchQueue.main.async {
                    action()
                }
            }
        }
    }
    //--- ViewDependency.requestImageGallery((URL)->Unit)
    func requestImageGallery(callback: @escaping (URL) -> Void) {
        withLibraryPermission {if UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum){
                let imageDelegate = self.imageDelegate
                imageDelegate.forImages()
                imageDelegate.onImagePicked = callback
                imageDelegate.prepareGallery()
                self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
            }
        }
    }
    
    //--- ViewDependency.requestVideoGallery((URL)->Unit)
    func requestVideoGallery(callback: @escaping (URL) -> Void) -> Void {
        withLibraryPermission {if UIImagePickerController.isSourceTypeAvailable(.savedPhotosAlbum){
                let imageDelegate = self.imageDelegate
                imageDelegate.forVideo()
                imageDelegate.onImagePicked = callback
                imageDelegate.prepareGallery()
                self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
            }
        }
    }


    //--- ViewDependency.requestVideosGallery((List<URL>)->Unit)
    func requestVideosGallery(callback: @escaping (Array<URL>) -> Void) -> Void {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            self.requestImagesGalleryRaw(type: .allVideos, callback: callback)
        } else {
            PHPhotoLibrary.requestAuthorization {_ in
                DispatchQueue.main.async {
                    self.requestImagesGalleryRaw(type: .allVideos, callback: callback)
                }
            }
        }
    }


    //--- ViewDependency.requestVideoCamera(Boolean, (URL)->Unit)
    func requestVideoCamera(front: Bool = false, callback: @escaping (URL) -> Void) -> Void {
        withCameraPermission {
            DispatchQueue.main.async {
                if UIImagePickerController.isSourceTypeAvailable(.camera){
                    if(UIImagePickerController.availableMediaTypes(for: .camera)?.contains("public.movie") == true) {
                        let imageDelegate = self.imageDelegate
                        imageDelegate.onImagePicked = callback
                        imageDelegate.forVideo()
                        imageDelegate.prepareCamera(front: front)
                        self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
                    }
                }
            }
        }
    }

    //--- ViewDependency.requestMediasGallery((List<URL>)->Unit)
    func requestMediasGallery(callback: @escaping (Array<URL>) -> Void) -> Void {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            self.requestImagesGalleryRaw(type: .allAssets, callback: callback)
        } else {
            PHPhotoLibrary.requestAuthorization {_ in
                DispatchQueue.main.async {
                    self.requestImagesGalleryRaw(type: .allAssets, callback: callback)
                }
            }
        }
    }
    
    //--- ViewDependency.requestMediaGallery((URL)->Unit)
    func requestMediaGallery(callback: @escaping (URL) -> Void) -> Void {
        
    }


    //--- ViewDependency.requestImagesGallery((List<URL>)->Unit)
    public func requestImagesGallery(callback: @escaping (Array<URL>) -> Void) -> Void {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            self.requestImagesGalleryRaw(type: .allPhotos, callback: callback)
        } else {
            PHPhotoLibrary.requestAuthorization {_ in
                DispatchQueue.main.async {
                    self.requestImagesGalleryRaw(type: .allPhotos, callback: callback)
                }
            }
        }
    }
    private func requestImagesGalleryRaw(type: DKImagePickerControllerAssetType, callback: @escaping (Array<URL>) -> Void) {
        let pickerController = DKImagePickerController()
        pickerController.assetType = type
        pickerController.didSelectAssets = { (assets: [DKAsset]) in
            print("didSelectAssets")
            print(assets)
            //Select Assets
            var result: Array<URL> = []
            var remaining = assets.count
            print("Assets remaining: \(remaining)")
            for item in assets {
                item.originalAsset!.getUrl { url in
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
                }
            }
        }
        self.parentViewController.present(pickerController, animated: true){}
    }

    //--- ViewDependency.requestImageCamera((URL)->Unit)
    public func requestImageCamera(front:Bool = false, callback: @escaping (URL) -> Void) {
        withCameraPermission {
            DispatchQueue.main.async {
                if UIImagePickerController.isSourceTypeAvailable(.camera){
                    if(UIImagePickerController.availableMediaTypes(for: .camera)?.contains("public.image") == true) {
                        let imageDelegate = self.imageDelegate
                        imageDelegate.onImagePicked = callback
                        imageDelegate.forImages()
                        imageDelegate.prepareCamera(front: front)
                        self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
                    }
                }
            }
        }
    }
    private func withCameraPermission(action: @escaping ()->Void) {
        DispatchQueue.main.async {
            if AVCaptureDevice.authorizationStatus(for: .video) == .authorized {
                AVCaptureDevice.requestAccess(for: .video) { granted in
                    DispatchQueue.main.async {
                        if granted {
                            if PHPhotoLibrary.authorizationStatus() == .authorized {
                                action()
                            } else {
                                PHPhotoLibrary.requestAuthorization {_ in
                                    action()
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
                                    action()
                            } else {
                                PHPhotoLibrary.requestAuthorization {_ in
                                    action()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
}

//--- Image helpers

private class ImageDelegate : NSObject, UIImagePickerControllerDelegate, UINavigationControllerDelegate {

    var imagePicker = UIImagePickerController()
    var onImagePicked: ((URL)->Void)? = nil
    
    func forVideo(){
        imagePicker.mediaTypes = ["public.movie"]
    }
    func forImages(){
        imagePicker.mediaTypes = ["public.image"]
    }
    func forAll(){
        imagePicker.mediaTypes = ["public.image", "public.movie"]
    }

    func prepareGallery(){
        imagePicker.delegate = self
        imagePicker.sourceType = .photoLibrary
        imagePicker.allowsEditing = false
    }

    func prepareCamera(front:Bool){
        imagePicker.delegate = self
        imagePicker.sourceType = .camera
        if imagePicker.mediaTypes.contains("public.image") {
            imagePicker.cameraCaptureMode = .photo
        } else {
            imagePicker.cameraCaptureMode = .video
        }
        if front{
            imagePicker.cameraDevice = .front
        }else{
            imagePicker.cameraDevice = .rear
        }
        imagePicker.allowsEditing = false
    }

    public func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
        if #available(iOS 11.0, *) {
            if let image = info[.imageURL] as? URL ?? info[.mediaURL] as? URL {
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
        if let asset = info[.phAsset] as? PHAsset {
            asset.getUrl { url in
                if let url = url {
                    DispatchQueue.main.async {
                        picker.dismiss(animated: true, completion: {
                            self.onImagePicked?(url)
                            self.onImagePicked = nil
                        })
                    }
                } else {
                    picker.dismiss(animated: true, completion: {
                        self.onImagePicked = nil
                    })
                }
            }
        } else if let originalImage = info[.originalImage] as? UIImage, let url = originalImage.saveTemp() {
            print("Image retrieved using save as backup")
            picker.dismiss(animated: true, completion: {
                self.onImagePicked?(url)
                self.onImagePicked = nil
            })
        } else {
            picker.dismiss(animated: true, completion: {
                self.onImagePicked = nil
            })
        }
    }
}

fileprivate extension PHAsset {
    func getUrl(completionHandler: @escaping (URL?)->Void) {
        if self.mediaType == .image {
            let options: PHContentEditingInputRequestOptions = PHContentEditingInputRequestOptions()
            options.canHandleAdjustmentData = {(adjustmeta: PHAdjustmentData) -> Bool in
                return true
            }
            self.requestContentEditingInput(with: options, completionHandler: {(contentEditingInput: PHContentEditingInput?, info: [AnyHashable : Any]) -> Void in
                DispatchQueue.main.async {
                    completionHandler(contentEditingInput!.fullSizeImageURL as URL?)
                }
            })
        } else if self.mediaType == .video {
            let options: PHVideoRequestOptions = PHVideoRequestOptions()
            options.version = .original
            PHImageManager.default().requestAVAsset(forVideo: self, options: options, resultHandler: {(asset: AVAsset?, audioMix: AVAudioMix?, info: [AnyHashable : Any]?) -> Void in
                if let urlAsset = asset as? AVURLAsset {
                    let localVideoUrl: URL = urlAsset.url as URL
                    DispatchQueue.main.async {
                        completionHandler(localVideoUrl)
                    }
                } else {
                    DispatchQueue.main.async {
                        completionHandler(nil)
                    }
                }
            })
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
                completionHandler(contentEditingInput?.fullSizeImageURL as URL?)
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
