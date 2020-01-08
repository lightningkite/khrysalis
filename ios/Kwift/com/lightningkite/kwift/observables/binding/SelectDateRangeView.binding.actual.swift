//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation


//--- SelectDateRangeView.bind(MutableObservableProperty<Date?>, MutableObservableProperty<Date?>)
public extension SelectDateRangeView {
    func bind(_ start: MutableObservableProperty<Date?>, _ endInclusive: MutableObservableProperty<Date?>) -> Void {
        self.start = start
        self.endInclusive = endInclusive
    }
    func bind(start: MutableObservableProperty<Date?>, endInclusive: MutableObservableProperty<Date?>) -> Void {
        return bind(start, endInclusive)
    }
}









