package io.github.randomkiddo.scl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

//TODO Tests and annotations (javadoc comments)
public class Main extends JavaPlugin implements Listener {
    private String currentFileName;
    private LocalDate now;
    @Override public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        File file = new File("plugins\\scl\\");
        file.mkdir();
        this.now = LocalDate.now();
        this.currentFileName = this.now.toString().replaceAll("-","_") + ".log";
        this.getCommand("slog").setExecutor(new StaffLogCommand());
        StaffLogCommand.DATA_PATH = "plugins/scl/";
    }
    @EventHandler
    public void onCommandRan(PlayerCommandPreprocessEvent cmd) {
        if (!cmd.getPlayer().isOp()) { return; }
        if (this.needsNewFile()) {
            this.now = LocalDate.now();
            this.currentFileName = this.now.toString().replaceAll("-","_") + ".log";
            File file = new File("plugins/scl/" + this.currentFileName);
            try {
                file.createNewFile();
            } catch (IOException e) { e.printStackTrace(); }
        }
        Logger logger = Logger.getLogger("SCL");
        logger.setLevel(Level.INFO);
        FileHandler fh;
        try {
            fh = new FileHandler("plugins/scl/" + this.currentFileName, true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            logger.info(this.format(cmd));
            fh.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
    private boolean needsNewFile() {
        //TODO change compareTo to String comparison to prevent multiple file creation
        String onCheck = LocalDate.now().toString().substring(5);
        if (onCheck.compareTo(this.now.toString().substring(5)) != 0 ||
                !(new File("plugins/scl/" + this.currentFileName)).exists()) { return true; }
        return false;
    }
    private String format(PlayerCommandPreprocessEvent cmd) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println(cmd.getPlayer().getName());
        return String.format("%s | %s | %s | %s\n", dtf.format(now) + " EST", cmd.getPlayer().getName(), cmd.getMessage(), this.locFormat(cmd.getPlayer().getLocation()));
    }
    private String locFormat(Location loc) {
        return "X: " + loc.getX() + ", Y: " + loc.getY() + ", Z: " + loc.getZ();
    }
}