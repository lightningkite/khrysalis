//
//  Codable.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 5/23/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation

public extension Formatter {
    static let iso8601: DateFormatter = {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .iso8601)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXXXX"
        return formatter
    }()
    static let iso8601noFS: DateFormatter = {
        let formatter = DateFormatter()
        formatter.calendar = Calendar(identifier: .iso8601)
        formatter.locale = Locale(identifier: "en_US_POSIX")
        formatter.timeZone = TimeZone(secondsFromGMT: 0)
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXXXX"
        return formatter
    }()
}

public var encoder: JSONEncoder = {
    let e = JSONEncoder()
    e.dateEncodingStrategy = JSONEncoder.DateEncodingStrategy.custom { (date, encoder) in
        var container = encoder.singleValueContainer()
        try container.encode(Formatter.iso8601.string(from: date))
    }
    return e
}()
public var decoder: JSONDecoder = {
    let d = JSONDecoder()
    d.dateDecodingStrategy = JSONDecoder.DateDecodingStrategy.custom { (decoder) in
        let container = try decoder.singleValueContainer()
        let string = try container.decode(String.self)
        if let date = Formatter.iso8601.date(from: string) ?? Formatter.iso8601noFS.date(from: string) {
            return date
        }
        throw DecodingError.dataCorruptedError(in: container, debugDescription: "Invalid date: \(string)")    }
    return d
}()

public extension Encodable {
    func toJsonData(coder: JSONEncoder = encoder) throws -> Data {
        if let result = try? coder.encode(self) {
            return result
        }
        let result = try coder.encode([self])
        let string = String(data: result, encoding: .utf8)!
        return string.substring(1, string.length - 1).data(using: .utf8)!
    }
    func toJsonString(coder: JSONEncoder = encoder) throws -> String {
        let data = try toJsonData(coder: coder)
        if let stringRep = String(data: data, encoding: .utf8) {
            return stringRep
        } else {
            throw Exception("Couldn't turn the data into a UTF8 String.")
        }
    }
}

public extension Decodable {
    static func fromJsonData(_ data: Data, coder: JSONDecoder = decoder) throws -> Self {
        if let result = try? coder.decode(Self.self, from: data) {
            return result
        }
        let dataString = String(data: data, encoding: .utf8)!
        let fixedData = ("[" + dataString + "]").data(using: .utf8)!
        let result = try coder.decode(Array<Self>.self, from: fixedData)
        return result[0]
    }
    static func fromJsonString(_ string: String, coder: JSONDecoder = decoder) throws -> Self {
        if let data = string.data(using: .utf8) {
            return try fromJsonData(data, coder: coder)
        } else {
            throw Exception("Couldn't turn the string into data")
        }
    }
}

public extension KeyedDecodingContainer {
    
    func decodeDoubleIfPresent(forKey key: KeyedDecodingContainer<K>.Key) throws -> Double? {
        if let result = try? decodeIfPresent(Double.self, forKey: key) {
            return result
        } else if let stringOrNil = try? decodeIfPresent(String.self, forKey: key) {
            return Double(stringOrNil)
        } else {
            return nil
        }
    }
    func decodeDouble(forKey key: KeyedDecodingContainer<K>.Key) throws -> Double {
        if let result = try? decode(Double.self, forKey: key) {
            return result
        }
        let string = try decode(String.self, forKey: key)
        return Double(string) ?? 0
    }
}

public extension String {
    func fromJsonStringUntyped() -> Any? {
        return try? JSONSerialization.jsonObject(with: self.data(using: .utf8)!, options: .allowFragments)
    }
}
