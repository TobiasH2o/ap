package components;

import java.math.BigDecimal;

public class DustUnit {

    public String productID;
    public int weightKG;
    public String impellorID;
    public int sundries;
    public String motorID;
    public String starterID1;
    public String starterID2;
    public int filterQuant;
    public String filterID;
    public int plasticBagQuant;
    public int bin;
    public String legCode;
    public BigDecimal cost;

    public DustUnit(String productID, int weightKG, String impellorID, int sundries, String motorID, String starterID1,
                    String starterID2, int filterQuant, String filterID, int plasticBagQuant, int bin, String legCode,
                    double cost) {
        this.productID = productID;
        this.weightKG = weightKG;
        this.impellorID = impellorID;
        this.sundries = sundries;
        this.motorID = motorID;
        this.starterID1 = starterID1;
        this.starterID2 = starterID2;
        this.filterQuant = filterQuant;
        this.filterID = filterID;
        this.plasticBagQuant = plasticBagQuant;
        this.bin = bin;
        this.legCode = legCode;
        this.cost = BigDecimal.valueOf(cost);
    }

}
