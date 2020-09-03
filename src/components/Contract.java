package components;

import java.time.LocalDate;

public class Contract {

    public String contractID;
    public LocalDate contractDate;
    public String companyName;
    public String address1;
    public String address2;
    public String address3;
    public String postcode;
    public String deliveryMethod;
    public LocalDate deliveryDate;
    public String engineer;
    public boolean quote;
    public boolean issued;
    public String contractor;

    public Contract(String contractID, LocalDate contractDate, String companyName, String address1, String address2,
                    String address3, String postcode, String deliveryMethod, LocalDate deliveryDate, boolean quote,
                    String engineer, Boolean issued, String contractor) {
        this.contractID = contractID;
        this.contractDate = contractDate;
        this.companyName = companyName;
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.postcode = postcode;
        this.deliveryMethod = deliveryMethod;
        this.deliveryDate = deliveryDate;
        this.engineer = engineer;
        this.quote = quote;
        this.issued = issued;
        this.contractor = contractor;
    }

    public Contract() {
        this.contractID = "";
        this.contractDate = LocalDate.now();
        this.companyName = "";
        this.address1 = "";
        this.address2 = "";
        this.address3 = "";
        this.postcode = "";
        this.deliveryMethod = "";
        this.deliveryDate = LocalDate.of(1000, 1, 1);
        this.engineer = "OTHER";
        this.quote = false;
        this.issued = false;
        this.contractor = null;
    }

    public void clear() {
        this.contractID = "";
        this.contractDate = LocalDate.now();
        this.companyName = "";
        this.address1 = "";
        this.address2 = "";
        this.address3 = "";
        this.postcode = "";
        this.deliveryMethod = "";
        this.deliveryDate = LocalDate.of(1000, 1, 1);
        this.engineer = "OTHER";
        this.quote = false;
        this.issued = false;
        this.contractor = null;
    }
}
