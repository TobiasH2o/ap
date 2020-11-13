import components.ContractHeading;
import components.HeadingLine;
import components.Qproduct;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class FileManager {

    public static String filePath;
    FileWriter fw;
    BufferedWriter bw;
    InputStreamReader fr;
    BufferedReader br;

    public FileManager(String filePath) {
        FileManager.filePath = filePath;
        buildDirectory(filePath);
    }

    public FileManager() {}

    public void backupContract(FullContract fc){
        saveContract(fc, new File(filePath + "\\Data\\Contracts\\backupContract.cot"));
    }

    public void buildDirectory(String filePath){
        File f = new File(filePath);
        if (!f.exists() || !f.isDirectory()) {
            Log.logLine("Making Directory " + f.getName());
            if (f.mkdirs()) hideFile(f);
            else Log.logLine("Failed Directory " + f.getName());
        }
        File f1 = new File(filePath + "\\Data\\");
        File f2 = new File(filePath + "\\Tables\\");
        File f3 = new File(filePath + "\\Data\\Contracts\\");
        File f4 = new File(filePath + "\\errorLogs\\");
        if (!f1.exists() || !f1.isDirectory()) {
            Log.logLine("Making Directory " + f1.getName());
            if (!f1.mkdirs()) JOptionPane.showMessageDialog(null, "Failed to create required directory \\Data\\");
        }
        if (!f2.exists() || !f2.isDirectory()) {
            Log.logLine("Making Directory " + f2.getName());
            if (!f2.mkdirs()) JOptionPane.showMessageDialog(null, "Failed to create required directory \\Tables\\");
        }
        if (!f3.exists() || !f3.isDirectory()) {
            Log.logLine("Making Directory " + f3.getName());
            if (!f3.mkdirs())
                JOptionPane.showMessageDialog(null, "Failed to create required directory " + "\\Data\\Contracts\\");
        }
        if (!f4.exists() || !f4.isDirectory()) {
            Log.logLine("Making Directory " + f4.getName());
            if (!f4.mkdirs())
                JOptionPane.showMessageDialog(null, "Failed to create required directory " + "\\Data\\errorLogs\\");
        }
    }

    public static boolean checkFile(String filePath) {
        if (!filePath.endsWith(".txt")) filePath += ".txt";
        File f = new File(filePath);
        Log.logLine("Checking file " + f);
        if (!f.exists()) {
            Log.logLine("Can't discover file");
            try {
                Log.logLine("Creating File " + f.getAbsolutePath());
                if (!f.createNewFile()) JOptionPane
                        .showMessageDialog(null, "Failed to create required file:\n" + f.getAbsolutePath(),
                                "CRITICAL ERROR", JOptionPane.WARNING_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    static private void hideFile(File file) {
        try {
            // execute attrib command to set hide attribute
            Process p = Runtime.getRuntime().exec("attrib +H " + file.getPath());
            // for removing hide attribute
            //Process p = Runtime.getRuntime().exec("attrib -H " + file.getPath());
            p.waitFor();
            if (file.isHidden()) {
                System.out.println(file.getName() + " hidden attribute is set to true");
            } else {
                System.out.println(file.getName() + " hidden attribute not set to true");
            }
        } catch (IOException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void saveFile(String file, String[][] data) {
        try {
            if (!file.endsWith(".txt")) file += ".txt";
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

    public void saveFile(String file, String[] data, boolean full) {
        try {
            if (!file.endsWith(".txt")) file += ".txt";
            File f = new File(filePath + "\\" + file);
            Log.logLine("Saving data to " + f);
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            for (String dat : data) {
                bw.append(dat.replaceAll("\n", "").replaceAll("\r", ""));
                if(full)
                    bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readFile(String file) {
        File f;
        try {
            if (!file.endsWith(".txt")) file += ".txt";
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

            if (file.exists()) if (file.delete()) if (!file.createNewFile()) {
                JOptionPane.showMessageDialog(null, "Failed to create critical file:\n" + file.getAbsolutePath(),
                        "CRITICAL ERROR", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ArrayList<String> tempArr = new ArrayList<>(0);
            ArrayList<String[]> saveValue = new ArrayList<>(0);
            saveValue.add(new String[]{contract.details.contractID, "" +
                                                                    contract.details.contractDate, contract.details.companyName, contract.details.address1, contract.details.address2, contract.details.address3, contract.details.postcode, contract.details.deliveryMethod,
                    "" + contract.details.deliveryDate,
                    "" + contract.details.quote, "" + contract.details.issued, contract.details.engineer,
                    contract.details.contractor});

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
            for (Qproduct q : contract.qProducts) {
                tempArr.add(q.qID + "%50" + q.headingLineID + "%50" + q.cost + "%50" + q.type);
            }
            saveValue.add(tempArr.toArray(String[]::new));


            saveFile(file, saveValue.toArray(String[][]::new));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deleteDir(String filePath) {
        File f = new File(filePath);
        _deleteDir(f);
    }

    private void _deleteDir(File f) {
        File[] allContent = f.listFiles();
        if (allContent != null) for (File f1 : allContent)
            _deleteDir(f1);
        if(!f.delete())
            JOptionPane.showMessageDialog(null, "Failed to remove file:\n" + f.getAbsolutePath(),
                    "MINOR ERROR", JOptionPane.WARNING_MESSAGE);
    }

    public boolean configBoolean(String entry) {
        return Convert.getBoolean(configString(entry));
    }

    public String configString(String entry){
        try{
            fr = new FileReader(filePath + "\\config.txt");
            br = new BufferedReader(fr);
            AtomicReference<String> rValue = new AtomicReference<>();
            br.lines().forEachOrdered(item ->{
                String cat = item.split("=")[0];
                if(cat.equals(entry))
                    rValue.set(item.split("=")[1]);
            });
            return rValue.get();
        }catch(Exception e){
            e.printStackTrace();
        }
        return "0";
    }

}
