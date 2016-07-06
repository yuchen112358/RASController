package com.zju.rascontroller;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;

public class ChartDrawer {
     private GraphicalView myGraphicalView;
     private XYMultipleSeriesDataset myMultipleSeriesDataset;// 数据集容器
     private XYMultipleSeriesRenderer myMultipleSeriesRenderer;// 渲染器容器
     private XYSeries mySeries;// 单条曲线数据集
     private XYSeriesRenderer myRenderer;// 单条曲线渲染器
     private Context context;
     public ChartDrawer(Context context) {
         this.context = context;
     }
 
     /**
      * 获取图表
      * 
      * @return
      */
     public GraphicalView getGraphicalView() {
         myGraphicalView = ChartFactory.getLineChartView(context,
                 myMultipleSeriesDataset, myMultipleSeriesRenderer);
         return myGraphicalView;
     }
     /**
      * 获取数据集，及xy坐标的集合
      * 
      * @param curveTitle
      */
     public void setXYMultipleSeriesDataset(String curveTitle) {
         myMultipleSeriesDataset = new XYMultipleSeriesDataset();
         mySeries = new XYSeries(curveTitle);
         myMultipleSeriesDataset.addSeries(mySeries);
     }
 
     /**
      * 获取渲染器
      * 
      * @param maxX
      *            x轴最大值
      * @param maxY
      *            y轴最大值
      * @param chartTitle
      *            曲线的标题
      * @param xTitle
      *            x轴标题
      * @param yTitle
      *            y轴标题
      * @param axeColor
      *            坐标轴颜色
      * @param labelColor
      *            标题颜色
      * @param curveColor
      *            曲线颜色
      * @param gridColor
      *            网格颜色
      */
     public void setXYMultipleSeriesRenderer(double maxX, double maxY,
             String chartTitle, String xTitle, String yTitle, int axeColor,
             int labelColor, int curveColor, int gridColor) {
         myMultipleSeriesRenderer = new XYMultipleSeriesRenderer();
         if (chartTitle != null) {
             myMultipleSeriesRenderer.setChartTitle(chartTitle);//设置曲线标题
         }
         myMultipleSeriesRenderer.setXTitle(xTitle);//x轴标签
         myMultipleSeriesRenderer.setYTitle(yTitle);//y轴标签
         myMultipleSeriesRenderer.setRange(new double[] { 0, maxX, 0, maxY });//xy轴的范围
         myMultipleSeriesRenderer.setLabelsColor(labelColor);//xy轴标签及曲线标题的颜色
         myMultipleSeriesRenderer.setXLabels(12);//设置X轴标签的个数
         myMultipleSeriesRenderer.setYLabels(15);//设置Y轴标签的个数
         myMultipleSeriesRenderer.setXLabelsAlign(Align.LEFT);//设数字标签位置
         myMultipleSeriesRenderer.setYLabelsAlign(Align.RIGHT);
         myMultipleSeriesRenderer.setAxisTitleTextSize(20);
         myMultipleSeriesRenderer.setChartTitleTextSize(30);
         myMultipleSeriesRenderer.setLabelsTextSize(20);
         myMultipleSeriesRenderer.setLegendTextSize(20);
         myMultipleSeriesRenderer.setPointSize(2f);//曲线描点尺寸
         myMultipleSeriesRenderer.setFitLegend(true);//Sets if the legend should size to fit. 
         myMultipleSeriesRenderer.setMargins(new int[] { 50, 45, 40, 10 });
         //the margin size values, in this order: top, left, bottom, right
         myMultipleSeriesRenderer.setShowGrid(true);
         myMultipleSeriesRenderer.setZoomEnabled(true, true);//X，Y缩放
         myMultipleSeriesRenderer.setZoomButtonsVisible(true);//显示缩放工具
         myMultipleSeriesRenderer.setPanLimits(new double[] {0, 26, 0, 100});
         myMultipleSeriesRenderer.setZoomLimits(new double[] {0, 26, 0, 100});
         myMultipleSeriesRenderer.setAxesColor(axeColor);
         myMultipleSeriesRenderer.setGridColor(gridColor);
         myMultipleSeriesRenderer.setBackgroundColor(Color.WHITE);//背景色
         myMultipleSeriesRenderer.setMarginsColor(Color.BLACK);//边距背景色，默认背景色为黑色，这里修改为白色
         myRenderer = new XYSeriesRenderer();
         myRenderer.setColor(curveColor);
         myRenderer.setPointStyle(PointStyle.CIRCLE);//描点风格，可以为圆点，方形点等等
//         mRenderer.setFillPoints(true);//设置描点是否为实心
         myMultipleSeriesRenderer.addSeriesRenderer(myRenderer);
     }
 
     /**
      * 根据新加的数据，更新曲线，只能运行在主线程
      * 
      * @param x
      *            新加点的x坐标
      * @param y
      *            新加点的y坐标
      */
     public void updateChart(double x, double y) {
         mySeries.add(x, y);
         myGraphicalView.repaint();//此处也可以调用invalidate()
     } 
     
     /**
      * 添加新的数据，多组，更新曲线，只能运行在主线程
      * @param xList
      * @param yList
      */
     public void updateChart(List<Double> xList, List<Double> yList) {
    	 mySeries.clear();
         for (int i = 0; i < xList.size(); i++) {
             mySeries.add(xList.get(i), yList.get(i));
         }
         myGraphicalView.repaint();//此处也可以调用invalidate()
     }
}
