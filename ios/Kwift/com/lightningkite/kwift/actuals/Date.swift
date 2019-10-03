//
//  Date.swift
//  KwiftTemplate
//
//  Created by Joseph Ivie on 9/10/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation

extension Date {
    var time: Int64 {
        return Int64((self.timeIntervalSince1970 * 1000.0).rounded())
    }
    
    init(_ milliseconds:Int64) {
        self = Date(timeIntervalSince1970: TimeInterval(milliseconds) / 1000)
    }
    
    func before(_ other: Date) -> Bool {
        return self.time < other.time
    }
    func after(_ other: Date) -> Bool {
        return self.time > other.time
    }
    var dayOfWeek: Int32 { return Int32(Calendar.current.component(.weekday, from: self)) }
    var dayOfMonth: Int32 { return Int32(Calendar.current.component(.day, from: self)) }
    var monthOfYear: Int32 { return Int32(Calendar.current.component(.month, from: self)) }
    var yearAd: Int32 { return Int32(Calendar.current.component(.year, from: self)) }
    var hourOfDay: Int32 { return Int32(Calendar.current.component(.hour, from: self)) }
    var minuteOfHour: Int32 { return Int32(Calendar.current.component(.minute, from: self)) }
    var secondOfMinute: Int32 { return Int32(Calendar.current.component(.second, from: self)) }
    
    func sameDay(_ other: Date) -> Bool {
        return self.yearAd == other.yearAd && self.monthOfYear == other.monthOfYear && self.dayOfMonth == other.dayOfMonth
    }
    func sameMonth(_ other: Date) -> Bool {
        return self.yearAd == other.yearAd && self.monthOfYear == other.monthOfYear
    }
    func sameYear(_ other: Date) -> Bool {
        return self.yearAd == other.yearAd
    }
    
    func dayOfWeek(_ value: Int32) -> Date {
        var components = Calendar.current.dateComponents([.year, .weekOfYear, .hour, .minute, .second, .nanosecond], from: self)
        components.weekday = Int(value)
        return Calendar.current.date(from: components)!
    }
    func dayOfMonth(_ value: Int32) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .hour, .minute, .second, .nanosecond], from: self)
        components.day = Int(value)
        return Calendar.current.date(from: components)!
    }
    func monthOfYear(_ value: Int32) -> Date {
        var components = Calendar.current.dateComponents([.year, .day, .hour, .minute, .second, .nanosecond], from: self)
        components.month = Int(value)
        return Calendar.current.date(from: components)!
    }
    func yearAd(_ value: Int32) -> Date {
        var components = Calendar.current.dateComponents([.month, .day, .hour, .minute, .second, .nanosecond], from: self)
        components.year = Int(value)
        return Calendar.current.date(from: components)!
    }
    func hourOfDay(_ value: Int32) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .day, .minute, .second, .nanosecond], from: self)
        components.hour = Int(value)
        return Calendar.current.date(from: components)!
    }
    func minuteOfHour(_ value: Int32) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .day, .hour, .second, .nanosecond], from: self)
        components.minute = Int(value)
        return Calendar.current.date(from: components)!
    }
    func secondOfMinute(_ value: Int32) -> Date {
        var components = Calendar.current.dateComponents([.year, .month, .day, .hour, .minute, .nanosecond], from: self)
        components.second = Int(value)
        return Calendar.current.date(from: components)!
    }
}

extension Int32 {
    func milliseconds() -> TimeInterval { return TimeInterval(self) / 1000 }
    func seconds() -> TimeInterval { return TimeInterval(self) }
    func minutes() -> TimeInterval { return TimeInterval(self * 60) }
    func hours() -> TimeInterval { return TimeInterval(self * 60 * 60) }
    func days() -> TimeInterval { return TimeInterval(self * 60 * 60 * 24) }
}

extension Int {
    func milliseconds() -> TimeInterval { return TimeInterval(self) / 1000 }
    func seconds() -> TimeInterval { return TimeInterval(self) }
    func minutes() -> TimeInterval { return TimeInterval(self * 60) }
    func hours() -> TimeInterval { return TimeInterval(self * 60 * 60) }
    func days() -> TimeInterval { return TimeInterval(self * 60 * 60 * 24) }
}

extension TimeInterval {
    var milliseconds: Int64 {
        return Int64(self * 1000)
    }
}
