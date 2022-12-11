package Lab3RabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Unlocker {
    private static final String EXCHANGE_NAME = "finki";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Scanner in = new Scanner(System.in);
        System.out.println("Vnesete go vaseto ime:");
        var name = in.nextLine();
        System.out.println("1 za profesor, 2 za student");
        var userType = in.nextLine();
        System.out.println("1 za ucilnica, 2 za laboratorija, 3 za kancelarija");
        var roomType = in.nextLine();
        System.out.println("Vnesete broj na prostorija");
        var roomNumber = in.nextLine();

        userType = userType.equals("1") ? "profesor" : "student";
        if(roomType.equals("1"))
            roomType = "ucilnica";
        if(roomType.equals("2"))
            roomType = "laboratorija";
        if(roomType.equals("3"))
            roomType = "kancelarija";

        try(Connection connection = factory.newConnection()){
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME,"fanout");
            // usertype#name#roomnumber#roomtype
            String msg = userType + "#" + name + "#" + roomNumber + "#" + roomType;

            channel.basicPublish(EXCHANGE_NAME,"",null,msg.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + userType + "':'" + msg + "'");
        }

    }
}
