package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;


import java.io.OutputStream;

public class PingRespMessage extends Message {

   public PingRespMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);

   }

   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {
      OutputStream outputStream = client.getDataOutputStream();
      try {
         outputStream.write(getType()<<4);
         outputStream.write(0);
      }catch (Exception e){
         System.out.println("Error Sending Ping Response " + e.getMessage());
      }


   }
}
