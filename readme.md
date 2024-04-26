### Usage
See `top.birthcat.awtnotitlebar.NoTitleBar`
```
NoTitleBar::removeIn
NoTitleBar::tryRemoveIn
```
Add jvm args:
```
--add-exports java.desktop/sun.awt.windows=ALL-UNNAMED
--enable-native-access=ALL-UNNAMED
--enable-preview
```
```
--add-exports java.desktop/sun.awt.windows=ALL-UNNAMED --enable-native-access=ALL-UNNAMED --enable-preview
```
### Build
Java 21 + Gradle