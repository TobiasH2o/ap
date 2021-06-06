import components.*;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SudoSQL {


    public static FullContract getContract(Contract contract, ContractHeading[] contractHeadings,
                                           HeadingLine[] headingLines, Product[] products, Qproduct[] qproducts) {
        FullContract fc = new FullContract();
        fc.setDetails(contract);
        Log.logLine("SudoSQL-Contractor " + contract.contractor);
        // Loads each Contract heading
        for (ContractHeading contractHeading : contractHeadings)
            // Comps each heading to contract ID
            if (contractHeading.contractID.equals(contract.contractID)) {
                fc.addHeading(contractHeading);
                // Comps each correct heading to each headingLineId that is validated
                for (HeadingLine headingLine : headingLines)
                    // Validates each heading line to fin if it belongs to the heading
                    if (headingLine.headingID == contractHeading.headingID) {
                        fc.addHeadingLine(headingLine);
                        // Find the product that is referenced inside HeadingLine
                        for (Product product : products)
                            if (headingLine.productID.equals(product.getProductID())) {
                                fc.addProduct(product);
                                if(product.getProductID().equals("QP") || product.getProductID().equals("QL"))
                                    for(Qproduct q : qproducts)
                                        if(q.headingLineID == headingLine.headingLineID)
                                            fc.addQProduct(q.headingLineID, q.cost.doubleValue(), q.type);
                            }
                    }
            }
        return fc;
    }

    public static Contract[] getContracts(String filePath) {
        Log.logLine("Loading contracts");
        URI f = new File(filePath + "\\Tables\\contract.txt").toURI();
        FileManager.checkFile(f.getPath());
        ArrayList<Contract> contracts = null;
        try {
            contracts = Files.lines(Paths.get(f)).map(line -> line.split("~~"))
                    .map(data -> new Contract(data[0], Convert.getIfDate(data[1]), data[2], data[3], data[4], data[5],
                                              data[6], data[7], Convert.getIfDate(data[8]),
                                              Convert.getBoolean(data[9]),
                                              data[11], Convert.getBoolean(data[10]), data[12]))
                    .collect(Collectors.toCollection(() -> new ArrayList<>(0)));
        } catch (Exception ignore) {}
        Log.logLine("Loaded contracts");
        assert contracts != null;
        return contracts.toArray(Contract[]::new);
    }

    public static String[] getEngineers(String filePath) {
        ArrayList<String> engineers = new ArrayList<>(0);
        Log.logLine("Loading engineers");
        engineers.addAll(Arrays.asList(new FileManager(filePath).readFile("\\Tables\\Engineer.txt")));
        Log.logLine("Loaded engineers");
        IntStream.range(0, engineers.size()).forEachOrdered(i -> {
            engineers.add(engineers.get(0).replace("~~", ""));
            engineers.remove(0);
        });
        return engineers.toArray(String[]::new);
    }

    public static Qproduct[] getQproducts(String filePath){
        URI f;
        ArrayList<Qproduct> qproducts = new ArrayList<>(0);
        f = new File(filePath + "\\Tables\\qProduct.txt").toURI();
        FileManager.checkFile(f.getPath());
        for(String line : new FileManager(filePath).readFile("\\Tables\\qProduct.txt")){
            String[] data = line.split("~~");
            qproducts.add(new Qproduct((int) Convert.getIfNumeric(data[0]), (int) Convert.getIfNumeric(data[1]),
                                       Convert.getIfNumeric(data[2]),
                          data[3]));
        }
        Log.logLine("Loading qProducts (" + qproducts.size() + ")");
        return qproducts.toArray(Qproduct[]::new);
    }

    public static Product[] getProducts(String filePath) {
        URI f;
        ArrayList<Product> products = new ArrayList<>(0);
        f = new File(filePath + "\\Tables\\Product.txt").toURI();
        FileManager.checkFile(f.getPath());
        for(String line : new FileManager(filePath).readFile("\\Tables\\Product.txt")){
                String[] data = line.split("~~");
                products.add(new Product(data[0], data[1], Convert.getIfNumeric(data[3]),
                         Convert.getIfNumeric(data[4]),
                                         Convert.getIfNumeric(data[5]), Convert.getIfNumeric(data[6]),
                                         Convert.getIfNumeric(data[7].replaceAll("£", "")),
                        Convert.getIfNumeric(data[8]),
                                         data[9],
                                         Convert.getBoolean(data[10]), Convert.getBoolean(data[11]),
                                         Convert.getBoolean(data[12]), Convert.getBoolean(data[13]),
                                         Convert.getBoolean(data[14]), Convert.getBoolean(data[15]),
                                         Convert.getBoolean(data[16]), Convert.getBoolean(data[17]),
                                         Convert.getBoolean(data[18]), Convert.getBoolean(data[19]),
                                         Convert.getBoolean(data[20]), Convert.getBoolean(data[21]),
                                         Convert.getBoolean(data[22]), Convert.getBoolean(data[23]),
                                         Convert.getBoolean(data[24]), Convert.getBoolean(data[25]),
                                         Convert.getBoolean(data[26]), Convert.getBoolean(data[27]),
                                         Convert.getBoolean(data[28]), Convert.getBoolean(data[29]),
                                         Convert.getBoolean(data[30]),
                        Convert.getBoolean(data[31]), Convert.getBoolean(data[32])));
            }
        Log.logLine("Loading products (" + products.size() + ")");
        return products.toArray(Product[]::new);
    }

    static public ContractHeading[] getContractHeading(String filePath) {
        Log.logLine("Loading contract Heading");
        URI f = new File(filePath + "\\Tables\\contractHeading.txt").toURI();
        FileManager.checkFile(f.getPath());
        ArrayList<ContractHeading> ch = null;
        try {
            ch = Files.lines(Paths.get(f)).map(line -> line.split("~~"))
                    .map(data -> new ContractHeading((int) Convert.getIfNumeric(data[0]), data[1], data[2]))
                    .collect(Collectors.toCollection(() -> new ArrayList<>(0)));
        } catch (Exception e) {e.printStackTrace();}
        Log.logLine("Loaded ContractHeading");

        assert ch != null;
        return ch.toArray(ContractHeading[]::new);
    }

    static public HeadingLine[] getHeadingLines(String filePath){
        Log.logLine("Loading Heading Lines");
        URI f = new File(filePath + "\\Tables\\headingLine.txt").toURI();
        FileManager.checkFile(f.getPath());
        ArrayList<HeadingLine> hl = null;
        try{
            hl =
                    Files.lines(Paths.get(f)).map(line -> line.split("~~")).map(data -> new HeadingLine((int) Convert.getIfNumeric(data[0]), (int) Convert.getIfNumeric(data[1]), data[2], data[3],
                                                                                                        (int) Convert.getIfNumeric(data[5]))).collect(
                            Collectors.toCollection(() -> new ArrayList<>(0)));
        }catch(Exception ignore){}
        assert hl != null;
        return hl.toArray(HeadingLine[]::new);
    }

}
