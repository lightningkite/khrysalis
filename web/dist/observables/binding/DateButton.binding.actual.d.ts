import { TimeAlone } from '../../time/TimeAlone.actual';
import { MutableObservableProperty } from '../MutableObservableProperty.shared';
import { DateAlone } from '../../time/DateAlone.actual';
export declare function xDateButtonBind(this_: HTMLInputElement, date: MutableObservableProperty<Date>): void;
export declare function xTimeButtonBind(this_: HTMLInputElement, date: MutableObservableProperty<Date>, minuteInterval?: number): void;
export declare function xDateButtonBindDateAlone(this_: HTMLInputElement, date: MutableObservableProperty<DateAlone>): void;
export declare function xTimeButtonBindTimeAlone(this_: HTMLInputElement, date: MutableObservableProperty<TimeAlone>, minuteInterval?: number): void;
