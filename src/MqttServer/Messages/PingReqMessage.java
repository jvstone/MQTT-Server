package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;

public class PingReqMessage extends  Message{
   public PingReqMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      super(type, flags, messageContents, messageLength);

   }

   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {
      new PingRespMessage(Message.MESSAGE_TYPE_PINGRESP, (byte)0, null, 0).messageHandler(client, mqttServer);
   }
}
