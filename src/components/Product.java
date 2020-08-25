package components;

import java.math.BigDecimal;

public class Product {

    private String productID;
    private String description;
    private double makeTime;
    private double plasmaTime;
    private double paintTime;
    private double balanceTime;
    private double testTime;
    private BigDecimal salesCost;
    private String productType;
    private Boolean skilled;
    private Boolean stock;
    private Boolean discontinued;
    private Boolean fan;
    private Boolean impellor;
    private Boolean cyclone;
    private Boolean broughtOut;
    private Boolean broughtOutPaB;
    private Boolean controlBox;
    private Boolean filter;
    private Boolean sprayBooth;
    private Boolean pipeOrBend;
    private Boolean soundEnclosure;
    private Boolean valve;
    private Boolean dustUnit;
    private Boolean hood;
    private Boolean beltGuard;
    private Boolean bin;
    private Boolean cefFilter;
    private Boolean erectionAndTransport;
    private Boolean oAndS;
    private Boolean pulleyBlastGates;
    private Boolean silo;


    public Product(String productID, String description, double makeTime, double plasmaTime, double paintTime, double balanceTime,
            double testTime, double salesCost, String productType, Boolean skilled, Boolean stock,
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

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMakeTime() {
        return makeTime;
    }

    public void setMakeTime(double makeTime) {
        this.makeTime = makeTime;
    }

    public double getPlasmaTime() {
        return plasmaTime;
    }

    public void setPlasmaTime(double plasmaTime) {
        this.plasmaTime = plasmaTime;
    }

    public double getPaintTime() {
        return paintTime;
    }

    public void setPaintTime(double paintTime) {
        this.paintTime = paintTime;
    }

    public double getBalanceTime() {
        return balanceTime;
    }

    public void setBalanceTime(double balanceTime) {
        this.balanceTime = balanceTime;
    }

    public double getTestTime() {
        return testTime;
    }

    public void setTestTime(double testTime) {
        this.testTime = testTime;
    }

    public BigDecimal getSalesCost() {
        return salesCost;
    }

    public void setSalesCost(BigDecimal salesCost) {
        this.salesCost = salesCost;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Boolean getSkilled() {
        return skilled;
    }

    public void setSkilled(Boolean skilled) {
        this.skilled = skilled;
    }

    public Boolean getStock() {
        return stock;
    }

    public void setStock(Boolean stock) {
        this.stock = stock;
    }

    public Boolean getDiscontinued() {
        return discontinued;
    }

    public void setDiscontinued(Boolean discontinued) {
        this.discontinued = discontinued;
    }

    public Boolean getFan() {
        return fan;
    }

    public void setFan(Boolean fan) {
        this.fan = fan;
    }

    public Boolean getImpellor() {
        return impellor;
    }

    public void setImpellor(Boolean impellor) {
        this.impellor = impellor;
    }

    public Boolean getCyclone() {
        return cyclone;
    }

    public void setCyclone(Boolean cyclone) {
        this.cyclone = cyclone;
    }

    public Boolean getBroughtOut() {
        return broughtOut;
    }

    public void setBroughtOut(Boolean broughtOut) {
        this.broughtOut = broughtOut;
    }

    public Boolean getBroughtOutPaB() {
        return broughtOutPaB;
    }

    public void setBroughtOutPaB(Boolean broughtOutPaB) {
        this.broughtOutPaB = broughtOutPaB;
    }

    public Boolean getControlBox() {
        return controlBox;
    }

    public void setControlBox(Boolean controlBox) {
        this.controlBox = controlBox;
    }

    public Boolean getFilter() {
        return filter;
    }

    public void setFilter(Boolean filter) {
        this.filter = filter;
    }

    public Boolean getSprayBooth() {
        return sprayBooth;
    }

    public void setSprayBooth(Boolean sprayBooth) {
        this.sprayBooth = sprayBooth;
    }

    public Boolean getPipeOrBend() {
        return pipeOrBend;
    }

    public void setPipeOrBend(Boolean pipeOrBend) {
        this.pipeOrBend = pipeOrBend;
    }

    public Boolean getSoundEnclosure() {
        return soundEnclosure;
    }

    public void setSoundEnclosure(Boolean soundEnclosure) {
        this.soundEnclosure = soundEnclosure;
    }

    public Boolean getValve() {
        return valve;
    }

    public void setValve(Boolean valve) {
        this.valve = valve;
    }

    public Boolean getDustUnit() {
        return dustUnit;
    }

    public void setDustUnit(Boolean dustUnit) {
        this.dustUnit = dustUnit;
    }

    public Boolean getHood() {
        return hood;
    }

    public void setHood(Boolean hood) {
        this.hood = hood;
    }

    public Boolean getBeltGuard() {
        return beltGuard;
    }

    public void setBeltGuard(Boolean beltGuard) {
        this.beltGuard = beltGuard;
    }

    public Boolean getBin() {
        return bin;
    }

    public void setBin(Boolean bin) {
        this.bin = bin;
    }

    public Boolean getCefFilter() {
        return cefFilter;
    }

    public void setCefFilter(Boolean cefFilter) {
        this.cefFilter = cefFilter;
    }

    public Boolean getErectionAndTransport() {
        return erectionAndTransport;
    }

    public void setErectionAndTransport(Boolean erectionAndTransport) {
        this.erectionAndTransport = erectionAndTransport;
    }

    public Boolean getoAndS() {
        return oAndS;
    }

    public void setoAndS(Boolean oAndS) {
        this.oAndS = oAndS;
    }

    public Boolean getPulleyBlastGates() {
        return pulleyBlastGates;
    }

    public void setPulleyBlastGates(Boolean pulleyBlastGates) {
        this.pulleyBlastGates = pulleyBlastGates;
    }

    public Boolean getSilo() {
        return silo;
    }

    public void setSilo(Boolean silo) {
        this.silo = silo;
    }
}
