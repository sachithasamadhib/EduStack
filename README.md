# 📚 EduStack

**EduStack** is a modern educational management system designed to simplify and automate the daily operations of schools, colleges, and other educational institutions. With dedicated modules for administrators, teachers, and students, EduStack provides a seamless experience for managing classes, accounts, schedules, reports, and communication.

## ✨ Features

- **Role-Based Access:** Separate dashboards and permissions for Admins, Teachers, and Students.
- **Account Management:** Create, update, and manage student and teacher accounts.
- **Class Management:** Organize classes, assign teachers, and enroll students.
- **Calendar & Events:** Schedule classes, exams, and events with reminders.
- **Reports:** Generate and view academic and attendance reports.
- **Notifications:** Real-time push notifications for important updates.
- **Secure Authentication:** Safe and secure login for all users.
- **Modern UI:** Clean, intuitive, and mobile-friendly interface.

## 👤 User Roles

### Admin
- Manage all user accounts (students, teachers).
- Create, edit, and delete classes.
- Assign teachers to classes.
- View and generate reports (attendance, performance, etc.).
- Manage school-wide events and notifications.
- Access to all system settings.

### Teacher
- View assigned classes and students.
- Mark attendance and manage class schedules.
- Add and edit class events.
- View and update student performance.
- Receive notifications and school updates.

### Student
- View enrolled classes and schedules.
- Access class events and calendar.
- Receive notifications and updates.
- View personal attendance and performance reports.

## 📸 Screenshots

![Capture](https://github.com/user-attachments/assets/b143e0ba-ca80-469f-9f51-11d97c79ef8b)
![Capture4](https://github.com/user-attachments/assets/b5ed30ec-d1c2-4be6-b0d4-31f3b0a525d2)
![Capture6](https://github.com/user-attachments/assets/26ba86a1-f12f-4553-8641-a6f168abf584)
![Capture9](https://github.com/user-attachments/assets/0ec3830d-c481-4822-a27e-d35cb039b806)
![Capture11](https://github.com/user-attachments/assets/19013fb2-77ee-4eab-b82f-5987fc0a301f)
![Capture13](https://github.com/user-attachments/assets/c68d0076-550c-4c05-985b-047107b9f09d)

## 🚀 Getting Started

### 🔧 Prerequisites
- [Android Studio](https://developer.android.com/studio)
- [Java JDK 8+](https://adoptopenjdk.net/)
- [Gradle](https://gradle.org/) (or use the included wrapper)
- (Optional) [Firebase Account](https://firebase.google.com/) for push notifications

### 📥 Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/sachithasamadhib/EduStack.git
   cd EduStack
   ```
2. **Open in Android Studio:**
   - Launch Android Studio.
   - Select "Open an existing project" and choose the `EduStack` directory.
3. **Build the project:**
   - Let Gradle sync and download dependencies.
   - Connect an Android device or start an emulator.
   - Click "Run" to build and launch the app.

### ⚙️ Configuration
- **Google Services:** The project includes a `google-services.json` file for Firebase integration. Replace it with your own if you use Firebase services.
- **App Settings:** Update any institution-specific settings in the app as needed.

## 🗂️ Project Structure

```
EduStack/
  app/
    src/
      main/
        java/com/edustack/edustack/
          Controller/      # ViewModels, Adapters, Controllers
          Models/          # Data models
          ...              # Activities and Fragments
        res/               # Layouts, Drawables, Values
      test/                # Unit tests
      androidTest/         # Instrumented tests
  build.gradle.kts
  README.md
  LICENSE
```

## 🤝 Contributing

We welcome contributions from the community!

1. **Fork the repository**
2. **Create your feature branch**
   ```bash
   git checkout -b feature/YourFeature
   ```
3. **Commit your changes**
   ```bash
   git commit -am 'Add new feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/YourFeature
   ```
5. **Open a pull request**

Please ensure your code follows the existing style and includes relevant tests.

## ❓ FAQ

**Q: How do I add a new admin/teacher/student?**  
A: Log in as an admin, go to the Accounts section, and use the "Create Account" feature.

**Q: How are notifications sent?**  
A: Notifications are sent via Firebase Cloud Messaging. Ensure your `google-services.json` is configured.

**Q: Can I customize the class schedule?**  
A: Yes, admins and teachers can add, edit, or remove class events from the calendar.

**Q: How do students view their attendance?**  
A: Students can view their attendance reports from their dashboard.

**Q: Is my data secure?**  
A: EduStack uses secure authentication and follows best practices for data privacy.

## 👥 Contributors

We are grateful to the following contributors for their valuable efforts in building EduStack:

- [Viraj Wathsala Gunasinghe](https://github.com/virajwathsalag)
- [Sachitha Samadhi Bandara](https://github.com/sachithasamadhib)
- [Ravin Jayasanka](https://github.com/MrRaveen)
- [Sanuja Rasanajna](https://github.com/SanujaRasanajna2007)

## 📜 License

This project is licensed under the [MIT License](LICENSE).

## 📬 Contact

For questions, support, or feature requests, please open an issue or contact the maintainer:  
- **GitHub Issues:** [https://github.com/sachithasamadhib/EduStack/issues](https://github.com/sachithasamadhib/EduStack/issues)

**EduStack** – Empowering Education, One Click at a Time.
