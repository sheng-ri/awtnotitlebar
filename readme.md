### Usage
#### jvm args:
```
--add-exports java.desktop/sun.awt.windows=ALL-UNNAMED
--enable-native-access=ALL-UNNAMED
--enable-preview
```
or in one line.
```
--add-exports java.desktop/sun.awt.windows=ALL-UNNAMED --enable-native-access=ALL-UNNAMED --enable-preview
```
#### AWT/Swing
See `top.birthcat.notitlebar.NoTitleBar`
```
NoTitleBar::removeIn
NoTitleBar::tryRemoveIn
```
#### Compose Desktop
Same as below, and apply this
```kotlin
Window(undecorated = true)
```
But with awt impl the window border will blink.
You need use `NoTitleBar::workWithCompose`
### Unknown behavior
* Minimize
* Maximize
### Build
Java 21 + Gradle