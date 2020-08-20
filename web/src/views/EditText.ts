
//! Declares com.lightningkite.khrysalis.views.setOnDoneClick
export function androidWidgetEditTextSetOnDoneClick(editText: HTMLInputElement, action: ()=>void) {
    editText.addEventListener("keyup", function(event) {
        if (event.key === "Enter") {
            action()
        }
    });
}