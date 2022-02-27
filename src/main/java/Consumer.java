import com.rabbitmq.client.*;
import java.nio.charset.StandardCharsets;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("host.docker.internal");

        try {
            Connection connection = factory.newConnection();

            Channel channel = connection.createChannel();
            channel.queueDeclare(ProducerConsumer.SECOND_QUEUE_NAME, false, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.printf("%-9s %-16s %-5s %-6s%n", "RECEIVED:", message, "FROM:", ProducerConsumer.SECOND_QUEUE_NAME);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };

            channel.basicConsume(ProducerConsumer.SECOND_QUEUE_NAME, false, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            // couldn't set a connection or connection has been interrupted
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}