import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class UDP_Multicast_Receiver extends Thread {
    private int port;
    private String group;
    private MulticastSocket s;
    private boolean isReceiving = false;
    private String nick;
    private boolean isReserved;
    private long timeToLive = 5000;

    boolean isNickBusy() {
        return isNickBusy;
    }

    private boolean isNickBusy = false;

    UDP_Multicast_Receiver(String nick) {
        this.nick = nick;
    }

    String getNick() {
        return nick;
    }

    MulticastSocket getS() {
        return s;
    }

    void Start(String group, int port) {
        this.port = port;
        this.group = group;
        this.isReserved = false;

        try {
            this.s = new MulticastSocket(this.port);
            this.s.setReuseAddress(true);
            this.s.setSoTimeout(2500);
            this.s.joinGroup(InetAddress.getByName(this.group));
            this.start();
        } catch (IOException e) {
            e.printStackTrace();
            MainFrame.add2output(e.getMessage());
        }

    }

    void Stop() {
        try {
            this.isReceiving = false;
            this.s.leaveGroup(InetAddress.getByName(this.group));
            this.s.close();
        } catch (IOException e) {
            if (!e.getMessage().equalsIgnoreCase("Socket is closed")) {
                e.printStackTrace();
                MainFrame.add2output(e.getMessage());
            }
        }
    }

    public void run() {
        byte[] buf = new byte[1024];
        DatagramPacket pack = new DatagramPacket(buf, buf.length);
        Thread thread = new Thread(() -> {
            long end = System.currentTimeMillis() + timeToLive;
            UDP_Multicast_Sender.sendData(this.group, this.port, "NICK " + this.nick);
            while (end > System.currentTimeMillis() || !this.isNickBusy){
                try {
                    this.s.receive(pack);
                    String message = new String(pack.getData(), pack.getOffset(), pack.getLength());
                    String[] splittedMessage = message.split(" ");
                    if (splittedMessage[splittedMessage.length - 1].equalsIgnoreCase("BUSY")) {
                        this.isNickBusy = true;
                    }
                } catch (IOException e) {
                    if (!e.getMessage().equalsIgnoreCase("Receive timed out")) {
                        e.printStackTrace();
                        MainFrame.add2output(e.getMessage());
                    }
                }
            }

        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e){

        }
        /*for (int i = 0; i < 2; i++) {
            try {
                this.s.receive(pack);
                String message = new String(pack.getData(), pack.getOffset(), pack.getLength());
                String[] splittedMessage = message.split(" ");
                if (splittedMessage[splittedMessage.length - 1].equalsIgnoreCase("BUSY")) {
                    this.isNickBusy = true;
                }
            } catch (IOException e) {
                if (!e.getMessage().equalsIgnoreCase("Receive timed out")) {
                    e.printStackTrace();
                    MainFrame.add2output(e.getMessage());
                }
            }
        }*/

        if (this.s.isClosed())
            return;

        if (!this.isNickBusy) {
            this.isReceiving = true;
            MainFrame.add2output("Start listening on " + this.group + ":" + this.port);
            this.isReserved = true;
        } else {
            MainFrame.add2output("Nick is busy!");
            this.Stop();
        }

        while(this.isReceiving && !this.s.isClosed()) {
            try {
                this.s.receive(pack);
                String message = new String(pack.getData(), pack.getOffset(), pack.getLength());
                String[] splittedMessage = message.split(" ");
                if (splittedMessage[0].equalsIgnoreCase("NICK")) {
                    String newNick = message.split(" ", 2)[1];
                    if (this.nick.equalsIgnoreCase(newNick) && this.isReserved) {
                        UDP_Multicast_Sender.sendData(this.group, this.port, "NICK " + this.nick + " BUSY");
                    }
                } else {
                    if (splittedMessage[0].equalsIgnoreCase("MSG")){
                        MainFrame.add2output(message);
                    }
                }
            } catch (IOException e) {
                if (!e.getMessage().equalsIgnoreCase("Receive timed out")) {
                    e.printStackTrace();
                    MainFrame.add2output(e.getMessage());
                }
            }
        }

    }
}
