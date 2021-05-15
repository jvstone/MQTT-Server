package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;

public class DisconnectMessage extends  Message {

   public DisconnectMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);

   }


   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {
      client.setActive(false);
   }
}
