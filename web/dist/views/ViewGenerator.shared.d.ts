export declare abstract class ViewGenerator {
    abstract readonly title: string;
    abstract generate(dependency: Window): HTMLElement;
    static Default: {
        new (): {
            readonly title: string;
            generate(dependency: Window): HTMLElement;
        };
        Default: any;
    };
}
