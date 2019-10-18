//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation


public var lastDialog = StandardObservableProperty<DialogRequest?>(nil)
public var showDialogEvent = StandardEvent<DialogRequest>()

public class DialogRequest {
    
    public var string: ViewString
    public var confirmation: (() -> Void)? 
    
    
    public init(string: ViewString, confirmation: (() -> Void)?  = nil) {
        self.string = string
        self.confirmation = confirmation
    }
    convenience public init(_ string: ViewString, _ confirmation: (() -> Void)?  = nil) {
        self.init(string: string, confirmation: confirmation)
    }
}
 
 

public func showDialog(request: DialogRequest) -> Void {
    lastDialog.value = request
    showDialogEvent.invokeAll(request)
}
public func showDialog(_ request: DialogRequest) -> Void {
    return showDialog(request: request)
}
 
 

public func showDialog(message: ViewString) -> Void {
    showDialog(DialogRequest(string: message))
}
public func showDialog(_ message: ViewString) -> Void {
    return showDialog(message: message)
}
 
