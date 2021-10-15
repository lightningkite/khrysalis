import Foundation

public extension String {
    init(kotlin: Any) {
        if let opt = kotlin as? OptionalProtocol {
            self.init(kotlin: opt.finalValue)
        } else {
            self.init(describing: kotlin)
        }
    }

    subscript(i: Int) -> Character {
        return self[index(startIndex, offsetBy: Int(i))]
    }

    func getOrNull(index: Int) -> Character? {
        if index >= count { return nil }
        return self[index]
    }

    func substring(_ startIndex: Int, _ endIndex: Int? = nil) -> String {
        if startIndex > self.count { return "" }
        if let endIndex = endIndex, startIndex >= endIndex { return "" }
        let s = self.index(self.startIndex, offsetBy: Int(startIndex))
        let e = self.index(self.startIndex, offsetBy: Int(endIndex ?? self.count))
        return String(self[s..<e])
    }
    func substring(startIndex: Int, endIndex: Int? = nil) -> String {
        return substring(startIndex, endIndex)
    }
    func contains(_ string: String) -> Bool {
        if string.isEmpty { return true }
        return self.range(of: string) != nil
    }
    func replace(_ target: String, _ withString: String) -> String {
        return self.replacingOccurrences(of: target, with: withString)
    }

    func removePrefix(prefix: String) -> String {
        if starts(with: prefix) {
            return substring(prefix.count)
        } else {
            return self
        }
    }

    func removeSuffix(suffix: String) -> String {
        if hasSuffix(suffix) {
            return substring(0, self.count - suffix.count)
        } else {
            return self
        }
    }

    func substringBefore(delimiter: String, missingDelimiterValue: String? = nil) -> String {
        let index = self.indexOf(string: delimiter)
        if index != -1 {
            return substring(0, index)
        } else {
            return missingDelimiterValue ?? self
        }
    }

    func substringAfter(delimiter: String, missingDelimiterValue: String? = nil) -> String {
        let index = self.indexOf(string: delimiter)
        if index != -1 {
            return substring(index + delimiter.count)
        } else {
            return missingDelimiterValue ?? self
        }
    }

    func substringBeforeLast(delimiter: String, missingDelimiterValue: String? = nil) -> String {
        let index = self.lastIndexOf(string: delimiter)
        if index != -1 {
            return substring(0, index)
        } else {
            return missingDelimiterValue ?? self
        }
    }

    func substringAfterLast(delimiter: String, missingDelimiterValue: String? = nil) -> String {
        let index = self.lastIndexOf(string: delimiter)
        if index != -1 {
            return substring(index + delimiter.count)
        } else {
            return missingDelimiterValue ?? self
        }
    }

    func remove(_ sequence:String) -> String{
        let temp = self
        return temp.replacingOccurrences(of: sequence, with:"")
    }

    func remove(sequence:String) -> String{
        return remove(sequence)
    }
    func remove(char: String) -> String{
        return remove(char)
    }

    func drop(n:Int) -> String{
        if n >= self.count{
            return ""
        } else{
            return substring(startIndex:n)
        }
    }
    func drop(_ n: Int) -> String{
        return drop(n:n)
    }

    fileprivate static let humanifyRegex: NSRegularExpression = { ()->NSRegularExpression in
        do {
            return try NSRegularExpression(pattern: "([a-z0-9])([A-Z])", options: [])
        } catch {
            print(error)
            fatalError(String(describing: error))
        }
    }()
    func humanify() -> String {
        if self.isEmpty { return "" }
        let step1 = self.replace("_", " ").replace(".", " - ")
        return String.humanifyRegex.stringByReplacingMatches(
            in: step1,
            options: [],
            range: NSRange(location: 0, length: step1.count),
            withTemplate: "$1_$2"
        ).trimmingCharacters(in: .whitespaces)
    }

    fileprivate static let snakeCaseRegex: NSRegularExpression = { ()->NSRegularExpression in
        do {
            return try NSRegularExpression(pattern: "([a-z0-9])([A-Z])", options: [])
        } catch {
            print(error)
            fatalError(String(describing: error))
        }
    }()
    func toSnakeCase() -> String {
        return String.snakeCaseRegex.stringByReplacingMatches(
            in: self,
            options: [],
            range: NSRange(location: 0, length: self.count),
            withTemplate: "$1_$2"
        ).lowercased()
    }

    fileprivate static let fixTemplateRegex: NSRegularExpression = { ()->NSRegularExpression in
        do {
            return try NSRegularExpression(pattern: "%(\\d+\\$)?s", options: [])
        } catch {
            print(error)
            fatalError(String(describing: error))
        }
    }()

    func formatList(arguments: Array<Any>) -> String {
        #if os(iOS)
        let fixedArgs: Array<CVarArg> = arguments.map { it in
            if let it = it as? CVarArg {
                return it
            } else {
                return String(describing: it)
            }
        }
        #else
        for argIndex in arguments.indices {
            let item = arguments[argIndex]
            if item is CVarArg {}
            else if let str = item as? String {
                var result = ""
                str.withCString { ptr in
                    var copy = arguments
                    copy[argIndex] = ptr
                    result = formatList(arguments: copy)
                }
                return result
            }
            else {
                var result = ""
                String(describing: item).withCString { ptr in
                    var copy = arguments
                    copy[argIndex] = ptr
                    result = formatList(arguments: copy)
                }
                return result
            }
        }
        let fixedArgs = arguments.map { $0 as! CVarArg }
        #endif
        let fixedTemplate = String.fixTemplateRegex.stringByReplacingMatches(in: self, options: [], range: NSMakeRange(0, self.count), withTemplate: "%$1@")
        return String(format: fixedTemplate, arguments: fixedArgs)
    }
    func format(_ arguments: Any...) -> String {
        return formatList(arguments: arguments)
    }

    func format(args: Any...) -> String {
        return formatList(arguments: args)
    }
}

public extension Character{
    func isDigit()->Bool{
        return self.isNumber
    }
}

public extension StringProtocol {
    func indexOf(string: Self, startIndex: Int = 0, ignoreCase: Bool = true) -> Int {
        if string.isEmpty { return 0 }
        var options: String.CompareOptions = [.literal]
        if ignoreCase {
            options = [.literal, .caseInsensitive]
        }
        if let index = range(of: string, options: options, range: self.index(self.startIndex, offsetBy: startIndex)..<self.endIndex)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }

    func lastIndexOf(string: Self, startIndex: Int = 0, ignoreCase: Bool = true) -> Int {
        if string.isEmpty { return 0 }
        var options: String.CompareOptions = [.literal, .backwards]
        if ignoreCase {
            options = [.literal, .caseInsensitive, .backwards]
        }
        if let index = range(of: string, options: options, range: self.index(self.startIndex, offsetBy: startIndex)..<self.endIndex)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }
    func indexOfAny(chars: Array<Character>, startIndex: Int = 0, ignoreCase: Bool = true) -> Int {
        var options: String.CompareOptions = [.literal]
        if ignoreCase {
            options = [.literal, .caseInsensitive]
        }
        if let index = rangeOfCharacter(from: CharacterSet(chars.flatMap { $0.unicodeScalars }), options: options, range: self.index(self.startIndex, offsetBy: startIndex)..<self.endIndex)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }

    func lastIndexOfAny(chars: Array<Character>, startIndex: Int = 0, ignoreCase: Bool = true) -> Int {
        var options: String.CompareOptions = [.literal, .backwards]
        if ignoreCase {
            options = [.literal, .caseInsensitive, .backwards]
        }
        if let index = rangeOfCharacter(from: CharacterSet(chars.flatMap { $0.unicodeScalars }), options: options, range: self.index(self.startIndex, offsetBy: startIndex)..<self.endIndex)?.lowerBound {
            return Int(distance(from: self.startIndex, to: index))
        } else {
            return -1
        }
    }
}
