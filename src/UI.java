import components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Stream;

public class UI extends JPanel implements ActionListener, WindowListener {

    private static final int port = 3306;

    // List of available tables in the database to be stored locally
    private final static String[] tables =
            new String[]{"altFilter", "beltGuard", "bin", "broughtOut", "broughtOutPaB", "cefFilter", "contract", "contractHeading", "controlBoxes", "cyclone", "dustUnit", "engineer", "erectionAndTransport", "fan", "filter", "headingLine", "hood", "impellor", "labourCost", "material", "materialCost", "oAndS", "pipeOrBend", "product", "pulleyBlastGate", "qProduct", "rawMatCost", "rotaryAndTwoWayValves", "silo", "soundEnclosure", "sprayBooth"};
    private final JFrame bulkUploadFrame = new JFrame("Upload Access dumps");
    private final Container southBox = new Container();
    private final HintTextField usernameBox = new HintTextField("Username");
    private final HintTextField passwordBox = new HintTextField("Password");
    private final JButton submit = new JButton("Load local data");
    private final JButton makeContract = new JButton("Make/Edit Contract");
    private final JButton searchContract = new JButton("Search Orders");
    private final JButton printInterface = new JButton("Print Contract");
    // sp allows user to search any loaded contracts
    private final SearchPanel sp = new SearchPanel();
    // Used to load and save files
    private final FileManager fm;
    // Used to talk to the SQL sever
    private final SQLInterface sql = new SQLInterface();
    // Stores company logo
    private final BufferedImage logo;
    private final PrinterJob job = PrinterJob.getPrinterJob();
    // used to find the SQL server
    private final PortSniffer ps = new PortSniffer();
    // Tables to store data from server
    // Stores printable version of logo
    private final Printer printer;
    private final JDialog makeIssued;
    private final String filePath;
    JDialog dateDialog = new JDialog();
    DatePicker dp = new DatePicker();
    // Builds and views contracts, Must be handed Contract by parent
    private ContractInterface ci;
    private FullContract fullContract = new FullContract();
    private String ip = "";
    private Contract[] contracts;
    private ContractHeading[] contractHeadings;
    private HeadingLine[] headingLines;
    private Product[] products;
    private String[] engineers;
    private Qproduct[] qProducts;
    private boolean serverFound = false;
    private double loadingBarMaxValue = 1;
    private double loadingBarCurValue = 1;
    private String loadingBarTxtValue = "Sync to begin";
    private HintTextField deliveryMethod = new HintTextField("Delivery Method");
    private String username = "";
    private String password = "";

    public UI(JFrame frame) {

        String myDocuments = null;
        JButton confirmDate = new JButton("Confirm Delivery Info");
        confirmDate.addActionListener(this);
        confirmDate.setActionCommand("ConfirmIssue&Print");
        dateDialog.setTitle("DELIVERY INFORMATION");
        dateDialog.setSize(350, 250);
        dateDialog.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        c.gridwidth = 2;

        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.insets = new Insets(0, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;

        dateDialog.add(dp, c);

        c.insets = new Insets(5, 0, 5, 0);
        c.fill = GridBagConstraints.NONE;

        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.LINE_START;
        c.ipadx = 190;
        dateDialog.add(deliveryMethod, c);
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 0.2;
        c.ipadx = 0;
        c.anchor = GridBagConstraints.LAST_LINE_END;
        dateDialog.add(confirmDate, c);

        try {
            Process p = Runtime.getRuntime()
                    .exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
            p.waitFor();

            InputStream in = p.getInputStream();
            byte[] b = new byte[in.available()];
            in.read(b);
            in.close();

            myDocuments = new String(b);
            myDocuments = myDocuments.split("\\s\\s+")[4];

            Log.logLine(myDocuments);

        } catch (Throwable t) {
            t.printStackTrace();
        }

        System.out.println(myDocuments);

        filePath = myDocuments + "\\apData";

        fm = new FileManager(filePath);
        ci = new ContractInterface(filePath);

        logo = fm.loadImage("/Images/Logo.png");
        printer = new Printer(fm.loadImage("/Images/PrinterLogo.png"), frame);

        sp.frame.addWindowListener(this);
        ContractInterface.frame.addWindowListener(this);

        submit.addActionListener(this);
        submit.setActionCommand("initialsync");
        submit.setFocusPainted(false);

        searchContract.addActionListener(this);
        searchContract.setActionCommand("sc");
        searchContract.setFocusPainted(false);

        printInterface.addActionListener(this);
        printInterface.setActionCommand("printDialog");
        printInterface.setFocusPainted(false);

        makeContract.addActionListener(this);
        makeContract.setActionCommand("makeContract");
        makeContract.setFocusPainted(false);

        JButton syncWithServer = new JButton("Sync");
        syncWithServer.addActionListener(this);
        syncWithServer.setActionCommand("serversync");

        JButton uploadDataButton = new JButton("Upload File");
        uploadDataButton.addActionListener(this);
        uploadDataButton.setActionCommand("uploadFile");

        // Constructs northern menu
        Container northBox = new Container();
        northBox.setLayout(new GridLayout(2, 3));
        northBox.add(submit);
        northBox.add(usernameBox);
        northBox.add(passwordBox);
        northBox.add(searchContract);
        northBox.add(makeContract);
        northBox.add(printInterface);

        // Disables program functionality untill it has loaded local data sources
        usernameBox.setEnabled(false);
        passwordBox.setEnabled(false);
        searchContract.setEnabled(false);
        makeContract.setEnabled(false);
        printInterface.setEnabled(false);

        southBox.setLayout(new GridLayout(1, 1));
        southBox.add(syncWithServer);

        this.setLayout(new BorderLayout());
        this.add(northBox, BorderLayout.NORTH);
        Container centerBox = new Container();
        this.add(centerBox, BorderLayout.CENTER);

        this.setFocusable(true);
        this.setBackground(new Color(31, 24, 94));

        JPanel buPanel = new JPanel();
        bulkUploadFrame.add(buPanel);
        bulkUploadFrame.setLocationRelativeTo(getParent());
        bulkUploadFrame.setSize(200, 100);

        buPanel.setLayout(new GridLayout(2, 1));
        JTextField queryBox = new JTextField("Target Table");
        buPanel.add(queryBox);
        buPanel.add(uploadDataButton);

        makeIssued = new JDialog(frame);
        makeIssued.setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        JButton y = new JButton("Issue & print");
        JButton n = new JButton("Just Print");
        JLabel l = new JLabel("Print Options");
        g.gridwidth = 2;
        makeIssued.add(l, g);
        g.gridwidth = 1;
        g.gridy = 1;
        makeIssued.add(y, g);
        g.gridx = 1;
        makeIssued.add(n, g);

        makeIssued.setUndecorated(true);
        makeIssued.setSize(200, 50);
        makeIssued.setLocationRelativeTo(frame);

        y.addActionListener(this);
        n.addActionListener(this);
        y.setActionCommand("print,issue");
        n.setActionCommand("print,leave");

        repaint();


        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String oDate;
        if (fm.readFile("Data/SyncDate.txt").length > 0) {
            oDate = fm.readFile("Data/SyncDate.txt")[0];
            if (getDayDiff(oDate) > 25) JOptionPane
                    .showMessageDialog(this, "ALERT: Your database is " + getDayDiff(oDate) + " days out of date.");
        } else {
            fm.saveFile("Data/SyncDate.txt", new String[]{"0001/01/01"});
        }

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

    private void uploadData() {

        ArrayList<String[]> entries = new ArrayList<>(0);
        String[] priority = new String[]{"E",// Engineer
                "EC" //Editing a Contract
        };
        URI f;
        try {

            f = new File(filePath + "\\Data\\uploadBuffer.txt").toURI();
            FileManager.checkFile(f.getPath());
            Object[] lines;
            lines = Files.lines(Paths.get(f)).toArray();
            for (Object line : lines) {// Loads all data into an array
                String ln = "" + line;
                Log.logLine("(BufferedUpload)-" + ln);
                entries.add(ln.split(":"));
            }
            for (String type : priority)// Used to cycle for each item in priority
                for (String[] entry : entries) // Checks each value
                    if ("E".equals(type) && entry[0].equals(type)) sql.addEngineer(entry[1]);

            fm.saveFile("\\Data\\uploadBuffer.txt", new String[]{"(UPLOAD:BUFFER)"});

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    private boolean syncWithServer() {

        // Loading bar reset
        uploadData();
        resetLoadBar("Connecting to server", tables.length);
        boolean error = false;
        SQLInterface.setDetails(ip, username, password);
        for (String table : tables) {
            updateLoadBar("Loading " + table);
            Log.logLine("Loading file" + table);
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
        return !error;
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
    }

    public void actionPerformed(ActionEvent e) {
        String event = e.getActionCommand();

        switch (event.split(",")[0]) {

            case "submit":
                username = usernameBox.getText();
                password = passwordBox.getText();
                SQLInterface.setDetails(ip, username, password);
                //offline mode is the default
                // It should allow the user to make search and delete orders on a local system
                if (serverFound) {
                    // Sets the button to assume it failed to connect
                    submit.setText("Go Online");
                    this.remove(southBox);
                    uploadData();
                    if (syncWithServer()) {
                        // Only runs if it succeeded to sync with the server data
                        this.add(southBox, BorderLayout.SOUTH);
                        submit.setText("Update Credentials");
                    }
                } else {
                    String serverIP;
                    // Uses basic account with minimal privileges to search for a server
                    serverIP = ps.searchForServer(username, password, port);
                    if (serverIP.split("-")[0].equals("UNAVAILABLE")) {
                        // Alerts the user that it could not find the server
                        // Sets the program into offline mode
                        serverFound = false;
                        resetLoadBar("CRITICAL ERROR", 1);
                        updateLoadBar();
                        JOptionPane.showMessageDialog(this, serverIP.split("-")[1], "CRITICAL FAILURE",
                                                      JOptionPane.ERROR_MESSAGE);
                        ci.setOffline(true);
                    } else {
                        // Sets the program into online mode and triggers the button to sync with server
                        // Sets the IP to be the valid IP returned by the server
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
                break;

            case "serversync":
                syncWithServer();
                break;

            case "initialsync":
                // Enables some of the programs functionality
                usernameBox.setEnabled(true);
                passwordBox.setEnabled(true);
                searchContract.setEnabled(true);
                makeContract.setEnabled(true);
                printInterface.setEnabled(true);

                // Loads data stored locally
                loadData();

                submit.setActionCommand("submit");
                submit.setText("Connect Server");
                // Sets synced to the true
                break;
            case "ConfirmIssue&Print":
                dateDialog.setVisible(false);
                fullContract.details.contractDate = LocalDate.now();
                fullContract.details.deliveryDate =
                        LocalDate.parse(dp.getSelectedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                fullContract.details.deliveryMethod = deliveryMethod.getText();
                sql.setContractDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                                    fullContract.details.contractID);
                sql.issueContract(fullContract.details.contractID);
                sql.setDeliveryDate(fullContract.details.deliveryDate, fullContract.details.contractID);
                sql.setDeliveryMethod(fullContract.details.deliveryMethod, fullContract.details.contractID);

                printer.updateContract(fullContract);
                printer.printContract(job);
                break;
            case "printDialog":
                makeIssued.setVisible(true);
                break;
            case "print":
                makeIssued.setVisible(false);
                if (event.split(",")[1].equalsIgnoreCase("issue")) {
                    if (serverFound) {
                        if (ci.getContract().details.issued) JOptionPane
                                .showMessageDialog(this, "This contract has already been issued.",
                                                   "DUPLICATE " + "ISSUE", JOptionPane.WARNING_MESSAGE);
                        dateDialog.setVisible(true);
                    } else JOptionPane.showMessageDialog(this, "You can not issue a Contract while offline.");
                } else {
                    printer.updateContract(ci.getContract());
                    if (printer.gotContract()) {
                        if (ci.getContract().details.issued)
                            JOptionPane.showMessageDialog(this, "This Contract has been issued.");
                        printer.printContract(job);
                    } else JOptionPane.showMessageDialog(this, "Please load or make a Contract first.");
                }
                break;
            default:
                Log.logLine("Unrecognised Command '" + event + "'");

        }

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

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

        products = SudoSQL.getProducts(filePath);
        contracts = SudoSQL.getContracts(filePath);
        contractHeadings = SudoSQL.getContractHeading(filePath);
        headingLines = SudoSQL.getHeadingLines(filePath);
        qProducts = SudoSQL.getQproducts(filePath);

        engineers = SudoSQL.getEngineers(filePath);

        Log.logLine("Products Loaded: " + products.length);

        Stream.of(products).forEach(product -> productCodesArr.add(product.productID));
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
            fullContract = ci.getContract();
            Log.logLine("");
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