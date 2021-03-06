// Generated by Khrysalis TypeScript converter - this file will be overwritten.
// File: testNulls.shared.kt
// Package: com.test.nulls
import { runOrNull } from 'butterfly-web/dist/kotlin/Language'

//! Declares com.test.nulls.WebSocketFrame
export class WebSocketFrame {
    public readonly binary: (ByteArray | null);
    public readonly text: (String | null);
    public constructor(binary: (ByteArray | null) = null, text: (String | null) = null) {
        this.binary = binary;
        this.text = text;
    }
    
    public toString(): String {
        return this.text ?? ((): (String | null) => {
                const temp22 = this.binary;
                if(temp22 !== null) {
                    return xByteArrayToString(temp22, Charsets.INSTANCE.UTF_8)
                } else { return null }
        })() ?? "<Empty Frame>";
    }
}

//! Declares com.test.nulls.test
export function test(): void {
    const frame = new WebSocketFrame(undefined, "asdf");
    
    const maybeFrame = frame.binary !== null ? frame : null;
    
    const it_23 = (maybeFrame?.text ?? null);
    if (it_23 !== null) { 
        println(it_23);
    }
}
