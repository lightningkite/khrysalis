//
//  Regex.actual.swift
//  Alamofire
//
//  Created by Brady on 4/24/20.
//

import Foundation

public extension NSRegularExpression {

    public struct Match {
        public let value: String
        public let groupValues: Array<String>
    }
    
    public func find(_ string: String) -> Match? {
        guard let match = self.firstMatch(in: string, options: [], range: NSRange(string.startIndex ..< string.endIndex, in: string)) else { return nil }
 
        var groupValues = Array<String>()
        for index in 0 ..< match.numberOfRanges {
            let nsRange = match.range(at: index)
            let match = string.substring(Int(nsRange.lowerBound), Int(nsRange.lowerBound + nsRange.length))
            groupValues.append(match)
        }
        return Match(value: groupValues[0], groupValues: groupValues)
    }

    public func matchEntire(_ input: String) -> Match?{
        let x = find(input)
        if let x = x {
            if x.value.count == input.count {
                return x
            }
        }
        return nil
    }

    public func split(input: String) -> Array<String> {
        let matches = self.matches(in: input, range: NSRange(0..<input.utf16.count))
        let ranges = [input.startIndex..<input.startIndex] + matches.map{Range($0.range, in: input)!} + [input.endIndex..<input.endIndex]
        return (0...matches.count).map {String(input[ranges[$0].upperBound..<ranges[$0+1].lowerBound])}
    }
}

public extension String {
    func matches(regex:NSRegularExpression)->Bool{
        let result = regex.matchEntire(self)
        return result != nil
    }
}
