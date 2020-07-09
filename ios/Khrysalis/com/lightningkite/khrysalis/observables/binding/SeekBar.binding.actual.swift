//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- SeekBar.bind(Int, Int, MutableObservableProperty<Int>)
public extension UISlider {
    func bind(_ start: Int, _ endInclusive: Int, _ observable: MutableObservableProperty<Int>) -> Void {
        var suppress = false
        self.minimumValue = Float(start)
        self.maximumValue = Float(endInclusive)
        self.addAction(for: .valueChanged, action: { [weak self] in
            guard let self = self, !suppress else { return }
            suppress = true
            observable.value = Int(self.value.rounded())
            suppress = false
        })
        observable.subscribeBy { (value) in
            guard !suppress else { return }
            suppress = true
            self.setValue(Float(value), animated: false)
            suppress = false
        }.until(self.removed)
    }
    func bind(start: Int, endInclusive: Int, observable: MutableObservableProperty<Int>) -> Void {
        return bind(start, endInclusive, observable)
    }
}
