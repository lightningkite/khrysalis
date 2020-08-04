export declare class WebSocketFrame {
    readonly binary: (Int8Array | null);
    readonly text: (string | null);
    constructor(binary?: (Int8Array | null), text?: (string | null));
    toString(): string;
}
