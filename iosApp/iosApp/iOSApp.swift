import SwiftUI
import GoogleMaps

@main
struct iOSApp: App {
    init() {
        if let apiKey = Bundle.main.object(forInfoDictionaryKey: "GOOGLE_MAPS_API_KEY") as? String,
           !apiKey.isEmpty, apiKey != "$(GOOGLE_MAPS_API_KEY)" {
            GMSServices.provideAPIKey(apiKey)
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
