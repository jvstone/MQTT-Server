package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;
import MqttServer.Topic;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.LinkedList;


public class SubscribeMessage extends Message {
   private String topicName;
   private byte dupFlag;
   private byte qos;
   private byte messageIdMSB;
   private byte messageIdLSB;
   private byte topicQOS;
   private LinkedList<Topic> topicList;

   public SubscribeMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);
      dupFlag = (byte) ((flags & 0x08) >> 3);
      qos = (byte) ((flags & 0x06) >> 1);
      topicList = new LinkedList<Topic>();
      ByteArrayInputStream inputStream = new ByteArrayInputStream(messageContents);
      DataInputStream dataInputStream = new DataInputStream(inputStream);

      int bytesRead = 0;
      try {
         messageIdMSB = dataInputStream.readByte();
         messageIdLSB = dataInputStream.readByte();
         bytesRead += 2;

         //Iterate while there are more topics
         do {
            topicName = dataInputStream.readUTF();
            topicQOS = dataInputStream.readByte();
            bytesRead = bytesRead + (3 + topicName.length());

            if(isValidTopic(topicName)){
               topicList.add(new Topic(topicName, topicQOS));
            }
         }while(bytesRead < getLength());
      }catch (Exception e){
         System.out.println("Error reading Subscribe message: " + e.getMessage());
      }
   }
   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {

      byte[] content = new byte[2 + topicList.size()];
      content[0] = messageIdMSB;
      content[1] = messageIdLSB;

      //Iterate over the topic list to build up the content of the suback and add subscriptions to the client
      for(int i = 0;  i < topicList.size(); i++){
         content[i+2] = topicList.get(i).getQOS();
         client.addSubscriptions(topicList.get(i));
      }
      content[2]= topicQOS;
      SubAckMessage subAck = new SubAckMessage(Message.MESSAGE_TYPE_SUBACK , (byte)0, content , content.length );
      subAck.messageHandler(client, mqttServer);

      for(PublishMessage m: mqttServer.getRetainedMessages()){
         if(client.topicMatches(m.getTopicName()) != null){
            m.sendPublishMessage(client);
         }

      }

   }

   /**
    * Check if the topic has a valid format
    * @return
    */
   private boolean isValidTopic(String topic){
      String[] subTopics =  topic.split("/");
      for(int i = 0; i < subTopics.length ;i++){
         if(subTopics[i].equals("#")&& i != subTopics.length - 1){
            return false;
         }
         if(subTopics[i].contains("#") && !subTopics[i].equals("#")  ){
            return  false;
         }
         if(subTopics[i].contains("+") && !subTopics[i].equals("+")  ){
            return  false;
         }

      }
      return true;
   }
}
