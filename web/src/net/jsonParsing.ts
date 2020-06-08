export function parse(item: any, asType: Array<any>): any {
    const mainType = asType[0];
    switch (mainType) {
        case null:
        case String:
        case Number:
        case Boolean:
            return item;
        case Array:
            return (item as Array<any>).map(x => parse(x, asType[1]));
        case Map:
            let asObj = item as object;
            let map = new Map<any, any>();
            if (asType[1] === String) {
                for (const key in Object.keys(asObj)) {
                    map.set(key, parse((asObj as any)[key], asType[2]))
                }
            } else {
                for (const key in Object.keys(asObj)) {
                    map.set(parse(key, asType[1]), parse((asObj as any)[key], asType[2]))
                }
            }
            return map;
        default:
            return mainType.fromJson(item, ...asType.slice(1))
    }
}