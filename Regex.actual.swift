//
//  Regex.actual.swift
//  Alamofire
//
//  Created by Brady on 4/24/20.
//

import Foundation

public struct Regex {
    public let pattern: NSRegularExpression
    public init(pattern: String){
        self.pattern = (try? NSRegularExpression(pattern: pattern, options: []))!
    }
    
    
    public init(_ pattern: String){
        self.pattern = (try? NSRegularExpression(pattern: pattern, options: []))!
    }
    
    public struct Match {
        public let value: String
        public let groupValues: Array<String>
    }
    
    public func find(_ string: String) -> Match? {
        guard let match = self.pattern.firstMatch(in: string, options: [], range: NSRange(string.startIndex ..< string.endIndex, in: string)) else { return nil }
 
        var groupValues = Array<String>()
        for index in 0 ..< match.numberOfRanges {
            let nsRange = match.range(at: index)
            let match = string.substring(Int32(nsRange.lowerBound), Int32(nsRange.lowerBound + nsRange.length))
            groupValues.add(match)
        }
        return Match(value: groupValues[0], groupValues: groupValues)
    }
}
