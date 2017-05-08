package JGT.GUI.HotelAdministrator;

import JGT.GUI.User.NewReservation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class RoomAssign extends JFrame
{
    private Container contentPane;

    private JLabel labelRoomNo;
    private JTextField textFieldRoomNo;
    private JButton buttonSubmit;

    private static Socket socket;
    private static DataOutputStream outputToServer;
    private static BufferedReader inputFromServer;

    public RoomAssign(Socket socket, String[] row)
    {
        super("Room Assign");
        this.socket = socket;

        contentPane = getContentPane();
        setBounds(0, 0, 300, 120);
        contentPane.setLayout(null);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        labelRoomNo = new JLabel("Room No. ", SwingConstants.RIGHT);
        labelRoomNo.setBounds(10, 10, 80, 15);
        labelRoomNo.setForeground(Color.BLACK);
        contentPane.add(labelRoomNo);

        textFieldRoomNo = new JTextField();
        textFieldRoomNo.setBounds(100, 8, 178, 20);
        textFieldRoomNo.setColumns(10);
        contentPane.add(textFieldRoomNo);

        buttonSubmit = new JButton("Submit");
        buttonSubmit.setBounds(75, 50, 150, 30);
        contentPane.add(buttonSubmit);

        buttonSubmit.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try {
                    String respond = "";
                    outputToServer = new DataOutputStream(socket.getOutputStream());
                    inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    outputToServer.writeBytes("<!UPDATE_ROOM_INFORMATION!>[" + row[0] + "," + textFieldRoomNo.getText()+ ']' + '\n');

                    respond = inputFromServer.readLine();
                    MainAdminH.refreshTable();
                    dispose();
                }
                catch (Exception _e)
                {
                    _e.printStackTrace();
                }
            }
        });

        pack();
        setSize(300, 120);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
