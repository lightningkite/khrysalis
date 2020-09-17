//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import Alamofire
import AlamofireImage
import MapKit


//--- ViewDependency
public typealias ActivityAccess = ViewDependency
public class ViewDependency: NSObject {
    public unowned let parentViewController: UIViewController
    public init(_ parentViewController: UIViewController){
        self.parentViewController = parentViewController
    }
    //--- ViewDependency.getString(StringResource)
    public func getString(_ resource: StringResource) -> String {
        return resource
    }
    public func getString(resource: StringResource) -> String {
        return resource
    }
    //--- ViewDependency.getColor(ColorResource)
    public func getColor(_ resource: ColorResource) -> UIColor {
        return resource
    }
    public func getColor(resource: ColorResource) -> UIColor {
        return resource
    }
    //--- ViewDependency.displayMetrics
    public var displayMetrics: DisplayMetrics {
        return DisplayMetrics(
            density: 1,
            scaledDensity: 1,
            widthPixels: Int(UIScreen.main.bounds.width * UIScreen.main.scale),
            heightPixels: Int(UIScreen.main.bounds.height * UIScreen.main.scale)
        )
    }

    //--- ViewDependency.share(String, String? , String? , Image? )
    public func share(_ shareTitle: String, _ message: String? = nil, _ url: String? = nil, _ image: Image? = nil) -> Void {
        var items: Array<Any> = []
        if let message = message {
            items.append(message)
        }
        if let url = url, let fixed = URL(string: url) {
            items.append(fixed)
        }
        if let image = image {
            TODO()
        }
        let vc = UIActivityViewController(activityItems: items, applicationActivities: nil)
        self.parentViewController.present(vc, animated: true, completion: nil)
    }
    public func share(shareTitle: String, message: String? = nil, url: String? = nil, image: Image? = nil) -> Void {
        return share(shareTitle, message, url, image)
    }

    //--- ViewDependency.openUrl(String)
    public func openUrl(_ url: String) -> Bool {
        if let url = URL(string: url) {
            if UIApplication.shared.canOpenURL(url) {
                UIApplication.shared.open(url)
                return true
            } else {
                return false
            }
        }
        return false
    }
    public func openUrl(url: String) -> Bool {
        return openUrl(url)
    }

    //--- ViewDependency.openAndroidAppOrStore(String)
    public func openAndroidAppOrStore(_ packageName: String) {
        openUrl("market://details?id=\(packageName)")
    }
    public func openAndroidAppOrStore(packageName: String) {
        return openAndroidAppOrStore(packageName)
    }
    
    //--- ViewDependency.openIosStore(String)
    public func openIosStore(_ numberId: String) {
        openUrl("https://apps.apple.com/us/app/taxbot/id\(numberId)")
    }
    public func openIosStore(numberId: String) {
        return openIosStore(numberId)
    }

    //--- ViewDependency.openMap(GeoCoordinate, String? , Float? )
    public func openMap(_ coordinate: GeoCoordinate, _ label: String? = nil, _ zoom: Float? = nil) -> Void {
        var options: Array<(String, ()->Void)> = [
            ("Apple Maps", {
                let mapItem = MKMapItem(placemark: MKPlacemark(coordinate: coordinate.toIos(), addressDictionary: nil))
                mapItem.name = label
                mapItem.openInMaps()
            })
        ]
        if UIApplication.shared.canOpenURL(URL(string: "comgooglemaps://")!) {
            options.append(("Google Maps", {
                var url = "string: comgooglemaps://?center=\(coordinate.latitude),\(coordinate.longitude)"
                if let zoom = zoom {
                    url += "&zoom=\(zoom)"
                }
                if let label = label {
                    url += "&q=\(label)"
                }
                UIApplication.shared.open(URL(string: url)!)
            }))
        }
        //TODO: Could add more options
        if options.count == 1 {
            options[0].1()
        } else {
            let optionsView = UIAlertController(title: "Open in Maps", message: nil, preferredStyle: .actionSheet)
            for option in options {
                optionsView.addAction(UIAlertAction(title: option.0, style: .default, handler: { (action) in
                    optionsView.dismiss(animated: true, completion: nil)
                    option.1()
                }))
            }
            self.parentViewController.present(optionsView, animated: true, completion: nil)
        }
    }
    public func openMap(coordinate: GeoCoordinate, label: String? = nil, zoom: Float? = nil) -> Void {
        return openMap(coordinate, label, zoom)
    }

    //--- ViewDependency.downloadDrawable(String, Int? , Int? , (Drawable?)->Unit)
    public func downloadDrawable(
        url: String,
        width: Int? = nil,
        height: Int? = nil,
        callback: @escaping (Drawable?)->Void
    ) {
        downloadDrawable(url, width, height, callback)
    }
    public func downloadDrawable(
        _ url: String,
        _ width: Int? = nil,
        _ height: Int? = nil,
        _ callback: @escaping (Drawable?)->Void
    ) {
        HttpClient.call(url: url)
            .readData()
            .subscribeBy({ (e) in
                callback(nil)
            }, { (image) in
                callback(Drawable { _ in CAImageLayer(UIImage(data: image)) })
            })
            .forever()
    }

    //--- ViewDependency.checkedDrawable(Drawable, Drawable)
    public func checkedDrawable(
        checked: Drawable,
        normal: Drawable
    ) -> Drawable {
        return checkedDrawable(checked, normal)
    }
    public func checkedDrawable(
        _ checked: Drawable,
        _ normal: Drawable
    ) -> Drawable {
        return Drawable { view in
            let layer = CALayer()

            let checkedLayer = checked.makeLayer(view)
            let normalLayer = normal.makeLayer(view)

            layer.addOnStateChange(view) { [unowned layer] state in
                layer.sublayers?.forEach { $0.removeFromSuperlayer() }
                if state.contains(.selected) {
                    layer.addSublayer(checkedLayer)
                } else {
                    layer.addSublayer(normalLayer)
                }
            }
            layer.onResize.startWith(layer.bounds).addWeak(referenceA: checkedLayer) { (checkedLayer, bounds) in
                checkedLayer.frame = bounds
            }
            layer.onResize.startWith(layer.bounds).addWeak(referenceA: normalLayer) { (normalLayer, bounds) in
                normalLayer.frame = bounds
            }

            return layer
        }
    }

    //--- ViewDependency.setSizeDrawable(Drawable, Int, Int)
    public func setSizeDrawable(drawable: Drawable, width: Int, height: Int) -> Drawable {
        return setSizeDrawable(drawable, width, height)
    }
    public func setSizeDrawable(_ drawable: Drawable, _ width: Int, _ height: Int) -> Drawable {
        return Drawable { view in
            let existing = drawable.makeLayer(view)
            existing.resize(CGRect(x: 0, y: 0, width: width, height: height))
            return existing
        }
    }

}
