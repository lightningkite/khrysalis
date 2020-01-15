//Stub file made with Kwift 2 (by Lightning Kite)
import Foundation
import UIKit


//--- PageIndicatorView.bind(Int, MutableObservableProperty<Int>)
public extension UIPageControl {
    func bind(_ count: Int32, _ selected: MutableObservableProperty<Int32>) -> Void {
        self.numberOfPages = Int(count)
        var suppress = false
        selected.addAndRunWeak(self) { this, value in
            guard !suppress else { return }
            suppress = true
            this.currentPage = Int(value)
            suppress = false
        }
        self.addAction(for: .valueChanged, action: {
            guard !suppress else { return }
            suppress = true
            selected.value = Int32(self.currentPage)
            suppress = false
        })
    }
    func bind(count: Int32, selected: MutableObservableProperty<Int32>) -> Void {
        return bind(count, selected)
    }
}












