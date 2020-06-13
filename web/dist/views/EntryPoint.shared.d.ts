import { ViewGenerator } from './ViewGenerator.shared';
import { ObservableStack } from '../observables/ObservableStack.shared';
export interface EntryPoint {
    handleDeepLink(schema: string, host: string, path: string, params: Map<string, string>): void;
    onBackPressed(): boolean;
    readonly mainStack: (ObservableStack<ViewGenerator> | null);
}
export declare class EntryPointDefaults {
    static handleDeepLink(this_: EntryPoint, schema: string, host: string, path: string, params: Map<string, string>): void;
    static onBackPressed(this_: EntryPoint): boolean;
    static getMainStack(this_: EntryPoint): (ObservableStack<ViewGenerator> | null);
}
