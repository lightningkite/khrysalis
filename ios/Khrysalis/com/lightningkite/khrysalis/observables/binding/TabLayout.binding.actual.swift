//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- TabLayout.bind(List<String>, MutableObservableProperty<Int>)
public extension UISegmentedControl {
    func bind(_ tabs: Array<String>, _ selected: MutableObservableProperty<Int32>) -> Void {
        for entry in tabs {
            self.insertSegment(withTitle: entry, at: self.numberOfSegments, animated: false)
        }
        self.addAction(for: .valueChanged, action: { [weak self, weak selected] in
            selected?.value = Int32(self?.selectedSegmentIndex ?? 0)
        })
        selected.addAndRunWeak(self) { this, value in
            this.selectedSegmentIndex = Int(value)
        }
    }
    func bind(tabs: Array<String>, selected: MutableObservableProperty<Int32>) -> Void {
        return bind(tabs, selected)
    }
}
