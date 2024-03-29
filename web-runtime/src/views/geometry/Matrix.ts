
export function matrixReset(matrix: DOMMatrix){
    matrix.m11 = 1
    matrix.m12 = 0
    matrix.m13 = 0
    matrix.m14 = 0
    matrix.m21 = 0
    matrix.m22 = 1
    matrix.m23 = 0
    matrix.m24 = 0
    matrix.m31 = 0
    matrix.m32 = 0
    matrix.m33 = 1
    matrix.m34 = 0
    matrix.m41 = 0
    matrix.m42 = 0
    matrix.m43 = 0
    matrix.m44 = 1
}

export function matrixSet(matrix: DOMMatrix, to: DOMMatrix){
    matrix.m11 = to.m11
    matrix.m12 = to.m12
    matrix.m13 = to.m13
    matrix.m14 = to.m14
    matrix.m21 = to.m21
    matrix.m22 = to.m22
    matrix.m23 = to.m23
    matrix.m24 = to.m24
    matrix.m31 = to.m31
    matrix.m32 = to.m32
    matrix.m33 = to.m33
    matrix.m34 = to.m34
    matrix.m41 = to.m41
    matrix.m42 = to.m42
    matrix.m43 = to.m43
    matrix.m44 = to.m44
}
