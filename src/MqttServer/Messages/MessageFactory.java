package MqttServer.Messages;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.SocketException;

public class MessageFactory {


   public static Message buildMessageFromClient(InputStream inputStream) {

      try {
         DataInputStream dataInputStream = new DataInputStream(inputStream);
         int first = dataInputStream.readUnsignedByte();
         byte type = (byte) (first >> 4);
         byte flag = (byte) (first & 15);
         int length = getMessageLength(dataInputStream);
         byte[] contents = new byte[length];
         if (length > 0) {
            dataInputStream.read(contents);
         }
         return buildMessage(type, flag, contents, length);

      } catch (SocketException se) {
         return null;
      } catch (Exception e) {
         System.out.println("Error encountered: " + e.getMessage());
         e.printStackTrace();
      }
      return null;
   }

   public static Message buildMessage(byte type, byte flags, byte[] messageContents, int messageLength) {
      switch (type) {
         case Message.MESSAGE_TYPE_CONNECT:
            return new ConnectMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_CONNACK:
            return new ConnackMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_PUBLISH:
            return new PublishMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_PUBACK:
            return new PubAckMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_SUBSCRIBE:
            return new SubscribeMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_SUBACK:
            return new SubAckMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_UNSUBSCRIBE:
            return new UnsubscribeMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_UNSUBACK:
            return new UnsubAckMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_PINGREQ:
            return new PingReqMessage(type, flags, messageContents, messageLength);
         case Message.MESSAGE_TYPE_DISCONNECT:
            return new DisconnectMessage(type, flags, messageContents, messageLength);
      }

      return null;
   }

   /***
    * Get the length of a message from a DataInput Stream
    * @param dataInputStream
    * @return
    */
   public static int getMessageLength(DataInputStream dataInputStream) {
      int multiplier = 1;
      int value = 0;
      byte encodedByte;
      try {
         do {
            encodedByte = (byte) dataInputStream.readUnsignedByte();
            value += (encodedByte & 127) * multiplier;
            multiplier *= 128;

            if (multiplier > 128 * 128 * 128) {
               throw new Error("Malformed Remaining Length");
            }

         } while ((encodedByte & 128) != 0);
      } catch (Exception e) {
         System.out.println("Error encountered: " + e.getMessage());

      }
      return value;
   }
}