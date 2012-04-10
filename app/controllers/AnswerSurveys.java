package controllers;

import groovy.util.MapEntry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Answer;
import models.Question;
import models.QuestionOptional;
import models.Survey;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;

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
  
  public static void saveAnswers(Long surveyId, String survee) throws IllegalAccessException, InvocationTargetException {
	 
	  List<Answer> answers =  ListUtils.lazyList(
			  new ArrayList<Answer>(),
			  new Factory() {
				  public Object create() {
						return new Answer();
					}
			  }
		);
	  
	  Map<String,String> getObjectMap = getObjectMap("answers\\[\\d{1,2}\\]\\..*");
	  
	  BeanUtilsBean.getInstance().populate(answers, getObjectMap);
	  for (Answer answer:answers) {
		  answer.survee = survee;
		  answer.survey.id = surveyId;
		  
		  answer.save();
	  }
	  
	  System.out.println(answers);
  }
  
  public static Map<String,String> getObjectMap(String namePattern) {
	  Map<String,String> objMap = new HashMap<String, String>();
	  
	  for (Map.Entry<String, String> entry: params.allSimple().entrySet()) {
		   String key = entry.getKey();
		   if (key.matches(namePattern)) {
			   objMap.put(key, entry.getValue());
		   }
	  }
	  
	  return objMap;
  }
  
}
