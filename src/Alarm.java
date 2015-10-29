import java.util.concurrent.atomic.AtomicBoolean;

public class Alarm extends Thread {
	private int duration;
	private AtomicBoolean flag;
	public Alarm (int duration, AtomicBoolean flag) {
		this.duration = duration;
		this.flag = flag;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep((long) 1000*duration - 200);
		} catch (InterruptedException e) {
		}
		flag.set(true);
	}
}
