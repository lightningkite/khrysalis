//
//  khrysalisTests.swift
//  khrysalisTests
//
//  Created by Joseph Ivie on 9/27/19.
//  Copyright © 2019 Lightning Kite. All rights reserved.
//

import XCTest
@testable import Khrysalis

class ByteBufferTests: XCTestCase {

    func testByte() {
        let inputValue: Int8 = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .put(inputValue)
                .data()
                .buffer()
                .get()
        )
    }

    func testShort() {
        let inputValue: Int16 = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putShort(inputValue)
                .data()
                .buffer()
                .getShort()
        )
    }

    func testInt() {
        let inputValue: Int32 = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putInt(inputValue)
                .data()
                .buffer()
                .getInt()
        )
    }

    func testLong() {
        let inputValue: Int64 = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putLong(inputValue)
                .data()
                .buffer()
                .getLong()
        )
    }

    func testFloat() {
        let inputValue: Float = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putFloat(inputValue)
                .data()
                .buffer()
                .getFloat()
        )
    }

    func testDouble() {
        let inputValue: Double = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putDouble(inputValue)
                .data()
                .buffer()
                .getDouble()
        )
    }

    func testUtf8() {
        let inputValue = "Test string!"
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putUtf8(inputValue)
                .data()
                .buffer()
                .getUtf8()
        )
    }

    func testByteAtPosition() {
        let inputValue: Int8 = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .put(18, inputValue)
                .data()
                .buffer()
                .get(18)
        )
    }

    func testShortAtPosition() {
        let inputValue: Int16 = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putShort(18, inputValue)
                .data()
                .buffer()
                .getShort(18)
        )
    }

    func testIntAtPosition() {
        let inputValue: Int32 = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putInt(18, inputValue)
                .data()
                .buffer()
                .getInt(18)
        )
    }

    func testLongAtPosition() {
        let inputValue: Int64 = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putLong(18, inputValue)
                .data()
                .buffer()
                .getLong(18)
        )
    }

    func testFloatAtPosition() {
        let inputValue: Float = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putFloat(18, inputValue)
                .data()
                .buffer()
                .getFloat(18)
        )
    }

    func testDoubleAtPosition() {
        let inputValue: Double = 21
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putDouble(18, inputValue)
                .data()
                .buffer()
                .getDouble(18)
        )
    }

    func testUtf8AtPosition() {
        let inputValue = "Test string!"
        XCTAssertEqual(
            inputValue,
            ByteBuffer.allocate(128)
                .putUtf8(18, inputValue)
                .data()
                .buffer()
                .getUtf8(18)
        )
    }
}
