package components;

import java.math.BigDecimal;

public class ControlBox {

    public String productID;
    public int drawTime;
    public int programmeTime;
    public String broughtOutID1;
    public String broughtOutID2;
    public String broughtOutID3;
    public String broughtOutID4;
    public String broughtOutID5;
    public String broughtOutID6;
    public BigDecimal compCost;
    public BigDecimal cost;

    public ControlBox(String productID, int drawTime, int programmeTime, String broughtOutID1, String broughtOutID2,
                      String broughtOutID3, String broughtOutID4, String broughtOutID5, String broughtOutID6,
                      double compCost, double cost) {
        this.productID = productID;
        this.drawTime = drawTime;
        this.programmeTime = programmeTime;
        this.broughtOutID1 = broughtOutID1;
        this.broughtOutID2 = broughtOutID2;
        this.broughtOutID3 = broughtOutID3;
        this.broughtOutID4 = broughtOutID4;
        this.broughtOutID5 = broughtOutID5;
        this.broughtOutID6 = broughtOutID6;
        this.compCost = BigDecimal.valueOf(compCost);
        this.cost = BigDecimal.valueOf(cost);
    }

}
