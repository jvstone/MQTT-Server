package MqttServer;

import MqttServer.Messages.ConnectMessage;
import MqttServer.Messages.Message;
import MqttServer.Messages.MessageFactory;


import java.io.InputStream;
import java.net.Socket;

public class Session implements Runnable {

   private Socket socket;
   private MqttServer server;

   public Session(Socket socket, MqttServer server) {
      this.socket = socket;
      this.server = server;
   }

   public void run() {

      try {
         InputStream inputStream = socket.getInputStream();
         Message message = MessageFactory.buildMessageFromClient(inputStream);

         if (message.getType() == Message.MESSAGE_TYPE_CONNECT) {
            ConnectMessage connect = (ConnectMessage) message;
            Client client = new Client(socket, server);

            //Check if this client has connected to the server before and if the session is clean
            if (server.getClients().containsKey(connect.getClientID()) && ((ConnectMessage) message).getCleanSession() == 0) {
               Client currentClient = server.getClients().get(connect.getClientID());
               client.setSubscribedTopics(currentClient.getSubscribedTopics());

            }
            message.messageHandler(client, server);

            client.run();
         }


      } catch (Exception e) {
         System.out.println("Error : " + e.getMessage());
         e.printStackTrace();
      }

   }
}
