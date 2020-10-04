import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.util.ArrayList;

public class PortSniffer {

    String error = "-";

    public PortSniffer() {
    }

    public String searchForServer(String username, String password, int port, boolean test) {
        error = "-";
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            String[] host = networkInterface.getInterfaceAddresses().get(0).getAddress().toString().split("\\.");
            String mask = host[0].replace("/", "") + "." + host[1] + "." + host[2];

            Log.logLine("Searching for server with mask: " + mask);
            Log.logLine("Password: " + password);
            Log.logLine("Username: " + username);

            if(!test) {
                Log.logLine("checking for server on default");
                if (poke("192.168.1.245", username, password, 3306)) return "192.168.1.245";
                Log.logLine("Failed to connect to default");
            }else {
                // Build list of IP's
                ArrayList<String> IPs = new ArrayList<>(255);
                ArrayList<String> goodIPs = new ArrayList<>(0);
                for (int i = 0;i < 255;i++) {
                    IPs.add(mask + "." + i);
                }

                // Removes unavailable IP's
                IPs.parallelStream().forEach(ip -> {
                    if (quickCheck(ip, port)) {
                        Log.logLine("IP " + ip);
                        goodIPs.add(ip);
                    }
                });

                String[] ip = new String[0];
                ip = goodIPs.toArray(ip);

                for (String s : ip) {
                    Log.logLine("Checking " + s);
                    if (poke(s, username, password, port)) {
                        return s;
                    }
                }
            }
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
            error += e.getMessage();
        }

        return "UNAVAILABLE" + error;
    }

    private boolean quickCheck(String address, int port) {
        try {

            try (Socket crunchifySocket = new Socket()) {
                // Connects this socket to the server with a specified timeout value.
                crunchifySocket.connect(new InetSocketAddress(address, port), 10);
            }
            // Return true if connection successful
            return true;
        } catch (IOException exception) {
            // Return false if connection fails
            Log.logLine(address + " : FAIL");
            return false;
        }
    }

    private boolean poke(String ip, String username, String password, int port) {// Returns true/false
        // depending on if
        // server is available
        try {
            // Attempts a basic connection using an account with minimum privileges
            Log.logLine("Checking server response");
            Connection dbConnection =
                    DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port, username, password);
            Log.logLine("Achieved server response");
            Statement statement = dbConnection.createStatement();
            ResultSet result = statement.executeQuery("SELECT 1 AS A");
            result.next();
            if (result.getInt("A") != 1) throw new SQLException();
            Log.logLine("IP check on " + ip + " success");
            return true;
        } catch (SQLException e) {
            error += "\n" + e.getMessage() + "\n";
            Log.logLine(e.getMessage());
            Log.logLine("IP check on " + ip + " fail");
            return false;
        }
    }

}