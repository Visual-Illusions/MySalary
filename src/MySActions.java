
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

public class MySActions {
	MySalary MyS;
	MySData MySD;
	Server server;
	boolean paying;
	
	public MySActions(MySalary MyS){
		this.MyS = MyS;
		server = etc.getServer();
	}
	
	public void Initialize(){
		MySD = MyS.MySD;
	}
	
	public void PayDay(){
		paying = true;
		if(!MySD.GPay && !MySD.Claim){
			for(String name : MySD.W2s.keySet()){
				MySD.regpayPlayer(name);
			}
		}
		else if(MySD.GPay && !MySD.Claim){
			for(String name : MySD.W2s.keySet()){
				MySD.gpayPlayer(name);
			}
		}
		else{
			for(String name : MySD.W2s.keySet()){
				MySD.W2s.put(name, true);
			}
		}
		etc.getServer().messageAll("[§aMySalary§f]§2 PAYDAY!§b CHECKS ARE HERE!");
		MyS.log.info("[MySalary] PAYDAY! CHECKS ARE HERE!");
		if(MySD.Claim){
			etc.getServer().messageAll("[§aMySalary§f]§b Use §e/mys claim§b to get your check!");
		}
		paying = false;
	}
	
	public boolean ClaimCheck(Player player){
		String name = player.getName();
		String group = player.getGroups()[0];
		if(MySD.GPay){
			if(MySD.GroupPay.containsKey(player.getGroups()[0])){
				double pay = MySD.GroupPay.get(player.getGroups()[0]);
				if(MySD.gpayClaim(name, group)){
					if(MySD.dCo){
						player.sendMessage("[§aMySalary§f]§b Here is your check for: §e"+priceForm(pay)+" "+MySD.getMoneyName());
					}
					else{
						player.sendMessage("[§aMySalary§f]§b Here is your check for: §e"+((int)pay)+" "+MySD.getMoneyName());
					}
				}
				else{
					player.sendMessage("[§aMySalary§f]§c You don't have a check!");
				}
			}
			else{
				player.sendMessage("[§aMySalary§f]§cYou are considered unemployed...");
			}
		}
		else{
			if(MySD.regpayClaim(name)){
				if(MySD.dCo){
					player.sendMessage("[§aMySalary§f]§b Here is your check for: §e"+priceForm(MySD.regpay)+" "+MySD.getMoneyName());
				}
				else{
					player.sendMessage("[§aMySalary§f]§b Here is your check for: §e"+((int)MySD.regpay)+" "+MySD.getMoneyName());
				}
			}
			else{
				player.sendMessage("[§aMySalary§f]§c You don't have a check!");
			}
		}
		return true;
	}
	
	public boolean displayTime(Player player){
		player.sendMessage("[§aMySalary§f]§b Next Pay in: ");
		player.sendMessage("§e"+timeUntil(MySD.reset));
		return true;
	}
	
	public boolean broadcastTime(){
		server.messageAll("[§aMySalary§f]§b Next Pay in: ");
		server.messageAll("§e"+timeUntil(MySD.reset));
		MyS.log.info("[MySalary] Next Pay in: "+timeUntil(MySD.reset));
		return true;
	}
	
	public boolean consoleDisplayTime(){
		MyS.log.info("[MySalary] Next Pay in: "+timeUntil(MySD.reset));
		return true;
	}
	
	public boolean manualPay(){
		PayDay();
		MyS.MyST.Reset();
		return true;
	}
	
	private String timeUntil(long time) {
		if(!paying){
			double timeLeft = Double.parseDouble(Long.toString(((time - System.currentTimeMillis()) / 1000)));
			StringBuffer Time = new StringBuffer();
			if(timeLeft >= 60 * 60 * 24) {
				int days = (int) Math.floor(timeLeft / (60 * 60 * 24));
				timeLeft -= 60 * 60 * 24 * days;
				if(days == 1) {
					Time.append(days + " day, ");
				} 
				else{
					Time.append(days + " days, ");
				}
			}
			if(timeLeft >= 60 * 60) {
				int hours = (int) Math.floor(timeLeft / (60 * 60));
				timeLeft -= 60 * 60 * hours;
				if(hours == 1) {
					Time.append(hours + " hour, ");
				} else {
					Time.append(hours + " hours, ");
				}
			}
			if(timeLeft >= 60) {
				int minutes = (int) Math.floor(timeLeft / (60));
				timeLeft -= 60 * minutes;
				if(minutes == 1) {
					Time.append(minutes + " minute ");
				} else {
					Time.append(minutes + " minutes ");
				}
			}
			int secs = (int) timeLeft;
			if(Time != null) {
				Time.append("and ");
			}
			if(secs == 1) {
				Time.append(secs + " second.");
			}
			else if(secs > -1){
				Time.append(secs + " seconds.");
			}
			else{
				Time = new StringBuffer();
				Time.append("Derp (manual pay required!)");
			}
			return Time.toString();
		}
		else{
			return "NOW!";
		}
	}
	
	private String priceForm(double price){
		String newprice = String.valueOf(price);
		String[] form = newprice.split("\\.");
		if(form[1].length() == 1){
			newprice += "0";
		}
		else{
			newprice = form[0] + "." + form[1].substring(0, 2);
		}
		return newprice;
	}
}
