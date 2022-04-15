# UnlockPremium

[![License](https://img.shields.io/github/license/GroupeMINASTE/UnlockPremium)](LICENSE)
[![Issues](https://img.shields.io/github/issues/GroupeMINASTE/UnlockPremium)]()
[![Pull Requests](https://img.shields.io/github/issues-pr/GroupeMINASTE/UnlockPremium)]()
[![Code Size](https://img.shields.io/github/languages/code-size/GroupeMINASTE/UnlockPremium)]()

## Installation

### iOS

Add `https://github.com/GroupeMINASTE/UnlockPremium.git` to your Swift Package configuration (or using the Xcode menu: `File` > `Swift Packages` > `Add Package Dependency`)

Don't forget to add the `In-App Purchase` capability to your app (in `Signing & Capabilities`). This should automatically add the `StoreKit` framework to your app as well.

### Android

Add the following to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'me.nathanfallet.unlockpremium:unlockpremium:1.0.0'
}
```

## Usage

### iOS

Setup a configuration for the unlock view:

```swift
import UnlockPremium

extension UnlockPremiumConfig {

    static func config() -> UnlockPremiumConfig {
        return UnlockPremiumConfig(
            arguments: [
                PremiumArgument(
                    title: "A feature name",
                    description: "A feature description",
                    icon: "app.fill"
                ),
                // ...
            ],
            productIdentifier: "myAppSKU.premiumPurchase", // In-App Purchase `Product ID`
            completionHandler: {
                // Set your user as premium, for example:
                UserService.shared.setUserPremium(to: true)
            }
        )
    }

}
```

The `completionHandler` is the method called when the purchase completes successfully.

Then, show the view where you want:

```swift
.sheet(isPresented: $viewModel.showPremium) {
    UnlockPremiumView(configuration: .config(), isPresented: $viewModel.showPremium)
}
```

### Android

Setup a configuration for the unlock view:

```kotlin
val config = UnlockPremiumConfig(
    listOf(
        PremiumArgument(
            "A feature name",
            "A feature description",
            R.drawable.ic_baseline_apps_24
        ),
        // ...
    ),
    "myAppSKU.premiumPurchase"
)
```

Create a request, and handle the response

```kotlin
private val unlockPremiumRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
    it.data?.getBooleanExtra(UnlockPremiumActivity.EXTRAS.SUCCESS, false)?.let { success ->
        if (success) {
            // Set your user as premium, for example:
            UserService.getInstance(getApplication()).setUserPremium(true)
        }
    }
}
```

Then, show the view where you want:

```kotlin
val intent = Intent(this, UnlockPremiumActivity::class.java)
intent.putExtra(UnlockPremiumActivity.EXTRAS.CONFIGURATION, config)
unlockPremiumRequest.launch(intent)
```
