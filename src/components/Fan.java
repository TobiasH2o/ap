package components;

import java.math.BigDecimal;

public class Fan {

    public String productID;
    public int weightKG;
    public String impellorID;
    public String shaftID;
    public String bearingsID;
    public String guardID;
    public String motorID;
    public String veeDriveID;
    public int fitUpBD;
    public int sundries;
    public BigDecimal cost;

    public Fan(String productID, int weightKG, String impellorID, String shaftID, String bearingsID, String guardID,
               String motorID, String veeDriveID, int fitUpBD, int sundries, double cost) {
        this.productID = productID;
        this.weightKG = weightKG;
        this.impellorID = impellorID;
        this.shaftID = shaftID;
        this.bearingsID = bearingsID;
        this.guardID = guardID;
        this.motorID = motorID;
        this.veeDriveID = veeDriveID;
        this.fitUpBD = fitUpBD;
        this.sundries = sundries;
        this.cost = BigDecimal.valueOf(cost);
    }

}
