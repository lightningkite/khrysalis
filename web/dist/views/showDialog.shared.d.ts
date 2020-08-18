import { ViewString } from './Strings.shared';
import { StandardObservableProperty } from '../observables/StandardObservableProperty.shared';
import { Subject } from 'rxjs';
export declare const _lastDialog: StandardObservableProperty<DialogRequest | null>;
export declare function getLastDialog(): StandardObservableProperty<(DialogRequest | null)>;
export declare const _showDialogEvent: Subject<DialogRequest>;
export declare function getShowDialogEvent(): Subject<DialogRequest>;
export declare class DialogRequest {
    readonly _string: ViewString;
    readonly confirmation: ((() => void) | null);
    constructor(_string: ViewString, confirmation?: ((() => void) | null));
}
export declare function showDialog(request: DialogRequest): void;
export declare function showDialogAlert(message: ViewString): void;
