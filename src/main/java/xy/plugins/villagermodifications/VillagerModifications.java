package xy.plugins.villagermodifications;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public final class VillagerModifications extends JavaPlugin implements Listener {

    private String mainPath;
    private long begin;
    private long end;
    private long allVillagersr;
    private long allVillagersxp;
    private long alert;
    private long TradesOff;
    private long CustomOnly;
    private long MaxLevel;
    private int HotV;
    private String BookTitle;
    private String BookLore;
    private String widentifier;
    private String bidentifier;
    private String alertmessage;


    @Override
    public void onEnable() {

        getConfig().options().copyDefaults();
        saveDefaultConfig();
        this.loadSettings();
        System.out.println("Villager Modifiers are running");
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void loadSettings() {
        this.mainPath = this.getDataFolder().getPath() + "/";
        File file = new File(this.mainPath, "config.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        widentifier = " ";
        bidentifier = " ";

        File wfile = new File(this.mainPath, "whitelist.yml");
        FileConfiguration whitelist_info = YamlConfiguration.loadConfiguration(wfile);
        if (!wfile.exists()) {
            List<String> whitelist = new ArrayList<>();
            whitelist.add("placeholder");
            whitelist_info.addDefault("whitelist", whitelist);
            whitelist_info.options().copyDefaults(true);
            try {
                whitelist_info.save(wfile);
            } catch (IOException var4) {
            }
        }


        this.begin = cfg.getInt("Work.begin");
        this.end = cfg.getInt("Work.end");
        this.allVillagersr = cfg.getLong("allVillagers.restricted");
        this.allVillagersxp = cfg.getLong("allVillagers.disablexp");
        this.alert = cfg.getLong("AlertOn");
        this.TradesOff = cfg.getInt("TradesOff");
        this.CustomOnly = cfg.getInt("CustomOnly");
        this.alertmessage = cfg.getString("AlertMessage");
        this.BookLore = cfg.getString("Book.Lore");
        this.BookTitle = cfg.getString("Book.Title");
        this.MaxLevel = cfg.getInt("allVillagers.levelmax");
        this.HotV = cfg.getInt("allVillagers.HotV", 5);
    }


    @EventHandler
    public void interact(PlayerInteractEntityEvent event) {

        Player p = event.getPlayer();
        if (!(event.getRightClicked() instanceof Villager)) return;
        Villager villager = (Villager) event.getRightClicked();

        this.mainPath = this.getDataFolder().getPath() + "/";
        File file = new File(this.mainPath, "config.yml");

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        File wfile = new File(this.mainPath, "whitelist.yml");
        FileConfiguration whitelist_info = YamlConfiguration.loadConfiguration(wfile);


        if (p.hasPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE)) {
            PotionEffect hero = p.getPotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
            if (HotV == 0) {
                p.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
            } else if (hero.getAmplifier() > (HotV - 1)) {
                p.removePotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE);
                p.addPotionEffect(new PotionEffect(PotionEffectType.HERO_OF_THE_VILLAGE, hero.getDuration(), (HotV - 1)));
            }
        }


        List<String> whitelist = whitelist_info.getStringList("whitelist");


        if (widentifier.equals(p.getName())) {
            if (!whitelist.contains(villager.getUniqueId().toString())) {
                whitelist.add(villager.getUniqueId().toString());
                whitelist_info.set("whitelist", whitelist);
                p.sendMessage("Villager has been added to the whitelist");
                try {
                    whitelist_info.save(wfile);
                } catch (IOException var4) {
                }
            } else {
                p.sendMessage("Villager is already whitelisted");
            }
        }

        if (bidentifier.equals(p.getName())) {
            if (whitelist.contains(villager.getUniqueId().toString())) {
                whitelist.remove(villager.getUniqueId().toString());
                whitelist_info.set("whitelist", whitelist);
                p.sendMessage("Villager has been removed from the whitelist");
                try {
                    whitelist_info.save(wfile);
                } catch (IOException var4) {
                }
            } else {
                p.sendMessage("Villager was not found in the whitelist");
            }
        }

        if (MaxLevel == 1 && villager.getVillagerExperience() < 499) {
            villager.setVillagerExperience(499);
        }

        if (!whitelist.contains(villager.getUniqueId().toString())) {
            if (this.TradesOff == 0) {

                if (this.allVillagersr == 1) {
                    if (!villager.getProfession().equals(Villager.Profession.NONE)) {
                        if (villager.getWorld().getTime() >= this.end) {
                            if (this.alert == 1) {
                                p.sendMessage(alertmessage);
                            }
                            event.setCancelled(true);
                        } else {
                            if (villager.getWorld().getTime() <= this.begin) {
                                if (this.alert == 1) {
                                    p.sendMessage(alertmessage);
                                }
                                event.setCancelled(true);
                            }
                        }
                    }
                }


                List<String> configbooks = cfg.getStringList("enchantments");
                List<String> restricteditems = cfg.getStringList("CustomItem");
                List<MerchantRecipe> recipes = Lists.newArrayList(villager.getRecipes());

                int pos = -1;
                ItemStack book_item = new ItemStack(Material.BOOK, 1);
                ItemMeta custom_book = book_item.getItemMeta();
                custom_book.setLore(Arrays.asList(BookLore));
                custom_book.setDisplayName(BookTitle);

                Iterator<MerchantRecipe> recipeIterator;
                for (recipeIterator = recipes.iterator(); recipeIterator.hasNext(); ) {
                    MerchantRecipe recipe = recipeIterator.next();
                    pos = pos + 1;

                    if (allVillagersxp == 1) {
                        recipe.setExperienceReward(false);
                    } else if (allVillagersxp == 0) {
                        recipe.setExperienceReward(true);
                    }


                    for (String item : restricteditems) {
                        if (recipe.getResult().getType().equals(Material.matchMaterial(item))) {
                            int vrestricted = cfg.getInt(item + ".restricted");
                            int vchange = cfg.getInt(item + ".change");
                            String vmaterial = cfg.getString(item + ".material");
                            int vcost = cfg.getInt(item + ".cost");
                            int vuses = cfg.getInt(item + ".uses");
                            int vxpvalue = cfg.getInt(item + ".xpvalue");
                            int vxpscale = cfg.getInt(item + ".xpscale");

                            if (villager.getWorld().getTime() >= this.end && vrestricted == 1) {
                                if (alert == 1) {
                                    p.sendMessage(alertmessage);
                                }
                                event.setCancelled(true);
                            } else {
                                if (villager.getWorld().getTime() <= this.begin && vrestricted == 1) {
                                    if (alert == 1) {
                                        p.sendMessage(alertmessage);
                                    }
                                    event.setCancelled(true);
                                }
                            }
                            if (vchange == 1) {
                                int uses = recipe.getUses();
                                ItemStack currency = new ItemStack(Material.getMaterial(vmaterial), vcost);
                                ItemStack tradeditem = new ItemStack(recipe.getResult());


                                MerchantRecipe changedrec = new MerchantRecipe(tradeditem, vuses);
                                changedrec.setUses(uses);
                                changedrec.addIngredient(currency);

                                if (vxpscale == 1 ) {
                                    if (villager.getVillagerLevel() == 2) {
                                        vxpvalue = (vxpvalue * 4);
                                    }
                                    if (villager.getVillagerLevel() == 3) {
                                        vxpvalue = (vxpvalue * 5);
                                    }
                                    if (villager.getVillagerLevel() == 4) {
                                        vxpvalue = (vxpvalue * 6);
                                    }
                                }
                                changedrec.setVillagerExperience(vxpvalue);



                                if (allVillagersxp == 1) {
                                    changedrec.setExperienceReward(false);
                                } else if (allVillagersxp == 0) {
                                    changedrec.setExperienceReward(true);
                                }
                                villager.setRecipe(pos, changedrec);
                            }
                        }
                    }

                    if (recipe.getResult().getType().equals(Material.ENCHANTED_BOOK)) {
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) recipe.getResult().getItemMeta();

                        for (String book : configbooks) {
                            if (book.contains(":")) {
                                String[] book_level = book.split(":");
                                Enchantment enchantment = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(book_level[0]));
                                int level = Integer.parseInt(book_level[1]);
                                if (meta.hasStoredEnchant(enchantment) && meta.getStoredEnchantLevel(enchantment) == level) {
                                    book = book.replace(":", "_");
                                    int vrestricted = cfg.getInt(book + ".restricted");
                                    int vchange = cfg.getInt(book + ".change");
                                    String vmaterial = cfg.getString(book + ".material");
                                    int vcost = cfg.getInt(book + ".cost");
                                    int vbook = cfg.getInt(book + ".book");
                                    int vuses = cfg.getInt(book + ".uses");
                                    int vxpvalue = cfg.getInt(book + ".xpvalue");
                                    int vxpscale = cfg.getInt(book + ".xpscale");
                                    if (villager.getWorld().getTime() >= this.end && vrestricted == 1) {
                                        if (alert == 1) {
                                            p.sendMessage(alertmessage);
                                        }
                                        event.setCancelled(true);
                                    } else {
                                        if (villager.getWorld().getTime() <= this.begin && vrestricted == 1) {
                                            if (alert == 1) {
                                                p.sendMessage(alertmessage);
                                            }
                                            event.setCancelled(true);
                                        }
                                    }
                                    if (vchange == 1) {
                                        int uses = recipe.getUses();
                                        ItemStack emerald = new ItemStack(Material.getMaterial(vmaterial), vcost);
                                        ItemStack enchantedbook = new ItemStack(recipe.getResult().getType(), 1);
                                        if (vbook == 1) {
                                            book_item.setItemMeta(custom_book);
                                        }
                                        enchantedbook.setItemMeta(meta);
                                        MerchantRecipe changedrec = new MerchantRecipe(enchantedbook, vuses);
                                        changedrec.setUses(uses);
                                        changedrec.addIngredient(emerald);
                                        changedrec.addIngredient(book_item);

                                        if (vxpscale == 1 ) {
                                            if (villager.getVillagerLevel() == 2) {
                                                vxpvalue = (vxpvalue * 4);
                                            }
                                            if (villager.getVillagerLevel() == 3) {
                                                vxpvalue = (vxpvalue * 5);
                                            }
                                            if (villager.getVillagerLevel() == 4) {
                                                vxpvalue = (vxpvalue * 6);
                                            }
                                        }
                                        changedrec.setVillagerExperience(vxpvalue);


                                        if (allVillagersxp == 1) {
                                            changedrec.setExperienceReward(false);
                                        } else if (allVillagersxp == 0) {
                                            changedrec.setExperienceReward(true);
                                        }
                                        villager.setRecipe(pos, changedrec);
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (this.TradesOff == 1){
                    event.setCancelled(true);
                } else {
                    p.sendMessage("TradesOff is set to a value other than 0 or 1 in the config.");
                    p.sendMessage("Villager trades will not be disabled, nor will they be altered.");
                    p.sendMessage("Please ensure the value is either 0 or 1 to ensure proper plugin function.");
                }

            }
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("vmreload")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("VillagerModification.reload")) {
                    p.sendMessage("Plugin has been reloaded");
                    this.loadSettings();
                } else {
                    p.sendMessage("No permission");
                }
            } else {
                System.out.println("Plugin has been reloaded");
                this.loadSettings();
            }
            return true;
        }

        if (command.getName().equals("vmbook")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("VillagerModification.reload")) {
                    p.sendMessage("§aBook received.");
                    ItemStack book = new ItemStack(Material.BOOK, 1);
                    ItemMeta custom_book = book.getItemMeta();
                    custom_book.setLore(Arrays.asList(BookLore));
                    custom_book.setDisplayName(BookTitle);
                    book.setItemMeta(custom_book);
                    p.getInventory().addItem(book);
                } else {
                    p.sendMessage("No permission");
                }
            } else {
                System.out.println("Cannot give book to console");
            }

        }

        if (command.getName().equals("vmwhitelist")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("VillagerModification.whitelist")) {
                    if (bidentifier.equals(" ")) {
                        p.sendMessage("Villager whitelist mode activated");
                        p.sendMessage("Enter /vmoff to deactivate");
                        widentifier = p.getName();
                    } if (!bidentifier.equals(" ")) {
                        p.sendMessage("Whitelist mode has not been activated.");
                        p.sendMessage("Please enter /vmoff before activating this.");
                    }

                } else {
                    p.sendMessage("No permission");
                }
            } else {
                System.out.println("Cannot identify UUID in console");
            }
            return true;

        }

        if (command.getName().equals("vmoff")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("VillagerModification.whitelist")) {
                    if (!widentifier.equals(" ")) {
                        p.sendMessage("Villager whitelist mode deactivated");
                        widentifier = " ";
                    }
                    if (!bidentifier.equals(" ")) {
                        p.sendMessage("Villager whitelist removing mode deactivated");
                        bidentifier = " ";
                    }


                } else {
                    p.sendMessage("No permission");
                }
            } else {
                System.out.println("Cannot identify UUID in console");
            }
            return true;
        }

        if (command.getName().equals("vmremove")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("VillagerModification.whitelist")) {
                    if (widentifier.equals(" ")) {
                        p.sendMessage("Villager whitelist removing mode activated");
                        p.sendMessage("Enter /vmoff to deactivate");
                        bidentifier = p.getName();
                    } if (!widentifier.equals(" ")) {
                        p.sendMessage("Removal mode has not been activated.");
                        p.sendMessage("Please enter /vmoff before activating this.");
                    }

                } else {
                    p.sendMessage("No permission");
                }
            } else {
                System.out.println("Cannot identify UUID in console");
            }
            return true;


        }

        if (command.getName().equals("vmtime")) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                if (p.hasPermission("VillagerModification.time")) {
                    p.sendMessage("Trades begin at " + begin + " ticks and ends at " + end + " ticks.");

                } else {
                    p.sendMessage("No permission");
                }
            } else {
                System.out.println("Trades begin at " + begin + " ticks and ends at " + end + " ticks.");
            }
            return true;



        }


        return false;
    }
}
