package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;

import java.io.DataOutputStream;

public class ConnackMessage extends Message {
   byte returnCode;
   byte [] output;
   byte CONNACK_REMAING_LENGTH = 2;

   public ConnackMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);
      output = messageContents;
      this.returnCode = messageContents[1];


   }

   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {
      DataOutputStream dataOutputStream;

      try {
         dataOutputStream = client.getDataOutputStream();
         dataOutputStream.write(getType() << 4);
         dataOutputStream.write(CONNACK_REMAING_LENGTH);
         dataOutputStream.write(output);

      }catch (Exception e){
         System.out.println("Error sending ConnAck Message: " + e.getMessage());
         e.printStackTrace();
      }
   }
}
