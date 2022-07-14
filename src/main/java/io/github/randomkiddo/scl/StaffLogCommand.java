package io.github.randomkiddo.scl;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class StaffLogCommand implements CommandExecutor {
    private final Integer DEFAULT_AMOUNT = 5;
    public static String DATA_PATH;
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.isOp() || !command.getName().equalsIgnoreCase("slog")) { return false; }
        /*
        Command formats:
        /slog player amount
        /slog player
        /slog amount
         */
        LocalDate now = LocalDate.now();
        String fn = DATA_PATH + now.toString().replaceAll("-","_") + ".log";
        final File file = new File(fn);
        Scanner in;
        try {
            in = new Scanner(file);
        } catch (FileNotFoundException e) { e.printStackTrace(); return false; }
        if (args.length == 2) {
            try {
                Integer amount = Integer.parseInt(args[1]);
                sender.sendMessage(amount.toString());
                if (amount > 15) { sender.sendMessage("Allocated Size Too Large For Display"); return false; }
                String name = args[0];
                Player player = this.findPlayer(name);
                if (args[0].equalsIgnoreCase("@s") || args[0].equalsIgnoreCase("@p")) {
                    player = (Player)sender;
                }
                if (player == null) { return false; }
                ArrayList<String> log = new ArrayList<>(100);
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    if (line == null) { break; }
                    else if (line.contains(player.getName())) { log.add(line); }
                }
                sender.sendMessage(this.condenseMessage(log, amount));
            } catch (ClassCastException e) { e.printStackTrace(); return false; }
        } else {
            try {
                Integer amount = Integer.parseInt(args[0]);
                if (amount > 15) { sender.sendMessage("Allocated Size Too Large For Display"); return false; }
                ArrayList<String> log = new ArrayList<>(100);
                if (!(sender instanceof Player)) { return false; }
                Player player = (Player)sender;
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    if (line == null) { break; }
                    else if (line.contains(player.getName())) { log.add(line); }
                }
                sender.sendMessage(this.condenseMessage(log, amount));
            } catch (Exception e) {
                Player player = this.findPlayer(args[0]);
                if (args[0].equalsIgnoreCase("@s") || args[0].equalsIgnoreCase("@p")) {
                    player = (Player)sender;
                }
                if (player == null) { return false; }
                ArrayList<String> log = new ArrayList<>(100);
                while (in.hasNextLine()) {
                    String line = in.nextLine();
                    if (line == null) { break; }
                    else if (line.contains(player.getName())) { log.add(line); }
                }
                sender.sendMessage(this.condenseMessage(log, DEFAULT_AMOUNT));
            }
        }
        return true;
    }
    private String condenseMessage(ArrayList<? extends Object> log, int size) {
        ArrayList<Object> copy = new ArrayList<>();
        for (int i = log.size() - 1; i >= 0; --i) {
            if (copy.size() == size) { break; }
            copy.add(log.get(i));
        }
        String str = "";
        for (Object obj : copy) {
            str += obj.toString() + "\n";
        }
        return str;
    }
    private Player findPlayer(String name) {
        final OfflinePlayer[] players = Bukkit.getOfflinePlayers();
        for (OfflinePlayer player : players) {
            if (player.getName().equalsIgnoreCase(name)) { return player.getPlayer(); }
        }
        return null;
    }
}
