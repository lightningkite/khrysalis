# To Do List

## Short Term

- [X] Android Video Thumbnail Generation Async
- [X] iOS Video Player
    - [X] Layout
    - [X] Binding
- [X] iOS Video Thumbnail Generation
- [X] iOS list groupBy
- [X] iOS string filter
- [ ] iOS Google Places Autocomplete (Brady)
- [X] iOS Horizontal Progress Bar
- [X] iOS Image toHttpBody
- [X] iOS Uri toHttpBody
- [ ] Http call with progress?

## Long Term

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