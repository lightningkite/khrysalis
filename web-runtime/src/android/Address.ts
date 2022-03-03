export interface Address {
    latitude?: number
    longitude?: number

    countryName?: string
    countryCode?: string
    adminArea?: string  // AKA State
    subAdminArea?: string  // AKA County
    locality?: string  // AKA City
    subLocality?: string  // AKA City Section
    thoroughfare?: string  // AKA Number + Street
    subThoroughfare?: string  // AKA Suite number?

    featureName?: string
    phone?: string
    postalCode?: string
    premises?: string
}