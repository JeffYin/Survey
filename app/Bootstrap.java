import java.util.List;

import controllers.QuestionOptionals;

import models.AnswerOutputFormat;
import models.Question;
import models.QuestionOptional;
import models.Survey;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class Bootstrap extends Job {
	public void doJob() {
		if (Survey.count()==0) {
		// Load default data if the database is empty
		Fixtures.deleteDatabase();
		Fixtures.loadModels("data.yml");
		
		initializeDB();
		}
	}
	
	private void initializeDB() {
		List<Survey> surveys = Survey.findAll();
		List<Question>questions = Question.findAll();
		List<QuestionOptional> questionOptionals = QuestionOptional.find("title !=? and title !=? ", "Yes","No").fetch();
		AnswerOutputFormat textOutputFormat = (AnswerOutputFormat) AnswerOutputFormat.find("name = ?", "text").fetch().get(0);
		AnswerOutputFormat chartOutputFormat = (AnswerOutputFormat) AnswerOutputFormat.find("name = ?", "chart").fetch().get(0);
		
		
		for (Question question:questions) {
			question.surveys = surveys;
			if ("radio".equalsIgnoreCase(question.type.name)) {
				question.answerOutputFormat = chartOutputFormat;
			}else if ("text".equalsIgnoreCase(question.type.name)) {
				question.answerOutputFormat = textOutputFormat;
			}

			question.save();
		}
		
		for (QuestionOptional optional:questionOptionals) {
			optional.questions = questions;
			optional.save();
		}
		
		
		
	}
}
