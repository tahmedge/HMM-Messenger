package myPackage;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;


public class Db {

    private String driverPath = "com.mysql.jdbc.Driver";
    private String Url = "jdbc:mysql://localhost:3306/hmm";
    private String UserName = "root";
    private String PassWord = "";
    PreparedStatement pst = null;
    MainScreen xyz;
    public static String[] onlineUser = new String[200];
    public static int totalOnlineUSer;
    
    Db(MainScreen abc) {
        xyz = abc;
    }

    Db() {
        
    }

    public Connection sqlConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driverPath);
        Connection con = DriverManager.getConnection(Url, UserName, PassWord);
        return con;
    }
    
    public void getOnlineUser() throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();
        String query = "select user_name from login_info WHERE online_stat = '1'";
        ResultSet rs = stm.executeQuery(query);
        //int i = 0;
        //int ui = Integer.parseInt(Id);
        int i =0;
        while (rs.next()) {
            onlineUser[i]= rs.getString("user_name");
            System.out.println(onlineUser[i]);
            i++;
            
            // break;
        }
        totalOnlineUSer=i;
    }

    public void setOnline(String user) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();

        String query = "Update login_info SET online_stat='1' WHERE user_name = '"+user+"' ";
        stm.executeUpdate(query);
        System.out.println("Online set for "+user);

    }
    public void setOffline(String user) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();

        String query = "Update login_info SET online_stat='0' WHERE user_name = '"+user+"' ";
        stm.executeUpdate(query);
        System.out.println("Offline set for "+user);

    }
    
     public int checkValidity(String user, String pass) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();
        String query = "select count(user_name) as total from login_info WHERE user_name = '"+user+"' and password = '"+pass+"'";
        ResultSet rs = stm.executeQuery(query);
        //int i = 0;
        //int ui = Integer.parseInt(Id);
        int i =0;
        while (rs.next()) {
            i= rs.getInt("total");
            System.out.println(i);
            break;
        }
        
        return i;
    }
     
     public int checkSignUpValidity(String user, String phone) throws ClassNotFoundException, SQLException {
        int uv=0;
        int pv=0;
        Connection con = sqlConnection();
        Statement stm = con.createStatement();
        String query = "select count(user_name) as total from login_info WHERE user_name = '"+user+"'";
        ResultSet rs = stm.executeQuery(query);
        //int i = 0;
        //int ui = Integer.parseInt(Id);
        int i =0;
        while (rs.next()) {
            
            i= rs.getInt("total");
            //System.out.println(i);
            uv+=i;
            break;
        }
        //return i;
        String query2 = "select count(phone) as totalP from login_info WHERE phone = '"+phone+"'";
        ResultSet rs2 = stm.executeQuery(query2);
        i =0;
        while (rs2.next()) {
            
            i= rs2.getInt("totalP");
            //System.out.println(i);
            if(i==1)pv=2;
        }
        return uv+pv;
        
    }
     
     public void addUser(String fullName, String uname, String phone, String pass) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();

        String query = "Insert into login_info values('"+fullName+"', '"+phone+"', '"+uname+"', '"+pass+"', '0')";
        stm.executeUpdate(query);
        System.out.println("Data Inserted Successfully");

    }

    public void updateimage(String id, byte[] pic) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();
        String sql = "Update info set image=?  Where card_no='" + id + "'";
        pst = con.prepareStatement(sql);
        pst.setBytes(1, pic);
        pst.execute();
        JOptionPane.showMessageDialog(null, "Image saved Added");

    }

    public void addStudent(String no, byte[] image, String name, String fName, String institution, String clas, String section, String stid, String rf_no, String sex, String dob, String phn, String parPhone, String address) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();

        String query = "Insert into info  values('" + no + "','" + image + "','" + name + "','" + fName + "','" + institution + "','" + clas + "','" + section + "','" + stid + "','" + rf_no + "','" + sex + "','" + dob + "','" + phn + "','" + parPhone + "','" + address + "')";
        stm.executeUpdate(query);
        System.out.println("Data Inserted Successfully");

        Statement stm1 = con.createStatement();
        String sql = "Update info set image=?  Where no='" + no + "'";
        pst = con.prepareStatement(sql);
        pst.setBytes(1, image);
        pst.execute();
        JOptionPane.showMessageDialog(null, "Student Added");
    }

    public void updateStudentInfo(String no, byte[] image, String name, String fName, String institution, String clas, String section, String stid, String rf_no, String sex, String dob, String phn, String parPhone, String address) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();

        String query = "Update info SET name='" + name + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query);
        String query2 = "Update info SET fathers_name='" + fName + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query2);
        String query3 = "Update info SET institution='" + institution + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query3);
        String query4 = "Update info SET class='" + clas + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query4);
        String query5 = "Update info SET section='" + section + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query5);
        String query6 = "Update info SET id='" + stid + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query6);
        String query7 = "Update info SET card_no='" + rf_no + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query7);
        String query8 = "Update info SET sex='" + sex + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query8);
        String query9 = "Update info SET dob='" + dob + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query9);
        String query10 = "Update info SET phone_no='" + phn + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query10);
        String query11 = "Update info SET parents_phone='" + parPhone + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query11);
        String query12 = "Update info SET address='" + address + "' WHERE No = '" + no + "' ";
        stm.executeUpdate(query12);


        String sql = "Update info set image=?  Where no='" + no + "'";
        pst = con.prepareStatement(sql);
        pst.setBytes(1, image);
        pst.execute();
        JOptionPane.showMessageDialog(null, "Infrormation Edited");
    }

    

    public void updateDbInfo(String tst) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();

        String query = "Update dbInfo SET TotalSt='" + tst + "' WHERE No = '1' ";
        stm.executeUpdate(query);

    }

    public void setMacA(String mac) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();

        String query = "Update dbInfo SET MacA='" + mac + "' WHERE No = '1' ";
        stm.executeUpdate(query);
        System.out.println("hmm");

    }
    
    

    /*public void load(String Id) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();
        String query = "select * from info WHERE card_no = '" + Id + "'";
        ResultSet rs = stm.executeQuery(query);

        System.out.println("here0");
        while (rs.next()) {
            no = rs.getString("No");
            pic_byte[1] = rs.getBytes("image");
            stName = rs.getString("name");


            stFather = rs.getString("fathers_name");
            stInst = rs.getString("institution");
            stClass = rs.getString("class");
            stSection = rs.getString("section");
            stId = rs.getString("id");
            rfid = rs.getString("card_no");
            stSex = rs.getString("sex");
            stDob = rs.getString("dob");
            stPhone = rs.getString("phone_no");
            stParent_phone = rs.getString("parents_phone");
            stAddress = rs.getString("address");

            System.out.println(stAddress);
            //i++;
            System.out.println("here11");
            // break;
        }
    }

    public void load(String nm, String cls) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();
        String query = null;
        if (nm.equals("") == true && cls.equals("") == false) {
            query = "select name, class, institution, card_no from info WHERE class = '" + cls + "'";
        } else if (nm.equals("") == false && cls.equals("") == true) {
            query = "select name, class, institution, card_no from info WHERE name LIKE '%" + nm + "%'";
        } else if (nm.equals("") == false && cls.equals("") == false) {
            query = "select name, class, institution, card_no from info WHERE name LIKE '%" + nm + "%' and class = '" + cls + "'";
        }
        ResultSet rs = stm.executeQuery(query);

        int z = 0;
        while (rs.next()) {

            sName[z] = rs.getString("name");
            sInst[z] = rs.getString("institution");
            sClass[z] = rs.getString("class");
            sRfid[z] = rs.getString("card_no");
            System.out.println(sName[z] + " " + sInst[z]);
            z++;

        }
        tSearch = z;
    }

    public void loadLoginInfo(String Id) throws ClassNotFoundException, SQLException {
        Connection con = sqlConnection();
        Statement stm = con.createStatement();
        String query = "select Time from login WHERE RFID = '" + Id + "'";
        ResultSet rs = stm.executeQuery(query);
        //int i = 0;
        //int ui = Integer.parseInt(Id);

        while (rs.next()) {
            lastLogin = rs.getString("Time");

            // break;
        }

    }*/

    

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Db s = new Db();
        //s.getDistAttendence("0800384E5628", "2016", "02");
        String x;
        int a = s.checkSignUpValidity("xn", "01718577119");
        System.out.println(a);
        //s.getOnlineUser();
        //s.checkValidity("xn", "1234");
        
    }

    
}
