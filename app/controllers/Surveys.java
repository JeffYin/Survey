package controllers;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;

import models.Answer;
import models.Question;
import models.Survey;

import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.TextAnchor;
import org.jfree.util.Rotation;
import org.jfree.util.SortOrder;

import play.mvc.Http.Response;

public class Surveys extends CRUD{
//	public static final String CharTargetFolder =  "c:/temp/png/";
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
  
  public static String getTextAnswers(Long surveyId, Long questionId) {
	  List<Answer> answers = Answer.find("survey.id = ? and question.id = ? order by title", surveyId, questionId).fetch();
	  StringBuilder info = new StringBuilder();
	  
	  for (Answer answer: answers) {
		  String title = answer.title;
		  if (StringUtils.isNotBlank(title)) {
			  info.append(title).append("<br>");
		  }
	  }
	  
	  return info.toString();
  }
 
  
  /**
   * Get the summary in the text format. 
   * @param surveyId
   * @param questionId
   * @return
   */
  public static String getTextSummary(Long surveyId, Long questionId) {
	Map<String, Integer> summaryMap = createAnswerSummary(surveyId, questionId);
	List<String> info = new ArrayList<String>(summaryMap.size());  
	
	for (Map.Entry<String, Integer> entry : summaryMap.entrySet()) {
		info.add(String.format("%s:%d votes", entry.getKey(), entry.getValue()));
	}
	
	String summary = StringUtils.join(info, ", &nbsp; &nbsp;");
	
	return summary;
  }
  
  public static void drawPIChart(Long surveyId, Long questionId) {
	  Survey survey = Survey.findById(surveyId);
	  Question question = Question.findById(questionId);
	  if (survey!=null && question!=null) {
		  BufferedImage piImage = drawPIChart(survey, question);
		  try {
			outputImage(piImage);
			} catch (IOException e) {
				e.printStackTrace();
			}
	  }
  }

  public static void drawBarChart(Long surveyId, Long questionId) {
	  Survey survey = Survey.findById(surveyId);
	  Question question = Question.findById(questionId);
	  if (survey!=null && question!=null) {
		  BufferedImage barImage = drawBarChart(survey, question);
		  try {
				outputImage(barImage);
			} catch (IOException e) {
				e.printStackTrace();
		}
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
      final JFreeChart chart = createPIChart(dataset, "");
      BufferedImage bufferedImage = chart.createBufferedImage(ImageWidth, ImageHeight);
      
      /*
      final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
      String piChartName = new StringBuilder(CharTargetFolder).append(question.bulletNo).append(".PI.png").toString();
      final File file1 = new File(piChartName);
      try {
		ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
		} catch (IOException e) {
			e.printStackTrace();
		}
      */
      return bufferedImage;
  }
  
 
	  
 
  private static BufferedImage drawBarChart(Survey survey, Question question) {
	  // create a dataset...
	  final CategoryDataset dataset = createBarDataset(survey, question);
	  String title = question.title;
	  // create the chart...
	  final JFreeChart chart = createBarChart(dataset, title);
	  BufferedImage bufferedImage = chart.createBufferedImage(ImageWidth, ImageHeight);
	  
	  /*
	  final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
	  String barChartName = new StringBuilder(CharTargetFolder).append(question.bulletNo).append(".Bar.png").toString();
	  final File file1 = new File(barChartName);
	  try {
		  ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
		  
	  } catch (IOException e) {
		  e.printStackTrace();
	  }
	  */
	  return bufferedImage;
	  
  }
  /**
   * Creates a PI dataset for the chart.
   * 
   * @return A sample dataset.
   */
  private static PieDataset createPIDataset(Survey survey, Question question) {
	  
	  final DefaultPieDataset result = new DefaultPieDataset();
	  Map<String, Integer> answersMap = createAnswerSummary(survey, question);
	  
	  for (Map.Entry<String, Integer> entry :answersMap.entrySet()) {
		  result.setValue(entry.getKey(), entry.getValue());
	  }
	  
//	  for (Answer answer: answers) {
//		  String title = answer.title;
//		  if (StringUtils.isNotBlank(title)) {
//			  Integer voteNumber = 0;
//			  try {
//				   voteNumber = (Integer) result.getValue(title);
//			  } catch (UnknownKeyException e) {
//				   
//			  }
//			   voteNumber = voteNumber + 1;
//			 
//			  result.setValue(title, voteNumber);
//		  }
//	  }
	 
      return result;
  }
  
  
  
  private static Map<String, Integer> createAnswerSummary(Long surveyId, Long questionId) {
	  Survey survey = Survey.findById(surveyId);
	  Question question = Question.findById(questionId);
	  return createAnswerSummary(survey, question);
  }
  private static Map<String, Integer> createAnswerSummary(Survey survey, Question question) {
	  Map<String,Integer> result = new ListOrderedMap();
	  if (survey!=null && question!=null) {
		   List<Answer> answers = Answer.find("survey = ? and question = ?", survey, question).fetch();
			
		   Collections.sort(answers, new Comparator<Answer>() {
			@Override
			public int compare(Answer arg0, Answer arg1) {
				String title0 = arg0.title;
				String title1 = arg1.title;
				
				String score0str = StringUtils.isBlank(title0)?"0":title0.substring(0, Math.min(2,title0.length())).trim();
				String score1str = StringUtils.isBlank(title1)?"0":title1.substring(0, Math.min(2,title1.length())).trim();
				
				
				String	score0 = score0str.length()==1?new StringBuilder("0").append(score0str).toString():score0str;
				String	score1 = score1str.length()==1?new StringBuilder("0").append(score1str).toString():score1str;
				
//				if ("NA".equalsIgnoreCase(score0str)) {
//					score0str = "-1";
//				}
//				if ("NA".equalsIgnoreCase(score1str)) {
//					score1str = "-1";
//				}
// 				
//				Integer score0 = -1;
//				try {
//					 score0 = Integer.parseInt(score0str);
//				} catch (Exception e) {
//					score0 = -1;
//				}
//
//				Integer score1 = -1;
//				try {
//					score1 = Integer.parseInt(score1str);
//				} catch (Exception e) {
//					score1 = -1;
//				}
//				
				
				return score0.compareTo(score1);
			}
			   
		    });
		   
		  for (Answer answer: answers) {
			  String title = answer.title;
			  if (StringUtils.isNotBlank(title)) {
				  Integer voteNumber = result.get(title);
				  if (voteNumber==null) {
					  voteNumber = new Integer(1);
				  } else {
				       voteNumber = new Integer(voteNumber.intValue() + 1);
				  }
				  
				  result.put(title, voteNumber);
			  }
		  }
	  }
	  //*/
	  return result; 
  }
  /**
   * Creates a bar dataset for the chart.
   * 
   * @return A sample dataset.
   */
  private static CategoryDataset createBarDataset(Survey survey, Question question) {
	  final DefaultCategoryDataset result = new DefaultCategoryDataset();
	  Map<String, Integer> answersMap = createAnswerSummary(survey, question);
	  
	  for (Map.Entry<String, Integer> entry :answersMap.entrySet()) {
		  result.setValue(entry.getValue(), entry.getKey(), question.title);
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
      
      plot.setStartAngle(-90);
      plot.setDirection(Rotation.CLOCKWISE);
      plot.setForegroundAlpha(1f);
      plot.setBackgroundPaint(Color.white);
      plot.setNoDataMessage("No data to display");
      
      List keyList = dataset.getKeys();
      if (keyList!=null) {
    	  int maxNum = dataset.getKeys().size();
    	  for (int i=0;i<maxNum-1; i++) {
    		  plot.setExplodePercent(dataset.getKey(i), 0.1d);
    	  }
      }
      
      configPIFont(chart);
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
			  "",  // chart title
			  "",
			  "Vote",
			  dataset,                // data
			  PlotOrientation.VERTICAL, // orientation
			  true,                   // include legend
			  false,
			  false
			  );
	  
	  final CategoryPlot plot = chart.getCategoryPlot();
	  plot.setBackgroundPaint(Color.WHITE);
	  
	  plot.setForegroundAlpha(1.0f);
	  plot.setNoDataMessage("No data to display");
	  plot.setColumnRenderingOrder(SortOrder.DESCENDING);
	  
	  BarRenderer render   =   new BarRenderer(); 
	  render.setMaximumBarWidth(0.1);
	  
	  render.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
	  render.setBaseItemLabelsVisible(true);
	  render.setItemLabelAnchorOffset(10D);
	  
	  render.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
	  render.setItemLabelAnchorOffset(2D);
	  

	  NumberAxis   yAxis   =   (NumberAxis)plot.getRangeAxis();
	  yAxis.setAxisLineVisible(false);
	  yAxis.setTickUnit(new   NumberTickUnit(1D));    
	  
	  
	  CategoryAxis xAxis = plot.getDomainAxis();
	  xAxis.setAxisLineVisible(false);
	  
	  plot.setRenderer(render);
	  configBarFont(chart);
	  return chart;
	  
  }


  /**
   * 配置字体 
   * @param chart JFreeChart 对象
   */
  private static void configBarFont(JFreeChart chart){
  	// 配置字体
  	Font xfont = new Font("宋体",Font.PLAIN,12) ;// X轴
  	Font yfont = new Font("宋体",Font.PLAIN,12) ;// Y轴
  	Font kfont = new Font("宋体",Font.PLAIN,12) ;// 底部
  	Font titleFont = new Font("隶书", Font.BOLD , 25) ; // 图片标题
  	CategoryPlot plot = chart.getCategoryPlot();// 图形的绘制结构对象
  	
  	
  	// 图片标题
  	chart.setTitle(new TextTitle(chart.getTitle().getText(),titleFont));
  	
  	// 底部
  	chart.getLegend().setItemFont(kfont);
  	
  	// X 轴
  	CategoryAxis domainAxis = plot.getDomainAxis();   
      domainAxis.setLabelFont(xfont);// 轴标题
      domainAxis.setTickLabelFont(xfont);// 轴数值  
      domainAxis.setTickLabelPaint(Color.BLUE) ; // 字体颜色
//      domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // 横轴上的label斜显示 
      
  	// Y 轴
  	ValueAxis rangeAxis = plot.getRangeAxis();   
      rangeAxis.setLabelFont(yfont); 
      rangeAxis.setLabelPaint(Color.BLUE) ; // 字体颜色
      rangeAxis.setTickLabelFont(yfont);  
      
  }

  /**
   * 配置字体 
   * @param chart JFreeChart 对象
   */
  private static void configPIFont(JFreeChart chart){
	  // 配置字体
	  Font xfont = new Font("宋体",Font.PLAIN,12) ;// X轴
	  Font yfont = new Font("宋体",Font.PLAIN,12) ;// Y轴
	  Font kfont = new Font("宋体",Font.PLAIN,12) ;// 底部
	  Font titleFont = new Font("隶书", Font.BOLD , 25) ; // 图片标题
	  PiePlot plot = (PiePlot) chart.getPlot();// 图形的绘制结构对象
	  
	  
	  // 图片标题
	  chart.setTitle(new TextTitle(chart.getTitle().getText(),titleFont));
	  
	  // 底部
	  chart.getLegend().setItemFont(kfont);
	  
	  
  }
}
