/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hibernatus.hibmobtech.achartengine;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.hibernatus.hibmobtech.achartengine.XYMultipleSeriesRenderer.Orientation;
import com.hibernatus.hibmobtech.model.XYMultipleSeriesDataset;
import com.hibernatus.hibmobtech.model.XYSeries;

import java.io.Serializable;
import java.util.List;

/**
 * The combined XY com.bleau.hibernatus.mob.achartengine.chart rendering class.
 */
public class CombinedXYChart extends XYChart {

  private XYCombinedChartDef[] chartDefinitions;

  /** The embedded XY charts. */
  private XYChart[] mCharts;

  /** The supported charts for being combined. */
  private Class<?>[] xyChartTypes = new Class<?>[] { TimeChart.class, LineChart.class,
      CubicLineChart.class, BarChart.class, BubbleChart.class, ScatterChart.class,
      RangeBarChart.class, RangeStackedBarChart.class };

  /**
   * Builds a new combined XY com.bleau.hibernatus.mob.achartengine.chart instance.
   * 
   * @param dataset the multiple series dataset
   * @param renderer the multiple series com.bleau.hibernatus.mob.achartengine.chart.renderer
   * @param chartDefinitions the XY com.bleau.hibernatus.mob.achartengine.chart definitions
   */
  public CombinedXYChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer,
      XYCombinedChartDef[] chartDefinitions) {
    super(dataset, renderer);
    this.chartDefinitions = chartDefinitions;
    int length = chartDefinitions.length;
    mCharts = new XYChart[length];
    for (int i = 0; i < length; i++) {
      try {
        mCharts[i] = getXYChart(chartDefinitions[i].getType());
      } catch (Exception e) {
        // ignore
      }
      if (mCharts[i] == null) {
        throw new IllegalArgumentException("Unknown type " + chartDefinitions[i].getType());
      } else {
        XYMultipleSeriesDataset newDataset = new XYMultipleSeriesDataset();
        XYMultipleSeriesRenderer newRenderer = new XYMultipleSeriesRenderer();
        for (int seriesIndex : chartDefinitions[i].getSeriesIndex()) {
          newDataset.addSeries(dataset.getSeriesAt(seriesIndex));
          newRenderer.addSeriesRenderer(renderer.getSeriesRendererAt(seriesIndex));
        }
        newRenderer.setBarSpacing(renderer.getBarSpacing());
        newRenderer.setPointSize(renderer.getPointSize());

        mCharts[i].setDatasetRenderer(newDataset, newRenderer);
      }
    }
  }

  /**
   * Builds a new combined XY com.bleau.hibernatus.mob.achartengine.chart instance using the given charts. This allows
   * users to specify parameters like smoothness for CombinedXYCharts.
   *
   * @param dataset
   * @param renderer
   * @param chartDefinitions
   * @param charts
   */
  public CombinedXYChart(XYMultipleSeriesDataset dataset, XYMultipleSeriesRenderer renderer,
                         CombinedXYChart.XYCombinedChartDef[] chartDefinitions, XYChart[] charts) {
    super(dataset, renderer);
    this.chartDefinitions = chartDefinitions;
    this.mCharts = charts;
  }

  /**
   * Returns a com.bleau.hibernatus.mob.achartengine.chart instance based on the provided type.
   * 
   * @param type the com.bleau.hibernatus.mob.achartengine.chart type
   * @return an instance of a com.bleau.hibernatus.mob.achartengine.chart implementation
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  private XYChart getXYChart(String type) throws IllegalAccessException, InstantiationException {
    XYChart chart = null;
    int length = xyChartTypes.length;
    for (int i = 0; i < length && chart == null; i++) {
      XYChart newChart = (XYChart) xyChartTypes[i].newInstance();
      if (type.equals(newChart.getChartType())) {
        chart = newChart;
      }
    }
    return chart;
  }

  /**
   * The graphical representation of a series.
   * 
   * @param canvas the canvas to paint to
   * @param paint the paint to be used for drawing
   * @param points the array of points to be used for drawing the series
   * @param seriesRenderer the series com.bleau.hibernatus.mob.achartengine.chart.renderer
   * @param yAxisValue the minimum value of the y axis
   * @param seriesIndex the index of the series currently being drawn
   * @param startIndex the start index of the rendering points
   */
  @Override
  public void drawSeries(Canvas canvas, Paint paint, List<Float> points,
      XYSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, int startIndex) {
    XYChart chart = getXYChart(seriesIndex);
    chart.setScreenR(getScreenR());
    chart.setCalcRange(getCalcRange(mDataset.getSeriesAt(seriesIndex).getScaleNumber()), 0);
    chart.drawSeries(canvas, paint, points, seriesRenderer, yAxisValue,
        getChartSeriesIndex(seriesIndex), startIndex);
  }

  @Override
  protected ClickableArea[] clickableAreasForPoints(List<Float> points, List<Double> values,
      float yAxisValue, int seriesIndex, int startIndex) {
    XYChart chart = getXYChart(seriesIndex);
    return chart.clickableAreasForPoints(points, values, yAxisValue,
        getChartSeriesIndex(seriesIndex), startIndex);
  }

  @Override
  protected void drawSeries(XYSeries series, Canvas canvas, Paint paint, List<Float> pointsList,
      XYSeriesRenderer seriesRenderer, float yAxisValue, int seriesIndex, Orientation or,
      int startIndex) {
    XYChart chart = getXYChart(seriesIndex);
    chart.setScreenR(getScreenR());
    chart.setCalcRange(getCalcRange(mDataset.getSeriesAt(seriesIndex).getScaleNumber()), 0);
    chart.drawSeries(series, canvas, paint, pointsList, seriesRenderer, yAxisValue,
        getChartSeriesIndex(seriesIndex), or, startIndex);
  }

  /**
   * Returns the legend shape width.
   * 
   * @param seriesIndex the series index
   * @return the legend shape width
   */
  public int getLegendShapeWidth(int seriesIndex) {
    XYChart chart = getXYChart(seriesIndex);
    return chart.getLegendShapeWidth(getChartSeriesIndex(seriesIndex));
  }

  /**
   * The graphical representation of the legend shape.
   * 
   * @param canvas the canvas to paint to
   * @param renderer the series com.bleau.hibernatus.mob.achartengine.chart.renderer
   * @param x the x value of the point the shape should be drawn at
   * @param y the y value of the point the shape should be drawn at
   * @param seriesIndex the series index
   * @param paint the paint to be used for drawing
   */
  public void drawLegendShape(Canvas canvas, SimpleSeriesRenderer renderer, float x, float y,
      int seriesIndex, Paint paint) {
    XYChart chart = getXYChart(seriesIndex);
    chart.drawLegendShape(canvas, renderer, x, y, getChartSeriesIndex(seriesIndex), paint);
  }

  /**
   * Returns the com.bleau.hibernatus.mob.achartengine.chart type identifier.
   * 
   * @return the com.bleau.hibernatus.mob.achartengine.chart type
   */
  public String getChartType() {
    return "Combined";
  }

  private XYChart getXYChart(int seriesIndex) {
    for (int i = 0; i < chartDefinitions.length; i++) {
      if (chartDefinitions[i].containsSeries(seriesIndex)) {
        return mCharts[i];
      }
    }
    throw new IllegalArgumentException("Unknown series with index " + seriesIndex);
  }

  private int getChartSeriesIndex(int seriesIndex) {
    for (int i = 0; i < chartDefinitions.length; i++) {
      if (chartDefinitions[i].containsSeries(seriesIndex)) {
        return chartDefinitions[i].getChartSeriesIndex(seriesIndex);
      }
    }
    throw new IllegalArgumentException("Unknown series with index " + seriesIndex);
  }

  /**
   * Definition of a com.bleau.hibernatus.mob.achartengine.chart inside a combined XY com.bleau.hibernatus.mob.achartengine.chart.
   */
  public static class XYCombinedChartDef implements Serializable {
    /** The com.bleau.hibernatus.mob.achartengine.chart type. */
    private String type;
    /** The series index. */
    private int[] seriesIndex;

    /**
     * Constructs a com.bleau.hibernatus.mob.achartengine.chart definition.
     * 
     * @param type XY com.bleau.hibernatus.mob.achartengine.chart type
     * @param seriesIndex corresponding data series indexes
     */
    public XYCombinedChartDef(String type, int... seriesIndex) {
      this.type = type;
      this.seriesIndex = seriesIndex;
    }

    public boolean containsSeries(int seriesIndex) {
      return getChartSeriesIndex(seriesIndex) >= 0;
    }

    public int getChartSeriesIndex(int seriesIndex) {
      for (int i = 0; i < getSeriesIndex().length; i++) {
        if (this.seriesIndex[i] == seriesIndex) {
          return i;
        }
      }
      return -1;
    }

    public String getType() {
      return type;
    }

    public int[] getSeriesIndex() {
      return seriesIndex;
    }
  }

}
