# Cordova Android Insets Plugin

A Cordova plugin that provides access to Android window insets (safe areas) for edge-to-edge app experiences. This plugin helps you handle system UI elements like status bars and navigation bars when building immersive Android applications.

## Features

- 🎯 Get accurate window insets for Android devices
- 📱 Support for edge-to-edge design patterns
- 🔧 Compatible with Android API levels 21+ with fallback support
- 📏 Returns inset values in density-independent pixels (dp)
- ⚡ Lightweight and easy to integrate

## Installation

Install the plugin using the Cordova CLI:

```bash
cordova plugin add cordova-plugin-android-insets
```

Or install from GitHub:

```bash
cordova plugin add https://github.com/MaazKhaleeq/cordova-plugin-android-insets.git
```

## Usage

### Basic Usage

```javascript
// Get window insets
cordova.plugins.InsetsPlugin.getInsets(
    function(insets) {
        console.log('Top inset (status bar):', insets.top + 'dp');
        console.log('Bottom inset (navigation bar):', insets.bottom + 'dp');
        console.log('Left inset:', insets.left + 'dp');
        console.log('Right inset:', insets.right + 'dp');
    },
    function(error) {
        console.error('Error getting insets:', error);
    }
);
```

### Applying CSS Padding

Use the inset values to add appropriate padding to your UI elements:

```javascript
function applyInsetPadding() {
    cordova.plugins.InsetsPlugin.getInsets(
        function(insets) {
            // Apply padding to your main container
            const mainContainer = document.getElementById('main-container');
            mainContainer.style.paddingTop = insets.top + 'px';
            mainContainer.style.paddingBottom = insets.bottom + 'px';
            mainContainer.style.paddingLeft = insets.left + 'px';
            mainContainer.style.paddingRight = insets.right + 'px';
            
            // Or use CSS custom properties
            document.documentElement.style.setProperty('--inset-top', insets.top + 'px');
            document.documentElement.style.setProperty('--inset-bottom', insets.bottom + 'px');
            document.documentElement.style.setProperty('--inset-left', insets.left + 'px');
            document.documentElement.style.setProperty('--inset-right', insets.right + 'px');
        },
        function(error) {
            console.error('Failed to get insets:', error);
        }
    );
}

// Call when device is ready
document.addEventListener('deviceready', applyInsetPadding, false);
```

### CSS Implementation

You can use the inset values with CSS custom properties:

```css
:root {
    --inset-top: 0px;
    --inset-bottom: 0px;
    --inset-left: 0px;
    --inset-right: 0px;
}

.safe-area-container {
    padding-top: var(--inset-top);
    padding-bottom: var(--inset-bottom);
    padding-left: var(--inset-left);
    padding-right: var(--inset-right);
}

/* For fixed headers */
.header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    padding-top: var(--inset-top);
    background: #ffffff;
    z-index: 1000;
}

/* For fixed footers */
.footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    padding-bottom: var(--inset-bottom);
    background: #ffffff;
}
```

### Advanced Usage with Orientation Changes

Handle orientation changes and dynamic inset updates:

```javascript
function updateInsets() {
    cordova.plugins.InsetsPlugin.getInsets(
        function(insets) {
            // Update CSS custom properties
            const root = document.documentElement;
            root.style.setProperty('--inset-top', insets.top + 'px');
            root.style.setProperty('--inset-bottom', insets.bottom + 'px');
            root.style.setProperty('--inset-left', insets.left + 'px');
            root.style.setProperty('--inset-right', insets.right + 'px');
            
            // Dispatch custom event for components that need to react
            const event = new CustomEvent('insetsChanged', { detail: insets });
            document.dispatchEvent(event);
        },
        function(error) {
            console.error('Error updating insets:', error);
        }
    );
}

// Update insets on device ready and orientation change
document.addEventListener('deviceready', updateInsets, false);
window.addEventListener('orientationchange', function() {
    // Small delay to ensure the orientation change is complete
    setTimeout(updateInsets, 100);
}, false);
```

## API Reference

### Methods

#### `getInsets(successCallback, errorCallback)`

Retrieves the current window insets from the Android system.

**Parameters:**
- `successCallback` (Function): Called when insets are successfully retrieved
- `errorCallback` (Function, optional): Called when an error occurs

**Success Callback Parameters:**
The success callback receives an object with the following properties:
- `top` (Number): Top inset in dp (typically status bar height)
- `bottom` (Number): Bottom inset in dp (typically navigation bar height)
- `left` (Number): Left inset in dp
- `right` (Number): Right inset in dp

## Android Configuration

### Edge-to-Edge Setup

To enable edge-to-edge display in your Android app, add the following to your `config.xml`:

```xml
<platform name="android">
    <!-- Enable edge-to-edge display -->
    <edit-config file="app/src/main/res/values/styles.xml" mode="merge" target="/resources/style[@name='AppTheme']">
        <item name="android:windowLayoutInDisplayCutoutMode">shortEdges</item>
        <item name="android:windowTranslucentStatus">true</item>
        <item name="android:windowTranslucentNavigation">true</item>
    </edit-config>
    
    <!-- For API 28+ -->
    <edit-config file="app/src/main/res/values-v28/styles.xml" mode="merge" target="/resources/style[@name='AppTheme']">
        <item name="android:windowLayoutInDisplayCutoutMode">shortEdges</item>
    </edit-config>
</platform>
```

### MainActivity Configuration

You may also need to configure your MainActivity for proper edge-to-edge behavior:

```java
// In your MainActivity.java or in a plugin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
    getWindow().setDecorFitsSystemWindows(false);
} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    );
}
```

## Compatibility

- **Cordova:** 9.0.0+
- **Android:** API Level 21+ (Android 5.0+)
- **Cordova Android:** 8.0.0+

The plugin includes fallback mechanisms for older Android versions and devices that don't support the latest inset APIs.

## Common Use Cases

### 1. Full-Screen Video Player
```javascript
function enterFullscreen() {
    cordova.plugins.InsetsPlugin.getInsets(function(insets) {
        const videoContainer = document.getElementById('video-container');
        videoContainer.style.marginTop = '-' + insets.top + 'px';
        videoContainer.style.marginBottom = '-' + insets.bottom + 'px';
    });
}
```

### 2. Chat Interface with Input at Bottom
```javascript
function setupChatInterface() {
    cordova.plugins.InsetsPlugin.getInsets(function(insets) {
        const chatInput = document.getElementById('chat-input');
        chatInput.style.paddingBottom = insets.bottom + 'px';
    });
}
```

### 3. Image Gallery with System UI Overlay
```javascript
function setupImageGallery() {
    cordova.plugins.InsetsPlugin.getInsets(function(insets) {
        const gallery = document.getElementById('gallery');
        // Allow images to go under system UI
        gallery.style.marginTop = '-' + insets.top + 'px';
        gallery.style.marginBottom = '-' + insets.bottom + 'px';
        
        // But keep controls in safe area
        const controls = document.getElementById('gallery-controls');
        controls.style.marginTop = insets.top + 'px';
        controls.style.marginBottom = insets.bottom + 'px';
    });
}
```

## Troubleshooting

### Insets Return Zero
If insets consistently return zero values:
1. Ensure your app is configured for edge-to-edge display
2. Check that the device supports window insets (API 21+)
3. Verify the plugin is called after the `deviceready` event

### Inconsistent Values
If inset values seem inconsistent:
1. Call `getInsets()` after orientation changes
2. Add a small delay after orientation changes before calling the method
3. Test on different devices and Android versions

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## Author

**Maaz Khaleeq**
- GitHub: [@MaazKhaleeq](https://github.com/MaazKhaleeq)

## Changelog

### 1.0.0
- Initial release
- Support for Android window insets
- Edge-to-edge display compatibility
- Fallback support for older Android versions
