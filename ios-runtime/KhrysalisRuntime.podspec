Pod::Spec.new do |s|
  s.name = "KhrysalisRuntime"
  s.version = "0.2.0"
  s.summary = "Standard library for code translated from Kotlin to Swift"
  s.description = "The Khrysalis Gradle plugin translates code from Kotlin to Swift, but in order to do so, a set of libraries must be present on both sides.  This is the iOS portion."
  s.homepage = "https://github.com/lightningkite/khrysalis"

  s.license = "MIT"
  s.author = { "Captain" => "joseph@lightningkite.com" }
  s.platform = :ios, "11.0"
  s.source = { :git => "https://github.com/lightningkite/khrysalis.git", :tag => "#{s.version}", :submodules => true }
  s.source_files =  "KhrysalisRuntime/**/*.{swift,swift.yml,swift.yaml}"

  s.pod_target_xcconfig = { "DEFINES_MODULE" => "YES" }

  s.requires_arc = true
  s.swift_version = '5.3'
  s.xcconfig = { 'SWIFT_VERSION' => '5.3' }
  s.dependency "Alamofire"
  s.dependency "AlamofireImage"
  s.dependency "KeychainAccess"
  s.dependency "UITextView+Placeholder"
  s.dependency "Cosmos"
  s.dependency "SearchTextField"
  s.dependency "RxSwift", "5.1.1" 
  s.dependency "RxRelay", "5.1.1"
  s.dependency "Starscream"
  s.dependency "IBPCollectionViewCompositionalLayout"

#   s.subspec 'Core' do |core|
#     core.source_files =  "KhrysalisRuntime/src/**/*.{swift,swift.yml,swift.yaml}"
#   end
#   s.subspec 'Images' do |images|
#     images.source_files =  "KhrysalisRuntime/srcImages/**/*.{swift,swift.yml,swift.yaml}"
#     images.dependency "KhrysalisRuntime/Core"
#     images.dependency "DKImagePickerController/Core"
#     images.dependency "DKImagePickerController/ImageDataManager"
#     images.dependency "DKImagePickerController/Resource"
#     images.dependency "DKImagePickerController/Camera"
#   end
#   s.subspec 'Calendar' do |calendar|
#     calendar.dependency "KhrysalisRuntime/Core"
#     calendar.source_files =  "KhrysalisRuntime/srcCalendar/**/*.{swift,swift.yml,swift.yaml}"
#   end
#   s.subspec 'Location' do |location|
#     location.dependency "KhrysalisRuntime/Core"
#     location.source_files =  "KhrysalisRuntime/srcLocation/**/*.{swift,swift.yml,swift.yaml}"
#   end
end
