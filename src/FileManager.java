import components.ContractHeading;
import components.HeadingLine;
import components.Qproduct;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class FileManager {

    FileWriter fw;
    BufferedWriter bw;
    InputStreamReader fr;
    BufferedReader br;
    String filePath;

    FileManager(String filePath) {
        this.filePath = filePath;
        File f = new File(filePath);
        if (!f.exists() || !f.isDirectory()) {
            Log.logLine("Making Directory " + f.getName());
            if (f.mkdirs())
            Log.logLine("Created Directory " + f.getName());
            else
                Log.logLine("Failed Directory " + f.getName());
        }
        File f1 = new File(filePath + "\\Data\\");
        File f2 = new File(filePath + "\\Tables\\");
        if (!f1.exists() || !f1.isDirectory()) {
            Log.logLine("Making Directory " + f1.getName());
            if (f1.mkdirs())
            Log.logLine("Created Directory " + f1.getName());
            else
                Log.logLine("Failed Directory " + f1.getName());
        }
        if (!f2.exists() || !f2.isDirectory()) {
            Log.logLine("Making Directory " + f2.getName());
            if (f2.mkdirs())
            Log.logLine("Created Directory " + f2.getName());
            else
                Log.logLine("Failed Directory " + f2.getName());
        }
    }

    public FileManager() {
        this.filePath = "C:\\";
    }

    public static void checkFile(String filePath) {
        if(!filePath.endsWith(".txt"))
            filePath += ".txt";
        File f = new File(filePath);
        Log.logLine("Checking file " + filePath);
        if (!f.exists()) {
            Log.logLine("Can't discover file");
            try {
                if (f.createNewFile()) Log.logLine("Created file");
                else Log.logLine("Can't create new file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveFile(String file, String[][] data) {
        try {
            if(!file.endsWith(".txt"))
                file += ".txt";
            File f;
            f = new File(filePath + "\\" + file);
            Log.logLine("Saving data to " + f);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            for (String[] dat : data) {
                for (String da : dat) {
                    String a = da + "~~";
                    bw.append(a.replaceAll("\n", "").replaceAll("\r", ""));
                }
                bw.append("\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(File file, String[][] data) {
        try {
            Log.logLine("Saving data to " + file);
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            for (String[] dat : data) {
                for (String da : dat) {
                    String a = da + "~~";
                    bw.append(a.replaceAll("\n", "").replaceAll("\r", ""));
                }
                bw.append("\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(String file, String[] data) {
        try {
            if(!file.endsWith(".txt"))
                file += ".txt";
            File f = new File(filePath + "\\" + file);
            Log.logLine("Saving data to " + f);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            for (String dat : data) {
                bw.append(dat.replaceAll("\n", "").replaceAll("\r", ""));
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readFile(String file) {
        File f;
        try {
            if(!file.endsWith(".txt"))
                file += ".txt";
            checkFile(filePath + "\\" + file);
            f = new File(filePath + "\\" + file);
            Log.logLine("Reading data from " + f);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            ArrayList<String> returnArray = new ArrayList<>(0);
            br.lines().forEach(returnArray::add);
            br.close();
            return returnArray.toArray(new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String[]{"ERROR"};
    }

    public String[] readFile(File f) {
        try {
            Log.logLine("Reading data from " + f);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            ArrayList<String> returnArray = new ArrayList<>(0);
            br.lines().forEach(returnArray::add);
            br.close();
            return returnArray.toArray(new String[]{});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String[]{"ERROR"};
    }

    public void appendToFile(String file, String data) {

        try {
            checkFile(filePath + "\\" + file);
            File f = new File(filePath + "\\" + file);
            Log.logLine("Appending data " + data + " to " + f.getPath());
            fw = new FileWriter(f, true);
            bw = new BufferedWriter(fw);
            bw.append("\n").append(data);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage loadImage(String path) {
        BufferedImage mb = null;
        Log.logLine(path);
        try {
            mb = ImageIO.read(this.getClass().getResourceAsStream(path));
        } catch (Exception e) {
            try {
                mb = ImageIO.read(this.getClass().getResourceAsStream("/Images/errorImage.png"));
            } catch (Exception ioException) {
                ioException.printStackTrace();
            }
            e.printStackTrace();
        }
        return mb;
    }

    public void saveContract(FullContract contract, File file) {
        try {

            if (file.exists()) file.delete();
            file.createNewFile();
            ArrayList<String> tempArr = new ArrayList<>(0);
            ArrayList<String[]> saveValue = new ArrayList<>(0);
            saveValue.add(new String[]{contract.details.contractID, "" + contract.details.contractDate,
                    contract.details.companyName, contract.details.address1, contract.details.address2,
                    contract.details.address3, contract.details.postcode, contract.details.deliveryMethod,
                    "" + contract.details.deliveryDate, "" + contract.details.quote, "" + contract.details.issued,
                    contract.details.engineer});

            for (ContractHeading ch : contract.contractHeadings) {
                tempArr.add(ch.headingID + "%50" + ch.contractID + "%50" + ch.headingTitle);
            }
            saveValue.add(tempArr.toArray(String[]::new));
            tempArr.clear();

            for (HeadingLine chl : contract.contractHeadingLine) {
                if (chl.comment.equals("")) {
                    chl.comment = "null";
                }
                tempArr.add(chl.headingLineID + "%50" + chl.headingID + "%50" + chl.productID + "%50" + chl.comment +
                            "%50" + chl.quantity);
            }
            saveValue.add(tempArr.toArray(String[]::new));
            tempArr.clear();
            for (Qproduct q : contract.qProducts){
                tempArr.add(q.qID + "%50" + q.headingLineID + "%50" + q.cost + "%50" + q.type);
            }
            saveValue.add(tempArr.toArray(String[]::new));


            saveFile(file, saveValue.toArray(String[][]::new));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
