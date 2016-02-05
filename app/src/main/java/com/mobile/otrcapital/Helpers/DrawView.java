package com.mobile.otrcapital.Helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mobile.otrcapital.R;

import java.util.ArrayList;

public class DrawView extends View {

    // variable to know what ball is being dragged
    Paint paint;
    Canvas canvas;
    Context context;
    private ArrayList<ColorBall> colorballs = new ArrayList<ColorBall>();
    // array that holds the balls
    private int balID = 0;
    public DrawView(Context context) {
        super(context);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
        this.context = context;
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    public Point[] getPoints() {
        Point[] points = new Point[4];
        for (int i = 0; i < points.length; i++) {
            points[i] = colorballs.get(i).getPointForWarp();
        }
        return points;
    }

    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas)
    {
        if (colorballs.size()==0)
        {
            int height = getMeasuredHeight();
            int width = getMeasuredWidth();

            //top left corner
            colorballs.add(new ColorBall(getContext(), R.drawable.edge_handle_top_left,
                    new Point(width /4, height /4),ColorBall.OFFSET_TOP_LEFT));

            //top right corner
            colorballs.add(new ColorBall(getContext(),R.drawable.edge_handle_top_right,
                    new Point(width- width/4, height /4),ColorBall.OFFSET_TOP_RIGHT));

            //bottom right corner
            colorballs.add(new ColorBall(getContext(),R.drawable.edge_handle_bottom_right,
                    new Point (width- width/4,height- height/4),ColorBall.OFFSET_BOTTOM_RIGHT));

            //bottom left corner
            colorballs.add(new ColorBall(getContext(),R.drawable.edge_handle_bottom_left,
                    new Point(width /4,height- height/4),ColorBall.OFFSET_BOTTOM_LEFT));

        }

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);

        //draw stroke
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.blue_alpha));
        paint.setStrokeWidth(0);

        Path path = new Path();
        path.moveTo(colorballs.get(0).getX()+colorballs.get(0).getPathOffsetX(),
                colorballs.get(0).getY()+ colorballs.get(0).getPathOffsetY());
        for (int i = 0;i<colorballs.size();i++)
        {
            int j =i;
            if (i>=colorballs.size())
                j=0;

            path.lineTo(colorballs.get(j).getX()+colorballs.get(j).getPathOffsetX(),
                    colorballs.get(j).getY()+colorballs.get(j).getPathOffsetY());
        }
        canvas.drawPath(path, paint);

        BitmapDrawable bitmap = new BitmapDrawable();
        paint.setColor(Color.BLUE);
        // draw the balls on the canvas
        for (int i =0; i < colorballs.size(); i ++)
        {
            ColorBall ball = colorballs.get(i);
            canvas.drawBitmap(ball.getBitmap(), ball.getX()+colorballs.get(i).getHandleOffsetX(),
                    ball.getY()+colorballs.get(i).getHandleOffsetY(), paint);
        }
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventaction) {

            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                // a ball
            {
                //resize rectangle
                balID = -1;
                for (int i = colorballs.size()-1; i>=0; i--)
                {
                    ColorBall ball = colorballs.get(i);
                    // check if inside the bounds of the ball (circle)
                    // get the center for the ball
                    int centerX = ball.getX();
                    int centerY = ball.getY();
                    paint.setColor(Color.CYAN);
                    // calculate the radius from the touch to the center of the
                    // ball
                    double radCircle = Math
                            .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                    * (centerY - Y)));

                    if (radCircle < 1.5*(double)ball.getWidthOfBall())
                    {

                        balID = ball.getID();
                        if (balID >=4)
                        {
                            balID = balID%4;
                        }
                        invalidate();
                        break;
                    }
                    invalidate();
                }
            }
            break;

            case MotionEvent.ACTION_MOVE: // touch drag with the ball

                if (balID > -1)
                {

                    try
                    {
                        // move the balls the same as the finger
                        colorballs.get(balID).setX(X);
                        colorballs.get(balID).setY(Y);
                        invalidate();

                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        Log.d(ActivityTags.TAG_LOG,"DrawView index out of bounds, Ballid = " + balID);
                    }

                }

                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                // touch drop - just do things here after dropping

                break;
        }
        // redraw the canvas
        invalidate();
        return true;

    }


    public static class ColorBall
    {

        public static final int OFFSET_TOP_LEFT=0,OFFSET_TOP_RIGHT=1,
                OFFSET_BOTTOM_RIGHT=2,OFFSET_BOTTOM_LEFT=3;
        static int count = 0;
        Bitmap bitmap;
        Context mContext;
        Point point;
        Point handleOffset;
        Point pathOffset;
        int id;

        public ColorBall(Context context, int resourceId, Point point, int offsetType)
        {
            if (count >=4)
            {
                count = count % 4;
            }
            this.id = count++;
            bitmap = BitmapFactory.decodeResource(context.getResources(),resourceId);
            mContext = context;
            this.point = point;
            setOffset(offsetType);
        }

        private void setOffset(int offsetType)
        {
            switch (offsetType)
            {
                case OFFSET_TOP_LEFT:
                    this.handleOffset = new Point(-bitmap.getWidth(),-bitmap.getHeight());
                    this.pathOffset = new Point (0,0);
                    break;
                case OFFSET_TOP_RIGHT:
                    this.handleOffset = new Point(0,-bitmap.getHeight());
                    this.pathOffset = new Point (bitmap.getWidth(),0);
                    break;
                case OFFSET_BOTTOM_LEFT:
                    this.handleOffset = new Point(-bitmap.getWidth(),0);
                    this.pathOffset = new Point (0,bitmap.getHeight());
                    break;
                case OFFSET_BOTTOM_RIGHT:
                    this.handleOffset = new Point(0, 0);
                    this.pathOffset = new Point (bitmap.getWidth(),bitmap.getHeight());
                    break;

            }
        }

        public int getWidthOfBall() {
            return bitmap.getWidth();
        }
        public int getHeightOfBall() {
            return bitmap.getHeight();
        }
        public Bitmap getBitmap() {
            return bitmap;
        }
        public int getHandleOffsetX()
        {
            return handleOffset.x;
        }
        public int getHandleOffsetY()
        {
            return handleOffset.y;
        }
        public int getPathOffsetX()
        {
            return handleOffset.x+pathOffset.x;
        }
        public int getPathOffsetY()
        {
            return handleOffset.y+pathOffset.y;
        }
        public int getX() {
            return point.x;
        }

        public void setX(int x) {
            point.x = x;
        }

        public int getY() {
            return point.y;
        }

        public void setY(int y) {
            point.y = y;
        }

        public int getID() {
            return id;
        }

        public Point getPointForWarp()
        {
            return new Point(point.x+getPathOffsetX(),point.y+getPathOffsetY());
        }

    }
}