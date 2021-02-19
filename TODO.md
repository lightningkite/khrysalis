# To Do List

These are the upcoming objectives for development:

## Documentation

- [ ] Khrysalis website
    - [ ] Searchable, testable examples
    - [ ] Try it out online directly
- [ ] Example Project of some kind

## Testing

- [ ] Unit testing for code translations
- [ ] Unit testing for layout translations, if at all possible
- [ ] Unit testing for observable properties

## Layout Translation

- [ ] A new layout translation system based on `.yaml` files
    - [ ] iOS
    - [ ] Web
- [ ] Use CSS classes for styling in more places
- [ ] Use some kind of equivalent for iOS if possible
    
## Better Errors

- [ ] Error on cross-return
- [ ] Error on control statement as expression
- [ ] Error on return in expression-control

## Cleaner Code Translations

- [ ] Shorter names for JS extensions
- [ ] Use YAML replacements in more Swift situations and remove more library parts
- [ ] Look into using the various Rx UI libraries on each platform

# Known issues

- [ ] iOS string isNullOrEmpty
- [ ] `-=` operator issue

        onDelete = {
            this.invitations.value -= observable.value
        }
        produces
        onDelete: { () -> Void in let temp869 = self.invitations
                        temp869.value = self.invitations.value.minus(self.invitations.value, element: observable.value) }
        which gives an error “too many arguments” because of self.invitations.value being in minus function.
        I changed it to
        onDelete = {
            this.invitations.value = this.invitations.value.minus(element = observable.value)
        }
        and it produced
        onDelete: { () -> Void in self.invitations.value = self.invitations.value.minus(element: observable.value) }
        which didn’t produce any errors

- [ ] Static imports are not supported





# Other Khrysalis Items

- Quality
  - Upload library to all platforms
    - Maven, 4h
    - Cocoapods, 4h
    - NPM, 4h
  - Unit test code translation, 20h
  - Continuous integration with Code Coverage, 20h
  - iOS storyboard UI, 40h
  - Web - make lighter-weight, ?h
  - Web Metadata, 5h
  - Web Server-side rendering (?)
  - Pure RX style using Rx library bindings
  - Jetpack Compose / SwiftUI
- Advertising
  - Get website up and running
    - Tutorials
  - Advertise where the devs are
  - Write blog post about language conversions
  - Create open-source simple demo apps
    - Life organizer app
    - Bluetooth Cards app