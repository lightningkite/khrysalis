Pod::Spec.new do |s|
  s.name = "KhrysalisGoogleMaps"
  s.version = "0.2.0"
  s.summary = "Google Maps for Khrysalis"
  s.description = "Shared code for maps using Khrysalis.  This is the iOS portion."
  s.homepage = "https://github.com/lightningkite/khrysalis"

  s.license = "GPL"
  s.author = { "Captain" => "joseph@lightningkite.com" }
  s.platform = :ios, "11.0"
  s.source = { :git => "https://github.com/lightningkite/khrysalis.git", :tag => "#{s.version}" }
  s.source_files =  "ios-google-maps/KhrysalisGoogleMaps/**/*.swift" # path to your classes. You can drag them into their own folder.

  s.pod_target_xcconfig = { "DEFINES_MODULE" => "YES" }

  s.requires_arc = true
  s.swift_version = '5.3'
  s.xcconfig = { 'SWIFT_VERSION' => '5.3' }
  # Dependency on Khrysalis not representable at the moment
  s.dependency "Khrysalis/Core"
  s.dependency "GoogleMaps"
  s.dependency "GooglePlaces"
end
