import {triggerDetatchEvent} from "./viewAttached";

const previousViewSymbol = Symbol("previousView")

declare global {
    interface HTMLDivElement {
        [previousViewSymbol]: HTMLElement | undefined
    }
}

export function swapViewSwap(view: HTMLDivElement, to: HTMLElement | null, animation: string) {
    if(to){
        to.style.width = "100%";
        to.style.height = "100%";
    }
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
                triggerDetatchEvent(current);
            }
        }
        current.addEventListener("animationend", animOutHandler)
        current.style.animation = `${animationOut} 0.25s`

        //animate in
        if (to) {
            const animationIn = `${animation}-in`
            let animInHandler: (ev: AnimationEvent) => void;
            animInHandler = (ev) => {
                if (ev.animationName === animationIn) {
                    to.onanimationend = null;
                    to.style.removeProperty("animation");
                }
            }
            to.addEventListener("animationend", animInHandler)
            to.style.animation = `${animationIn} 0.25s 0.25s` //Delay seems to make this work right
            view.appendChild(to);
        } else {
            view.style.visibility = "hidden"; //TODO: Smooth fade would look nicer
            console.log("Hiding myself... I'm shy")
        }
    } else if (to) {
        view.appendChild(to);
    } else {
        view.style.visibility = "hidden";
        console.log("Hiding myself... I'm shy")
    }
    view[previousViewSymbol] = to ?? undefined;
}