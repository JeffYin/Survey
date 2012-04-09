import play.jobs.*;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {
	public void doJob() {
		// Load default data if the database is empty
		Fixtures.deleteDatabase();
		Fixtures.loadModels("data.yml");
	}
}
