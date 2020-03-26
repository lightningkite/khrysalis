//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- DateButton.bind(MutableObservableProperty<Date>)
public extension DateButton {
    func bind(_ observable: MutableObservableProperty<Date>) -> Void {
        self.date = observable.value
        observable.subscribeBy { ( value) in
            if this.date != value {
                this.date = value
            }
        }.until(self.removed)
        self.onDateEntered.subscribeBy { value in
            if observable.value != value {
                observable.value = value
            }
        }.until(self.removed)
    }
    func bind(date: MutableObservableProperty<Date>) -> Void {
        return bind(date)
    }
}

//--- TimeButton.bind(MutableObservableProperty<Date>, Int)
public extension TimeButton {
    func bind(_ observable: MutableObservableProperty<Date>, _ minuteInterval: Int32 = 1) -> Void {
        self.minuteInterval = Int(minuteInterval)
        self.date = observable.value
        observable.subscribeBy { ( value) in
            if this.date != value {
                this.date = value
            }
        }.until(self.removed)
        self.onDateEntered.subscribeBy { value in
            if observable.value != value {
                observable.value = value
            }
        }.until(self.removed)
    }
    func bind(date: MutableObservableProperty<Date>, minuteInterval: Int32 = 1) -> Void {
        return bind(date, minuteInterval)
    }
}

//--- DateButton.bindDateAlone(MutableObservableProperty<DateAlone>)
public extension DateButton {
    func bindDateAlone(_ observable: MutableObservableProperty<DateAlone>) -> Void {
        observable.subscribeBy { ( value) in
            if this.date.dateAlone != value {
                this.date = dateFrom(value, Date().timeAlone)
            }
        }.until(self.removed)
        self.onDateEntered.subscribeBy { value in
            let newValue = self.date.dateAlone
            if observable.value != newValue {
                observable.value = newValue
            }
        }.until(self.removed)
    }
    func bindDateAlone(date: MutableObservableProperty<DateAlone>) -> Void {
        return bindDateAlone(date)
    }
}

//--- TimeButton.bindTimeAlone(MutableObservableProperty<TimeAlone>, Int)
public extension TimeButton {
    func bindTimeAlone(_ observable: MutableObservableProperty<TimeAlone>, _ minuteInterval: Int32 = 1) -> Void {
        self.minuteInterval = Int(minuteInterval)
        observable.subscribeBy { ( value) in
            if this.date.timeAlone != value {
                this.date = dateFrom(Date().dateAlone, value)
            }
        }.until(self.removed)
        self.onDateEntered.subscribeBy { value in
            let newValue = self.date.timeAlone
            if observable.value != newValue {
                observable.value = newValue
            }
        }.until(self.removed)
    }
    func bindTimeAlone(date: MutableObservableProperty<TimeAlone>, minuteInterval: Int32 = 1) -> Void {
        return bindTimeAlone(date, minuteInterval)
    }
}
