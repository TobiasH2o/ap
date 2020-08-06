package components;

import java.math.BigDecimal;

public class OandS {

    public String productID;
    public int weightKG;
    public int sundries;
    public BigDecimal cost;

    public OandS(String productID, int weightKG, int sundries, double cost) {
        this.productID = productID;
        this.weightKG = weightKG;
        this.sundries = sundries;
        this.cost = BigDecimal.valueOf(cost);
    }

}
