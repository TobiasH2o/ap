import components.*;

import java.util.ArrayList;
import java.util.Arrays;

public class FullContract {

    public Contract details = new Contract();
    public ArrayList<ContractHeading> contractHeadings = new ArrayList<>(0);
    public ArrayList<HeadingLine> contractHeadingLine = new ArrayList<>(0);
    public ArrayList<Product> products = new ArrayList<>(0);
    public ArrayList<Qproduct> qProducts = new ArrayList<>(0);
    int headingNumber = 0;
    int headingLineNumber = 0;
    int qID = 0;

    public void setDetails(Contract contract) {
        details = contract;
    }

    public void addQProduct(int headlingLineID, double cost, String type){
        boolean conflict = true;
        do if (qProducts.size() != 0) for (Qproduct q : qProducts)
            if (q.qID == qID) qID++;
            else conflict = false;
        else conflict = false; while (conflict);
        qProducts.add(new Qproduct(qID, headlingLineID, cost, type));
    }

    public String fullContractID(){
        if(details.quote)
            return "Q" + details.contractID;
        return details.contractID;
    }

    public void purge() {
        details = new Contract();
        contractHeadings.clear();
        contractHeadingLine.clear();
        products.clear();
        qProducts.clear();
        headingNumber = 0;
        headingLineNumber = 0;
        qID = 0;
    }

    public void addProduct(Product product) {
        boolean dupe = false;
        for (Product pr : products) {
            if (pr.getProductID().equalsIgnoreCase(product.getProductID())) {
                dupe = true;
                break;
            }
        }
        if (!dupe) {
            products.add(product);
        }
    }

    public void setContractHeadings(ContractHeading[] ch){
        contractHeadings.clear();
        contractHeadings.addAll(Arrays.asList(ch));
    }

    public void setContractHeadingLines(HeadingLine[] chl) {
        contractHeadingLine.clear();
        contractHeadingLine.addAll(Arrays.asList(chl));
    }

    public int addHeading(String headingTitle) {
        boolean conflict = true;
        do if (contractHeadings.size() != 0) for (ContractHeading ch : contractHeadings)
            if (ch.headingID == headingNumber) headingNumber++;
            else conflict = false;
        else conflict = false; while (conflict);
        contractHeadings.add(new ContractHeading(headingNumber, details.contractID, headingTitle));
        return headingNumber;
    }

    public int addHeading(ContractHeading heading) {
        headingNumber = heading.headingID;
        boolean conflict = true;
        do if (contractHeadings.size() != 0) for (ContractHeading ch : contractHeadings)
            if (ch.headingID == headingNumber) headingNumber++;
            else conflict = false;
        else conflict = false; while (conflict);
        contractHeadings.add(new ContractHeading(headingNumber, details.contractID, heading.headingTitle));
        return headingNumber;
    }

    public int addHeadingLine(int headingNumber, String productCode, int quantity, String description) {
        boolean conflict = true;
        do {
            if (contractHeadingLine.size() != 0) {
                for (HeadingLine chl : contractHeadingLine) {
                    if (chl.headingLineID == headingLineNumber) {
                        headingLineNumber++;
                        break;
                    } else {
                        conflict = false;
                    }
                }
            } else {
                conflict = false;
            }
        } while (conflict);
        boolean qProduct = (productCode.contains("QP") || productCode.contains("QL"));
        contractHeadingLine
                .add(new HeadingLine(headingLineNumber, headingNumber, productCode, description, quantity));
        return headingLineNumber;
    }

    public int addHeadingLine(HeadingLine headingLine) {
        boolean conflict = true;
        headingLineNumber = headingLine.headingLineID;
        do {
            if (contractHeadingLine.size() != 0) {
                for (HeadingLine chl : contractHeadingLine) {
                    if (chl.headingLineID == headingLineNumber) {
                        headingLineNumber++;
                        break;
                    } else {
                        conflict = false;
                    }
                }
            } else {
                conflict = false;
            }
        } while (conflict);
        contractHeadingLine
                .add(new HeadingLine(headingLineNumber, headingNumber, headingLine.productID, headingLine.comment,
                                     headingLine.quantity));
        return headingLineNumber;
    }

    public String[][] contractHeadingsAsString() {
        ArrayList<String[]> rv = new ArrayList<>();
        for (ContractHeading ch:
                contractHeadings) {
            rv.add(new String[]{ch.headingID + "", ch.contractID, ch.headingTitle});
        }
        return rv.toArray(new String[0][]);
    }

    public String[][] contractHeadingLineAsString() {
        ArrayList<String[]> rv = new ArrayList<>();
        for (HeadingLine chl : contractHeadingLine) {
            rv.add(new String[]{
                    "" + chl.headingLineID, "" + chl.comment, chl.productID, "" + chl.quantity, chl.comment});
        }
        return rv.toArray(new String[0][]);
    }
}
