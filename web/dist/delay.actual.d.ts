import { Subject } from 'rxjs';
import { ObservableProperty } from "./observables/ObservableProperty.shared";
export declare function delay(milliseconds: number, action: () => void): void;
export declare function post(action: () => void): void;
export declare const _animationFrame: Subject<number>;
export declare function getAnimationFrame(): Subject<number>;
export declare function getApplicationIsActive(): ObservableProperty<boolean>;
