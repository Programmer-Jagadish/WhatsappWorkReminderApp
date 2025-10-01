import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WhatsAppReminderRotating {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private static String getEnv(String key) {
        String value = System.getenv(key);
        if (value != null && !value.isEmpty()) return value;
        return dotenv.get(key);
    }

    public static final String ACCOUNT_SID = getEnv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = getEnv("TWILIO_AUTH_TOKEN");
    public static final String SENDER_NUMBER = getEnv("TWILIO_FROM_NUMBER");

    static final List<String> tasks = Arrays.asList(
            "Utensils Washing",
            "Dustbin Disposal",
            "Morning Water Tank Fill",
            "Kitchen Floor Clean",
            "Drinking Water Can"
    );

    static final Map<String, String> people = new LinkedHashMap<>();

    static {
        people.put("Akshay", "whatsapp:+919766888937");
        people.put("Azeez", "whatsapp:+919597177255");
        people.put("Mahesh", "whatsapp:+919148245690");
        people.put("Jagadish", "whatsapp:+917414928464");
    }

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        LocalDate startDate = LocalDate.of(2025, 7, 26);

        LocalDate today=LocalDate.now(ZoneId.of("Asia/Kolkata")).plusDays(1);
        String todayFormated = today.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String reminderDate= today.minusDays(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(startDate, today);

        List<String> names = new ArrayList<>(people.keySet());
        Map<String, List<String>> todayAssignments = new LinkedHashMap<>();

        for (String name : names) {
            todayAssignments.put(name, new ArrayList<>());
        }

        int shift = (int) (daysElapsed % names.size());
        for (int i = 0; i < tasks.size() - 1; i++) {
            String task = tasks.get(i);
            int memberIndex = (i + shift) % names.size();
            String person = names.get(memberIndex);
            todayAssignments.get(person).add(task);
        }

        if (daysElapsed % 3 == 0) {
            int extraIndex = (int) ((daysElapsed / 3) % names.size());
            String personForExtra = names.get(extraIndex);
            todayAssignments.get(personForExtra).add("Drinking Water Can");
        }

        StringBuilder messageText = new StringBuilder();
        messageText.append("🧾 *Daily Room Work Tracker* - ").append(todayFormated).append("\n\n");

        for (Map.Entry<String, List<String>> entry : todayAssignments.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                messageText.append("👤 ").append(entry.getKey()).append(": ")
                        .append(String.join(", ", entry.getValue()))
                        .append("\n");
            }
        }

        messageText.append("\nPlease complete your tasks sincerely. ✅");

        // ✅ Write log locally (same as before, just local file)
        try {
            Path logDir = Paths.get("logs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
            String logFilePath = "logs/reminders_log.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
                String timestamp = LocalTime.now(ZoneId.of("Asia/Kolkata")).format(DateTimeFormatter.ofPattern("hh:mm a"));
                writer.write("------ Reminder Date: " + reminderDate + " (" + timestamp + ") ------\n");
                writer.write(messageText.toString());
                writer.write("\n---------------------------------------------\n\n");
            }
            System.out.println("📜 Reminder logged locally.");
        } catch (IOException e) {
            System.err.println("⚠️ Failed to write log file: " + e.getMessage());
        }

        // ✅ Send message to all members (unchanged)
        for (Map.Entry<String, String> entry : people.entrySet()) {
            try {
                Message message = Message.creator(
                        new PhoneNumber(entry.getValue()),
                        new PhoneNumber(SENDER_NUMBER),
                        messageText.toString()
                ).create();

                System.out.println("✅ Sent to " + entry.getKey() + ": " + message.getSid());
            } catch (Exception e) {
                System.err.println("❌ Error for " + entry.getKey() + ": " + e.getMessage());
            }
        }

        // ✅ Print final message (unchanged)
        System.out.println("\n--- Tomorrow's Reminder ---\n" + messageText);

        // ✅ Extra: Sync log file to Google Drive without overwriting
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "rclone",
                    "copy",
                    "logs/reminders_log.txt",
                    "gdrive:/WhatsappReminderLog/",
                    "--update"
            );
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
            System.out.println("☁️ Log file synced to Google Drive successfully.");
        } catch (Exception e) {
            System.err.println("⚠️ Failed to sync log file to Google Drive: " + e.getMessage());
        }
    }
}
