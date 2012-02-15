import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
* MySalary v1.x
* Copyright (C) 2012 Visual Illusions Entertainment
* @author darkdiplomat <darkdiplomat@visualillusionsent.net>
* 
* This file is part of MySalary
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see http://www.gnu.org/copyleft/gpl.html.
*/

public class MySalary extends Plugin{
	protected final Logger log = Logger.getLogger("Minecraft");
	
	protected MySActions MySA;
	protected MySData MySD;
	protected MySListener MySL;
	protected MySTimer MyST;
	
	protected final String version = "1.2";
	protected String CurrVer = "1.2";
	
	public void enable(){
		log.info("[MySalary] v"+version+" by darkdiplomat enabled!");
		if(!isLatest()){
			log.info("[MySalary] - There is an update available! Current = " + CurrVer);
		}
		MySD = new MySData(this);
		if(MySD.Proceed){
			MySA = new MySActions(this);
			MySL = new MySListener(this);
			MyST = new MySTimer(this);
		}
	}
	
	public void disable(){
		if(MySD.Proceed){
			MyST.Stop();
			MySD.dumpData();
		}
		log.info("[MySalary] disabled!");
	}
	
	public void initialize(){
		if(MySD.Proceed){
			MySD.Initialize();
			MySL.Initialize();
			MySA.Initialize();
			MyST.Initialize();
			etc.getLoader().addListener(PluginLoader.Hook.LOGIN, MySL, this, PluginListener.Priority.MEDIUM);
			etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, MySL, this, PluginListener.Priority.MEDIUM);
			etc.getLoader().addListener(PluginLoader.Hook.COMMAND, MySL, this, PluginListener.Priority.MEDIUM);
			etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND, MySL, this, PluginListener.Priority.MEDIUM);
			log.info("[MySalary] initialized!");
		}
		else{
			etc.getLoader().disablePlugin("MySalary");
		}
	}
	
	public boolean isLatest(){
		String address = "http://www.visualillusionsent.net/cmod_plugins/Versions.html";
		URL url = null;
		try {
			url = new URL(address);
		} catch (MalformedURLException e) {
			return true;
		}
		String[] Vpre = new String[1]; 
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("MySalary=")){
					Vpre = inputLine.split("=");
					CurrVer = Vpre[1].replace("</p>", "");
				}
			}
			in.close();
		} catch (IOException e) {
			return true;
		}
		return (version.equals(CurrVer));
	}
}
