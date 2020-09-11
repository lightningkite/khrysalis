//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- TabLayout.bind(List<String>, MutableObservableProperty<Int>)
public extension UISegmentedControl {
    func bind(_ tabs: Array<String>, _ selected: MutableObservableProperty<Int>, _ allowReselect:Bool = false) -> Void {
        self.removeAllSegments()
        for entry in tabs {
            self.insertSegment(withTitle: entry, at: self.numberOfSegments, animated: false)
        }
        if allowReselect{
            if let self = self as? UISegmentedControlSquare {
                self.reselectable = true
            }
            self.addAction(for: .valueChanged, action: { [weak self] in
                selected.value = Int(self?.selectedSegmentIndex ?? 0)
            })
        }else{
            self.addAction(for: .valueChanged, action: { [weak self] in
                selected.value = Int(self?.selectedSegmentIndex ?? 0)
            })
        }
        selected.subscribeBy { value in
            self.selectedSegmentIndex = Int(value)
        }.until(self.removed)
    }
    func bind(tabs: Array<String>, selected: MutableObservableProperty<Int>, allowReselect:Bool = false) -> Void {
        return bind(tabs, selected, allowReselect)
    }
    func bind<T: Equatable>(_ tabs: Array<T>, _ selected: MutableObservableProperty<T>, _ allowReselect:Bool = false, _ toString: @escaping (T)->String) -> Void {
        self.removeAllSegments()
        for entry in tabs {
            self.insertSegment(withTitle: toString(entry), at: self.numberOfSegments, animated: false)
        }
        if allowReselect {
            if let self = self as? UISegmentedControlSquare {
                self.reselectable = true
            }
            self.addAction(for: .valueChanged, action: { [weak self] in
                if let i = self?.selectedSegmentIndex, i >= 0, i < tabs.count {
                    selected.value = tabs[i]
                }
            })
        }else{
            self.addAction(for: .valueChanged, action: { [weak self] in
                selected.value = tabs[self?.selectedSegmentIndex ?? 0]
            })
        }
        selected.subscribeBy { value in
            self.selectedSegmentIndex = tabs.firstIndex(of: value) ?? 0
        }.until(self.removed)
    }
    func bind<T: Equatable>(tabs: Array<T>, selected: MutableObservableProperty<T>, allowReselect:Bool = false, toString: @escaping (T)->String) -> Void {
        return bind(tabs, selected, allowReselect, toString)
    }
    
    func bind<T:Equatable>(options:ObservableProperty<Array<T>>, selected: MutableObservableProperty<T>, allowReselect:Bool = false, toString: @escaping (T)->String) -> Void{
        options.subscribeBy(
            onNext:{tabs in
                self.removeAllSegments()
                for entry in tabs {
                    self.insertSegment(withTitle: toString(entry), at: self.numberOfSegments, animated: false)
                }
        }).until(self.removed)
        if allowReselect {
            if let self = self as? UISegmentedControlSquare {
                self.reselectable = true
            }
            self.addAction(for: .valueChanged, action: { [weak self] in
                if let i = self?.selectedSegmentIndex, i >= 0, i < options.value.count {
                    selected.value = options.value[i]
                }
            })
        } else{
            self.addAction(for: .valueChanged, action: { [weak self] in
                if let i = self?.selectedSegmentIndex, i >= 0, i < options.value.count {
                    selected.value = options.value[i]
                }
            })
        }
        selected.subscribeBy { value in
            self.selectedSegmentIndex = options.value.firstIndex(of: value) ?? 0
        }.until(self.removed)
    }
    
    func bind<T:Equatable>(_ options:ObservableProperty<Array<T>>, _ selected: MutableObservableProperty<T>, _ allowReselect:Bool = false, _ toString: @escaping (T)->String) -> Void{
        bind(options:options, selected:selected, allowReselect: allowReselect, toString: toString)
    }
}
