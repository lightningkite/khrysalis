//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- TabLayout.bind(List<String>, MutableObservableProperty<Int>)
public extension UISegmentedControl {
    func bind(_ tabs: Array<String>, _ selected: MutableObservableProperty<Int>) -> Void {
        self.removeAllSegments()
        for entry in tabs {
            self.insertSegment(withTitle: entry, at: self.numberOfSegments, animated: false)
        }
        self.addAction(for: .valueChanged, action: { [weak self, weak selected] in
            selected?.value = Int(self?.selectedSegmentIndex ?? 0)
        })
        selected.subscribeBy { value in
            self.selectedSegmentIndex = Int(value)
        }.until(self.removed)
    }
    func bind(tabs: Array<String>, selected: MutableObservableProperty<Int>) -> Void {
        return bind(tabs, selected)
    }
    func bind<T: Equatable>(_ tabs: Array<T>, _ selected: MutableObservableProperty<T>, _ toString: @escaping (T)->String) -> Void {
        self.removeAllSegments()
        for entry in tabs {
            self.insertSegment(withTitle: toString(entry), at: self.numberOfSegments, animated: false)
        }
        self.addAction(for: .valueChanged, action: { [weak self, weak selected] in
            selected?.value = tabs[self?.selectedSegmentIndex ?? 0]
        })
        selected.subscribeBy { value in
            self.selectedSegmentIndex = tabs.firstIndex(of: value) ?? 0
        }.until(self.removed)
    }
    func bind<T: Equatable>(tabs: Array<T>, selected: MutableObservableProperty<T>, toString: @escaping (T)->String) -> Void {
        return bind(tabs, selected, toString)
    }
}
