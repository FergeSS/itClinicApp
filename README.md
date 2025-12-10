## Eng
# itClinicApp
Android app for it-clinic spbu

## Rus
# itClinicApp
Андроид-приложение для IT-клиники СПбГУ

# Описание

Приложение, позволяющее студентам, руководителям и заказчикам просматривать и предлагать проекты, общаться в командных чатах и видеть статистику по поставленным задачам.

# Структура

См. подробности в [документации](docs/scheme.md).

### Проекты

На этом экране пользователь видит список всех проектов, которые были в IT-клинике СПбГУ. Также есть поиск с фильтрами. Снизу располагается кнопка переключения между всеми проектами и текущим проектом пользователя, если он закреплен за ним. По нажатию на проект о нем появляется подробная информация, которую пользователь может изучить. Также для заказчиков доступна еще одна кнопка - предложить проект, где они смогут оставить свои контактные данные, чтобы с ними связались.


# Дизайн

[Дизайн](https://www.figma.com/design/Pac1BvaweGNNmjDnvW2ZdL/IT-CLINIC?node-id=0-1&p=f&t=Se6QVT4BCQxH8jUh-0)

# Сборка и запуск

This is a Kotlin Multiplatform project targeting Android, iOS.

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…

