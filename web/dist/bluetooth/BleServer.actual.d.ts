export declare type RequestId = number;
export declare let RequestId: NumberConstructor;
export declare class BleResponseStatus {
    readonly value: number;
    private constructor();
    static success: BleResponseStatus;
    static invalidHandle: BleResponseStatus;
    static readNotPermitted: BleResponseStatus;
    static writeNotPermitted: BleResponseStatus;
    static invalidPdu: BleResponseStatus;
    static insufficientAuthentication: BleResponseStatus;
    static requestNotSupported: BleResponseStatus;
    static invalidOffset: BleResponseStatus;
    static insufficientAuthorization: BleResponseStatus;
    static prepareQueueFull: BleResponseStatus;
    static attributeNotFound: BleResponseStatus;
    static attributeNotLong: BleResponseStatus;
    static insufficientEncryptionKeySize: BleResponseStatus;
    static invalidAttributeValueLength: BleResponseStatus;
    static unlikelyError: BleResponseStatus;
    static insufficientEncryption: BleResponseStatus;
    static unsupportedGroupType: BleResponseStatus;
    static insufficientResources: BleResponseStatus;
    private static _values;
    static values(): Array<BleResponseStatus>;
    readonly name: string;
    static valueOf(name: string): BleResponseStatus;
    toString(): string;
}
