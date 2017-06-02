package ru.security59.parser;

import org.apache.commons.cli.*;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.security59.parser.entities.Target;
import ru.security59.parser.entities.Target_;
import ru.security59.parser.entities.Vendor;
import ru.security59.parser.shops.*;
import ru.security59.parser.util.DualStream;
import ru.security59.parser.util.Exporter;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class HTMLParser {
    public static String export_path;
    public static final boolean LOAD_IMAGES = true;
    public static final boolean SIMULATION = false;
    public static EntityManager entityManager;
    public static CriteriaBuilder criteriaBuilder;

    public static void main(String[] args) {
        if (!initLog()) return;
        if (!initConfig()) return;
        CommandLine cmd = initCMD(args);
        if (cmd == null) return;

        /*Configuration configuration = new Configuration();
        EntityScanner.scanPackages("ru.security59.parser.entities").addTo(configuration);
        SessionFactory sessionFactory = configuration.buildSessionFactory();*/

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        entityManager = sessionFactory.createEntityManager();
        criteriaBuilder = entityManager.getCriteriaBuilder();

        if (cmd.hasOption("e"))
            export(cmd);
        else if (cmd.hasOption("p"))
            parsePrices(getTargets(cmd));
        else if (cmd.hasOption("t"))
            parseProducts(getTargets(cmd));
        else if (cmd.hasOption("vendor") && cmd.getArgList().size() > 0)
            parseVendor(Integer.parseInt(cmd.getArgList().get(0)));

        entityManager.close();
        sessionFactory.close();
    }

    private static void parseProducts(List<Target> targets) {
        AbstractShop shop;
        for (Target target : targets) {
            shop = getShop(target.getUrl());
            if (shop == null) continue;
            shop.parseItems(target);
        }
    }

    private static void parsePrices(List<Target> targets) {
        AbstractShop shop;
        for (Target target : targets) {
            shop = getShop(target.getUrl());
            if (shop == null) continue;
            shop.parsePrices(target);
        }
    }

    private static void parseVendor(int vendorId) {
        CriteriaQuery<Target> criteria = criteriaBuilder.createQuery(Target.class);
        Root<Target> root = criteria.from(Target.class);
        criteria.select(root);
        Vendor vendor = new Vendor();
        vendor.setId(vendorId);
        criteria.where(criteriaBuilder.equal(root.get(Target_.vendor), vendor));
        List<Target> targets = entityManager.createQuery(criteria).getResultList();
        parseProducts(targets);
    }

    private static List<Target> getTargets(CommandLine cmd) {
        List<String> arguments = cmd.getArgList();
        int firstTargetId;
        int lastTargetId;
        firstTargetId = Integer.parseInt(arguments.get(0));
        if (arguments.size() > 1) {
            lastTargetId = Integer.parseInt(arguments.get(1));
        } else {
            lastTargetId = firstTargetId;
        }
        CriteriaQuery<Target> criteria = criteriaBuilder.createQuery(Target.class);
        Root<Target> root = criteria.from(Target.class);
        criteria.select(root);
        criteria.where(criteriaBuilder.between(root.get(Target_.id), firstTargetId, lastTargetId));
        return entityManager.createQuery(criteria).getResultList();
    }

    private static AbstractShop getShop(String url) {
        int index = url.indexOf('/') + 2;
        String domain = url.substring(index, url.indexOf('/', index));
        System.out.println(domain);
        AbstractShop shop = null;
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

    private static boolean initLog() {
        try {
            PrintStream out = new PrintStream(new FileOutputStream("out.log", true));
            System.setOut(new DualStream(System.out, out));

            PrintStream err = new PrintStream(new FileOutputStream("err.log", true));
            System.setErr(new DualStream(System.err, err));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        System.out.println(dateFormat.format(new Date()));
        return true;
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
