package components;

public class AltFilters {

    public String productID;
    public int skilledTime;
    public int semiSkilled;
    public int weightKG;
    public int filterQuant;
    public String filterCode;
    public int centerQuant;
    public int explosionPanelQuant;
    public int screw1800;
    public int screw2400;
    public int screw3000;
    public int screw3600;
    public int sleeveSupport;
    public int sundries;
    public int masticQuant;
    public int nbQuant;

    public AltFilters(String productID, int skilledTime, int semiSkilled, int weightKG, int filterQuant,
                      String filterCode, int centerQuant, int explosionPanelQuant, int screw1800, int screw2400,
                      int screw3000, int screw3600, int sleeveSupport, int sundries, int masticQuant, int nbQuant) {
        this.productID = productID;
        this.skilledTime = skilledTime;
        this.semiSkilled = semiSkilled;
        this.weightKG = weightKG;
        this.filterQuant = filterQuant;
        this.filterCode = filterCode;
        this.centerQuant = centerQuant;
        this.explosionPanelQuant = explosionPanelQuant;
        this.screw1800 = screw1800;
        this.screw2400 = screw2400;
        this.screw3000 = screw3000;
        this.screw3600 = screw3600;
        this.sleeveSupport = sleeveSupport;
        this.sundries = sundries;
        this.masticQuant = masticQuant;
        this.nbQuant = nbQuant;
    }
}
