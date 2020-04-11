//Package: test
//Converted using Khrysalis2

import Foundation
import RxSwift
import RxRelay



public enum MyEnum: String, StringEnum, CaseIterable, Codable {
    case VALUE1 = "VALUE1"
    case VALUE2 = "VALUE2"
    case VALUE3 = "VALUE3"
    public init(from decoder: Decoder) throws {
        self = try MyEnum(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .VALUE1
    }
}
 
 

public enum AnotherEnum: String, StringEnum, CaseIterable, Codable {
    case VAL1 = "VAL1"
    case VAL2 = "VAL2"
    case VAL3 = "VAL3"
    public init(from decoder: Decoder) throws {
        self = try AnotherEnum(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .VAL1
    }
}
 
 

public enum SingleLine: String, StringEnum, CaseIterable, Codable {
    case VALUE1 = "VALUE1"
    case VALUE2 = "VALUE2"
    public init(from decoder: Decoder) throws {
        self = try SingleLine(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .VALUE1
    }
}
 
 

public enum ConformingEnum: String, StringEnum, CaseIterable, Codable {
    case VAL1 = "VAL1"
    case VAL2 = "VAL2"
    public init(from decoder: Decoder) throws {
        self = try ConformingEnum(rawValue: decoder.singleValueContainer().decode(RawValue.self)) ?? .VAL1
    }
}
 
 
