import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;

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

public class MySData {
	private MySalary MyS;
	private String DS;
	
	private PluginLoader loader;
	
	private final String DIR = "plugins/config/MySalary/";
	private final String PF = "plugins/config/MySalary/MySProps.ini";
	private final String GPF = "plugins/config/MySalary/GroupPay.txt";
	private final String TRF = "plugins/config/MySalary/TimerReset.DONOTEDIT";
	private final String ERF = "plugins/config/MySalary/EmployeeRecord.list";
	
	protected HashMap<String, Boolean> W2s;
	protected HashMap<String, Double> GroupPay;
	protected HashMap<String, String> Employer;
	
	private PropertiesFile PROPS;
	private PropertiesFile Reset;
	
	private File PropsFile;
	private File GroupPayFile;
	private File EmployeeRecordFile;
	private File Dire;
	
	protected long delay = 120, reset = -1;
	protected double regpay = 10;
	protected boolean GPay = false, Claim = false, Proceed = false, dCo = false, iCo = false;
	
	public MySData(MySalary MyS){
		this.MyS = MyS;
		DS = etc.getInstance().getDataSourceType();
		loader = etc.getLoader();
		if(loader.getPlugin("dConomy") != null && loader.getPlugin("dConomy").isEnabled()){
			MyS.log.info("[MySalary] dConomy Found!");
			Proceed = true;
			dCo = true;
		}
		else if(loader.getPlugin("iConomy") != null && loader.getPlugin("iConomy").isEnabled()){
			MyS.log.info("[MySalary] iConomy Found!");
			Proceed = true;
			iCo = true;
		}
		if(!Proceed){
			MyS.log.warning("[MySalary] No Sutible Economy Plugin Found! Disabling!");
		}
	}
	
	public void Initialize(){
		Dire = new File(DIR);
		if(!Dire.exists()){
			Dire.mkdirs();
		}
		W2s = new HashMap<String, Boolean>();
		GroupPay = new HashMap<String, Double>();
		Employer = new HashMap<String, String>();
		loadProps();
		loadChecks();
		GroupPayFile = new File(GPF);
		if(!GroupPayFile.exists()){
			try {
				GroupPayFile.createNewFile();
			} catch (IOException e) {
			}
		}
		if(GPay){
			loadGroupPay();
		}
	}
	
	private void loadProps(){
		PropsFile = new File(PF);
		if(!PropsFile.exists()){
			try{
				InputStream in = getClass().getClassLoader().getResourceAsStream("DefaultProps.ini");
				FileWriter out = new FileWriter(PF);
				int c;
				while ((c = in.read()) != -1){
					out.write(c);
				}
				in.close();
				out.close();
			}catch (IOException ioe){
				MyS.log.warning("[MySalary] - Issue creating MySalary Properties File From Template!");
			}
		}
		PROPS = new PropertiesFile(PF);
		Reset = new PropertiesFile(TRF);
		delay = PROPS.getLong("Delay")*60000;
		regpay = PROPS.getDouble("PayAmount");
		Claim = PROPS.getBoolean("Use-RequireClaim");
		GPay = PROPS.getBoolean("Use-GroupPayAmounts");
		if(Reset.containsKey("TimerReset")){
			reset = Reset.getLong("TimerReset");
		}
	}
	
	private void loadGroupPay(){
		try {
		    BufferedReader in = new BufferedReader(new FileReader(GPF));
		    String str;
		    int line = 1;
		    while ((str = in.readLine()) != null) {
		    	if(!str.contains("#")){
		    		String trimmed = str.trim();
		    		String[] it = trimmed.split(":");
		    		double pay = 0;
		    		try{
		    			pay = Double.parseDouble(it[1]);
		    		}catch (NumberFormatException nfe){
		    			line++;
		    			MyS.log.severe("[MySalary] Issue with group pay at line:"+line);
		    			continue;
		    		}
		    		if(pay < 1){
		    			line++;
		    			MyS.log.severe("[MySalary] Issue with group pay at line:"+line);
		    			continue;
		    		}
		    		GroupPay.put(it[0], pay);
		    		line++;
		    	}
		    	else{
		    		line++;
		    	}
		    }
		    in.close();
		} catch (IOException e) {
			MyS.log.severe("[MySalary] - Issue reading from GroupPay.txt! Disabling GroupPay");
			GPay = false;
		}
	}
	
	private Connection getSQLConn(){
		return etc.getSQLConnection();
	}
	
	private void loadChecks(){
		if(DS.equalsIgnoreCase("mysql")){
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection conn = getSQLConn();
			try{
				ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS `MySalary` (`ID` INT(255) NOT NULL AUTO_INCREMENT, `Player` varchar(20) NOT NULL, `Employer` varchar(32) NOT NULL, `HasCheck` varchar(10) NOT NULL, PRIMARY KEY (`ID`))");
				ps.executeUpdate();
				ps = conn.prepareStatement("SELECT * FROM MySalary");
				rs = ps.executeQuery();
				while(rs.next()){
					String name = rs.getString("Player");
					String group = rs.getString("Employer");
					boolean check = Boolean.valueOf(rs.getString("HasCheck"));
					W2s.put(name, check);
					Employer.put(name, group);
				}
				rs.close();
				ps.close();
				conn.close();
			} catch (SQLException sqle) {
				MyS.log.log(Level.SEVERE, "[MySalary] - Issue with MySQL! ", sqle);
			}
		}
		else{
			EmployeeRecordFile = new File(ERF);
			if(!EmployeeRecordFile.exists()){
				try {
					EmployeeRecordFile.createNewFile();
				} catch (IOException e) {
					MyS.log.severe("[MySalary] - Issue creating EmployeeRecord.list!");
				}
			}
			try {
				BufferedReader in = new BufferedReader(new FileReader(ERF));
				String str;
				while ((str = in.readLine()) != null) {
					if(!str.contains("#")){
			    		String trimmed = str.trim();
			    		String[] it = trimmed.split(":");
			    		W2s.put(it[0], Boolean.parseBoolean(it[2]));
			    		Employer.put(it[0], it[1]);
			    	}
			    }
			    in.close();
			} catch (IOException e) {
				MyS.log.severe("[MySalary] - Issue reading from EmployeeRecord.list!");
			}
		}
	}
	
	public void saveReset(long reset){
		this.reset = reset;
		Reset.setLong("TimerReset", reset);
	}
	
	public void regpayPlayer(String name){
		if(dCo){
			loader.callCustomHook("dCBalance", new Object[]{"Player-Pay", name, regpay});
		}
		else{
			loader.callCustomHook("iBalance", new Object[]{"deposit", name, (int)regpay});
		}
	}
	
	public void gpayPlayer(String name){
		if(Employer.containsKey(name)){
			String group = Employer.get(name);
			if(GroupPay.containsKey(group)){
				double pay = GroupPay.get(group);
				if(dCo){
					loader.callCustomHook("dCBalance", new Object[]{"Player-Pay", name, pay});
				}
				else{
					loader.callCustomHook("iBalance", new Object[]{"deposit", name, (int)pay});
				}
			}
		}
	}
	
	public boolean regpayClaim(String name){
		if(W2s.containsKey(name)){
			if(W2s.get(name)){
				regpayPlayer(name);
				return true;
			}
			else{
				return false;
			}
		}
		else{
			W2s.put(name, false);
			return false;
		}
	}
	
	public boolean gpayClaim(String name, String group){
		if(W2s.containsKey(name)){
			if(W2s.get(name)){
				gpayPlayer(name);
				W2s.put(name, false);
				if(!Employer.containsKey(name)){
					Employer.put(name, group);
				}
				return true;
			}
			else{
				return false;
			}
		}
		else{
			W2s.put(name, false);
			Employer.put(name, group);
			return false;
		}
	}
	
	public void dumpData(){
		if(DS.equalsIgnoreCase("mysql")){
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection conn = getSQLConn();
			if(conn != null){
				try{
					ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS MySalary (ID INT NOT NULL AUTO_INCREMENT, Player varchar(20) NOT NULL, Employer varchar(32) NOT NULL, HasCheck varchar(10) NOT NULL, PRIMARY KEY (ID))");
					ps.executeUpdate();
					for(String name : W2s.keySet()){
						String group = "N/A";
						String check = W2s.get(name).toString();
						if(Employer.containsKey(name)){
							group = Employer.get(name);
						}
						ps = conn.prepareStatement("SELECT * FROM MySalary WHERE Player = ?");
						ps.setString(1, name);
						rs = ps.executeQuery();
						if(rs.next()){
							ps = conn.prepareStatement("UPDATE MySalary SET Employer = ?, HasCheck = ? WHERE Player = ?");
							ps.setString(1, group);
							ps.setString(2, check);
							ps.setString(3, name);
							ps.executeUpdate();
						}
						else{
							ps = conn.prepareStatement("INSERT INTO MySalary (Player,Employer,HasCheck) VALUES(?,?,?)");
							ps.setString(1, name);
							ps.setString(2, group);
							ps.setString(3, check);
							ps.executeUpdate();
						}
					}
					rs.close();
					ps.close();
					conn.close();
				} catch (SQLException sqle) {
					MyS.log.log(Level.SEVERE, "[MySalary] - Issue with MySQL! ", sqle);
				}
			}
		}
		else{
			EmployeeRecordFile = new File(ERF);
			try {
				EmployeeRecordFile.createNewFile();
			} catch (IOException ioe) {
				MyS.log.severe("[MySalary] - Issue recreating EmployeeRecord.list!");
			}
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(GPF));
				for(String name : W2s.keySet()){
					String group = "N/A";
					String check = W2s.get(name).toString();
					if(Employer.containsKey(name)){
						group = Employer.get(name);
					}
					out.write(name+":"+group+":"+check); out.newLine();
			    }
				out.close();
			} catch (IOException e) {
				MyS.log.severe("[MySalary] - Issue reading from EmployeeRecord.list!");
			}
		}
	}
	
	public String getMoneyName(){
		String Money = null;
		if(iCo){
			PropertiesFile iSet = new PropertiesFile("iConomy/settings.properties");
			if(iSet.containsKey("money-name")){
				Money = iSet.getString("money-name");
			}
			else{
				Money = "";
			}
		}
		else{
			Money = (String)loader.callCustomHook("dCBalance", new Object[]{"MoneyName"});
		}
		return Money;
	}
}
