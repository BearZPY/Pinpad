package com.io.pyz.pinpad.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;
import java.util.Random;

public class Pinpad extends View{

    private Paint tablePaint = new Paint();
    private Paint keyPaint = new Paint();
    private int[] numBuffer = new int[10];
    private int baseLineY = 0;
    private int startX = 20;
    private int startY = 20;
    private int height = 300;
    private int width = 230;
    private int selectCount = -1;
//    private OnItemSelectListener onItemSelectListener = null;


    public Pinpad(Context context) {
        super(context);
    }

    public Pinpad(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        // 初始化随机 KeyValue
        initKeyValue();
        // 初始化 KeyValue 画笔
        initKeyValuePaint();
        // 初始化 Table 画笔
        initTablePaint();
    }

    public Pinpad(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initKeyValue(){
        LinkedList<Integer> list = new LinkedList<>();
        for(int i = 0; i < 10; i++) {
            list.add(i);
        }
        Random r = new Random();
        for(int i = 10, temp; i > 0; i--){
            temp = r.nextInt(i);
            numBuffer[10 - i] = list.get(temp);
            list.remove(temp);
        }
    }

    private void initKeyValuePaint(){
        keyPaint.setTextSize(50);
        keyPaint.setColor(Color.BLACK);
        keyPaint.setStyle(Paint.Style.FILL);
        keyPaint.setAntiAlias(true);
        keyPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics metrics = keyPaint.getFontMetrics();
        baseLineY = (int)(metrics.top/2 + metrics.bottom/2);
    }

    private void initTablePaint(){
        tablePaint.setColor(Color.GRAY);
        tablePaint.setStyle(Paint.Style.STROKE);
        tablePaint.setStrokeWidth(2);
        tablePaint.setAntiAlias(true);
    }

//    public interface OnItemSelectListener{
//        public void onItemSelect(int key);
//    }
//
//    public void setOnItemSelectListener(OnItemSelectListener listener){
//        this.onItemSelectListener = listener;
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画框
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 3; j++){
                if(selectCount == (4 * i + j)){
                    tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                } else {
                    tablePaint.setStyle(Paint.Style.STROKE);
                }
                canvas.drawRect(
                        startX + j * width,
                        startY + i * height,
                        startX + (j + 1) * width,
                        startY + (i + 1) * height,
                        tablePaint);
            }
        }

        if(selectCount == 3){
            tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            tablePaint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawRect(
                startX + 3 * width,
                startY,
                startX + 4 * width,
                startY + 2 * height,
                tablePaint);

        if(selectCount == 11){
            tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            tablePaint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawRect(
                startX + 3 * width,
                startY + 2 * height,
                startX + 4 * width,
                startY + 4 * height,
                tablePaint);

        // 填充值
        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++){
                canvas.drawText(
                        String.valueOf(numBuffer[3*i+j]),
                        startX + j * width + width / 2,
                        startY +  i * height + height / 2 - baseLineY,
                        keyPaint);
            }
        }

        canvas.drawText(
                String.valueOf(numBuffer[9]),
                startX + width + width / 2,
                startY +  3 * height + height / 2 - baseLineY,
                keyPaint);

        canvas.drawText(
                "回退",
                startX + 3 * width + width / 2,
                startY + height - baseLineY,
                keyPaint);

        canvas.drawText(
                "确定",
                startX + 3 * width + width / 2,
                startY +  3 * height- baseLineY,
                keyPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(MotionEvent.ACTION_DOWN == event.getAction()){
            float x = event.getX();
            float y = event.getY();
            int selectColumn = (int)((x - startX) / width);
            int selectRow = (int)((y - startY) / height);
            selectCount = 4 * selectRow + selectColumn;
            if(selectCount == 12 || selectCount == 14){
                selectCount = -1;
            }
            if(selectCount == 7){
                selectCount = 3;
            }
            if(selectCount == 15){
                selectCount = 11;
            }
            invalidate();
            return true;
        }
        if(MotionEvent.ACTION_UP == event.getAction()){
            selectCount = -1;
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
