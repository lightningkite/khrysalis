
import './main.scss'
import * as html from './layouts/margin_test.html'

console.log("Hello world!");
console.log(html);
// console.log(require('./layouts/margin_test.html'));
let root = document.getElementById("app");
root.innerHTML = html;
(root.firstElementChild as HTMLElement).style.width = "100%";
(root.firstElementChild as HTMLElement).style.height = "100%";
// root?.appendChild(require("./layouts/margin_test.html"));
// document.replaceChild(document.createRange().createContextualFragment(html), root);
