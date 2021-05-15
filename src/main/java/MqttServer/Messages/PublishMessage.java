package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;
import MqttServer.Topic;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PublishMessage extends Message {
  private byte dupFlag;
   private byte qos;
   private byte retainFlag;
   private String topicName;
   private byte topicNameMsb;
   private byte topicNameLsb;
   private byte packetIdMsb;
   private byte packetIdLsb;
   private byte[] messagePayload;
   byte[] topicArr;

   public PublishMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);
      dupFlag = (byte) ((flags & 0x08) >> 3);
      qos = (byte) ((flags & 0x06) >> 1);
      retainFlag = (byte) (flags & 0x01);

      ByteArrayInputStream inputStream = new ByteArrayInputStream(messageContents);
      DataInputStream dataInputStream = new DataInputStream(inputStream);

      try {
         topicNameMsb = dataInputStream.readByte();
         topicNameLsb = dataInputStream.readByte();

         //Get the length of the topic name
         int nameLength = ((int) topicNameMsb << 4) | topicNameLsb;

         //Read topic name into an array of bytes
         topicArr = new byte[nameLength];
         dataInputStream.read(topicArr);

         //Convert to string
         topicName = new String(topicArr, "UTF-8");

         if(qos > 0){
            packetIdMsb = dataInputStream.readByte();
            packetIdLsb = dataInputStream.readByte();

         }
         int lengthSoFar = 2 + nameLength +(qos == 0? 0:2);
         messagePayload = new byte[getLength() - lengthSoFar];
         dataInputStream.read(messagePayload);


      }catch (Exception e){
         System.out.println("Error reading Publish message: " + e.getMessage());
      }
   }

   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {
      //Do nothing it the topic name contains a wildcard character
      if(topicName.contains("+") || topicName.contains("#")){
         return;
      }
      //TODO: Handle retaining messages
      if(getRetainFlag() == 1){
         if( messagePayload.length > 0) {
            mqttServer.addRetainedMessage(this);
         }else{
            mqttServer.removeRetainedMessage(getTopicName());
         }

      }


      if(qos == 1){
         byte[] ackContents = new byte[]{packetIdMsb, packetIdLsb};
         PubAckMessage pubAck = new PubAckMessage(Message.MESSAGE_TYPE_PUBACK,(byte)0,ackContents, (byte)2  );
         pubAck.messageHandler(client, mqttServer);
      }

      //Publish to clients that are subscribed to the topic
      for(Client cl:mqttServer.getClients().values()){
         if(cl.isActive())
            sendPublishMessage(cl);
      }

   }

   public  void sendPublishMessage(Client client){
      Topic clientTopic= client.topicMatches(topicName);
      if(clientTopic != null){
         DataOutputStream outputStream = client.getDataOutputStream();
         try {
            outputStream.write((getType() << 4)  |(getDupFlag()<< 3) |(clientTopic.getQOS() << 1)| getRetainFlag());

            //Special case when client topic has a QOS of 0 and published message does not
            if(clientTopic.getQOS() == 0 && getQos() > 0){
               outputStream.write(getLength() - 2);
            }else {
               outputStream.write(getLength());
            }
            outputStream.write(topicNameMsb);
            outputStream.write(topicNameLsb);
            outputStream.write(topicArr);

            if(clientTopic.getQOS() > 0) {
               outputStream.write(packetIdMsb);
               outputStream.write(packetIdLsb);

            }
            outputStream.write(messagePayload);
         }catch (Exception e){
            System.out.println("Error sending publish message: " + e.getMessage());
            e.printStackTrace();
         }
      }

   }


   public byte getDupFlag() {
      return dupFlag;
   }

   public byte getQos() {
      return qos;
   }

   public byte getRetainFlag() {
      return retainFlag;
   }

   public String getTopicName() {
      return topicName;
   }

   public String payloadToString(){
      return new String(messagePayload);
   }
}
