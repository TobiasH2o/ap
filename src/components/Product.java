package components;

import java.math.BigDecimal;

public class Product {

    public String productID;
    public String description;
    public int makeTime;
    public int plasmaTime;
    public int paintTime;
    public int balanceTime;
    public int testTime;
    public BigDecimal salesCost;
    public String productType;
    public Boolean skilled;
    public Boolean stock;
    public Boolean discontinued;
    public Boolean fan;
    public Boolean impellor;
    public Boolean cyclone;
    public Boolean broughtOut;
    public Boolean broughtOutPaB;
    public Boolean controlBox;
    public Boolean filter;
    public Boolean sprayBooth;
    public Boolean pipeOrBend;
    public Boolean soundEnclosure;
    public Boolean valve;
    public Boolean dustUnit;
    public Boolean hood;
    public Boolean beltGuard;
    public Boolean bin;
    public Boolean cefFilter;
    public Boolean erectionAndTransport;
    public Boolean oAndS;
    public Boolean pulleyBlastGates;
    public Boolean silo;


    public Product(String productID, String description, int makeTime, int plasmaTime, int paintTime, int balanceTime,
                   int testTime, double salesCost, String productType, Boolean skilled, Boolean stock,
                   Boolean discontinued, Boolean fan, Boolean impellor, Boolean cyclone, Boolean broughtOut,
                   Boolean broughtOutPaB, Boolean controlBox, Boolean filter, Boolean sprayBooth, Boolean pipeOrBend,
                   Boolean soundEnclosure, Boolean valve, Boolean dustUnit, Boolean hood, Boolean beltGuard,
                   Boolean bin, Boolean cefFilter, Boolean erectionAndTransport, Boolean oAndS,
                   Boolean pulleyBlastGates, Boolean silo) {
        this.productID = productID;
        this.description = description;
        this.makeTime = makeTime;
        this.plasmaTime = plasmaTime;
        this.paintTime = paintTime;
        this.balanceTime = balanceTime;
        this.testTime = testTime;
        this.salesCost = BigDecimal.valueOf(salesCost);
        this.productType = productType;
        this.skilled = skilled;
        this.stock = stock;
        this.discontinued = discontinued;
        this.fan = fan;
        this.impellor = impellor;
        this.cyclone = cyclone;
        this.broughtOut = broughtOut;
        this.broughtOutPaB = broughtOutPaB;
        this.controlBox = controlBox;
        this.filter = filter;
        this.sprayBooth = sprayBooth;
        this.valve = valve;
        this.soundEnclosure = soundEnclosure;
        this.dustUnit = dustUnit;
        this.hood = hood;
        this.beltGuard = beltGuard;
        this.bin = bin;
        this.cefFilter = cefFilter;
        this.erectionAndTransport = erectionAndTransport;
        this.oAndS = oAndS;
        this.pulleyBlastGates = pulleyBlastGates;
        this.silo = silo;
        this.pipeOrBend = pipeOrBend;
    }

}
