@echo off

REM Change to JAR file location
cd "C:\Users\Jagad\Downloads\Projects\WhatsAppReminderRotating\WhatsAppReminderRotating\target"

REM Set log file with today's date (e.g., log_2025-07-26.txt)
set LOGFILE="C:\Users\Jagad\Downloads\Projects\WhatsAppReminderRotating\logs\RoomTaskReminder_log_%date:~-4%-%date:~4,2%-%date:~7,2%.txt"

REM Ensure log directory exists
if not exist "C:\Users\Jagad\Downloads\Projects\WhatsAppReminderRotating\logs" (
    mkdir "C:\Users\Jagad\Downloads\Projects\WhatsAppReminderRotating\logs"
)

REM Run the Java program and save output to log
echo Running WhatsApp Reminder >> %LOGFILE%
echo. >> %LOGFILE%
java -jar WhatsAppReminderRotating-1.0-SNAPSHOT.jar >> %LOGFILE% 2>&1

echo. >> %LOGFILE%
echo Run finished at %time% >> %LOGFILE%
