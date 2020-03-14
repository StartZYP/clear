//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package clear;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Main extends JavaPlugin implements Listener {
    private static final Pattern PLAIN_PATTERN = Pattern.compile("lang_\\d{1,5}");
    private static final Pattern FORMAT_PATTERN = Pattern.compile("lang-\\d{1,5}");
    private String pn;
    private BukkitScheduler scheduler;
    private String mainPath;
    private String pluginPath;
    private String dataFolder;
    private String pluginVersion;
    private Tps tps;
    private Names names;
    private Time time;
    private ServerManager serverManager;
    private Clear clear;
    private RedStone redStone;
    private Crop crop;
    private Liquid liquid;
    private HashMap<Integer, String> plainHash;
    private HashMap<String, String> formatHash;
    private List<Pattern> filter = new ArrayList();

    public Main() {
    }

    public void onEnable() {
        this.initBasic();
        this.initConfig();
        this.tps = new Tps(this);
        this.names = new Names(this);
        this.time = new Time(this);
        this.serverManager = new ServerManager(this);
        this.redStone = new RedStone(this);
        this.crop = new Crop(this);
        this.liquid = new Liquid(this);
        this.clear = new Clear(this);
        this.loadConfig();
        sendConsoleMessage(this.format("pluginEnabled", this.pn, this.pluginVersion));
    }

    public void onDisable() {
        if (this.scheduler != null) {
            this.scheduler.cancelAllTasks();
        }

        sendConsoleMessage(this.format("pluginDisabled", this.pn, this.pluginVersion));
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p = null;
        if (sender instanceof Player) {
            p = (Player)sender;
        }

        String cmdName = cmd.getName();
        int length = args.length;

        try {
            if (cmdName.equalsIgnoreCase("clear")) {
                if (p != null && !p.isOp()) {
                    p.sendMessage(this.get(5));
                    return true;
                }

                if (length != 1 || !args[0].equalsIgnoreCase("?")) {
                    if (length == 1) {
                        if (args[0].equalsIgnoreCase("reload")) {
                            this.loadConfig();
                            sender.sendMessage(this.get(7));
                            return true;
                        }

                        if (args[0].equalsIgnoreCase("info")) {
                            this.clear.info(sender);
                            return true;
                        }

                        if (args[0].equalsIgnoreCase("start")) {
                            this.clear.clear(true, -1);
                            return true;
                        }
                    } else if (length == 2 && args[0].equalsIgnoreCase("start")) {
                        this.clear.clear(true, Integer.parseInt(args[1]));
                        return true;
                    }
                }

                sender.sendMessage(this.format("cmdHelpHeader", this.get(10)));
                sender.sendMessage(this.format("cmdHelpItem", this.get(15), this.get(20)));
                sender.sendMessage(this.format("cmdHelpItem", this.get(1215), this.get(1220)));
                sender.sendMessage(this.format("cmdHelpItem", this.get(1225), this.get(1230)));
            }
        } catch (NumberFormatException var9) {
            sender.sendMessage(this.format("fail", this.get(190)));
        }

        return true;
    }

    public static double getDouble(double num, int accuracy) {
        if (accuracy < 0) {
            accuracy = 0;
        }

        String s = String.valueOf(num);
        if (s.split("\\.").length == 2) {
            String[] ss = s.split("\\.");
            return Double.parseDouble(ss[0] + "." + ss[1].substring(0, Math.min(accuracy, ss[1].length())));
        } else {
            return num;
        }
    }

    public static String convert(String s) {
        if (s == null) {
            return null;
        } else {
            s = s.replace("//", "\u0001");
            s = s.replace("/&", "\u0002");
            s = s.replace("&", String.valueOf('§'));
            s = s.replace("\u0002", "&");
            s = s.replace("\u0001", "/");
            return s;
        }
    }

    public static String convertBr(String s) {
        if (s == null) {
            return null;
        } else {
            s = s.replace("\n ", "\n");
            return s;
        }
    }

    public static void sendConsoleMessage(String msg) {
        try {
            if (Bukkit.getConsoleSender() != null) {
                Bukkit.getConsoleSender().sendMessage(msg);
            } else {
                Bukkit.getLogger().info(msg);
            }
        } catch (Exception var2) {
            System.out.println(msg);
        }

    }

    public static String getPluginVersion(File plugin) {
        return "2.3";
    }

    public static YamlConfiguration loadConfigByUTF8(File file) {
        YamlConfiguration config = new YamlConfiguration();

        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charset.forName("utf-8"));
            StringBuilder builder = new StringBuilder();
            BufferedReader input = new BufferedReader(reader);

            String line;
            try {
                while((line = input.readLine()) != null) {
                    builder.append(line);
                    builder.append('\n');
                }
            } finally {
                input.close();
            }

            config.loadFromString(builder.toString());
            return config;
        } catch (FileNotFoundException var12) {
            return null;
        } catch (IOException var13) {
            return null;
        } catch (InvalidConfigurationException var14) {
            return null;
        }
    }

    public static boolean generateFiles(File sourceJarFile, String destPath, List<Pattern> filter) {
        JarInputStream jis = null;
        FileOutputStream fos = null;

        try {
            (new File(destPath)).mkdirs();
            jis = new JarInputStream(new FileInputStream(sourceJarFile));
            byte[] buff = new byte[1024];

            JarEntry entry;
            label197:
            while((entry = jis.getNextJarEntry()) != null) {
                String fileName = entry.getName();
                Iterator var10 = filter.iterator();

                while(true) {
                    Matcher matcher;
                    do {
                        do {
                            if (!var10.hasNext()) {
                                continue label197;
                            }

                            Pattern pattern = (Pattern)var10.next();
                            matcher = pattern.matcher(fileName);
                        } while(!matcher.find());
                    } while((new File(destPath + File.separator + fileName)).exists());

                    fos = new FileOutputStream(destPath + File.separator + fileName);

                    int read;
                    while((read = jis.read(buff)) > 0) {
                        fos.write(buff, 0, read);
                    }

                    fos.close();
                }
            }

            return true;
        } catch (FileNotFoundException var27) {
            return false;
        } catch (IOException var28) {
        } finally {
            try {
                if (jis != null) {
                    jis.close();
                }
            } catch (IOException var25) {
                return false;
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException var26) {
                return false;
            }

        }

        return false;
    }

    public String get(int id) {
        try {
            return (String)this.plainHash.get(id);
        } catch (Exception var3) {
            return "";
        }
    }

    public String format(String type, Object... args) {
        String result = (String)this.formatHash.get(type);
        int i;
        if (result != null) {
            for(i = 0; i < args.length; ++i) {
                if (args[i] == null) {
                    args[i] = "";
                }

                result = result.replace("{" + i + "}", args[i].toString());
            }

            return result;
        } else {
            result = (String)this.formatHash.get(type);
            if (result != null) {
                for(i = 0; i < args.length; ++i) {
                    if (args[i] == null) {
                        args[i] = "";
                    }

                    result = result.replace("{" + i + "}", args[i].toString());
                }

                return result;
            } else {
                return "";
            }
        }
    }

    public String getPn() {
        return this.pn;
    }

    public ServerManager getServerManager() {
        return this.serverManager;
    }

    public RedStone getRedStone() {
        return this.redStone;
    }

    public Crop getCrop() {
        return this.crop;
    }

    public String getMainPath() {
        return this.mainPath;
    }

    public Liquid getLiquid() {
        return this.liquid;
    }

    public String getPluginPath() {
        return this.pluginPath;
    }

    public Time getTime() {
        return this.time;
    }

    public Tps getTps() {
        return this.tps;
    }

    private void initBasic() {
        this.pn = this.getName();
        this.scheduler = Bukkit.getScheduler();
        this.mainPath = System.getProperty("user.dir");
        this.pluginPath = this.getFile().getParentFile().getAbsolutePath();
        this.dataFolder = this.pluginPath + File.separator + this.pn;
        this.pluginVersion = getPluginVersion(this.getFile());
    }

    private void initConfig() {
        this.filter.add(Pattern.compile("config.yml"));
        this.filter.add(Pattern.compile("config_[a-zA-Z]+.yml"));
        this.filter.add(Pattern.compile("language.yml"));
        this.filter.add(Pattern.compile("language_[a-zA-Z]+.yml"));
        this.filter.add(Pattern.compile("hibernate.cfg.xml"));
        this.filter.add(Pattern.compile("names.yml"));
    }

    private void loadConfig() {
        generateFiles(new File(this.pluginPath + File.separator + this.pn + ".jar"), this.dataFolder, this.filter);
        YamlConfiguration config = loadConfigByUTF8(new File(this.getPluginPath() + File.separator + this.pn + File.separator + "config.yml"));
        if (config != null) {
            this.loadConfig0();
            if (this.clear != null) {
                this.clear.loadConfig(config);
            }

            if (this.crop != null) {
                this.crop.loadConfig(config);
            }

            if (this.liquid != null) {
                this.liquid.loadConfig(config);
            }

            if (this.names != null) {
                this.names.loadConfig();
            }

            if (this.redStone != null) {
                this.redStone.loadConfig(config);
            }

            if (this.serverManager != null) {
                this.serverManager.loadConfig(config);
            }
        }

    }

    private void loadConfig0() {
        this.plainHash = new HashMap();
        this.formatHash = new HashMap();
        YamlConfiguration languageConfig = loadConfigByUTF8(new File(this.pluginPath + File.separator + this.pn + File.separator + "language.yml"));
        if (languageConfig != null) {
            Iterator var5 = languageConfig.getKeys(true).iterator();

            while(var5.hasNext()) {
                String key = (String)var5.next();
                if (PLAIN_PATTERN.matcher(key).matches()) {
                    this.plainHash.put(this.getId(key), convertBr(convert(languageConfig.getString(key))));
                } else if (FORMAT_PATTERN.matcher(key).matches()) {
                    String s = languageConfig.getString(key);
                    int index = s.indexOf(":");
                    if (index < 1) {
                        return;
                    }

                    String name = s.substring(0, index);
                    if (name.isEmpty()) {
                        return;
                    }

                    if (index == -1) {
                        return;
                    }

                    String value = convertBr(convert(s.substring(index + 1, s.length())));
                    this.formatHash.put(name, value);
                }
            }
        }

    }

    private int getId(String s) {
        try {
            for(int i = 0; i < s.length(); ++i) {
                if (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
                    return Integer.parseInt(s.substring(i, s.length()));
                }
            }
        } catch (NumberFormatException var3) {
        }

        return -1;
    }
}
