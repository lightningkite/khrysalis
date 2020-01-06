//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import Alamofire
import AlamofireImage
import Photos
import AVKit


//--- ViewDependency
public class ViewDependency {
    public unowned let parentViewController: UIViewController
    public init(_ parentViewController: UIViewController){
        self.parentViewController = parentViewController
    }
    //--- ViewDependency.getString(StringResource)
    public func getString(_ reference: StringResource) -> String {
        return reference
    }
    //--- ViewDependency.getColor(ColorResource)
    public func getColor(_ reference: ColorResource) -> UIColor {
        return reference
    }
    //--- ViewDependency.displayMetrics
    public var displayMetrics: DisplayMetrics {
        return DisplayMetrics(
            density: Float(UIScreen.main.scale),
            scaledDensity: Float(UIScreen.main.scale),
            widthPixels: Int32(UIScreen.main.bounds.width * UIScreen.main.scale),
            heightPixels: Int32(UIScreen.main.bounds.height * UIScreen.main.scale)
        )
    }
    
    //--- ViewDependency.downloadDrawable(String, Int? , Int? , (Drawable?)->Unit)
    public func downloadDrawable(
        url: String,
        width: Int? = nil,
        height: Int? = nil,
        onResult: @escaping (Drawable?)->Void
    ) {
        downloadDrawable(url, width, height, onResult)
    }
    public func downloadDrawable(
        _ url: String,
        _ width: Int? = nil,
        _ height: Int? = nil,
        _ onResult: @escaping (Drawable?)->Void
    ) {
        Alamofire.request(url).responseImage(imageScale: 1) { response in
            if var image = response.value {
//                if let width = width, let height = height {
//                    image = image.af_imageAspectScaled(toFit: CGSize(width: width, height: height))
//                }
                onResult({ _ in CAImageLayer(image) })
            } else {
                onResult(nil)
            }
        }
    }
    
    //--- ViewDependency.checkedDrawable(Drawable, Drawable)
    public func checkedDrawable(
        checked: @escaping Drawable,
        normal: @escaping Drawable
    ) -> Drawable {
        return checkedDrawable(checked, normal)
    }
    public func checkedDrawable(
        _ checked: @escaping Drawable,
        _ normal: @escaping Drawable
    ) -> Drawable {
        return { view in
            let layer = CALayer()
            
            let checkedLayer = checked(view)
            let normalLayer = normal(view)
            
            layer.addOnStateChange(view) { [unowned layer] state in
                layer.sublayers?.forEach { $0.removeFromSuperlayer() }
                if state.contains(.selected) {
                    layer.addSublayer(checkedLayer)
                } else {
                    layer.addSublayer(normalLayer)
                }
            }
            layer.onResize.addAndRunWeak(checkedLayer, layer.bounds) { (checkedLayer, bounds) in
                checkedLayer.frame = bounds
            }
            layer.onResize.addAndRunWeak(normalLayer, layer.bounds) { (normalLayer, bounds) in
                normalLayer.frame = bounds
            }
            
            return layer
        }
    }
    
    //--- ViewDependency.setSizeDrawable(Drawable, Int, Int)
    public func setSizeDrawable(drawable: @escaping Drawable, width: Int, height: Int) -> Drawable {
        return setSizeDrawable(drawable, width, height)
    }
    public func setSizeDrawable(_ drawable: @escaping Drawable, _ width: Int, _ height: Int) -> Drawable {
        return { view in
            let existing = drawable(view)
            existing.resize(CGRect(x: 0, y: 0, width: width, height: height))
            return existing
        }
    }
    
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

    //--- ViewDependency.requestImageGallery((Uri)->Unit)
    public func requestImageGallery(onResult: @escaping (Uri) -> Void) {
        if PHPhotoLibrary.authorizationStatus() == .authorized {
            self.requestImageGalleryRaw(onResult: onResult)
        } else {
            PHPhotoLibrary.requestAuthorization {_ in
                self.requestImageGalleryRaw(onResult: onResult)
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
    
    //--- ViewDependency.requestImageCamera((Uri)->Unit)
    public func requestImageCamera(onResult: @escaping (Uri) -> Void) {
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
    private func requestImageCameraRaw(onResult: @escaping (Uri) -> Void) {
        if UIImagePickerController.isSourceTypeAvailable(.camera){
            let imageDelegate = self.imageDelegate
            imageDelegate.onImagePicked = onResult
            imageDelegate.prepareCamera()
            self.parentViewController.present(imageDelegate.imagePicker, animated: true, completion: nil)
        }
    }
}

//--- Image helpers

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





