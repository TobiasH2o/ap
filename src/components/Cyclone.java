package components;

import java.math.BigDecimal;

public class Cyclone {

    public String productID;
    public String metal;
    public BigDecimal shaftCost;
    public int sundries;
    public int rings;
    public BigDecimal cost;

    public Cyclone(String productID, String metal, double shaftCost, int sundries, int rings, double cost) {
        this.productID = productID;
        this.metal = metal;
        this.shaftCost = BigDecimal.valueOf(shaftCost);
        this.sundries = sundries;
        this.rings = rings;
        this.cost = BigDecimal.valueOf(cost);
    }

}
