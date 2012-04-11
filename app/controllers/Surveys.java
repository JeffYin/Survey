package controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import models.Answer;
import models.Question;
import models.Survey;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

import play.mvc.Http.Response;

public class Surveys extends CRUD{
	public static final String CharTargetFolder =  "c:/temp/png/";
	public static final Integer ImageWidth = 300;
	public static final Integer ImageHeight = 300;
	//*
  public static void save(String id) throws Exception {
	  if (StringUtils.isNotBlank(params.get("_createSummary"))) {
		  createSummary(id);
	  } else {
		  CRUD.save(id);
	  }
  }
  
  /**
   * 
   * @param id the survey id. 
   */
  public static void createSummary(String id) {
	  Survey survey = Survey.findById(Long.parseLong(id));
	  List<Question> questions = survey.questions;
	  
	  render("AnswerSurveys/displaySummary.html", survey, questions);
	  /*
	  for (Question question:questions) {
		 drawPIChart(survey, question);
		 drawBarChart(survey, question);
	  }
	  //*/
  }
 
  public static void drawPIChart(Long surveyId, Long questionId) {
	  Survey survey = Survey.findById(surveyId);
	  Question question = Question.findById(questionId);
	  BufferedImage piImage = drawPIChart(survey, question);
	  try {
		outputImage(piImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
  }

  public static void drawBarChart(Long surveyId, Long questionId) {
	  Survey survey = Survey.findById(surveyId);
	  Question question = Question.findById(questionId);
	  BufferedImage barImage = drawBarChart(survey, question);
	  try {
			outputImage(barImage);
		} catch (IOException e) {
			e.printStackTrace();
	}
  }
  
  /**
   * Output the image.
   * @param imgage
 * @throws IOException 
   */
  private static void outputImage(BufferedImage image) throws IOException {
	  ImageInputStream is = ImageIO.createImageInputStream(image);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ImageIO.write(image, "png", baos);
	    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	    Response.current().contentType = "image/png";

	    renderBinary(bais);
  }
  
  private static BufferedImage drawPIChart(Survey survey, Question question) {
	  // create a dataset...
      final PieDataset dataset = createPIDataset(survey, question);
      String title = question.title;
      // create the chart...
      final JFreeChart chart = createPIChart(dataset, title);
      BufferedImage bufferedImage = chart.createBufferedImage(ImageWidth, ImageHeight);
      
      final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
      String piChartName = new StringBuilder(CharTargetFolder).append(question.bulletNo).append(".PI.png").toString();
      final File file1 = new File(piChartName);
      try {
		ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
		} catch (IOException e) {
			e.printStackTrace();
		}
      
      return bufferedImage;
  }
  
 
	  
 
  private static BufferedImage drawBarChart(Survey survey, Question question) {
	  // create a dataset...
	  final CategoryDataset dataset = createBarDataset(survey, question);
	  String title = question.title;
	  // create the chart...
	  final JFreeChart chart = createBarChart(dataset, title);
	  BufferedImage bufferedImage = chart.createBufferedImage(ImageWidth, ImageHeight);
	  
	  final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
	  String barChartName = new StringBuilder(CharTargetFolder).append(question.bulletNo).append(".Bar.png").toString();
	  final File file1 = new File(barChartName);
	  try {
		  ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
		  
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	  
	  return bufferedImage;
	  
  }
  /**
   * Creates a PI dataset for the chart.
   * 
   * @return A sample dataset.
   */
  private static PieDataset createPIDataset(Survey survey, Question question) {
	  final DefaultPieDataset result = new DefaultPieDataset();
	  List<Answer> answers = Answer.find("survey = ? and question = ? ", survey, question).fetch();
	  
	  for (Answer answer: answers) {
		  String title = answer.title;
		  Integer voteNumber = 0;
		  try {
			   voteNumber = (Integer) result.getValue(title);
		  } catch (UnknownKeyException e) {
			   
		  }
		   voteNumber = voteNumber + 1;
		 
		  result.setValue(title, voteNumber);
	  }
	 
      return result;
  }

  /**
   * Creates a bar dataset for the chart.
   * 
   * @return A sample dataset.
   */
  private static CategoryDataset createBarDataset(Survey survey, Question question) {
	  
	  List<Answer> answers = Answer.find("survey = ? and question = ? ", survey, question).fetch();
	  Map<String, Integer> counterMap = new HashMap<String, Integer>(answers==null?0:answers.size());
	  
	  for (Answer answer: answers) {
		  String title = answer.title;
		  Integer voteNumber = counterMap.get(title);
		  if (voteNumber==null) {
			  voteNumber = 0;
		  }
		  voteNumber++;
		  counterMap.put(title, voteNumber);
	  }
		  
	  final DefaultCategoryDataset result = new DefaultCategoryDataset();
	  for (Map.Entry<String, Integer>entry: counterMap.entrySet()) {
		String rowKey =  entry.getKey();
		result.setValue(entry.getValue(), rowKey, question.title);
	  }
	  
	  return result;
  }
  
  /**
   * Creates a PI chart.
   * 
   * @param dataset  the dataset.
   * 
   * @return A chart.
   */
  private static JFreeChart createPIChart(final PieDataset dataset, String title) {
      
      final JFreeChart chart = ChartFactory.createPieChart(
          title,  // chart title
          dataset,                // data
          true,                   // include legend
          true,
          false
      );

      final PiePlot plot = (PiePlot) chart.getPlot();
      plot.setStartAngle(290);
      plot.setDirection(Rotation.CLOCKWISE);
      plot.setForegroundAlpha(0.5f);
      plot.setNoDataMessage("No data to display");
      return chart;
      
  }

  /**
   * Creates a Bar chart.
   * 
   * @param dataset  the dataset.
   * 
   * @return A chart.
   */
  private static JFreeChart createBarChart(final CategoryDataset dataset, String title) {
	  
	  final JFreeChart chart = ChartFactory.createBarChart(
			  title,  // chart title
			  "Score",
			  "Vote",
			  dataset,                // data
			  PlotOrientation.VERTICAL, // orientation
			  true,                   // include legend
			  true,
			  false
			  );
	  
	  final CategoryPlot plot = chart.getCategoryPlot();
	  
	  plot.setForegroundAlpha(0.5f);
	  plot.setNoDataMessage("No data to display");
	  
	  return chart;
	  
  }
  //*/
}
