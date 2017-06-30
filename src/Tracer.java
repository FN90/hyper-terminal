import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;


public class Tracer extends JFrame{
	public JFrame frame = new JFrame("Graphique");
	static TimeSeries ts = new TimeSeries("data");
	public Tracer(){
		TimeSeriesCollection dataset = new TimeSeriesCollection(ts);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Graphique",
            "Temps",
            "Valeur",
            dataset,
            true,
            true,
            false
        );
        final XYPlot plot = chart.getXYPlot();
        ValueAxis axis = plot.getDomainAxis();
        //axis.setAutoRange(true);
        axis.setFixedAutoRange(6000.0);

        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ChartPanel label = new ChartPanel(chart);
        label.setSize(new Dimension(800,300));
        frame.getContentPane().add(label);
        frame.setSize(new Dimension(800,350));
        
        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        setPreferredSize(new Dimension(200, 200));
        Dimension windowSize = new Dimension(frame.getPreferredSize());
        int wdwLeft =  screenSize.width / 2 - windowSize.width / 2-60;
        int wdwTop = 260+screenSize.height / 2 - windowSize.height / 2;
        pack();   
        frame.setLocation(wdwLeft, wdwTop);
        //frame.setLocationRelativeTo(getParent());
        //Suppose I add combo boxes and buttons here later
       // frame.pack();
        
        //int num = randGen.nextInt(1000);
        
        }
    
	public static void plot(double num){
    	ts.addOrUpdate(new Millisecond(), num);
        /*try {
			Thread.currentThread().sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
}
