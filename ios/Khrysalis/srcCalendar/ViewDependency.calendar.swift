//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import MapKit
import EventKitUI


//--- ViewDependency
public extension ViewDependency {

    //--- ViewDependency.openEvent(String, String, String, Date, Date)
    public func openEvent(_ title: String, _ description: String, _ location: String, _ start: Date, _ end: Date) -> Void {
        let store = EKEventStore()
        store.requestAccess(to: .event) { (hasPermission, error) in
            if hasPermission {
                DispatchQueue.main.async {
                    let addController = EKEventEditViewController()
                    addController.eventStore = store
                    addController.editViewDelegate = self
                    let event = EKEvent(eventStore: store)
                    event.title = title
                    event.notes = description
                    event.location = location
                    event.startDate = start
                    event.endDate = end
                    addController.event = event
                    self.parentViewController.present(addController, animated: true, completion: nil)
                }
            }
        }
    }
    public func openEvent(title: String, description: String, location: String, start: Date, end: Date) -> Void {
        return openEvent(title, description, location, start, end)
    }
}
extension ViewDependency: EKEventEditViewDelegate {
    public func eventEditViewController(_ controller: EKEventEditViewController, didCompleteWith action: EKEventEditViewAction) {
        self.parentViewController.dismiss(animated: true) {[weak self] in
            
        }
    }
}
