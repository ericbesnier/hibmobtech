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

import android.graphics.Color;
import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A com.bleau.hibernatus.mob.achartengine.chart.renderer for the XY type series.
 */
public class XYSeriesRenderer extends SimpleSeriesRenderer {
  /** If the com.bleau.hibernatus.mob.achartengine.chart points should be filled. */
  private boolean mFillPoints = false;
  /** If the com.bleau.hibernatus.mob.achartengine.chart should be filled outside its line. */
  private List<FillOutsideLine> mFillBelowLine = new ArrayList<FillOutsideLine>();
  /** The point style. */
  private PointStyle mPointStyle = PointStyle.POINT;
  /** The point stroke width */
  private float mPointStrokeWidth = 1;
  /** The com.bleau.hibernatus.mob.achartengine.chart line width. */
  private float mLineWidth = 1;
  /** If the values should be displayed above the com.bleau.hibernatus.mob.achartengine.chart points. */
  private boolean mDisplayChartValues;
  /** The minimum distance between displaying com.bleau.hibernatus.mob.achartengine.chart values. */
  private int mDisplayChartValuesDistance = 100;
  /** The com.bleau.hibernatus.mob.achartengine.chart values text size. */
  private float mChartValuesTextSize = 10;
  /** The com.bleau.hibernatus.mob.achartengine.chart values text alignment. */
  private Paint.Align mChartValuesTextAlign = Paint.Align.CENTER;
  /** The com.bleau.hibernatus.mob.achartengine.chart values spacing from the data point. */
  private float mChartValuesSpacing = 5f;
  /** The annotations text size. */
  private float mAnnotationsTextSize = 10;
  /** The annotations text alignment. */
  private Paint.Align mAnnotationsTextAlign = Paint.Align.CENTER;
  /** The annotations color. */
  private int mAnnotationsColor = DefaultRenderer.TEXT_COLOR;

  /**
   * A descriptor for the line fill behavior.
   */
  public static class FillOutsideLine implements Serializable {
    public enum Type {
      NONE, BOUNDS_ALL, BOUNDS_BELOW, BOUNDS_ABOVE, BELOW, ABOVE
    };

    /** The fill type. */
    private final Type mType;
    /** The fill color. */
    private int mColor = Color.argb(125, 0, 0, 200);
    /** The fill points index range. */
    private int[] mFillRange;

    /**
     * The line fill behavior.
     * 
     * @param type the fill type
     */
    public FillOutsideLine(Type type) {
      this.mType = type;
    }

    /**
     * Returns the fill color.
     * 
     * @return the fill color
     */
    public int getColor() {
      return mColor;
    }

    /**
     * Sets the fill color
     * 
     * @param color the fill color
     */
    public void setColor(int color) {
      mColor = color;
    }

    /**
     * Returns the fill type.
     * 
     * @return the fill type
     */
    public Type getType() {
      return mType;
    }

    /**
     * Returns the fill range which is the minimum and maximum data index values
     * for the fill.
     * 
     * @return the fill range
     */
    public int[] getFillRange() {
      return mFillRange;
    }

    /**
     * Sets the fill range which is the minimum and maximum data index values
     * for the fill.
     * 
     * @param range the fill range
     */
    public void setFillRange(int[] range) {
      mFillRange = range;
    }
  }

  /**
   * Returns if the com.bleau.hibernatus.mob.achartengine.chart should be filled below the line.
   * 
   * @return the fill below line status
   * 
   * @deprecated Use {@link #getFillOutsideLine()} instead.
   */
  @Deprecated
  public boolean isFillBelowLine() {
    return mFillBelowLine.size() > 0;
  }

  /**
   * Sets if the line com.bleau.hibernatus.mob.achartengine.chart should be filled below its line. Filling below the
   * line transforms a line com.bleau.hibernatus.mob.achartengine.chart into an area com.bleau.hibernatus.mob.achartengine.chart.
   * 
   * @param fill the fill below line flag value
   * 
   * @deprecated Use {@link #addFillOutsideLine(FillOutsideLine)} instead.
   */
  @Deprecated
  public void setFillBelowLine(boolean fill) {
    mFillBelowLine.clear();
    if (fill) {
      mFillBelowLine.add(new FillOutsideLine(FillOutsideLine.Type.BOUNDS_ALL));
    } else {
      mFillBelowLine.add(new FillOutsideLine(FillOutsideLine.Type.NONE));
    }
  }

  /**
   * Returns the type of the outside fill of the line.
   * 
   * @return the type of the outside fill of the line.
   */
  public FillOutsideLine[] getFillOutsideLine() {
    return mFillBelowLine.toArray(new FillOutsideLine[0]);
  }

  /**
   * Sets if the line com.bleau.hibernatus.mob.achartengine.chart should be filled outside its line. Filling outside
   * with FillOutsideLine.INTEGRAL the line transforms a line com.bleau.hibernatus.mob.achartengine.chart into an area
   * com.bleau.hibernatus.mob.achartengine.chart.
   * 
   * @param fill the type of the filling
   */
  public void addFillOutsideLine(FillOutsideLine fill) {
    mFillBelowLine.add(fill);
  }

  /**
   * Returns if the com.bleau.hibernatus.mob.achartengine.chart points should be filled.
   * 
   * @return the points fill status
   */
  public boolean isFillPoints() {
    return mFillPoints;
  }

  /**
   * Sets if the com.bleau.hibernatus.mob.achartengine.chart points should be filled.
   * 
   * @param fill the points fill flag value
   */
  public void setFillPoints(boolean fill) {
    mFillPoints = fill;
  }

  /**
   * Sets the fill below the line color.
   * 
   * @param color the fill below line color
   * 
   * @deprecated Use FillOutsideLine.setColor instead
   */
  @Deprecated
  public void setFillBelowLineColor(int color) {
    if (mFillBelowLine.size() > 0) {
      mFillBelowLine.get(0).setColor(color);
    }
  }

  /**
   * Returns the point style.
   * 
   * @return the point style
   */
  public PointStyle getPointStyle() {
    return mPointStyle;
  }

  /**
   * Sets the point style.
   * 
   * @param style the point style
   */
  public void setPointStyle(PointStyle style) {
    mPointStyle = style;
  }

  /**
   * Returns the point stroke width in pixels.
   * 
   * @return the point stroke width in pixels
   */
  public float getPointStrokeWidth() {
    return mPointStrokeWidth;
  }

  /**
   * Sets the point stroke width in pixels.
   * 
   * @param strokeWidth the point stroke width in pixels
   */
  public void setPointStrokeWidth(float strokeWidth) {
    mPointStrokeWidth = strokeWidth;
  }

  /**
   * Returns the com.bleau.hibernatus.mob.achartengine.chart line width.
   * 
   * @return the line width
   */
  public float getLineWidth() {
    return mLineWidth;
  }

  /**
   * Sets the com.bleau.hibernatus.mob.achartengine.chart line width.
   * 
   * @param lineWidth the line width
   */
  public void setLineWidth(float lineWidth) {
    mLineWidth = lineWidth;
  }

  /**
   * Returns if the com.bleau.hibernatus.mob.achartengine.chart point values should be displayed as text.
   * 
   * @return if the com.bleau.hibernatus.mob.achartengine.chart point values should be displayed as text
   */
  public boolean isDisplayChartValues() {
    return mDisplayChartValues;
  }

  /**
   * Sets if the com.bleau.hibernatus.mob.achartengine.chart point values should be displayed as text.
   * 
   * @param display if the com.bleau.hibernatus.mob.achartengine.chart point values should be displayed as text
   */
  public void setDisplayChartValues(boolean display) {
    mDisplayChartValues = display;
  }

  /**
   * Returns the com.bleau.hibernatus.mob.achartengine.chart values minimum distance.
   * 
   * @return the com.bleau.hibernatus.mob.achartengine.chart values minimum distance
   */
  public int getDisplayChartValuesDistance() {
    return mDisplayChartValuesDistance;
  }

  /**
   * Sets com.bleau.hibernatus.mob.achartengine.chart values minimum distance.
   * 
   * @param distance the com.bleau.hibernatus.mob.achartengine.chart values minimum distance
   */
  public void setDisplayChartValuesDistance(int distance) {
    mDisplayChartValuesDistance = distance;
  }

  /**
   * Returns the com.bleau.hibernatus.mob.achartengine.chart values text size.
   * 
   * @return the com.bleau.hibernatus.mob.achartengine.chart values text size
   */
  public float getChartValuesTextSize() {
    return mChartValuesTextSize;
  }

  /**
   * Sets the com.bleau.hibernatus.mob.achartengine.chart values text size.
   * 
   * @param textSize the com.bleau.hibernatus.mob.achartengine.chart values text size
   */
  public void setChartValuesTextSize(float textSize) {
    mChartValuesTextSize = textSize;
  }

  /**
   * Returns the com.bleau.hibernatus.mob.achartengine.chart values text align.
   * 
   * @return the com.bleau.hibernatus.mob.achartengine.chart values text align
   */
  public Paint.Align getChartValuesTextAlign() {
    return mChartValuesTextAlign;
  }

  /**
   * Sets the com.bleau.hibernatus.mob.achartengine.chart values text align.
   * 
   * @param align the com.bleau.hibernatus.mob.achartengine.chart values text align
   */
  public void setChartValuesTextAlign(Paint.Align align) {
    mChartValuesTextAlign = align;
  }

  /**
   * Returns the com.bleau.hibernatus.mob.achartengine.chart values spacing from the data point.
   * 
   * @return the com.bleau.hibernatus.mob.achartengine.chart values spacing
   */
  public float getChartValuesSpacing() {
    return mChartValuesSpacing;
  }

  /**
   * Sets the com.bleau.hibernatus.mob.achartengine.chart values spacing from the data point.
   * 
   * @param spacing the com.bleau.hibernatus.mob.achartengine.chart values spacing (in pixels) from the com.bleau.hibernatus.mob.achartengine.chart data
   *          point
   */
  public void setChartValuesSpacing(float spacing) {
    mChartValuesSpacing = spacing;
  }

  /**
   * Returns the annotations text size.
   * 
   * @return the annotations text size
   */
  public float getAnnotationsTextSize() {
    return mAnnotationsTextSize;
  }

  /**
   * Sets the annotations text size.
   * 
   * @param textSize the annotations text size
   */
  public void setAnnotationsTextSize(float textSize) {
    mAnnotationsTextSize = textSize;
  }

  /**
   * Returns the annotations text align.
   * 
   * @return the annotations text align
   */
  public Paint.Align getAnnotationsTextAlign() {
    return mAnnotationsTextAlign;
  }

  /**
   * Sets the annotations text align.
   * 
   * @param align the com.bleau.hibernatus.mob.achartengine.chart values text align
   */
  public void setAnnotationsTextAlign(Paint.Align align) {
    mAnnotationsTextAlign = align;
  }

  /**
   * Returns the annotations color.
   * 
   * @return the annotations color
   */
  public int getAnnotationsColor() {
    return mAnnotationsColor;
  }

  /**
   * Sets the annotations color.
   * 
   * @param color the annotations color
   */
  public void setAnnotationsColor(int color) {
    mAnnotationsColor = color;
  }

}
