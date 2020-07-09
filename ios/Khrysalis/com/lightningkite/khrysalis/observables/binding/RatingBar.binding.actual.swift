//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- RatingBar.bind(Int, MutableObservableProperty<Int>)
public extension UIRatingBar {
    func bind(_ stars: Int, _ observable: MutableObservableProperty<Int>) -> Void {
        self.settings.totalStars = Int(stars)
        self.settings.fillMode = .full

        var suppress = false
        observable.subscribeBy { (value) in
            guard !suppress else { return }
            suppress = true
            self.rating = Double(value)
            suppress = false
        }.until(self.removed)
        self.didTouchCosmos = { rating in
            guard !suppress else { return }
            suppress = true
            observable.value = Int(rating)
            suppress = false
        }
    }
    func bind(stars: Int, observable: MutableObservableProperty<Int>) -> Void {
        return bind(stars, observable)
    }
}

//--- RatingBar.bind(Int, ObservableProperty<Int>)
public extension UIRatingBar {
    func bind(_ stars: Int, _ observable: ObservableProperty<Int>) -> Void {
        self.settings.totalStars = Int(stars)
        self.settings.fillMode = .full

        observable.subscribeBy { (value) in
            self.rating = Double(value)
        }.until(self.removed)
    }
    func bind(stars: Int, observable: ObservableProperty<Int>) -> Void {
        return bind(stars, observable)
    }
}

//--- RatingBar.bindFloat(Int, MutableObservableProperty<Float>)
public extension UIRatingBar {
    func bindFloat(_ stars: Int, _ observable: MutableObservableProperty<Float>) -> Void {
        self.settings.totalStars = Int(stars)
        self.settings.fillMode = .precise

        var suppress = false
        observable.subscribeBy { (value) in
            guard !suppress else { return }
            suppress = true
            self.rating = Double(value)
            suppress = false
        }.until(self.removed)
        self.didTouchCosmos = { rating in
            guard !suppress else { return }
            suppress = true
            observable.value = Float(rating)
            suppress = false
        }
    }
    func bindFloat(stars: Int, observable: MutableObservableProperty<Float>) -> Void {
        return bindFloat(stars, observable)
    }
}

//--- RatingBar.bindFloat(Int, ObservableProperty<Float>)
public extension UIRatingBar {
    func bindFloat(_ stars: Int, _ observable: ObservableProperty<Float>) -> Void {
        self.settings.totalStars = Int(stars)
        self.settings.fillMode = .precise

        observable.subscribeBy { (value) in
            self.rating = Double(value)
        }.until(self.removed)
    }
    func bindFloat(stars: Int, observable: ObservableProperty<Float>) -> Void {
        return bindFloat(stars, observable)
    }
}
