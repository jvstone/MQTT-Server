package MqttServer;

public class Broker {

    public static void main(String[] args) {
        int portNum;
        MqttServer broker;
        portNum = args.length > 1 ? Integer.parseInt(args[0]) : 1883;
        broker = new MqttServer(portNum);
        broker.startServer();
    }
}

