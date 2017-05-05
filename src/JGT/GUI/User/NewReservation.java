package JGT.GUI.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.jdesktop.swingx.JXDatePicker;
import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.text.*;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;


public class NewReservation extends JFrame
{
    private Container contentPane;

    private JLabel labelCountry;
    private JLabel labelCity;
    private JLabel labelPriceFrom;
    private JLabel labelPriceTo;
    private JLabel labelDateFrom;
    private JLabel labelDateTo;
    private JLabel labelMinimumRate;
    private JLabel labelDoubleSingle;

    private JTextField textFieldPriceFrom;
    private JTextField textFieldPriceTo;

    private JComboBox comboBoxCountry;
    private JComboBox comboBoxCity;
    private JComboBox comboBoxMinimumRate;
    private JComboBox comboBoxDoubleSingle;

    private JXDatePicker pickerFrom;
    private JXDatePicker pickerTo;

    private JButton buttonSearch;

    private JTable tableResults;
    private DefaultTableModel modelResults;

    private ArrayList<String> countryList;
    private ArrayList<String> citiesList;

    private Socket socket;
    private DataOutputStream outputToServer;
    private BufferedReader inputFromServer;

    private String username;
    private String startDate;
    private String endDate;


    public NewReservation(Socket socket, String username)
    {
        super("New Reservation");
        this.socket = socket;
        this.username = username;

        contentPane = getContentPane();
        setBounds(0, 0, 800, 500);
        contentPane.setLayout(null);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);

        /*
		 * Labels
		 */

        labelCountry = new JLabel("Country: ", SwingConstants.RIGHT);
        labelCountry.setBounds(10, 27, 100, 15);
        labelCountry.setForeground(Color.BLACK);
        contentPane.add(labelCountry);

        labelCity = new JLabel("City: ", SwingConstants.RIGHT);
        labelCity.setBounds(235, 27, 100, 15);
        labelCity.setForeground(Color.BLACK);
        contentPane.add(labelCity);

        labelPriceFrom = new JLabel("Price from: ", SwingConstants.RIGHT);
        labelPriceFrom.setBounds(10, 54, 100, 11);
        labelPriceFrom.setForeground(Color.BLACK);
        contentPane.add(labelPriceFrom);

        labelPriceTo = new JLabel("to: ", SwingConstants.RIGHT);
        labelPriceTo.setBounds(170, 54, 25, 11);
        labelPriceTo.setForeground(Color.BLACK);
        contentPane.add(labelPriceTo);

        labelDateFrom = new JLabel("Date from: ", SwingConstants.RIGHT);
        labelDateFrom.setBounds(10, 79, 100, 11);
        labelDateFrom.setForeground(Color.BLACK);
        contentPane.add(labelDateFrom);

        labelDateTo = new JLabel("to: ", SwingConstants.RIGHT);
        labelDateTo.setBounds(210, 79, 30, 11);
        labelDateTo.setForeground(Color.BLACK);
        contentPane.add(labelDateTo);

        labelMinimumRate = new JLabel("Minimum rate: ", SwingConstants.RIGHT);
        labelMinimumRate.setBounds(10, 104, 100, 11);
        labelMinimumRate.setForeground(Color.BLACK);
        contentPane.add(labelMinimumRate);

        labelDoubleSingle = new JLabel("Room type: ", SwingConstants.RIGHT);
        labelDoubleSingle.setBounds(150, 102, 100, 15);
        labelDoubleSingle.setForeground(Color.BLACK);
        contentPane.add(labelDoubleSingle);


        /*
		 * Textfields
		 */

        textFieldPriceFrom = new JTextField();
        textFieldPriceFrom.setBounds(115, 50, 50, 20);
        textFieldPriceFrom.setColumns(10);
        contentPane.add(textFieldPriceFrom);

        textFieldPriceTo = new JTextField();
        textFieldPriceTo.setBounds(200, 50, 50, 20);
        textFieldPriceTo.setColumns(10);
        contentPane.add(textFieldPriceTo);

        /*
		 * Comboboxes
		 */
        getCountryList();
        countryList.add(0,"Any");
        comboBoxCountry = new JComboBox();
        comboBoxCountry.setBounds(115, 25, 178, 20);
        comboBoxCountry.setModel(new DefaultComboBoxModel(countryList.toArray()));
        contentPane.add(comboBoxCountry);

        comboBoxCountry.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                comboBoxCity.setEnabled(true);
                getCitiesList(String.valueOf(comboBoxCountry.getSelectedItem()));
                citiesList.add(0, "Any");
                comboBoxCity.setModel(new DefaultComboBoxModel(citiesList.toArray()));
            }
        });

        comboBoxCity = new JComboBox();
        comboBoxCity.setBounds(340, 25, 178, 20);
        comboBoxCity.setEnabled(false);
        contentPane.add(comboBoxCity);

        String[] rateTypes = { "1", "2", "3", "4", "5"};
        comboBoxMinimumRate = new JComboBox(rateTypes);
        comboBoxMinimumRate.setBounds(115, 100, 60, 20);
        contentPane.add(comboBoxMinimumRate);

        String[] roomTypes = { "Single", "Double" };
        comboBoxDoubleSingle = new JComboBox(roomTypes);
        comboBoxDoubleSingle.setBounds(255, 100, 100, 20);
        contentPane.add(comboBoxDoubleSingle);

        /*
         * DataPicker
         */

        pickerFrom = new JXDatePicker();
        pickerFrom.setDate(Calendar.getInstance().getTime());
        pickerFrom.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
        pickerFrom.setBounds(115,75,100,20);
        contentPane.add(pickerFrom);

        pickerTo = new JXDatePicker();
        pickerTo.setDate(Calendar.getInstance().getTime());
        pickerTo.setFormats(new SimpleDateFormat("dd.MM.yyyy"));
        pickerTo.setBounds(245,75,100,20);
        contentPane.add(pickerTo);

        /*
		 * Button
		 */

        buttonSearch = new JButton("Search");
        buttonSearch.setBounds(350, 435, 100, 30);
        contentPane.add(buttonSearch);

        buttonSearch.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String country = "", city = "";
                String priceMin = "", priceMax = "";
                String dateStart = "", dateEnd = "";
                String minimumRate = "", roomType = "";

                dateStart = (new SimpleDateFormat("dd/MM/yyyy").format(pickerFrom.getDate())).toString();
                dateEnd = (new SimpleDateFormat("dd/MM/yyyy").format(pickerTo.getDate())).toString();

                if(dateStart.equals(dateEnd))
                {
                    JOptionPane.showMessageDialog(null, "Dates should be different");
                }
                else
                {
                    if((Integer.parseInt(dateStart.split("/")[2]) > Integer.parseInt(dateEnd.split("/")[2])) ||
                            (Integer.parseInt(dateStart.split("/")[2]) == Integer.parseInt(dateEnd.split("/")[2]) && Integer.parseInt(dateStart.split("/")[1]) > Integer.parseInt(dateEnd.split("/")[1]))  ||
                            (Integer.parseInt(dateStart.split("/")[2]) == Integer.parseInt(dateEnd.split("/")[2]) && Integer.parseInt(dateStart.split("/")[1]) == Integer.parseInt(dateEnd.split("/")[1]) && Integer.parseInt(dateStart.split("/")[0]) > Integer.parseInt(dateEnd.split("/")[0])))
                    {
                        JOptionPane.showMessageDialog(null, "End date must be after start date");
                    }
                    else
                    {
                        if(!String.valueOf(comboBoxCountry.getSelectedItem()).equals("Any"))
                        {
                            country = String.valueOf(comboBoxCountry.getSelectedItem());

                            if(!String.valueOf(comboBoxCity.getSelectedItem()).equals("Any"))
                                city = String.valueOf(comboBoxCity.getSelectedItem());
                        }
                        if(!textFieldPriceFrom.getText().equals(""))
                        {
                            priceMin = textFieldPriceFrom.getText();
                        }
                        if(!textFieldPriceTo.getText().equals(""))
                        {
                            priceMax = textFieldPriceTo.getText();
                        }
                        minimumRate = String.valueOf(comboBoxMinimumRate.getSelectedItem());
                        roomType = String.valueOf(comboBoxDoubleSingle.getSelectedItem());


                        String requestStatement = "SELECT  hotels.hotelID, hotels.Name,hotels.City,hotels.Country,hotels.SingleRoomPrice,hotels.DoubleRoomPrice, hotels.SingleRoomCount, hotels.DoubleRoomCount, hotels.Rate  FROM hotels WHERE ";
                        if(!country.equals(""))
                            requestStatement += "hotels.Country = \"" + country + "\" AND ";
                            if(!city.equals(""))
                                requestStatement += "hotels.City = \"" + city + "\" AND ";
                            if(roomType.equals("Single"))
                            {
                                requestStatement += "hotels.SingleRoomCount > 0 AND ";
                                if (!priceMin.equals(""))
                                    requestStatement += "hotels.SingleRoomPrice >= " + priceMin + " AND ";
                                if (!priceMax.equals(""))
                                    requestStatement += "hotels.SingleRoomPrice <= " + priceMax + " AND ";
                            }
                            else
                            {
                                requestStatement += "hotels.DoubleRoomCount > 0 AND ";
                                if (!priceMin.equals(""))
                                    requestStatement += "hotels.DoubleRoomPrice >= " + priceMin + " AND ";
                                if (!priceMax.equals(""))
                                    requestStatement += "hotels.DoubleRoomPrice <= " + priceMax + " AND ";
                            }
                            if(!minimumRate.equals("1"))
                                requestStatement += "hotels.Rate >= " + minimumRate + " AND ";

                            if(requestStatement.substring(requestStatement.length()-4,requestStatement.length()).equals("AND "))
                                requestStatement = requestStatement.substring(0,requestStatement.length()-5) + ";";

                        try {
                            String respond = "";
                            outputToServer = new DataOutputStream(socket.getOutputStream());
                            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                            outputToServer.writeBytes("<!CHECK_AVAILABILITY_BY_QUERY!>[" + requestStatement +"]{" + dateStart +","+ dateEnd + '}' + '\n');

                            respond = inputFromServer.readLine();

                            modelResults.setRowCount(0);
                            for (String record : respond.split(";"))
                            {

                                Object[] o = new Object[6];
                                int index = 0;
                                for (String data : record.split(","))
                                {
                                    o[index] = data;
                                    index++;
                                }
                                index = 0;
                                modelResults.addRow(o);


                            }
                            tableResults.setModel(modelResults);

                            startDate = dateStart;
                            endDate = dateEnd;
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        }

                    }
                }


        });

        /*
         * Table
         */

        //headers for the table
        String[] columns = new String[] {
                "Hotel name", "City", "Country", "Room Type", "Rate","Price"
        };

        //create table with data
        modelResults = new DefaultTableModel();
        modelResults.setColumnIdentifiers(columns);

        tableResults = new JTable();
        tableResults.setModel(modelResults);
        tableResults.setRowSelectionAllowed(true);
        tableResults.setDefaultEditor(Object.class, null);
        tableResults.setBounds(10,130,770,300);
        JScrollPane scrollPane=new JScrollPane(tableResults);
        scrollPane.setBounds(10,130,770,300);
        //add the table to the frame
        add(scrollPane);

        tableResults.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    Point pnt = evt.getPoint();
                    int row = tableResults.rowAtPoint(pnt);


                    String[] result = getRowAt(row);

                    makeReservation(result[0],result[2],result[1],result[3],result[5],username,startDate,endDate);
                }
            }
        });



        pack();
        setSize(800, 500);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void getCountryList()
    {
        try {
            countryList = new ArrayList<String>();
            String respond = "";
            outputToServer = new DataOutputStream(socket.getOutputStream());
            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            outputToServer.writeBytes("<!GET_COUNTRY_LIST!>" + '\n');

            respond = inputFromServer.readLine();

            for (String country : respond.split(",")) {
                if (!countryList.contains(country)) {
                    countryList.add(country);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private void getCitiesList(String country)
    {
        try {
            citiesList = new ArrayList<String>();
            String respond = "";
            outputToServer = new DataOutputStream(socket.getOutputStream());
            inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            outputToServer.writeBytes("<!GET_CITIES_LIST!>[" + country + ']' + '\n');

            respond = inputFromServer.readLine();

            for (String city : respond.split(",")) {
                if (!citiesList.contains(city)) {
                    citiesList.add(city);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private String[] getRowAt(int row) {
        String[] result = new String[6];

        for (int i = 0; i < 6; i++) {
            result[i] = tableResults.getModel().getValueAt(row, i).toString();
        }

        return result;
    }

    private void makeReservation(String hotelName, String country, String city, String roomType, String price, String username, String startDate, String endDate)
    {
        long duration = daysBetween(startDate,endDate);
        String message = "Do you confirm your reservation?\nHotel name: " + hotelName + "\nCity: " + city + "\nRoom type: " + roomType + "\nTotal price: " + toIntExact(duration)*Integer.parseInt(price) + "\nFrom: " + startDate + "\nTo: " + endDate;

        int reply = JOptionPane.showConfirmDialog(null, message, "Confirm Reservation", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            try
            {
                String respond = "";
                outputToServer = new DataOutputStream(socket.getOutputStream());
                inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                outputToServer.writeBytes("<!MAKE_RESERVATION!>["+username+","+hotelName+","+city+","+country+","+roomType+","+startDate+","+endDate+']'+ '\n');

                respond = inputFromServer.readLine();

            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Thank you for booking.");
            MainUser.refreshTable();
            modelResults.setRowCount(0);
            tableResults.setModel(modelResults);


        }
    }

    private static long daysBetween(String one, String two)
    {
        try {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            long difference = (df.parse(one).getTime() - df.parse(two).getTime()) / 86400000;
            return Math.abs(difference);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }
}
