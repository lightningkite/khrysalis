Pod::Spec.new do |s|
  s.name = "Khrysalis"
  s.version = "0.2.0"
  s.summary = "Standard library for code translated from Kotlin to Swift"
  s.description = "The Khrysalis Gradle plugin translates code from Kotlin to Swift, but in order to do so, a set of libraries must be present on both sides.  This is the iOS portion."
  s.homepage = "https://github.com/lightningkite/khrysalis"

  s.license = "GPL"
  s.author = { "Captain" => "joseph@lightningkite.com" }
  s.platform = :ios, "11.0"
  s.source = { :git => "https://github.com/lightningkite/khrysalis.git", :tag => "#{s.version}" }
  s.source_files =  "ios/Khrysalis/**/*.swift" # path to your classes. You can drag them into their own folder.

  s.pod_target_xcconfig = { "DEFINES_MODULE" => "YES" }

  s.requires_arc = true
  s.swift_version = '5.0'
  s.xcconfig = { 'SWIFT_VERSION' => '5.0' }
  s.dependency "Alamofire", "~> 4.9.1"
  s.dependency "AlamofireImage", "~> 3.6.0"
  s.dependency "KeychainAccess"
  s.dependency "UITextView+Placeholder"
  s.dependency "Cosmos", "~> 19.0"
  s.dependency "SearchTextField"
  s.dependency "RxSwift"
  s.dependency "RxRelay"
  s.dependency "Starscream"
  s.dependency "DKImagePickerController/Core"
  s.dependency "DKImagePickerController/ImageDataManager"
  s.dependency "DKImagePickerController/Resource"
end
