
export const detatchEventSymbol = Symbol("detatchEvent")
declare global {
    interface HTMLElement {
        [detatchEventSymbol]: Array<(view: HTMLElement)=>void> | undefined
    }
}
export function addDetatchEvent(view: HTMLElement, action: (view: HTMLElement)=>void) {
    let existing = view[detatchEventSymbol];
    if(Array.isArray(existing)){
        existing.push(action);
    } else {
        view[detatchEventSymbol] = [action];
    }
}
export function triggerDetatchEvent(view: HTMLElement){
    let existing = view[detatchEventSymbol];
    if(Array.isArray(existing)){
        for(const element of existing){
            (element as (view: Node)=>void)(view);
        }
    }
    for(let i = 0; i < view.childNodes.length; i++){
        const child = view.childNodes.item(i);
        if(child instanceof HTMLElement){
            triggerDetatchEvent(child);
        }
    }
}