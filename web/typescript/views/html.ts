export function loadHtmlFromString(data: string): HTMLElement {
    const d = document.createElement("div");
    d.innerHTML = data;
    return d.firstChild as HTMLElement;
}

export function findViewById<T extends HTMLElement>(view: HTMLElement, id: string): T | null {
    return (view.getElementsByClassName(`id-${id}`)[0] as (T | undefined)) ?? null;
}

export function getViewById<T extends HTMLElement>(view: HTMLElement, id: string): T {
    return view.getElementsByClassName(`id-${id}`)[0] as T;
}

export function replaceViewWithId(base: HTMLElement, withElement: () => HTMLElement, id: string) {
    const existing = findViewById(base, id);
    if (existing) {
        const newElement = withElement();
        newElement.setAttribute('style', newElement.getAttribute('style') + '; ' + existing.getAttribute('style'))
        existing.parentNode.replaceChild(existing, newElement);
    }
}