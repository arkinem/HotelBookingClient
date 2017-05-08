package JGT.GUI.HotelAdministrator;

import JGT.GUI.User.NewReservation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;



public class MainAdminH extends JFrame implements ActionListener
{
    private Container contentPane;

    private JLabel labelGreeting;
    private JLabel labelTable;

    private static Socket socket;
    private static DataOutputStream outputToServer;
    private static BufferedReader inputFromServer;
    private static String username;

    private static JTable tableReservations;
    private static DefaultTableModel modelReservations;

    private JPopupMenu popupMenu;
    private JMenuItem menuItemRoom;


    public MainAdminH(Socket socket, String username) {
        super("Hotel Booking System [Hotel Administrator]");
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

        labelGreeting = new JLabel(getHotelNameById(), SwingConstants.CENTER);
        labelGreeting.setBounds(200, 10, 400, 40);
        labelGreeting.setFont(new Font("Serif", Font.PLAIN, 30));
        labelGreeting.setForeground(Color.BLACK);
        contentPane.add(labelGreeting);

        labelTable = new JLabel("Room reservations: ", SwingConstants.RIGHT);
        labelTable.setBounds(10, 69, 150, 15);
        labelTable.setForeground(Color.BLACK);
        contentPane.add(labelTable);


        //headers for the table
        String[] columns = new String[] {
                "Reservation ID", "Name", "Room Type", "Start Date", "End Date", "Room"
        };

        //create table with data
        modelReservations = new DefaultTableModel();
        modelReservations.setColumnIdentifiers(columns);
        try {
            getReservations();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        tableReservations = new JTable();
        tableReservations.setModel(modelReservations);
        tableReservations.setRowSelectionAllowed(true);
        tableReservations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableReservations.setDefaultEditor(Object.class, null);
        tableReservations.setBounds(20,100,750,200);
        JScrollPane scrollPane=new JScrollPane(tableReservations);
        scrollPane.setBounds(20,100,750,200);
        //add the table to the frame
        add(scrollPane);


        /*
         * Table popup menu
         */

        popupMenu = new JPopupMenu();
        menuItemRoom = new JMenuItem("Assign room");

        menuItemRoom.addActionListener(this);

        popupMenu.add(menuItemRoom);
        tableReservations.setComponentPopupMenu(popupMenu);
        tableReservations.addMouseListener(new JGT.GUI.TableMouseListener(tableReservations));


        pack();
        setSize(800, 400);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private static String getHotelNameById()
    {
        String respond = "";
        try {
            String hotelID = username.substring(6);
            outputToServer = new DataOutputStream(socket.getOutputStream());
            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            outputToServer.writeBytes("<!GET_HOTEL_NAME!>[" + hotelID + "]" + '\n');

            respond = inputFromServer.readLine();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return respond;
    }

    private static void getReservations() throws IOException
    {
        String respond = "";
        String hotelID = username.substring(6);
        outputToServer = new DataOutputStream(socket.getOutputStream());
        inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        outputToServer.writeBytes("<!GET_HOTEL_RESERVATIONS!>["+hotelID+"]" + '\n');

        respond = inputFromServer.readLine();


        for(String line : respond.split(";")) {
            Object[] o = new Object[6];
            int index = 0;
            for (String data : line.split(",")) {
                if(index == 2) //Recognize room type
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
            modelReservations.addRow(o);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        JMenuItem menu = (JMenuItem) event.getSource();
        if (menu == menuItemRoom) {
            assignRoomNumber();
        }
    }

    private void assignRoomNumber()
    {
        int selectedRow = tableReservations.getSelectedRow();

        JGT.GUI.HotelAdministrator.RoomAssign roomAssign = new JGT.GUI.HotelAdministrator.RoomAssign(socket, getRowAt(selectedRow));

    }


    public String[] getRowAt(int row)
    {
        String[] result = new String[6];

        try {
            for (int i = 0; i < 6; i++) {
                result[i] = tableReservations.getModel().getValueAt(row, i).toString();
            }
        }
        catch(NullPointerException e)
        {

            for (int i = 0; i < 5; i++) {
                result[i] = tableReservations.getModel().getValueAt(row, i).toString();
            }
            result[5] = "";
        }

        return result;
    }

    public static void refreshTable()
    {
        modelReservations.setRowCount(0);
        try {
            getReservations();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        tableReservations.setModel(modelReservations);
    }
}
