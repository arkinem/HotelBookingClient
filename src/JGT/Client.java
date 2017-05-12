package JGT;
import java.io.IOException;
import java.net.Socket;

public class Client
{
    private static JGT.GUI.Login login;

    public void showClientLoginWindow(boolean b){

        login.setVisible(b);

    }
    public static void main(String[] args) throws IOException
    {
        Socket socket = new Socket("localhost", 8189);
//        Socket socket = new Socket("10.100.21.38", 8189);
        login = new JGT.GUI.Login(socket);


        boolean quit = false;

    }
}

