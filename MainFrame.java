import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.text.BadLocationException;

public class MainFrame extends JFrame {
    private static MainFrame frame;
    private static final long serialVersionUID = 1L;
    private String ip = "224.0.0.1";
    private int port = 10000;
    private UDP_Multicast_Receiver receiver;
    private JTextField textFieldNick = new JTextField("Nick");
    private JTextField textFieldMsg = new JTextField("");
    private JButton startButton = new JButton("Start listening");
    private JButton stopButton = new JButton("Stop");
    private JTextArea output = new JTextArea();

    private MainFrame(){
        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        Font font = new Font("Verdana", Font.BOLD, 11);
        this.output.setFont(font);
        this.output.setForeground(Color.BLUE);
        JLabel labelNick = new JLabel("Nick");
        JLabel labelMsg = new JLabel("Message to sent");
        JButton sendButton = new JButton("Send");
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(labelNick)
                        .addComponent(this.textFieldNick)
                        .addComponent(this.startButton)
                        .addComponent(this.stopButton))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(labelMsg)
                        .addComponent(this.textFieldMsg)
                        .addComponent(sendButton))
                .addComponent(this.output)));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(labelNick)
                        .addComponent(this.textFieldNick)
                        .addComponent(this.startButton)
                        .addComponent(this.stopButton))
                .addGroup(layout.createParallelGroup(Alignment.BASELINE)
                        .addComponent(labelMsg)
                        .addComponent(this.textFieldMsg)
                        .addComponent(sendButton))
                .addComponent(this.output));
        this.stopButton.setEnabled(false);
        this.setTitle("UDP Multicast");
        this.setSize(560, 400);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.startButton.addActionListener(e -> {
            MainFrame.this.startButton.setEnabled(false);
            MainFrame.this.stopButton.setEnabled(true);
            MainFrame.this.textFieldNick.setEnabled(false);
            MainFrame.frame.receiver = new UDP_Multicast_Receiver(MainFrame.this.textFieldNick.getText());
            MainFrame.this.receiver.Start(MainFrame.this.ip, MainFrame.this.port);
        });
        this.stopButton.addActionListener(e -> {
            MainFrame.this.startButton.setEnabled(true);
            MainFrame.this.stopButton.setEnabled(false);
            MainFrame.this.textFieldNick.setEnabled(true);
            MainFrame.this.receiver.Stop();
        });
        sendButton.addActionListener(e -> {
            if (!MainFrame.this.receiver.isNickBusy() && !MainFrame.this.receiver.getS().isClosed()) {
                UDP_Multicast_Sender.sendData(MainFrame.this.ip, MainFrame.this.port, "MSG " + MainFrame.this.receiver.getNick() + " " + MainFrame.this.textFieldMsg.getText());
            }
        });
    }

    static void add2output(String str) {
        try {
            frame.output.getDocument().insertString(0, str + "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            MainFrame.frame = new MainFrame();
        });
    }
}
