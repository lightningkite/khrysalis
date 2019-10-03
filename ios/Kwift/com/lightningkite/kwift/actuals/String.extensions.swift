//
//  String+humanify.swift
//  Lifting Generations
//
//  Created by Joseph Ivie on 6/26/19.
//  Copyright © 2019 Joseph Ivie. All rights reserved.
//

import Foundation

extension String {
    
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
    
    
    fileprivate static let fixTemplateRegex: NSRegularExpression = { ()->NSRegularExpression in
        do {
            return try NSRegularExpression(pattern: "%(\\d+\\$)?s", options: [])
        } catch {
            print(error)
            fatalError(String(describing: error))
        }
    }()
    
    func formatList(arguments: Array<Any>) -> String {
        return formatList(arguments)
    }
    func formatList(_ arguments: Array<Any>) -> String {
        let fixedArgs: Array<CVarArg> = arguments.filter { $0 is CVarArg }.map { $0 as! CVarArg }
        let fixedTemplate = String.fixTemplateRegex.stringByReplacingMatches(in: self, options: [], range: NSMakeRange(0, self.count), withTemplate: "%$1@")
        return String(format: fixedTemplate, arguments: fixedArgs)
    }
}
