package components;

import java.math.BigDecimal;

public class SprayBooth {

    public String productID;
    public int fanQuantity;
    public String fanCode;
    public int weightKG;
    public int sundries;
    public BigDecimal cost;

    public SprayBooth(String productID, int fanQuantity, String fanCode, int weightKG, int sundries, double cost) {
        this.productID = productID;
        this.fanQuantity = fanQuantity;
        this.fanCode = fanCode;
        this.weightKG = weightKG;
        this.sundries = sundries;
        this.cost = BigDecimal.valueOf(cost);
    }

}
