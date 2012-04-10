import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import models.*;

@OnApplicationStart
public class Bootstrap extends Job {
	public void doJob() {
		if (Survey.count()==0) {
		// Load default data if the database is empty
		Fixtures.deleteDatabase();
		Fixtures.loadModels("data.yml");
		}
	}
	
	private void initializeDB() {
		
	}
}
