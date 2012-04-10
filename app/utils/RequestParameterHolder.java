package utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import models.Answer;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.list.GrowthList;
import org.apache.commons.collections.list.LazyList;

public class RequestParameterHolder {
	private List<String> questionIds;
	private  List<Answer> answers;
	
	public RequestParameterHolder() {
		super();
		
		List<Answer> backingAnswerList = new LinkedList<Answer>();
		answers = LazyList.decorate(GrowthList.decorate(backingAnswerList), new Factory() {
				 public Object create() {
						return new Answer();
					}
		});

		List<String> backingQuestionIdList = new LinkedList<String>();
		questionIds = LazyList.decorate(GrowthList.decorate(backingQuestionIdList), new Factory() {
			public Object create() {
				return new String();
			}
		});
		
	}

	

	public List<Answer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}



	public List<String> getQuestionIds() {
		return questionIds;
	}

	public void setQuestionIds(List<String> questionIds) {
		this.questionIds = questionIds;
	}

}
