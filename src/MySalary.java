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
	Logger log = Logger.getLogger("Minecraft");
	
	MySActions MySA;
	MySData MySD;
	MySListener MySL;
	MySTimer MyST;
	
	String version = "1.1";
	
	public void enable(){
		MySD = new MySData(this);
		if(MySD.Proceed){
			MySA = new MySActions(this);
			MySL = new MySListener(this);
			MyST = new MySTimer(this);
			log.info("[MySalary] by darkdiplomat enabled!");
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
	}
}
