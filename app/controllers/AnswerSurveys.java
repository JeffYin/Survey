package controllers;

import java.util.List;

import models.Question;
import models.QuestionOptional;
import models.Survey;

public class AnswerSurveys extends CRUD {
 public static void list() {
	 selectSurvey();
 }
	
 
  public static void selectSurvey() {
	  List<Survey> surveys = Survey.findAll();
	  render(surveys);
  }
  
  /**
   * Load all questions belongs to the serveyId
   * @param surverId
   */
  public static void loadQuestions(Long surveyId, String survee) {
      Survey survey = Survey.findById(surveyId);
	  List<Question> questions = Question.find("select q from Survey s join s.questions q where s.id = ?", surveyId).fetch();
	  //load options
	  for (Question question:questions) {
		  List<QuestionOptional> optionals = QuestionOptional.find("question = ?", question).fetch();
		  question.optionals = optionals;
	  }
	  render(survey,survee,questions);
  }
  
  public static void saveAnswers(Long surveyId, String survee) {
	  
  }
}
