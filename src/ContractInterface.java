import components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.time.LocalDate;
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
    private final JButton edit = new JButton("Amend");
    private final Container editSave = new Container();
    private final JButton clear = new JButton("Clear");
    private final JButton load = new JButton("Load Contract");
    private final HintTextField sectionHeading = new HintTextField("Section Heading", HintTextField.RIGHT_LEADING);
    private final ArrayList<Entry> entries = new ArrayList<>(0);
    private final ArrayList<JTextField> ID = new ArrayList<>(0);
    private final ArrayList<JTextField> desc = new ArrayList<>(0);
    private final ArrayList<JTextField> quant = new ArrayList<>(0);
    private final ArrayList<Double[]> costs = new ArrayList<>(0);
    private final FileManager fm;
    private final SQLInterface sql = new SQLInterface();
    private Boolean offline = true;
    private boolean issued = false;
    private FullContract fullContract = new FullContract();
    private int heading = 0;
    private String[] productCode = new String[0];
    private final SuggestionField sf = new SuggestionField(productCode, frame);
    private boolean loading = false;
    private Product[] products = new Product[0];// Contains all products available

    public ContractInterface(String filePath) {

        fm = new FileManager(filePath);

        entries.add(new Entry());

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

        edit.addActionListener(this);
        edit.setActionCommand("Amend");
        edit.setAlignmentX(CENTER_ALIGNMENT);
        edit.setFocusPainted(false);

        clear.addActionListener(this);
        clear.setActionCommand("Clear");
        clear.setAlignmentX(CENTER_ALIGNMENT);
        clear.setFocusPainted(false);

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

        editSave.setLayout(new BoxLayout(editSave, BoxLayout.X_AXIS));

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
        edit.setMaximumSize(new Dimension(1000, 35));
        editSave.setMaximumSize(new Dimension(1000, 35));
        clear.setMaximumSize(new Dimension(1000, 35));
        load.setMaximumSize(new Dimension(1000, 35));

        editSave.add(save);
        editSave.add(edit);

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
        westCont.add(clear);
        westCont.add(Box.createRigidArea(new Dimension(1, 10)));
        westCont.add(load);

        this.setLayout(new BorderLayout());
        this.add(westCont, BorderLayout.WEST);
        this.add(dataSection, BorderLayout.CENTER);

        frame.setSize(800, 450);
        frame.setLocationRelativeTo(getParent());
        frame.add(this);

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
    }

    public boolean saveToEntries(boolean force) {
        String[] rID = new String[ID.size()];
        String[] rDesc = new String[ID.size()];
        int[] rQuant = new int[ID.size()];
        ArrayList<Integer> qProductPos = new ArrayList<>(0);
        int skips = 0;
        for (int i = 0;i < ID.size();i++) {
            if (Convert.isNumeric(quant.get(i).getText())) {
                if (Convert.getIfNumeric(quant.get(i).getText()) > 0) {
                    rID[i - skips] = ID.get(i).getText();
                    rDesc[i - skips] = desc.get(i).getText();
                    rQuant[i - skips] = (int) Convert.getIfNumeric(quant.get(i).getText());
                } else {
                    skips++;
                }
            } else {
                if (!force) if (!ID.get(i).getText().trim().equals("") || !desc.get(i).getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity", "ERROR", JOptionPane.ERROR_MESSAGE);
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
            for (int i = 0, eIDLength = eID.length;i < eIDLength;i++) {
                String id = eID[i];
                if ((id.equals("QP") || id.equals("QL"))) qProductPos.add(i);
                else for (Product p : products)
                    if (p.productID.equalsIgnoreCase(id)) {
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
        engineer.addItem("NEW ENGINEER");
        for (String e : engineers) {
            engineer.addItem(e);
        }
        engineer.setSelectedIndex(0);
    }

    public void setContractDetails(FullContract fc) {
        heading = 0;
        loading = true;
        entries.clear();
        ID.clear();
        desc.clear();
        quant.clear();
        fullContract = fc;
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
                        if (contractHeadingLine.productID.equalsIgnoreCase(product.productID)) {
                            description = contractHeadingLine.comment;
                            if (description == null) description = "";
                            addField(product.productID, description, contractHeadingLine.quantity + "");
                            if (product.productID.equals("QP") || product.productID.equals("QL")) {
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
            nextHeading.doClick();
        }
        Log.logLine("Found Contract details");
        if (fc.details.issued) {
            disableEdits();
        } else {
            enableEdits();
        }

        while (heading > 0) prevHeading.doClick();
        showMenu();
        Log.logLine("Finished Loading");
        loading = false;
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
        if (quant.equals("")) c.setText("1");
        else c.setText(quant);
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
        quote.setEnabled(false);
        if (!sectionHeading.getText().isEmpty()) saveToEntries(false);
        loadFromEntries();
    }

    public void refresh() {
        saveToEntries(true);
        loadFromEntries();
        redraw();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

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
                    }
                } else {
                    if (heading <= entries.size() - 1) {
                        heading++;
                        loadFromEntries();
                    }
                }
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
                break;

            case "newItem":
                addField("", "", "");
                break;

            case "Engi":
                if (!loading) {
                    if (frame.isVisible() && Objects.equals(engineer.getSelectedItem(), "NEW ENGINEER")) {
                        String engy =
                                JOptionPane.showInputDialog(this, "Engineer ID:").toUpperCase().replaceAll(" ", "");
                        boolean add = true;
                        for (int i = 0;i < engineer.getItemCount();i++) {
                            if (engineer.getItemAt(i).equals(engy)) {
                                add = false;
                                break;
                            }
                        }
                        if (add) {
                            fm.appendToFile("Data/uploadBuffer.txt", "E:" + engy);
                            engineer.addItem(engy);
                        }
                        engineer.setSelectedItem(engy);
                    }
                }
                break;

            case "Save": {
                boolean su = true;
                String message = "";
                updateContract();
                String cn = fullContract.details.contractID;
                if (cn.isEmpty()) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "No contractID provided");
                } else if (cn.length() < 6) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "ContractID must be 6 numeric digits");
                } else if (!Convert.isNumeric(cn.substring(0, 6))) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "ContractID must be numeric");
                } else if (offline) {
                    su = false;
                    JFileChooser fd = new JFileChooser();
                    fd.setDialogTitle("Save Contract");
                    fd.setSelectedFile(new File(
                            fd.getCurrentDirectory().getPath() + "/Contract_" + fullContract.details.contractID));
                    if (fd.showDialog(this, "Save Contract") == JFileChooser.APPROVE_OPTION) {
                        if (!fd.getSelectedFile().getPath().endsWith(".cot"))
                            fm.saveContract(fullContract, new File(fd.getSelectedFile().getPath() + ".cot"));
                        else fm.saveContract(fullContract, new File(fd.getSelectedFile().getPath()));
                    }
                } else {
                    message = sql.pushContract(fullContract);
                }
                if (su) JOptionPane.showMessageDialog(this, message, "Contract upload response",
                                                      JOptionPane.INFORMATION_MESSAGE);
                break;
            }

            case "Amend":
                Boolean su = true;
                updateContract();
                String message = "";
                String cn = fullContract.details.contractID;
                if (cn.isEmpty()) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "No contractID provided");
                } else if (cn.length() < 6) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "ContractID must be 6 numeric digits");
                } else if (!Convert.isNumeric(cn.substring(0, 6))) {
                    su = false;
                    JOptionPane.showMessageDialog(frame, "ContractID must be numeric");
                } else if (offline) {
                    JOptionPane.showMessageDialog(this, "You can not amend contracts offline. To save a new version " +
                                                        "of the contract while offline overwrite the original using " +
                                                        "the [save] " + "button.", "ERROR",
                                                  JOptionPane.WARNING_MESSAGE);
                } else {
                    message = sql.reuploadContract(fullContract);
                }
                if(su) JOptionPane.showMessageDialog(this, message, "Contract amend response",
                                                     JOptionPane.INFORMATION_MESSAGE);
                break;

            case "Load":
                int cont;
                updateContract();
                JFileChooser fd = new JFileChooser();

                fd.setDialogTitle("Load Contract");
                do {
                    cont = 0;
                    fd.showDialog(this, "Load contact");
                    if (!fd.getSelectedFile().getPath().endsWith(".cot")) cont = JOptionPane.showConfirmDialog(this,
                                                                                                               "The selected file is not supported. Loading the file may not" +
                                                                                                               " work\n Load anyway?",
                                                                                                               "FILE FORMAT",
                                                                                                               JOptionPane.YES_NO_CANCEL_OPTION);

                } while (cont == 1);
                if (cont == 2) {
                    break;
                } else {
                    clear.doClick();
                    Log.logLine("Loading file");
                    String[] data = fm.readFile(fd.getSelectedFile());
                    fullContract.purge();
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
                    fullContract.setDetails(c);

                    String[] heading = data[1].split("~~");
                    String[] headingLine = data[2].split("~~");
                    String[] qProducts = data[3].split("~~");
                    ArrayList<ContractHeading> contractHeading = new ArrayList<>(0);
                    ArrayList<HeadingLine> contractHeadingLine = new ArrayList<>(0);
                    ArrayList<Product> products = new ArrayList<>(0);
                    ArrayList<Qproduct> qproducts = new ArrayList<>(0);


                    for (String s : heading) {
                        contractHeading.add(new ContractHeading((int) Convert.getIfNumeric(s.split("%50")[0]),
                                                                s.split("%50")[1], s.split("%50")[2]));
                    }
                    Log.logLine("Heading Count: " + contractHeading.size());

                    for (String s : headingLine) {
                        Log.logLine(s.split("%50"));
                        contractHeadingLine.add(new HeadingLine((int) Convert.getIfNumeric(s.split("%50")[0]),
                                                                (int) Convert.getIfNumeric(s.split("%50")[1]),
                                                                s.split("%50")[2], s.split("%50")[3],
                                                                (int) Convert.getIfNumeric(s.split("%50")[4])));
                    }
                    Log.logLine("Heading Line Count: " + contractHeadingLine.size());


                    for (HeadingLine head : contractHeadingLine) {
                        for (Product prod : this.products) {
                            if (head.productID.equalsIgnoreCase(prod.productID)) {
                                products.add(prod);
                            }
                        }
                    }

                    for (String s : qProducts) {
                        qproducts.add(new Qproduct((int) Convert.getIfNumeric(s.split("%50")[0]),
                                                   (int) Convert.getIfNumeric(s.split("%50")[1]),
                                                   Convert.getIfNumeric(s.split("%50")[2]), s.split("%50")[3]));
                    }

                    Log.logLine("Product Count: " + products.size());

                    fullContract.contractHeadings = contractHeading;
                    fullContract.contractHeadingLine = contractHeadingLine;
                    fullContract.products = products;
                    fullContract.qProducts = qproducts;

                    setContractDetails(fullContract);
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

            default:
                Log.logLine("Unknown command " + command);
                break;
        }

        repaint();
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
        fullContract.purge();
        fullContract.details.contractID = contractNumber.getText();
        fullContract.details.engineer = "" + engineer.getSelectedItem();
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
                    if (pr.productID.equalsIgnoreCase(ID[i])) {
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
        if (e.getKeyCode() == 9 || e.getKeyCode() == 13) {
            for (int i = 0;i < idCont.getComponentCount();i++) {
                if (e.getSource().equals(idCont.getComponent(i))) {
                    String s = ((HintTextField) idCont.getComponent(i)).getText();
                    if (s.equalsIgnoreCase("QP") || s.equalsIgnoreCase("QL")) {
                        Double cost = Convert.getIfNumeric(JOptionPane.showInputDialog("Product Price"));
                        if (cost < 0) cost = 0.00;
                        addCost(i, cost);
                        repaint();
                    }
                    sf.apply();
                    sf.leaving = true;
                    ((HintTextField) quantCont.getComponent(i)).grabFocus();
                    break;
                }
                if (e.getSource().equals(quantCont.getComponent(i))) {
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
