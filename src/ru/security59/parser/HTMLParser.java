package ru.security59.parser;

import org.apache.commons.cli.*;

import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

class HTMLParser {
    private static String db_url;
    private static String db_user;
    private static String db_pass;
    static String export_path;

    private static Connection connection;
    static Statement statement;

    public static void main(String[] args) throws FileNotFoundException {
        initLog();
        boolean isConfigLoaded = initConfig();
        if (!isConfigLoaded) return;

        Options options = new Options();
        options.addOption("t", "target", false, "Parse items by target");
        //options.addOption("u", "update", false, "Update items. Use only with -t");
        options.addOption("p", "prices", false, "Parse prices by target");
        options.addOption("e", "export", true, "Write export by vendor");
        options.addOption("vendor", false, "Parse items by vendor");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(db_url, db_user, db_pass);
            statement = connection.createStatement();

            if (cmd.hasOption("e")) export(cmd);
            else if (cmd.hasOption("p")) parsePrices(cmd);
            else if (cmd.hasOption("t")) parseTargets(cmd);
            else if (cmd.hasOption("vendor") && cmd.getArgList().size() > 0)
                parseVendor(Integer.parseInt(cmd.getArgList().get(0)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { connection.close(); } catch(SQLException ignored) {}
            try { statement.close(); } catch(SQLException ignored) {}
        }
    }

    private static Target getTarget(int targetId) {
        System.out.println("Target id: " + targetId);
        String query = String.format("CALL getTargetById(%d);", targetId);
        ResultSet resultSet = null;
        Target target = null;
        try {
            resultSet = statement.executeQuery(query);
            resultSet.next();
            target = new Target(
                    targetId,
                    resultSet.getInt("cat_id"),
                    resultSet.getInt("last_id"),
                    resultSet.getInt("vend_id"),
                    resultSet.getString("currency"),
                    resultSet.getString("unit"),
                    resultSet.getString("url"),
                    resultSet.getString("vend_name")
            );
        } catch (SQLException e) {
            System.out.println(query);
            e.printStackTrace();
        } finally {
            if (resultSet != null) try { resultSet.close(); } catch(SQLException ignored) {}
        }
        return target;
    }

    private static Shop getShop(String url) {
        int index = url.indexOf('/') + 2;
        String domain = url.substring(index, url.indexOf('/', index));
        System.out.println(domain);
        Shop shop = null;
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
            case "www.satro-paladin.com":
                shop = new SatroPaladin();
                break;
            case "bronegilet.ru":
                shop = new Avangard();
                break;
            default:
                System.out.println("Wrong target domain: " + domain);
        }
        return shop;
    }

    private static void export(CommandLine cmd) {
        List<String> arguments = cmd.getArgList();
        if (arguments.size() == 0) {
            System.out.println("Need more parameters");
            return;
        }
        int firstVendor;
        int lastVendor;
        firstVendor = Integer.parseInt(arguments.get(0));
        if (arguments.size() > 1) lastVendor = Integer.parseInt(arguments.get(1));
        else lastVendor = firstVendor;
        Exporter exporter = new Exporter();
        switch (cmd.getOptionValue("e")) {
            case "a":
                exporter.write(0, firstVendor, lastVendor);
                exporter.write(1, firstVendor, lastVendor);
                break;
            case "t":
                exporter.write(0, firstVendor, lastVendor);
                break;
            case "u":
                exporter.write(1, firstVendor, lastVendor);
            default:
                System.out.println("Wrong export parameter");
        }
    }

    private static void parseTargets(int... targets) {
        Target target;
        Shop shop;
        for (int targetId : targets) {
            target = getTarget(targetId);
            shop = getShop(target.getUrl());
            if (shop == null) continue;
            try {
                shop.parseItems(target, true, false);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parseTargets(int firstTarget, int lastTarget) {
        int[] targets = new int[lastTarget - firstTarget + 1];
        for (int index = 0; index < targets.length; index++)
            targets[index] = firstTarget + index;
        parseTargets(targets);
    }

    private static void parseTargets(CommandLine cmd) {
        List<String> arguments = cmd.getArgList();
        int firstTarget;
        int lastTarget;
        firstTarget = Integer.parseInt(arguments.get(0));
        if (arguments.size() > 1) lastTarget = Integer.parseInt(arguments.get(1));
        else lastTarget = firstTarget;
        parseTargets(firstTarget, lastTarget);
    }

    private static void parsePrices(int... targets) {
        Target target;
        Shop shop;
        for (int targetId : targets) {
            target = getTarget(targetId);
            shop = getShop(target.getUrl());
            if (shop == null) continue;
            try {
                shop.parsePricesByTarget(target);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parsePrices(int firstTarget, int lastTarget) {
        int[] targets = new int[lastTarget - firstTarget + 1];
        for (int index = 0; index < targets.length; index++)
            targets[index] = firstTarget + index;
        parsePrices(targets);
    }

    private static void parsePrices(CommandLine cmd) {
        List<String> arguments = cmd.getArgList();
        int firstTarget;
        int lastTarget;
        firstTarget = Integer.parseInt(arguments.get(0));
        if (arguments.size() > 1) lastTarget = Integer.parseInt(arguments.get(1));
        else lastTarget = firstTarget;
        parsePrices(firstTarget, lastTarget);
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

    private static void initLog() throws FileNotFoundException {
        PrintStream out = new PrintStream(new FileOutputStream("out/out.log", true));
        PrintStream dual = new DualStream(System.out, out);
        System.setOut(dual);

        PrintStream err = new PrintStream(new FileOutputStream("out/err.log", true));
        dual= new DualStream(System.err, err);
        System.setErr(dual);

        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
    }

    private static boolean initConfig() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        db_user = properties.getProperty("db_user");
        db_pass = properties.getProperty("db_pass");
        db_url = String.format(
                "jdbc:mysql://%s:%s/parser?useUnicode=true&characterEncoding=utf-8&useSSL=false",
                properties.getProperty("db_host"),
                properties.getProperty("db_port")
        );
        export_path = properties.getProperty("export_path");
        return true;
    }
}
