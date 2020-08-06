package components;

import java.math.BigDecimal;

public class BeltGuard {

    public String productID;
    public int weightKG;
    public String broughtOutCode1;
    public int broughtOutQuant1;
    public String broughtOutCode2;
    public int broughtOutQuant2;
    public String broughtOutCode3;
    public int broughtOutQuant3;
    public BigDecimal cost;

    public BeltGuard(String productID, int weightKG, String broughtOutCode1, int broughtOutQuant1,
                     String broughtOutCode2, int broughtOutQuant2, String broughtOutCode3, int broughtOutQuant3,
                     double cost) {
        this.productID = productID;
        this.weightKG = weightKG;
        this.broughtOutCode1 = broughtOutCode1;
        this.broughtOutQuant1 = broughtOutQuant1;
        this.broughtOutCode2 = broughtOutCode2;
        this.broughtOutQuant2 = broughtOutQuant2;
        this.broughtOutCode3 = broughtOutCode3;
        this.broughtOutQuant3 = broughtOutQuant3;
        this.cost = BigDecimal.valueOf(cost);
    }

}
