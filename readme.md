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
`NoTitleBar::workWithCompose` for a fast path for compose
### Strange behavior
* Resize window to 0px

In my pc,window border blink when mouse over Button or user input to TextField.  
But when I test in other pc,this problem can't reproduce.
### Build
Java 21 + Gradle