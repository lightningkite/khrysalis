Pod::Spec.new do |s|
  s.name = "kwift"
  s.version = "0.1.0"
  s.summary = "Standard library for code translated from Kotlin to Swift"
  s.description = "The Kwift Gradle plugin translates code from Kotlin to Swift, but in order to do so, a set of libraries must be present on both sides.  This is the iOS portion."
  s.homepage = "https://github.com/lightningkite/kwift"
  
  s.license = "Description of your licence, name or otherwise"
  s.author = { "Captain" => "joseph@lightningkite.com" }
  s.platform = :ios, "9.0"
  s.source = { :git => "https://github.com/lightningkite/kwift.git", :tag => "#{s.version}" }
  s.source_files =  "ios/kwift/**/*.swift" # path to your classes. You can drag them into their own folder.
  
  s.requires_arc = true
  s.swift_version = '5.0'
  s.xcconfig = { 'SWIFT_VERSION' => '5.0' }
  s.dependency "Alamofire"
  s.dependency "AlamofireImage"
  s.dependency "KeychainAccess"
  s.dependency "PinLayout"
  s.dependency "FlexLayout"
  s.dependency "UITextView+Placeholder"
  s.dependency "Fabric"
  s.dependency "Crashlytics"
  s.dependency "Cosmos", "~> 19.0"
  s.dependency "QVRWeekView"
end