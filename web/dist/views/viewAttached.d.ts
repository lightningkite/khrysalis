export declare const attachmentEventSymbol: unique symbol;
export declare function isAttached(view: Node): boolean;
export declare function addAttachmentEvent(view: Node, action: (view: Node) => void): void;
export declare function triggerAttachmentEvent(view: Node): void;
