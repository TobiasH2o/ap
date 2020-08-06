package components;

import java.math.BigDecimal;

public class Hood {

    public String productID;
    public int weightKG;
    public BigDecimal cost;

    public Hood(String productID, int weightKG, double cost) {
        this.productID = productID;
        this.weightKG = weightKG;
        this.cost = BigDecimal.valueOf(cost);
    }

}
