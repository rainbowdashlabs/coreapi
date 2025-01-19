package de.sakuramc.coreapi;

import de.sakuramc.coreapi.commands.*;
import de.sakuramc.coreapi.listener.*;
import de.sakuramc.coreapi.manager.*;
import de.sakuramc.coreapi.mysql.MySQLManager;
import de.sakuramc.coreapi.scoreboard.ScoreboardManager;
import de.sakuramc.coreapi.utils.FriendGUI;
import de.sakuramc.coreapi.utils.UUIDFetcher;
import org.bukkit.plugin.java.JavaPlugin;

public class CoreAPI extends JavaPlugin {

    public final String prefix = "§8┃ §d§lSakuraMC §8»§7 ";
    public final String noPerm = prefix + "Du hast dafür §4keine §7Rechte!";
    public final String noPlayer = prefix + "Du musst ein Spieler sein!";
    public final String onUse = prefix + "§cVerwende: §7/";

    private static CoreAPI instance;

    public MySQLManager mySQLManager;
    public TeamManager teamManager;
    public PlayerManager playerManager;
    public ScoreboardManager scoreboardManager;
    public ServerManager serverManager;
    public ClanManager clanManager;
    public LanguageManager languageManager;
    public UUIDFetcher uuidFetcher;
    public FriendGUI friendGUI;

    public static final String LANG_CHANNEL_ID = "sakuramc:lang";

    @Override
    public void onEnable() {
        instance = this;

        this.getServer().getMessenger().registerIncomingPluginChannel(this, LANG_CHANNEL_ID, new PluginMessageListenerImpl());
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, LANG_CHANNEL_ID);

        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        this.mySQLManager = new MySQLManager(
                this.getConfig().getString("mysql.hostname"),
                this.getConfig().getInt("mysql.port"),
                this.getConfig().getString("mysql.database"),
                this.getConfig().getString("mysql.username"),
                this.getConfig().getString("mysql.password")
        );
        this.mySQLManager.connect();

        this.uuidFetcher = new UUIDFetcher(mySQLManager);
        this.teamManager = new TeamManager(mySQLManager);
        this.playerManager = new PlayerManager(mySQLManager);
        this.clanManager = new ClanManager(mySQLManager);
        this.languageManager = new LanguageManager(this, playerManager);

        this.friendGUI = new FriendGUI(this);
        //this.scoreboardManager = new ScoreboardManager(this);

        this.serverManager = new ServerManager();

        loadCommands();
        loadListener();

        instance.getServer().getConsoleSender().sendMessage(prefix + "Die CoreAPI wurde §aerfolgreich §7aktiviert.");
    }

    @Override
    public void onDisable() {
        mySQLManager.disconnect();

        instance.getServer().getConsoleSender().sendMessage(prefix + "Die CoreAPI wurde §aerfolgreich §7deaktiviert!");
    }

    private void loadCommands() {
        new GameModeCommand(this);
        new FlyCommand(this);
        new FriendCommand(this);
        new ClanCommand(this);
        new CommandNotFoundListener(this);
        new LanguageListener(this);
    }

    private void loadListener() {
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new FlyListener(this);
        new ClanCommand(this);
        new LanguageCommand(this);
    }

    public static CoreAPI getInstance() {
        return instance;
    }

}
