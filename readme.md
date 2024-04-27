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
You need set
```kotlin
Window(undecorated = true)
```
then everything same as AWT.
### Unknown behavior
* Minimize
* Maximize
### Build
Java 21 + Gradle