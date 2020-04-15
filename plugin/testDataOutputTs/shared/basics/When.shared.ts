//Package: com.test
//Converted using Khrysalis2


export function main(): void {
    let aNumber : number = (0);
    let value = (43).toInt();
    switch(value) {
        case (1):
        aNumber += (1);
        break;
        case (2):
        aNumber += (2);
        aNumber += (3);
        break;
        case (43):
        aNumber += (4);
        break;
        default:
        aNumber -= (99);
        break;
    };
    if (value == (42)) {
        aNumber += (8);
    }else if (value == (41)) {
        aNumber += (16);
    }else if (value == (43) || value == (44)) {
        aNumber += (32);
    }else { 
    };
    console.log(aNumber);
    aNumber = (() => {
            if (value == (42)){
                return (8);
            }else if (value == (41)){
                return (16);
            }else if (value == (43) || value == (44)){
                return (32);
            }else {
                return (0);
            }
    })();
    console.log(aNumber);
    console.log((() => {
                switch(value) {
                    case (0):
                    return "Hi";
                    default:
                    return "Nope";
                }
    })())
}

