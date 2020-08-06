package components;

import java.math.BigDecimal;

public class SoundEnclosure {

    public String productID;
    public int weightKG;
    public int sundries;
    public int foamMtrSqr;
    public int camLockQuant;
    public BigDecimal cost;

    public SoundEnclosure(String productID, int weightKG, int sundries, int foamMtrSqr, int camLockQuant, double cost) {
        this.productID = productID;
        this.weightKG = weightKG;
        this.sundries = sundries;
        this.foamMtrSqr = foamMtrSqr;
        this.camLockQuant = camLockQuant;
        this.cost = BigDecimal.valueOf(cost);
    }

}
