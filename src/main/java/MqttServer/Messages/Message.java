package MqttServer.Messages;
import MqttServer.*;

public abstract class Message {
   public static final byte MESSAGE_TYPE_CONNECT = 1;
   public static final byte MESSAGE_TYPE_CONNACK = 2;
   public static final byte MESSAGE_TYPE_PUBLISH = 3;
   public static final byte MESSAGE_TYPE_PUBACK = 4;
   public static final byte MESSAGE_TYPE_PUBREC = 5;
   public static final byte MESSAGE_TYPE_PUBREL = 6;
   public static final byte MESSAGE_TYPE_PUBCOMP = 7;
   public static final byte MESSAGE_TYPE_SUBSCRIBE = 8;
   public static final byte MESSAGE_TYPE_SUBACK = 9;
   public static final byte MESSAGE_TYPE_UNSUBSCRIBE = 10;
   public static final byte MESSAGE_TYPE_UNSUBACK = 11;
   public static final byte MESSAGE_TYPE_PINGREQ = 12;
   public static final byte MESSAGE_TYPE_PINGRESP = 13;
   public static final byte MESSAGE_TYPE_DISCONNECT = 14;

   private byte type;
   private byte flags;
   private int length;
   private byte[] messageContents;

   public  Message(byte type, byte flags, byte[] messageContents, int messageLength){
      this.type = type;
      this.flags = flags;
      this.length = messageLength;
      this.messageContents = messageContents;
   }

 
   public byte getType() {
      return type;
   }

   public byte getFlags(){
      return flags;
   }

   public int getLength() {
      return length;
   }

   public byte[] getMessageContents() {
      return messageContents;
   }

   public abstract void messageHandler(Client client, MqttServer mqttServer);
}
