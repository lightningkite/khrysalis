// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: observables/ObservableProperty.shared.kt
// Package: com.lightningkite.khrysalis.observables
import Foundation

public class ObservableProperty<T> {
    protected init() {
    }
    
    public let value: T
    public let onChange: Observable<T>
}


