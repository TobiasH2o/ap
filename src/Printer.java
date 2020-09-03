import components.ContractHeading;
import components.HeadingLine;
import components.Product;
import components.Qproduct;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Printer implements Printable, ActionListener {

    private final int lineBuffer = 5;
    private final int closeLineBuffer = 2;
    private final JCheckBox engineersCopy = new JCheckBox();
    private final JCheckBox bends = new JCheckBox();
    private final JCheckBox stockItems = new JCheckBox();
    private final JCheckBox skilled = new JCheckBox();
    private final JCheckBox plasma = new JCheckBox();
    private final JCheckBox pipes = new JCheckBox();
    private final JCheckBox loading = new JCheckBox();
    private final JCheckBox erector = new JCheckBox();
    private final JDialog printTypes;
    private final Font titleFont = new Font("Dialog", Font.BOLD, 34);
    private final Font subHeading = new Font("Dialog", Font.ITALIC, 12);
    private final Font largeBasicFont = new Font("Dialog", Font.PLAIN, 10);
    private final Font basicFont = new Font("Dialog", Font.PLAIN, 8);
    DecimalFormat df = new DecimalFormat("#.###");
    private String[][] costs = new String[0][];
    @SuppressWarnings("FieldCanBeLocal")
    private boolean multiPrint = false;
    // Logo size multiplier
    private String title = "TITLE";
    private ArrayList<String[]> printDetails = new ArrayList<>();
    private String[] printColumns = new String[]{};
    private int description = -1;
    private int sumTable = -1;
    private String sumName = "";
    private Integer[] sPos;
    private double[] widthSize;
    // Stores current page length
    private double cSize = 0;
    private double cEnd = 0;
    private boolean gotContract = false;
    private FullContract fc = new FullContract();
    private PrinterJob job;
    private String sumType = "";

    public Printer(JFrame frame) {
        // Stores logo for print sheet

        printTypes = new JDialog(frame);

        engineersCopy.setText("Engineers copy");
        bends.setText("Bends");
        stockItems.setText("Purchase");
        skilled.setText("Skilled");
        plasma.setText("Plasma");
        pipes.setText("Pipes");
        loading.setText("Loading");
        erector.setText("Erector");
        engineersCopy.setHorizontalTextPosition(SwingConstants.LEFT);
        engineersCopy.setFocusPainted(false);
        engineersCopy.setBorderPainted(true);
        engineersCopy.setHorizontalAlignment(SwingConstants.CENTER);
        bends.setHorizontalTextPosition(SwingConstants.LEFT);
        bends.setFocusPainted(false);
        bends.setBorderPainted(true);
        bends.setHorizontalAlignment(SwingConstants.CENTER);
        stockItems.setHorizontalTextPosition(SwingConstants.LEFT);
        stockItems.setFocusPainted(false);
        stockItems.setBorderPainted(true);
        stockItems.setHorizontalAlignment(SwingConstants.CENTER);
        skilled.setHorizontalTextPosition(SwingConstants.LEFT);
        skilled.setFocusPainted(false);
        skilled.setBorderPainted(true);
        skilled.setHorizontalAlignment(SwingConstants.CENTER);
        plasma.setHorizontalTextPosition(SwingConstants.LEFT);
        plasma.setFocusPainted(false);
        plasma.setBorderPainted(true);
        plasma.setHorizontalAlignment(SwingConstants.CENTER);
        pipes.setHorizontalTextPosition(SwingConstants.LEFT);
        pipes.setFocusPainted(false);
        pipes.setBorderPainted(true);
        pipes.setHorizontalAlignment(SwingConstants.CENTER);
        loading.setHorizontalTextPosition(SwingConstants.LEFT);
        loading.setFocusPainted(false);
        loading.setBorderPainted(true);
        loading.setHorizontalAlignment(SwingConstants.CENTER);
        erector.setHorizontalTextPosition(SwingConstants.LEFT);
        erector.setFocusPainted(false);
        erector.setBorderPainted(true);
        erector.setHorizontalAlignment(SwingConstants.CENTER);

        JButton confirmPrintButton = new JButton("Confirm");
        confirmPrintButton.addActionListener(this);
        confirmPrintButton.setActionCommand("PrintCommand");

        printTypes.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = 0;
        c.gridx = 0;
        printTypes.add(engineersCopy, c);
        c.gridy = 0;
        c.gridx = 1;
        printTypes.add(bends, c);
        c.gridy = 1;
        c.gridx = 0;
        printTypes.add(stockItems, c);
        c.gridy = 1;
        c.gridx = 1;
        printTypes.add(skilled, c);
        c.gridy = 2;
        c.gridx = 0;
        printTypes.add(plasma, c);
        c.gridy = 2;
        c.gridx = 1;
        printTypes.add(pipes, c);
        c.gridy = 3;
        c.gridx = 0;
        printTypes.add(loading, c);
        c.gridy = 3;
        c.gridx = 1;
        printTypes.add(erector, c);
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.weighty = 0.5;
        c.gridy = 4;
        c.gridx = 0;
        c.gridwidth = 2;
        printTypes.add(confirmPrintButton, c);
        //200, 175
        printTypes.setSize(300, 175);
        printTypes.setLocationRelativeTo(null);

    }

    public boolean gotContract() {
        return gotContract;
    }

    public void updateContract(FullContract fc) {
        this.fc = fc;
        gotContract = true;
        printTypes.setTitle("Contract " + fc.fullContractID());
    }

    public int print(Graphics g, PageFormat pf, int pi) {

        Graphics2D g2 = (Graphics2D) g;


        setupPage(g2, pf, pi);
        if (pi == 0) calcPages(g, pf); // calcPages uses values declared inside setupPage

        if (pi == sPos.length - 1) {
            description = -1;
            sumTable = -1;
            sumType = "";
            return 1;
        }// Final empty page resets all values

        for (int i = sPos[pi];i < sPos[pi + 1];i++) {
            if (printDetails.get(i).length == 1) {// If it is one long it is a subHeading
                if (i != 0) {
                    int costPosition = 0;
                    for (int q = 0;q < costs.length;q++) {
                        Log.logLine(i);
                        if (costs[q][1].equals("" + i)) {
                            costPosition = q;
                            break;
                        }
                    }
                    if (sumTable > 0) {
                        g2.setFont(basicFont);
                        double width = (1 - widthSize[sumTable]) * pf.getImageableWidth();
                        if (sumTable < widthSize.length - 1)
                            width = (widthSize[sumTable + 1] - widthSize[sumTable]) * pf.getImageableWidth();
                        g2.setColor(new Color(255, 255, 255));
                        g2.fillRect((int) (widthSize[sumTable] * pf.getImageableWidth()) -
                                    (g2.getFontMetrics().stringWidth(sumName) + 5), (int) cSize,
                                (int) (width + (g2.getFontMetrics().stringWidth(sumName) + 5)),
                                g2.getFontMetrics().getHeight() * 2);
                        g2.setColor(new Color(0, 0, 0, 255));
                        g2.drawRect((int) (widthSize[sumTable] * pf.getImageableWidth()), (int) cSize - 5, (int) width,
                                g2.getFontMetrics().getHeight() * 2);
                        g2.drawString(sumName, (int) (widthSize[sumTable] * pf.getImageableWidth()) -
                                               (g2.getFontMetrics().stringWidth(sumName) + 5),
                                (int) (cSize - 5 + (g2.getFontMetrics().getHeight() * 1.5)));
                        g2.drawString(costs[costPosition][0], (int) (widthSize[sumTable] * pf.getImageableWidth() + 5),
                                (int) (cSize - 5 + (g2.getFontMetrics().getHeight() * 1.5)));
                    }
                }
                g2.setFont(subHeading);
                g2.setColor(new Color(0, 0, 0));
                g2.drawString(printDetails.get(i)[0], (int) (pf.getImageableWidth() * widthSize[1]),
                        (int) (cSize + lineBuffer));

                cSize += (lineBuffer * 2);
                cSize +=
                        Math.max(g.getFontMetrics(subHeading).getHeight(), g.getFontMetrics(basicFont).getHeight() * 2);
            } else {// Not a subheading
                g2.setFont(basicFont);
                for (int j = 0;j < printDetails.get(i).length;j++) {
                    if (!printDetails.get(i)[j].contains("##")) {// !(Marks a new line)
                        if (printColumns[j * 2].equals("Quantity")) g2.drawString(printDetails.get(i)[j],
                                (int) ((pf.getImageableWidth() * widthSize[j + 1]) -
                                       (10 + (g2.getFontMetrics().stringWidth(printDetails.get(i)[j])))),
                                (int) cSize);// Places quant next
                            // to bar
                        else if (printColumns[j * 2].toUpperCase().contains("TIME"))
                            g2.drawString(fixTime(printDetails.get(i)[j]),
                                    (int) (pf.getImageableWidth() * widthSize[j]), (int) cSize);
                        else g2.drawString(printDetails.get(i)[j], (int) (pf.getImageableWidth() * widthSize[j]),
                                    (int) cSize);
                    }
                }
                for (int j = 0;j < printDetails.get(i).length;j++) {
                    if (printDetails.get(i)[j].contains("##")) { // Places a new line in every position that
                        // contains the key ##
                        String[] print = printDetails.get(i)[j].split("##");
                        for (String s : print) {
                            g2.drawString(s, (int) (pf.getImageableWidth() * widthSize[j]), (int) cSize);
                            cSize += closeLineBuffer + g2.getFontMetrics().getHeight();
                        }
                        cSize -= closeLineBuffer + g2.getFontMetrics().getHeight();// Adds one to many as final line
                        // will be added and be blank, so can move back one
                    }
                }
                cSize += lineBuffer + g2.getFontMetrics().getHeight();
            }

            if (i == printDetails.size() - 1) {
                // Final item
                if (sumTable != -1) {

                    double width;
                    if (costs.length >= 2) {
                        g2.setFont(basicFont);
                        width = (1 - widthSize[sumTable]) * pf.getImageableWidth();
                        if (sumTable < widthSize.length - 1)
                            width = (widthSize[sumTable + 1] - widthSize[sumTable]) * pf.getImageableWidth();
                        g2.setColor(new Color(255, 255, 255));
                        g2.fillRect((int) (widthSize[sumTable] * pf.getImageableWidth()) -
                                    (g2.getFontMetrics().stringWidth(sumName) + 5), (int) cSize,
                                (int) (width + (g2.getFontMetrics().stringWidth(sumName) + 5)),
                                g2.getFontMetrics().getHeight() * 2);
                        g2.setColor(new Color(0, 0, 0, 255));
                        g2.drawRect((int) (widthSize[sumTable] * pf.getImageableWidth()), (int) cSize - 5, (int) width,
                                g2.getFontMetrics().getHeight() * 2);
                        g2.drawString(sumName, (int) (widthSize[sumTable] * pf.getImageableWidth()) -
                                               (g2.getFontMetrics().stringWidth(sumName) + 5),
                                (int) (cSize - 5 + (g2.getFontMetrics().getHeight() * 1.5)));
                        g2.drawString(costs[costs.length - 2][0],
                                (int) (widthSize[sumTable] * pf.getImageableWidth() + 5),
                                (int) (cSize - 5 + (g2.getFontMetrics().getHeight() * 1.5)));

                        cSize += (lineBuffer * 2);
                        cSize += Math.max(g.getFontMetrics(subHeading).getHeight(),
                                g.getFontMetrics(basicFont).getHeight() * 2);
                    }

                    g2.setFont(basicFont);
                    if (costs[costs.length - 1] != null) {
                        width = (1 - widthSize[sumTable]) * pf.getImageableWidth();
                        if (sumTable < widthSize.length - 1)
                            width = (widthSize[sumTable + 1] - widthSize[sumTable]) * pf.getImageableWidth();
                        g2.setColor(new Color(255, 255, 255));
                        g2.fillRect((int) (widthSize[sumTable] * pf.getImageableWidth()) -
                                    (g2.getFontMetrics().stringWidth(sumName) + 5), (int) cSize,
                                (int) (width + (g2.getFontMetrics().stringWidth(sumName) + 5)),
                                g2.getFontMetrics().getHeight() * 2);
                        g2.setColor(new Color(0, 0, 0, 255));
                        g2.drawRect((int) (widthSize[sumTable] * pf.getImageableWidth()), (int) cSize - 5, (int) width,
                                g2.getFontMetrics().getHeight() * 2);
                        g2.drawString("Total " + sumName, (int) (widthSize[sumTable] * pf.getImageableWidth()) -
                                                          (g2.getFontMetrics().stringWidth("Total " + sumName) + 5),
                                (int) (cSize - 5 + (g2.getFontMetrics().getHeight() * 1.5)));
                        g2.drawString(costs[costs.length - 1][0],
                                (int) (widthSize[sumTable] * pf.getImageableWidth() + 5),
                                (int) (cSize - 5 + (g2.getFontMetrics().getHeight() * 1.5)));
                    }

                }

            }

        }

        return 0;
    }

    private void calcPages(Graphics g, PageFormat pf) {
        ArrayList<Integer> sPos = new ArrayList<>(2);
        int size = 0;
        int headingCount = 0;
        sPos.add(size);
        double imagableWidth = (pf.getImageableWidth() * (1 - widthSize[description]));
        if (description < widthSize.length - 1)
            imagableWidth = pf.getImageableWidth() * (widthSize[description + 1] - widthSize[description]);
        for (int i = 0;i < printDetails.size();i++) {
            if (printDetails.get(i).length == 1) {
                headingCount++;
                size += (lineBuffer * 2);
                size += Math.max(g.getFontMetrics(subHeading).getHeight(), g.getFontMetrics(basicFont).getHeight() * 2);
            } else if (description == -1) size += lineBuffer + g.getFontMetrics(basicFont).getHeight();
            else if (g.getFontMetrics(basicFont).stringWidth(printDetails.get(i)[description]) > imagableWidth) {
                String[] sentance = printDetails.get(i)[description].replaceAll("##", "").split(" ");
                StringBuilder splitString = new StringBuilder();
                String row = sentance[0];
                for (int j = 1;j < sentance.length;j++) {
                    row += " " + sentance[j];
                    if (g.getFontMetrics(basicFont).stringWidth(row) + 15 > imagableWidth) {
                        row = row.substring(0, row.length() - sentance[j].length());
                        j--;
                        splitString.append(" ").append(row).append("##");
                        row = "";
                        size += g.getFontMetrics(basicFont).getHeight() + closeLineBuffer;
                    }
                }
                splitString.append(" ").append(row);
                printDetails.get(i)[description] = splitString.toString().trim();
                size += g.getFontMetrics(basicFont).getHeight() + lineBuffer;
            } else {
                size += g.getFontMetrics(basicFont).getHeight() + lineBuffer;
            }
            if (size + cSize + cEnd > pf.getImageableHeight()) {
                sPos.add(i);
                size = 0;
            }
        }
        size += g.getFontMetrics(basicFont).getHeight() + 10;
        if (size + cSize + cEnd > pf.getImageableHeight()) {
            sPos.add(sPos.get(sPos.size() - 1) + 1);
        }
        sPos.add(printDetails.size());
        this.sPos = sPos.toArray(new Integer[]{});
        costs = new String[headingCount + 1][];
        int costCounter = -1;
        BigDecimal summnation = new BigDecimal("0");
        for (int i = 0;i < printDetails.size();i++) {
            if (printDetails.get(i).length == 1) {
                if (costCounter >= 0) {
                    costs[costCounter] = new String[]{"", "" + i};
                    if (sumType.equals("MONEY")) costs[costCounter][0] = fixCost(summnation.toString());
                    else costs[costCounter][0] = summnation.toString();
                    summnation = BigDecimal.ZERO;
                }
                costCounter++;
            } else if (sumTable > -1)
                if (isNumeric(printDetails.get(i)[sumTable].replaceAll("£", "").replaceAll(",", ""))) summnation =
                        summnation.add(getIfNumeric(
                                printDetails.get(i)[sumTable].replaceAll("£", "").replaceAll(",", "")));
        }
        if (costCounter == -1) costCounter++;
        if (costCounter >= 0) {
            costs[costCounter] = new String[]{"", ""};
            if (sumType.equals("MONEY")) costs[costCounter][0] = fixCost(summnation.toString());
            else costs[costCounter][0] = summnation.toString();
            summnation = BigDecimal.ZERO;
            costCounter++;
            for (int i = 0;i < costs.length - 1;i++) {
                summnation = summnation.add(new BigDecimal(costs[i][0].replaceAll("£", "").replaceAll(",", "").trim()));
            }
            if (costCounter != 1) {
                costs[costCounter] = new String[]{""};
                costs[costCounter][0] = fixCost(summnation.toString());
            }
        }
    }

    private String[][] getSectionDetailsEngi(int sectionHeading) {
        ArrayList<String[]> returnValue = new ArrayList<>(0);

        String cost;
        for (HeadingLine contractHeadingLine : fc.contractHeadingLine) {
            if (sectionHeading == contractHeadingLine.headingID) {
                // Adds all products to the list
                if (contractHeadingLine.productID.equals("QP") || contractHeadingLine.productID.equals("QL")) {
                    for (Qproduct qproduct : fc.qProducts)
                        if (contractHeadingLine.headingLineID == qproduct.headingLineID) {

                            if (qproduct.cost.doubleValue() != 0)
                                cost = fixCost("" + (qproduct.cost.doubleValue() * contractHeadingLine.quantity));
                            else cost = "£--";


                            returnValue.add(new String[]{
                                    "" + contractHeadingLine.quantity, contractHeadingLine.comment, "-", cost});
                        }
                } else for (Product products : fc.products)
                    if (contractHeadingLine.productID.equalsIgnoreCase(products.getProductID())) {
                        if (products.getSalesCost().doubleValue() != 0)
                            cost = "£" + (products.getSalesCost().doubleValue() * contractHeadingLine.quantity);
                        else cost = "£--";

                        cost = fixCost(cost);

                        returnValue.add(new String[]{
                                "" + contractHeadingLine.quantity, getDesc(products, contractHeadingLine), df.format(
                                products.getMakeTime() * contractHeadingLine.quantity), cost});
                        break;

                    }

            }
        }


        return returnValue.toArray(new String[0][]);
    }

    private String[][] getErectorDetails(int headingNumber) {

        ArrayList<String[]> returnValues = new ArrayList<>(0);

        for (HeadingLine contractHeadingLine : fc.contractHeadingLine)
            if (headingNumber == contractHeadingLine.headingID) for (Product products : fc.products)
                if (contractHeadingLine.productID.equalsIgnoreCase(products.getProductID())) {
                    returnValues.add(new String[]{
                            "" + contractHeadingLine.quantity, getDesc(products, contractHeadingLine)});
                    break;

                }


        return returnValues.toArray(new String[0][]);

    }

    private String fixTime(String x) {
        if (Convert.getIfNumeric(x) > 0) {
            return x;
        } else return "-";
    }

    private void setupPage(Graphics2D g2, PageFormat pf, int pi) {
        cSize = 45;

        g2.translate(pf.getImageableX(), pf.getImageableY());

        String longestEntry = "Contract Number: " + fc.fullContractID();
        if (longestEntry.length() < ("Date of Contract: " + fc.details.contractDate).length())
            longestEntry = "Date of Contract: " + fc.details.contractDate;
        if (longestEntry.length() < ("Date of delivery: " + fc.details.deliveryDate).length())
            longestEntry = "Date of delivery: " + fc.details.deliveryDate;
        if (longestEntry.length() < ("Delivery Method: " + fc.details.deliveryMethod).length())
            longestEntry = "Delivery Method: " + fc.details.deliveryMethod;

        g2.setFont(titleFont);

        String longTitle = title.toUpperCase();
        Font font = titleFont;
        if (g2.getFontMetrics().stringWidth(longTitle) <
            g2.getFontMetrics(largeBasicFont).stringWidth("Air Plants Dust Extraction")) {
            longTitle = "Air Plants Dust Extraction";
            font = largeBasicFont;
        }

        g2.drawString(title.toUpperCase(), (int) (pf.getImageableWidth() -
                                                  (g2.getFontMetrics(largeBasicFont).stringWidth(longestEntry) +
                                                   g2.getFontMetrics(font).stringWidth(longTitle)) - 40),
                40 + g2.getFontMetrics().getHeight());

        g2.setFont(largeBasicFont);

        g2.drawString("Air Plants Dust Extraction", (int) (pf.getImageableWidth() - (g2.getFontMetrics(largeBasicFont)
                                                                                             .stringWidth(
                                                                                                     longestEntry) +
                                                                                     g2.getFontMetrics(font)
                                                                                             .stringWidth(longTitle)) -
                                                           40),
                40 + g2.getFontMetrics().getHeight() + g2.getFontMetrics(titleFont).getHeight());


        g2.setFont(subHeading);
        String t = "NOT ISSUED";
        if (fc.details.quote) t = "QUOTATION";
        if (fc.details.issued) t = "ISSUED";
        g2.drawString(t, (int) (pf.getImageableWidth() - (g2.getFontMetrics(largeBasicFont).stringWidth(longestEntry) +
                                                          g2.getFontMetrics(font).stringWidth(longTitle)) - 40), 38 +
                                                                                                                 (g2.getFontMetrics(
                                                                                                                         titleFont)
                                                                                                                          .getHeight() +
                                                                                                                  g2.getFontMetrics(
                                                                                                                          largeBasicFont)
                                                                                                                          .getHeight() +
                                                                                                                  g2.getFontMetrics()
                                                                                                                          .getHeight()));
        g2.setFont(largeBasicFont);
        if (fc.details.engineer != null) g2.drawString("Engineer:   " + fc.details.engineer,
                (int) (pf.getImageableWidth() - (g2.getFontMetrics(largeBasicFont).stringWidth(longestEntry) +
                                                 g2.getFontMetrics(font).stringWidth(longTitle)) - 40), 38 +
                                                                                                        (g2.getFontMetrics(
                                                                                                                titleFont)
                                                                                                                 .getHeight() +
                                                                                                         g2.getFontMetrics(
                                                                                                                 largeBasicFont)
                                                                                                                 .getHeight() +
                                                                                                         g2.getFontMetrics(
                                                                                                                 subHeading)
                                                                                                                 .getHeight() +
                                                                                                         g2.getFontMetrics()
                                                                                                                 .getHeight()));
        if (fc.details.contractor != null) g2.drawString("Contractor: " + fc.details.contractor,
                (int) (pf.getImageableWidth() - (g2.getFontMetrics(largeBasicFont).stringWidth(longestEntry) +
                                                 g2.getFontMetrics(font).stringWidth(longTitle)) - 40), 38 +
                                                                                                        (g2.getFontMetrics(
                                                                                                                titleFont)
                                                                                                                 .getHeight() +
                                                                                                         g2.getFontMetrics(
                                                                                                                 largeBasicFont)
                                                                                                                 .getHeight() +
                                                                                                         g2.getFontMetrics(
                                                                                                                 subHeading)
                                                                                                                 .getHeight() +
                                                                                                         (2 *
                                                                                                          g2.getFontMetrics()
                                                                                                                  .getHeight())));


        g2.drawString("Contract Number: " + fc.fullContractID(),
                (int) pf.getImageableWidth() - 5 - g2.getFontMetrics().stringWidth(longestEntry),
                (int) cSize + 5 + g2.getFontMetrics().getHeight());

        cSize += 5 + g2.getFontMetrics().getHeight();

        g2.drawString(
                "Date of Contract: " + fc.details.contractDate.format(DateTimeFormatter.ofPattern("dd-MM" + "-yyyy")),
                (int) pf.getImageableWidth() - 5 - g2.getFontMetrics().stringWidth(longestEntry),
                (int) cSize + 5 + g2.getFontMetrics().getHeight());

        cSize += 5 + g2.getFontMetrics().getHeight();

        if (fc.details.issued) {
            g2.drawString("Date of delivery: " +
                          fc.details.deliveryDate.format(DateTimeFormatter.ofPattern("dd-MM" + "-yyyy")),
                    (int) pf.getImageableWidth() - 5 - g2.getFontMetrics().stringWidth(longestEntry),
                    (int) cSize + 5 + g2.getFontMetrics().getHeight());

            cSize += 5 + g2.getFontMetrics().getHeight();


            g2.drawString("Delivery Method: " + fc.details.deliveryMethod,
                    (int) pf.getImageableWidth() - 5 - g2.getFontMetrics().stringWidth(longestEntry),
                    (int) cSize + 5 + g2.getFontMetrics().getHeight());
        }
        cSize -= (5 + g2.getFontMetrics().getHeight()) * 2;

        g2.drawString(fc.details.companyName, 25, (int) cSize);
        cSize += 5 + g2.getFontMetrics().getHeight();
        g2.drawString(fc.details.address1, 25, (int) cSize);
        cSize += 5 + g2.getFontMetrics().getHeight();
        g2.drawString(fc.details.address2, 25, (int) cSize);
        cSize += 5 + g2.getFontMetrics().getHeight();
        g2.drawString(fc.details.address3, 25, (int) cSize);
        cSize += 5 + g2.getFontMetrics().getHeight();
        g2.drawString(fc.details.postcode, 25, (int) cSize);
        cSize += 5 + g2.getFontMetrics().getHeight();

        cSize += 15 + g2.getFontMetrics().getHeight() * 2;
        int cWidth = printColumns.length / 2;
        widthSize = new double[cWidth];

        for (int k = 0;k < widthSize.length;k++) {
            widthSize[k] = getIfNumeric(printColumns[(k * 2) + 1]).doubleValue();
            g2.setColor(new Color(0, 0, 0));
            if (k != 0)
                g2.drawString(printColumns[(k * 2)], (int) (pf.getImageableWidth() * widthSize[k]), (int) cSize);
            else g2.drawString(printColumns[0],
                    (int) (pf.getImageableWidth() * getIfNumeric(printColumns[((k + 1) * 2) + 1]).doubleValue()) - 10 -
                    g2.getFontMetrics().stringWidth(printColumns[0]), (int) cSize);
            g2.setColor(new Color(187, 187, 187));
            if (k != 0) g2.fillRect(((int) (pf.getImageableWidth() * widthSize[k]) - 5), (int) (cSize + 2), 1,
                    (int) (pf.getImageableHeight() - (cSize + 2)) - 30);
        }
        cSize += 2;
        g2.setColor(new Color(0, 0, 0));

        g2.fillRect(5, (int) cSize, (int) pf.getImageableWidth() - 10, 4);
        cSize += 6 + g2.getFontMetrics().getHeight();
        cEnd = 35;

        g2.fillRect(5, (int) (pf.getImageableHeight() - 30), (int) pf.getImageableWidth() - 10, 4);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm - dd-MM-yyy");
        LocalDateTime now = LocalDateTime.now();
        g2.setFont(basicFont);

        g2.drawString(dtf.format(now), 10, (int) pf.getImageableHeight() - 5);
        if (sPos != null) {

            g2.drawString("page" + (pi + 1) + " of " + (sPos.length - 1), (int) pf.getImageableWidth() - 10 -
                                                                          g2.getFontMetrics().stringWidth(
                                                                                  "page" + (pi + 1) + " of " +
                                                                                  (sPos.length - 1)),
                    (int) (pf.getImageableHeight() - 5));
        }


    }

    public void prepEngies() {
        description = 1;
        title = "Engineer";
        sumTable = 3;
        sumName = "Cost";
        printDetails.clear();
        printColumns = new String[]{"Quantity", "0.05", "Description", "0.2", "Make time", "0.75", "Cost", "0.9"};
        for (int i = 0;i < fc.contractHeadings.size();i++) {
            printDetails.add(new String[]{fc.contractHeadings.get(i).headingTitle});
            String[][] section = getSectionDetailsEngi(fc.contractHeadings.get(i).headingID);
            if (section.length == 0) printDetails.remove(printDetails.size() - 1);
            else printDetails.addAll(Arrays.asList(section));
        }
        sumType = "MONEY";

    }

    public void prepBends() {
        this.description = 1;
        title = "BENDS";
        printDetails.clear();
        printColumns = new String[]{"Quantity", "0.05", "Description", "0.2", "Make time", "0.75"};
        for (ContractHeading ch : fc.contractHeadings)
            for (HeadingLine chl : fc.contractHeadingLine)
                if (ch.headingID == chl.headingID) for (Product pr : fc.products)
                    if (pr.getProductID().equalsIgnoreCase(chl.productID))
                        if (pr.getProductType().equalsIgnoreCase("bends")) {
                            printDetails.add(new String[]{
                                    "" + chl.quantity, getDesc(pr, chl), df.format(pr.getMakeTime() * chl.quantity)});
                            break;
                        }
        printDetails = sort(1, printDetails);
        printDetails = sumDupes(printDetails, new int[]{1}, new int[]{0, 2});
        sumTable = 2;
        sumName = "Make time";
    }

    public void prepStockItems() {
        description = 1;
        title = "PURCHASE";
        printDetails.clear();
        printColumns = new String[]{"Quantity", "0.05", "Description", "0.2"};
        for (HeadingLine chl : fc.contractHeadingLine)
            for (Product pr : fc.products) {
                if (pr.getStock() && chl.productID.equalsIgnoreCase(pr.getProductID())) {
                    printDetails.add(new String[]{"" + chl.quantity, getDesc(pr, chl)});
                    break;
                }
            }
        sumTable = -1;
        printDetails = sort(1, printDetails);
        printDetails = sumDupes(printDetails, new int[]{1}, new int[]{0});
    }

    public void prepSkilled() {
        description = 1;
        title = "SKILLED";
        printDetails.clear();
        printColumns = new String[]{"Quantity", "0.05", "Description", "0.2", "Make Time", "0.75"};
        for (ContractHeading ch : fc.contractHeadings) {
            for (HeadingLine chl : fc.contractHeadingLine) {
                if (ch.headingID == chl.headingID) {
                    for (Product pr : fc.products) {
                        if (chl.productID.equalsIgnoreCase(pr.getProductID())) {
                            if (pr.getSkilled()) {
                                printDetails.add(new String[]{"" + chl.quantity, getDesc(pr, chl), df.format(
                                        pr.getMakeTime() * chl.quantity)});
                                break;
                            }
                        }
                    }
                }
            }
        }
        sumName = "Make Time:";
        sumTable = 2;
        printDetails = sort(1, printDetails);
        printDetails = sumDupes(printDetails, new int[]{1}, new int[]{0, 2});
    }

    public void prepPlasma() {
        printDetails.clear();
        title = "PLASMA";
        description = 1;
        printColumns = new String[]{"Quantity", "0.05", "Description", "0.2"};
        for (HeadingLine hl : fc.contractHeadingLine)
            for (Product pr : fc.products)
                if (pr.getProductID().equalsIgnoreCase(hl.productID)) {
                    if (pr.getProductType().equalsIgnoreCase("SKILLED"))
                        printDetails.add(new String[]{hl.quantity + "", getDesc(pr, hl)});
                    break;
                }
        sumTable = -1;
        printDetails = sort(1, printDetails);
        printDetails = sumDupes(printDetails, new int[]{1}, new int[]{0});
    }

    public void prepPipes() {
        description = 1;
        printDetails.clear();
        title = "PIPES";
        printColumns = new String[]{"Quantity", "0.05", "Description", "0.2", "Make Time", "0.75"};
        sumName = "Make Time";
        for (HeadingLine hl : fc.contractHeadingLine)
            for (Product pr : fc.products)
                if (hl.productID.equalsIgnoreCase(pr.getProductID())) {
                    if (pr.getProductType().equalsIgnoreCase("pipes")) {
                        printDetails.add(new String[]{
                                hl.quantity + "", getDesc(pr, hl), df.format(pr.getMakeTime() * hl.quantity)});
                        break;
                    }
                }
        sumTable = 2;
        printDetails = sort(1, printDetails);
        printDetails = sumDupes(printDetails, new int[]{1}, new int[]{0, 2});

    }

    public void prepLoading() {
        description = 1;
        printDetails.clear();
        title = "LOADING";
        printColumns = new String[]{"Quantity", "0.05", "Description", "0.2"};
        int items = 0;
        ArrayList<String> cats = new ArrayList<>(0);
        String cCat = "";
        while (items < fc.products.size()) {
            for (HeadingLine chl : fc.contractHeadingLine) {
                for (Product pr : fc.products) {
                    if (chl.productID.equalsIgnoreCase(pr.getProductID())) {
                        if (cCat.equals("") && !cats.contains(pr.getProductType())) {
                            cCat = pr.getProductType();
                            cats.add(pr.getProductType());
                            if (!pr.getProductType().isEmpty() && !pr.getProductType().equalsIgnoreCase("NULL"))
                                printDetails.add(new String[]{pr.getProductType()});

                        }
                        if (cCat.equals(pr.getProductType())) {
                            if (!pr.getProductType().isEmpty() && !pr.getProductType().equalsIgnoreCase("NULL"))
                                printDetails.add(new String[]{"" + chl.quantity, getDesc(pr, chl)});
                            items++;
                        }
                        break;
                    }
                }
            }
            cCat = "";
        }
        sumTable = -1;
        printDetails = sort(1, printDetails);
        printDetails = sumDupes(printDetails, new int[]{1}, new int[]{0});
    }

    public void prepErector() {
        description = 1;
        title = "ERECTOR";
        printDetails.clear();
        printColumns = new String[]{"Quantity", "0.05", "Description", "0.2"};
        for (int i = 0;i < fc.contractHeadings.size();i++) {
            printDetails.add(new String[]{fc.contractHeadings.get(i).headingTitle});
            String[][] section = getErectorDetails(fc.contractHeadings.get(i).headingID);
            if (section.length == 0) printDetails.remove(printDetails.size() - 1);
            else Collections.addAll(printDetails, section);
        }
        sumTable = -1;
    }

    public ArrayList<String[]> sumDupes(ArrayList<String[]> entry, int[] checks, int[] sums) {
        ArrayList<String[]> finalString = new ArrayList<>(10);
        int big = 0;
        for (int i : checks)
            if (big < i) big = i;
        for (int i : sums)
            if (big < i) big = i;
        {
            String[] t = new String[big];
            Arrays.fill(t, "");
            entry.add(t);
        }

        int sPos = -1;

        for (int i = 0;i < entry.size();i++)
            if (i == 0 || entry.get(i).length == 1 || i == entry.size() - 1) {
                if (sPos != -1) {
                    finalString.add(entry.get(sPos));
                    String[][] values = _sumDupes(entry.subList(sPos + 1, i), checks, sums);
                    Collections.addAll(finalString, values);
                }
                sPos = i;
            }
        return finalString;
    }

    public String[][] _sumDupes(List<String[]> values, int[] checks, int[] sums) {
        ArrayList<String[]> returnValues = new ArrayList<>(10);

        boolean found;
        for (String[] value : values) {
            found = false;
            for (String[] check : returnValues) {
                found = true;
                for (int checkPos : checks) {
                    if (!check[checkPos].equals(value[checkPos]) ||
                        (check[checkPos].contains("Meters of") && !check[checkPos].contains("Mtr Coil"))) {
                        found = false;
                        break;
                    }
                }

                if (!found) continue;

                for (int sumPos : sums) {
                    check[sumPos] = "" + getIfNumeric(check[sumPos]).add(getIfNumeric(value[sumPos])).intValue();
                }
                break;
            }
            if (!found) {
                returnValues.add(value);
            }
        }

        return returnValues.toArray(new String[][]{});
    }

    // Checks if its double with error catching
    private boolean isNumeric(String x) {
        if (x == null) {
            return false;
        }
        try {
            Double.parseDouble(x.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Checks if it is double with error checking and also returns the double
    private BigDecimal getIfNumeric(String x) {
        x = x.trim();
        if (!x.contains(".")) x += ".00";
        if (isNumeric(x)) {
            return new BigDecimal(x);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("PrintCommand".equals(command)) {
            try {
                if (engineersCopy.isSelected() || bends.isSelected() || stockItems.isSelected() ||
                    skilled.isSelected() || plasma.isSelected() || pipes.isSelected() || loading.isSelected() ||
                    erector.isSelected()) {
                    multiPrint = false;
                    if (engineersCopy.isSelected()) {
                        prepEngies();
                        if (!multiPrint) {
                            if (job.printDialog()) {
                                multiPrint = true;
                                job.print();
                            }
                        } else job.print();
                    }
                    if (bends.isSelected()) {
                        prepBends();
                        if (!multiPrint) {
                            if (job.printDialog()) {
                                multiPrint = true;
                                job.print();
                            }
                        } else job.print();
                    }
                    if (stockItems.isSelected()) {
                        prepStockItems();
                        if (!multiPrint) {
                            if (job.printDialog()) {
                                multiPrint = true;
                                job.print();
                            }
                        } else job.print();
                    }
                    if (skilled.isSelected()) {
                        prepSkilled();
                        if (!multiPrint) {
                            if (job.printDialog()) {
                                multiPrint = true;
                                job.print();
                            }
                        } else job.print();
                    }
                    if (plasma.isSelected()) {
                        prepPlasma();
                        if (!multiPrint) {
                            if (job.printDialog()) {
                                multiPrint = true;
                                job.print();
                            }
                        } else job.print();
                    }
                    if (pipes.isSelected()) {
                        prepPipes();
                        if (!multiPrint) {
                            if (job.printDialog()) {
                                multiPrint = true;
                                job.print();
                            }
                        } else job.print();
                    }
                    if (loading.isSelected()) {
                        prepLoading();
                        if (!multiPrint) {
                            if (job.printDialog()) {
                                multiPrint = true;
                                job.print();
                            }
                        } else job.print();
                    }
                    if (erector.isSelected()) {
                        prepErector();
                        if (!multiPrint) {
                            if (job.printDialog()) {
                                multiPrint = true;
                                job.print();
                            }
                        } else job.print();
                    }
                }
                printTypes.setVisible(false);
            } catch (PrinterException printerException) {
                printerException.printStackTrace();
            }
        }

    }

    private String getDesc(Product pr, HeadingLine chl) {
        String des = "";
        if (!pr.getDescription().equalsIgnoreCase("null")) des = pr.getDescription();
        if (!(chl.comment.equalsIgnoreCase("null") || chl.comment.isEmpty())) {
            if (!des.isEmpty()) des += " - ";
            des += chl.comment;
        }
        return des;
    }

    private String fixCost(String x) {
        x = x.replaceAll("£", "").trim();
        if (x.contains(".")) {
            if (x.split("\\.")[1].length() > 2) {
                x = x.split("\\.")[0] + "." + x.split("\\.")[1].substring(0, 2);
            } else if (x.split("\\.")[1].length() == 1) {
                x += "0";
            } else if (x.split("\\.")[1].length() == 0) {
                x += "00";
            }
        } else {
            x += ".00";
        }

        for (int i = x.length() - 6;i > 0;i -= 3) {
            x = x.substring(0, i) + "," + x.substring(i);
        }

        if (!x.startsWith("£")) return "£ " + x;
        else return x;
    }

    public void printContract(PrinterJob job) {
        this.job = job;
        job.setPrintable(this);
        printTypes.setVisible(true);
    }

    public ArrayList<String[]> sort(int sortColumn, ArrayList<String[]> values) {
        ArrayList<String[]> section = new ArrayList<>();
        ArrayList<String[]> returnValues = new ArrayList<>();
        String title = "";
        for (int i = 0;i < values.size();i++) {
            if (values.get(i).length == 1) {
                if (section.size() > 0) {
                    returnValues.add(new String[]{title});
                    returnValues.addAll(_sort(sortColumn, section));
                }
                title = values.get(i)[0];
                section.clear();
            } else {
                section.add(values.get(i));
            }
        }
        if (section.size() > 0) {
            returnValues.add(new String[]{title});
            returnValues.addAll(_sort(sortColumn, section));
        }
        return returnValues;
    }

    public ArrayList<String[]> _sort(int sortColum, ArrayList<String[]> values) {
        int[] valuePositions = new int[values.size()];
        int numEnd = 0;
        boolean end;
        int pointer = 0;
        for (int i = 0;i < values.size();i++) {
            while (pointer < values.get(i)[sortColum].length() &&
                   Convert.isNumeric("" + values.get(i)[sortColum].charAt(pointer))) pointer++;
            if (pointer == 0) {
                valuePositions[i] = Integer.MAX_VALUE;
            } else {
                valuePositions[i] = (int) Convert.getIfNumeric(values.get(i)[sortColum].substring(0, pointer));
            }
        }
        boolean move = false;
        int l;
        int temp;
        String[] temp2;
        for (int i = 1;i < valuePositions.length;i++) {
            l = i;
            while (valuePositions[l] > valuePositions[l - 1]) {
                temp = valuePositions[l];
                temp2 = values.get(l);
                valuePositions[l] = valuePositions[l - 1];
                values.set(l, values.get(l - 1));
                valuePositions[l - 1] = temp;
                values.set(l - 1, temp2);
                l--;
                if (l == 0) break;
            }
        }
        Log.logLine(values.toArray(String[][]::new));
        return values;
    }


    public void setAllPrints(Boolean x) {
        engineersCopy.setSelected(x);
        bends.setSelected(x);
        stockItems.setSelected(x);
        skilled.setSelected(x);
        plasma.setSelected(x);
        pipes.setSelected(x);
        loading.setSelected(x);
        erector.setSelected(x);
    }

}
