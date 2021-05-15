package MqttServer;

import MqttServer.Messages.Message;
import MqttServer.Messages.MessageFactory;
import MqttServer.Messages.PublishMessage;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class MqttServer {

   int portNum;
   private ConcurrentHashMap<String, Client> clients;
   private List<PublishMessage> retainedMessages;

   public MqttServer(int portNum) {
      this.portNum = portNum;
      clients = new ConcurrentHashMap<String, Client>();
      retainedMessages = Collections.synchronizedList(new LinkedList<>());

   }

   public void startServer() {
      ServerSocket serverSocket = null;
      Socket socket = null;

      try {
         serverSocket = new ServerSocket(portNum);

         while (true) {
            try {
               socket = serverSocket.accept();
               new Thread(new Session(socket, this)).start();
            } catch (Exception e) {
               System.out.println("Error encountered: " + e.getMessage());
               break;
            }
         }
      } catch (Exception e) {
         System.out.println("Error encountered: " + e.getMessage());
      } finally {
         try{
            socket.close();
         }catch (IOException e){
            e.printStackTrace();
         }
      }

   }

   public ConcurrentHashMap<String, Client> getClients() {
      return clients;
   }

   /**
    * Add a new client and send a connection acknowledgement
    * @param clientID
    * @param client
    */
   public void addClient(String clientID, Client client){
      byte[] messageContents = new byte[2];
      if(clients.contains(clientID)){
         //disconnect
         removeClient(clientID);
         getClients().put(clientID, client);

      }else {
         clients.put(clientID, client);
      }
         messageContents[0] = 0;
         messageContents[1] = 0;
         Message connack = MessageFactory.buildMessage(Message.MESSAGE_TYPE_CONNACK,(byte)0, messageContents, 2);
         connack.messageHandler(client, this);

   }

   public void removeClient(String clientID){
      clients.remove(clientID);
   }

   /**
    * Add a message to retain
    * @param message
    */
   public void addRetainedMessage( PublishMessage message){
         if(containsRetainedTopic(message.getTopicName())){
            removeRetainedMessage(message.getTopicName());
         }
         retainedMessages.add(message);
   }

   /**
    * Determine if there is a retained message for the topic
    * @param topic
    * @return
    */
   public boolean containsRetainedTopic(String topic){
      for(PublishMessage m : getRetainedMessages()){
         if(m.getTopicName().matches(topic)){
            return true;
         }
      }
      return false;
   }

   /**
    * Remove retained messages that have are for a topic that matches topicName
    * @param topicName
    */
   public void removeRetainedMessage(String topicName){
         retainedMessages.removeIf(m -> m.getTopicName().matches(topicName));

   }

   public List<PublishMessage> getRetainedMessages(){
      return retainedMessages;
   }

   public static void main(String[] args) {
      int portNum;
      MqttServer broker;

      if (args.length < 1) {
         System.out.println("Not enough arguments provided");
      } else {
         portNum = Integer.parseInt(args[0]);
         broker = new MqttServer(portNum);
         broker.startServer();
      }
   }

}
