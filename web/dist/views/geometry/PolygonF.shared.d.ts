import { PointF } from './PointF.actual';
export declare class PolygonF {
    readonly points: Array<PointF>;
    constructor(points: Array<PointF>);
    hashCode(): number;
    equals(other: any): boolean;
    toString(): string;
    copy(points?: Array<PointF>): PolygonF;
    contains(point: PointF): boolean;
}
