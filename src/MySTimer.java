import java.util.Timer;
import java.util.TimerTask;

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

public class MySTimer {
	private Timer PayOut;
	private MySalary MyS;
	private MySData MySD;
	private MySActions MySA;
	
	private long delay, reset;
	
	public MySTimer(MySalary MyS){
		this.MyS = MyS;
		PayOut = new Timer();
	}
	
	public void Initialize(){
		MySD = MyS.MySD;
		MySA = MyS.MySA;
		delay = MySD.delay;
		reset = MySD.reset;
		if(reset < 0){
			reset = delay;
			MySD.saveReset(delay+System.currentTimeMillis());
		}
		else{
			reset -= System.currentTimeMillis();
			if(reset < 1){
				reset = 60000;
			}
		}
		PayOut.schedule(new PayAll(), reset);
	}
	
	public void Reset(){
		PayOut.purge();
		PayOut.cancel();
		PayOut = new Timer();
		PayOut.schedule(new PayAll(), delay);
		MySD.saveReset(delay+System.currentTimeMillis());
	}
	
	public void Stop(){
		PayOut.purge();
		PayOut.cancel();
	}
	
	private class PayAll extends TimerTask{
		public void run(){
			MySA.PayDay();
			PayOut.schedule(new PayAll(), delay);
			MySD.saveReset(delay+System.currentTimeMillis());
			MySD.dumpData();
		}
	}
}
