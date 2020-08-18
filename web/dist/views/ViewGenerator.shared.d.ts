import { ViewString } from './Strings.shared';
export declare abstract class ViewGenerator {
    protected constructor();
    get title(): string;
    get titleString(): ViewString;
    abstract generate(dependency: Window): HTMLElement;
}
export declare namespace ViewGenerator {
    class Default extends ViewGenerator {
        constructor();
        generate(dependency: Window): HTMLElement;
    }
}
