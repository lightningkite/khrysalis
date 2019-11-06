//Package: com.lightningkite.kwift.views.shared
//Converted using Kwift2

import Foundation



public class FormValidationError {
    
    public var field: UntypedFormField
    public var string: ViewString
    
    
    public init(field: UntypedFormField, string: ViewString) {
        self.field = field
        self.string = string
    }
    convenience public init(_ field: UntypedFormField, _ string: ViewString) {
        self.init(field: field, string: string)
    }
}
 
 

public protocol UntypedFormField {
    
    var name: ViewString { get }
    
    var untypedObservable: Any { get }
    
    var validation: (UntypedFormField) -> ViewString? { get }
    
    var error: StandardObservableProperty<ViewString?> { get }
}
 
 

public class FormField<T>: UntypedFormField {
    
    public var name: ViewString
    public var observable: StandardObservableProperty<T>
    public var validation:  (UntypedFormField) -> ViewString?

    
    public var error: StandardObservableProperty<ViewString?>
    public var value: T {
        get {
            return observable.value
        }
    }
    public var untypedObservable: Any {
        get {
            return observable
        }
    }
    
    public init(name: ViewString, observable: StandardObservableProperty<T>, validation: @escaping (UntypedFormField) -> ViewString?
) {
        self.name = name
        self.observable = observable
        self.validation = validation
        let error: StandardObservableProperty<ViewString?> = StandardObservableProperty(nil)
        self.error = error
    }
    convenience public init(_ name: ViewString, _ observable: StandardObservableProperty<T>, _ validation: @escaping (UntypedFormField) -> ViewString?
) {
        self.init(name: name, observable: observable, validation: validation)
    }
}
 
 

public class Form {
    
    
    
    //Start Companion
    static public var xIsRequired: ViewString = ViewStringRaw("%1$s is required.")
    static public var xMustMatchY: ViewString = ViewStringRaw("%1$s must match %2$s.")
    //End Companion
    
    public var fields: Array<UntypedFormField>
    
    public func field<T>(name: ViewString, defaultValue: T, validation: @escaping (FormField<T>) -> ViewString?
) -> FormField<T> {
        var obs = StandardObservableProperty(defaultValue)
        var field = FormField(name: name, observable: obs, validation: { (untypedField) in 
            validation(untypedField as! FormField<T>)
        })
        fields.add(field)
        return field
    }
    public func field<T>(_ name: ViewString, _ defaultValue: T, _ validation: @escaping (FormField<T>) -> ViewString?
) -> FormField<T> {
        return field(name: name, defaultValue: defaultValue, validation: validation)
    }
    
    public func field<T>(name: StringResource, defaultValue: T, validation: @escaping (FormField<T>) -> ViewString?
) -> FormField<T> {
        return field(ViewStringResource(name), defaultValue, validation)
    }
    public func field<T>(_ name: StringResource, _ defaultValue: T, _ validation: @escaping (FormField<T>) -> ViewString?
) -> FormField<T> {
        return field(name: name, defaultValue: defaultValue, validation: validation)
    }
    
    public func check() -> Array<FormValidationError> {
        return fields.mapNotNull{ (it) in 
            var result = checkField(it)
            if let result = result {
                return FormValidationError(field: it, string: result)
            } else {
                return nil
            }
        }
    }
    
    public func runOrDialog(action: () -> Void) -> Void {
        var errors = check()
        if errors.isNotEmpty() {
            showDialog(errors.map{ (it) in 
                it.string
            }.joinToViewString())
        } else {
            action()
        }
    }
    
    public func checkField(field: UntypedFormField) -> ViewString?  {
        var result = field.validation(field)
        field.error.value = result
        return result
    }
    public func checkField(_ field: UntypedFormField) -> ViewString?  {
        return checkField(field: field)
    }
    
    public init() {
        let fields: Array<UntypedFormField> = Array()
        self.fields = fields
    }
}
 
 

extension FormField where T == String {
    public func required() -> ViewString?  {
        if self.observable.value.isBlank() {
            return ViewStringTemplate(Form.xIsRequired, [self.name])
        } else {
            return nil
        }
    }
}
 
 

extension FormField {
    public func notNull() -> ViewString?  {
        if self.observable.value == nil {
            return ViewStringTemplate(Form.xIsRequired, [self.name])
        } else {
            return nil
        }
    }
}
 
 

extension FormField where T == Bool {
    public func notFalse() -> ViewString?  {
        if !self.observable.value {
            return ViewStringTemplate(Form.xIsRequired, [self.name])
        } else {
            return nil
        }
    }
}
 
 

extension ViewString {
    public func unless(condition: Bool) -> ViewString?  {
        if condition {
            return nil
        } else {
            return self
        }
    }
    public func unless(_ condition: Bool) -> ViewString?  {
        return unless(condition: condition)
    }
}
 
 
 

extension FormField where T: Equatable {
    public func matches(other: FormField<T>) -> ViewString?  {
        if self.observable.value != other.observable.value {
            return ViewStringTemplate(Form.xMustMatchY, [self.name, other.name])
        } else {
            return nil
        }
    }
    public func matches(_ other: FormField<T>) -> ViewString?  {
        return matches(other: other)
    }
}
 
 
 
 
 
 
 
 
 
