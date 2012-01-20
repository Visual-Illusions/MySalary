import java.util.logging.Logger;


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
