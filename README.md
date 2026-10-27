# AssignmentHub — Online Assignment Submission Mobile Application

A production-grade, highly responsive Android mobile application engineered with **Kotlin** and **Jetpack Compose (Material 3)**. Built specifically for academic environments, **AssignmentHub** delivers an end-to-end assignment workflow supporting dual roles (**Students** and **Teachers**), comprehensive multi-format submissions (documents, archives, code, written text, links), cloud-ready repository architecture, and adaptive UI layouts that respond seamlessly across all device aspect ratios.

---

## 🌟 Key Features

### 1. Dual-Role Experience & Instant Presentation Switcher
- **Student Experience**:
  - Academic dashboard with real-time metric cards (*Due Soon, Submitted, Graded, Overdue*).
  - Search and filter by course code (*CS-402, CS-301, UX-204, SE-410*) and assignment status.
  - Interactive **Submission Studio**: attach simulated PDFs, code archives, written text responses, and GitHub repository links.
  - View graded submissions with score breakdown and instructor feedback comments.
- **Teacher / Faculty Experience**:
  - Priority **Grading Queue**: inspect incoming submissions awaiting grading.
  - Grading Studio: review student artifacts, assign scores, choose quick feedback templates, and publish grades in real time.
  - Assignment Creator: draft and publish new assignments directly to courses.
- **Instant Demo Switcher**:
  - One-tap role switcher in the top bar allowing presenters to toggle between **Alex Johnson (Student)** and **Dr. Sarah Mitchell (Teacher)**.
  - Built-in **"Reset Demo Data"** action to return to a clean state anytime during live presentations.

### 2. Flawless Adaptation Across All Screen Aspect Ratios
- **Compact Phones (< 600dp / 16:9, 18:9, 20:9)**:
  - Ergonomic bottom navigation (`NavigationBar`), fluid vertical scrolling, and stacked hierarchical screen transitions.
- **Medium Screens & Foldables (600dp - 840dp / Unfolded Foldables & Landscape)**:
  - Switches automatically to a pinned `NavigationRail` on the left.
- **Expanded Screens & Tablets (> 840dp)**:
  - Permanent `NavigationDrawer` with student profile and quick links.
  - **Adaptive Two-Pane Split View**: Assignment list on the left pane and assignment details / submission studio on the right pane simultaneously.

### 3. Android Studio Presentation Preview Suite
- Built-in multi-screen `@Preview` composables configured directly in `com.assignmenthub.ui.previews.MultiDevicePreviews`:
  - `PreviewPhonePortrait`: Standard phone (16:9 / 18:9)
  - `PreviewTallAspectPhone`: Tall aspect ratio flagship (20:9)
  - `PreviewFoldableDevice`: Foldable square display
  - `PreviewTabletTwoPane`: 10" Widescreen tablet two-pane layout
  - `PreviewDarkMode`: High-contrast dark theme
- Presenters can simply click **"Split"** or **"Design"** in Android Studio to demonstrate responsiveness across all form factors side-by-side **without waiting for an emulator to boot!**

---

## 🏗️ Project Architecture & Tech Stack

```
Online Assignment Submission Application/
├── app/
│   ├── build.gradle.kts                # Android application configuration (AGP 8.4+, Compose BOM)
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml     # App declaration with responsive configChanges
│       │   ├── java/com/assignmenthub/
│       │   │   ├── MainActivity.kt     # Entry point with edge-to-edge support
│       │   │   ├── AssignmentApp.kt    # Root App composable, adaptive scaffold & nav host
│       │   │   ├── model/              # Domain entities
│       │   │   │   ├── User.kt         # Student & Teacher models
│       │   │   │   ├── Course.kt       # Course details & colors
│       │   │   │   ├── Assignment.kt   # Assignment status & rubric models
│       │   │   │   └── Submission.kt   # Submission attachments & scores
│       │   │   ├── data/repository/    # Clean architecture repository pattern
│       │   │   │   ├── AssignmentRepository.kt
│       │   │   │   ├── SubmissionRepository.kt
│       │   │   │   └── MockDataRepository.kt  # Reactive offline presentation provider
│       │   │   ├── ui/
│       │   │   │   ├── theme/          # Material 3 Color, Type, and Theme
│       │   │   │   ├── components/     # Status badges, metric cards, role top bar
│       │   │   │   ├── adaptive/       # WindowSizeClass & Two-Pane layouts
│       │   │   │   ├── viewmodel/      # Assignment, Submission & Grading ViewModels
│       │   │   │   ├── screens/
│       │   │   │   │   ├── student/    # Dashboard, Detail, SubmissionStudio
│       │   │   │   │   └── teacher/    # Dashboard, GradingStudio, CreateAssignment
│       │   │   │   └── previews/       # MultiDevicePreviews for Android Studio design view
│       │   └── res/                    # Vector icons, themes, and strings
│       └── test/                       # JUnit repository & workflow unit tests
├── gradle/
│   ├── libs.versions.toml              # Modern Gradle Version Catalog
│   └── wrapper/                        # Official Gradle 8.7 wrapper
├── build.gradle.kts                    # Root build script
├── settings.gradle.kts                 # Subproject & plugin settings
└── README.md
```

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose (Material 3 BOM 2024.06.00)
- **Architecture**: MVVM + Clean Architecture (StateFlow & Coroutines)
- **Minimum SDK**: Android 7.0 (API level 24)
- **Target / Compile SDK**: Android 14 / 15 (API level 34)

---

## 🚀 How to Open and Present in Android Studio

### Step 1: Open in Android Studio
1. Launch **Android Studio** (Hedgehog, Iguana, Jellyfish, Koala, Ladybug, or newer).
2. Click **File -> Open...** and select this project directory:
   `c:\Users\LALITKO\Desktop\projects\Online Assignment Submission Application`
3. Android Studio will automatically recognize the Gradle build files and sync the project dependencies.

### Step 2: Live Aspect Ratio Demo (No Emulator Needed)
1. In the Android Studio project tree, navigate to:
   `app -> src -> main -> java -> com -> assignmenthub -> ui -> previews -> MultiDevicePreviews.kt`
2. At the top right of the editor tab, click **"Split"** or **"Design"**.
3. Android Studio's interactive preview panel will render all aspect ratios side-by-side:
   - **Phone (16:9 / 18:9)**
   - **Modern Tall Phone (20:9)**
   - **Foldable Device**
   - **Tablet Landscape (Two-Pane Split View)**
   - **Dark Mode**

### Step 3: Run on Emulator / Physical Device
1. Select any emulator or connected Android device in the top toolbar dropdown.
2. Click the green **Run (▶)** button or press `Shift + F10`.

---

## 🎬 3-Minute Live Presentation Script

Here is an effective script to demonstrate the full application lifecycle to professors or evaluators:

1. **Student Dashboard & Responsiveness**:
   - Show the summary cards (*Due Soon, Submitted, Graded, Overdue*).
   - Filter by course (*CS-402*) or tap status pills (*Pending*).
   - If rotating the device or on tablet, show how the layout smoothly expands to the **Two-Pane Split View**.
2. **Submit an Assignment**:
   - Tap on `"Responsive UI & Adaptive Navigation in Jetpack Compose"`.
   - Tap **"Submit Assignment"** to open **Submission Studio**.
   - Tap `+ PDF Report` and `+ Code.zip` to attach simulated files.
   - Enter a brief text answer and tap **"Confirm & Submit Assignment"**.
   - Show how the status immediately updates to **"Submitted"**.
3. **Switch to Teacher View**:
   - Tap the top-right role chip **"Student: Alex"** -> instantly switches to **"Teacher: Dr. Sarah Mitchell"**.
   - Point out the **Grading Queue**: Alex's newly submitted assignment is now waiting at the top of the queue.
4. **Grade & Feedback**:
   - Tap **"Grade Now"** to open **Grading Studio**.
   - Review the student's attached PDF and code archive.
   - Enter score (e.g. `98 / 100`) and tap one of the quick feedback chips (*"Exceptional work! Well-structured solution and clear documentation."*).
   - Tap **"Publish Grade to Student"**.
5. **Verify Student Grade**:
   - Tap the role switcher back to **"Student: Alex"**.
   - The assignment now displays the green **"Graded"** badge with `98 / 100 pts` and Dr. Mitchell's comments.
6. **Reset Anytime**:
   - Click the three dots (⋮) in the top bar -> select **"Reset Demo Data"** to restore the pristine state.
