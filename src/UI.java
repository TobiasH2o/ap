import components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Stream;

import static javax.swing.BoxLayout.Y_AXIS;

public class UI extends JPanel implements ActionListener, WindowListener {

    private static final int port = 3306;

    // List of available tables in the database to be stored locally
    private final static String[] tables =
            new String[]{"contract", "contractHeading", "engineer", "headingLine", "product", "qProduct"};
    private final Container southBox = new Container();
    private final HintTextField usernameBox = new HintTextField("Username", HintTextField.CENTER_HIDDEN);
    private final HintTextField passwordBox = new HintTextField("Password", HintTextField.CENTER_HIDDEN);
    private final JButton submit = new JButton("Load local data");
    private final JButton makeContract = new JButton("Contract Interface");
    private final JButton searchContract = new JButton("Search Orders");
    // sp allows user to search any loaded contracts
    private final SearchPanel sp = new SearchPanel();
    // Used to load and save files
    private final FileManager fm;
    // Used to talk to the SQL sever
    private final SQLInterface sql = new SQLInterface();
    // Stores company logo
    private final BufferedImage logo;
    // used to find the SQL server
    private final PortSniffer ps = new PortSniffer();
    // Tables to store data from server
    // Stores printable version of logo
    private final String filePath;
    // Builds and views contracts, Must be handed Contract by parent
    private final ContractInterface ci;
    private final String version;
    private final boolean test;
    private final boolean offlineMode;
    private FullContract fullContract = new FullContract();
    private String ip = "";
    private Contract[] contracts;
    private ContractHeading[] contractHeadings;
    private HeadingLine[] headingLines;
    private Product[] products;
    private Qproduct[] qProducts;
    private boolean serverFound = false;
    private double loadingBarMaxValue = 1;
    private double loadingBarCurValue = 1;
    private String loadingBarTxtValue = "";
    private String username = "";
    private String password = "";

    public UI(JFrame frame) {

        String myDocuments;

        //here, we assign the name of the OS, according to Java, to a variable...
        String OS = (System.getProperty("os.name")).toUpperCase();
        //to determine what the workingDirectory is.
        //if it is some version of Windows
        if (OS.contains("WIN")) {
            //it is simply the location of the "AppData" folder
            myDocuments = System.getenv("AppData");
        }
        //Otherwise, we assume Linux or Mac
        else {
            //in either case, we would start in the user's home directory
            myDocuments = System.getProperty("user.home");
            //if we are on a Mac, we are not done, we look for "Application Support"
            myDocuments += "/Library/Application Support";
        }
        //we are now free to set the workingDirectory to the subdirectory that is our
        //folder.

        System.out.println(myDocuments);

        filePath = myDocuments + "\\apData";

        fm = new FileManager(filePath);

        logo = fm.loadImage("/Images/Logo.png");

        fm.configSet("logOnCrash", "0");

        if (!new File(filePath + "\\config.txt").exists()) {
            try {
                createConfigFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!getCurrentVersion().equalsIgnoreCase(fm.configString("version"))) try {
            new File(filePath + "\\config.txt").delete();
            createConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        test = fm.configBoolean("testing");
        version = fm.configString("version");
        offlineMode = fm.configBoolean("offlineMode");
        ci = new ContractInterface(offlineMode);

        sp.frame.addWindowListener(this);
        ContractInterface.frame.addWindowListener(this);

        submit.addActionListener(this);
        submit.setActionCommand("initialsync");
        submit.setFocusPainted(false);

        searchContract.addActionListener(this);
        searchContract.setActionCommand("sc");
        searchContract.setFocusPainted(false);

        makeContract.addActionListener(this);
        makeContract.setActionCommand("makeContract");
        makeContract.setFocusPainted(false);

        JButton syncWithServer = new JButton("Sync");
        syncWithServer.addActionListener(this);
        //noinspection SpellCheckingInspection
        syncWithServer.setActionCommand("serversync");

        JButton uploadDataButton = new JButton("Upload File");
        uploadDataButton.addActionListener(this);
        uploadDataButton.setActionCommand("uploadFile");

        // Constructs northern menu
        Container northBox = new Container();
        northBox.setLayout(new GridLayout(2, 3));
        northBox.add(submit);
        northBox.add(usernameBox);
        usernameBox.setText("APENG");
        northBox.add(passwordBox);
        passwordBox.setText("APENG");
        northBox.add(searchContract);
        northBox.add(makeContract);

        // Disables program functionality until it has loaded local data sources
        usernameBox.setEnabled(false);
        passwordBox.setEnabled(false);
        searchContract.setEnabled(false);
        makeContract.setEnabled(false);

        southBox.setLayout(new GridLayout(1, 1));
        southBox.add(syncWithServer);

        this.setLayout(new BorderLayout());
        this.add(northBox, BorderLayout.NORTH);
        Container centerBox = new Container();
        this.add(centerBox, BorderLayout.CENTER);

        this.setFocusable(true);
        this.setBackground(new Color(31, 24, 94));

        JPanel buPanel = new JPanel();
        JFrame bulkUploadFrame = new JFrame("Upload Access dumps");
        bulkUploadFrame.add(buPanel);
        bulkUploadFrame.setLocationRelativeTo(getParent());
        bulkUploadFrame.setSize(200, 100);

        buPanel.setLayout(new GridLayout(2, 1));
        JTextField queryBox = new JTextField("Target Table");
        buPanel.add(queryBox);
        buPanel.add(uploadDataButton);

        frame.setTitle("Version: " + fm.configString("version"));

        repaint();

        checkDates();

        checkLogging();

        submit.doClick();

    }

    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g2);

        // LOADING BAR
        if (loadingBarCurValue > 0) {
            g2.setColor(new Color(16, 110, 18));
            g2.fillRect(5, this.getHeight() - southBox.getHeight() - 20,
                    (int) ((this.getWidth() - 10) * loadingBarCurValue / loadingBarMaxValue), 15);
        }
        g2.setColor(Color.WHITE);
        g2.drawString(loadingBarTxtValue, 10, this.getHeight() - southBox.getHeight() - 7);
        g2.setColor(Color.BLACK);
        g2.drawRect(5, this.getHeight() - southBox.getHeight() - 20, this.getWidth() - 10, 15);

        // Logo
        g2.drawImage(logo, 0, this.getHeight() - southBox.getHeight() - 25 - logo.getHeight(), this);

    }

    private void checkLogging() {
        if (fm.configBoolean("log")) Log.setOutput(filePath + "\\errorLogs\\" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy" + "-MM" + "-dd-hh-mm")) + ".txt");
        Log.logLine("====================================Logging Started====================================");

    }

    private void checkDates() {
        String oDate;
        if (fm.readFile("Data/SyncDate.txt").length > 0) {
            oDate = fm.readFile("Data/SyncDate.txt")[0].replaceAll("~~", "");
            Log.logLine(oDate);
            if (getDayDiff(oDate) > 7) {
                JOptionPane.showMessageDialog(this,
                        "ALERT: It has been " + getDayDiff(oDate) + " days since you were last " +
                        "online. Please sync with the database.");
                fm.deleteDir(filePath + "\\Data");
                fm.deleteDir(filePath + "\\Tables");
                fm.buildDirectory(filePath);
            }
        } else {
            JOptionPane
                    .showMessageDialog(this, "ALERT: Your database is empty. Please sync it with the central database");
            fm.saveFile("Data/SyncDate.txt", new String[]{"0001/01/01"}, false);
        }
    }

    private String getCurrentVersion() {
        InputStream in = this.getClass().getResourceAsStream("config.txt");
        InputStreamReader sr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(sr);
        try {
            return br.readLine().split("=")[1];
        } catch (Exception e) {
            Log.logLine(e);
            return "";
        }
    }

    private void createConfigFile() throws IOException {
        new File(filePath + "\\config.txt").createNewFile();
        InputStream in = this.getClass().getResourceAsStream("config.txt");
        InputStreamReader sr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(sr);
        ArrayList<String> returnArray = new ArrayList<>(0);
        br.lines().forEach(returnArray::add);
        br.close();
        Log.logLine(returnArray.toArray(new String[]{}));
        fm.saveFile("\\config.txt", returnArray.toArray(new String[]{}), true);
        this.getClass().getResourceAsStream("config.txt").readAllBytes();
    }

    private void uploadData() {
        ArrayList<String[]> entries = new ArrayList<>(0);
        URI f;
        try {

            f = new File(filePath + "\\Data\\uploadBuffer.txt").toURI();
            FileManager.checkFile(f.getPath());
            Object[] lines;
            lines = Files.lines(Paths.get(f)).toArray();
            resetLoadBar("Uploading engineers", lines.length);
            for (Object line : lines) {// Loads all data into an array
                String ln = "" + line;
                Log.logLine(ln);
                entries.add(ln.split(":"));
            }
            for (String[] entry : entries) { // Checks each value
                sql.addEngineer(entry[1]);
                updateLoadBar();
            }

            fm.saveFile("\\Data\\uploadBuffer.txt", new String[]{}, false);

            if (!offlineMode) {
                File[] contracts = new File(filePath + "\\Data\\Contracts\\").listFiles();
                if (contracts != null && contracts.length > 0) {
                    SelectionList selecList = new SelectionList();
                    for (File contract : contracts)
                        selecList.addItem(contract.getName());
                    selecList.addOption("Upload");
                    selecList.addOption("Delete");
                    selecList.addOption("Ignore");
                    JPanel selectionPanel = new JPanel();
                    selectionPanel.setLayout(new BoxLayout(selectionPanel, Y_AXIS));
                    selectionPanel.add(new JLabel("Select contracts to save to the server."));
                    selectionPanel.add(selecList);

                    JOptionPane.showMessageDialog(this, selecList);
                    selecList.redraw();


                    StringBuilder results = new StringBuilder("Result:");

                    for (int i = 0;i < contracts.length;i++) {
                        if (selecList.checkItemOption(0, i)) results.append(uploadContract(contracts[i]));
                        if (selecList.checkItemOption(1, i)) fm.deleteDir(contracts[i].getAbsolutePath());
                    }
                    if (results.length() != 7) JOptionPane
                            .showMessageDialog(this, results, "Server Sync Report", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    private String uploadContract(File contract) {
        try {
            FullContract fc;
            updateLoadBar("Loading Contract " + contract.getName(), 1);
            fc = loadContract(contract);
            if (sql.contractExists(fc.details.contractID)) {
                if (JOptionPane.showOptionDialog(this, "Contract " + fc.fullContractID() +
                                                       " could not be uploaded as a duplicate ID is on the database.\n You can instead amend the contract. This will overwrite the current contract on the server and can not be undone.",
                        "Amend Contract?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        new String[]{"Amend", "Cancel"}, "") == 0) {
                    updateLoadBar("Amending Contract " + contract.getName(), 1);
                    sql.amendContract(fc);
                    updateLoadBar("Removing Contract " + contract.getName(), 1);
                    fm.deleteDir(contract.getAbsolutePath());
                    return "\n[PASSED]-" + contract.getName();
                }
            } else {
                updateLoadBar("Uploading Contract " + contract.getName(), 1);
                sql.pushContract(fc);
                updateLoadBar("Removing Contract " + contract.getName(), 1);
                fm.deleteDir(contract.getAbsolutePath());
                return "\n[PASSED]-" + contract.getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "\n[FAILED]-" + contract.getName();
        }
        return "";
    }

    private FullContract loadContract(File contract) {
        FullContract fullContract = new FullContract();
        String[] data = fm.readFile(contract);
        String[] cDetails = data[0].split("~~");

        Contract c = new Contract();
        c.contractID = cDetails[0];
        c.contractDate = Convert.getIfDate(cDetails[1]);
        c.companyName = cDetails[2];
        c.address1 = cDetails[3];
        c.address2 = cDetails[4];
        c.address3 = cDetails[5];
        c.postcode = cDetails[6];
        c.deliveryMethod = cDetails[7];
        c.deliveryDate = Convert.getIfDate(cDetails[8]);
        c.quote = Convert.getBoolean(cDetails[9]);
        c.issued = Convert.getBoolean(cDetails[10]);
        c.engineer = cDetails[11];
        c.contractor = cDetails[12];
        fullContract.setDetails(c);

        String[] heading = data[1].split("~~");
        String[] headingLine = data[2].split("~~");
        String[] qProducts = data[3].split("~~");
        ArrayList<ContractHeading> contractHeading = new ArrayList<>(0);
        ArrayList<HeadingLine> contractHeadingLine = new ArrayList<>(0);
        ArrayList<Product> products = new ArrayList<>(0);
        ArrayList<Qproduct> qproducts = new ArrayList<>(0);

        for (String s : heading) {
            contractHeading.add(new ContractHeading((int) Convert.getIfNumeric(s.split("%50")[0]), s.split("%50")[1],
                    s.split("%50")[2]));
        }

        for (String s : headingLine) {
            Log.logLine(s.split("%50"));
            contractHeadingLine.add(new HeadingLine((int) Convert.getIfNumeric(s.split("%50")[0]),
                    (int) Convert.getIfNumeric(s.split("%50")[1]), s.split("%50")[2], s.split("%50")[3],
                    (int) Convert.getIfNumeric(s.split("%50")[4])));
        }


        for (HeadingLine head : contractHeadingLine) {
            for (Product prod : this.products) {
                if (head.productID.equalsIgnoreCase(prod.getProductID())) {
                    products.add(prod);
                }
            }
        }

        if (qProducts.length > 0) if (qProducts[0].length() > 0) for (String s : qProducts) {
            qproducts.add(new Qproduct((int) Convert.getIfNumeric(s.split("%50")[0]),
                    (int) Convert.getIfNumeric(s.split("%50")[1]), Convert.getIfNumeric(s.split("%50")[2]),
                    s.split("%50")[3]));
        }


        fullContract.contractHeadings = contractHeading;
        fullContract.contractHeadingLine = contractHeadingLine;
        fullContract.products = products;
        fullContract.qProducts = qproducts;

        return fullContract;
    }

    private boolean syncWithServer() {

        // Loading bar reset
        uploadData();
        resetLoadBar("Connecting to server", tables.length);
        boolean error = false;
        SQLInterface.setDetails(ip, username, password);
        for (String table : tables) {
            updateLoadBar("Loading " + table);
            // Uses file manager to save results provided by static SQL interface
            String[][] data = sql.fetchEntireTable(table);
            if (data != null) {
                fm.saveFile("\\Tables\\" + table, data);
            } else {
                loadingBarTxtValue = "INVALID CREDENTIALS";
                this.paintImmediately(5, this.getHeight() - southBox.getHeight() - 20, this.getWidth() - 10, 15);
                error = true;
                break;
            }
            updateLoadBar();
        }
        if (!error) {
            // Loads data from local txt files to internal classes
            loadData();
            // Sends loaded contracts to be stored in the search panel
            sp.setData(contracts);
            updateLoadBar("Connected to server");


            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            LocalDateTime now = LocalDateTime.now();
            String date = dtf.format(now);
            fm.saveFile("\\Data\\SyncDate", new String[][]{{date}});
        }
        checkVersion();
        return !error;
    }

    public void checkVersion() {
        if (!test) {
            String sVersion = sql.getVersion();
            if (sVersion.equals(version)) return;
            String[] segments = sVersion.split("\\.");
            String[] segments2 = version.split("\\.");
            for (int i = 0;i < segments.length;i++) {
                if (Convert.getIfNumeric(segments2[i]) > Convert.getIfNumeric(segments[i])) {
                    sql.updateVersion(version);
                    break;
                } else if (Convert.getIfNumeric(segments2[i]) < Convert.getIfNumeric(segments[i])) {
                    JOptionPane.showMessageDialog(this,
                            "A new version of the software is available.[" + sVersion + "]\nIt is recommended " +
                            "that you update to maintain stability.\nClick okay to open a webpage. From there you can" +
                            " download the latest version");
                    try {
                        Desktop.getDesktop().browse(new URI(
                                "https://drive.google.com/drive/folders/12vxGlNtRQ_qgaXYxjEOoabeG4dN7rwpY?usp=sharing"));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public int getDayDiff(String oldDate) {

        DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();

        int days = 0;
        days -= Convert.getIfNumeric(oldDate.split("/")[2]);
        days -= Convert.getIfNumeric(oldDate.split("/")[0]) * 365;
        for (int i = 0;i < Convert.getIfNumeric(oldDate.split("/")[1]);i++) {
            if (i % 2 == 0) {
                days -= 31;
            } else if (i == 1) {
                days -= 28;
            } else {
                days -= 30;
            }
        }

        days += now.getDayOfMonth();
        days += now.getYear() * 365;
        for (int i = 0;i < now.getMonthValue();i++) {
            if (i % 2 == 0) {
                days += 31;
            } else if (i == 1) {
                days += 28;
            } else {
                days += 30;
            }
        }

        return days;
    }

    public void getContract(String contractID) {
        fullContract.purge();
        for (Contract contract : contracts)
            if (contract.contractID.equals(contractID)) {
                fullContract = SudoSQL.getContract(contract, contractHeadings, headingLines, products, qProducts);
            }
        ci.setContractDetails(fullContract);
        ci.showMenu();
    }

    public void actionPerformed(ActionEvent e) {
        String event = e.getActionCommand();

        switch (event.split(",")[0]) {

            case "submit":
                username = usernameBox.getText();
                ci.setUsername(username);
                password = passwordBox.getText();
                SQLInterface.setDetails(ip, username, password);
                //offline mode is the default
                // It should allow the user to make search and delete orders on a local system
                if (serverFound) {
                    // Sets the button to assume it failed to connect

                    submit.setText("Go Online");
                    this.remove(southBox);
                    if (syncWithServer()) {
                        // Only runs if it succeeded to sync with the server data
                        this.add(southBox, BorderLayout.SOUTH);
                        submit.setText("Update Credentials");
                    }
                } else {
                    String serverIP;
                    // Uses basic account with minimal privileges to search for a server
                    serverIP = ps.searchForServer(username, password, port, test);
                    if (serverIP.split("-")[0].equals("UNAVAILABLE")) {
                        // Alerts the user that it could not find the server
                        // Sets the program into offline mode
                        serverFound = false;
                        resetLoadBar("CRITICAL ERROR", 1);
                        updateLoadBar();
                        JOptionPane.showMessageDialog(this, serverIP, "CRITICAL FAILURE", JOptionPane.ERROR_MESSAGE);
                        ci.setOffline(true);
                    } else {
                        // Sets the program into online mode and triggers the button to sync with server
                        // Sets the IP to be the valid IP returned by the server
                        //noinspection SpellCheckingInspection
                        ip = "jdbc:mysql://" + serverIP + ":" + port + "/apdb";
                        SQLInterface.IP = ip;
                        SQLInterface.PASSWORD = password;
                        SQLInterface.USERNAME = username;
                        Log.logLine("Logged IP " + ip);
                        serverFound = true;
                        submit.setText("Go Online");
                        submit.doClick();
                        ci.setOffline(false);
                    }
                }
                break;

            case "sc":
                // Search panel shown with conditions set to contracts
                sp.setData(contracts);
                sp.setVisible(true);
                break;

            case "makeContract":
                // Displays Contract building interface to the user
                ci.setContractDetails(fullContract);
                ci.showMenu();
                break;

            //noinspection SpellCheckingInspection
            case "serversync":
                syncWithServer();
                break;

            //noinspection SpellCheckingInspection
            case "initialsync":
                // Enables some of the programs functionality
                usernameBox.setEnabled(true);
                passwordBox.setEnabled(true);
                searchContract.setEnabled(true);
                makeContract.setEnabled(true);

                // Loads data stored locally
                loadData();

                if (new File(filePath + "\\Data\\Backup\\backupContract.cot").exists()) {
                    ci.loadContract(new File(filePath + "\\Data\\Backup\\backupContract.cot"));
                    fullContract = ci.getContract();
                }

                submit.setActionCommand("submit");
                submit.setText("Connect Server");
                // Sets synced to the true
                break;

            default:
                Log.logLine("Unrecognised Command '" + event + "'");

        }

    }

    // ############################################################################### USED TO CONTROL LOADING/PROGRESS BAR

    private void updateLoadBar(String s, int i) {
        loadingBarCurValue += i;
        loadingBarTxtValue = s;
        this.paintImmediately(5, this.getHeight() - southBox.getHeight() - 20, this.getWidth() - 10, 15);
    }

    private void updateLoadBar(String s) {
        updateLoadBar(s, 0);
    }

    private void updateLoadBar() {
        updateLoadBar(loadingBarTxtValue, 1);
    }

    private void resetLoadBar(String s, int x) {
        loadingBarMaxValue = x;
        loadingBarTxtValue = s;
        loadingBarCurValue = 0;
        updateLoadBar(s);
    }

    // loads data into the program from local files
    private void loadData() {
        ArrayList<String> productCodesArr = new ArrayList<>(0);

        try {
            products = SudoSQL.getProducts(filePath);
        } catch (Exception e) {
            Log.logLine(e.getMessage());
        }
        try {
            contracts = SudoSQL.getContracts(filePath);
        } catch (Exception e) {
            Log.logLine(e.getMessage());
        }
        try {
            contractHeadings = SudoSQL.getContractHeading(filePath);
        } catch (Exception e) {
            Log.logLine(e.getMessage());
        }
        try {
            headingLines = SudoSQL.getHeadingLines(filePath);
        } catch (Exception e) {
            Log.logLine(e.getMessage());
        }
        try {
            qProducts = SudoSQL.getQproducts(filePath);
        } catch (Exception e) {
            Log.logLine(e.getMessage());
        }

        String[] engineers = SudoSQL.getEngineers(filePath);

        Log.logLine("Products Loaded: " + products.length);

        Stream.of(products).forEach(product -> productCodesArr.add(product.getProductID()));
        ci.setProducts(products);
        ci.setProductCodes(productCodesArr);
        ci.updateEngineers(engineers);

        repaint();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        Log.logLine("Window Closing");
        if (e.getSource().equals(ContractInterface.frame)) {
            Log.logLine("Loading built contract");
            ContractInterface.frame.setVisible(false);
            fullContract = ci.getContract();
            fm.backupContract(fullContract);
            if (serverFound) syncWithServer();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {
        Log.logLine("Window Closed");
        if (e.getSource().equals(sp.frame)) if (sp.getSelectedFile()) {
            Log.logLine("Loading selected File");
            getContract(sp.getContractID());
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}