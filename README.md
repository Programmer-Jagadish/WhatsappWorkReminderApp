# WhatsApp Reminder App 📲

A Java-based WhatsApp Reminder application that runs via **GitHub Actions** and uses:
- **Twilio Sandbox** for sending WhatsApp reminders
- **Google Drive** (via rclone) for storing and updating reminder logs
- **GitHub Actions** for scheduled or manual workflow runs

---

## 🚀 Features
- Send automated WhatsApp reminders to multiple participants
- Run on a **schedule** (cron) or **manual trigger** through GitHub Actions
- Maintain a **shared reminder log** stored on Google Drive
- Automatically **download previous log**, update with new reminders, and **re-upload** latest version
- Fully automated pipeline — no manual server management needed

---

## 🔄 Workflow Overview
1. **Trigger**: GitHub Actions workflow runs (scheduled or manual).
2. **Setup**: Java runtime + dependencies + rclone are installed.
3. **Execution**:
   - The Java app downloads the **previous log file** from Google Drive (using rclone + `RCLONE_CONFIG_B64`).
   - Sends WhatsApp reminders to participants using **Twilio Sandbox**.
   - Updates the log file with reminder details.
   - Uploads the **latest log** back to Google Drive.
4. **Result**: 
   - Reminders are delivered to all participants.
   - A single, up-to-date log file is maintained in Google Drive.

---

## 🔐 GitHub Repository Secrets

Configure these secrets in your GitHub repository →  
**Settings → Secrets and variables → Actions**

| Secret Name           | Purpose |
|------------------------|---------|
| `TWILIO_ACCOUNT_SID`   | Twilio Account SID |
| `TWILIO_AUTH_TOKEN`    | Twilio Auth Token |
| `TWILIO_FROM_NUMBER`   | Twilio Sandbox WhatsApp number (format: `whatsapp:+14155238886`) |
| `GDRIVE_CREDENTIALS`   | Google Drive service account credentials (JSON) |
| `RCLONE_CONFIG_B64`    | Base64 encoded rclone config for Drive access |

---



## 🛠️ Setup Instructions

### 1. Clone the repository
```bash
git clone https://github.com/<your-username>/whatsapp-reminder.git
cd whatsapp-reminder
```
## 2. Build the project
```bash
mvn clean install
```

### 3. Run locally (optional)
```bash
java -jar target/whatsapp-reminder.jar
```


**Make sure you have rclone configured locally if you want to test Google Drive sync outside GitHub Actions.** 

### 📅 GitHub Actions Workflow

**The workflow file is located at .github/workflows/reminder.yml and will:**

- Run on schedule (cron) or manual dispatch
- Install Java + rclone
- Run the reminder app
- Upload logs to Google Drive

### 📂 Logs

- Logs are stored in Google Drive as reminders_log.txt.
- Each reminder run appends updates with timestamp.

- Example log entry:

```
Reminder Date: 2025-09-25 (21:00:15)
*Daily Room Work Tracker* - 2025-09-26
- Akshay: Utensils Washing
- Azeez: Dustbin Disposal
- Mahesh: Morning Water Tank Fill
- Jagadish: Kitchen Floor Clean, Drinking Water Can
```

### 👨‍💻 Contributors

**Jagadish Navale – Developer & Maintainer**
**jagadish.navale@outlook.com**

