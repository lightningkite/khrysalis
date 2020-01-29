//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- SelectMultipleDatesView.bind(MutableObservableProperty<Set<Date>>)
public extension SelectMultipleDatesView {
    func bind(_ dates: MutableObservableProperty<Set<Date>>) -> Void {
        self.dates = dates
    }
    func bind(dates: MutableObservableProperty<Set<Date>>) -> Void {
        return bind(dates)
    }
}
