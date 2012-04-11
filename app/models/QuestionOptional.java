package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.db.jpa.Model;

@Entity
public class QuestionOptional extends Model{
	@ManyToMany
	public List<Question> questions;
	
	public String title; 
}
