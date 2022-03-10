Pod::Spec.new do |s|
  s.name             = 'KhrysalisRuntime'
  s.version          = '1.0.0-RC1'
  s.summary          = 'Used in post-Khrysalis conversion Swift code.'

  s.description      = <<-DESC
A bunch of extension functions and declarations that assist in converting Kotlin to Swift.
                       DESC

  s.homepage         = 'https://github.com/lightningkite/khrysalis'
  # s.screenshots     = 'www.example.com/screenshots_1', 'www.example.com/screenshots_2'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'Joseph' => 'joseph@lightningkite.com' }
  s.source           = { :git => 'https://github.com/lightningkite/khrysalis.git', :tag => s.version.to_s }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.ios.deployment_target = '9.0'

  s.source_files = 'ios-runtime/KhrysalisRuntime/Classes/**/*'
end
