// WhatsAppReminderRotating.java

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.time.LocalDate;
import java.util.*;

public class WhatsAppReminderRotating {

    public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
    public static final String SENDER_NUMBER = "whatsapp:+14155238886"; // Sandbox

    static final List<String> tasks = Arrays.asList(
            "Utensils Washing",
            "Dustbin Disposal",
            "Morning Water Tank Fill",
            "Drinking Water Can",
            "Kitchen Floor Clean"
    );

    static final Map<String, String> people = new LinkedHashMap<>();

    static {
        people.put("Akshay", "whatsapp:+91xxxxxxxxxx");
        people.put("Azeez", "whatsapp:+91xxxxxxxxxx");
        people.put("Mahesh", "whatsapp:+91xxxxxxxxxx");
        people.put("Jagadish", "whatsapp:+91xxxxxxxxxx");
    }

    public static void main(String[] args) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        LocalDate startDate = LocalDate.of(2025, 7, 26);
        LocalDate today = LocalDate.now();
        long daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(startDate, today);

        List<String> names = new ArrayList<>(people.keySet());
        Map<String, String> todayAssignments = new LinkedHashMap<>();

        int taskIndex = 0;
        for (String task : tasks) {
            int memberIndex = (int)((daysElapsed + taskIndex) % names.size());

            // Drinking water can rotates every 3rd day
            if (task.equals("Drinking Water Can") && daysElapsed % 3 != 0) {
                taskIndex++;
                continue;
            }

            String person = names.get(memberIndex);
            todayAssignments.put(task, person);
            taskIndex++;
        }

        StringBuilder summary = new StringBuilder("🔔 *Today's Task Assignments* 🔔\n\n");

        for (Map.Entry<String, String> entry : todayAssignments.entrySet()) {
            summary.append("🧹 ").append(entry.getKey()).append(": *").append(entry.getValue()).append("*\n");
        }

        // Send to all members
        for (Map.Entry<String, String> entry : people.entrySet()) {
            try {
                Message message = Message.creator(
                        new PhoneNumber(entry.getValue()),
                        new PhoneNumber(SENDER_NUMBER),
                        summary.toString()
                ).create();

                System.out.println("✅ Sent to " + entry.getKey() + ": " + message.getSid());
            } catch (Exception e) {
                System.err.println("❌ Error for " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }
}