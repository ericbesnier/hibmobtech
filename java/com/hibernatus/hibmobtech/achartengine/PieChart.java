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
import android.graphics.Paint.Style;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.util.Log;

import com.hibernatus.hibmobtech.model.CategorySeries;
import com.hibernatus.hibmobtech.model.Point;
import com.hibernatus.hibmobtech.model.SeriesSelection;

import java.util.ArrayList;
import java.util.List;

/**
 * The pie rendering class.
 */
public class PieChart extends RoundChart {
    public static final String TAG = PieChart.class.getSimpleName();

    /** Handles returning values when tapping on PieChart. */
    private PieMapper mPieMapper;

    /**
     * Builds a new pie  instance.
     *
     * @param dataset the series dataset
     * @param renderer the series .renderer
     */
    public PieChart(CategorySeries dataset, DefaultRenderer renderer) {
        super(dataset, renderer);
        Log.d(TAG, "PieChart");
        mPieMapper = new PieMapper();
    }

    /**
     * The graphical representation of the pie .
     *
     * @param canvas the canvas to paint to
     * @param x the top left x value of the view to draw to
     * @param y the top left y value of the view to draw to
     * @param width the width of the view to draw to
     * @param height the height of the view to draw to
     * @param paint the paint
     */
    @Override
    public void draw(Canvas canvas, int x, int y, int width, int height, Paint paint) {
        //Log.d(TAG, "draw: x=" + x + " y=" + y);
        paint.setAntiAlias(mRenderer.isAntialiasing());
        paint.setStyle(Style.FILL);
        paint.setTextSize(mRenderer.getLabelsTextSize());
        int legendSize = getLegendSize(mRenderer, height / 5, 0);
        // TODO calculer la bonne hauteur par rapport à la taille de l'icone  du cluster
        y = y + 80;
        int left = x;
        int top = y;
        int right = x + width;
        int sLength = mDataset.getItemCount();
        double total = 0;
        String[] titles = new String[sLength];
        for (int i = 0; i < sLength; i++) {
            total += mDataset.getValue(i);
            titles[i] = mDataset.getCategory(i);
        }
        if (mRenderer.isFitLegend()) {
            legendSize = drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize,
                    paint, true);
        }
        int bottom = y + height - legendSize;
        drawBackground(mRenderer, canvas, x, y, width, height, paint, false, DefaultRenderer.NO_COLOR);

        float currentAngle = mRenderer.getStartAngle();
        int mRadius = Math.min(Math.abs(right - left), Math.abs(bottom - top));
        int radius = (int) (mRadius * 0.35 * mRenderer.getScale());
        Log.d(TAG, "draw : radius=" + radius);
        if (mCenterX == NO_VALUE) {
            mCenterX = (left + right) / 2;
        }
        if (mCenterY == NO_VALUE) {
            mCenterY = (bottom + top) / 2;
        }

        // Hook in clip detection after center has been calculated
        mPieMapper.setDimensions(radius, mCenterX, mCenterY);
        boolean loadPieCfg = !mPieMapper.areAllSegmentPresent(sLength);
        if (loadPieCfg) {
            mPieMapper.clearPieSegments();
        }

        float shortRadius = radius * 0.9f;
        float longRadius = radius * 1.1f;
        Log.d(TAG, "draw : shortRadius=" + shortRadius + " longRadius=" + longRadius);
        RectF oval = new RectF(mCenterX - radius, mCenterY - radius, mCenterX + radius, mCenterY
                + radius);
        List<RectF> prevLabelsBounds = new ArrayList<RectF>();

        for (int i = 0; i < sLength; i++) {
            SimpleSeriesRenderer seriesRenderer = mRenderer.getSeriesRendererAt(i);
            if (seriesRenderer.isGradientEnabled()) {
                RadialGradient grad = new RadialGradient(mCenterX, mCenterY, longRadius,
                        seriesRenderer.getGradientStartColor(), seriesRenderer.getGradientStopColor(),
                        TileMode.MIRROR);
                paint.setShader(grad);
            } else {
                paint.setColor(seriesRenderer.getColor());
            }

            float value = (float) mDataset.getValue(i);
            float angle = (float) (value / total * 360);
            if (seriesRenderer.isHighlighted()) {
                double rAngle = Math.toRadians(90 - (currentAngle + angle / 2));
                float translateX = (float) (radius * 0.1 * Math.sin(rAngle));
                float translateY = (float) (radius * 0.1 * Math.cos(rAngle));
                oval.offset(translateX, translateY);
                canvas.drawArc(oval, currentAngle, angle, true, paint);
                oval.offset(-translateX, -translateY);
            } else {
                canvas.drawArc(oval, currentAngle, angle, true, paint);
            }
            paint.setColor(seriesRenderer.getColor());
            paint.setShader(null);

           /*
            * Ne pas supprimer : à decommenter pour avoir les flèches & labels

            drawLabel(canvas, mDataset.getCategory(i), mRenderer, prevLabelsBounds, mCenterX, mCenterY,
                    shortRadius, longRadius, currentAngle, angle, left, right, mRenderer.getLabelsColor(),
                    paint, true, false);
            */

            if (mRenderer.isDisplayValues()) {
                Log.d(TAG, "draw : i=" + i + " mDataset.getValue(i)=" + mDataset.getValue(i)
                + " prevLabelsBounds=" + prevLabelsBounds+ " mCenterX=" + mCenterX + " mCenterY=" + mCenterY
                + " shortRadius=" + shortRadius + " longRadius=" + longRadius
                + " currentAngle=" + currentAngle + " angle=" + angle
                + " left=" + left + " right=" + right);
                myDrawLabel(
                        canvas,
                        getLabel(mRenderer.getSeriesRendererAt(i).getChartValuesFormat(), mDataset.getValue(i)),
                        mRenderer, mCenterX, mCenterY, radius,
                        currentAngle, angle, mRenderer.getLabelsColor(), paint);
            }

            // Save details for getSeries functionality
            if (loadPieCfg) {
                mPieMapper.addPieSegment(i, value, currentAngle, angle);
            }
            currentAngle += angle;
        }
        prevLabelsBounds.clear();
        if (mRenderer.isDisplayLegend()) {
            drawLegend(canvas, mRenderer, titles, left, right, y, width, height, legendSize, paint, false);
        }
        drawTitle(canvas, x, y, width, paint);
        // drawTitle(canvas, x, y + (width / 3) - 10, width, paint); pour centrer le titre
    }

    public SeriesSelection getSeriesAndPointForScreenCoordinate(Point screenPoint) {
        return mPieMapper.getSeriesAndPointForScreenCoordinate(screenPoint);
    }

}
