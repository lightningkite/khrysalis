//
//  ToggleButton.binding.actual.swift
//  Pods
//
//  Created by Brady Svedin on 4/7/20.
//

import Foundation

public extension ToggleButton{

/**
 *
 * Binds the textOn value in the toggle button to the observable provided
 *
 * Example
 * val text = StandardObservableProperty("Test Text")
 * view.bindOnString(text)
 *
 */
    
func bindOnString(observable: ObservableProperty<String>) {
    observable.subscribeBy{ (value) in
        self.textOn = value
        self.syncText()
    }.until(self.removed)
}
func bindOnString(_ observable: ObservableProperty<String>) {
    observable.subscribeBy{ (value) in
        self.textOn = value
        self.syncText()
    }.until(self.removed)
}

/**
 *
 * Binds the textOff value in the toggle button to the observable provided
 *
 * Example
 * val text = StandardObservableProperty("Test Text")
 * view.bindOffString(text)
 *
 */

func bindOffString(observable: ObservableProperty<String>) {
    observable.subscribeBy{ (value) in
        self.textOff = value
        self.syncText()
    }.until(self.removed)
}
func bindOffString( _ observable: ObservableProperty<String>) {
    observable.subscribeBy{ (value) in
        self.textOff = value
        self.syncText()
    }.until(self.removed)
}

/**
 *
 * Binds both the textOff and textOn values in the toggle button to the observable provided
 *
 * Example
 * val text = StandardObservableProperty("Test Text")
 * view.bindOnOffString(text)
 *
 */

func bindOnOffString(observable: ObservableProperty<String>) {
    observable.subscribeBy{ (value) in
        self.textOn = value
        self.textOff = value
        self.syncText()
    }.until(self.removed)
}

    func bindOnOffString(_ observable: ObservableProperty<String>) {
        observable.subscribeBy{ (value) in
            self.textOn = value
            self.textOff = value
            self.syncText()
        }.until(self.removed)
    }

}
