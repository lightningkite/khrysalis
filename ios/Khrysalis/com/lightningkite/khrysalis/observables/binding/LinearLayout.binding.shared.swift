//Package: com.lightningkite.khrysalis.observables.binding
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



private class LinearLayoutBoundSubview<T>: Equatable, Hashable {
    
    public var view: View
    public var property: StandardObservableProperty<T>
    
    public static func == (lhs: LinearLayoutBoundSubview, rhs: LinearLayoutBoundSubview) -> Bool {
        return lhs.view == rhs.view &&
            lhs.property == rhs.property
    }
    public func hash(into hasher: inout Hasher) {
        hasher.combine(view)
        hasher.combine(property)
    }
    public func copy(
        view: (View)? = nil,
        property: (StandardObservableProperty<T>)? = nil
    ) -> LinearLayoutBoundSubview {
        return LinearLayoutBoundSubview(
            view: view ?? self.view,
            property: property ?? self.property
        )
    }
    
    
    public init(view: View, property: StandardObservableProperty<T>) {
        self.view = view
        self.property = property
    }
    convenience public init(_ view: View, _ property: StandardObservableProperty<T>) {
        self.init(view: view, property: property)
    }
}
 
 

extension LinearLayout {
    public func bind<T>(data: ObservableProperty<Array<T>>, defaultValue: T, makeView: (ObservableProperty<T>) -> View) -> Void {
        var existingViews = Array<LinearLayoutBoundSubview<T>>()
        data.addAndRunWeak(self) { (self, value) in 
            var excessViews = existingViews.size - value.size
            if excessViews > 0 {
                
                for iter in 1 ... excessViews {
                    var old = existingViews.removeAt(existingViews.lastIndex)
                    self.removeView(old.view)
                }
            } else if existingViews.size < value.size {
                
                for iter in 1 ... ( -excessViews ) {
                    var prop = StandardObservableProperty(defaultValue)
                    var view = makeView(prop)
                    self.addView(view, self.params(gravity: AlignPair.centerFill))
                    existingViews.add(LinearLayoutBoundSubview(view, prop))
                }
            }
            
            for index in 0 ... value.size - 1 {
                existingViews[ index ].property.value = value[ index ]
            }
        }
    }
    public func bind<T>(_ data: ObservableProperty<Array<T>>, _ defaultValue: T, _ makeView: (ObservableProperty<T>) -> View) -> Void {
        return bind(data: data, defaultValue: defaultValue, makeView: makeView)
    }
}
 
 
