//Package: com.test
//Converted using Khrysalis2


export function main(): void {
    let value : any | null = null;
    const asString = ((): string | null => { const _item: any = value; if(typeof _item == "string") return _item; else return null })();
    value = "XP";
    const forced = value as string;
    console.log(value ?? "Nope");
    console.log(forced)
}
 
