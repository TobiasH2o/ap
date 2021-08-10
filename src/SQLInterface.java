import components.Contract;
import components.ContractHeading;
import components.HeadingLine;
import components.Qproduct;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class SQLInterface {

    final static String[] illegalCharacters = new String[]{";"};
    static String IP;
    static String USERNAME;
    static String PASSWORD;

    public static void setDetails(String ip, String username, String password) {
        IP = ip;
        USERNAME = username;
        PASSWORD = password;
    }

    public String[] getTableNames() {
        String[][] tables = sendQuery("SHOW TABLES");
        ArrayList<String> names = new ArrayList<>();
        for (String[] tableName : tables)
            names.addAll(Arrays.asList(tableName));
        return names.toArray(String[]::new);
    }

    public boolean contractExists(String contractID) {
        contractID = check(contractID);
        String[][] data;
        data = sendQuery("SELECT * FROM `contract` WHERE `contract`.`contractID` = '" + contractID + "'");
        return data.length != 0;
    }

    public String pushContract(FullContract contract) {
        if (!contractExists(contract.details.contractID)) {
            uploadContract(contract.details);
            pushFullContract(contract);
            return "Contract Uploaded";
        } else {
            return "Contract Exists. You can use the [amend] button to change the contract.";
        }
    }

    private void pushFullContract(FullContract contract) {
        for (ContractHeading ch : contract.contractHeadings) {
            int headingID = addContractHeading(ch);
            for (HeadingLine hl : contract.contractHeadingLine) {
                if (ch.headingID == hl.headingID) {
                    int headingLineID = addContractHeadingLine(headingID, hl);
                    if (hl.productID.equalsIgnoreCase("QP") || hl.productID.equalsIgnoreCase("QL")) {
                        for (Qproduct qp : contract.qProducts) {
                            if (qp.headingLineID == hl.headingLineID) {
                                addQproduct(headingLineID, qp);
                            }
                        }
                    }
                }
            }
        }
    }

    private void uploadContract(Contract contract) {
        //language=RoomSql
        sendQuery("INSERT INTO `contract` (`contractID`, `contractDate`, `companyName`, `addressOne`, `addressTwo`, `" +
                  "addressThree`, `postcode`, `deliveryMethod`, `quote`, `issued`, `engineer`) VALUES" + " ('" +
                  check(contract.contractID) + "', '" + checkDate(contract.contractDate) + "', '" +
                  check(contract.companyName) + "', '" + check(contract.address1) + "', '" + check(contract.address2) +
                  "', '" + check(contract.address3) + "', '" + check(contract.postcode) + "', '" +
                  check(contract.deliveryMethod) + "', '" + checkBool(contract.quote) + "', '" +
                  checkBool(contract.issued) + "', '" + check(contract.engineer) + "')");
    }

    private void addQproduct(int headingLineID, Qproduct qp) {
        sendQuery("INSERT INTO `qProduct` (`headingLineID`, `cost`, `type`) VALUES ('" + headingLineID + "', '" +
                  qp.cost + "', '" + qp.type + "')");
    }

    public void addEngineer(String engineer) {
        String engineerName = check(engineer);
        String[][] engineers = sendQuery("SELECT * FROM `engineer`");
        boolean add = true;
        for (String[] s : engineers) {
            if (engineerName.equalsIgnoreCase(s[0])) {
                add = false;
                break;
            }
        }
        if (add) sendQuery("INSERT INTO `engineer` (`engineer`) VALUES ('" + engineerName + "')");
    }

    private int addContractHeadingLine(int headingID, HeadingLine headingLine) {
        return (int) Convert.getIfNumeric(sendQuery("INSERT INTO `headingLine` (`headingID`, `productID`, " +
                                                    "`description`, `qProduct`, `quantity`) VALUES ('" + headingID +
                                                    "', '" + check(headingLine.productID) + "', '" +
                                                    check(headingLine.comment) + "', '" + checkBool(
                headingLine.productID.equalsIgnoreCase("QP") || headingLine.productID.equalsIgnoreCase("QL")) + "', '" +
                                                    headingLine.quantity + "'); SELECT LAST_INSERT_ID()")[0][0]);
    }

    private int addContractHeading(ContractHeading contractHeading) {
        return (int) Convert.getIfNumeric(sendQuery(
                //language=RoomSql
                "INSERT INTO `contractHeading` (`contractID`, `headingTitle`) " + "VALUES ('" +
                check(contractHeading.contractID) + "', '" + check(contractHeading.headingTitle) +
                "'); SELECT LAST_INSERT_ID()")[0][0]);
    }

    public String[][] fetchEntireTable(String table) {
        return sendQuery("SELECT * FROM `" + table + "`");
    }

    public void setContractDate(String date, String contractID) {
        sendQuery("UPDATE `contract` SET `contractDate` = '" + date + "' WHERE `contractID` = '" + contractID + "'");
    }

    public void issueContract(String contractID) {
        sendQuery("UPDATE `contract` SET `issued` = 1 WHERE `contractID` = '" + contractID + "'");
    }

    public void setContractor(String contractID, String contractor) {
        sendQuery("UPDATE `contract` SET `issuer` = '" + contractor + "' WHERE `contractID` = '" + contractID + "'");
    }

    public void setDeliveryDate(LocalDate deliveryDate, String contractID) {
        sendQuery("UPDATE `contract` SET `deliveryDate` = '" + deliveryDate + "' WHERE `contractID` = '" + contractID +
                  "'");
    }

    public void setDeliveryMethod(String deliveryMethod, String contractID) {
        sendQuery("UPDATE `contract` SET `deliveryMethod` = '" + deliveryMethod + "' WHERE `contractID` = '" +
                  contractID + "'");
    }

    public boolean ifIssued(String contractID) {
        String r =
                sendQuery("SELECT `issued` FROM `contract` WHERE `contract`.`contractID` = '" + contractID + "'")[0][0];
        return r.equals("1");
    }

    public String amendContract(FullContract fullContract) {
        ArrayList<String[][]> headingLines = new ArrayList<>();
        if (!contractExists(fullContract.details.contractID))
            return "The contract " + fullContract.details.contractID + " does not exist.\nYou can save the contract " +
                   "using the [Save] button instead.";
        if (ifIssued(fullContract.details.contractID))
            return "The contract " + fullContract.details.contractID + " has already been issued. It can not be " +
                   "amended.";
        String[][] contractHeadings = sendQuery(
                "SELECT `headingID` FROM `contractHeading` WHERE `contractHeading`" + ".`contractID` = '" +
                fullContract.details.contractID + "'");
        for (String[] value : contractHeadings)
            headingLines.add(sendQuery(
                    "SELECT `headingLineID`, `productID` FROM `headingLine` WHERE `headingLine`" + ".`headingID` =" +
                    " " + "'" + value[0] + "'"));
        for (String[][] items : headingLines)
            for (String[] item : items) {
                if (item[1].equalsIgnoreCase("QP") || item[1].equalsIgnoreCase("QL"))
                    sendQuery("DELETE FROM `qProduct` WHERE `headingLineID` = '" + item[0] + "'");
            }

        for (String[] contractHeading : contractHeadings)
            sendQuery("DELETE FROM `headingLine` WHERE `headingLine`.`headingID` = '" + contractHeading[0] + "'");

        sendQuery("DELETE FROM `contractHeading` WHERE `contractHeading`.`contractID` = '" +
                  fullContract.details.contractID + "'");

        pushFullContract(fullContract);
        return "Amended contract";

    }

    private String[][] parseResultSet(ResultSet rs) {
        String[][] result = null;
        try {
            int width = rs.getMetaData().getColumnCount();
            ArrayList<String> serverResponse = new ArrayList<>(0);
            while (rs.next()) {
                StringBuilder s1 = new StringBuilder();
                for (int i = 1;i <= width;i++) {
                    s1.append((rs.getString(i) + " ").trim()).append("~~");
                }
                s1.deleteCharAt(s1.length() - 1);
                s1.deleteCharAt(s1.length() - 1);
                serverResponse.add(s1.toString());
            }
            String[][] returnData = new String[serverResponse.size()][width];
            for (int i = 0;i < serverResponse.size();i++) {
                returnData[i] = serverResponse.get(i).split("~~").clone();
            }
            result = returnData;
        } catch (Exception ignored) {

        }
        return result;
    }

    private String[][] sendQuery(String statement) {
        Connection con = null;
        String[][] result = null;
        statement = "SET ROLE ALL;" + statement;
        try {
            con = DriverManager.getConnection(IP, USERNAME, PASSWORD);
            for (String statement2 : statement.split(";")) {
                Log.logLine(statement2);
                result = _sendQuery(con, statement2.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert con != null;
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String[][] _sendQuery(Connection con, String statement) {
        try {
            // Establishes connection to the server
            Statement s = con.createStatement();
            if (statement.contains("SELECT") || statement.contains("SHOW")) {
                ResultSet rs = s.executeQuery(statement);
                return parseResultSet(rs);
            } else {
                s.executeUpdate(statement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String check(String s) {
        for (String a : illegalCharacters)
            s = s.replaceAll(a, "");
        if (s.isEmpty()) s = "No Title";
        return s;
    }

    private int checkBool(Boolean bool) {
        if (bool) return 1;
        else return 0;
    }

    private String checkDate(LocalDate date) {
        return Objects.requireNonNullElseGet(date, () -> LocalDate.of(1000, 1, 1)).toString();
    }

    public String getVersion() {
        return sendQuery("SELECT * FROM `programVersion`")[0][0];
    }

    public void updateVersion(String version) {
        sendQuery("UPDATE `programVersion` SET `version` = '" + version + "'");
    }
}
