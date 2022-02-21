import {DataClass, setUpDataClass, JSON2, EqualOverrideMap, ReifiedType} from "../src";


describe("TestClass", ()=> {
    test("testMap", ()=> {
        const map = new EqualOverrideMap()
        map.set(2, 3)
        expect(map.get(2)).toEqual(3)
    })
})

class Superclass {
    equals(other: any): boolean { return other instanceof Superclass }
    hashCode(): number { return 0 }
}
class Point<T> extends Superclass implements DataClass {
    public static properties = ["x", "y"]
    public static propertyTypes = (T: any): Record<string, ReifiedType> => ({x: [Number], y: [Number]})
    public constructor(public x: number, public y: number) { super() }
    copy: (values: Partial<this>) => this;
    equals: (other: any) => boolean;
    hashCode: () => number;
    toJSON: () => Record<string, any>;
    public static fromJSON: (record: Record<string, any>, innerType: Array<any>)=>Point<any>;
}
setUpDataClass(Point)

describe("TestDataClass", ()=> {
    test("equals", ()=> {
        const point1 = new Point(23, 31)
        const point2 = new Point(23, 31)
        const point3 = new Point(24, 31)
        expect(point1.equals(point1)).toEqual(true)
        expect(point1.equals(point2)).toEqual(true)
        expect(point1.equals(point3)).toEqual(false)
        expect(new Superclass().equals(point1)).toEqual(true)
        expect(point1.equals(new Superclass())).toEqual(false)
    })
    test("hashCode", ()=> {
        const point = new Point(32, 12)
        console.log(point.hashCode())
    })
    test("toString", ()=> {
        const point = new Point(32, 12)
        expect(point.toString()).toEqual("Point(x=32, y=12)")
    })
    test("copy", ()=> {
        const point = new Point(32, 12)
        const copy = point.copy({y: 24})
        const expected = new Point(32, 24)
        expect(copy).toBeInstanceOf(Point)
        expect(copy.equals(expected)).toEqual(true)
    })
    test("toJSON", ()=> {
        const point = new Point(32, 12)
        expect(JSON.stringify(point)).toEqual('{"x":32,"y":12}')
    })
    test("fromJSON", ()=> {
        const parsed = JSON2.parse('{"x":32,"y":12}', [Point])
        expect(parsed).toBeInstanceOf(Point)
        expect(parsed).toEqual(new Point(32, 12))
    })
})