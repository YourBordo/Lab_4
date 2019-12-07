package bsu.rfe.java.lab_4;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.*;


public class GraphicsDisplay extends JPanel {

    private Double[][] graphicsData;
    private boolean showAxis = true;
    private boolean showMarkers = true;
    private boolean showDelta = true;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;

    private double scale;

    private BasicStroke graphicsStroke;
    private BasicStroke axisStroke;
    private BasicStroke markerStroke;
    private BasicStroke deltaStroke;

    private Font axisFont;


    public GraphicsDisplay() {
        setBackground(Color.WHITE);
        graphicsStroke = new BasicStroke(6.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, new float[]{40, 10, 10, 10, 20, 10, 10, 10, 40, 20}, 0.0f);
        axisStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        markerStroke = new BasicStroke(0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);
        deltaStroke = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, null, 0.0f);

        axisFont = new Font("Serif", Font.BOLD, 36);
    }

    public void showGraphics(Double[][] graphicsData) {
        this.graphicsData = graphicsData;
        repaint();
    }

    public void setShowAxis(boolean showAxis) {
        this.showAxis = showAxis;
        repaint();
    }

    public void setShowDelta(boolean showDelta){
        this.showDelta = showDelta;
        repaint();
    }

    public void setShowMarkers(boolean showMarkers) {
        this.showMarkers = showMarkers;
        repaint();
    }


    public void paintComponent(Graphics g){
        super.paintComponent(g);
        if (graphicsData==null || graphicsData.length==0) return;

        minX = graphicsData[0][0];
        maxX = graphicsData[graphicsData.length-1][0];
        minY = graphicsData[0][1];
        maxY = minY;

        for (int i = 1; i<graphicsData.length; i++) {
            if (graphicsData[i][1]<minY) {
                minY = graphicsData[i][1];
            }
            if (graphicsData[i][1]>maxY) {
                maxY = graphicsData[i][1];
            }
        }

        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);

        scale = Math.min(scaleX, scaleY);

        if (scale==scaleX) {
            double yIncrement = (getSize().getHeight()/scale - (maxY - minY))/2;
            maxY += yIncrement;
            minY -= yIncrement;
        }
        if (scale==scaleY) {
            double xIncrement = (getSize().getWidth()/scale - (maxX - minX))/2;
            maxX += xIncrement;
            minX -= xIncrement;
        }

        Graphics2D canvas = (Graphics2D) g;
        Stroke oldStroke = canvas.getStroke();
        Color oldColor = canvas.getColor();
        Paint oldPaint = canvas.getPaint();
        Font oldFont = canvas.getFont();
        if (showAxis) paintAxis(canvas);
        paintGraphics(canvas);
        if (showMarkers) paintMarkers(canvas);
        if (showDelta) paintDelta(canvas);
        canvas.setFont(oldFont);
        canvas.setPaint(oldPaint);
        canvas.setColor(oldColor);
        canvas.setStroke(oldStroke);
    }

    protected void paintGraphics(Graphics2D canvas){
        canvas.setStroke(graphicsStroke);
        canvas.setColor(Color.PINK);
        GeneralPath graphics = new GeneralPath();
        for (int i=0; i<graphicsData.length; i++) {
            Point2D.Double point = xyToPoint(graphicsData[i][0],
                    graphicsData[i][1]);
            if (i>0) {
                graphics.lineTo(point.getX(), point.getY());
            }
            else {
                graphics.moveTo(point.getX(), point.getY());
            }
        }
        canvas.draw(graphics);
    }

    protected void paintMarkers(Graphics2D canvas) {

        for (Double[] point : graphicsData){
            boolean check = false;
            Double value = point[1];
            value = Math.abs(value - Math.floor(value));
            if (value <= 0.1 || value >= 0.9){
                check = true;
            }

            if (check){
                canvas.setColor(Color.GREEN);
                canvas.setPaint(Color.GREEN);
            }
            else{
                canvas.setColor(Color.RED);
                canvas.setPaint(Color.RED);
            }

            canvas.setStroke(markerStroke);
            Point2D.Double center = xyToPoint(point[0], point[1]);
            canvas.draw(new Line2D.Double(shiftPoint(center, -11, 11), shiftPoint(center, 11, 11)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -11, -11), shiftPoint(center, 11, -11)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 11, 11), shiftPoint(center, 11, -11)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -11, -11), shiftPoint(center, -11, 11)));
            canvas.draw(new Line2D.Double(shiftPoint(center, 11, 11), shiftPoint(center, -11, -11)));
            canvas.draw(new Line2D.Double(shiftPoint(center, -11, 11), shiftPoint(center, 11, -11)));

        }
    }

    protected void paintDelta(Graphics2D canvas){
        double scaleX = getSize().getWidth() / (maxX - minX);
        double scaleY = getSize().getHeight() / (maxY - minY);
        Double step_x = (maxX - minX)/100;
        Double step_y = (maxY - minY)/100;
        int i = 0;
        canvas.setStroke(deltaStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);


        for (Double temp = minX; temp < maxX; temp += step_x){
            if (i % 5 == 0){
                canvas.draw(new Line2D.Double(xyToPoint(temp, step_y), xyToPoint(temp, -step_y)));
            }
            else{
                canvas.draw(new Line2D.Double(xyToPoint(temp, step_y/2), xyToPoint(temp, -step_y/2)));
            }
            i++;

        }
        i = 0;
        for (Double temp = minY; temp < maxY; temp += step_y){
            if (i % 5 == 0){
                canvas.draw(new Line2D.Double(xyToPoint(step_x, temp), xyToPoint(-step_x, temp)));
            }
            else{
                canvas.draw(new Line2D.Double(xyToPoint(step_x/2, temp), xyToPoint(-step_x/2, temp)));
            }
            i++;

        }
        canvas.setColor(Color.RED);
        canvas.setPaint(Color.RED);
     //   canvas.draw(new Line2D.Double(xyToPoint(maxX-minX , step_y*2), xyToPoint(temp, -step_y*2)));
     //   canvas.draw(new Line2D.Double(xyToPoint(step_x*2, temp), xyToPoint(-step_x*2, temp)));


    }

    protected void paintAxis(Graphics2D canvas) {
        canvas.setStroke(axisStroke);
        canvas.setColor(Color.BLACK);
        canvas.setPaint(Color.BLACK);
        canvas.setFont(axisFont);
        FontRenderContext context = canvas.getFontRenderContext();

        if (minX <= 0.0 && maxX >= 0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(0, maxY), xyToPoint(0, minY)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(0, maxY);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX()+5, arrow.getCurrentPoint().getY()+20);
            arrow.lineTo(arrow.getCurrentPoint().getX()-10, arrow.getCurrentPoint().getY());

            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);

            Rectangle2D bounds = axisFont.getStringBounds("y", context);
            Point2D.Double labelPos = xyToPoint(0, maxY);
            canvas.drawString("y", (float)labelPos.getX() + 10, (float)(labelPos.getY() - bounds.getY()));



        }

        if (minY<=0.0 && maxY>=0.0) {
            canvas.draw(new Line2D.Double(xyToPoint(minX, 0), xyToPoint(maxX, 0)));
            GeneralPath arrow = new GeneralPath();
            Point2D.Double lineEnd = xyToPoint(maxX, 0);
            arrow.moveTo(lineEnd.getX(), lineEnd.getY());
            arrow.lineTo(arrow.getCurrentPoint().getX() - 20, arrow.getCurrentPoint().getY() - 5);
            arrow.lineTo(arrow.getCurrentPoint().getX(), arrow.getCurrentPoint().getY() + 10);
            arrow.closePath();
            canvas.draw(arrow);
            canvas.fill(arrow);
            Rectangle2D bounds = axisFont.getStringBounds("x", context);
            Point2D.Double labelPos = xyToPoint(maxX, 0);
            canvas.drawString("x", (float) (labelPos.getX() - bounds.getWidth() - 10), (float) (labelPos.getY() + bounds.getY()));

        }

    }


    protected Point2D.Double xyToPoint(double x, double y) {
        double deltaX = x - minX;
        double deltaY = maxY - y;
        return new Point2D.Double(deltaX*scale, deltaY*scale);
    }

    protected Point2D.Double shiftPoint(Point2D.Double src, double deltaX, double deltaY) {
        Point2D.Double dest = new Point2D.Double();
        dest.setLocation(src.getX() + deltaX, src.getY() + deltaY);
        return dest;
    }
}


