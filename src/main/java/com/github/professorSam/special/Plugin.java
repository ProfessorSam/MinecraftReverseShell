package com.github.professorSam.special;

import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
	public static Plugin plugin;
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(new ChatListener(), this);
		plugin = this;
	}
	
	@Override
	public void onDisable() {
	}
}
