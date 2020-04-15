//Package: com.test
//Converted using Khrysalis2


class SillyBox {
    subBox: SillyBox | null;
    
    public constructor(subBox: SillyBox | null = null) {
        this.subBox = subBox;
    }
    
}
 
 

export function main(): void {
    const item = new SillyBox(new SillyBox());
    item.subBox?.subBox?.let{ 
         console.log("I got a box!")
     } ?? run{ 
         console.log("I didn't get a box...")
     };
    const tempVarSet0 = item.subBox?.subBox?.subBox;
    if (tempVarSet0 !== null) {
        tempVarSet0.subBox = new SillyBox()
    }
    ;
    item.subBox?.subBox?.let{ 
         console.log("I got a box!")
     } ?? run{ 
         console.log("I didn't get a box...")
     };
    const tempVarSet1 = item.subBox;
    if (tempVarSet1 !== null) {
        tempVarSet1.subBox = new SillyBox()
    }
    ;
    item.subBox?.subBox?.let{ 
         console.log("I got a box!")
     } ?? run{ 
         console.log("I didn't get a box...")
     }
}
 
