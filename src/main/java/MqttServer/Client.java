package MqttServer;

import MqttServer.Messages.*;
import MqttServer.Messages.MessageFactory;

import java.io.*;
import java.net.Socket;
import  java.util.*;

public class Client  {
   private Socket socket;
   private MqttServer broker;
   private String id;

   private List<Topic> subscribedTopics;
   private InputStream inputStream;
   private DataOutputStream dataOutputStream;
   private boolean isActive;

   public Client(Socket socket, MqttServer broker) {
      this.socket = socket;
      this.broker = broker;
      subscribedTopics = new LinkedList<Topic>();
      try {
         inputStream = socket.getInputStream();
         dataOutputStream = new DataOutputStream(socket.getOutputStream());
      }catch (Exception e){
         System.out.println("Error creating client " + e.getMessage());
      }

   }

   public void setId(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public Socket getSocket() {
      return socket;
   }

   public void setSocket(Socket socket){
      this.socket = socket;
   }

   /**
    * Add a new topic to the list of subscriptions
    * @param newTopic
    */
   public  void addSubscriptions(Topic newTopic){
      for (Topic topic: subscribedTopics) {
         if(topic.getTopicName().matches(newTopic.getTopicName())){
            return;
         }
      }
      subscribedTopics.add(newTopic);
   }

   /**
    * Remove a topic the client is subscribed to
    * @param topic
    */
   public void removeSubscription(String topic){
      for (int t = 0; t < subscribedTopics.size(); t++) {
         if(subscribedTopics.get(t).getTopicName().matches(topic)){
            subscribedTopics.remove(t);
            break;
         }
      }
   }

   /**
    * Check is topicName matches a topic that the client is subscribed to.
    * @param topicName
    * @return
    */
   public Topic topicMatches(String topicName){
      //Iterate over topics client is subscribed to and replace '+' wild card with regular expression
      for (Topic t:subscribedTopics) {
         String topics = t.getTopicName();
         topics = topics.replaceAll("\\+", "[A-z0-9_\\\\-\\\\s]*");
         //if it matches then we are done
         if( topicName.matches(topics))
            return t;

         //If  the topic ends with the '#' wild card, have to take into consideration that + wildcard may also have been used
         if(topics.endsWith("#") ){
            //Spilt both subscription and topic name  on '/'
            String[] topicsHierarchy = topics.split("/");
            String[] topicNameHierarchy = topicName.split("/");

            //if topic name hierarchy is shorter than it is not a match
            if(topicNameHierarchy.length < topicsHierarchy.length){
               continue;
            }
            //Check if values for both match until '#'
            for(int i = 0; i <topicNameHierarchy.length && i < topicsHierarchy.length - 1; i++){
               if(!topicNameHierarchy[i].matches(topicsHierarchy[i])){
                  continue;
               }
            }
            return t;
         }
      }
      return null;
   }


   public void run() {

      try {
         inputStream = socket.getInputStream();
         dataOutputStream = new DataOutputStream(socket.getOutputStream());
         isActive = true;
         while (true ) {
            Message message = MessageFactory.buildMessageFromClient(getInputStream());
            //If message is null, terminate connection
            if(message == null){
               break;
            }
            message.messageHandler(this, broker);

            if (message.getType() == Message.MESSAGE_TYPE_DISCONNECT) {
               break;
            }
         }
      } catch (Exception e) {
          System.out.println("Error getting message: " + e.getMessage());
          e.printStackTrace();

      } finally {
         try {
            //broker.removeClient(getId());
            isActive = false;
            socket.close();

         } catch (Exception e) {
            System.out.println(e.getMessage());
         }
      }
   }


   public InputStream getInputStream() {
      return inputStream;
   }

   public DataOutputStream getDataOutputStream() {
      return dataOutputStream;
   }

   public List<Topic> getSubscribedTopics() {
      return subscribedTopics;
   }


   public boolean isActive() {
      return isActive;
   }

   public void setActive(boolean active) {
      isActive = active;
   }

   public void setSubscribedTopics(List<Topic> topics){
      this.subscribedTopics = topics;
   }
}
