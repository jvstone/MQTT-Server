package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;

import java.io.OutputStream;

public class PubAckMessage extends Message {

   public PubAckMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);

   }

   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {
      OutputStream outputStream = client.getDataOutputStream();
      try {
         outputStream.write(getType() << 4);
         outputStream.write(getLength());
         outputStream.write(getMessageContents());
      }catch (Exception e){
         System.out.println("Error Sending Publish Acknowledgement " + e.getMessage());
      }


   }
}
