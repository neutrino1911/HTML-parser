package ru.security59.parser;

import org.apache.commons.cli.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.security59.parser.entities.Target;
import ru.security59.parser.shops.*;
import ru.security59.parser.util.DualStream;
import ru.security59.parser.util.Exporter;

import javax.persistence.EntityManager;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class HTMLParser {
    public static String export_path;
    public static boolean loadImages = true;
    public static boolean simulation = true;
    public static EntityManager entityManager;

    public static void main(String[] args) throws FileNotFoundException {
        initLog();
        if (!initConfig()) return;
        CommandLine cmd = initCMD(args);
        if (cmd == null) return;
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        entityManager = sessionFactory.createEntityManager();

        if (cmd.hasOption("e")) export(cmd);
        else if (cmd.hasOption("p")) parsePrices(cmd);
        else if (cmd.hasOption("t")) parseTargets(cmd);
        else if (cmd.hasOption("vendor") && cmd.getArgList().size() > 0)
            parseVendor(Integer.parseInt(cmd.getArgList().get(0)));

        entityManager.close();
        sessionFactory.close();

        /*
        initLog();
        if (!initConfig()) return;
        CommandLine cmd = initCMD(args);
        if (cmd == null) return;

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
        */
    }

    private static Target getTarget(int targetId) {
        System.out.println("Target id: " + targetId);
        return entityManager.find(Target.class, targetId);
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
            if (target == null) continue;
            shop = getShop(target.getUrl());
            if (shop == null) continue;
            shop.parseItems(target);
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
        if (arguments.size() > 1)
            lastTarget = Integer.parseInt(arguments.get(1));
        else lastTarget = firstTarget;
        parseTargets(firstTarget, lastTarget);
    }

    private static void parsePrices(int... targets) {
        Target target;
        Shop shop;
        for (int targetId : targets) {
            target = getTarget(targetId);
            if (target == null) continue;
            shop = getShop(target.getUrl());
            if (shop == null) continue;
            shop.parsePricesByTarget(target);
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
        /*String query = null;
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
        if (targets != null) parseTargets(targets);*/
    }

    private static void initLog() throws FileNotFoundException {
        PrintStream out = new PrintStream(new FileOutputStream("out.log", true));
        System.setOut(new DualStream(System.out, out));

        PrintStream err = new PrintStream(new FileOutputStream("err.log", true));
        System.setErr(new DualStream(System.err, err));

        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
    }

    private static boolean initConfig() {
        Properties properties = new Properties();
        try {
            properties.load(HTMLParser.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        export_path = properties.getProperty("export_path");
        return true;
    }

    private static CommandLine initCMD(String[] args) {
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
            return null;
        }
        return cmd;
    }
}
