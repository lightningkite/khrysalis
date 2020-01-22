//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation


//--- SelectDayView.bind(MutableObservableProperty<Date?>)
public extension SelectDayView {
    func bind(_ day: MutableObservableProperty<Date?>) -> Void {
        self.selected = day
    }
    func bind(day: MutableObservableProperty<Date?>) -> Void {
        return bind(day)
    }
}
