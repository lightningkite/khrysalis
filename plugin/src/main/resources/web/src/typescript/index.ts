
import './main.scss'
import * as html from './layouts/root.html'

function isPrototypeSwapView(view: HTMLElement): boolean {
    return (view as any).stack != null;
}
function prototypeSwapViewStack(view: HTMLElement): Array<string> {
    return ((view as any).stack as Array<string>);
}
function setPrototypeSwapViewStack(view: HTMLElement, stack: Array<string>) {
    (view as any).stack = stack;
}
function prototypeSwapViewSetup(view: HTMLElement, key: string, defaultView: string | null) {
    (view as any).stackKey = key;
    if (defaultView != null){
        setPrototypeSwapViewStack(view, [defaultView]);
    } else {
        setPrototypeSwapViewStack(view, []);
    }
}
function prototypeSwapViewReload(view: HTMLElement){
    const stack = prototypeSwapViewStack(view);
    if(stack.length == 0){
        view.style.display = "none";
        return;
    }
    view.style.display = "relative";
    const link = stack[stack.length-1];
    fetch(link)
        .then((response) => response.text())
        .then((html) => {
            view.innerHTML = html;
            const element = (view.firstElementChild as HTMLElement);
            element.style["width"] = "100%";
            element.style["height"] = "100%";
        })
        .catch((error) => {
            console.warn(error)
        });
}
function prototypeSwapViewPush(view: HTMLElement, name: string) {
    prototypeSwapViewStack(view).push(name);
    prototypeSwapViewReload(view);
}
function prototypeSwapViewSwap(view: HTMLElement, name: string) {
    const stack = prototypeSwapViewStack(view);
    stack.pop();
    stack.push(name);
    prototypeSwapViewReload(view);
}
function prototypeSwapViewReset(view: HTMLElement, name: string) {
    setPrototypeSwapViewStack(view, [name]);
    prototypeSwapViewReload(view);
}
function prototypeSwapViewPop(view: HTMLElement) {
    const stack = prototypeSwapViewStack(view);
    if(stack.length > 1){
        stack.pop();
        prototypeSwapViewReload(view);
    }
}
function prototypeSwapViewDismiss(view: HTMLElement) {
    const stack = prototypeSwapViewStack(view);
    if(stack.length > 0){
        stack.pop();
        prototypeSwapViewReload(view);
    }
}

function prototypeFindTarget(view: HTMLElement, targetName: string | null): HTMLElement | null {
    let current = view;
    while(true){
        if(targetName == null){
            if(isPrototypeSwapView(current)) return current;
        } else {
            const key = (view as any).stackKey as String;
            if(isPrototypeSwapView(current) && key == targetName) return current;
        }
        current = current.parentElement;
        if(current == null) return null;
    }
}
function prototypePush(view: HTMLElement, targetName: string | null, link: string) {
    prototypeSwapViewPush(prototypeFindTarget(view, targetName), link)
}
function prototypeSwap(view: HTMLElement, targetName: string | null, link: string) {
    prototypeSwapViewSwap(prototypeFindTarget(view, targetName), link)
}
function prototypeReset(view: HTMLElement, targetName: string | null, link: string) {
    prototypeSwapViewReset(prototypeFindTarget(view, targetName), link)
}
function prototypePop(view: HTMLElement, targetName: string | null) {
    prototypeSwapViewPop(prototypeFindTarget(view, targetName))
}
function prototypeDismiss(view: HTMLElement, targetName: string | null) {
    prototypeSwapViewDismiss(prototypeFindTarget(view, targetName))
}

console.log("Hello world!");
let root = document.getElementById("app");
root.innerHTML = html;
(root.firstElementChild as HTMLElement).style.width = "100%";
(root.firstElementChild as HTMLElement).style.height = "100%";
