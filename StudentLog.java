package Lab3RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;

public class StudentLog {
    private static final String EXCHANGE_NAME = "finki";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName,EXCHANGE_NAME,"");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // name#roomnumber#roomtype
            String message = new String(delivery.getBody(),"UTF-8");
            StringTokenizer st = new StringTokenizer(message,"#");
            var userType = st.nextToken();
            if(userType.equals("student")){
                var name = st.nextToken();
                var roomNumber = st.nextToken();
                var roomType = st.nextToken();
                if(roomType.equals("kancelarija")){
                    System.out.println("Kancelarija so broj " + roomNumber + " e pristapena od strana na studentot");
                    System.out.println("\t\t\t Ime na korisnik: " + name);
                }
            }

        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});

    }

}
