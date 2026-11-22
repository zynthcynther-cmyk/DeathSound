package com.example.deathsound;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DeathSoundPlugin extends JavaPlugin implements Listener, TabExecutor {

    private static final float MIN_PITCH = 0.5f;
    private static final float MAX_PITCH = 2.0f;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        if (getCommand("deathsound") != null) {
            getCommand("deathsound").setExecutor(this);
            getCommand("deathsound").setTabCompleter(this);
        }
        getLogger().info("DeathSound enabled. Sound: " + soundChoice() + ", pitch: " + pitch());
    }

    // ------------------------------------------------------------------ events

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        Player dead = event.getEntity();
        Location loc = dead.getLocation();
        World world = loc.getWorld();
        if (world == null) {
            return;
        }

        Sound sound = buildSound();
        if (getConfig().getBoolean("play-for-everyone", true)) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.playSound(sound, Sound.Emitter.self());
            }
        } else {
            world.playSound(sound, loc.getX(), loc.getY(), loc.getZ());
        }
    }

    // ---------------------------------------------------------------- commands

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String sub = args.length == 0 ? "info" : args[0].toLowerCase(Locale.ROOT);

        switch (sub) {
            case "sound" -> {
                if (args.length < 2) {
                    send(sender, "Usage: /deathsound sound <warden|wither>", NamedTextColor.RED);
                    return true;
                }
                String choice = args[1].toLowerCase(Locale.ROOT);
                if (!choice.equals("warden") && !choice.equals("wither")) {
                    send(sender, "Unknown sound. Choose warden or wither.", NamedTextColor.RED);
                    return true;
                }
                getConfig().set("sound", choice);
                saveConfig();
                send(sender, "Death sound set to " + choice + ".", NamedTextColor.GREEN);
            }
            case "pitch" -> {
                if (args.length < 2) {
                    send(sender, "Usage: /deathsound pitch <" + MIN_PITCH + "-" + MAX_PITCH + ">", NamedTextColor.RED);
                    return true;
                }
                double value;
                try {
                    value = Double.parseDouble(args[1]);
                } catch (NumberFormatException ex) {
                    send(sender, "That isn't a number. Example: /deathsound pitch 0.8", NamedTextColor.RED);
                    return true;
                }
                if (value < MIN_PITCH || value > MAX_PITCH) {
                    send(sender, "Pitch must be between " + MIN_PITCH + " and " + MAX_PITCH + ".", NamedTextColor.RED);
                    return true;
                }
                getConfig().set("pitch", value);
                saveConfig();
                send(sender, "Pitch set to " + value + ".", NamedTextColor.GREEN);
            }
            case "test" -> {
                if (!(sender instanceof Player player)) {
                    send(sender, "Only players can use /deathsound test.", NamedTextColor.RED);
                    return true;
                }
                player.playSound(buildSound(), Sound.Emitter.self());
                send(sender, "Playing " + soundChoice() + " at pitch " + pitch() + ".", NamedTextColor.GRAY);
            }
            case "reload" -> {
                reloadConfig();
                send(sender, "DeathSound config reloaded.", NamedTextColor.GREEN);
            }
            default -> {
                send(sender, "DeathSound: " + soundChoice() + " at pitch " + pitch()
                        + ", volume " + volume() + ".", NamedTextColor.GOLD);
                send(sender, "/deathsound sound <warden|wither>", NamedTextColor.GRAY);
                send(sender, "/deathsound pitch <" + MIN_PITCH + "-" + MAX_PITCH + ">", NamedTextColor.GRAY);
                send(sender, "/deathsound test  |  /deathsound reload", NamedTextColor.GRAY);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> out = new ArrayList<>();
        if (args.length == 1) {
            for (String s : new String[]{"sound", "pitch", "test", "reload", "info"}) {
                if (s.startsWith(args[0].toLowerCase(Locale.ROOT))) out.add(s);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("sound")) {
            for (String s : new String[]{"warden", "wither"}) {
                if (s.startsWith(args[1].toLowerCase(Locale.ROOT))) out.add(s);
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("pitch")) {
            for (String s : new String[]{"0.5", "0.75", "1.0", "1.5", "2.0"}) {
                if (s.startsWith(args[1])) out.add(s);
            }
        }
        return out;
    }

    // ----------------------------------------------------------------- helpers

    private String soundChoice() {
        String choice = getConfig().getString("sound", "warden").toLowerCase(Locale.ROOT);
        return choice.equals("wither") ? "wither" : "warden";
    }

    private float pitch() {
        double p = getConfig().getDouble("pitch", 1.0);
        return (float) Math.max(MIN_PITCH, Math.min(MAX_PITCH, p));
    }

    private float volume() {
        double v = getConfig().getDouble("volume", 1.0);
        return (float) Math.max(0.1, v);
    }

    private Sound buildSound() {
        String key = soundChoice().equals("wither") ? "entity.wither.death" : "entity.warden.death";
        return Sound.sound(Key.key(key), Sound.Source.MASTER, volume(), pitch());
    }

    private static void send(CommandSender sender, String text, NamedTextColor color) {
        sender.sendMessage(Component.text(text, color));
    }
}
