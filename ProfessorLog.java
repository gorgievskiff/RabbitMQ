package Lab3RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;

public class ProfessorLog {
    private static final String EXCHANGE_NAME = "finki";

    public static void main(String[] args) throws IOException, TimeoutException {

        var pathFile = "E:\\Distribuirani sistemi\\src\\Lab3RabbitMQ\\Professor_log.csv";
        Path fileName = Path.of("E:\\Distribuirani sistemi\\src\\Lab3RabbitMQ\\Professor_log.csv");




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
            if(userType.equals("profesor")){
                var time = LocalDateTime.now();
                var name = st.nextToken();
                var roomNumber = st.nextToken();
                var roomType = st.nextToken();

                var csvString = "\n" + time + "," + roomType + "-" + roomNumber + "," + name;

                Files.writeString(fileName,csvString,StandardOpenOption.APPEND);


                System.out.println("Log - vlez vo prostorija od strana na: ");
                System.out.println("\t\t\t Tip na korisnik: " + userType);
                System.out.println("\t\t\t Ime na korisnik: " + name);
                System.out.println("\t\t\t Vid na prostorija: " + roomType);
                System.out.println("\t\t\t Broj na prostorija: " + roomNumber);
                System.out.println("\t\t\t VREME: " + time);

            }

        };
        channel.basicConsume(queueName,true,deliverCallback,consumerTag -> {});

    }

}
