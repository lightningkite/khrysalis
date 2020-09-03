export interface ViewString {
    get(dependency: Window): string;
}
export declare class ViewStringRaw implements ViewString {
    static implementsInterfaceComLightningkiteKhrysalisViewsViewString: boolean;
    readonly _string: string;
    constructor(_string: string);
    get(dependency: Window): string;
}
export declare class ViewStringResource implements ViewString {
    static implementsInterfaceComLightningkiteKhrysalisViewsViewString: boolean;
    readonly resource: string;
    constructor(resource: string);
    get(dependency: Window): string;
}
export declare class ViewStringTemplate implements ViewString {
    static implementsInterfaceComLightningkiteKhrysalisViewsViewString: boolean;
    readonly template: ViewString;
    readonly _arguments: Array<any>;
    constructor(template: ViewString, _arguments: Array<any>);
    get(dependency: Window): string;
}
export declare class ViewStringComplex implements ViewString {
    static implementsInterfaceComLightningkiteKhrysalisViewsViewString: boolean;
    readonly getter: ((a: Window) => string);
    constructor(getter: ((a: Window) => string));
    get(dependency: Window): string;
}
export declare class ViewStringList implements ViewString {
    static implementsInterfaceComLightningkiteKhrysalisViewsViewString: boolean;
    readonly parts: Array<ViewString>;
    readonly separator: string;
    constructor(parts: Array<ViewString>, separator?: string);
    get(dependency: Window): string;
}
export declare function xListJoinToViewString(this_: Array<ViewString>, separator?: string): ViewString;
export declare function xViewStringToDebugString(this_: ViewString): string;
