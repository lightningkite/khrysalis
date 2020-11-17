import {RootVG} from "./vg/RootVG.shared";
import {listenForDialogs} from "butterfly/dist/views/ViewDependency.actual";

const vg = new RootVG();
const view = vg.generate(window);
document.body.appendChild(view);
listenForDialogs();