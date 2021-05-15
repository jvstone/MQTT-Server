package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;

import java.io.DataOutputStream;

public class UnsubAckMessage extends Message {

   public UnsubAckMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);
   }

   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {
      try {
         DataOutputStream dataOutputStream = client.getDataOutputStream();
         dataOutputStream.write(getType() << 4);
         dataOutputStream.write(getLength());
         dataOutputStream.write(getMessageContents());

      }catch (Exception e){
         System.out.println(e.getMessage());
      }

   }
}
