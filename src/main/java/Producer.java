import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Producer {
    public static final String FIRST_QUEUE_NAME = "queue1";
    static final String[] COLORS = { "Green", "Blue", "Orange", "Yellow", "Red", "Purple", "Pink" };

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("host.docker.internal");

         try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare(FIRST_QUEUE_NAME, false, false, false, null);

            while (true) {
                String message = COLORS[new Random().nextInt(COLORS.length)];
                channel.basicPublish("", FIRST_QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.printf("%-9s %-16s %-5s %-6s%n", "SENT:", message, "TO:", FIRST_QUEUE_NAME);
                Thread.sleep(5000);
            }
         } catch (Exception e) {
             // couldn't set a connection or connection has been interrupted
            System.out.println(e.getMessage());
            System.exit(1);
         }
    }
}