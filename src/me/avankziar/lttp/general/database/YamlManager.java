package me.avankziar.lttp.general.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import me.avankziar.lttp.general.database.Language.ISO639_2B;

public class YamlManager
{	
	public enum Type
	{
		BUNGEE, SPIGOT, VELO;
	}
	
	private ISO639_2B languageType = ISO639_2B.GER;
	//The default language of your plugin. Mine is german.
	private ISO639_2B defaultLanguageType = ISO639_2B.GER;
	private Type type;
	
	//Per Flatfile a linkedhashmap.
	private static LinkedHashMap<String, Language> configKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> commandsKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> languageKeys = new LinkedHashMap<>();
	private static LinkedHashMap<String, Language> mvelanguageKeys = new LinkedHashMap<>();
	/*
	 * Here are mutiplefiles in one "double" map. The first String key is the filename
	 * So all filename muss be predefine. For example in the config.
	 */
	private static LinkedHashMap<String, LinkedHashMap<String, Language>> guisKeys = new LinkedHashMap<>();
	
	public YamlManager(Type type)
	{
		this.type = type;
		initConfig();
		initLanguage();
	}
	
	public ISO639_2B getLanguageType()
	{
		return languageType;
	}

	public void setLanguageType(ISO639_2B languageType)
	{
		this.languageType = languageType;
	}
	
	public ISO639_2B getDefaultLanguageType()
	{
		return defaultLanguageType;
	}
	
	public LinkedHashMap<String, Language> getConfigKey()
	{
		return configKeys;
	}
	
	public LinkedHashMap<String, Language> getCommandsKey()
	{
		return commandsKeys;
	}
	
	public LinkedHashMap<String, Language> getLanguageKey()
	{
		return languageKeys;
	}
	
	public LinkedHashMap<String, Language> getModifierValueEntryLanguageKey()
	{
		return mvelanguageKeys;
	}
	
	public LinkedHashMap<String, LinkedHashMap<String, Language>> getGUIKey()
	{
		return guisKeys;
	}
	
	public void setFileInput(dev.dejvokep.boostedyaml.YamlDocument yml,
			LinkedHashMap<String, Language> keyMap, String key, ISO639_2B languageType)
	{
		if(!keyMap.containsKey(key))
		{
			return;
		}
		if(yml.get(key) != null)
		{
			return;
		}
		if(key.startsWith("#"))
		{
			if(type == Type.BUNGEE)
			{
				return;
			}
			//Comments
			String k = key.replace("#", "");
			if(yml.get(k) == null)
			{
				//return because no actual key are present
				return;
			}
			if(yml.getBlock(k) == null)
			{
				return;
			}
			if(yml.getBlock(k).getComments() != null && !yml.getBlock(k).getComments().isEmpty())
			{
				//Return, because the comments are already present, and there could be modified. F.e. could be comments from a admin.
				return;
			}
			if(keyMap.get(key).languageValues.get(languageType).length == 1)
			{
				if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
				{
					String s = ((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", "");
					yml.getBlock(k).setComments(Arrays.asList(s));
				}
			} else
			{
				List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
				ArrayList<String> stringList = new ArrayList<>();
				if(list instanceof List<?>)
				{
					for(Object o : list)
					{
						if(o instanceof String)
						{
							stringList.add(((String) o).replace("\r\n", ""));
						}
					}
				}
				yml.getBlock(k).setComments((List<String>) stringList);
			}
			return;
		}
		if(keyMap.get(key).languageValues.get(languageType).length == 1)
		{
			if(keyMap.get(key).languageValues.get(languageType)[0] instanceof String)
			{
				yml.set(key, convertMiniMessageToBungee(((String) keyMap.get(key).languageValues.get(languageType)[0]).replace("\r\n", "")));
			} else
			{
				yml.set(key, keyMap.get(key).languageValues.get(languageType)[0]);
			}
		} else
		{
			List<Object> list = Arrays.asList(keyMap.get(key).languageValues.get(languageType));
			ArrayList<String> stringList = new ArrayList<>();
			if(list instanceof List<?>)
			{
				for(Object o : list)
				{
					if(o instanceof String)
					{
						stringList.add(convertMiniMessageToBungee(((String) o).replace("\r\n", "")));
					} else
					{
						stringList.add(o.toString().replace("\r\n", ""));
					}
				}
			}
			yml.set(key, (List<String>) stringList);
		}
	}
	
	private String convertMiniMessageToBungee(String s)
	{
		if(type != Type.BUNGEE)
		{
			//If Server is not Bungee, there is no need to convert.
			return s;
		}
		StringBuilder b = new StringBuilder();
		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			if(c == '<' && i+1 < s.length())
			{
				char cc = s.charAt(i+1);
				if(cc == '#' && i+8 < s.length())
				{
					//Hexcolors
					//     i12345678
					//f.e. <#00FF00>
					String rc = s.substring(i, i+8);
					b.append(rc.replace("<#", "&#").replace(">", ""));
					i += 8;
				} else
				{
					//Normal Colors
					String r = null;
					StringBuilder sub = new StringBuilder();
					sub.append(c).append(cc);
					i++;
					for(int j = i+1; j < s.length(); j++)
					{
						i++;
						char jc = s.charAt(j);
						if(jc == '>')
						{
							sub.append(jc);
							switch(sub.toString())
							{
							case "</color>":
							case "</black>":
							case "</dark_blue>":
							case "</dark_green>":
							case "</dark_aqua>":
							case "</dark_red>":
							case "</dark_purple>":
							case "</gold>":
							case "</gray>":
							case "</dark_gray>":
							case "</blue>":
							case "</green>":
							case "</aqua>":
							case "</red>":
							case "</light_purple>":
							case "</yellow>":
							case "</white>":
							case "</obf>":
							case "</obfuscated>":
							case "</b>":
							case "</bold>":
							case "</st>":
							case "</strikethrough>":
							case "</u>":
							case "</underlined>":
							case "</i>":
							case "</em>":
							case "</italic>":
								r = "";
								break;
							case "<black>":
								r = "&0";
								break;
							case "<dark_blue>":
								r = "&1";
								break;
							case "<dark_green>":
								r = "&2";
								break;
							case "<dark_aqua>":
								r = "&3";
								break;
							case "<dark_red>":
								r = "&4";
								break;
							case "<dark_purple>":
								r = "&5";
								break;
							case "<gold>":
								r = "&6";
								break;
							case "<gray>":
								r = "&7";
								break;
							case "<dark_gray>":
								r = "&8";
								break;
							case "<blue>":
								r = "&9";
								break;
							case "<green>":
								r = "&a";
								break;
							case "<aqua>":
								r = "&b";
								break;
							case "<red>":
								r = "&c";
								break;
							case "<light_purple>":
								r = "&d";
								break;
							case "<yellow>":
								r = "&e";
								break;
							case "<white>":
								r = "&f";
								break;
							case "<obf>":
							case "<obfuscated>":
								r = "&k";
								break;
							case "<b>":
							case "<bold>":
								r = "&l";
								break;
							case "<st>":
							case "<strikethrough>":
								r = "&m";
								break;
							case "<u>":
							case "<underlined>":
								r = "&n";
								break;
							case "<i>":
							case "<em>":
							case "<italic>":
								r = "&o";
								break;
							case "<reset>":
								r = "&r";
								break;
							case "<newline>":
								r = "~!~";
								break;
							}
							b.append(r);
							break;
						} else
						{
							//Search for the color.
							sub.append(jc);
						}
					}
				}
			} else
			{
				b.append(c);
			}
		}
		return b.toString();
	}
	
	private void addComments(LinkedHashMap<String, Language> mapKeys, String path, Object[] o)
	{
		mapKeys.put(path, new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, o));
	}
	
	private void addConfig(String path, Object[] c, Object[] o)
	{
		configKeys.put(path, new Language(new ISO639_2B[] {ISO639_2B.GER}, c));
		addComments(configKeys, "#"+path, o);
	}
	
	public void initConfig() //INFO:Config
	{
		addConfig("Language",
				new Object[] {
				"ENG"},
				new Object[] {
				"",
				"Die eingestellte Sprache. Von Haus aus sind 'ENG=Englisch' und 'GER=Deutsch' mit dabei.",
				"Falls andere Sprachen gewünsch sind, kann man unter den folgenden Links nachschauen, welchs Kürzel für welche Sprache gedacht ist.",
				"Siehe hier nach, sowie den Link, welche dort auch für Wikipedia steht.",
				"https://github.com/Avankziar/RootAdministration/blob/main/src/main/java/me/avankziar/roota/general/Language.java",
				"",
				"The set language. By default, ENG=English and GER=German are included.",
				"If other languages are required, you can check the following links to see which abbreviation is intended for which language.",
				"See here, as well as the link, which is also there for Wikipedia.",
				"https://github.com/Avankziar/RootAdministration/blob/main/src/main/java/me/avankziar/roota/general/Language.java"});
		
		addConfig("Sync.Message.SendByJoin",
				new Object[] {
				false},
				new Object[] {
				"",
				"Wenn 'true', wird dem Spieler beim joinen eine Sync Nachricht gesendet.",
				"",
				"If 'true', a sync message will be sent to the player when joining."});
		addConfig("ExcludedPermissionPerGroup",
				new Object[] {
				"vip;dummy.one",
				"supporter;dummy.two"},
				new Object[] {
				"",
				"Hier kommen alle Permission hinein, welche von dem Tracking exkludiert werden soll.",
				"Diese Permission werden somit beim Joinen des Spielers ignoriert.",
				"Die Angegebene Gruppe ist als Variable für die primäre Gruppe des Spielers gemeint.",
				"Bedeutet, wenn der Spieler diese Gruppe als primäre hat, so zählt diese nur.",
				"Eine <default> ist für alle Spieler, egal in welcher Gruppe sich der Spieler befindet.",
				"Bedenkt, dass nur Spielerbezogene temporäre Permission gespeichert werden können.",
				"Temporäre Permission, welcher Gruppen vergeben werden, werden nicht geträckt, da sie für mehrere Spieler sind.",
				"",
				"This is where all permissions that should be excluded from tracking go.",
				"These permissions will therefore be ignored when the player joins.",
				"The specified group is meant as a variable for the player's primary group.",
				"This means that if the player has this group as their primary, then only this one counts.",
				"A <default> is for all players, regardless of which group the player is in.",
				"Please note that only player-related temporary permissions can be saved.",
				"Temporary permissions granted to groups are not tracked because they are for multiple players."});
	}
	
	public void initLanguage() //INFO:Languages
	{		
		languageKeys.put("SyncMessage", 
				new Language(new ISO639_2B[] {ISO639_2B.GER, ISO639_2B.ENG}, new Object[] {
						"<gray>[<red>LTTP<gray>] <white>%amount% Permission Synchronisiert!",
						"<gray>[<red>LTTP<gray>] <white>%amount% Permission syncronizied!"}));
	}
}