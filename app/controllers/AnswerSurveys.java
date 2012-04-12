package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Answer;
import models.Question;
import models.QuestionOptional;
import models.Survey;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;

import utils.RequestParameterHolder;

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
	  validation.required(survee);
	  validation.required(surveyId);
	  if(validation.hasErrors()) {
          params.flash(); // add http parameters to the flash scope
          validation.keep(); // keep the errors for the next request
          selectSurvey();
      }
      Survey survey = Survey.findById(surveyId);
	  List<Question> questions = Question.find("select q from Survey s join s.questions q where s.id = ?", surveyId).fetch();
	  //load options
	  for (Question question:questions) {
		  List<QuestionOptional> optionals = QuestionOptional.find("select o from Question q join q.optionals o where q = ?", question).fetch();
		  question.optionals = optionals;
	  }
	  render(survey,survee,questions);
  }
  
  public static void saveAnswers(Long surveyId, String survee) throws IllegalAccessException, InvocationTargetException {
	 List<Answer> answers = getAnswersFromRequest();
	 Survey survey = Survey.findById(surveyId);
	  for (Answer answer:answers) {
		  answer.survee = survee;
		  answer.survey = survey;
		  
		  answer.save();
	  }
	  
	  //Clear the answers since it was created in a static method. 
	  answers = null;
	  
	  selectSurvey();
//	  createSummary(survey);
  }
  
  /**
   * display the summary of the survey summary. 
   * @param id The survey id. 
   */
  public static void displaySummary(String id) {
	  Long surveyId = Long.parseLong(id);
	  Survey survey = Survey.findById(surveyId);
	  List<Question> questions = Question.find("select q from Survey s join s.questions q where s.id = ?", surveyId).fetch();
	  
	  render(survey,questions);
  }
  
  
  public static List<Answer> getAnswersFromRequest() {
	  RequestParameterHolder paHoParameterHolder = new RequestParameterHolder();
	  try {
		BeanUtilsBean.getInstance().populate(paHoParameterHolder,  params.allSimple());
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		e.printStackTrace();
	}
	  
	  
	  List<Answer> answers = paHoParameterHolder.getAnswers();
	  List<String>questionIds = paHoParameterHolder.getQuestionIds();
	  
	  for (int i=0; i<answers.size() && i<questionIds.size(); i++) {
		  Long questionId = Long.parseLong(questionIds.get(i));
		  
		  Question question = Question.findById(questionId);
		  
		  answers.get(i).question = question;
	  }
	  
	  return answers; 
  }
  
}
