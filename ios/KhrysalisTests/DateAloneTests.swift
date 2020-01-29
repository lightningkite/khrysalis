//
//  khrysalisTests.swift
//  khrysalisTests
//
//  Created by Joseph Ivie on 9/27/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import XCTest
@testable import Khrysalis

class DateAloneTests: XCTestCase {
    
    var testDates = (-5000...5000).map {
        Date().dateAlone.safeAddDayOfWeek($0)
    }
    var testDatesPerformance = (-500...500).map {
        Date().dateAlone.safeAddDayOfWeek($0)
    }
    var testDatesShort = (-20...20).map {
        Date().dateAlone.safeAddDayOfWeek($0)
    }

    func testDayOfWeekRead(){
        for date in testDates {
            XCTAssertEqual(date.dayOfWeek, date.safeDayOfWeek, "Failed for \(date.toString())")
        }
    }

    func testDayOfWeek(){
        for testValue in 1.toInt()...7.toInt() {
            for date in testDatesShort {
                let actual = date.dayOfWeek(testValue)
                let expected = date.safeDayOfWeek(testValue)
                XCTAssertEqual(actual, expected, "Failed for \(date.toString()): expected \(expected.toString()), got \(actual.toString())")
            }
        }
    }
    func testDayOfMonth(){
        for testValue in 1.toInt()...28.toInt() {
            for date in testDatesShort {
                let actual = date.dayOfMonth(testValue)
                let expected = date.safeDayOfMonth(testValue)
                XCTAssertEqual(actual, expected, "Failed for \(date.toString()): expected \(expected.toString()), got \(actual.toString())")
            }
        }
    }
    func testMonthOfYear(){
        for testValue in 1.toInt()...12.toInt() {
            for date in testDatesShort {
                let actual = date.monthOfYear(testValue)
                let expected = date.safeMonthOfYear(testValue)
                XCTAssertEqual(actual, expected, "Failed for \(date.toString()): expected \(expected.toString()), got \(actual.toString())")
            }
        }
    }
    func testYearAd(){
        for testValue in 1970.toInt()...2020.toInt() {
            for date in testDates {
                let actual = date.yearAd(testValue)
                let expected = date.safeYearAd(testValue)
                XCTAssertEqual(actual, expected, "Failed for \(date.toString()): expected \(expected.toString()), got \(actual.toString())")
            }
        }
    }
    func testAddDayOfWeek(){
        for testValue in -4.toInt()...4.toInt() {
            for date in testDates {
                let actual = date.addDayOfWeek(testValue)
                let expected = date.safeAddDayOfWeek(testValue)
                XCTAssertEqual(actual, expected, "Failed for \(date.toString()): expected \(expected.toString()), got \(actual.toString())")
            }
        }
    }
    func testAddDayOfMonth(){
        for testValue in -4.toInt()...4.toInt() {
            for date in testDates {
                let actual = date.addDayOfMonth(testValue)
                let expected = date.safeAddDayOfMonth(testValue)
                XCTAssertEqual(actual, expected, "Failed for \(date.toString()): expected \(expected.toString()), got \(actual.toString())")
            }
        }
    }
    func testAddMonthOfYear(){
        for testValue in -4.toInt()...4.toInt() {
            for date in testDates {
                let actual = date.addMonthOfYear(testValue)
                let expected = date.safeAddMonthOfYear(testValue)
                XCTAssertEqual(actual, expected, "Failed for \(date.toString()): expected \(expected.toString()), got \(actual.toString())")
            }
        }
    }
    func testAddYearAd(){
        for testValue in -4.toInt()...4.toInt() {
            for date in testDates {
                let actual = date.addYearAd(testValue)
                let expected = date.safeAddYearAd(testValue)
                XCTAssertEqual(actual, expected, "Failed for \(date.toString()): expected \(expected.toString()), got \(actual.toString())")
            }
        }
    }

    func testDayOfWeekPerformance(){
        let myStart = Date()
        for testValue in 1.toInt()...7.toInt() {
            for date in testDatesPerformance {
                let actual = date.dayOfWeek(testValue)
            }
        }
        let myDuration = Date().timeIntervalSince(myStart)
        
        print("My: \(myDuration) seconds")
        
        let appleStart = Date()
        for testValue in 1.toInt()...7.toInt() {
            for date in testDatesPerformance {
                let expected = date.safeDayOfWeek(testValue)
            }
        }
        let appleDuration = Date().timeIntervalSince(appleStart)
        
        print("Apple: \(appleDuration) seconds")
        XCTAssertLessThan(myDuration, appleDuration)
    }
    func testDayOfMonthPerformance(){
        let myStart = Date()
        for testValue in 1.toInt()...28.toInt() {
            for date in testDatesPerformance {
                let actual = date.dayOfMonth(testValue)
            }
        }
        let myDuration = Date().timeIntervalSince(myStart)
        
        print("My: \(myDuration) seconds")
        
        let appleStart = Date()
        for testValue in 1.toInt()...28.toInt() {
            for date in testDatesPerformance {
                let expected = date.safeDayOfMonth(testValue)
            }
        }
        let appleDuration = Date().timeIntervalSince(appleStart)
        
        print("Apple: \(appleDuration) seconds")
        XCTAssertLessThan(myDuration, appleDuration)
    }
    func testMonthOfYearPerformance(){
        let myStart = Date()
        for testValue in 1.toInt()...12.toInt() {
            for date in testDatesPerformance {
                let actual = date.monthOfYear(testValue)
            }
        }
        let myDuration = Date().timeIntervalSince(myStart)
        
        print("My: \(myDuration) seconds")
        
        let appleStart = Date()
        for testValue in 1.toInt()...12.toInt() {
            for date in testDatesPerformance {
                let expected = date.safeMonthOfYear(testValue)
            }
        }
        let appleDuration = Date().timeIntervalSince(appleStart)
        
        print("Apple: \(appleDuration) seconds")
        XCTAssertLessThan(myDuration, appleDuration)
    }
    func testYearAdPerformance(){
        let myStart = Date()
        for testValue in 1970.toInt()...2020.toInt() {
            for date in testDatesPerformance {
                let actual = date.yearAd(testValue)
            }
        }
        let myDuration = Date().timeIntervalSince(myStart)
        
        print("My: \(myDuration) seconds")
        
        let appleStart = Date()
        for testValue in 1970.toInt()...2020.toInt() {
            for date in testDatesPerformance {
                let expected = date.safeYearAd(testValue)
            }
        }
        let appleDuration = Date().timeIntervalSince(appleStart)
        
        print("Apple: \(appleDuration) seconds")
        XCTAssertLessThan(myDuration, appleDuration)
    }
    func testAddDayOfWeekPerformance(){
        let myStart = Date()
        for testValue in -4.toInt()...4.toInt() {
            for date in testDatesPerformance {
                let actual = date.addDayOfWeek(testValue)
            }
        }
        let myDuration = Date().timeIntervalSince(myStart)
        
        print("My: \(myDuration) seconds")
        
        let appleStart = Date()
        for testValue in -4.toInt()...4.toInt() {
            for date in testDatesPerformance {
                let expected = date.safeAddDayOfWeek(testValue)
            }
        }
        let appleDuration = Date().timeIntervalSince(appleStart)
        
        print("Apple: \(appleDuration) seconds")
        XCTAssertLessThan(myDuration, appleDuration)
    }
    func testAddDayOfMonthPerformance(){
        let myStart = Date()
        for testValue in -4.toInt()...4.toInt() {
            for date in testDatesPerformance {
                let actual = date.addDayOfMonth(testValue)
            }
        }
        let myDuration = Date().timeIntervalSince(myStart)
        
        print("My: \(myDuration) seconds")
        
        let appleStart = Date()
        for testValue in -4.toInt()...4.toInt() {
            for date in testDatesPerformance {
                let expected = date.safeAddDayOfMonth(testValue)
            }
        }
        let appleDuration = Date().timeIntervalSince(appleStart)
        
        print("Apple: \(appleDuration) seconds")
        XCTAssertLessThan(myDuration, appleDuration)
    }
    func testAddMonthOfYearPerformance(){
        let myStart = Date()
        for testValue in -4.toInt()...4.toInt() {
            for date in testDatesPerformance {
                let actual = date.addMonthOfYear(testValue)
            }
        }
        let myDuration = Date().timeIntervalSince(myStart)
        
        print("My: \(myDuration) seconds")
        
        let appleStart = Date()
        for testValue in -4.toInt()...4.toInt() {
            for date in testDatesPerformance {
                let expected = date.safeAddMonthOfYear(testValue)
            }
        }
        let appleDuration = Date().timeIntervalSince(appleStart)
        
        print("Apple: \(appleDuration) seconds")
        XCTAssertLessThan(myDuration, appleDuration)
    }
    func testAddYearAdPerformance(){
        let myStart = Date()
        for testValue in -4.toInt()...4.toInt() {
            for date in testDatesPerformance {
                let actual = date.addYearAd(testValue)
            }
        }
        let myDuration = Date().timeIntervalSince(myStart)
        
        print("My: \(myDuration) seconds")
        
        let appleStart = Date()
        for testValue in -4.toInt()...4.toInt() {
            for date in testDatesPerformance {
                let expected = date.safeAddYearAd(testValue)
            }
        }
        let appleDuration = Date().timeIntervalSince(appleStart)
        
        print("Apple: \(appleDuration) seconds")
        XCTAssertLessThan(myDuration, appleDuration)
    }

}
