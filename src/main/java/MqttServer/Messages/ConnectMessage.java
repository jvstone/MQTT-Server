package MqttServer.Messages;

import MqttServer.Client;
import MqttServer.MqttServer;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class ConnectMessage extends Message {
   private  String clientID;
   private String protocol;
   private  byte version;
   private byte connect_flags;
   private byte keepAliveMSB;
   private byte keepAliveLSB;


   public ConnectMessage(byte type, byte flags,byte[] messageContents, int messageLength){
      super(type,flags, messageContents, messageLength);
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(messageContents);
      DataInputStream inputStream = new DataInputStream(byteArrayInputStream);
      try {
         protocol = inputStream.readUTF();
         version = inputStream.readByte();
         connect_flags = inputStream.readByte();
         keepAliveMSB = inputStream.readByte();
         keepAliveLSB = inputStream.readByte();
         clientID = inputStream.readUTF();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   @Override
   public void messageHandler(Client client, MqttServer mqttServer) {
      client.setId(clientID);
      mqttServer.addClient(getClientID(), client);
   }

   public String getClientID() {
      return clientID;
   }

   public byte getCleanSession() {
      return (byte) (connect_flags & 0x02);
   }
}
