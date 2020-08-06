package components;

import java.math.BigDecimal;

public class Impellor {

    public String productID;
    public String hubCodeID;
    public int weightKG;
    public int sundries;
    public BigDecimal cost;

    public Impellor(String productID, String hubCodeID, int weightKG, int sundries, double cost) {
        this.productID = productID;
        this.hubCodeID = hubCodeID;
        this.weightKG = weightKG;
        this.sundries = sundries;
        this.cost = BigDecimal.valueOf(cost);
    }

}
