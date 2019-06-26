import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.Charset;

public class UDP_Multicast_Sender {
    public UDP_Multicast_Sender() {
    }

    static void sendData(String group, int port, String mesg) {
        byte ttl = 1;

        try {
            MulticastSocket s = new MulticastSocket();
            byte[] buf = mesg.getBytes(Charset.forName("UTF-8"));
            DatagramPacket pack = new DatagramPacket(buf, buf.length, InetAddress.getByName(group), port);
            s.send(pack, (byte)ttl);
            s.close();
        } catch (IOException var7) {
            var7.printStackTrace();
            MainFrame.add2output(var7.getMessage());
        }

    }
}
