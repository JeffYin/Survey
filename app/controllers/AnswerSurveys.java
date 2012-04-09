package controllers;

import java.util.List;

import models.Question;
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
  public static void loadQuestions(Long surveyId) {
	  
	  System.out.println("surveyId = "+surveyId);
	  List<Question> questions = Question.find("select q from Survey s join s.questions q where s.id = ?", surveyId).fetch();
	  
	  System.out.println(questions);
  }
}
