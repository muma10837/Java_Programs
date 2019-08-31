
/**
 *
 * @author mahmed27
 */
import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        Scanner inFile = null;

        try {
            if (args.length > 0) {
                serverSocket = new ServerSocket(Integer.parseInt(args[0]));
            } else {
                serverSocket = new ServerSocket(10007);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10007.");
            System.exit(1);
        }

        try {
            if (args.length > 1) {
                inFile = new Scanner(new File(args[1]));
            } else {
                inFile = new Scanner(new File("DNS-table.txt"));
            }
        } catch (FileNotFoundException e) {
            System.out.println("File error");
        }

        StringTokenizer stok = null;
        ArrayList<String> domainNames = new ArrayList<>();
        ArrayList<String> ipAddress = new ArrayList<>();

        while (inFile.hasNext()) {
            stok = new StringTokenizer(inFile.nextLine(), ", ");
            domainNames.add(stok.nextToken());
            ipAddress.add(stok.nextToken());
        }

        inFile.close();

        Socket clientSocket = null;
        System.out.println("Waiting for connection ...");

        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        System.out.println("Connection successful");
        System.out.println("Waiting for input ...");

        //open IO streams
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        String action;

        //wait and read input from client. Exit when input from socket is Bye
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Server: " + inputLine);
            stok = new StringTokenizer(inputLine, " ");
            action = stok.nextToken();

            if (action.equals(".bye")) {
                out.println(".bye");
                break;
            } else if (action.equals("get-ip")) {
                action = stok.nextToken();
                if (domainNames.contains(action)) {
                    out.println(ipAddress.get(domainNames.indexOf(action)));
                } else if(action.equals("0")) {
                    out.println("connection closed");
                    break;
                }else {
                    out.println("error this ip is not in the registry");
                }
            } else if (action.equals("get-hostname")) {
                action = stok.nextToken();
                if (ipAddress.contains(action)) {
                    out.println(domainNames.get(ipAddress.indexOf(action)));
                } else {
                    out.println("error this hostname is not in the registry");
                }
            } else {
                out.println("Please use .bye, get-ip, or get-hostname");
            }
        }

        //close IO streams and at the end socket
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
