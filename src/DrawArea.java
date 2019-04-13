/*
  DrawArea.java
 */

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.awt.Robot;
import java.util.LinkedList;
import javax.swing.*;


public class DrawArea extends JComponent implements MouseListener,MouseMotionListener {

    public Boolean selecting = false;
    public Boolean dragging = false;
    public Boolean isFilled = false;

    private int w,h,MAX_X,MAX_Y;
    private int posX,posY;//Image position
    static private final Color OUTLINE_COLOR = Color.BLACK; //Selection's outline

    // Image to draw on
    public BufferedImage image;
    public BufferedImage selectedImage;

    // Graphics2D
    public Graphics2D g2d;

    // Mouse coordinates
    private int currentX, currentY, oldX, oldY;
    private int toolFlag=100;

    private Color transparentBG;
    private Color pixelColor;
    public Color frontColor, bgColor;
    public int penSize;
    public int eraserSize;

    //private final JLabel crop_lower_right;
    //private final JLabel crop_upper_left;

    private int SelectionWidth = 0;
    private int SelectionHeight = 0;

    private int x = -1;
    private int y = -1;

    private Point startDrag, endDrag;
    public Point2D center;
    Point offset;

    private int zoomLevel = 0;
    public static final int minZoomLevel = -20;
    public static final int maxZoomLevel = 10;
    private static final double zoomMultiplicationFactor = 1.2;
    private AffineTransform coordTransform = new AffineTransform();

    final static float dash1[] = {10.0f};
    final static BasicStroke dashed =
            new BasicStroke(1.0f,
                    BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER,
                    10.0f, dash1, 0.0f);
    //------------------------------------------------------------------------
    // constructor
    public DrawArea(int width, int height, int type) {
/*
        crop_lower_right = new JLabel(new ImageIcon("img/icons/crop_1.png"));
        crop_lower_right.setVisible(false);
        crop_upper_left = new JLabel(new ImageIcon("img/icons/crop_2.png"));
        crop_upper_left.setVisible(false);
*/
        setOpaque(true);
        transparentBG = new Color (1f,0f,0f,0f);
        image = simulatedImage(width,height);
        g2d = image.createGraphics();

        w = width;
        h = height;
        MAX_X = -1;
        MAX_Y = -1;
        posX =0;
        posY=0;

        //setDoubleBuffered(false);
        addMouseListener(this);
        addMouseMotionListener(this);

            System.out.println("Foreground  = " + frontColor);
            System.out.println("Background = " + bgColor);
            System.out.println("Width " + width);
            System.out.println("Height "+height);
            clear();
           setBounds(0,0,width,height);

    }

    public void mousePressed(MouseEvent e) {
         offset = e.getPoint();
        oldX = e.getX();
        oldY = e.getY();
        // For shapes
        startDrag = new Point(e.getX(), e.getY());
        endDrag = startDrag;
        if (toolFlag == 2)//Paint Bucket
        {
            System.out.println(selecting + "   " + "Paint Bucket");
            if (selecting) {//If part of image is being selected
                g2d.setPaint(frontColor);
                g2d.fillRect(x, y, SelectionWidth, SelectionHeight);
                selecting = false;
                repaint();
            } else { // flood fill
                pixelColor = new Color (image.getRGB(oldX, oldY));
                //System.out.println("Old color vs current location");
                //System.out.println(image.getRGB(oldX, oldY) == pixelColor.getRGB());
               image = fill(image, oldX, oldY, frontColor);
               //System.out.println(image);
                //System.out.println(pixelColor);
            }
        }

        else if(toolFlag == 7){
            offset = e.getPoint();
        }
        else if (toolFlag == 8) {
            selecting = false;
            g2d.setXORMode(OUTLINE_COLOR);
            clearSelection(e.getPoint());
            System.out.println("Clear selection");
        }
            repaint();
        }

    public void mouseDragged(MouseEvent e) {
        dragging = true;
        // coord x,y when drag mouse
        currentX = e.getX();
        currentY = e.getY();
        endDrag = new Point(e.getX(), e.getY());

        if (toolFlag == 0 || toolFlag == 1 || toolFlag == 8) {
            drawShape(image.createGraphics());
            repaint();
        }
        switch (toolFlag) {
            case 3:
                System.out.println("crop");

                if (startDrag.getX() > getWidth() - 10 && startDrag.getY() > getHeight() - 10) {
                    System.out.println("In");
                    setSize((int) (getWidth() + (endDrag.x - startDrag.x)),
                            (int) (getHeight() + (endDrag.y - startDrag.y)));
                    startDrag = endDrag;
                }
                break;
            case 7: //move
                int x = e.getPoint().x - offset.x;
                int y = e.getPoint().y - offset.y;
                Component component = e.getComponent();
                Point location = component.getLocation();
                location.x += x;
                location.y += y;
                component.setLocation(location);
                break;

        }
    }


    public void mouseExited(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseClicked(MouseEvent e) {


    }
    public void mouseReleased(MouseEvent e) {
        dragging = false;
        System.out.println("mouseReleased");
        drawShape(image.createGraphics());
        switch(toolFlag){
            case 8://Save selection
                if(SelectionHeight!=0 && SelectionWidth!=0){
                    selecting = true;
                    createSelection();
                }
                break;
        }
        repaint();

    }
    public void mouseMoved(MouseEvent arg0)
    {
    }

    private void setPenSize(){

    }
    private void drawShape(Graphics2D g2d){
        g2d.setColor(frontColor);
        int shapeX = startDrag.x;
         int shapeY = startDrag.y;
        if(endDrag.x>startDrag.x && endDrag.y >startDrag.y);
        else if(endDrag.x > startDrag.x && endDrag.y<startDrag.y)  shapeY = endDrag.y;
        else if(endDrag.x < startDrag.x && endDrag.y>startDrag.y)  shapeX = endDrag.x;
        else   {
            shapeX = endDrag.x;
            shapeY = endDrag.y;
        }
        switch(toolFlag){
            case 0: //Pen
                  System.out.println("Pen is drawing");
                g2d.setPaint(frontColor);
                g2d.setStroke(new BasicStroke(penSize));
                g2d.drawLine(oldX,oldY,currentX,currentY);
                    // draw line if g2d context not null
                    // refresh draw area to repaint
                    // store current coords x,y as olds x,y
                    oldX = currentX;
                    oldY = currentY;

                break;
            case 1: //Pen
            System.out.println("Eraser is drawing");
            g2d.setPaint(bgColor);
            g2d.setStroke(new BasicStroke(eraserSize));
            g2d.drawLine(oldX,oldY,currentX,currentY);
            // draw line if g2d context not null
            // refresh draw area to repaint
            // store current coords x,y as olds x,y
            oldX = currentX;
            oldY = currentY;

            break;
            case 4: //draw oval
                System.out.println("drawOval");
                if(isFilled) g2d.fillOval(shapeX,shapeY, Math.abs(currentX-oldX),Math.abs(currentY-oldY));
                else g2d.drawOval(shapeX,shapeY, Math.abs(currentX-oldX),Math.abs(currentY-oldY));
                break;

            case 5:
                if(isFilled) g2d.fillRect(shapeX,shapeY,Math.abs(endDrag.x-startDrag.x),Math.abs(endDrag.y-startDrag.y));
                else g2d.drawRect(shapeX,shapeY,Math.abs(endDrag.x-startDrag.x),Math.abs(endDrag.y-startDrag.y));
                break;

            case 8://Make selection
                System.out.println("Selecting");
                //System.out.println(e.getPoint());
                updateSelection(endDrag );
                break;

            case 9://Straight line
                g2d.drawLine(startDrag.x,startDrag.y,endDrag.x,endDrag.y);
                break;


        }
    }


    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D)g;
       // super.paintComponent( g );
        g2d.drawImage(image, posX, posY, null);
        if(dragging){
            System.out.println("Drawing");
            drawShape(g2d);
        }
        g2d.dispose();
    }

    // now we create exposed methods
    public void clear() {

        System.out.println("Clear");
        g2d.setPaintMode();
        g2d.setPaint(Color.white);
        // draw white on entire draw area to clear
        g2d.fillRect(0, 0, w, h);
        g2d.setPaint(frontColor);
        repaint();
    }

    public void clear(Color color) {
        g2d.setPaintMode();
        g2d.setPaint(color);
        // draw white on entire draw area to clear
        g2d.fillRect(0, 0, w, h);
        g2d.setPaint(frontColor);
        repaint();
    }


    public void pen() {
        g2d.setPaintMode();
        toolFlag = 0;
    }

    public void eraser() {
        g2d.setPaintMode();
        toolFlag = 1;
    }

    public void paintBucket() {
        g2d.setPaintMode();
        toolFlag = 2;
    }

    public void crop() {
//        crop_upper_left.setVisible(true);
  //      crop_lower_right.setVisible(true);

        toolFlag = 3;
    }

    public void drawOval(){
        g2d.setPaintMode();
        toolFlag = 4;
    }
    public void drawRect(){
        g2d.setPaintMode();
        toolFlag =5;
    }
    public void drawTriangle(){
        g2d.setPaintMode();
        toolFlag = 6;
    }


    public void moveSelected() {

        toolFlag = 7;
    }

    public void select() {

        toolFlag = 8;
    }

    public void drawLine(){
        toolFlag = 9;
    }


    public void setPenStroke(int penSize) {
        this.penSize = penSize;
        System.out.println("Setting stroke");
        repaint();
    }

    public void setEraserSize(int eraserSize) {
        this.eraserSize = eraserSize;
        System.out.println("Setting eraser");
        repaint();
    }
    private void setColor(int locx, int locy, Color color) {
        int c = color.getRGB();
        image.setRGB(locx, locy, c);
    }


    //-------------------- Select --------------------------------------
    // accessors - get points defining the area selected
    Point2D.Double getUpperLeft()
    {
        return getUpperLeft( new Point2D.Double() );
    }

    Point2D.Double getUpperLeft( Point2D.Double p )
    {
        if ( SelectionWidth < 0 )
            if ( SelectionHeight < 0 )
                p.setLocation( (x+SelectionWidth)/((double) MAX_X), (y+SelectionHeight)/((double) MAX_Y) );
            else
                p.setLocation( (x+SelectionWidth)/((double) MAX_X), y/((double) MAX_Y) );
        else if ( SelectionHeight < 0 )
            p.setLocation( x/((double) MAX_X), (y+SelectionHeight)/((double) MAX_Y) );
        else
            p.setLocation( x/((double) MAX_X), y/((double) MAX_Y) );
        return p;
    }

    Point2D.Double getLowerRight()
    {
        return getLowerRight( new Point2D.Double() );
    }
    Point2D.Double getLowerRight( Point2D.Double p )
    {
        if ( SelectionWidth < 0 )
            if ( SelectionHeight< 0 )
                p.setLocation( x/((double) MAX_X), y/((double) MAX_Y) );
            else
                p.setLocation( x/((double) MAX_X), (y+SelectionHeight)/((double) MAX_Y) );
        else if ( SelectionHeight < 0 )
            p.setLocation( (x+SelectionWidth)/((double) MAX_X), y/((double) MAX_Y) );
        else
            p.setLocation( (x+SelectionWidth)/((double) MAX_X), (y+SelectionHeight)/((double) MAX_Y) );
        return p;
    }
    private void clearSelection( Point p )
    {
        // erase old selection
        drawSelection();
        // begin new selection
        x = (p.x < 0) ? 0 : ( (p.x < w) ? p.x : MAX_X );
        y = (p.y < 0) ? 0 : ( (p.y < h) ? p.y : MAX_Y );
        SelectionWidth = 0;
        SelectionHeight = 0;
        //System.out.println("p,x= " + p.x + "p,y= " + p.y);
        drawSelection();
    }
    private void updateSelection( Point p )
    {
        // erase old selection
        drawSelection();

        // modify current selection
        int px = (p.x < 0) ? 0 : ( (p.x < w) ? p.x : MAX_X );
        int py = (p.y < 0) ? 0 : ( (p.y < h) ? p.y : MAX_Y );
        SelectionWidth = px - x;
        SelectionHeight = py - y;
        //System.out.println("x= " + x + "y= " +y);
       // System.out.println("w= " + SelectionWidth + "h= " +SelectionHeight);
        drawSelection();
    }
    private void drawSelection()
    {
        g2d.setStroke(dashed);

        //System.out.println("Drawing Selection");
        if ( SelectionWidth < 0 )
            if ( SelectionHeight < 0 ) {
               // System.out.println("w= " + SelectionWidth + "h= " +SelectionHeight);
                g2d.drawRect((x + SelectionWidth), (y + SelectionHeight), -SelectionWidth, -SelectionHeight);
            }
            else {
              //  System.out.println("w= " + SelectionWidth+ "h= " + SelectionHeight);
                g2d.drawRect((x + SelectionWidth), y, -SelectionWidth, SelectionHeight);
            }
        else if ( SelectionHeight < 0 ) {
          //  System.out.println("w= " + SelectionWidth + "h= " + SelectionHeight);
            g2d.drawRect(x, (y + SelectionHeight), SelectionWidth, -SelectionHeight);
        }
        else{
         //   System.out.println("w= " + SelectionWidth + "h= " +SelectionHeight);
            g2d.drawRect( x, y, SelectionWidth, SelectionHeight );
        }

        repaint();
    }

    private void createSelection(){
         System.out.println("Save Selection");
        double UL_x = getUpperLeft().getX();
        double UL_y = getUpperLeft().getY();
        double LR_x = getLowerRight().getX();
        double LR_y = getLowerRight().getY();
        if(selecting){
            selectedImage = image.getSubimage(Math.abs((int)UL_x),Math.abs((int)UL_y),Math.abs(SelectionWidth),Math.abs(SelectionHeight));
        }
    }

    public void setFrontColor(Color frontColor){
        this.frontColor = frontColor;
    }

    public void setBgColor(Color bgColor){
        this.bgColor = bgColor;
    }
    public void setWidth(int w){
        this.w = w;
    }
    public void setHeight(int h){
        this.h = h;
    }



    public BufferedImage fill(BufferedImage img, int initialX, int initialY, Color col)
    {
        BufferedImage filledIMG = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        filledIMG.getGraphics().drawImage(img, 0, 0, null);
        int x = initialX;
        int y = initialY;
        int width = filledIMG.getWidth();
        int height = filledIMG.getHeight();

        DataBufferInt data = (DataBufferInt) (filledIMG.getRaster().getDataBuffer());
        int[] pixels = data.getData();

        if (x >= 0 && x < width && y >= 0 && y < height)
        {

            int oldColor = pixels[y * width + x];
            int fillColor = col.getRGB();

            //Perform flood fill if current pixel is not new color
            if (oldColor != fillColor)
            {
                floodFill(pixels, x, y, width, height, oldColor, fillColor);
            }
        }
        return filledIMG;
    }


    //Use BFS to color all adjacent pixels with old color to new color
    private void floodFill(int[] pixels, int x, int y, int width, int height, int oldColor, int newColor) {

        int[] point = new int[]{x, y};
        LinkedList<int[]> points = new LinkedList<int[]>();
        points.addFirst(point);

        while (!points.isEmpty()) {
            //Remove a parent node first, look through all of its adjacent nodes
            point = points.remove();

            x = point[0];
            y = point[1];
            int xr = x;

            int yp = y * width;
            int ypp = yp + width;
            int ypm = yp - width;

            do {
                pixels[xr + yp] = newColor;
                xr++;
            }

            while (xr < width && pixels[xr + yp] == oldColor);

            int xl = x;
            do {
                pixels[xl + yp] = newColor;
                xl--;
            }
            while (xl >= 0 && pixels[xl + y * width] == oldColor);

            xr--;
            xl++;

            boolean up = false;
            boolean down = false;

            //Add adjacent points that have the old color to the list
            for (int xi = xl; xi <= xr; xi++) {
                
                if (y > 0 && pixels[xi + ypm] == oldColor && !up) {
                    points.addFirst(new int[]{xi, y - 1});
                    up = true;
                } else {
                    up = false;
                }
                if (y < height - 1 && pixels[xi + ypp] == oldColor && !down) {
                    points.addFirst(new int[]{xi, y + 1});
                    down = true;
                } else {
                    down = false;
                }
            }
        }
    }


    protected BufferedImage simulatedImage(int width_,int height_){
        while (true) {
            if (width_ < 0 || height_ < 0)
                return null;
            try {
                BufferedImage img = new BufferedImage(width_,height_,BufferedImage.TYPE_INT_RGB);
                return img;
            } catch (OutOfMemoryError err) {
                JOptionPane.showMessageDialog(this, "Ran out of memory! Try using a smaller image size.");
            }
        }
    }

}