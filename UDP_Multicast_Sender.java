import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;

public class UDP_Multicast_Sender {
    public UDP_Multicast_Sender() {
    }

    static void sendData(String group, int port, String mesg) {
        try {
            MulticastSocket s = new MulticastSocket();
            byte[] buf = mesg.getBytes(Charset.forName("UTF-8"));
            DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(group), port);
            s.send(pack);
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
            MainFrame.add2output(e.getMessage());
        }

    }
}
