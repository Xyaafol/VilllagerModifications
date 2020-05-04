package xy.plugins.villagermodifications;

import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
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

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public final class VillagerModifications extends JavaPlugin implements Listener{

    private String mainPath;
    private long begin;
    private long end;
    private long allVillagers;
    private int pos;
    private int mending_restricted;
    private int mending_change;
    private int mending_uses;
    private String mending_material;
    private int mending_cost;
    private int mending_book;
    private int silk_touch_restricted;
    private int silk_change;
    private int silk_uses;
    private String silk_material;
    private int silk_cost;
    private int silk_book;
    private int fortune_restricted;
    private int fortune_change;
    private int fortune_uses;
    private String fortune_material;
    private int fortune_cost;
    private int fortune_book;
    private int unbreaking_restricted;
    private int unbreaking_change;
    private int unbreaking_uses;
    private String unbreaking_material;
    private int unbreaking_cost;
    private int unbreaking_book;
    private int m_uses;
    private int s_uses;
    private int f_uses;
    private int u_uses;




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

        this.begin = cfg.getLong("Work.begin");
        this.end = cfg.getLong("Work.end");
        this.allVillagers = cfg.getLong("allVillagers");
        this.mending_restricted = cfg.getInt("mending.restricted");
        this.mending_change = cfg.getInt("mending.change");
        this.mending_uses = cfg.getInt("mending.uses");
        this.mending_material = cfg.getString("mending.material");
        this.mending_cost = cfg.getInt("mending.cost");
        this.mending_book = cfg.getInt("mending.book");
        this.silk_touch_restricted = cfg.getInt("silk.restricted");
        this.silk_change = cfg.getInt("silk.change");
        this.silk_uses = cfg.getInt("silk.uses");
        this.silk_material = cfg.getString("silk.material");
        this.silk_cost = cfg.getInt("silk.cost");
        this.silk_book = cfg.getInt("silk.book");
        this.fortune_restricted = cfg.getInt("fortune.restricted");
        this.fortune_change = cfg.getInt("fortune.change");
        this.fortune_uses = cfg.getInt("fortune.uses");
        this.fortune_material = cfg.getString("fortune.material");
        this.fortune_cost = cfg.getInt("fortune.cost");
        this.fortune_book = cfg.getInt("fortune.book");
        this.unbreaking_restricted = cfg.getInt("unbreaking.restricted");
        this.unbreaking_change = cfg.getInt("unbreaking.change");
        this.unbreaking_uses = cfg.getInt("unbreaking.uses");
        this.unbreaking_material = cfg.getString("unbreaking.material");
        this.unbreaking_cost = cfg.getInt("unbreaking.cost");
        this.unbreaking_book = cfg.getInt("unbreaking.book");

    }


    @EventHandler
    public void interact(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        if (!(event.getRightClicked() instanceof Villager)) return;
        Villager villager = (Villager) event.getRightClicked();
        if (this.allVillagers == 1) {
            if (!villager.getProfession().equals(Villager.Profession.NONE)) {
                if (villager.getWorld().getTime() >= this.end) {
                    event.setCancelled(true);


                } else {
                    if (villager.getWorld().getTime() <= this.begin) {
                        event.setCancelled(true);
                    }
                }

            }

        }

        List<MerchantRecipe> recipes = Lists.newArrayList(villager.getRecipes());

        pos = -1;
        ItemStack book = new ItemStack(Material.BOOK, 1);
        ItemMeta custom_book = book.getItemMeta();
        custom_book.setLore(Arrays.asList("Trade Value: 1"));
        custom_book.setDisplayName("§aVillager Trade Book");


        Iterator<MerchantRecipe> recipeIterator;
        for (recipeIterator = recipes.iterator(); recipeIterator.hasNext(); ) {
            MerchantRecipe recipe = recipeIterator.next();
            pos = pos + 1;
            if (recipe.getResult().getType().equals(Material.ENCHANTED_BOOK)) {
                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) recipe.getResult().getItemMeta();
                if (meta.hasStoredEnchant(Enchantment.MENDING)) {

                    if (villager.getWorld().getTime() >= this.end && this.mending_restricted == 1) {
                        event.setCancelled(true);

                    } else {
                        if (villager.getWorld().getTime() <= this.begin && this.mending_restricted == 1) {
                            event.setCancelled(true);
                        }
                    }

                    if (this.mending_change == 1){
                        m_uses = recipe.getUses();
                        ItemStack emerald = new ItemStack(Material.getMaterial(this.mending_material), this.mending_cost);
                        ItemStack enchantedbook = new ItemStack(recipe.getResult().getType(), 1);
                        if (this.mending_book == 1){
                            book.setItemMeta(custom_book);
                        }
                        enchantedbook.setItemMeta(meta);
                        MerchantRecipe mending = new MerchantRecipe(enchantedbook, mending_uses);
                        mending.setUses(m_uses);
                        mending.addIngredient(emerald);
                        mending.addIngredient(book);
                        villager.setRecipe(pos, mending);
                    }


                }

                if (meta.hasStoredEnchant(Enchantment.SILK_TOUCH)) {
                    if (villager.getWorld().getTime() >= this.end && this.silk_touch_restricted == 1) {
                        event.setCancelled(true);

                    } else {
                        if (villager.getWorld().getTime() <= this.begin && this.silk_touch_restricted == 1) {
                            event.setCancelled(true);
                        }
                    }

                    if (this.silk_change == 1){
                        s_uses = recipe.getUses();
                        ItemStack emerald = new ItemStack(Material.getMaterial(this.silk_material), this.silk_cost);
                        ItemStack enchantedbook = new ItemStack(recipe.getResult().getType(), 1);
                        if (this.silk_book == 1){
                            book.setItemMeta(custom_book);
                        }
                        enchantedbook.setItemMeta(meta);
                        MerchantRecipe silk = new MerchantRecipe(enchantedbook, this.silk_uses);
                        silk.setUses(s_uses);
                        silk.addIngredient(emerald);
                        silk.addIngredient(book);
                        villager.setRecipe(pos, silk);
                    }

                }

                if (meta.hasStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS)) {
                    if (villager.getWorld().getTime() >= this.end && this.fortune_restricted == 1) {
                        event.setCancelled(true);

                    } else {
                        if (villager.getWorld().getTime() <= this.begin && this.fortune_restricted == 1) {
                            event.setCancelled(true);
                        }
                    }
                    if (this.fortune_change == 1){
                        f_uses = recipe.getUses();
                        ItemStack emerald = new ItemStack(Material.getMaterial(this.fortune_material), this.fortune_cost);
                        ItemStack enchantedbook = new ItemStack(recipe.getResult().getType(), 1);
                        if (this.fortune_book == 1){
                            book.setItemMeta(custom_book);
                        }
                        enchantedbook.setItemMeta(meta);
                        MerchantRecipe fortune = new MerchantRecipe(enchantedbook, this.fortune_uses);
                        fortune.setUses(f_uses);
                        fortune.addIngredient(emerald);
                        fortune.addIngredient(book);
                        villager.setRecipe(pos, fortune);
                    }
                }

                if (meta.hasStoredEnchant(Enchantment.DURABILITY)) {
                    if (villager.getWorld().getTime() >= this.end && this.unbreaking_restricted == 1) {
                        event.setCancelled(true);


                    } else {
                        if (villager.getWorld().getTime() <= this.begin && this.unbreaking_restricted == 1) {
                            event.setCancelled(true);
                        }
                    }
                    if (this.unbreaking_change == 1){
                        u_uses = recipe.getUses();
                        ItemStack emerald = new ItemStack(Material.getMaterial(this.unbreaking_material), this.unbreaking_cost);
                        ItemStack enchantedbook = new ItemStack(recipe.getResult().getType(), 1);
                        if (this.unbreaking_book == 1){
                            book.setItemMeta(custom_book);
                        }
                        enchantedbook.setItemMeta(meta);
                        MerchantRecipe unbreaking = new MerchantRecipe(enchantedbook, this.unbreaking_uses);
                        unbreaking.setUses(u_uses);
                        unbreaking.addIngredient(emerald);
                        unbreaking.addIngredient(book);
                        villager.setRecipe(pos, unbreaking);
                    }
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
                    custom_book.setLore(Arrays.asList("Trade Value: 1"));
                    custom_book.setDisplayName("§aVillager Trade Book");
                    book.setItemMeta(custom_book);
                    p.getInventory().addItem(book);
                }
            }
        }




        return false;
    }




    @Override
    public void onDisable() {

    }
}