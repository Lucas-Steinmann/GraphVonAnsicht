# GraphVonAnsicht

## Project setup
### Eclipse

Install "Buildship: Eclipse Plug-ins for Gradle" :
[Buildship: Eclipse Plug-ins for Gradle | projects.eclipse.org](http://projects.eclipse.org/projects/tools.buildship/downloads).

Optional: Install "Minimalist Gradle Editor" for viewing and editing Gradle files:
 [Minimalist Gradle Editor | Eclipse Plugins, Bundles and Products - Eclipse Marketplace](https://marketplace.eclipse.org/content/minimalist-gradle-editor)

- Git Clone Project
- File -> Import -> Gradle -> Gradle Project
- Import: Wizard: Next -> Select Git Root Folder -> Next -> Next -> Finish (Keep existing files when asked)
- Gradle Tasks -> Graph von Ansicht -> ide -> eclipse -> execute task

## Build

To build an executable version of the jar containing all currently available plugins execute the gradle task `allJar` from the projects root folder or from your IDE.
```bash
./gradlew clean allJar
```
The jar will be placed into `build/libs/`.

## Code Coverage
```bash
./gradlew clean jacocoRootReport
```
The html report will be placed into `build/reports/jacoco/jacocoRootReport/html/index.html` 

