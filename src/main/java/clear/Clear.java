//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package clear;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SpawnEggMeta;

public class Clear implements Listener {
    private static final int DELAY_SHOW = 35;
    private static final int CHEST_ID = 54;
    private Random r = new Random();
    private Main main;
    private Server server;
    private ServerManager serverManager;
    private boolean tip;
    private HashMap<String, Boolean> ignoreWorlds;
    private int checkInterval;
    private int startClearEntitys;
    private int mustClearAmount;
    private int mustClearLevel;
    private List<Clear.Level> levelList;
    private int gridSize;
    private HashMap<Integer, Boolean> clearList;
    private int ticksLived;
    private int clearMode;
    private HashMap<Integer, Boolean> clearWhite;
    private HashMap<Integer, Boolean> clearBlack;
    private boolean ske;
    private HashMap<Short, Boolean> clearMonsterList;
    private int maxPerGrid;
    private boolean firstAll;
    private int heightMax;
    private int heightMin;
    private HashMap<Integer, Integer> clearTypes;
    private Clear.ClearTimer clearTimer;
    private HashMap<Integer, Boolean> airBlocks;

    public Clear(Main main) {
        this.main = main;
        this.server = main.getServer();
        this.serverManager = main.getServerManager();
        this.clearTimer = new Clear.ClearTimer();
        main.getServer().getScheduler().scheduleSyncDelayedTask(main, this.clearTimer, (long)(this.checkInterval * 20));
    }

    public void info(CommandSender sender) {
        HashMap<String, Integer> entityHash = new HashMap<>();
        int total = 0;
        Iterator var6 = this.server.getWorlds().iterator();

        while(var6.hasNext()) {
            World w = (World)var6.next();
            List<Entity> list = w.getEntities();
            total += list.size();

            Entity e;
            for(Iterator<Entity> var8 = list.iterator(); var8.hasNext(); entityHash.put(e.getType().getName(), (Integer)entityHash.get(e.getType().getName()) + 1)) {
                e = var8.next();
                if (!entityHash.containsKey(e.getType().getName())) {
                    entityHash.put(e.getType().getName(), 0);
                }
            }
        }

        sender.sendMessage(this.main.format("success", new Object[]{this.get(1200)}));

        String s;
        String name;
        for(var6 = entityHash.keySet().iterator(); var6.hasNext(); sender.sendMessage(this.main.format("broadcastInfo", new Object[]{name, entityHash.get(s)}))) {
            s = (String)var6.next();
            name = "";

            try {
                name = Names.getEntityName(EntityType.fromName(s).getTypeId());
            } catch (Exception var9) {
            }
        }

        sender.sendMessage(this.main.format("clearInfo2", new Object[]{total}));
    }

    public void clear(boolean force, int clearLevel) {
        HashMap<Short, Integer> startHash = new HashMap<>();
        int startTotal = 0;
        Iterator<World> var7 = this.server.getWorlds().iterator();

        World w;
        while(var7.hasNext()) {
            w = var7.next();
            List<Entity> list = w.getEntities();
            startTotal += list.size();

            Entity e;
            for(Iterator<Entity> var9 = list.iterator(); var9.hasNext(); startHash.put(e.getType().getTypeId(), (Integer)startHash.get(e.getType().getTypeId()) + 1)) {
                e = var9.next();
                if (!startHash.containsKey(e.getType().getTypeId())) {
                    startHash.put(e.getType().getTypeId(), 0);
                }
            }
        }

        if (startTotal >= this.mustClearAmount) {
            clearLevel = this.mustClearLevel;
        } else {
            if (!force && this.serverManager.getServerStatus() == 0) {
                return;
            }

            if (!force && startTotal < this.startClearEntitys) {
                return;
            }
        }

        if (clearLevel == -1) {
            clearLevel = this.serverManager.getServerStatus();
        } else if (clearLevel < 0) {
            clearLevel = 0;
        } else if (clearLevel > 3) {
            clearLevel = 3;
        }

        this.server.broadcastMessage(this.main.format("success", new Object[]{this.get(1205)}));
        this.server.broadcastMessage(this.main.format("clearLevel", new Object[]{((Clear.Level)this.levelList.get(clearLevel)).getShow()}));
        Iterator it;
        if (((Clear.Level)this.levelList.get(clearLevel)).isEntity()) {
            if (this.tip) {
                this.server.broadcastMessage(this.get(1295) + this.get(1890));
            } else {
                Main.sendConsoleMessage(this.get(1295) + this.get(1890));
            }

            var7 = this.server.getWorlds().iterator();

            label235:
            while(true) {
                do {
                    if (!var7.hasNext()) {
                        break label235;
                    }

                    w = var7.next();
                } while(this.ignoreWorlds.containsKey(w.getName()));

                it = w.getEntities().iterator();

                while(it.hasNext()) {
                    Entity e = (Entity)it.next();

                    try {
                        int id = e.getType().getTypeId();
                        if (id == 1) {
                            Item item = (Item)e;
                            if (item.getTicksLived() >= this.ticksLived) {
                                ItemStack is = item.getItemStack();
                                int itemId = is.getTypeId();
                                if (this.clearMode == 1 && !this.clearWhite.containsKey(itemId) || this.clearMode == 2 && this.clearBlack.containsKey(itemId)) {
                                    e.remove();
                                    it.remove();
                                }
                            }
                        } else if (this.clearList.containsKey(Integer.valueOf(id))) {
                            e.remove();
                            it.remove();
                        }
                    } catch (Exception var23) {
                    }
                }
            }
        } else if (this.tip) {
            this.server.broadcastMessage(this.get(1295) + this.get(1891));
        } else {
            Main.sendConsoleMessage(this.get(1295) + this.get(1891));
        }

        if (((Clear.Level)this.levelList.get(clearLevel)).isMonster()) {
            if (this.tip) {
                this.server.broadcastMessage(this.get(1305) + this.get(1890));
            } else {
                Main.sendConsoleMessage(this.get(1305) + this.get(1890));
            }

            var7 = this.server.getWorlds().iterator();

            label204:
            while(true) {
                do {
                    if (!var7.hasNext()) {
                        break label204;
                    }

                    w = var7.next();
                } while(this.ignoreWorlds.containsKey(w.getName()));

                it = w.getEntitiesByClass(Monster.class).iterator();

                while(true) {
                    Monster mon;
                    do {
                        do {
                            if (!it.hasNext()) {
                                continue label204;
                            }

                            mon = (Monster)it.next();
                        } while(!this.clearMonsterList.containsKey(mon.getType().getTypeId()));
                    } while(this.ske && mon.getType().equals(EntityType.SKELETON) && w.getEnvironment().equals(Environment.NETHER));

                    mon.remove();
                    it.remove();
                }
            }
        } else if (this.tip) {
            this.server.broadcastMessage(this.get(1305) + this.get(1891));
        } else {
            Main.sendConsoleMessage(this.get(1305) + this.get(1891));
        }

        if (((Clear.Level)this.levelList.get(clearLevel)).isAnimal()) {
            if (this.tip) {
                this.server.broadcastMessage(this.get(1310) + this.get(1890));
            } else {
                Main.sendConsoleMessage(this.get(1310) + this.get(1890));
            }

            Iterator<World> var32 = this.server.getWorlds().iterator();

            label176:
            while(true) {
                HashMap<Integer, HashMap> locHash;
                HashMap<Integer, HashMap> amountHash;
                HashMap<Integer, HashMap> generateHash;
                do {
                    if (!var32.hasNext()) {
                        break label176;
                    }

                    w = var32.next();
                    amountHash = new HashMap<>();
                    generateHash = new HashMap<>();
                    locHash = new HashMap<>();
                } while(this.ignoreWorlds.containsKey(w.getName()));

                it = w.getEntitiesByClass(Animals.class).iterator();

                while(it.hasNext()) {
                    Animals animals = (Animals)it.next();
                    int id = animals.getType().getTypeId();
                    if (this.clearTypes.containsKey(Integer.valueOf(id))) {
                        int x = animals.getLocation().getBlockX() / this.gridSize;
                        int z = animals.getLocation().getBlockZ() / this.gridSize;
                        if (!amountHash.containsKey(x)) {
                            amountHash.put(x, new HashMap());
                        }

                        if (!(amountHash.get(x)).containsKey(z)) {
                            (amountHash.get(x)).put(z, 0);
                        }

                        int current;
                        if ((current = (Integer)(amountHash.get(x)).get(z)) >= this.maxPerGrid) {
                            animals.remove();
                            it.remove();
                            if (this.r.nextInt(1000) < (Integer)this.clearTypes.get(Integer.valueOf(id))) {
                                if (!generateHash.containsKey(x)) {
                                    generateHash.put(x, new HashMap());
                                }

                                if (!(generateHash.get(x)).containsKey(z)) {
                                    (generateHash.get(x)).put(z, new HashMap());
                                }

                                if (!((HashMap)(generateHash.get(x)).get(z)).containsKey(Integer.valueOf(id))) {
                                    ((HashMap)(generateHash.get(x)).get(z)).put(Integer.valueOf(id), 0);
                                }

                                ((HashMap)(generateHash.get(x)).get(z)).put(id, (Integer)((HashMap)(generateHash.get(x)).get(z)).get(Integer.valueOf(id)) + 1);
                                if (!locHash.containsKey(x)) {
                                    locHash.put(x, new HashMap());
                                }

                                (locHash.get(x)).put(z, animals.getLocation());
                            }
                        } else {
                            (amountHash.get(x)).put(z, current + 1);
                        }
                    }
                }

                Iterator<Integer> var18 = generateHash.keySet().iterator();

                while(var18.hasNext()) {
                    int x2 = var18.next();
                    Iterator var20 = (generateHash.get(x2)).keySet().iterator();

                    while(var20.hasNext()) {
                        int z2 = (Integer)var20.next();
                        try {
                            this.checkGenerateChest(w, x2, z2, (HashMap<Integer, Integer>)(generateHash.get(x2)).get(z2), (Location)(locHash.get(x2)).get(z2));
                        } catch (Exception var22) {
                        }
                    }
                }
            }
        } else if (this.tip) {
            this.server.broadcastMessage(this.get(1310) + this.get(1891));
        } else {
            Main.sendConsoleMessage(this.get(1310) + this.get(1891));
        }

        this.server.getScheduler().scheduleSyncDelayedTask(this.main, new Clear.DelayShow(startHash, startTotal), 35L);
    }

    public void loadConfig(YamlConfiguration config) {
        this.tip = config.getBoolean("clear.tip");
        this.ignoreWorlds = new HashMap<>();
        Iterator<String> var3 = config.getStringList("clear.ignoreWorlds").iterator();

        String show;
        while(var3.hasNext()) {
            show = var3.next();
            this.ignoreWorlds.put(show, true);
        }

        this.checkInterval = config.getInt("clear.checkInterval");
        this.startClearEntitys = config.getInt("clear.startClearEntitys");
        this.mustClearAmount = config.getInt("clear.mustClear.amount");
        this.mustClearLevel = config.getInt("clear.mustClear.level");
        this.levelList = new ArrayList<>();
        String[] var9;
        int id = (var9 = new String[]{"unknown", "good", "fine", "bad"}).length;

        String s;
        for(int var7 = 0; var7 < id; ++var7) {
            s = var9[var7];
            show = Main.convert(config.getString("clear.clear." + s + ".show"));
            boolean entity = config.getBoolean("clear.clear." + s + ".entity");
            boolean monster = config.getBoolean("clear.clear." + s + ".monster");
            boolean animal = config.getBoolean("clear.clear." + s + ".animal");
            Clear.Level level = new Clear.Level(show, entity, monster, animal);
            this.levelList.add(level);
        }

        this.clearList = new HashMap<>();
        Iterator var13 = config.getIntegerList("clear.entity.clear").iterator();

        int i;
        while(var13.hasNext()) {
            i = (Integer)var13.next();
            this.clearList.put(i, true);
        }

        this.ticksLived = config.getInt("clear.entity.items.ticksLived");
        this.clearMode = config.getInt("clear.entity.items.mode");
        this.clearWhite = new HashMap<>();
        this.clearBlack = new HashMap<>();
        var13 = config.getIntegerList("clear.entity.items.white").iterator();

        while(var13.hasNext()) {
            i = (Integer)var13.next();
            this.clearWhite.put(i, true);
        }

        var13 = config.getIntegerList("clear.entity.items.black").iterator();

        while(var13.hasNext()) {
            i = (Integer)var13.next();
            this.clearBlack.put(i, true);
        }

        this.ske = config.getBoolean("clear.monster.ske");
        this.clearMonsterList = new HashMap<>();
        var13 = config.getIntegerList("clear.monster.clear").iterator();

        while(var13.hasNext()) {
            i = (Integer)var13.next();
            this.clearMonsterList.put((short)i, true);
        }

        this.gridSize = config.getInt("clear.animal.gridSize");
        this.maxPerGrid = config.getInt("clear.animal.maxPerGrid");
        this.firstAll = config.getBoolean("clear.animal.firstAll");
        this.heightMax = config.getInt("clear.animal.heightMax");
        this.heightMin = config.getInt("clear.animal.heightMin");
        this.clearTypes = new HashMap<>();
        var13 = config.getStringList("clear.animal.clearTypes").iterator();

        while(var13.hasNext()) {
            s = (String)var13.next();
            id = Integer.parseInt(s.split(" ")[0]);
            int chance = Integer.parseInt(s.split(" ")[1]);
            this.clearTypes.put(id, chance);
        }

        this.airBlocks = new HashMap<>();
        var13 = config.getIntegerList("clear.animal.airBlocks").iterator();

        while(var13.hasNext()) {
            i = (Integer)var13.next();
            this.airBlocks.put(i, true);
        }

    }

    private void checkGenerateChest(World w, int x, int z, HashMap<Integer, Integer> hash, Location l) {
        int xx;
        int zz;
        int yy;
        Chest chest;
        Inventory inventory;
        int id;
        Iterator<Integer> var12;
        if (this.firstAll) {
            for(xx = l.getBlockY() - this.heightMin; xx <= l.getBlockY() + this.heightMax; ++xx) {
                for(zz = x * this.gridSize; zz < x * (this.gridSize + 1); ++zz) {
                    for(yy = z * this.gridSize; yy < z * (this.gridSize + 1); ++yy) {
                        if (w.getBlockTypeIdAt(zz, xx, yy) == 54) {
                            chest = (Chest)w.getBlockAt(zz, xx, yy).getState();
                            inventory = chest.getBlockInventory();
                            var12 = hash.keySet().iterator();

                            while(var12.hasNext()) {
                                id = var12.next();
                                ItemStack itemStack = new ItemStack(Material.MONSTER_EGG, hash.get(id), (short) id);
                                SpawnEggMeta meta = (SpawnEggMeta) itemStack.getItemMeta();
                                meta.setSpawnedType(EntityType.fromId(id));
                                itemStack.setItemMeta(meta);
                                inventory.addItem(itemStack);
                            }
                            return;
                        }
                    }
                }
            }
        }

        xx = l.getBlockX();
        zz = l.getBlockZ();
        if (this.airBlocks.containsKey(w.getBlockAt(xx, l.getBlockY(), zz).getTypeId())) {
            for(yy = l.getBlockY() - 1; yy > 0; --yy) {
                if (!this.airBlocks.containsKey(w.getBlockAt(xx, yy, zz).getTypeId())) {
                    if (w.getBlockAt(xx, yy, zz).getTypeId() != 54) {
                        ++yy;
                        w.getBlockAt(xx, yy, zz).setTypeId(54);
                    }

                    chest = (Chest)w.getBlockAt(xx, yy, zz).getState();
                    inventory = chest.getBlockInventory();
                    var12 = hash.keySet().iterator();

                    while(var12.hasNext()) {
                        id = var12.next();
                        ItemStack itemStack = new ItemStack(Material.MONSTER_EGG, hash.get(id), (short) id);
                        SpawnEggMeta meta = (SpawnEggMeta) itemStack.getItemMeta();
                        meta.setSpawnedType(EntityType.fromId(id));
                        itemStack.setItemMeta(meta);
                        inventory.addItem(itemStack);
                    }

                    return;
                }
            }

            for(yy = 254; yy > l.getBlockY(); --yy) {
                if (!this.airBlocks.containsKey(w.getBlockAt(xx, yy, zz).getTypeId())) {
                    if (w.getBlockAt(xx, yy, zz).getTypeId() != 54) {
                        ++yy;
                        w.getBlockAt(xx, yy, zz).setTypeId(54);
                    }

                    chest = (Chest)w.getBlockAt(xx, yy, zz).getState();
                    inventory = chest.getBlockInventory();
                    var12 = hash.keySet().iterator();

                    while(var12.hasNext()) {
                        id = var12.next();
                        ItemStack itemStack = new ItemStack(Material.MONSTER_EGG, hash.get(id), (short) id);
                        SpawnEggMeta meta = (SpawnEggMeta) itemStack.getItemMeta();
                        meta.setSpawnedType(EntityType.fromId(id));
                        itemStack.setItemMeta(meta);
                        inventory.addItem(itemStack);
                    }

                    return;
                }
            }
        } else {
            for(yy = l.getBlockY() + 1; yy < 255; ++yy) {
                if (this.airBlocks.containsKey(w.getBlockAt(xx, yy, zz).getTypeId())) {
                    if (w.getBlockAt(xx, yy - 1, zz).getTypeId() == 54) {
                        --yy;
                    } else {
                        w.getBlockAt(xx, yy, zz).setTypeId(54);
                    }

                    chest = (Chest)w.getBlockAt(xx, yy, zz).getState();
                    inventory = chest.getBlockInventory();
                    var12 = hash.keySet().iterator();

                    while(var12.hasNext()) {
                        id = var12.next();
                        ItemStack itemStack = new ItemStack(Material.MONSTER_EGG, hash.get(id), (short) id);
                        SpawnEggMeta meta = (SpawnEggMeta) itemStack.getItemMeta();
                        meta.setSpawnedType(EntityType.fromId(id));
                        itemStack.setItemMeta(meta);
                        inventory.addItem(itemStack);
                    }

                    return;
                }
            }

            for(yy = l.getBlockY() - 1; yy > 0; --yy) {
                if (this.airBlocks.containsKey(w.getBlockAt(xx, yy, zz).getTypeId())) {
                    for(int yyy = yy - 1; yyy > 0; --yyy) {
                        if (!this.airBlocks.containsKey(w.getBlockAt(xx, yyy, zz).getTypeId())) {
                            if (w.getBlockAt(xx, yyy, zz).getTypeId() != 54) {
                                ++yyy;
                                w.getBlockAt(xx, yyy, zz).setTypeId(54);
                            }

                            chest = (Chest)w.getBlockAt(xx, yyy, zz).getState();
                            inventory = chest.getBlockInventory();
                            Iterator<Integer> var19 = hash.keySet().iterator();

                            while(var19.hasNext()) {
                                id = var19.next();
                                ItemStack itemStack = new ItemStack(Material.MONSTER_EGG, hash.get(id), (short) id);
                                SpawnEggMeta meta = (SpawnEggMeta) itemStack.getItemMeta();
                                meta.setSpawnedType(EntityType.fromId(id));
                                itemStack.setItemMeta(meta);
                                inventory.addItem(itemStack);
                            }

                            return;
                        }
                    }
                }
            }
        }

    }


    private String get(int id) {
        return this.main.get(id);
    }

    class ClearTimer implements Runnable {
        ClearTimer() {
        }

        public void run() {
            Clear.this.clear(false, -1);
            Clear.this.main.getServer().getScheduler().scheduleSyncDelayedTask(Clear.this.main, Clear.this.clearTimer, (long)(Clear.this.checkInterval * 20));
        }
    }

    class DelayShow implements Runnable {
        HashMap<Short, Integer> startHash;
        int startTotal;

        public DelayShow(HashMap<Short, Integer> startHash, int startTotal) {
            this.startHash = startHash;
            this.startTotal = startTotal;
        }

        public void run() {
            HashMap<Short, Integer> endHash = new HashMap<>();
            int endTotal = 0;
            Iterator<World> var5 = Clear.this.server.getWorlds().iterator();

            while(var5.hasNext()) {
                World w = var5.next();
                List<Entity> list2 = w.getEntities();
                endTotal += list2.size();

                Entity e;
                for(Iterator<Entity> var7 = list2.iterator(); var7.hasNext(); endHash.put(e.getType().getTypeId(), (Integer)endHash.get(e.getType().getTypeId()) + 1)) {
                    e = var7.next();
                    if (!endHash.containsKey(e.getType().getTypeId())) {
                        endHash.put(e.getType().getTypeId(), 0);
                    }
                }
            }

            Clear.this.server.broadcastMessage(Clear.this.main.format("success", new Object[]{Clear.this.get(1210)}));

            short s;
            Iterator<Short> var11;
            String show;
            for(var11 = this.startHash.keySet().iterator(); var11.hasNext(); endHash.remove(s)) {
                s = var11.next();
                int end;
                if (endHash.containsKey(s)) {
                    end = (Integer)endHash.get(s);
                } else {
                    end = 0;
                }

                show = Names.getEntityName(s);
                if (Clear.this.tip) {
                    Clear.this.server.broadcastMessage(Clear.this.main.format("clearInfo", new Object[]{show, this.startHash.get(s), end}));
                } else {
                    Main.sendConsoleMessage(Clear.this.main.format("clearInfo", new Object[]{show, this.startHash.get(s), end}));
                }
            }

            var11 = endHash.keySet().iterator();

            while(var11.hasNext()) {
                Short sx = var11.next();
                show = Names.getEntityName(sx);
                if (Clear.this.tip) {
                    Clear.this.server.broadcastMessage(Clear.this.main.format("clearInfo", new Object[]{show, 0, endHash.get(sx)}));
                } else {
                    Main.sendConsoleMessage(Clear.this.main.format("clearInfo", new Object[]{show, 0, endHash.get(sx)}));
                }
            }

            Clear.this.server.broadcastMessage(Clear.this.main.format("clearInfo3", new Object[]{this.startTotal, endTotal}));
        }
    }

    class Level {
        private String show;
        private boolean entity;
        private boolean monster;
        private boolean animal;

        public Level(String show, boolean entity, boolean monster, boolean animal) {
            this.show = show;
            this.entity = entity;
            this.monster = monster;
            this.animal = animal;
        }

        public String getShow() {
            return this.show;
        }

        public boolean isEntity() {
            return this.entity;
        }

        public boolean isMonster() {
            return this.monster;
        }

        public boolean isAnimal() {
            return this.animal;
        }
    }
}
