
//--- Class for license
class LicenseForm(
    var id: Long? = null,
    type: String = "",
    number: String = "",
    state: StateType? = null,
    years: Int = 0,
    active: Boolean = false,
    imageUrl: String? = null,
    image: ImageReference? = null
) {
    val form: Form
    val typeField: FormField<String>
    val numberField: FormField<String>
    val stateField: FormField<StateType>
    val yearsField: FormField<String>
    val activeField: FormField<Boolean>
    val imageUrlField: FormField<String?>
    val imageField: FormField<ImageReference?>

    init {
        this.form = Form()
        this.typeField = form.field(R.string.license_type_hint, type) { field -> field.required() }
        this.numberField =
            form.field(R.string.license_slash_certificate_number, number) { field -> field.required() }
        this.stateField = form.field(
            R.string.state_hint,
            state ?: ChoiceCache.states.firstOrNull() ?: StateType()
        ) { field -> null }
        this.yearsField =
            form.field(R.string.years, if (years > 0) years.toString() else "") { field -> field.required() }
        this.activeField = form.field(R.string.is_active, active) { field -> field.notNull() }
        this.imageField = form.field(R.string.license_slash_certificate_image, image) { field -> null }
        this.imageUrlField = form.field(R.string.license_slash_certificate_image, imageUrl) { field -> field.notNull()
            if(field.value == null && this.imageField.value == null) {
                return@field ViewStringResource(R.string.license_slash_certificate_image_required)
            } else {
                return@field null
            }
        }
    }
}
