package org.tshark.tsz.rabbitmq.customer;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author tanshizhen
 */
public class GerritEventCustomer {

    private Channel channel;

    private Connection conn;

    private ConnectionFactory connectionFactory;

    private String id;


    public GerritEventCustomer(String id)
        throws IOException, TimeoutException {
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        connectionFactory.setPort(5672);
        connectionFactory.setVirtualHost("/");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setAutomaticRecoveryEnabled(true);

        conn = connectionFactory.newConnection();
        conn.addShutdownListener(new ShutdownListener() {
            @Override public void shutdownCompleted(
                ShutdownSignalException cause) {
            }
        });
        this.id = id;
        initChannel();
    }

    private void initChannel() {
        try {
            if (null == conn || !conn.isOpen()) {
                conn = connectionFactory.newConnection();
            }

            if (null == this.channel || !this.channel.isOpen())  {
                this.channel = conn.createChannel();
            }

            String  queueName ="localhost-queue-" + id;
            this.channel.exchangeDeclare("localhost-test", "topic", true, false, null);
            this.channel.queueDeclare(queueName, true,false,false, null);
            this.channel.queueBind(queueName, "localhost-test", "GERRIT_EVENT");
            this.channel.basicQos(0,10, false);
            this.channel.basicConsume(queueName, false, new Consumer() {
                @Override public void handleConsumeOk(String consumerTag) {

                }

                @Override public void handleCancelOk(String consumerTag) {

                }

                @Override public void handleCancel(String consumerTag)
                    throws IOException {

                }

                @Override public void handleShutdownSignal(String consumerTag,
                    ShutdownSignalException sig) {
                }

                @Override public void handleRecoverOk(String consumerTag) {

                }

                @Override public void handleDelivery(String consumerTag,
                    Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                    System.out.println(new String(body, "utf-8"));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            });
        } catch (Exception e) {
            System.out.println("--------------------");
            e.printStackTrace();
        }

    }
}
