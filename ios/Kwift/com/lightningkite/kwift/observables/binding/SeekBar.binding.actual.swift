//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit


//--- SeekBar.bind(Int, Int, MutableObservableProperty<Int>)
public extension UISlider {
    func bind(_ start: Int32, _ endInclusive: Int32, _ observable: MutableObservableProperty<Int32>) -> Void {
        var suppress = false
        self.minimumValue = Float(start)
        self.maximumValue = Float(endInclusive)
        self.addAction(for: .valueChanged, action: { [weak self] in
            guard let self = self, !suppress else { return }
            suppress = true
            observable.value = Int32(self.value.rounded())
            suppress = false
        })
        observable.addAndRunWeak(self) { (self, value) in
            guard !suppress else { return }
            suppress = true
            self.value = Float(self.value)
            suppress = false
        }
    }
    func bind(start: Int32, endInclusive: Int32, observable: MutableObservableProperty<Int32>) -> Void {
        return bind(start, endInclusive, observable)
    }
}






