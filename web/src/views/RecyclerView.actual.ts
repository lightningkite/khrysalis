import {tryCastClass} from "../kotlin/Language";

export function scrollChildIntoView(e: HTMLDivElement, index: number, options: ScrollIntoViewOptions) {
    tryCastClass<HTMLElement>(e.children.item(index), HTMLElement)?.scrollIntoView(options)
}