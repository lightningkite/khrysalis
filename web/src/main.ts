import {ViewGenerator} from "./views/ViewGenerator.shared";
import {listenForDialogs} from "./views/ViewDependency.actual";
import {HttpClient} from "./net/HttpClient.actual";
import {asyncScheduler} from "rxjs";

export function main(rootVg: ViewGenerator){
    HttpClient.INSTANCE.ioScheduler = asyncScheduler
    HttpClient.INSTANCE.responseScheduler = asyncScheduler
    const view = rootVg.generate(window);
    document.body.appendChild(view);
    listenForDialogs();
}