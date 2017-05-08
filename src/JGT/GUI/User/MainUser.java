package JGT.GUI.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class MainUser extends JFrame implements ActionListener
{
    private Container contentPane;

    private JButton buttonNewReservation;

    private JLabel labelGreeting;
    private JLabel labelTable;

    private static Socket socket;
    private static DataOutputStream outputToServer;
    private static BufferedReader inputFromServer;

    private static JTable tableUserReservations;
    private static DefaultTableModel modelUserReservations;

    private JPopupMenu popupMenu;
    private JMenuItem menuItemEdit;
    private JMenuItem menuItemRemove;

    private static String username;

    public MainUser(Socket socket, String username) {
        super("Hotel Booking System");
        this.socket = socket;
        this.username = username;

        contentPane = getContentPane();
        setBounds(0, 0, 800, 400);
        contentPane.setLayout(null);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);


        /*
		 * Labels
		 */

        labelGreeting = new JLabel("Hello,  "+ getNameByUsername() + "!", SwingConstants.CENTER);
        labelGreeting.setBounds(200, 10, 400, 40);
        labelGreeting.setFont(new Font("Serif", Font.PLAIN, 30));
        labelGreeting.setForeground(Color.BLACK);
        contentPane.add(labelGreeting);

        labelTable = new JLabel("Your bookings: ", SwingConstants.RIGHT);
        labelTable.setBounds(10, 69, 100, 15);
        labelTable.setForeground(Color.BLACK);
        contentPane.add(labelTable);


        //headers for the table
        String[] columns = new String[] {
                "Reservation ID", "Name", "Hotel", "City", "Country", "Room Type", "Start Date", "End Date"
        };

        //create table with data
        modelUserReservations = new DefaultTableModel();
        modelUserReservations.setColumnIdentifiers(columns);
        try {
            getUserReservations();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        tableUserReservations = new JTable();
        tableUserReservations.setModel(modelUserReservations);
        tableUserReservations.setRowSelectionAllowed(true);
        tableUserReservations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableUserReservations.setDefaultEditor(Object.class, null);
        tableUserReservations.setBounds(20,100,750,200);
        JScrollPane scrollPane=new JScrollPane(tableUserReservations);
        scrollPane.setBounds(20,100,750,200);
        //add the table to the frame
        add(scrollPane);

        /*
		 * Button
		 */

        buttonNewReservation = new JButton("New reservation");
        buttonNewReservation.setBounds(325, 315, 150, 30);
        contentPane.add(buttonNewReservation);

        buttonNewReservation.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                NewReservation newReservation = new NewReservation(socket, username);
            }
        });

        /*
         * Table popup menu
         */

        popupMenu = new JPopupMenu();
        menuItemEdit = new JMenuItem("Edit");
        menuItemRemove = new JMenuItem("Remove");

        menuItemEdit.addActionListener(this);
        menuItemRemove.addActionListener(this);

        popupMenu.add(menuItemEdit);
        popupMenu.add(menuItemRemove);
        tableUserReservations.setComponentPopupMenu(popupMenu);
        tableUserReservations.addMouseListener(new JGT.GUI.TableMouseListener1(tableUserReservations));



        pack();
        setSize(800, 400);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    private static void getUserReservations() throws IOException
    {
        String respond = "";
        outputToServer = new DataOutputStream(socket.getOutputStream());
        inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        outputToServer.writeBytes("<!GET_USER_RESERVATIONS!>["+username+"]" + '\n');

        respond = inputFromServer.readLine();


        for(String line : respond.split(";")) {
            Object[] o = new Object[8];
            int index = 0;
            for (String data : line.split(",")) {
                if(index == 5) //Recognize room type
                {
                    if(data.equals("1"))
                        o[index] = "Double";
                    else
                        o[index] = "Single";
                }
                else
                    o[index] = data;

                index++;
            }
            index = 0;
            modelUserReservations.addRow(o);
        }
    }

    public static void refreshTable()
    {
        modelUserReservations.setRowCount(0);
        try {
            getUserReservations();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        tableUserReservations.setModel(modelUserReservations);
    }

    private static String getNameByUsername()
    {
        String respond = "";
        try {

            outputToServer = new DataOutputStream(socket.getOutputStream());
            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            outputToServer.writeBytes("<!GET_NAME!>[" + username + "]" + '\n');

            respond = inputFromServer.readLine();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return respond;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemEdit) {
            editReservation();
        } else if (menu == menuItemRemove) {
            removeCurrentRow();
        }
    }

    private void editReservation()
    {
        int selectedRow = tableUserReservations.getSelectedRow();

        JGT.GUI.User.EditReservation editReservation = new JGT.GUI.User.EditReservation(socket,username, getRowAt(selectedRow));
    }

    private void removeCurrentRow()
    {
        int selectedRow = tableUserReservations.getSelectedRow();
        String[] result = getRowAt(selectedRow);


        String message = "Are you sure? Delete operation is irreversible.";
        int reply = JOptionPane.showConfirmDialog(null, message, "Confirm cancellation", JOptionPane.YES_NO_OPTION);
        try
        {
            if (reply == JOptionPane.YES_OPTION)
            {
                String respond = "";
                outputToServer = new DataOutputStream(socket.getOutputStream());
                inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                outputToServer.writeBytes("<!DELETE_RESERVATION!>[" + result[0] + "] " + '\n');

                respond = inputFromServer.readLine();
            }
            getUserReservations();
            refreshTable();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public String[] getRowAt(int row) {
        String[] result = new String[8];

        for (int i = 0; i < 8; i++) {
            result[i] = tableUserReservations.getModel().getValueAt(row, i).toString();
        }

        return result;
    }


}

