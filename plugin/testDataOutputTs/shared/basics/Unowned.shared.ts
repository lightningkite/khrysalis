//Package: com.test
//Converted using Khrysalis2


class Thing {
    
    public constructor(input: any) {
    }
    
    public thing : any;
    public lambda : () => void;
}

 

class DummyObject {
    
    public constructor() {
    }
    
}

 

export function main(): void {
    (new Thing(new DummyObject())).lambda()
}

