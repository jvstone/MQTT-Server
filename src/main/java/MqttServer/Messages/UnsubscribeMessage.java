package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;


import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.LinkedList;

public class UnsubscribeMessage extends Message {

   private String topicName;
   private byte qos;
   private byte messageIdMSB;
   private byte messageIdLSB;
   private byte topicQOS;
   private LinkedList<String> topicList;

   public UnsubscribeMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);
      qos = (byte) ((flags & 0x06) >> 1);
      topicList = new LinkedList();

      ByteArrayInputStream inputStream = new ByteArrayInputStream(messageContents);
      DataInputStream dataInputStream = new DataInputStream(inputStream);

      int bytesRead = 0;
      try {
         messageIdMSB = dataInputStream.readByte();
         messageIdLSB = dataInputStream.readByte();
         bytesRead += 2;

         //Iterate while there are more topics
         while(bytesRead < getLength()) {
            topicName = dataInputStream.readUTF();
            bytesRead = bytesRead + (2 + topicName.length());
            topicList.add(topicName);
         }

      }catch (Exception e){
         System.out.println("Error reading Subscribe message: " + e.getMessage());
      }
   }


   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {

      byte[] content = new byte[2];
      content[0] = messageIdMSB;
      content[1] = messageIdLSB;

      for(int i = 0;  i < topicList.size(); i++){
         client.removeSubscription(topicList.get(i));
      }

      UnsubAckMessage unsubAck = new UnsubAckMessage(Message.MESSAGE_TYPE_UNSUBACK , (byte)0, content , content.length );
      unsubAck.messageHandler(client, mqttServer);

   }

}
