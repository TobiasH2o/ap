package components;

import java.math.BigDecimal;

public class PulleyBlastGate {

    public String productID;
    public int size;
    public int weightKG;
    public BigDecimal cost;

    public PulleyBlastGate(String productID, int size, int weightKG, double cost) {
        this.productID = productID;
        this.size = size;
        this.weightKG = weightKG;
        this.cost = BigDecimal.valueOf(cost);
    }

}
