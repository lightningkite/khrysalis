//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation
import UIKit


//--- PageIndicatorView.bind(Int, MutableObservableProperty<Int>)
public extension UIPageControl {
    func bind(_ count: Int, _ selected: MutableObservableProperty<Int>) -> Void {
        self.numberOfPages = Int(count)
        var suppress = false
        selected.subscribeBy { value in
            guard !suppress else { return }
            suppress = true
            self.currentPage = Int(value)
            suppress = false
        }.until(self.removed)
        self.addAction(for: .valueChanged, action: {
            guard !suppress else { return }
            suppress = true
            selected.value = Int(self.currentPage)
            suppress = false
        })
    }
    func bind(count: Int, selected: MutableObservableProperty<Int>) -> Void {
        return bind(count, selected)
    }
}

//--- PageIndicatorView.bind(ObservableProperty<Int>, MutableObservableProperty<Int>)
public extension UIPageControl {
    func bind(_ count: ObservableProperty<Int>, _ selected: MutableObservableProperty<Int>) -> Void {
        count.subscribeBy { count in
            self.numberOfPages = Int(count)
        }
        var suppress = false
        selected.subscribeBy { value in
            guard !suppress else { return }
            suppress = true
            self.currentPage = Int(value)
            suppress = false
        }.until(self.removed)
        self.addAction(for: .valueChanged, action: {
            guard !suppress else { return }
            suppress = true
            selected.value = Int(self.currentPage)
            suppress = false
        })
    }
    func bind(count: ObservableProperty<Int>, selected: MutableObservableProperty<Int>) -> Void {
        return bind(count, selected)
    }
}
