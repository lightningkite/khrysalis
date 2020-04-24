//Package: com.test
//Converted using Khrysalis2


interface AdvancedCastingTestInterface {
    implementsInterfaceAdvancedCastingTestInterface: boolean;
}


class AdvancedCastingTestImplementation {
    
    public constructor() {
    }
    
}

 

export function main(): void {
    let value : any | null = null;
    const asString = ((): string | null => { const _item: any = value; if(typeof _item == "string") return _item; else return null })();
    const asTestInterface = ((): AdvancedCastingTestInterface | null => { const _item: any = value; if ((_item as any).implementsInterfaceAdvancedCastingTestInterface) return _item as AdvancedCastingTestInterface; else return null })();
    const asTestImplementation = ((): AdvancedCastingTestImplementation | null => { const _item: any = value; if (_item instanceof AdvancedCastingTestImplementation) return _item; else return null })() 
    
}

