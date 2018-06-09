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

    private final static int MSG_UNSELECTED = -1;
    private final static int MSG_BACKSPACE = 3;
    private final static int MSG_ENTER = 11;
    private Paint tablePaint = new Paint();
    private Paint keyPaint = new Paint();
    private int[] numBuffer = new int[10];
    private int baseLineY = 0;
    private int startX = 20;
    private int startY = 20;
    private int height = 150;
    private int width = 230;
    private int startInputKeyX = startX;
    private int startInputKeyY = startY + height;
    private int selectCount = MSG_UNSELECTED;
    private StringBuilder password = new StringBuilder();
    private StringBuilder passwordValue = new StringBuilder();
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

    private void passwordHandle(int index){
        switch (selectCount){
            case MSG_UNSELECTED:
                return;
            case MSG_BACKSPACE:
                if(password.length() == 0){
                    return;
                }
                password.deleteCharAt(password.length()-1);
                passwordValue.deleteCharAt(passwordValue.length()-1);
                break;
            case MSG_ENTER:
                break;
            default:
                if(password.length() >= 6){
                    return;
                }
                password.append("*");
                if(index >= 9){
                    index = 9;
                }
                passwordValue.append(numBuffer[index]);
                break;
        }
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
        canvas.drawRect(
                startX,
                startY,
                startX + 4 * width,
                startInputKeyY,
                tablePaint);

        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < 3; j++){
                if(selectCount == (4 * i + j)){
                    tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                } else {
                    tablePaint.setStyle(Paint.Style.STROKE);
                }
                canvas.drawRect(
                        startInputKeyX + j * width,
                        startInputKeyY + i * height,
                        startInputKeyX + (j + 1) * width,
                        startInputKeyY + (i + 1) * height,
                        tablePaint);
            }
        }

        if(selectCount == 3){
            tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            tablePaint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawRect(
                startInputKeyX + 3 * width,
                startInputKeyY,
                startInputKeyX + 4 * width,
                startInputKeyY + 2 * height,
                tablePaint);

        if(selectCount == 11){
            tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        } else {
            tablePaint.setStyle(Paint.Style.STROKE);
        }
        canvas.drawRect(
                startInputKeyX + 3 * width,
                startInputKeyY + 2 * height,
                startInputKeyX + 4 * width,
                startInputKeyY + 4 * height,
                tablePaint);
        tablePaint.setStyle(Paint.Style.STROKE);

        // 填充值
        canvas.drawText(
                password.toString(),
                //passwordValue.toString(),
                startX + 2 * width,
                startY + height / 2 - baseLineY,
                keyPaint);

        for(int i = 0; i < 3; i++) {
            for(int j = 0; j < 3; j++){
                canvas.drawText(
                        String.valueOf(numBuffer[3*i+j]),
                        startInputKeyX + j * width + width / 2,
                        startInputKeyY +  i * height + height / 2 - baseLineY,
                        keyPaint);
            }
        }

        canvas.drawText(
                String.valueOf(numBuffer[9]),
                startInputKeyX + width + width / 2,
                startInputKeyY +  3 * height + height / 2 - baseLineY,
                keyPaint);

        canvas.drawText(
                "回退",
                startInputKeyX + 3 * width + width / 2,
                startInputKeyY + height - baseLineY,
                keyPaint);

        canvas.drawText(
                "确定",
                startInputKeyX + 3 * width + width / 2,
                startInputKeyY +  3 * height- baseLineY,
                keyPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(MotionEvent.ACTION_DOWN == event.getAction()){
            float x = event.getX();
            float y = event.getY();
            int selectColumn = (int)((x - startInputKeyX) / width);
            int selectRow = (int)((y - startInputKeyY) / height);
            selectCount = 4 * selectRow + selectColumn;
            if(selectCount == 12 || selectCount == 14 || selectCount > 15){
                selectCount = MSG_UNSELECTED;
            }
            if(selectCount == 7){
                selectCount = MSG_BACKSPACE;
            }
            if(selectCount == 15){
                selectCount = MSG_ENTER;
            }
            passwordHandle(3 * selectRow + selectColumn);
            invalidate();
            return true;
        }
        if(MotionEvent.ACTION_UP == event.getAction()){
            selectCount = MSG_UNSELECTED;
            invalidate();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
