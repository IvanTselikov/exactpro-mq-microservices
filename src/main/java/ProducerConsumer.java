import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class ProducerConsumer {
    public static final String SECOND_QUEUE_NAME = "queue2";
    static final String[] FRUITS = { "Apple", "Banana", "Orange", "Melon", "Pear", "Kiwifruit", "Pineapple" };

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("host.docker.internal");

        try {
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(Producer.FIRST_QUEUE_NAME, false, false, false, null);
            channel.queueDeclare(SECOND_QUEUE_NAME, false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String receivedMessage = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.printf("%-9s %-16s %-5s %-6s%n", "RECEIVED:", receivedMessage, "FROM:", Producer.FIRST_QUEUE_NAME);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);

                String messageToSend = receivedMessage + " " + FRUITS[new Random().nextInt(FRUITS.length)];
                channel.basicPublish("", SECOND_QUEUE_NAME, null, messageToSend.getBytes(StandardCharsets.UTF_8));
                System.out.printf("%-9s %-16s %-5s %-6s%n", "SENT:", messageToSend, "TO:", SECOND_QUEUE_NAME);
            };

            channel.basicConsume(Producer.FIRST_QUEUE_NAME, false, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            // couldn't set a connection or connection has been interrupted
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}