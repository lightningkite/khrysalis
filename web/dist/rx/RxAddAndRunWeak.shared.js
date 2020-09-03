"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
//! Declares com.lightningkite.khrysalis.rx.solvePrivateDisposal
// export function xDisposableSolvePrivateDisposal(this_: SubscriptionLike, items: Array<any>): void{
//     for (const item of items) {
//         if(item instanceof View){
//             xDisposableUntil(this_, xViewRemovedGet(item));
//         }
//     }
// }
//! Declares com.lightningkite.khrysalis.rx.add
function xObservableAdd(this_, listener) {
    let disposable = null;
    const disp = this_.subscribe((item) => {
        if (listener(item)) {
            const temp143 = disposable;
            if (temp143 !== null)
                temp143.unsubscribe();
        }
    }, undefined, undefined);
    disposable = disp;
    return disp;
}
exports.xObservableAdd = xObservableAdd;
//! Declares com.lightningkite.khrysalis.rx.addWeak
// export function xObservableAddWeak<A extends object, Element extends any>(this_: Observable<Element>, referenceA: A, listener:  (a: A, b: Element) => void): SubscriptionLike{
//     let disposable: (SubscriptionLike | null) = null;
//
//     const weakA: (A | null);
//
//     const disp = this_.subscribe((item) => {
//             const a = weakA;
//
//             if (!(a.equals(null))) {
//                 listener(a, item);
//             } else {
//                 const temp145 = disposable;
//                 if(temp145 !== null) temp145.unsubscribe();
//             }
//     }, undefined, undefined);
//
//     disposable = disp;
//     xDisposableSolvePrivateDisposal(disp, [referenceA]);
//     return disp;
// }
//! Declares com.lightningkite.khrysalis.rx.addWeak
// export function xObservableAddWeak<A extends object, B extends object, Element extends any>(this_: Observable<Element>, referenceA: A, referenceB: B, listener:  (a: A, b: B, c: Element) => void): SubscriptionLike{
//     let disposable: (SubscriptionLike | null) = null;
//
//     const weakA: (A | null);
//
//     const weakB: (B | null);
//
//     const disp = this_.subscribe((item) => {
//             const a = weakA;
//
//             const b = weakB;
//
//             if (!(a.equals(null)) && !(b.equals(null))) {
//                 listener(a, b, item);
//             } else {
//                 const temp147 = disposable;
//                 if(temp147 !== null) temp147.unsubscribe();
//             }
//     }, undefined, undefined);
//
//     disposable = disp;
//     xDisposableSolvePrivateDisposal(disp, [referenceA, referenceB]);
//     return disp;
// }
//! Declares com.lightningkite.khrysalis.rx.addWeak
// export function xObservableAddWeak<A extends object, B extends object, C extends object, Element extends any>(this_: Observable<Element>, referenceA: A, referenceB: B, referenceC: C, listener:  (a: A, b: B, c: C, d: Element) => void): SubscriptionLike{
//     let disposable: (SubscriptionLike | null) = null;
//
//     const weakA: (A | null);
//
//     const weakB: (B | null);
//
//     const weakC: (C | null);
//
//     const disp = this_.subscribe((item) => {
//             const a = weakA;
//
//             const b = weakB;
//
//             const c = weakC;
//
//             if (!(a.equals(null)) && !(b.equals(null)) && !(c.equals(null))) {
//                 listener(a, b, c, item);
//             } else {
//                 const temp149 = disposable;
//                 if(temp149 !== null) temp149.unsubscribe();
//             }
//     }, undefined, undefined);
//
//     xDisposableSolvePrivateDisposal(disp, [referenceA, referenceB, referenceC]);
//     disposable = disp;
//     return disp;
// }
//
//# sourceMappingURL=RxAddAndRunWeak.shared.js.map