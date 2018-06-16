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
    private final static int MSG_CANCEL = 7;
    private final static int MSG_ENTER = 11;
    private final static int MSG_BACKSPACE = 3;
    private Paint tablePaint = new Paint();
    private Paint keyPaint = new Paint();
    private int[] numBuffer = new int[10];
    private int baseLineY = 0;
    private int startX = 16;
    private int startY = 16;
    private int height;
    private int width;
    private int startInputKeyX = startX;
    private int startInputKeyY = startY + height;
    private int selectCount = MSG_UNSELECTED;
    private StringBuilder password = new StringBuilder();
    private StringBuilder passwordValue = new StringBuilder();
//    private OnItemSelectListener onItemSelectListener = null;
    private OnFinishListener onFinishListener = null;

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
            case MSG_CANCEL:
                password.delete(0, password.length());
                passwordValue.delete(0, passwordValue.length());
                if(onFinishListener != null){
                    onFinishListener.onFinish(-1, "USER CANCEL!");
                }
                break;
            case MSG_ENTER:
                if(password.length() < 6){
                    return;
                }
                if(onFinishListener != null){
                    onFinishListener.onFinish(0, passwordValue.toString());
                }
                password.delete(0, password.length());
                passwordValue.delete(0, passwordValue.length());
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

    public interface OnFinishListener{
        void onFinish(int returnCode, String password);
    }

    public void setOnFinishListener(OnFinishListener listener){
        this.onFinishListener = listener;
    }
    
    private void drawBaseTable(Canvas canvas) {
        canvas.drawRect(
        startX, startY,
        startX + 4 * width, startY + 5 * height, tablePaint);
        float[] points = {
                // 画行
                startX, startY + height, startX + 4 * width, startY + height,
                startX, startY + 2 * height, startX + 4 * width, startY + 2 * height,
                startX, startY + 3 * height, startX + 4 * width, startY + 3 * height,
                startX, startY + 4 * height, startX + 3 * width, startY + 4 * height,
                // 画列
                startX + width, startY + height, startX + width, startY + 5 * height,
                startX + 2 * width, startY + height, startX + 2 * width, startY + 5 * height,
                startX + 3 * width, startY + height, startX + 3 * width, startY + 5 * height,
        };

        canvas.drawLines(points, tablePaint);
    }

    private void drawSelectTable(Canvas canvas, int selectCount) {
        int selectColumn = selectCount / 4;
        int selectRow = selectCount % 4;

        switch (selectCount) {
            case MSG_UNSELECTED:
                break;
            case MSG_ENTER:
                tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawRect(
                        startInputKeyX + selectRow * width,
                        startInputKeyY + selectColumn * height,
                        startInputKeyX + (selectRow + 1)  * width,
                        startInputKeyY + (selectColumn + 2)  * height,
                        tablePaint);
                tablePaint.setStyle(Paint.Style.STROKE);
                break;
            default:
                tablePaint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawRect(
                        startInputKeyX + selectRow * width,
                        startInputKeyY + selectColumn * height,
                        startInputKeyX + (selectRow + 1)  * width,
                        startInputKeyY + (selectColumn + 1)  * height,
                        tablePaint);
                tablePaint.setStyle(Paint.Style.STROKE);
                break;
        }
    }

    private void drawKeyValue(Canvas canvas) {
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
                startInputKeyY + height / 2 - baseLineY,
                keyPaint);

        canvas.drawText(
                "取消",
                startInputKeyX + 3 * width + width / 2,
                startInputKeyY + height + height / 2 - baseLineY,
                keyPaint);

        canvas.drawText(
                "确定",
                startInputKeyX + 3 * width + width / 2,
                startInputKeyY +  3 * height- baseLineY,
                keyPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int totalWidth = getMeasuredWidth();
        int totalHeight = getMeasuredHeight();
        width = (totalWidth - 2 * startX) / 4;
        height = (totalHeight - 2 * startY) / 5;
        startInputKeyX = startX;
        startInputKeyY = startY + height;

        drawBaseTable(canvas);
        drawSelectTable(canvas, selectCount);
        drawKeyValue(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                float x = event.getX();
                float y = event.getY();
                if(x < startInputKeyX || y < startInputKeyY){
                    return true;
                }
                int selectColumn = (int)((x - startInputKeyX) / width);
                int selectRow = (int)((y - startInputKeyY) / height);
                selectCount = 4 * selectRow + selectColumn;
                if(selectCount == 12 || selectCount == 14 || selectCount > 15){
                    selectCount = MSG_UNSELECTED;
                }
                if(selectCount == 15){
                    selectCount = MSG_ENTER;
                }
                passwordHandle(3 * selectRow + selectColumn);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                selectCount = MSG_UNSELECTED;
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }
}
