package models;

import javax.persistence.Entity;

@Entity
public class AnswerText extends Answer {
   public String text;

@Override
public String getAnswer() {
	return text;
} 
   
   
}
