import {EqualOverrideMap} from "../src/kotlin/Collections"


describe("TestClass", ()=> {
    test("testMap", ()=> {
        const map = new EqualOverrideMap()
        map.set(2, 3)
        expect(map.get(2)).toEqual(3)
    })
})