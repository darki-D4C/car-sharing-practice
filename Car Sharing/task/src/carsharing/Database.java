package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Database {
    Connection conn;
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String db_url = "jdbc:h2:~/IdeaProjects/Car Sharing/Car Sharing/task/src/carsharing/db/carsharing;DB_CLOSE_ON_EXIT=FALSE";
    
    public Database(){
        this.conn = null;
        Statement stmt = null;
        try {


            Class.forName(JDBC_DRIVER);
            //STEP 2: Open a connection

            this.conn=DriverManager.getConnection(db_url);


            this.conn.setAutoCommit(true);

            stmt = conn.createStatement();

            String sql =  "CREATE TABLE IF NOT EXISTS  COMPANY " +
                    "(ID INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    " NAME VARCHAR(255) NOT NULL UNIQUE);";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS CAR" +
                    "(ID INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "NAME VARCHAR(255) UNIQUE NOT NULL," +
                    "COMPANY_ID INTEGER NOT NULL," +
                    "CONSTRAINT ID FOREIGN KEY (COMPANY_ID)" +
                    "REFERENCES COMPANY(ID));";
            stmt.executeUpdate(sql);

            sql = "ALTER TABLE company ALTER COLUMN id RESTART WITH 1";
            stmt.executeUpdate(sql);

            sql = "ALTER TABLE car ALTER COLUMN id RESTART WITH 1";
            stmt.executeUpdate(sql);

            sql = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                    "(ID INTEGER PRIMARY KEY AUTO_INCREMENT," +
                    "NAME VARCHAR(255) UNIQUE NOT NULL," +
                    "RENTED_CAR_ID INTEGER," +
                    "CONSTRAINT CAR_ID FOREIGN KEY (RENTED_CAR_ID)" +
                    "REFERENCES CAR(ID));";
            stmt.executeUpdate(sql);

            sql = "ALTER TABLE customer ALTER COLUMN id RESTART WITH 1";
            stmt.executeUpdate(sql);


            stmt.close();

        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();

        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();

        } finally {
            //finally block used to close resources
            try {

                if(conn!=null) conn.close();

            } catch(SQLException se){
                se.printStackTrace();
            } //end finally try
        }
    }


    public void insertCompany(){
        Scanner inputName = new Scanner(System.in);
        this.conn = null;
        Statement stmt = null;
        try{
            System.out.println("Enter the company name:");
            String Company = inputName.nextLine();
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement();
            String sql =  "INSERT INTO COMPANY (name) VALUES"+"('"+Company+"');";
            stmt.executeUpdate(sql);
            this.conn.close();
            stmt.close();
            System.out.println("The company was created!");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }


    public void printCompanyList(){
        this.conn = null;
        Statement stmt = null;

        try{

            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT id, name FROM COMPANY";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){

                System.out.println("Choose a company:");
                rs.beforeFirst();
                while(rs.next()){
                    //Retrieve by column name
                    int id  = rs.getInt("id");
                    String name = rs.getString("name");

                    //Display values
                    System.out.print(id+". ");
                    System.out.println("" + name);
                }
                System.out.println("0. Back");
            }else{
                System.out.println("The company list is empty");
                printCompanyMenu();
            }

            this.conn.close();
            stmt.close();
            System.out.println("\n");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void pickCompany(){
        Scanner inputId = new Scanner(System.in);
        this.conn = null;
        Statement stmt = null;
        String name = null;
        try{
            Integer id = inputId.nextInt();

            if(id==0) printCompanyMenu();
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT id,name FROM COMPANY WHERE ID = "+id+"";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                rs.beforeFirst();
                while(rs.next()){
                    name = rs.getString("name");
                }
                this.conn.close();
                stmt.close();
                printCarMenu(id,name);
            }else{
                System.out.println("No such company!");
                this.conn.close();
                stmt.close();
                printCompanyMenu();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void printCarMenu(int idCompany, String nameCompany){
        Scanner inputCommand = new Scanner(System.in);


        System.out.println("'"+nameCompany+"' cars");
        System.out.println("1. Car list");
        System.out.println("2. Create a car");
        System.out.println("0. Back");

        int command = inputCommand.nextInt();

        switch (command) {
            case 2:
                insertCar(idCompany,nameCompany);
                printCarMenu(idCompany,nameCompany);
            case 1:
                printCarList(idCompany,nameCompany);
                printCarMenu(idCompany,nameCompany);
            case 0:
               printCompanyMenu();
        }

    }

    private void insertCar(int idCompany, String nameCompany) {
        Scanner inputName = new Scanner(System.in);
        this.conn = null;
        Statement stmt = null;
        try{
            System.out.println("Enter the car name:");
            String carName = inputName.nextLine();
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement();
            String sql =  "INSERT INTO CAR (name,company_id) VALUES"+"('"+carName+"',"+idCompany+");";
            stmt.executeUpdate(sql);
            this.conn.close();
            stmt.close();
            System.out.println("The car was created!");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void printCarList(int idCompany,String nameCompany){
        this.conn = null;
        Statement stmt = null;

        try{
            System.out.println("'"+nameCompany+"' cars:");
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT id, name FROM CAR WHERE COMPANY_ID="+idCompany+"";
            ResultSet rs = stmt.executeQuery(sql);
            int i = 1 ;
            if(rs.next()){
                rs.beforeFirst();
                while(rs.next()){
                    //Retrieve by column name
                    int id  = rs.getInt("id");
                    String name = rs.getString("name");


                    //Display values
                    System.out.print(i+". ");
                    System.out.println("" + name);
                    i++;
                }
                System.out.println("0. Back");
            }else{
                System.out.println("The car list is empty!");
                printCarMenu(idCompany,nameCompany);
            }

            this.conn.close();
            stmt.close();
            System.out.println("\n");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    public void printEntryMenu( ){

        Scanner inputCommand = new Scanner(System.in);
        System.out.println("1. Log in as a manager");
        System.out.println("2. Log in as a customer");
        System.out.println("3. Create a customer");
        System.out.println("0. Exit\n");
        int command = inputCommand.nextInt();
        switch (command) {
            case 1:
                printCompanyMenu();
            case 2:
                showCustomers();
                pickCustomer();
            case 3:
                createCustomer();
                printEntryMenu();
            case 0:
                System.exit(1);
        }

    }

    private void pickCustomer() {
        Scanner inputId = new Scanner(System.in);
        this.conn = null;
        Statement stmt = null;
        String name = null;
        try{
            Integer id = inputId.nextInt();

            if(id==0) printCompanyMenu();
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT id,name FROM CUSTOMER WHERE ID = "+id+"";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                rs.beforeFirst();
                while(rs.next()){
                    name = rs.getString("name");
                }
                this.conn.close();
                stmt.close();
                printCustomerMenu(id,name);
            }else{
                System.out.println("No such customer!");
                this.conn.close();
                stmt.close();
                printEntryMenu();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void printCustomerMenu(Integer id, String name) {

        Scanner inputCommand = new Scanner(System.in);



        System.out.println("1. Rent a car");
        System.out.println("2. Return a rented car");
        System.out.println("3. My rented car");
        System.out.println("0. Back");

        int command = inputCommand.nextInt();

        switch (command) {
            case 3:
                showRentedCar(id,name);
                printCustomerMenu(id, name);
            case 2:
                returnRentedCar(id,name);
                printCustomerMenu(id,name);
            case 1:
                showAvailableCompanies(id,name);
                printCustomerMenu(id,name);
            case 0:
                printEntryMenu();
        }

    }

    private void showAvailableCompanies(Integer idCustomer, String nameCustomer) {
        this.conn = null;
        Statement stmt = null;
        ArrayList<String> companyList = new ArrayList<>();

        try{

            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = " SELECT rented_car_id FROM CUSTOMER WHERE id = "+idCustomer+" AND rented_car_id IS NOT NULL";
            ResultSet resultSet = stmt.executeQuery(sql);
            if(resultSet.next()){
                System.out.print("You've already rented a car!\n");
                printCustomerMenu(idCustomer,nameCustomer);
            }

            sql =  "SELECT id,name FROM COMPANY;";
            ResultSet rs = stmt.executeQuery(sql);
            int i = 1 ;
            if(rs.next()){
                System.out.println("Choose a company:");
                rs.beforeFirst();
                while(rs.next()){
                    //Retrieve by column name
                    String name = rs.getString("name");
                    companyList.add(name);


                    //Display values
                    System.out.print(i+". ");
                    System.out.println("" + name);
                    i++;
                }
                System.out.println("0. Back");
                pickCompanyCars(companyList,idCustomer,nameCustomer);
            }else{
                System.out.println("The car list is empty!");
                printCustomerMenu(idCustomer,nameCustomer);
            }


            this.conn.close();
            stmt.close();
            System.out.println("\n");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void pickCompanyCars(ArrayList<String> companyList, Integer idCustomer, String nameCustomer) {
        Scanner inputId = new Scanner(System.in);
        this.conn = null;
        Statement stmt = null;
        String name;
        String nameCompany = null;
        int idCompany = 0;
        try{
            Integer id = inputId.nextInt();
            if(id==0) printCustomerMenu(idCustomer,nameCustomer);
            name = companyList.get(id-1);
            System.out.println(name);
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT ID FROM COMPANY WHERE NAME = '"+name+"'";
            ResultSet resultSet = stmt.executeQuery(sql);
            resultSet.next();
            int companyId = resultSet.getInt("id");
            sql =  "SELECT id,name FROM CAR WHERE company_id = '"+companyId+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                rs.beforeFirst();

                while(rs.next()){
                    idCompany = rs.getInt("id");
                }
                this.conn.close();
                stmt.close();
                showAvailableCars(idCustomer,nameCustomer,companyId);
                this.conn.close();
                stmt.close();
            }else{
                System.out.println("No such customer!");
                this.conn.close();
                stmt.close();
                printEntryMenu();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void returnRentedCar(Integer id, String name) {
        this.conn = null;
        Statement stmt = null;
        try{
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT RENTED_CAR_ID FROM CUSTOMER WHERE id="+id+" AND RENTED_CAR_ID IS NOT NULL";
            ResultSet rs = stmt.executeQuery(sql);
            int i = 1 ;
            if(rs.next()){
                rs.beforeFirst();
                sql = "UPDATE CUSTOMER SET RENTED_CAR_ID=null WHERE id="+id;

                stmt.executeUpdate(sql);
            }else{
                this.conn.close();
                stmt.close();
                System.out.println("You didn't rent a car!");
                printCustomerMenu(id,name);
            }
            this.conn.close();
            stmt.close();
            System.out.println("You've returned a rented car!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void showRentedCar(Integer idCust, String name) {
        this.conn = null;
        Statement stmt = null;

        try{
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT rented_car_id FROM CUSTOMER WHERE id="+idCust;
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                sql = "SELECT name,company_id FROM CAR WHERE id=" + rs.getString("rented_car_id");

                ResultSet resultSetCar = stmt.executeQuery(sql);
                if (resultSetCar.next()) {

                    String nameCar = resultSetCar.getString("name");
                    sql = "SELECT name FROM COMPANY WHERE id = " + resultSetCar.getString("company_id");
                    ResultSet resultSetCompany = stmt.executeQuery(sql);
                    if (resultSetCompany.next()) {
                        String nameCompany = resultSetCompany.getString("name");
                        System.out.println("Your rented car: \n" + nameCar + "\nCompany:\n" + nameCompany);
                        printCustomerMenu(idCust, name);
                    }

                }else{
                    System.out.println("You didn't rent a car!");
                    this.conn.close();
                    stmt.close();
                    System.out.println("\n");
                    printCustomerMenu(idCust,name);
                }

            }
            this.conn.close();
            stmt.close();
            System.out.println("\n");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAvailableCars(int idCustomer, String nameCustomer, int idCompany){
        this.conn = null;
        Statement stmt = null;
        ArrayList<String> nameList = new ArrayList<>();

        try{
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT id,name FROM CAR WHERE id NOT IN(SELECT rented_car_id FROM CUSTOMER WHERE rented_car_id IS NOT NULL) AND company_id = "+idCompany+";";
            ResultSet rs = stmt.executeQuery(sql);
            int i = 1 ;
            if(rs.next()){
                System.out.println("Choose a car:");
                rs.beforeFirst();
                while(rs.next()){
                    //Retrieve by column name
                    int id  = rs.getInt("id");
                    String name = rs.getString("name");
                    nameList.add(name);


                    //Display values
                    System.out.print(i+". ");
                    System.out.println("" + name);
                    i++;
                }
                System.out.println("0. Back");
                pickCar(nameList,idCustomer,nameCustomer);
            }else{
                System.out.println("The car list is empty!");
                printCustomerMenu(idCustomer,nameCustomer);
            }


            this.conn.close();
            stmt.close();
            System.out.println("\n");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void pickCar(ArrayList<String> nameList,int idCustomer,String nameCustomer) {
        Scanner inputId = new Scanner(System.in);
        this.conn = null;
        Statement stmt = null;
        String name = null;
        try{
            Integer id = inputId.nextInt();
            if(id==0) printCustomerMenu(idCustomer,nameCustomer);
            name = nameList.get(id-1);
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT id,name FROM CAR WHERE name = '"+name+"'";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){
                rs.beforeFirst();

                while(rs.next()){
                    id = rs.getInt("id");
                }

                sql = "UPDATE CUSTOMER SET rented_car_id = "+id+" WHERE id = "+idCustomer;
                stmt.executeUpdate(sql);
                this.conn.close();
                stmt.close();
                System.out.println("You rented '"+name+"'");
                printCustomerMenu(idCustomer,nameCustomer);
            }else{
                System.out.println("No such customer!");
                this.conn.close();
                stmt.close();
                printEntryMenu();
            }

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void showCustomers() {

        this.conn = null;
        Statement stmt = null;

        try{

            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT id, name FROM CUSTOMER";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()){

                System.out.println("Choose a customer:");
                rs.beforeFirst();
                while(rs.next()){
                    //Retrieve by column name
                    int id  = rs.getInt("id");
                    String name = rs.getString("name");

                    //Display values
                    System.out.print(id+". ");
                    System.out.println("" + name);
                }
                System.out.println("0. Back");
            }else{
                System.out.println("The customer list is empty!");
                printEntryMenu();
            }

            this.conn.close();
            stmt.close();
            System.out.println("\n");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public void createCustomer() {
        Scanner inputName = new Scanner(System.in);
        this.conn = null;
        Statement stmt = null;
        try{
            System.out.println("Enter the customer name:");
            String customerName = inputName.nextLine();
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement();
            String sql =  "INSERT INTO customer (name) VALUES"+"('"+customerName+"');"
                    ;
            stmt.executeUpdate(sql);
            this.conn.close();
            stmt.close();
            System.out.println("The customer was created!");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }

    public  void printCompanyMenu(){

        Scanner inputCommand = new Scanner(System.in);

        System.out.println("1. Company list");
        System.out.println("2. Create a company");
        System.out.println("0. Back\n");

        int command = inputCommand.nextInt();

        switch (command){
            case 0:
                printEntryMenu();
            case 1:
                printCompanyList();
                pickCompany();
            case 2:
                insertCompany();
                printCompanyMenu();
        }

    }


}



/**
 * During testing, data is added to the database,
 * then the database is completely cleared for a new test.
 * When new entries are added to the table, the indexes of
 * the new entries do not return to their initial values.
 * To fix this, before adding data to an empty table,
 * run the query: "ALTER TABLE company ALTER COLUMN id RESTART WITH 1"
 */


/**
 * СНАЧАЛА ВЫБОР КОМПАНИЙ (ВНОСИМ ВСЕ КОМПАНИИ В ARRAYLIST, И ВЫВОДИМ ВСЕ СВОБОДНЫЕ МАШИНЫ ПО SELECT ОТ ID ГДЕ  ПОТОМ ВЫБОР СВОБОДНЫХ МАШИН ПО ID КОМПАНИИ
 * ЛУЧШЕ ВСЕГО : ЗАНЕСТИ ВСЕ МАШИНЫ, ID КОТОРЫХ НЕ ИСПОЛЬЗОВАНО В CUSTOMER
 */


/**
 * Нужно выбрать все айди и имена из Кар, которых нет в выборке кар_айди в кастомерах,
 * потом в кастомер установить выбранный айди
 *
 * Выбрать по айди название машины, потом по компания_айди
 * выбрать название компании (вспомни ЮНИОН)
 *
 * Установить значение NULL в car_id
 */


/*

private void showAvailableCars(int idCustomer, String nameCustomer,ArrayList<String> companyList){
        this.conn = null;
        Statement stmt = null;
        ArrayList<String> nameList = new ArrayList<>();

        try{
            Class.forName(JDBC_DRIVER);
            this.conn = DriverManager.getConnection(this.db_url);
            this.conn.setAutoCommit(true);
            stmt = conn.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql =  "SELECT id,name FROM CAR WHERE id NOT IN(SELECT rented_car_id FROM CUSTOMER WHERE rented_car_id IS NOT NULL);";
            ResultSet rs = stmt.executeQuery(sql);
            int i = 1 ;
            if(rs.next()){
                System.out.println("Choose a car:");
                rs.beforeFirst();
                while(rs.next()){
                    //Retrieve by column name
                    int id  = rs.getInt("id");
                    String name = rs.getString("name");
                    nameList.add(name);


                    //Display values
                    System.out.print(i+". ");
                    System.out.println("" + name);
                    i++;
                }
                System.out.println("0. Back");
                pickCar(nameList,idCustomer,nameCustomer);
            }else{
                System.out.println("The car list is empty!");
                printCustomerMenu(idCustomer,nameCustomer);
            }


            this.conn.close();
            stmt.close();
            System.out.println("\n");

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

 */