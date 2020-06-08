
export const attachmentEventSymbol = Symbol("attachmentEvent")
export function isAttached(view: Node): boolean {
    return document.body.contains(view);
}
export function addAttachmentEvent(view: Node, action: (view: Node)=>void) {
    let existing = (view as any)[attachmentEventSymbol];
    if(Array.isArray(existing)){
        existing.push(action);
    } else {
        (view as any)[attachmentEventSymbol] = [view];
    }
}
export function triggerAttachmentEvent(view: Node){
    let existing = (view as any)[attachmentEventSymbol];
    if(Array.isArray(existing)){
        for(const element of existing){
            (element as (view: Node)=>void)(view);
        }
    }
}