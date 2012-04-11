package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import play.db.jpa.Model;

@Entity
public class Question extends Model {
	
  @ManyToMany	
  public List<Survey> surveys;
  
  
  @ManyToMany(mappedBy="questions")
  public List<QuestionOptional> optionals;
  
  @ManyToOne
  public QuestionType type;
  
  public String bulletNo;
  
  public String title; 
  
  @ManyToOne
  public AnswerOutputFormat answerOutputFormat;
  
}
