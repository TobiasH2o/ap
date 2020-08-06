package components;

import java.math.BigDecimal;

public class RotaryAndTwoWayValves {

    public String productID;
    public int weightKG;
    public int sundries;
    public String shaftID;
    public String rubberID;
    public String gearboxID;
    public String kinetrolID;
    public String bearingID;
    public int bearingQuantity;
    public BigDecimal cost;

    public RotaryAndTwoWayValves(String productID, int weightKG, int sundries, String shaftID, String rubberID,
                                 String gearboxID, String kinetrolID, String bearingID, int bearingQuantity,
                                 double cost) {
        this.productID = productID;
        this.weightKG = weightKG;
        this.sundries = sundries;
        this.shaftID = shaftID;
        this.rubberID = rubberID;
        this.gearboxID = gearboxID;
        this.kinetrolID = kinetrolID;
        this.bearingID = bearingID;
        this.bearingQuantity = bearingQuantity;
        this.cost = BigDecimal.valueOf(cost);
    }

}
