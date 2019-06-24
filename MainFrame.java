import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.GroupLayout.Alignment;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class MainFrame extends JFrame {
    private static MainFrame frame;
    private static final long serialVersionUID = 1L;
    public static int clientsCount = 0;
    private String ip = "224.0.0.1";
    private int port = 10000;
    UDP_Multicast_Receiver receiver;
    JLabel labelNick = new JLabel("Nick");
    JLabel labelMsg = new JLabel("Message to sent");
    JLabel emptylabel = new JLabel("                ");
    JTextField textFieldNick = new JTextField("Nick");
    JTextField textFieldMesg = new JTextField("");
    JButton startButton = new JButton("Start listening");
    JButton stopButton = new JButton("Stop");
    JTextArea output = new JTextArea();
    JButton sendButton = new JButton("Send");

    MainFrame(){
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        Font font = new Font("Verdana", 1, 11);
        this.output.setFont(font);
        this.output.setForeground(Color.BLUE);
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(this.labelNick)
                        .addComponent(this.textFieldNick)
                        .addComponent(this.emptylabel)
                        .addComponent(this.startButton)
                        .addComponent(this.stopButton))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(this.labelMsg)
                        .addComponent(this.textFieldMesg)
                        .addComponent(this.sendButton))
                .addComponent(this.output)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.labelNick)
                        .addComponent(this.textFieldNick)
                        .addComponent(this.emptylabel)
                        .addComponent(this.startButton)
                        .addComponent(this.stopButton))
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(this.labelMsg)
                        .addComponent(this.textFieldMesg)
                        .addComponent(this.sendButton))
                .addComponent(this.output));
        this.stopButton.setEnabled(false);
        this.setTitle("UDP Multicast");
        this.setSize(560, 400);
        this.setDefaultCloseOperation(3);
        this.setLocationRelativeTo((Component)null);
        this.setVisible(true);

        if (MainFrame.this.receiver != null && MainFrame.this.receiver.isNickBusy()){
            MainFrame.this.startButton.setEnabled(true);
            MainFrame.this.stopButton.setEnabled(false);
            MainFrame.this.textFieldNick.setEnabled(true);
        }

        this.startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                MainFrame.this.startButton.setEnabled(false);
                MainFrame.this.stopButton.setEnabled(true);
                MainFrame.this.textFieldNick.setEnabled(false);
                MainFrame.frame.receiver = new UDP_Multicast_Receiver(MainFrame.this.textFieldNick.getText());
                MainFrame.this.receiver.Start(MainFrame.this.ip, MainFrame.this.port);
            }
        });
        this.stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.startButton.setEnabled(true);
                MainFrame.this.stopButton.setEnabled(false);
                MainFrame.this.textFieldNick.setEnabled(true);
                MainFrame.this.receiver.Stop();
            }
        });
        this.sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                UDP_Multicast_Sender.sendData(MainFrame.this.ip, MainFrame.this.port, MainFrame.this.receiver.getNick() + " " + MainFrame.this.textFieldMesg.getText());
            }
        });
    }

    public static void add2output(String str) {
        try {
            frame.output.getDocument().insertString(0, str + "\n", (AttributeSet)null);
        } catch (BadLocationException var2) {
            var2.printStackTrace();
        }

    }

    public static void refreshClientsCount(int val) {
        clientsCount += val;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                } catch (Exception var2) {
                    var2.printStackTrace();
                }
                MainFrame.frame = new MainFrame();
            }
        });
    }
}
