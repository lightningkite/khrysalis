import {triggerAttachmentEvent} from "./viewAttached";

const previousViewSymbol = Symbol("previousView")

declare global {
    interface HTMLDivElement {
        [previousViewSymbol]: HTMLElement | undefined
    }
}

export function swapViewSwap(view: HTMLDivElement, to: HTMLElement | null, animation: string) {
    view.style.visibility = "visible";
    const current = view[previousViewSymbol];
    if (current) {
        //animate out
        const animationOut = `${animation}-out`
        let animOutHandler: (ev: AnimationEvent) => void;
        animOutHandler = (ev) => {
            if (ev.animationName === animationOut) {
                current.removeEventListener("animationend", animOutHandler)
                view.removeChild(current);
                triggerAttachmentEvent(current);
            }
        }
        current.addEventListener("animationend", animOutHandler)
        current.style.animation = `${animationOut} 0.25s`

        //animate in
        if (to) {
            to.style.animation = `${animation}-in 0.25s`
            view.appendChild(to);
            triggerAttachmentEvent(to);
        } else {
            view.style.visibility = "hidden"; //TODO: Smooth fade would look nicer
        }
    } else if (to) {
        view.appendChild(to);
        triggerAttachmentEvent(to);
    }
    view[previousViewSymbol] = to;
}