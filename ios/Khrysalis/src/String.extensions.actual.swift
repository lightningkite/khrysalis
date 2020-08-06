//Stub file made with Khrysalis 2 (by Lightning Kite)
import Foundation

public extension String {

    //--- String.humanify()
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

    //--- String.remove(Char)
    func remove(_ char: Character) -> String {
        TODO()
    }
    func remove(char: Character) -> String {
        TODO()
    }

    //--- String.toSnakeCase()
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

    //--- String.formatList(List<Any?>)
    func formatList(arguments: Array<Any>) -> String {
        return formatList(arguments)
    }
    func formatList(_ arguments: Array<Any>) -> String {
        let fixedArgs: Array<CVarArg> = arguments.map { it in
            if let it = it as? CVarArg {
                return it
            } else {
                return String(describing: it)
            }
        }
        let fixedTemplate = String.fixTemplateRegex.stringByReplacingMatches(in: self, options: [], range: NSMakeRange(0, self.count), withTemplate: "%$1@")
        return String(format: fixedTemplate, arguments: fixedArgs)
    }
    func format(_ arguments: Any...) -> String {
        return formatList(arguments)
    }
    
    func format(args: Any...) -> String {
        return formatList(args)
    }
}
