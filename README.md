# QueryStore 🚀

QueryStore is a professional, cloud-synchronized SQL management tool. It allows developers to write, save, and manage their SQL queries in a modern desktop interface while keeping everything backed up in a secure PostgreSQL cloud database.

---

## ⚡ Quick Start (Recommended)

### **Using the Portable Software**
The easiest way to get started is with the portable version. No build or backend setup is required:
1.  Locate the **`QueryStore.zip`** file in the root directory.
2.  Download and extract the ZIP file to any folder on your computer.
3.  Double-click **`QueryStore.exe`** to launch the application.
4.  *(Note: Requires Java 17 or higher installed on your system).*

---

## ✨ Key Features
*   **Cloud Synchronization:** Access your queries from any computer by logging into your account.
*   **SQL Syntax Highlighting:** A beautiful editor that colors SQL keywords (SELECT, FROM, JOIN, etc.) for better readability.
*   **Modern UI:** Built with the **FlatLaf** theme for a clean, professional appearance.
*   **Secure Authentication:** User accounts are protected using **BCrypt password hashing**.
*   **Smart Overwrite:** Automatically detects existing query titles and asks if you want to update them.
*   **Search & Filter:** Quickly find specific queries in your library using the search bar.

---

## 🛠 Technical Stack
### **Frontend (Desktop)**
*   **Language:** Java 17+
*   **Framework:** Java Swing
*   **Libraries:** 
    *   `FlatLaf`: Modern Look & Feel.
    *   `RSyntaxTextArea`: Advanced SQL text editing.
*   **Communication:** Java `HttpClient` (REST API).

### **Backend (Server)**
*   **Framework:** Spring Boot 3
*   **Security:** Spring Security (Stateless).
*   **Database:** PostgreSQL (Hosted on **Supabase**).
*   **Deployment:** Dockerized on **Render**.

---

## 🚀 Development & Building from Source

If you want to run the project for development or build it yourself:

### **Backend:**
1.  Navigate to `/server`.
2.  Run `.\run_backend_locally.bat` (Ensure your Environment Variables are set).

### **Frontend:**
1.  Navigate to the root directory.
2.  Run: `java -cp "lib/*;build/classes" querystore.QueryStore`

---

## 📂 Project Structure
*   **QueryStore.zip**: Ready-to-use portable package (EXE + Libraries).
*   **src/**: Desktop application source code.
*   **server/**: Spring Boot backend source code.
*   **lib/**: External libraries.

---

## 📦 Deployment & Maintenance
*   **Hosting:** The backend is hosted on Render's free tier. 
*   **Keep-Alive:** An **Uptime Monitor** (UptimeRobot) is configured to ping the server every 10 minutes to prevent it from going to sleep.
*   **Database:** Hosted on Supabase. Credentials are managed via **Environment Variables** for maximum security.

---

## 🛡 Security Note
This project follows security best practices:
*   **No Hardcoded Passwords:** All database credentials are stored in environment variables.
*   **Ignore Sensitive Files:** `.gitignore` is configured to prevent local configuration and passwords from being uploaded to GitHub.
*   **Hashed Passwords:** User passwords are never stored in plain text.

---

## 👨‍💻 Author
Developed by **Partho**.

---
*QueryStore - Your SQL library, anywhere, anytime.*
