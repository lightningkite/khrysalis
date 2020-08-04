import { ViewGenerator } from './ViewGenerator.shared';
import { ObservableStack } from '../observables/ObservableStack.shared';
export interface EntryPoint {
    handleDeepLink(schema: string, host: string, path: string, params: Map<string, string>): void;
    onBackPressed(): boolean;
    readonly mainStack: (ObservableStack<ViewGenerator> | null);
}
export declare namespace EntryPointDefaults {
    function handleDeepLink(this_: EntryPoint, schema: string, host: string, path: string, params: Map<string, string>): void;
    function onBackPressed(this_: EntryPoint): boolean;
    function getMainStack(this_: EntryPoint): (ObservableStack<ViewGenerator> | null);
}
