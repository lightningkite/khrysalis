//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation


//--- Codable
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


//--- IsCodable
public typealias IsCodable = Codable

//--- JsonList
public typealias JsonList = NSArray

//--- JsonMap
public typealias JsonMap = NSDictionary

//--- IsCodable?.toJsonString()
public extension Encodable {
    func toJsonData(coder: JSONEncoder = encoder) -> Data {
        if let result = try? coder.encode(self) {
            return result
        }
        let result = try? coder.encode([self])
        let string = String(data: result!, encoding: .utf8)!
        return string.substring(1, string.count - 1).data(using: .utf8)!
    }
    func toJsonString(coder: JSONEncoder = encoder) -> String {
        let data = toJsonData(coder: coder)
        if let stringRep = String(data: data, encoding: .utf8) {
            return stringRep
        } else {
            return ""
        }
    }
}

public func xAnyToJsonString(_ value: Encodable) -> String {
    return value.toJsonString()
}
public func xAnyToJsonData(_ value: Encodable) -> Data {
    return value.toJsonData()
}
public func xAnyToJsonString(_ value: Dictionary<String, Any>) -> String {
    return PrimitiveCodableBox(value).toJsonString()
}
public func xAnyToJsonData(_ value: Dictionary<String, Any>) -> Data {
    return PrimitiveCodableBox(value).toJsonData()
}
public func xAnyToJsonString(_ value: Dictionary<String, Any?>) -> String {
    return PrimitiveCodableBox(value).toJsonString()
}
public func xAnyToJsonData(_ value: Dictionary<String, Any?>) -> Data {
    return PrimitiveCodableBox(value).toJsonData()
}
public func xAnyToJsonString(_ value: Dictionary<String, Codable>) -> String {
    return PrimitiveCodableBox(value).toJsonString()
}
public func xAnyToJsonData(_ value: Dictionary<String, Codable>) -> Data {
    return PrimitiveCodableBox(value).toJsonData()
}
public func xAnyToJsonString(_ value: Dictionary<String, Codable?>) -> String {
    return PrimitiveCodableBox(value).toJsonString()
}
public func xAnyToJsonData(_ value: Dictionary<String, Codable?>) -> Data {
    return PrimitiveCodableBox(value).toJsonData()
}


//--- String.fromJsonString()
//--- String.fromJsonStringUntyped()
public extension String {
    func fromJsonStringUntyped() -> Any? {
        let obj = try? JSONSerialization.jsonObject(with: self.data(using: .utf8)!, options: .allowFragments)
        return obj
    }
    func fromJsonString<T>() -> T? where T : Decodable {
        if let data = self.data(using: .utf8) {
            var err: Error? = nil
            do {
                let result = try decoder.decode(T.self, from: data)
                return result
            } catch {
                //Check error here
                err = error
            }
            let dataString = String(data: data, encoding: .utf8)!
            let fixedData = ("[" + dataString + "]").data(using: .utf8)!
            do {
                let result = try decoder.decode(Array<T>.self, from: fixedData)
                return result[0]
            } catch {
                err = error
            }
            if let err = err {
                print("Error decoding JSON into \(T.self): \(err)")
            }
            return nil
        }
        print("Error reading JSON into UTF8")
        return nil
    }
}


//--- Extra

public extension Decodable {
    static func fromJsonData(_ data: Data, coder: JSONDecoder = decoder) throws -> Self {
        var err: Error? = nil
        do {
            let result = try decoder.decode(Self.self, from: data)
            return result
        } catch {
            //Check error here
            err = error
        }
        let dataString = String(data: data, encoding: .utf8)!
        let fixedData = ("[" + dataString + "]").data(using: .utf8)!
        do {
            let result = try decoder.decode(Array<Self>.self, from: fixedData)
            return result[0]
        } catch {
            err = error
        }
        if let err = err {
            print("Error decoding JSON into \(Self.self): \(err)")
        }
        throw err ?? IllegalStateException()
    }
    static func fromJsonString(_ string: String, coder: JSONDecoder = decoder) throws -> Self {
        if let data = string.data(using: .utf8) {
            var err: Error? = nil
            do {
                let result = try decoder.decode(Self.self, from: data)
                return result
            } catch {
                //Check error here
                err = error
            }
            let dataString = String(data: data, encoding: .utf8)!
            let fixedData = ("[" + dataString + "]").data(using: .utf8)!
            do {
                let result = try decoder.decode(Array<Self>.self, from: fixedData)
                return result[0]
            } catch {
                err = error
            }
            if let err = err {
                print("Error decoding JSON into \(Self.self): \(err)")
            }
            throw err ?? IllegalStateException()
        }
        print("Error reading JSON into UTF8")
        throw IllegalArgumentException()
    }
}

public struct PrimitiveCodableBox: Codable {
    public var value: Any?
    public init(_ value: Any?) {
        self.value = value
    }
    
    struct StringKey: CodingKey {
        var stringValue: String
        
        init(stringValue: String) {
            self.stringValue = stringValue
        }
        
        var intValue: Int? = nil
        
        init?(intValue: Int) {
            self.stringValue = String(intValue)
        }
        
    }
    
    public init(from decoder: Decoder) throws {
        do {
            let values = try decoder.singleValueContainer()
            do { self.init(try values.decode(Int8.self)) } catch {
            do { self.init(try values.decode(Int16.self)) } catch {
            do { self.init(try values.decode(Int32.self)) } catch {
            do { self.init(try values.decode(Int64.self)) } catch {
            do { self.init(try values.decode(UInt8.self)) } catch {
            do { self.init(try values.decode(UInt16.self)) } catch {
            do { self.init(try values.decode(UInt32.self)) } catch {
            do { self.init(try values.decode(UInt64.self)) } catch {
            do { self.init(try values.decode(Float.self)) } catch {
            do { self.init(try values.decode(Double.self)) } catch {
            do { self.init(try values.decode(Int.self)) } catch {
            do { self.init(try values.decode(Bool.self)) } catch {
            do { self.init(try values.decode(String.self)) } catch {
            do { self.init(try values.decode(Array<PrimitiveCodableBox>.self)) } catch {
            do { self.init(try values.decode(Dictionary<String, PrimitiveCodableBox>.self)) } catch {
            self.init(nil)
                }}}}}}}}}}}}}}}} catch {
           self.init(nil)
           }
    }
    
    
    public func encode(to encoder: Encoder) throws {
        if let v = self.value as? Codable {
            try v.encode(to: encoder)
        } else if let v = self.value as? Dictionary<String, Any?> {
            var container = encoder.container(keyedBy: StringKey.self)
            for (key, value) in v {
                try container.encode(PrimitiveCodableBox(value), forKey: StringKey(stringValue: key))
            }
        } else {
            var svc = encoder.singleValueContainer()
            try svc.encodeNil()
        }
    }
}
