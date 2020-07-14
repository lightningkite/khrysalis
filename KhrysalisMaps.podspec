Pod::Spec.new do |s|
  s.name = "KhrysalisMaps"
  s.version = "0.2.0"
  s.summary = "Maps for Khrysalis"
  s.description = "Shared code for maps using Khrysalis.  This is the iOS portion."
  s.homepage = "https://github.com/lightningkite/khrysalis"

  s.license = "GPL"
  s.author = { "Captain" => "joseph@lightningkite.com" }
  s.platform = :ios, "11.0"
  s.source = { :git => "https://github.com/lightningkite/khrysalis.git", :tag => "#{s.version}" }
  s.source_files =  "ios-maps/KhrysalisMaps/**/*.swift" # path to your classes. You can drag them into their own folder.

  s.pod_target_xcconfig = { "DEFINES_MODULE" => "YES" }

  s.requires_arc = true
  s.swift_version = '5.0'
  s.xcconfig = { 'SWIFT_VERSION' => '5.0' }
  # Dependency on Khrysalis not representable at the moment
  s.dependency "Khrysalis"
end
