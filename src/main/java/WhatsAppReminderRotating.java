import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;
import java.util.*;

public class WhatsAppReminderRotating {

    // Load .env file locally
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

    // List of tasks
    static final List<String> tasks = Arrays.asList(
            "Utensils Washing",
            "Dustbin Disposal",
            "Morning Water Tank Fill",
            "Drinking Water Can",
            "Kitchen Floor Clean"
    );

    // Map of people and their WhatsApp numbers
    static final Map<String, String> people = new LinkedHashMap<>();

    static {
        people.put("Akshay", "whatsapp:+919766888937");
        people.put("Azeez", "whatsapp:+919597177255");
        people.put("Mahesh", "whatsapp:+919148245690");
        people.put("Jagadish", "whatsapp:+917414928464");
    }

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        LocalDate startDate = LocalDate.of(2025, 7, 26); // base rotation date
        LocalDate today = LocalDate.now();
        long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(startDate, today);

        List<String> names = new ArrayList<>(people.keySet());
        Map<String, String> todayAssignments = new LinkedHashMap<>();

        int rotationIndex = 0;

        for (String task : tasks) {
            if (task.equals("Drinking Water Can")) {
                if (daysElapsed % 3 != 0) {
                    continue; // Skip assignment for today
                }
            }

            int memberIndex = (int)((daysElapsed + rotationIndex) % names.size());
            String person = names.get(memberIndex);
            todayAssignments.put(person, task);

            rotationIndex++;
        }

        StringBuilder messageText = new StringBuilder();
        messageText.append("🧾 *Daily Room Work Tracker* - ").append(today).append("\n\n");

        for (Map.Entry<String, String> entry : todayAssignments.entrySet()) {
            messageText.append("👤 ").append(entry.getKey())
                    .append(": ").append(entry.getValue()).append("\n");
        }

        messageText.append("\nPlease complete your tasks sincerely. ✅");

        // Send to all members
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
    }
}
