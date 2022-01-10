export function colorValue(int: number): string {
    const converted = int.toString(16).padStart(8, '0')
    return '#' + converted.substring(2) + converted.substring(0, 2)
}