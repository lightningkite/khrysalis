export interface Array<T> {
    add(item: T): void;
}

Array.prototype.add = function (item: any) {
    this.push(item);
};

export interface Number {
    toByte(): number;

    toShort(): number;

    toInt(): number;

    toLong(): number;

    toFloat(): number;

    toDouble(): number;
}

Number.prototype.toByte = function (): number {
    return Math.floor(this)
};
Number.prototype.toShort = Number.prototype.toByte;
Number.prototype.toInt = Number.prototype.toByte;
Number.prototype.toLong = Number.prototype.toByte;
Number.prototype.toFloat = function (): number {
    return this
};
Number.prototype.toDouble = Number.prototype.toFloat;
