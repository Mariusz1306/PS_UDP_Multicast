import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDP_Multicast_Receiver extends Thread {
    int port;
    String group;
    MulticastSocket s;
    boolean isReceiving = false;
    private String nick;
    boolean isReserved; //TODO

    public boolean isNickBusy() {
        return isNickBusy;
    }

    private boolean isNickBusy = false;

    public UDP_Multicast_Receiver(String nick) {
        this.nick = nick;
    }

    public void Start(String group, int port) {
        this.port = port;
        this.group = group;

        try {
            this.s = new MulticastSocket(this.port);
            this.s.setReuseAddress(true);
            this.s.setSoTimeout(1000);
            this.s.joinGroup(InetAddress.getByName(this.group));
            this.start();
        } catch (IOException var4) {
            var4.printStackTrace();
            MainFrame.add2output(var4.getMessage());
        }

    }

    public void Stop() {
        try {
            this.isReceiving = false;
            this.s.leaveGroup(InetAddress.getByName(this.group));
            this.s.close();
        } catch (IOException var2) {
            var2.printStackTrace();
            MainFrame.add2output(var2.getMessage());
        }

    }

    public void run() {
        byte[] buf = new byte[1024];
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
        this.isReceiving = true;
        MainFrame.add2output("Start listening on " + this.group + ":" + this.port);

        while(this.isReceiving) {
            try {
                this.s.receive(pack);
                String message = new String(pack.getData(), pack.getOffset(), pack.getLength());
                String splittedMessage[] = message.split(" ");
                if (splittedMessage[0].equalsIgnoreCase("nick") && !splittedMessage[splittedMessage.length-1].equalsIgnoreCase("busy")){
                    String newNick = message.split(" ", 2)[1];
                    if (this.nick.equalsIgnoreCase(newNick));
                    UDP_Multicast_Sender.sendData(this.group, this.port, "NICK " + newNick + " BUSY");
                } else if (splittedMessage[0].equalsIgnoreCase("nick") && splittedMessage[splittedMessage.length-1].equalsIgnoreCase("busy")){
                    this.isNickBusy = true;
                } else {
                    MainFrame.add2output("    " + message);
                    MainFrame.add2output("Received " + pack.getLength() + " from " + pack.getAddress().toString() + ":" + pack.getPort());
                }
            } catch (IOException var4) {
                if (!var4.getMessage().equalsIgnoreCase("Receive timed out")) {
                    var4.printStackTrace();
                    MainFrame.add2output(var4.getMessage());
                }
            }
        }

    }
}
