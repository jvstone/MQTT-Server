package MqttServer;

public class Topic {
   private String topicName;
   private byte QOS;


   public Topic(String topicName, byte QOS) {
      this.topicName = topicName;
      this.QOS = QOS;
   }


   public String getTopicName() {
      return topicName;
   }

   public byte getQOS() {
      return QOS;
   }
}
