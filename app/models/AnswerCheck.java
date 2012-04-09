package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class AnswerCheck extends Answer{
	@ManyToOne
   public QuestionOptional optional;

	@Override
	public String getAnswer() {
		return optional.title;
	}
	
	
}
