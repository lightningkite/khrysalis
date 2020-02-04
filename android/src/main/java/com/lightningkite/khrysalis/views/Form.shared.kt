package com.lightningkite.khrysalis.views

import com.lightningkite.khrysalis.Equatable
import com.lightningkite.khrysalis.escaping
import com.lightningkite.khrysalis.observables.MutableObservableProperty
import com.lightningkite.khrysalis.observables.StandardObservableProperty
import com.lightningkite.khrysalis.views.StringResource


class FormValidationError(
    val field: UntypedFormField,
    val string: ViewString
)

interface UntypedFormField {
    val name: ViewString
    val untypedObservable: Any
    val validation: (UntypedFormField) -> ViewString?
    val error: StandardObservableProperty<ViewString?>
}

class FormField<T>(
    override val name: ViewString,
    val observable: MutableObservableProperty<T>,
    override val validation: @escaping() (UntypedFormField) -> ViewString?
) : UntypedFormField {
    override val error: StandardObservableProperty<ViewString?> = StandardObservableProperty(null)
    val value: T get() = observable.value
    override val untypedObservable: Any
        get() = observable
}

class Form {

    companion object {
        var xIsRequired: ViewString = ViewStringRaw("%1\$s is required.")
        var xMustMatchY: ViewString = ViewStringRaw("%1\$s must match %2\$s.")
    }

    val fields: ArrayList<UntypedFormField> = ArrayList()

    fun <T> field(
        name: ViewString,
        defaultValue: T,
        validation: @escaping() (FormField<T>) -> ViewString?
    ): FormField<T> {
        val obs = StandardObservableProperty(defaultValue)
        val field = FormField(
            name = name,
            observable = obs,
            validation = { untypedField ->
                validation(untypedField as FormField<T>)
            }
        )
        fields.add(field)
        return field
    }

    fun <T> field(
        name: StringResource,
        defaultValue: T,
        validation: @escaping() (FormField<T>) -> ViewString?
    ): FormField<T> = field(ViewStringResource(name), defaultValue, validation)

    fun <T> fieldFromProperty(
        name: ViewString,
        property: MutableObservableProperty<T>,
        validation: @escaping() (FormField<T>) -> ViewString?
    ): FormField<T> {
        val field = FormField(
            name = name,
            observable = property,
            validation = { untypedField ->
                validation(untypedField as FormField<T>)
            }
        )
        fields.add(field)
        return field
    }

    fun <T> fieldFromProperty(
        name: StringResource,
        property: MutableObservableProperty<T>,
        validation: @escaping() (FormField<T>) -> ViewString?
    ): FormField<T> = fieldFromProperty(ViewStringResource(name), property, validation)

    fun check(): List<FormValidationError> {
        return fields.mapNotNull { it ->
            val result = checkField(it)
            if (result != null) {
                return@mapNotNull FormValidationError(field = it, string = result)
            } else {
                return@mapNotNull null
            }
        }
    }

    fun runOrDialog(action:()->Unit){
        val errors = check()
        if(errors.isNotEmpty()){
            showDialog(errors.map { it -> it.string }.joinToViewString())
        } else {
            action()
        }
    }

    fun checkField(field: UntypedFormField): ViewString? {
        val result = field.validation(field)
        field.error.value = result
        return result
    }
}

fun FormField<String>.required(): ViewString? {
    if (this.observable.value.isBlank()) {
        return ViewStringTemplate(Form.xIsRequired, listOf(this.name))
    } else {
        return null
    }
}

fun <T> FormField<T>.notNull(): ViewString? {
    if (this.observable.value == null) {
        return ViewStringTemplate(Form.xIsRequired, listOf(this.name))
    } else {
        return null
    }
}

fun FormField<Boolean>.notFalse(): ViewString? {
    if (!this.observable.value) {
        return ViewStringTemplate(Form.xIsRequired, listOf(this.name))
    } else {
        return null
    }
}

fun ViewString.unless(condition: Boolean): ViewString? {
    if (condition) {
        return null
    } else {
        return this
    }
}


fun <T : Equatable> FormField<T>.matches(other: FormField<T>): ViewString? {
    if (this.observable.value != other.observable.value) {
        return ViewStringTemplate(Form.xMustMatchY, listOf(this.name, other.name))
    } else {
        return null
    }
}

//object test {
//    val form = Form()
//
//    val username = Field(ViewStringRaw("Username"), "") { it.required() ?: it.isEmail() ?: it.matches(otherField) }
//    val password = Field(ViewStringRaw("Password"), "") { it.required() }
//    val verifyPassword = Field(ViewStringRaw("Verify Password"), "") { it.required() ?: it.matches(password) }
//}