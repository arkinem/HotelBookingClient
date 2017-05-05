package JGT.GUI.User;


import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;

import static java.lang.Math.toIntExact;


public class EditReservation extends JFrame
{
    private Container contentPane;

    private JLabel labelReservationID;
    private JLabel labelHotelName;
    private JLabel labelRoomType;
    private JLabel labelStartDate;
    private JLabel labelEndDate;

    private JComboBox comboBoxDoubleSingle;

    private JXDatePicker pickerFrom;
    private JXDatePicker pickerTo;

    private Socket socket;
    private DataOutputStream outputToServer;
    private BufferedReader inputFromServer;

    private JButton buttonCheck;


    private String username;

    public EditReservation(Socket socket, String username, String[] row)
    {

        super("Edit Reservation");
        this.socket = socket;
        this.username = username;

        contentPane = getContentPane();
        setBounds(0, 0, 400, 300);
        contentPane.setLayout(null);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        /*
		 * Labels
		 */

        labelReservationID = new JLabel("Reservation ID: " + row[0].toString(), SwingConstants.CENTER);
        labelReservationID.setBounds(100, 10, 200, 20);
        labelReservationID.setFont(new Font("Serif", Font.PLAIN, 17));
        labelReservationID.setForeground(Color.BLACK);
        contentPane.add(labelReservationID);

        labelHotelName = new JLabel(row[2].toString() + ", " + row[3].toString(), SwingConstants.CENTER);
        labelHotelName.setBounds(100, 40, 200, 15);
        labelHotelName.setForeground(Color.BLACK);
        contentPane.add(labelHotelName);

        labelRoomType = new JLabel("Room type: ", SwingConstants.RIGHT);
        labelRoomType.setBounds(65, 75, 100, 13);
        labelRoomType.setForeground(Color.BLACK);
        contentPane.add(labelRoomType);

        labelStartDate = new JLabel("Date from: ", SwingConstants.RIGHT);
        labelStartDate.setBounds(95, 115, 70, 11);
        labelStartDate.setForeground(Color.BLACK);
        contentPane.add(labelStartDate);

        labelEndDate = new JLabel("to: ", SwingConstants.RIGHT);
        labelEndDate.setBounds(140, 138, 25, 11);
        labelEndDate.setForeground(Color.BLACK);
        contentPane.add(labelEndDate);

        /*
         * Combobox
         */
        String[] roomTypes = { "Single", "Double" };
        comboBoxDoubleSingle = new JComboBox(roomTypes);
        comboBoxDoubleSingle.setBounds(170, 72, 100, 20);
        contentPane.add(comboBoxDoubleSingle);

        /*
         * DataPicker
         */

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            pickerFrom = new JXDatePicker();
            pickerFrom.setDate(format.parse((row[6].toString())));
            pickerFrom.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
            pickerFrom.setBounds(170, 110, 100, 20);
            contentPane.add(pickerFrom);

            pickerTo = new JXDatePicker();
            pickerTo.setDate(format.parse((row[7].toString())));
            pickerTo.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
            pickerTo.setBounds(170, 136, 100, 20);
            contentPane.add(pickerTo);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        /*
		 * Button
		 */

        buttonCheck = new JButton("Check availability");
        buttonCheck.setBounds(125, 185, 150, 30);
        contentPane.add(buttonCheck);

        buttonCheck.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String message = "Are you sure? It could be irreversible.";

                int reply = JOptionPane.showConfirmDialog(null, message, "Confirm changes", JOptionPane.YES_NO_OPTION);
                try
                {
                    if (reply == JOptionPane.YES_OPTION)
                    {
                        String dateStart = (new SimpleDateFormat("dd/MM/yyyy").format(pickerFrom.getDate())).toString();
                        String dateEnd = (new SimpleDateFormat("dd/MM/yyyy").format(pickerTo.getDate())).toString();

                        if(dateStart.equals(dateEnd))
                        {
                            JOptionPane.showMessageDialog(null, "Dates should be different");
                        }
                        else
                        {
                            if ((Integer.parseInt(dateStart.split("/")[2]) > Integer.parseInt(dateEnd.split("/")[2])) ||
                                    (Integer.parseInt(dateStart.split("/")[2]) == Integer.parseInt(dateEnd.split("/")[2]) && Integer.parseInt(dateStart.split("/")[1]) > Integer.parseInt(dateEnd.split("/")[1])) ||
                                    (Integer.parseInt(dateStart.split("/")[2]) == Integer.parseInt(dateEnd.split("/")[2]) && Integer.parseInt(dateStart.split("/")[1]) == Integer.parseInt(dateEnd.split("/")[1]) && Integer.parseInt(dateStart.split("/")[0]) > Integer.parseInt(dateEnd.split("/")[0]))) {
                                JOptionPane.showMessageDialog(null, "End date must be after start date");
                            } else {
                                String respond = "";
                                outputToServer = new DataOutputStream(socket.getOutputStream());
                                inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                                String isDoubleRoom = String.valueOf(comboBoxDoubleSingle.getSelectedItem());

                                outputToServer.writeBytes("<!EDIT_RESERVATION!>[" + username + "," + row[2] + "," + row[3] + "," + row[4] + "," + isDoubleRoom + "," + dateStart + "," + dateEnd + "," + row[0] + ']' + '\n');

                                respond = inputFromServer.readLine();
                                if (respond.equals("DENIED")) {
                                    JOptionPane.showMessageDialog(null, "Unfortunatly nie mamy pokoju spelniajacego twoje wymagania, poszukaj jeszcze raz.");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Sukces");
                                }
                            }

                        }
                    }
                }
                catch(Exception ex)
                {
                    ex.printStackTrace();
                }
                MainUser.refreshTable();
            }
        });


        pack();
        setSize(400, 270);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

}
