// Generated by Khrysalis Swift converter - this file will be overwritten.
// File: views/Form.shared.kt
// Package: com.lightningkite.khrysalis.views
import Foundation

public class FormValidationError {
    public var field: UntypedFormField
    public var string: ViewString
    public init(field: UntypedFormField, string: ViewString) {
        self.field = field
        self.string = string
    }
}

public protocol UntypedFormField {
    
    var name: ViewString { get }
    
    var untypedObservable: Any { get }
    
    var validation: (UntypedFormField) -> ViewString? { get }
    
    var error: StandardObservableProperty<ViewString?> { get }
    
}


public class FormField<T> : UntypedFormField {
    public var name: ViewString
    public var observable: MutableObservableProperty<T>
    public var validation:  (UntypedFormField) -> ViewString?
    public init(name: ViewString, observable: MutableObservableProperty<T>, validation: @escaping  (UntypedFormField) -> ViewString?) {
        self.name = name
        self.observable = observable
        self.validation = validation
        self.error = StandardObservableProperty(underlyingValue: nil)
    }
    
    public let error: StandardObservableProperty<ViewString?>
    public var value: T {
        get { return self.observable.value }
        set(value) {
            self.observable.value = value
        }
    }
    public var untypedObservable: Any {
        get { return self.observable }
    }
}

public class Form {
    public init() {
        self.fields = []
    }
    
    
    public class Companion {
        private init() {
            self.xIsRequired = ViewStringRaw(string: "%1$s is required.")
            self.xMustMatchY = ViewStringRaw(string: "%1$s must match %2$s.")
        }
        public static let INSTANCE = Companion()
        
        public var xIsRequired: ViewString
        public var xMustMatchY: ViewString
    }
    
    public var fields: Array<UntypedFormField>
    
    public func field<T>(name: ViewString, defaultValue: T, validation: @escaping  (FormField<T>) -> ViewString?) -> FormField<T> {
        let obs = StandardObservableProperty(underlyingValue: defaultValue)
        let field = FormField(name: name, observable: obs, validation: { (untypedField: UntypedFormField) -> ViewString? in validation(untypedField as! FormField<T>) })
        self.fields.append(field)
        return field
    }
    
    public func field<T>(name: StringResource, defaultValue: T, validation: @escaping  (FormField<T>) -> ViewString?) -> FormField<T> { return self.field(name: ViewStringResource(resource: name), defaultValue: defaultValue, validation: validation) }
    
    public func fieldFromProperty<T>(name: ViewString, property: MutableObservableProperty<T>, validation: @escaping  (FormField<T>) -> ViewString?) -> FormField<T> {
        let field = FormField(name: name, observable: property, validation: { (untypedField: UntypedFormField) -> ViewString? in validation(untypedField as! FormField<T>) })
        self.fields.append(field)
        return field
    }
    
    public func fieldFromProperty<T>(name: StringResource, property: MutableObservableProperty<T>, validation: @escaping  (FormField<T>) -> ViewString?) -> FormField<T> { return self.fieldFromProperty(name: ViewStringResource(resource: name), property: property, validation: validation) }
    
    public func check() -> Array<FormValidationError> {
        return self.fields.compactMap({ (it: UntypedFormField) -> FormValidationError? in 
                let result = self.checkField(field: it)
                if let result = result {
                    return FormValidationError(field: it, string: result)
                } else {
                    return nil
                }
        })
    }
    
    public func runOrDialog(action: () -> Void) -> Void {
        let errors = self.check()
        if (!errors.isEmpty) {
            showDialog(message: errors.map({ (it: FormValidationError) -> ViewString in it.string }).joinToViewString())
        } else {
            action()
        }
    }
    
    public func checkField(field: UntypedFormField) -> ViewString? {
        let result = field.validation(field)
        field.error.value = result
        return result
    }
}

public extension FormField where T == String {
    func required() -> ViewString? {
        if self.observable.value.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
            return ViewStringTemplate(template: Form.Companion.INSTANCE.xIsRequired, arguments: [self.name])
        } else {
            return nil
        }
    }
}

public extension FormField {
    func notNull() -> ViewString? {
        if self.observable.value == nil {
            return ViewStringTemplate(template: Form.Companion.INSTANCE.xIsRequired, arguments: [self.name])
        } else {
            return nil
        }
    }
}

public extension FormField where T == Bool {
    func notFalse() -> ViewString? {
        if !self.observable.value {
            return ViewStringTemplate(template: Form.Companion.INSTANCE.xIsRequired, arguments: [self.name])
        } else {
            return nil
        }
    }
}

public extension ViewString {
    func unless(condition: Bool) -> ViewString? {
        if condition {
            return nil
        } else {
            return self
        }
    }
}


public extension FormField where T: Equatable {
    func matches(other: FormField<T>) -> ViewString? {
        if self.observable.value != other.observable.value {
            return ViewStringTemplate(template: Form.Companion.INSTANCE.xMustMatchY, arguments: [self.name, other.name])
        } else {
            return nil
        }
    }
}

//object test {
    //    val form = Form()
    //
    //    val username = Field(ViewStringRaw("Username"), "") { it.required() ?: it.isEmail() ?: it.matches(otherField) }
    //    val password = Field(ViewStringRaw("Password"), "") { it.required() }
    //    val verifyPassword = Field(ViewStringRaw("Verify Password"), "") { it.required() ?: it.matches(password) }
//}


