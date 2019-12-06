//
//  UISwitch+binding.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 8/21/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation
import UIKit

public extension CompoundButton {
    func bind(_ observable: MutableObservableProperty<Bool>){
        return bind(observable: observable)
    }
    func bind(observable: MutableObservableProperty<Bool>){
        observable.addAndRunWeak(referenceA: self) { (this, value) in
            if this.isOn != value {
                this.isOn = value
            }
        }
        self.onCheckChanged = { [weak self] value in
            if observable.value != self?.isOn {
                observable.value = self?.isOn ?? false
            }
        }
    }
    
    func bindSelect<T: Equatable>(_ myValue: T, _ selected: MutableObservableProperty<T>){
        return bindSelect(myValue: myValue, selected: selected)
    }
    func bindSelect<T: Equatable>(myValue: T, selected: MutableObservableProperty<T>){
        selected.addAndRunWeak(referenceA: self) { (this, value) in
            let shouldBeOn = value == myValue
            if this.isOn != shouldBeOn {
                this.isOn = shouldBeOn
            }
        }
        self.onCheckChanged = { [weak self] value in
            if value && selected.value != myValue {
                selected.value = myValue
            } else if !value && selected.value == myValue  {
                self?.isOn = true
            }
        }
    }
    
    
    func bindSelectNullable<T: Equatable>(_ myValue: T, _ selected: MutableObservableProperty<T?>){
        return bindSelectNullable(myValue: myValue, selected: selected)
    }
    func bindSelectNullable<T: Equatable>(myValue: T, selected: MutableObservableProperty<T?>){
        selected.addAndRunWeak(referenceA: self) { (this, value) in
            let shouldBeOn = value == myValue
            if this.isOn != shouldBeOn {
                this.isOn = shouldBeOn
            }
        }
        self.onCheckChanged = { [weak self] value in
            if value && selected.value != myValue {
                selected.value = myValue
            } else if !value && selected.value == myValue  {
                selected.value = nil
            }
        }
    }
    
    func bindSelectInvert<T: Equatable>(_ myValue: T, _ selected: MutableObservableProperty<T?>){
        return bindSelectInvert(myValue: myValue, selected: selected)
    }
    func bindSelectInvert<T: Equatable>(myValue: T, selected: MutableObservableProperty<T?>){
        var suppress:Bool = false
        selected.addAndRunWeak(referenceA: self) { (this, value) in
            if !suppress{	
                suppress = true
                let shouldBeOn = value == myValue || value == nil
                if this.isOn != shouldBeOn {
                    this.isOn = shouldBeOn
                }
                suppress = false
            }
        }
        self.onCheckChanged = { [weak self] value in
            if !suppress{
                suppress = true
                if !value && selected.value == myValue {
                    selected.value = nil
                    self?.isOn = true
                } else if selected.value != myValue  {
                    selected.value = myValue
                    self?.isOn = true
                }
                suppress = false
            }
        }
    }
}

