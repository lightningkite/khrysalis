export enum Platform {
    Android, Ios, Web
}

export namespace Platform {
    export const Companion = Platform
    export const INSTANCE = Platform
    export const current:Platform = Platform.Web
}