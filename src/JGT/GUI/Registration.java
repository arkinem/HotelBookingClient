package JGT.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Registration extends JFrame
{
    private Container contentPane;

    private JLabel labelLogin;
    private JLabel labelPassword;
    private JLabel labelName;
    private JLabel labelSurname;

    private JTextField textFieldLogin;
    private JPasswordField textFieldPassword;
    private JTextField textFieldName;
    private JTextField textFieldSurname;

    private JButton buttonConfirm;

    private Socket socket;
    private DataOutputStream outputToServer;
    private BufferedReader inputFromServer;

    public Registration(Socket socket)
    {
        super("Registration");
        this.socket = socket;

        contentPane = getContentPane();

        setBounds(0, 0, 340, 220);
        contentPane.setLayout(null);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

		/*
		 * Labels
		 */

        labelLogin = new JLabel("Username: ", SwingConstants.RIGHT);
        labelLogin.setBounds(10, 29, 100, 11);
        labelLogin.setForeground(Color.BLACK);
        contentPane.add(labelLogin);

        labelPassword = new JLabel("Password: ", SwingConstants.RIGHT);
        labelPassword.setBounds(10, 59, 100, 11);
        labelPassword.setForeground(Color.BLACK);
        contentPane.add(labelPassword);

        labelName = new JLabel("Name: ", SwingConstants.RIGHT);
        labelName.setBounds(10, 89, 100, 11);
        labelName.setForeground(Color.BLACK);
        contentPane.add(labelName);

        labelSurname = new JLabel("Surname: ", SwingConstants.RIGHT);
        labelSurname.setBounds(10, 119, 100, 11);
        labelSurname.setForeground(Color.BLACK);
        contentPane.add(labelSurname);

        /*
		 * Textfields
		 */

        textFieldLogin = new JTextField();
        textFieldLogin.setBounds(115, 25, 178, 20);
        textFieldLogin.setColumns(10);
        contentPane.add(textFieldLogin);

        textFieldPassword = new JPasswordField();
        textFieldPassword.setBounds(115, 55, 178, 20);
        textFieldPassword.setColumns(10);
        contentPane.add(textFieldPassword);

        textFieldName = new JTextField();
        textFieldName.setBounds(115, 85, 178, 20);
        textFieldName.setColumns(10);
        contentPane.add(textFieldName);

        textFieldSurname = new JTextField();
        textFieldSurname.setBounds(115, 115, 178, 20);
        textFieldSurname.setColumns(10);
        contentPane.add(textFieldSurname);


        /*
		 * Button
		 */

        buttonConfirm = new JButton("Confirm");
        buttonConfirm.setBounds(120, 145, 100, 30);
        contentPane.add(buttonConfirm);

        buttonConfirm.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    if(!textFieldLogin.getText().equals("") && !(new String(textFieldPassword.getPassword())).equals("") && !textFieldName.equals("") && !textFieldSurname.equals(""))
                    {
                        String respond = "";
                        outputToServer = new DataOutputStream(socket.getOutputStream());
                        inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                        outputToServer.writeBytes("<!REGISTER!>[" + textFieldLogin.getText() + "," + new String(textFieldPassword.getPassword())+ "," +textFieldName.getText() + "," + textFieldSurname.getText() + ']' + '\n');

                        respond = inputFromServer.readLine();
                        JOptionPane.showMessageDialog(null, "Register successful");
                        dispose();
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Please, fill all blank spaces in the form");
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });


        pack();
        setSize(340, 220);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


    }
}
