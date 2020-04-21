function KotlinAnyApply<T>(thing: T, action: (T)=>void) {
    action(thing);
    return thing;
}
function KotlinAnyLet<T, R>(thing: T, action: (T)=>R) {
    return action(thing);
}
