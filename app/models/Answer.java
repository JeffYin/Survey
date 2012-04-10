package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;


@Entity
public class Answer extends Model{
 
  @ManyToOne	
  public Survey survey;
  
  @ManyToOne
  public Question question; 
  
  public String survee;
  
  public String title;
  
//  public String getAnswer();  
  
}
