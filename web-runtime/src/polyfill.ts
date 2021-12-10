export {}

declare global {
    interface String {
        replaceAll(replacing: string | RegExp, withString: string): string
    }
}
if(!String.prototype.replaceAll){
    String.prototype.replaceAll = function(this: string, replacing: string | RegExp, withString: string): string {
        if (typeof replacing === "string") {
            return this.replace(new RegExp(replacing.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'), "g"), withString)
        } else {
            return this.replace(replacing, withString)
        }
    }
}