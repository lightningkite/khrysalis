import {ViewGenerator} from "./views/ViewGenerator.shared";
import {listenForDialogs} from "./views/ViewDependency.actual";

export function main(rootVg: ViewGenerator){
    const view = rootVg.generate(window);
    document.body.appendChild(view);
    listenForDialogs();
}