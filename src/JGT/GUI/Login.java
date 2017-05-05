package JGT.GUI;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;


public class Login extends JFrame
{
    private Container contentPane;

    private JLabel labelLogin;
    private JLabel labelPassword;

    private JTextField textFieldLogin;
    private JPasswordField textFieldPassword;

    private JButton buttonLogin;
    private JButton buttonRegister;

    private Socket socket;
    private DataOutputStream outputToServer;
    private BufferedReader inputFromServer;


    public Login(Socket socket)
    {
        super("Login");
        this.socket = socket;

        contentPane = getContentPane();

        setBounds(0, 0, 340, 170);
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


        /*
		 * Button
		 */

        buttonLogin = new JButton("Login");
        buttonLogin.setBounds(50, 90, 100, 30);
        contentPane.add(buttonLogin);

        buttonLogin.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    login();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        buttonRegister = new JButton("Register");
        buttonRegister.setBounds(190, 90, 100, 30);
        contentPane.add(buttonRegister);

        buttonRegister.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    JGT.GUI.Registration registration = new JGT.GUI.Registration(socket);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });

        pack();
        setSize(340, 170);
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    private void login() throws IOException
    {
        String respond = "";
        outputToServer = new DataOutputStream(socket.getOutputStream());
        inputFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        outputToServer.writeBytes("<!LOGIN!>" + this.textFieldLogin.getText() + "," + new String(this.textFieldPassword.getPassword()) + '\n');

        respond = inputFromServer.readLine();

        if(respond.equals("DENIED"))
        {
            JOptionPane.showMessageDialog(null, "Wrong username or password");
        }
        else
        {
            if (respond.equals("ACCEPTED_USER"))
            {
                setVisible(false);
                dispose();
                JGT.GUI.User.MainUser mainUser = new JGT.GUI.User.MainUser(this.socket, this.textFieldLogin.getText());
            }
            if (respond.equals("ACCEPTED_HOTEL_ADMIN"))
            {
                setVisible(false);
                dispose();
                JGT.GUI.HotelAdministrator.MainAdminH mainAdminH = new JGT.GUI.HotelAdministrator.MainAdminH(this.socket, this.textFieldLogin.getText());
            }
        }
    }
}
