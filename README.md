# Welcome to CommunityConnect 🏙️

Welcome to the **CommunityConnect** repository. This is a native Android application designed to facilitate neighborhood engagement. It provides a centralized platform for residents to report local issues, participate in community surveys, discuss topics in forums, and explore a directory of local resources.

---

## 📚 About The Project

| Feature                | Details |
| ---------------------- | ------- |
| 🎯 **Purpose**         | A platform to connect residents, report neighborhood issues, and manage local community resources efficiently. |
| ⚙️ **Architecture**     | Built using a multi-activity Android architecture utilizing Kotlin and traditional XML View-based layouts. |
| 💾 **Data Management** | Real-time data synchronization and role-based access control powered by Firebase Firestore. |
| 🔄 **Core Operations** | Secure authentication, role-based feature gating (Admin vs. Citizen), dynamic polling, forum discussions, and civic reporting. |

---

## 🚀 Tech Stack

### Android & UI

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![XML](https://img.shields.io/badge/XML-00599C?style=for-the-badge&logo=xml&logoColor=white)

- **Kotlin & Android SDK:** The core application logic, utilizing standard Android Activities and Adapters.
- **XML Layouts:** Fully custom user interface built with `ConstraintLayout`, `RecyclerView`, and custom drawable shapes for a unique visual identity.
- **Material Components:** Usage of Bottom App Bars, Floating Action Buttons, and Bottom Sheets for navigation and interactions.

### Backend & Database

![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

- **Firebase Authentication:** Handles user onboarding via Email and Password.
- **Firebase Firestore:** A NoSQL cloud database handling real-time posts, poll increments, report submissions, and user roles.

---

## 🔧 Highlighted Features

| Feature | Description |
|--------|------------|
| **Role-Based Access** | Built-in logic for 'Citizen' and 'Admin' roles. Admins have exclusive access to create new polls and review all submitted civic reports. |
| **Interactive Surveys** | Dynamic 4-question polls featuring a custom visual rating scale (0, 5, 10). Includes real-time percentage calculation and result rendering. |
| **Civic Reporting** | Dedicated forms for citizens to report local problems (Environmental, Security, etc.) with date pickers and detailed descriptions. |
| **Community Forums** | A space for users to publish and read community discussions, complete with custom sorting/filtering (by User, Date, or Title). |
| **Resource Directory** | A categorized local directory (Parks, Offices, Hospitals, Museums) allowing users to quickly find local services and schedules. |

---

## 📸 Screenshots

- ![Login & Registration](assets/LoginScreen.jpeg)
- ![Main Dashboard](assets/MainDashboard.jpeg)
- ![Interactive Polls](assets/Surveys.jpeg)
- ![Resource Directory](assets/Directory.jpeg)

---

## 🛠️ How to Run Locally

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/CommunityConnect.git
cd CommunityConnect
```

### 2. Open the project

Launch Android Studio, select **Open an existing project**, and navigate to the cloned folder.

### 3. Firebase Setup

1. Go to the Firebase Console and create a new project.
2. Add an Android app (ensure the package name matches `com.example.conexioncomunitaria`).
3. Download the `google-services.json` file.
4. Place the file inside the `app/` directory of the project.
5. Enable **Firestore Database** and **Authentication (Email/Password)**.

### 4. Build and Run

Click **Sync Project with Gradle Files** in Android Studio.  
Once synced, select your emulator or physical device and click **Run (Shift + F10)**.

---

## 🎨 Credits

Special thanks to @lamusaliza for the original UI design concepts and visual inspiration used throughout this application.

---

## 💡 Final Notes

This project demonstrates the implementation of a fully functional Android application using traditional XML layouts, custom UI components, and strict role-based Firebase integration.
