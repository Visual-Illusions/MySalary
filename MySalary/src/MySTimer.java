import java.util.Timer;
import java.util.TimerTask;

public class MySTimer {
	Timer PayOut;
	MySalary MyS;
	MySData MySD;
	MySActions MySA;
	
	long delay, reset;
	
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
		PayOut.cancel();
		PayOut.purge();
		PayOut = new Timer();
		PayOut.schedule(new PayAll(), delay);
		MySD.saveReset(delay+System.currentTimeMillis());
	}
	
	public void Stop(){
		PayOut.cancel();
		PayOut.purge();
	}
	
	public class PayAll extends TimerTask{
		public void run(){
			MySA.PayDay();
			PayOut.schedule(new PayAll(), delay);
			MySD.saveReset(delay+System.currentTimeMillis());
		}
	}
}
