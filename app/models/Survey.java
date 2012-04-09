package models;

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


import play.db.jpa.Model;

@Entity
public class Survey extends Model {
   public String title;
   public Date date;
   
   @ManyToMany
   public List<Question> questions;
}
