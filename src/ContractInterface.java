import components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.PrinterJob;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class ContractInterface extends JPanel implements ActionListener, KeyListener {

    public static final JFrame frame = new JFrame();
    private final HintTextField contractNumber = new HintTextField("Contract Number", HintTextField.CENTER_HIDDEN);
    private final HintTextField companyName = new HintTextField("Company name", HintTextField.CENTER_HIDDEN);
    private final HintTextField address1 = new HintTextField("Address 1", HintTextField.CENTER_HIDDEN);
    private final HintTextField address2 = new HintTextField("Address 2", HintTextField.CENTER_HIDDEN);
    private final HintTextField address3 = new HintTextField("Address 3", HintTextField.CENTER_HIDDEN);
    private final HintTextField postcode = new HintTextField("Postcode", HintTextField.CENTER_HIDDEN);
    private final JComboBox<String> engineer = new JComboBox<>(new String[]{"engineersUnavailable"});
    private final JCheckBox quote = new JCheckBox();
    private final Container centerCont = new Container();
    private final Container idCont = new Container();
    private final Container descCont = new Container();
    private final Container quantCont = new Container();
    private final JPanel dataSection = new JPanel();
    private final JButton prevHeading = new JButton("< < < <");
    private final JButton nextHeading = new JButton("> > > >");
    private final JButton newEntry = new JButton("New Entry");
    private final JButton save = new JButton("Save");
    private final JButton printButton = new JButton("Print");
    private final JButton clear = new JButton("Clear");
    private final HintTextField sectionHeading = new HintTextField("Section Heading", HintTextField.RIGHT_LEADING);
    private final ArrayList<Entry> entries = new ArrayList<>(0);
    private final ArrayList<JTextField> ID = new ArrayList<>(0);
    private final ArrayList<JTextField> desc = new ArrayList<>(0);
    private final ArrayList<JTextField> quant = new ArrayList<>(0);
    private final ArrayList<Double[]> costs = new ArrayList<>(0);
    private final FileManager fm;
    private final SQLInterface sql = new SQLInterface();
    private final boolean offlineMode;
    private final DatePicker dp = new DatePicker();
    private final PrinterJob job = PrinterJob.getPrinterJob();
    private final JComboBox<? extends String> deliveryTypes = new JComboBox<>(
            new String[]{"Carrier Standard", "Carrier next day pre 10AM", "Erector deliver and fix", "Lorry", "customer Collect"});
    private final JDialog makeIssued;
    private final Printer printer;
    JDialog dateDialog = new JDialog();
    private String username = "VOID";
    private Boolean offline = true;
    private boolean issued = false;
    private FullContract fullContract = new FullContract();
    private int heading = 0;
    private String[] productCode = new String[0];
    private final SuggestionField sf = new SuggestionField(productCode, frame);
    private Product[] products = new Product[0];// Contains all products available

    public ContractInterface(boolean offlineMode) {

        fm = new FileManager();
        this.offlineMode = offlineMode;
        entries.add(new Entry());

        printer = new Printer(frame);

        contractNumber.setToolTipText("Contract number");
        companyName.setToolTipText("Company name");
        address1.setToolTipText("Address line 1");
        address2.setToolTipText("Address line 2");
        address3.setToolTipText("Address line 3");
        postcode.setToolTipText("Postcode");

        Container contractBox = new Container();
        contractBox.setLayout(new BoxLayout(contractBox, BoxLayout.X_AXIS));
        JLabel contractLabel = new JLabel("Quote:");
        contractBox.add(contractLabel);
        contractBox.add(quote);

        prevHeading.addActionListener(this);
        prevHeading.setActionCommand("previous");
        prevHeading.setFocusPainted(false);

        nextHeading.addActionListener(this);
        nextHeading.setActionCommand("next");
        nextHeading.setFocusPainted(false);

        Container navigation = new Container();
        navigation.setLayout(new BoxLayout(navigation, BoxLayout.X_AXIS));
        navigation.add(prevHeading);
        navigation.add(sectionHeading);
        navigation.add(nextHeading);

        idCont.setLayout(new BoxLayout(idCont, BoxLayout.Y_AXIS));
        descCont.setLayout(new BoxLayout(descCont, BoxLayout.Y_AXIS));
        quantCont.setLayout(new BoxLayout(quantCont, BoxLayout.Y_AXIS));

        GridBagConstraints gc = new GridBagConstraints();

        centerCont.setLayout(new GridBagLayout());

        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 0.1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        centerCont.add(idCont, gc);

        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 0.1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        centerCont.add(quantCont, gc);

        gc.gridx = 2;
        gc.gridy = 0;
        gc.weightx = 0.75;
        gc.fill = GridBagConstraints.HORIZONTAL;
        centerCont.add(descCont, gc);

        newEntry.addActionListener(this);
        newEntry.setActionCommand("newItem");
        newEntry.setFocusable(false);

        JScrollPane entryView = new JScrollPane(centerCont, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        dataSection.setLayout(new BorderLayout());
        dataSection.add(navigation, BorderLayout.NORTH);
        dataSection.add(entryView, BorderLayout.CENTER);
        dataSection.add(newEntry, BorderLayout.SOUTH);

        save.addActionListener(this);
        save.setActionCommand("Save");
        save.setAlignmentX(CENTER_ALIGNMENT);
        save.setFocusPainted(false);

        printButton.addActionListener(this);
        printButton.setActionCommand("printDialog");
        printButton.setAlignmentX(CENTER_ALIGNMENT);
        printButton.setFocusPainted(false);

        clear.addActionListener(this);
        clear.setActionCommand("Clear");
        clear.setAlignmentX(CENTER_ALIGNMENT);
        clear.setFocusPainted(false);

        JButton duplicate = new JButton("Duplicate");
        duplicate.addActionListener(this);
        duplicate.setActionCommand("Duplicate");
        duplicate.setAlignmentX(CENTER_ALIGNMENT);
        duplicate.setFocusPainted(true);

        JButton load = new JButton("Load Contract");
        load.addActionListener(this);
        load.setActionCommand("Load");
        load.setAlignmentX(CENTER_ALIGNMENT);
        load.setFocusPainted(true);

        engineer.addActionListener(this);
        engineer.setActionCommand("Engi");

        JLabel westTitle = new JLabel("Company details");
        westTitle.setAlignmentX(CENTER_ALIGNMENT);

        Container westCont = new Container();
        westCont.setLayout(new BoxLayout(westCont, BoxLayout.Y_AXIS));

        Container editSave = new Container();
        editSave.setLayout(new GridLayout(2, 2));

        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(westTitle);

        contractNumber.setMaximumSize(new Dimension(1000, 35));
        engineer.setMaximumSize(new Dimension(1000, 35));
        companyName.setMaximumSize(new Dimension(1000, 35));
        address1.setMaximumSize(new Dimension(1000, 35));
        address2.setMaximumSize(new Dimension(1000, 35));
        address3.setMaximumSize(new Dimension(1000, 35));
        postcode.setMaximumSize(new Dimension(1000, 35));
        contractBox.setMaximumSize(new Dimension(1000, 35));
        save.setMaximumSize(new Dimension(1000, 35));
        printButton.setMaximumSize(new Dimension(1000, 35));
        editSave.setMaximumSize(new Dimension(1000, 75));
        clear.setMaximumSize(new Dimension(1000, 35));
        load.setMaximumSize(new Dimension(1000, 35));

        editSave.add(save);
        editSave.add(printButton);
        editSave.add(duplicate);
        editSave.add(clear);

        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(contractNumber);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(engineer);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(companyName);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(address1);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(address2);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(address3);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(postcode);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(contractBox);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(editSave);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(load);

        this.setLayout(new BorderLayout());
        this.add(westCont, BorderLayout.WEST);
        this.add(dataSection, BorderLayout.CENTER);

        frame.setSize(800, 450);
        frame.setLocationRelativeTo(getParent());
        frame.add(this);

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

        dateDialog.setTitle("DELIVERY INFORMATION");
        dateDialog.setSize(350, 250);
        dateDialog.setLayout(new BorderLayout());
        Container south = new Container();
        south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));

        JButton confirmDate = new JButton("Confirm Delivery Info");
        confirmDate.addActionListener(this);
        confirmDate.setActionCommand("ConfirmIssue&Print");

        dateDialog.add(dp, BorderLayout.CENTER);

        south.add(deliveryTypes);
        south.add(confirmDate);
        dateDialog.add(south, BorderLayout.SOUTH);


        y.addActionListener(this);
        n.addActionListener(this);
        y.setActionCommand("print,issue");
        n.setActionCommand("print,leave");

    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void checkBoxes() {
        for (int i = 0;i < idCont.getComponentCount();i++) {
            checkBox(i);
        }
    }

    public void checkBox(int i) {
        idCont.getComponent(i).setBackground(new Color(231, 108, 108, 255));
        if (isValidCode(((HintTextField) idCont.getComponent(i)).getText())) {
            idCont.getComponent(i).setBackground(new Color(150, 231, 108, 255));
        }
    }

    public boolean isValidCode(String code) {
        for (String s : productCode)
            if (s.equals(code)) return true;
        return false;
    }

    public void showMenu() {
        if (issued) {
            disableEdits();
        } else {
            enableEdits();
        }
        refresh();
        frame.setVisible(true);
    }

    public void hideMenu() {
        if (issued) {
            disableEdits();
        } else {
            enableEdits();
        }
        refresh();
        frame.setVisible(false);
    }

    public void redraw() {
        idCont.removeAll();
        quantCont.removeAll();
        descCont.removeAll();
        sf.purge();
        for (int i = 0;i < ID.size();i++) {
            idCont.add(ID.get(i));
            quantCont.add(quant.get(i));
            descCont.add(desc.get(i));
            sf.addBox(ID.get(i));
        }
        idCont.revalidate();
        quantCont.revalidate();
        descCont.revalidate();
        centerCont.revalidate();
        dataSection.repaint();
        checkBoxes();
    }

    public FullContract getContract() {
        updateContract();
        return fullContract;
    }

    public void loadFromEntries() {
        sf.purge();
        if (heading >= entries.size()) entries.add(new Entry());
        String[] idata = entries.get(heading).getID();
        String[] ddata = entries.get(heading).getDesc();
        int[] qdata = entries.get(heading).getQuant();
        this.costs.clear();
        Collections.addAll(this.costs, entries.get(heading).getCosts());
        ID.clear();
        desc.clear();
        quant.clear();
        for (int i = 0;i < idata.length;i++) {// Storing locally reduces load times ~50x
            addField(idata[i], ddata[i], "" + qdata[i]);
        }
        sectionHeading.setText(entries.get(heading).getTitle());
        sectionHeading.setHint("(" + (heading + 1) + "/" + entries.size() + ")");
        sectionHeading.setEditable(save.isEnabled());
        checkBoxes();
    }

    public boolean saveToEntries(boolean force) {
        String[] rID = new String[ID.size()];
        String[] rDesc = new String[ID.size()];
        int[] rQuant = new int[ID.size()];
        int skips = 0;
        for (int i = 0;i < ID.size();i++) {
            if (Convert.isNumeric(quant.get(i).getText())) {
                if (Convert.getIfNumeric(quant.get(i).getText()) > 0 && (!ID.get(i).getText().isEmpty())) {
                    rID[i - skips] = ID.get(i).getText();
                    rDesc[i - skips] = desc.get(i).getText();
                    rQuant[i - skips] = (int) Convert.getIfNumeric(quant.get(i).getText());
                } else {
                    skips++;
                }
            } else {
                if (!force) if (!ID.get(i).getText().trim().equals("") || !desc.get(i).getText().trim().equals("")) {
                    rID[i - skips] = ID.get(i).getText();
                    rDesc[i - skips] = desc.get(i).getText();
                    rQuant[i - skips] = 1;
                } else {
                    skips++;
                }
            }
        }
        String[] tmpID = new String[rID.length - skips];
        String[] tmpDesc = new String[rID.length - skips];
        int[] tmpQuant = new int[rID.length - skips];
        for (int i = 0;i < tmpID.length;i++) {
            tmpID[i] = rID[i];
            tmpDesc[i] = rDesc[i];
            tmpQuant[i] = rQuant[i];
        }
        for (Entry e : entries) {
            String[] eID = e.getID();
            for (String id : eID) {
                for (Product p : products)
                    if (p.getProductID().equalsIgnoreCase(id)) {
                        fullContract.addProduct(p);
                        break;
                    }
            }
        }
        if (heading >= entries.size()) entries.add(new Entry());
        entries.get(heading).setCosts(cleanCosts(costs, tmpID).toArray(Double[][]::new));
        entries.get(heading).setID(tmpID);
        entries.get(heading).setDesc(tmpDesc);
        entries.get(heading).setQuant(tmpQuant);
        entries.get(heading).setTitle(sectionHeading.getText());
        if (entries.get(heading).getSize() == 0) entries.remove(heading);
        return true;
    }

    public void addCost(int pos, double cost) {
        Double[] toAdd = new Double[]{(double) pos, cost};
        boolean added = false;
        for (int i = 0;i < costs.size();i++) {
            if (costs.get(i)[0].equals(toAdd[0])) {
                costs.set(i, toAdd);
                added = true;
                break;
            }
        }
        if (!added) {
            costs.add(toAdd);
        }
    }

    public ArrayList<Double[]> cleanCosts(ArrayList<Double[]> c, String[] t) {
        for (int i = 0;i < c.size();i++) {
            if (c.get(i)[0] < t.length) {
                if (!(t[c.get(i)[0].intValue()].equalsIgnoreCase("QL") ||
                      t[c.get(i)[0].intValue()].equalsIgnoreCase("QP"))) {
                    c.remove(i);
                    i--;
                }
            } else {
                c.remove(i);
                i--;
            }

        }
        return c;
    }

    public void updateEngineers(String[] engineers) {
        engineer.removeAllItems();
        for (String e : engineers) {
            engineer.addItem(e);
        }
        if (engineers.length == 0) engineer.addItem("OTHER");
        engineer.setSelectedIndex(0);
        setEngi(fullContract.details.engineer);
    }

    public void setContractDetails(FullContract fc) {
        heading = 0;
        issued = false;
        entries.clear();
        ID.clear();
        desc.clear();
        quant.clear();
        fullContract = fc;
        Log.logLine("Contractor: " + fc.details.contractor);
        Log.logLine("Contract is selected: " + fc.details.quote);
        quote.setSelected(fc.details.quote);
        contractNumber.setText(fc.details.contractID);
        if (!fc.details.companyName.equalsIgnoreCase("NULL")) companyName.setText(fc.details.companyName);
        if (!fc.details.address1.equalsIgnoreCase("NULL")) address1.setText(fc.details.address1);
        if (!fc.details.address2.equalsIgnoreCase("NULL")) address2.setText(fc.details.address2);
        if (!fc.details.address3.equalsIgnoreCase("NULL")) address3.setText(fc.details.address3);
        if (!fc.details.postcode.equalsIgnoreCase("NULL")) postcode.setText(fc.details.postcode);
        setEngi(fc.details.engineer);
        String description;
        Log.logLine("================================Started searching for Contract " +
                    "details================================");
        for (ContractHeading contractHeading : fc.contractHeadings) {
            sectionHeading.setText(contractHeading.headingTitle);
            for (HeadingLine contractHeadingLine : fc.contractHeadingLine) {
                if (contractHeading.headingID == contractHeadingLine.headingID) {
                    for (Product product : fc.products)
                        if (contractHeadingLine.productID.equalsIgnoreCase(product.getProductID())) {
                            description = contractHeadingLine.comment;
                            if (description == null) description = "";
                            addField(product.getProductID(), description, contractHeadingLine.quantity + "");
                            if (product.getProductID().equals("QP") || product.getProductID().equals("QL")) {
                                for (Qproduct qproduct : fc.qProducts) {
                                    if (contractHeadingLine.headingLineID == qproduct.headingLineID) {
                                        costs.add(new Double[]{(double) ID.size() - 1, qproduct.cost.doubleValue()});
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                }
            }
            Log.logLine("Next Line");
            nextHeading.doClick();
        }
        while (heading > 0) prevHeading.doClick();
        Log.logLine("Found Contract details");
        if (fc.details.issued) {
            disableEdits();
        } else {
            enableEdits();
        }
        refresh();
        Log.logLine("Finished Loading");
    }

    private void setEngi(String engy) {
        boolean add = true;
        for (int i = 0;i < engineer.getItemCount();i++) {
            if (engineer.getItemAt(i).equals(engy)) {
                add = false;
                break;
            }
        }
        if (add) {
            fm.appendToFile("Data/uploadBuffer", "E:" + engy);
            engineer.addItem(engy);
        }
        engineer.setSelectedItem(engy);
    }

    public void addField(String ID, String comment, String quant) {
        HintTextField a = new HintTextField("ID", HintTextField.CENTER_HIDDEN);
        HintTextField b = new HintTextField("Comment", HintTextField.CENTER_HIDDEN);
        HintTextField c = new HintTextField("Quantity", HintTextField.CENTER_HIDDEN);
        a.setText(ID);
        sf.addBox(a);
        if (!comment.equals("null")) b.setText(comment);
        else b.setText("");
        c.setText(quant);
        a.addKeyListener(this);
        a.setFocusTraversalKeysEnabled(false);
        b.addKeyListener(this);
        b.setFocusTraversalKeysEnabled(false);
        c.addKeyListener(this);
        c.setFocusTraversalKeysEnabled(false);
        this.ID.add(a);
        this.desc.add(b);
        this.quant.add(c);
        this.idCont.add(a);
        this.descCont.add(b);
        this.quantCont.add(c);
        this.centerCont.revalidate();
        this.dataSection.repaint();
        this.repaint();
    }

    public void enableEdits() {
        issued = false;
        newEntry.setEnabled(true);
        newEntry.setText("New Entry");
        save.setEnabled(true);
        contractNumber.setEditable(true);
        engineer.setEnabled(true);
        postcode.setEditable(true);
        address1.setEditable(true);
        address2.setEditable(true);
        address3.setEditable(true);
        companyName.setEditable(true);
        printButton.setEnabled(true);
        quote.setEnabled(true);
        if (!sectionHeading.getText().isEmpty()) saveToEntries(false);
        loadFromEntries();
    }

    public void disableEdits() {
        issued = true;
        newEntry.setEnabled(false);
        newEntry.setText("(Issued)");
        save.setEnabled(false);
        contractNumber.setEditable(false);
        engineer.setEnabled(false);
        postcode.setEditable(false);
        address1.setEditable(false);
        address2.setEditable(false);
        address3.setEditable(false);
        companyName.setEditable(false);
        printButton.setEnabled(false);
        quote.setEnabled(false);
        if (!sectionHeading.getText().isEmpty()) saveToEntries(false);
        loadFromEntries();
    }

    public void refresh() {
        saveToEntries(true);
        loadFromEntries();
        checkBoxes();
        redraw();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand().split(",")[0];

        switch (command) {


            case "next":
                if (!issued) {
                    if (sectionHeading.getText().equals("") && heading == entries.size() - 1) {
                        JOptionPane.showMessageDialog(this, "Enter section heading");
                        break;
                    }
                    if (saveToEntries(false)) {
                        heading++;
                        if (heading >= entries.size()) entries.add(new Entry());
                        loadFromEntries();
                        if (sectionHeading.getText().isEmpty()) sectionHeading.setText("HEADING " + heading);
                    }
                } else {
                    if (heading <= entries.size() - 1) {
                        heading++;
                        loadFromEntries();
                    }
                }
                checkBoxes();
                redraw();
                break;

            case "previous":
                if (heading != 0) {
                    if (saveToEntries(false)) {
                        if (sectionHeading.getText().equals("") && heading < entries.size()) entries.remove(heading);
                        heading--;
                        loadFromEntries();
                    }
                    redraw();
                }
                checkBoxes();
                break;

            case "newItem":
                if (ID.size() < 20) addField("", "", "");
                else JOptionPane.showMessageDialog(frame, "Headings only support 25 ", "Maximum heading size",
                        JOptionPane.ERROR_MESSAGE);
                break;

            case "Duplicate":
                contractNumber.setText("");
                engineer.setSelectedItem("OTHER");
                quote.setSelected(false);
                fullContract.details.clear();
                issued = false;
                enableEdits();
                updateContract();
                nextHeading.doClick();
                prevHeading.doClick();
                break;

            case "Save": {
                boolean su = true;
                updateContract();
                String cn = fullContract.details.contractID;
                if (cn.isEmpty()) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "No contractID provided");
                } else if (quote.isSelected()) {
                    if (cn.startsWith("Q") || cn.startsWith("q")) {
                        cn = cn.substring(1);
                        fullContract.details.contractID = cn;
                    }
                } else if (cn.length() < 5) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "ContractID must be 5 numeric digits");
                } else if (!Convert.isNumeric(cn.substring(0, 5))) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "ContractID must be numeric");
                }
                if (offline) {
                    if (offlineMode) {
                        JOptionPane.showMessageDialog(frame, "Saving offline is currently disabled");
                    } else {
                        JFileChooser fd = new JFileChooser();
                        fd.setDialogTitle("Save Contract");
                        fd.setCurrentDirectory(new File(FileManager.filePath + "\\Data\\Contracts"));
                        fd.setSelectedFile(new File(
                                fd.getCurrentDirectory().getPath() + "/Contract_" + fullContract.details.contractID));
                        if (fd.showDialog(this, "Save Contract") == JFileChooser.APPROVE_OPTION) {
                            if (!fd.getSelectedFile().getPath().endsWith(".cot"))
                                fm.saveContract(fullContract, new File(fd.getSelectedFile().getPath() + ".cot"));
                            else fm.saveContract(fullContract, new File(fd.getSelectedFile().getPath()));
                        }
                        JOptionPane.showMessageDialog(frame, "Saved contract.");
                    }
                } else if (sql.contractExists(cn) && su) {
                    if (JOptionPane.showConfirmDialog(null,
                            "You are about to update contract " + cn + " as it " + "already " +
                            "exists.\nDo you wish to continue to overwrite " + cn + "?") == 0) JOptionPane
                            .showMessageDialog(this, sql.reuploadContract(fullContract), "Contract amend response",
                                    JOptionPane.INFORMATION_MESSAGE);
                } else if (su) {
                    JOptionPane.showMessageDialog(this, sql.pushContract(fullContract), "Contract upload response",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            }
            case "Load":
                int cont;
                updateContract();
                JFileChooser fd = new JFileChooser();
                fd.setCurrentDirectory(new File(FileManager.filePath + "\\Data\\Contracts"));
                fd.setDialogTitle("Load Contract");
                do {
                    cont = 0;
                    fd.showDialog(this, "Load contact");
                    if (!fd.getSelectedFile().getPath().endsWith(".cot")) cont = JOptionPane.showConfirmDialog(this,
                            "The selected file is not supported. Loading the file may not" + " work\n Load anyway?",
                            "FILE FORMAT", JOptionPane.YES_NO_CANCEL_OPTION);

                } while (cont == 1);
                if (cont == 2) {
                    break;
                } else {
                    clear.doClick();
                    Log.logLine("Loading file");
                    loadContract(fd.getSelectedFile());
                }
                break;

            case "Clear":
                contractNumber.setText("");
                entries.clear();
                engineer.setSelectedItem("OTHER");
                companyName.setText("");
                address1.setText("");
                address2.setText("");
                address3.setText("");
                postcode.setText("");
                fullContract.purge();
                heading = 0;
                sf.purge();
                sectionHeading.setText("");
                this.ID.clear();
                this.desc.clear();
                this.quant.clear();
                this.idCont.removeAll();
                this.descCont.removeAll();
                this.quantCont.removeAll();
                entries.add(new Entry());
                saveToEntries(true);
                loadFromEntries();
                enableEdits();
                repaint();
                break;

            case "printDialog":
                makeIssued.setVisible(true);
                break;
            case "print":
                fullContract = getContract();
                makeIssued.setVisible(false);
                if (e.getActionCommand().split(",")[1].equalsIgnoreCase("issue")) {
                    if (!offline) {
                        if (fullContract.details.issued) JOptionPane
                                .showMessageDialog(this, "This contract has already been issued.", "DUPLICATE ISSUE",
                                        JOptionPane.WARNING_MESSAGE);
                        else if (fullContract.details.quote) JOptionPane
                                .showMessageDialog(this, "Can not issue a quote.", "QUOTE ISSUE",
                                        JOptionPane.WARNING_MESSAGE);
                        else dateDialog.setVisible(true);
                    } else JOptionPane.showMessageDialog(this, "You can not issue a Contract while offline.");
                } else {
                    printer.updateContract(fullContract);
                    if (printer.gotContract()) {
                        if (fullContract.details.issued)
                            JOptionPane.showMessageDialog(this, "This Contract has been issued.");

                        printer.setAllPrints(false);
                        printer.printContract(job);
                    } else JOptionPane.showMessageDialog(this, "Please load or make a Contract first.");
                }
                break;
            case "ConfirmIssue&Print":
                dateDialog.setVisible(false);
                if(!sql.contractExists(fullContract.details.contractID))
                    sql.pushContract(fullContract);
                fullContract.details.contractDate = LocalDate.now();
                fullContract.details.deliveryDate =
                        LocalDate.parse(dp.getSelectedDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                fullContract.details.deliveryMethod =
                        Objects.requireNonNull(deliveryTypes.getSelectedItem()).toString();
                fullContract.details.issued = true;
                fullContract.details.contractor = username;
                sql.setContractDate(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                        fullContract.details.contractID);
                sql.setContractor(fullContract.details.contractID, username);
                sql.issueContract(fullContract.details.contractID);
                sql.setDeliveryDate(fullContract.details.deliveryDate, fullContract.details.contractID);
                sql.setDeliveryMethod(fullContract.details.deliveryMethod, fullContract.details.contractID);
                printer.setAllPrints(true);
                printer.updateContract(fullContract);
                printer.printContract(job);
                break;


            default:
                Log.logLine("Unknown command " + command);
                break;
        }

        repaint();
    }

    public void loadContract(File file) {
        String[] data = fm.readFile(file);
        fullContract.purge();
        String[] cDetails = data[0].split("~~");

        Contract c = new Contract();
        if (cDetails.length >= 1) c.contractID = cDetails[0];
        if (cDetails.length >= 1) c.contractDate = Convert.getIfDate(cDetails[1]);
        if (cDetails.length >= 3) c.companyName = cDetails[2];
        if (cDetails.length >= 4) c.address1 = cDetails[3];
        if (cDetails.length >= 5) c.address2 = cDetails[4];
        if (cDetails.length >= 6) c.address3 = cDetails[5];
        if (cDetails.length >= 7) c.postcode = cDetails[6];
        if (cDetails.length >= 8) c.deliveryMethod = cDetails[7];
        if (cDetails.length >= 9) c.deliveryDate = Convert.getIfDate(cDetails[8]);
        if (cDetails.length >= 10) c.quote = Convert.getBoolean(cDetails[9]);
        if (cDetails.length >= 11) c.issued = Convert.getBoolean(cDetails[10]);
        if (cDetails.length >= 12) c.engineer = cDetails[11];
        if (cDetails.length >= 13) c.contractor = cDetails[12];
        fullContract.setDetails(c);

        String[] heading = data[1].split("~~");
        String[] headingLine = data[2].split("~~");
        String[] qProducts = data[3].split("~~");
        ArrayList<ContractHeading> contractHeading = new ArrayList<>(0);
        ArrayList<HeadingLine> contractHeadingLine = new ArrayList<>(0);
        ArrayList<Product> products = new ArrayList<>(0);
        ArrayList<Qproduct> qproducts = new ArrayList<>(0);

        for (String s : heading) {
            if (s.split("%50").length == 3) contractHeading
                    .add(new ContractHeading((int) Convert.getIfNumeric(s.split("%50")[0]), s.split("%50")[1],
                            s.split("%50")[2]));
        }
        Log.logLine("Heading Count: " + contractHeading.size());

        for (String s : headingLine) {
            if (s.split("%50").length == 5) contractHeadingLine
                    .add(new HeadingLine((int) Convert.getIfNumeric(s.split("%50")[0]),
                            (int) Convert.getIfNumeric(s.split("%50")[1]), s.split("%50")[2], s.split("%50")[3],
                            (int) Convert.getIfNumeric(s.split("%50")[4])));
        }
        Log.logLine("Heading Line Count: " + contractHeadingLine.size());


        for (HeadingLine head : contractHeadingLine) {
            for (Product prod : this.products) {
                if (head.productID.equalsIgnoreCase(prod.getProductID())) {
                    products.add(prod);
                }
            }
        }

        for (String s : qProducts) {
            if (s.split("%50").length == 4) qproducts.add(new Qproduct((int) Convert.getIfNumeric(s.split("%50")[0]),
                    (int) Convert.getIfNumeric(s.split("%50")[1]), Convert.getIfNumeric(s.split("%50")[2]),
                    s.split("%50")[3]));
        }

        Log.logLine("Product Count: " + products.size());

        fullContract.contractHeadings = contractHeading;
        fullContract.contractHeadingLine = contractHeadingLine;
        fullContract.products = products;
        fullContract.qProducts = qproducts;

        setContractDetails(fullContract);
    }

    public void updateContract() {
        String[] ID;
        String[] desc;
        int[] quant;
        int headingNumber;
        int headingLineNumber;
        String deliveryMethod = fullContract.details.deliveryMethod;
        LocalDate deliveryDate = fullContract.details.deliveryDate;
        LocalDate contractDate = fullContract.details.contractDate;
        String contractor = fullContract.details.contractor;
        fullContract.purge();
        fullContract.details.contractID = contractNumber.getText();
        fullContract.details.engineer = engineer.getSelectedItem() + "";
        fullContract.details.address1 = address1.getText();
        fullContract.details.address2 = address2.getText();
        fullContract.details.address3 = address3.getText();
        fullContract.details.postcode = postcode.getText();
        fullContract.details.contractDate = contractDate;
        fullContract.details.deliveryMethod = deliveryMethod;
        fullContract.details.deliveryDate = deliveryDate;
        fullContract.details.companyName = companyName.getText();
        fullContract.details.quote = quote.isSelected();
        fullContract.details.issued = issued;
        fullContract.details.contractor = contractor;
        if (saveToEntries(false)) for (Entry entry : entries) {
            headingNumber = fullContract.addHeading(entry.getTitle());
            ID = entry.getID();
            desc = entry.getDesc();
            quant = entry.getQuant();
            for (int i = 0;i < entry.getSize();i++) {
                headingLineNumber = fullContract.addHeadingLine(headingNumber, ID[i], quant[i], desc[i]);
                if (ID[i].equals("QP") || ID[i].equals("QL")) {
                    fullContract.addQProduct(headingLineNumber, entry.getQp(i), ID[i]);
                }
                for (Product pr : products)
                    if (pr.getProductID().equalsIgnoreCase(ID[i])) {
                        Log.logLine(pr.getProductID());
                        fullContract.addProduct(pr);
                        break;
                    }
            }
        }
    }

    public void setProductCodes(ArrayList<String> x) {
        productCode = sort(x.toArray(new String[]{}));
        sf.updateValues(productCode);
    }

    public void setOffline(boolean b) {
        offline = b;
    }

    public void setProducts(Product[] productsL) {
        products = productsL;
    }


    public String[] sort(String[] arr) {
        int n = arr.length;
        // Build heap (rearrange array)
        for (int i = n / 2 - 1;i >= 0;i--)
            heapify(arr, n, i);
        // One by one extract an element from heap
        for (int i = n - 1;i > 0;i--) {
            // Move current root to end
            String temp = arr[0];
            arr[0] = arr[i];
            arr[i] = temp;
            // call max heapify on the reduced heap
            heapify(arr, i, 0);
        }
        return arr;
    }

    // To heapify a subtree rooted with node i which is
    // an index in arr[]. n is size of heap
    void heapify(String[] arr, int n, int i) {
        int largest = i; // Initialize largest as root
        int l = 2 * i + 1; // left = 2*i + 1
        int r = 2 * i + 2; // right = 2*i + 2
        // If left child is larger than root
        if (l < n && arr[l].compareTo(arr[largest]) > 0) largest = l;
        // If right child is larger than largest so far
        if (r < n && arr[r].compareTo(arr[largest]) > 0) largest = r;
        // If largest is not root
        if (largest != i) {
            String swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            // Recursively heapify the affected sub-tree
            heapify(arr, n, largest);
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        Log.logLine(e.getKeyCode());
        if (e.getKeyCode() == 9) {
            for (int i = 0;i < idCont.getComponentCount();i++) {
                if (e.getSource().equals(idCont.getComponent(i))) {
                    String s = ((HintTextField) idCont.getComponent(i)).getText();
                    if (s.equalsIgnoreCase("QP") || s.equalsIgnoreCase("QL")) {
                        double cost = Convert.getIfNumeric(JOptionPane.showInputDialog("Product Price"));
                        if (cost < 0) cost = 0.00;
                        addCost(i, cost);
                        repaint();
                    }
                    sf.apply();
                    Log.logLine("Grabbing Focus");
                    s = ((HintTextField) idCont.getComponent(i)).getText();
                    if (s.length() >= 3) {
                        String s2;
                        if (s.toUpperCase().endsWith("BKT")) {
                            s2 = JOptionPane.showInputDialog(frame, "Bracket center line height (mm):", "BRACKET",
                                    JOptionPane.WARNING_MESSAGE);
                            int v = (int) ((Convert.getIfNumeric(s.replaceAll("BKT", "")) * 25.4) + 40) / 2;
                            while (Convert.getIfNumeric(s2) < v) s2 = JOptionPane.showInputDialog(frame,
                                    "INVALID BRACKET CENTER LINE VALUE\nBracket " + "center line height (mm):",
                                    "BRACKET", JOptionPane.WARNING_MESSAGE);
                            ((HintTextField) descCont.getComponent(i))
                                    .setText("Bracket center line height " + s2 + "mm");
                        } else if (s.toUpperCase().endsWith("WB")) {
                            s2 = JOptionPane.showInputDialog(frame, "Bracket wall to center line (mm):", "BRACKET",
                                    JOptionPane.WARNING_MESSAGE);
                            int v = (int) ((Convert.getIfNumeric(s.replaceAll("WB", "")) * 25.4) + 40) / 2;
                            while (Convert.getIfNumeric(s2) < v) s2 = JOptionPane.showInputDialog(frame,
                                    "INVALID BRACKET WALL LINE VALUE\nBracket wall to center line (mm):", "BRACKET",
                                    JOptionPane.WARNING_MESSAGE);
                            ((HintTextField) descCont.getComponent(i))
                                    .setText("Bracket wall to center line " + s2 + "mm");
                        }
                    }
                    ((HintTextField) quantCont.getComponent(i)).grabFocus();
                    Log.logLine("Got Focus");
                    checkBox(i);
                    break;
                }
                if (e.getSource().equals(quantCont.getComponent(i))) {
                    if (((HintTextField) quantCont.getComponent(i)).getText().isBlank())
                        ((HintTextField) quantCont.getComponent(i)).setText("1");
                    ((HintTextField) descCont.getComponent(i)).grabFocus();
                    break;
                }
                if (e.getSource().equals(descCont.getComponent(i))) {
                    if (i + 1 == idCont.getComponentCount()) newEntry.doClick();
                    ((HintTextField) idCont.getComponent(i + 1)).grabFocus();
                    break;
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
