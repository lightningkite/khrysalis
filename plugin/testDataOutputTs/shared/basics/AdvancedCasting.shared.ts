//Package: com.test
//Converted using Khrysalis2


class AdvancedCastingTestInterface {
    
    public constructor() {
    }
    
}
 

class AdvancedCastingTestImplementation implements AdvancedCastingTestInterface {
    
    public constructor() {
    }
    
}
 
 

export function main(): void {
    let value : any | null = null;
    const asString = ((): string | null => { const _item: any = value; if(typeof _item == "string") return _item; else return null })();
    const asTestInterface = ((): AdvancedCastingTestInterface | null => { const _item: any = value; if (_item.implementsInterfaceAdvancedCastingTestInterface) return _item; else return null })();
    const asTestImplementation = ((): AdvancedCastingTestImplementation | null => { const _item: any = value; if (_item instanceof AdvancedCastingTestImplementation) return _item; else return null })() 
    
}
 
