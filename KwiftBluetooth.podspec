Pod::Spec.new do |s|
  s.name = "KwiftBluetooth"
  s.version = "0.1.0"
  s.summary = "Bluetooth for Kwift"
  s.description = "Shared code for bluetooth using Kwift.  This is the iOS portion."
  s.homepage = "https://github.com/lightningkite/kwift"

  s.license = "GPL"
  s.author = { "Captain" => "joseph@lightningkite.com" }
  s.platform = :ios, "11.0"
  s.source = { :git => "https://github.com/lightningkite/kwift.git", :tag => "#{s.version}" }
  s.source_files =  "ios-bluetooth/KwiftBluetooth/**/*.swift" # path to your classes. You can drag them into their own folder.

  s.pod_target_xcconfig = { "DEFINES_MODULE" => "YES" }

  s.requires_arc = true
  s.swift_version = '5.0'
  s.xcconfig = { 'SWIFT_VERSION' => '5.0' }
  # Dependency on Kwift not representable at the moment
  s.dependency "Kwift"
  s.dependency "RxBluetoothKit"
end