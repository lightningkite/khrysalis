//Package: com.lightningkite.khrysalis.observables.binding
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



private class LinearLayoutBoundSubview<T> {
    
    public var view: View
    public var property: StandardObservableProperty<T>
    
    
    public init(view: View, property: StandardObservableProperty<T>) {
        self.view = view
        self.property = property
    }
    convenience public init(_ view: View, _ property: StandardObservableProperty<T>) {
        self.init(view: view, property: property)
    }
}
 
 

extension LinearLayout {
    public func bind<T>(data: ObservableProperty<Array<T>>, defaultValue: T, makeView: @escaping (ObservableProperty<T>) -> View) -> Void {
        var existingViews: Array<LinearLayoutBoundSubview<T>> = Array()
        data.subscribeBy{ (value) in 
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
            
            for index in 0..<value.size {
                existingViews[ index ].property.value = value[ index ]
            }
        }.until(self.removed)
    }
    public func bind<T>(_ data: ObservableProperty<Array<T>>, _ defaultValue: T, _ makeView: @escaping (ObservableProperty<T>) -> View) -> Void {
        return bind(data: data, defaultValue: defaultValue, makeView: makeView)
    }
}
 
 
