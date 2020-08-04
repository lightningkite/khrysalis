export declare abstract class ViewGenerator {
    protected constructor();
    abstract readonly title: string;
    abstract generate(dependency: Window): HTMLElement;
}
export declare namespace ViewGenerator {
    class Default extends ViewGenerator {
        constructor();
        get title(): string;
        generate(dependency: Window): HTMLElement;
    }
}
