package controllers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;

public class Surveys extends CRUD{
	//*
  public static void save(String id) throws Exception {
	  if (StringUtils.isNotBlank(params.get("_createSummary"))) {
		  createSummary(id);
	  } else {
		  CRUD.save(id);
	  }
  }
  
  public static void createSummary(String surveyId) {
	  // create a dataset...
      final PieDataset dataset = createSampleDataset();
      
      // create the chart...
      final JFreeChart chart = createChart(dataset);
      
      final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
      final File file1 = new File("c:/temp/multipiechart100.png");
      try {
		ChartUtilities.saveChartAsPNG(file1, chart, 600, 400, info);
		
	} catch (IOException e) {
		e.printStackTrace();
	}

  }
  
  /**
   * Creates a sample dataset for the demo.
   * 
   * @return A sample dataset.
   */
  private static PieDataset createSampleDataset() {
      
      final DefaultPieDataset result = new DefaultPieDataset();
      result.setValue("Java", new Double(43.2));
      result.setValue("Visual Basic", new Double(10.0));
      result.setValue("C/C++", new Double(17.5));
      result.setValue("PHP", new Double(32.5));
      result.setValue("Perl", new Double(1.0));
      return result;
      
  }
  
  /**
   * Creates a sample chart.
   * 
   * @param dataset  the dataset.
   * 
   * @return A chart.
   */
  private static JFreeChart createChart(final PieDataset dataset) {
      
      final JFreeChart chart = ChartFactory.createPieChart3D(
          "Pie Chart 3D Demo 1",  // chart title
          dataset,                // data
          true,                   // include legend
          true,
          false
      );

      final PiePlot3D plot = (PiePlot3D) chart.getPlot();
      plot.setStartAngle(290);
      plot.setDirection(Rotation.CLOCKWISE);
      plot.setForegroundAlpha(0.5f);
      plot.setNoDataMessage("No data to display");
      return chart;
      
  }
  //*/
}
