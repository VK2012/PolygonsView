package com.vk.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VK on 2016/10/14.
 */

public class PolygonsView extends View {

    private String tag = getClass().getSimpleName();

    private int mWidth, mHeight;

    /**中心点位置*/
    private int center;

    private static final int DEFAULT_SIZE = 300;

    private static final int TEXTSIZE = 12;

    private String[] strs = {"击杀", "生存", "助攻", "物理", "魔法", "防御", "金钱",};

    /**单个指标的文本边界尺寸*/
    private Rect str_rect;

    private static final int STROKE_WIDTH = 4;//线段宽度

    private int superRadius;//外圆半径

    private int bigRadius;//

    private int normalRadius;//

    private int smalllRadius;//最内圆半径

    /**最外多边形路径*/
    private Path superPath;

    private Path bigPath;

    private Path normalPath;

    private Path smallPath;

    private List<Pair<Float, Float>> superPoints = new ArrayList<>();

    private List<Pair<Float, Float>> realPoints = new ArrayList<>();

    private List<Pair<Float, Float>> textPoints = new ArrayList<>();

    private static final int SMALL_COLOR = 0xff2e8c96;//0xff2e8c96

    private static final int NORMAL_COLOR = 0xff5bc1c6;

    private static final int BIG_COLOR = 0xff96dde3;// 0xff96dde3

    private static final int SUPER_COLOR = 0xffd7eff3;

    private static final int FRAME_LINE_COLOR = 0xffafdbda;//框架线颜色

    private static final int REAL_LINE_COLOR = 0xffde6753;//真实数据形状颜色

    double radian = Math.toRadians(360 / 7);

    /**击杀*/
    public static final int TYPE_KILL = 0;

    /**生存*/
    public static final int TYPE_SURVIVE = 1;

    /**助攻*/
    public static final int TYPE_ASSIST = 2;

    /**物理*/
    public static final int TYPE_PHYSICAL = 3;

    /**魔法*/
    public static final int TYPE_MAGIC = 4;

    /**防御*/
    public static final int TYPE_DEFENSE = 5;

    /**金钱*/
    public static final int TYPE_MONEY = 6;

    private float default_strength_kill = 1.0f;

    private float default_strength_survive = 1.0f;

    private float default_strength_assist = 1.0f;

    private float default_strength_physical = 1.0f;

    private float default_strength_magic = 1.0f;

    private float default_strength_defense = 1.0f;

    private float default_strength_money = 1.0f;

    public PolygonsView(Context context) {
        super(context);
        init(context);
    }

    public PolygonsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PolygonsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /***
     * 初始化
     * @param context
     */
    private void init(Context context) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速
        str_rect = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXTSIZE, getResources().getDisplayMetrics()));
        paint.getTextBounds(strs[0], 0, strs[0].length(), str_rect);//测量文本边界尺寸
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(tag,"onDraw");
        //绘制多边形的顺序不能改变
        drawSuperPolygon(canvas);
        drawBigPolygon(canvas);
        drawNormalPolygon(canvas);
        drawSmallPolygon(canvas);

        //绘制框架线
        drawFrameLine(canvas);

        //绘制真实数据形状
        drawRealPolygon(canvas);

        //绘制各项指标文本
        drawText(canvas);
    }

    private void drawPolygon(Canvas canvas, int color, Path path, Path excludePath) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);

        canvas.save();
        //剪裁，避免绘制重复区域
        if (excludePath != null)
            canvas.clipPath(excludePath, Region.Op.DIFFERENCE);
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    /**
     * 绘制最外多边形
     */
    private void drawSuperPolygon(Canvas canvas) {
        drawPolygon(canvas, SUPER_COLOR, superPath, bigPath);
    }

    /**
     * @param canvas
     */
    private void drawBigPolygon(Canvas canvas) {
        drawPolygon(canvas, BIG_COLOR, bigPath, normalPath);
    }

    /**
     * @param canvas
     */
    private void drawNormalPolygon(Canvas canvas) {
        drawPolygon(canvas, NORMAL_COLOR, normalPath, smallPath);
    }

    /**
     * 绘制最内多边形
     *
     * @param canvas
     */
    private void drawSmallPolygon(Canvas canvas) {
        drawPolygon(canvas, SMALL_COLOR, smallPath, null);
    }

    /**
     * 绘制框架线
     *
     * @param canvas
     */
    private void drawFrameLine(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(FRAME_LINE_COLOR);
        paint.setStyle(Paint.Style.STROKE);

        canvas.save();
        //外边线
        canvas.drawPath(superPath, paint);
        canvas.restore();
        canvas.save();
        //外边顶点到圆心半径线
        for (int i = 0; i < strs.length; i++) {
            canvas.drawLine(center, center, superPoints.get(i).first, superPoints.get(i).second, paint);
        }
        canvas.restore();
    }

    /**
     * 绘制真实数据形状
     */
    private void drawRealPolygon(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(REAL_LINE_COLOR);
        paint.setStyle(Paint.Style.STROKE);

        Path mPath = new Path();
        mPath.moveTo(realPoints.get(0).first, realPoints.get(0).second);
        for (int i = 1; i < realPoints.size(); i++)
            mPath.lineTo(realPoints.get(i).first, realPoints.get(i).second);
        mPath.lineTo(realPoints.get(0).first, realPoints.get(0).second);
        canvas.save();
        canvas.drawPath(mPath, paint);
        canvas.restore();
    }

    /**
     * 绘制各项指标文本
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, TEXTSIZE, getResources().getDisplayMetrics()));

        canvas.save();
        for (int i = 0; i < textPoints.size(); i++) {
            canvas.drawText(strs[i], textPoints.get(i).first, textPoints.get(i).second, paint);
//            Log.d(tag, strs[i] + " ,x=" + textPoints.get(i).first + " ,y=" + textPoints.get(i).second);
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(tag,"onMeasure");
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

//        ViewGroup father = (ViewGroup) getParent();
//        int useHeight = father.getHeight() - father.getPaddingTop() -father.getPaddingBottom();
//        int useWidth = father.getWidth() - father.getPaddingLeft() -father.getPaddingRight();

        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST) {
//            mWidth = Math.min(width,useWidth);
            mWidth = width;
        } else {
            mWidth = Math.min(width, DEFAULT_SIZE);
        }

        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST) {
//            mHeight = Math.min(height,useHeight);
            mHeight = height;
        } else {
            mHeight = Math.min(height, DEFAULT_SIZE);
        }

        mWidth = mHeight = Math.min(mWidth, mHeight);//正方形

        center = mWidth / 2;//中心点

        superRadius = Math.min(center - getPaddingTop() - 2 * str_rect.height(), center - getPaddingRight() - 2 * str_rect.width());//外圆半径
        bigRadius = superRadius * 3 / 4;
        normalRadius = superRadius / 2;
        smalllRadius = superRadius / 4;

        computeAllPaths();
        computeTextLocation(superRadius);
        setMeasuredDimension(mWidth, mHeight);

    }

    /**
     * 计算所有多边形路径
     */
    private void computeAllPaths() {
        superPath = computPath(superRadius);
        bigPath = computPath(bigRadius);
        normalPath = computPath(normalRadius);
        smallPath = computPath(smalllRadius);
    }

    /**
     * 计算单个多边形路径
     *
     * @param radius
     * @return
     */
    private Path computPath(int radius) {

        Pair<Float, Float> tA, tB, tC, tD, tE, tF, tG;

        tA = new Pair<>((float) center, (float) (center - radius));
        tB = new Pair<>((float) (center + radius * Math.sin(radian)), (float) (center - Math.abs(radius * Math.cos(radian))));
        tC = new Pair<>((float) (center + radius * Math.sin(radian / 2 + radian)), (float) (center + Math.abs(radius * Math.cos(radian / 2 + radian))));
        tD = new Pair<>((float) (center + radius * Math.sin(radian / 2)), (float) (center + Math.abs(radius * Math.cos(radian / 2))));
        tE = new Pair<>((float) (center - radius * Math.sin(radian / 2)), (float) (center + Math.abs(radius * Math.cos(radian / 2))));
        tF = new Pair<>((float) (center - radius * Math.sin(radian / 2 + radian)), (float) (center + Math.abs(radius * Math.cos(radian / 2 + radian))));
        tG = new Pair<>((float) (center - radius * Math.sin(radian)), (float) (center - Math.abs(radius * Math.cos(radian))));

        Path tmpPath = new Path();

        //A点 顶点顺时针
        tmpPath.moveTo(tA.first, tA.second);
        //B点
        tmpPath.lineTo(tB.first, tB.second);
        //C点
        tmpPath.lineTo(tC.first, tC.second);
        //D点
        tmpPath.lineTo(tD.first, tD.second);
        //E点
        tmpPath.lineTo(tE.first, tE.second);
        //F点
        tmpPath.lineTo(tF.first, tF.second);
        //G点
        tmpPath.lineTo(tG.first, tG.second);
        //H点 与A点重合闭环
        tmpPath.lineTo(tA.first, tA.second);

        if (radius == superRadius) {
            superPoints.clear();
            superPoints.add(tA);
            superPoints.add(tB);
            superPoints.add(tC);
            superPoints.add(tD);
            superPoints.add(tE);
            superPoints.add(tF);
            superPoints.add(tG);

            if(realPoints.size() == 0) {
                realPoints.add(null);
                realPoints.add(null);
                realPoints.add(null);
                realPoints.add(null);
                realPoints.add(null);
                realPoints.add(null);
                realPoints.add(null);
            }

            //刷新默认强度
            refreshStrength(TYPE_KILL,default_strength_kill);
            refreshStrength(TYPE_PHYSICAL,default_strength_physical);
            refreshStrength(TYPE_SURVIVE,default_strength_survive);
            refreshStrength(TYPE_ASSIST,default_strength_assist);
            refreshStrength(TYPE_DEFENSE,default_strength_defense);
            refreshStrength(TYPE_MAGIC,default_strength_magic);
            refreshStrength(TYPE_MONEY,default_strength_money);
        }

        return tmpPath;
    }

    /**
     * 计算所有指标文本坐标位置
     */
    private void computeTextLocation(int radius) {

        Pair<Float, Float> tA, tB, tC, tD, tE, tF, tG;

        tA = new Pair<>((float) center - str_rect.width() / 2, (float) (center - radius - str_rect.height()));
        tB = new Pair<>((float) (center + radius * Math.sin(radian) + str_rect.width() / 4), (float) (center - Math.abs(radius * Math.cos(radian))));
        tC = new Pair<>((float) (center + radius * Math.sin(radian / 2 + radian) + str_rect.width() / 2), (float) (center + Math.abs(radius * Math.cos(radian / 2 + radian)) + str_rect.height() * 3 / 4));
        tD = new Pair<>((float) (center + radius * Math.sin(radian / 2) - str_rect.width() / 2), (float) (center + Math.abs(radius * Math.cos(radian / 2))) + 2 * str_rect.height());
        tE = new Pair<>((float) (center - radius * Math.sin(radian / 2) - str_rect.width() * 3 / 4), (float) (center + Math.abs(radius * Math.cos(radian / 2))) + 2 * str_rect.height());
        tF = new Pair<>((float) (center - radius * Math.sin(radian / 2 + radian) - str_rect.width() * 3 / 2), (float) (center + Math.abs(radius * Math.cos(radian / 2 + radian))) + str_rect.height() * 3 / 4);
        tG = new Pair<>((float) (center - radius * Math.sin(radian) - str_rect.width() * 5 / 4), (float) (center - Math.abs(radius * Math.cos(radian))));

        textPoints.clear();
        textPoints.add(tA);
        textPoints.add(tB);
        textPoints.add(tC);
        textPoints.add(tD);
        textPoints.add(tE);
        textPoints.add(tF);
        textPoints.add(tG);

    }

    /**
     * 刷新某项的实力强度
     *
     * @param type
     * @param strength 百分比 0-100%
     */
    private void refreshStrength(int type, float strength){
        if (type <= -1 || type >= realPoints.size()) {
            return;
        }
        Pair<Float, Float> newPoint = null;

        float radius = superRadius * strength;
        switch (type) {
            case TYPE_KILL:
                newPoint = new Pair<>((float) center, (float) (center - radius));
                break;
            case TYPE_SURVIVE:
                newPoint = new Pair<>((float) (center + radius * Math.sin(radian)), (float) (center - Math.abs(radius * Math.cos(radian))));
                break;
            case TYPE_ASSIST:
                newPoint = new Pair<>((float) (center + radius * Math.sin(radian / 2 + radian)), (float) (center + Math.abs(radius * Math.cos(radian / 2 + radian))));
                break;
            case TYPE_PHYSICAL:
                newPoint = new Pair<>((float) (center + radius * Math.sin(radian / 2)), (float) (center + Math.abs(radius * Math.cos(radian / 2))));
                break;
            case TYPE_MAGIC:
                newPoint = new Pair<>((float) (center - radius * Math.sin(radian / 2)), (float) (center + Math.abs(radius * Math.cos(radian / 2))));
                break;
            case TYPE_DEFENSE:
                newPoint = new Pair<>((float) (center - radius * Math.sin(radian / 2 + radian)), (float) (center + Math.abs(radius * Math.cos(radian / 2 + radian))));
                break;
            case TYPE_MONEY:
                newPoint = new Pair<>((float) (center - radius * Math.sin(radian)), (float) (center - Math.abs(radius * Math.cos(radian))));
                break;
            default:
                throw new IllegalArgumentException("Strength type is not match : "+type);
        }
        //新值替换
        realPoints.set(type, newPoint);
    }

    /**
     * 设置某项的实力强度并更新视图，{@link com.www.myapplication.view.PolygonsView#onMeasure(int, int)} 完成视图测量之前该方法调用无效。
     * <p>若需初始化默认值请调用{@link com.www.myapplication.view.PolygonsView#setStrength(int, float)}。
     *
     * @param type {@link com.www.myapplication.view.PolygonsView#TYPE_KILL},{@link com.www.myapplication.view.PolygonsView#TYPE_ASSIST}...
     * @param strength 百分比 0-1.0
     */
    public void setStrength(int type, float strength) {
        refreshStrength(type,strength);
        invalidate();
    }

    /**
     * 设置默认强度
     * @param type {@link com.www.myapplication.view.PolygonsView#TYPE_KILL},{@link com.www.myapplication.view.PolygonsView#TYPE_ASSIST}...
     * @param strength 百分比 0-1.0
     */
    public void setDefaultStrength(int type, float strength){
        switch (type) {
            case TYPE_KILL:
                default_strength_kill = strength;
                break;
            case TYPE_SURVIVE:
                default_strength_survive = strength;
                break;
            case TYPE_ASSIST:
                default_strength_assist = strength;
                break;
            case TYPE_PHYSICAL:
                default_strength_physical = strength;
                break;
            case TYPE_MAGIC:
                default_strength_magic = strength;
                break;
            case TYPE_DEFENSE:
                default_strength_defense = strength;
                break;
            case TYPE_MONEY:
                default_strength_money = strength;
                break;
            default:
                throw new IllegalArgumentException("Strength type is not match : " + type);
        }
    }

}
