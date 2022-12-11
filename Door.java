package Lab3RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;

public class Door {
    private static final String EXCHANGE_NAME = "finki";
    private static final String STUDENT = "student";
    private static final String PROFESOR = "profesor";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
        String queueName = channel.queueDeclare().getQueue();

        channel.queueBind(queueName,EXCHANGE_NAME,"");
        //channel.queueBind(queueName,EXCHANGE_NAME,"profesor");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            // usertype#name#roomnumber#roomtype
            String message = new String(delivery.getBody(),"UTF-8");
            StringTokenizer st = new StringTokenizer(message,"#");
            var userType = st.nextToken();
            var name = st.nextToken();
            var roomNumber = st.nextToken();
            var roomType = st.nextToken();

           // System.out.println("Message: " + message);
            System.out.println("Log - vlez vo prostorija od strana na: ");
            System.out.println("\t\t\t Tip na korisnik: " + userType);
            System.out.println("\t\t\t Ime na korisnik: " + name);
            System.out.println("\t\t\t Vid na prostorija: " + roomType);
            System.out.println("\t\t\t Broj na prostorija: " + roomNumber);
        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});

    }
}
