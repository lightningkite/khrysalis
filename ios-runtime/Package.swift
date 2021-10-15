// swift-tools-version:5.1
import PackageDescription

let package = Package(
    name: "KhrysalisRuntime",
    platforms: [
        .iOS(.v11),
    ],
    products: [
        .library(
            name: "KhrysalisRuntime",
            targets: ["KhrysalisRuntime"]),
    ],
    dependencies: [
            .package()
//         .package(url: "https://github.com/lightningkite/butterfly-ios", from: "0.0.0"),
    ],
    targets: [
        .target(
            name: "KhrysalisRuntime",
            dependencies: []),
        .testTarget(
            name: "KhrysalisRuntimeTests",
            dependencies: ["KhrysalisRuntime"]),
    ]
)