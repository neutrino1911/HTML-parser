package ru.security59.parser;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class Parser {
    private static final String url = "jdbc:mysql://urgant/parser?useUnicode=true&characterEncoding=utf-8&useSSL=false";
    private static final String user = "script";
    private static final String password = "YLO617hxs662";

    private static Connection connection;
    static Statement statement;

    public static void main(String[] args) throws FileNotFoundException {
        PrintStream out = new PrintStream(new FileOutputStream("out.log", true));
        PrintStream dual = new DualStream(System.out, out);
        System.setOut(dual);

        PrintStream err = new PrintStream(new FileOutputStream("err.log", true));
        dual= new DualStream(System.err, err);
        System.setErr(dual);

        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(url, user, password);
            statement = connection.createStatement();
            if (args.length == 0) args = getArgs();

            switch (args[0].substring(0, 2)) {
                case "-p":
                    int firstTarget = Integer.parseInt(args[1]);
                    int lastTarget = firstTarget;
                    if (args.length > 2) lastTarget = Integer.parseInt(args[2]);
                    //****//
                    parseTargets(firstTarget, lastTarget);
                    //****//
                    break;
                case "-v":
                    int vendor = Integer.parseInt(args[1]);
                    parseVendor(vendor);
                    break;
                case "-w":
                    int firstVendor = Integer.parseInt(args[1]);
                    int lastVendor = firstVendor;
                    Exporter exporter = new Exporter();
                    if (args.length > 2) lastVendor = Integer.parseInt(args[2]);
                    if (args[0].length() == 2 || "a".equals(args[0].substring(2, 3))) {
                        exporter.write(0, firstVendor, lastVendor);
                        exporter.write(1, firstVendor, lastVendor);
                    }
                    else if ("t".equals(args[0].substring(2, 3)))
                        exporter.write(0, firstVendor, lastVendor);
                    else if ("u".equals(args[0].substring(2, 3)))
                        exporter.write(1, firstVendor, lastVendor);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { connection.close(); } catch(SQLException ignored) { }
            try { statement.close(); } catch(SQLException ignored) { }
        }
    }

    private static String[] getArgs() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] args = new String[0];
        try {
            System.out.println("Парсинг: -p target [lastTarget]");
            System.out.println("Парсинг: -v vendor");
            System.out.println("Импорт:  -w vendor [lastVendor]");
            System.out.print("Введите параметры: ");
            while (args.length == 0) {
                while (reader.ready())
                    args = reader.readLine().split(" ");
                Thread.sleep(100);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        try {reader.close();} catch (IOException ignored) {}
        return args;
    }

    private static void parseTargets(int[] targets) {
        String query = null;
        try {
            ResultSet resultSet;
            Shop shop;
            for (int index = 0; index < targets.length; index++) {
                query = String.format("CALL getTargetById(%d);", targets[index]);
                resultSet = statement.executeQuery(query);
                resultSet.next();
                Target target = new Target(
                        targets[index],
                        resultSet.getInt("cat_id"),
                        resultSet.getInt("last_id"),
                        resultSet.getInt("vend_id"),
                        resultSet.getString("currency"),
                        resultSet.getString("unit"),
                        resultSet.getString("url"),
                        resultSet.getString("vend_name"));

                String URI = resultSet.getString("url");
                String domain = URI.substring(7, URI.indexOf('/', 7));
                System.out.println(domain);
                switch (domain) {
                    case "sec-s.ru":
                        shop = new LiderSB();
                        break;
                    case "www.tinko.ru":
                        shop = new Tinko();
                        break;
                    case "shop.nag.ru":
                        shop = new Nag();
                        break;
                    default:
                        System.out.printf("Wrong target domain: %s id: %d\r\n", domain, targets[index]);
                        continue;
                }
                shop.parseItemByTarget(target, false, false);
            }
        } catch (SQLException e) {
            System.out.println(query);
            e.printStackTrace();
        }
    }

    private static void parseTargets(int firstTarget, int lastTarget) {
        int[] targets = new int[lastTarget - firstTarget + 1];
        for (int index = 0; index < targets.length; index++)
            targets[index] = firstTarget + index;
        parseTargets(targets);
    }

    private static void parseTargets(int target) {
        parseTargets(new int[]{target});
    }

    private static void parseVendor(int vendor) {
        String query = null;
        int[] targets = null;
        try {
            ResultSet resultSet;
            query = "SELECT COUNT(id) AS count FROM Targets WHERE vend_id=" + vendor + ";";
            resultSet = statement.executeQuery(query);
            resultSet.next();
            int size = resultSet.getInt("count");
            query = "SELECT id FROM Targets WHERE vend_id=" + vendor + ";";
            resultSet = statement.executeQuery(query);
            targets = new int[size];
            for (int index = 0; index < size; index++) {
                resultSet.next();
                targets[index] = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(query);
            e.printStackTrace();
        }
        if (targets != null) parseTargets(targets);
    }
}
