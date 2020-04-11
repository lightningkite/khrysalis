//Package: com.test
//Converted using Khrysalis2


dataclassSillyBox(varsubBox:SillyBox | null=null)


export function main(): void {
    valitem=SillyBox(SillyBox())
    item.subBox?.subBox?.let{
        println("I got a box!")
    }??run{
        println("I didn't get a box...")
    }
    
    item.subBox?.subBox?.subBox?.subBox=SillyBox()
    item.subBox?.subBox?.let{
        println("I got a box!")
    }??run{
        println("I didn't get a box...")
    }
    
    item.subBox?.subBox=SillyBox()
    item.subBox?.subBox?.let{
        println("I got a box!")
    }??run{
        println("I didn't get a box...")
    }
}

