package JGT; /**
 * Created by Błażej on 26.04.2017.
 */
import java.io.*;
import java.net.*;

public class Client
{
    public static void main(String[] args) throws IOException
    {
        Socket socket = new Socket("localhost", 8189);
        JGT.GUI.Login login = new JGT.GUI.Login(socket);

        boolean quit = false;
        /*
        while(!quit)
        {
            String sentence;
            String modifiedSentence;
            BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            sentence = inFromUser.readLine();
            outToServer.writeBytes(sentence + '\n');
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
            if(sentence.equals("quit"))
                quit = true;
        }
        clientSocket.close();45
        */
    }
}
