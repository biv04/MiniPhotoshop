import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JLayeredPane;


class mainPanel extends JFrame {

    private final JFileChooser chooser;
    ArrayList<DrawArea> canvas = new ArrayList<>();
    private int layer_index;

   // private DrawArea canvas;
    private DrawArea background;
    private DrawArea midground;
    private DrawArea foreground;

    //private final paintBoard paintBoard;
    private final JPanel toolPanel;       // penSizetainer panel for the left
    private final JPanel rightPanel;    // penSizetainer panel for the right
    private final JPanel centerPanel;
    //  === Inside of right panel ===

    private JPanel propertyPanel;
    private JPanel penPane;
    private JPanel shapePane;
    private final JPanel layerPanel;
    private final JPanel navigationPanel;

    // === buttons for left panel
    private final JButton buttonPen;
    private final JButton buttonEraser;
    private final JButton buttonPaintBucket;
    private final JButton buttonCrop;
    private final JButton buttonSelect;
    private final JButton buttonShapes;
    private final JButton buttonMove;
    private final JButton buttonftColor;
    private final JButton buttonbcColor;
    private final JButton clear;

    private final JButton frontVisible;
    private final JButton midVisible;
    private final JButton backVisible;

    private final JButton rectangle;
    private final JButton oval;
    private final JButton straightLine;

    private final JButton buttonBackground;
    private final JButton buttonMidground;
    private final JButton buttonForeground;

    private final JRadioButton fill;

    //Slider
    private JSlider PsizeSlider;
    private JSlider PpowerSlider;
    private JSlider EsizeSlider;
    private JSlider EpowerSlider;

    //Label
    private JLabel Psize, Ppower;
    private JLabel Esize, Epower;

    private BufferedImage combined;
    private Graphics g;

    private Color frontColor;
    private Color bgColor;

    private int canvasHeight;
    private int canvasWidth;

    private int penSize, eraserSize;
    public int penPower, eraserPower;

    CardLayout cl;
    private JLayeredPane layers;

    public mainPanel(int width, int height) {

        this.setTitle("Mini Photoshop");
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addMenu();


        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("."));

        //Initialize size and color
        canvasHeight = height;
        canvasWidth = width;
        frontColor = Color.yellow;
        bgColor = Color.white;
        penSize = 10;
        eraserSize = 10;


        System.out.println(width + "   " + height);
        background = new DrawArea(canvasWidth,canvasHeight,0);
        background.setFrontColor(frontColor);

        midground = new DrawArea(canvasWidth,canvasHeight,0);
        midground.setFrontColor(frontColor);

        foreground = new DrawArea(canvasWidth,canvasHeight,0); // our mid component
        foreground.frontColor = frontColor;

        canvas.add(0,background);
        canvas.add(1,midground);
        canvas.add(2,foreground);

        layer_index = 2;


        toolPanel = new JPanel();         // our left component
        rightPanel = new JPanel();      // our right component
        centerPanel = new JPanel();

        penPane = new JPanel();
        shapePane = new JPanel();
        propertyPanel = new JPanel();
        navigationPanel = new JPanel();
        layerPanel = new JPanel();

        event e = new event();
        PsizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, penSize);
        PpowerSlider = new JSlider(JSlider.HORIZONTAL, 1, 255, 255);
        EsizeSlider = new JSlider(JSlider.HORIZONTAL, 1, 50, eraserSize);
        EpowerSlider = new JSlider(JSlider.HORIZONTAL, 1, 255, 255);

        Psize = new JLabel("Pen Size: " + penSize);
        Ppower = new JLabel("Pen Power: 255");
        Esize = new JLabel("Eraser Size: " + eraserSize);
        Epower = new JLabel("Eraser Power: 255");

        buttonPen = new JButton();    // and a button at the right, to send the text
        buttonPen.setIcon(new ImageIcon("img/icons/pen.png"));
        buttonPen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPen.addActionListener(actionListener);

        buttonEraser = new JButton();    // and a button at the right, to send the text
        buttonEraser.setIcon(new ImageIcon("img/icons/eraser.png"));
        buttonEraser.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonEraser.addActionListener(actionListener);

        buttonPaintBucket = new JButton();    // and a button at the right, to send the text
        buttonPaintBucket.setIcon(new ImageIcon("img/icons/paint.png"));
        buttonPaintBucket.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonPaintBucket.addActionListener(actionListener);

        buttonCrop = new JButton();    // and a button at the right, to send the text
        buttonCrop.setIcon(new ImageIcon("img/icons/gradient.png"));
        buttonCrop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonCrop.addActionListener(actionListener);

        buttonSelect = new JButton();    // and a button at the right, to send the text
        buttonSelect.setIcon(new ImageIcon("img/icons/select.png"));
        buttonSelect.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonSelect.addActionListener(actionListener);

        buttonShapes = new JButton();    // and a button at the right, to send the text
        buttonShapes.setIcon(new ImageIcon("img/icons/shapes.png"));
        buttonShapes.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonShapes.addActionListener(actionListener);

        buttonMove = new JButton();    // and a button at the right, to send the text
        buttonMove.setIcon(new ImageIcon("img/icons/move.png"));
        buttonMove.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonMove.addActionListener(actionListener);

        buttonftColor = new JButton();    // and a button at the right, to send the text
        buttonftColor.setBackground(frontColor);
        buttonftColor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        //System.out.println(frontColor);
        buttonftColor.setIcon(new ImageIcon("img/icons/front.png"));
        buttonftColor.addActionListener(actionListener);

        buttonbcColor = new JButton();    // and a button at the right, to send the text
        buttonbcColor.setBackground(bgColor);
        buttonbcColor.setCursor(new Cursor(Cursor.HAND_CURSOR));
        buttonbcColor.setIcon(new ImageIcon("img/icons/back.png"));
        buttonbcColor.addActionListener(actionListener);

        clear = new JButton("Clear");
        clear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clear.addActionListener(actionListener);

        //================= Right PANEL ====================================================
        BoxLayout boxlayout1 = new BoxLayout(rightPanel, BoxLayout.Y_AXIS);
        rightPanel.setLayout(boxlayout1);

        propertyPanel.setBorder(BorderFactory.createTitledBorder("Property"));
      //  navigationPanel.setBorder(BorderFactory.createTitledBorder("Navigation"));
        layerPanel.setBorder(BorderFactory.createTitledBorder("Layers"));

        rightPanel.add(propertyPanel);                // first we add the scrollPane to the rightPanel, so it is at the top
     //   rightPanel.add(navigationPanel);                // then we add the inputPanel to the rightPanel, so it under the scrollPane / textArea
        rightPanel.add(layerPanel);                // then we add the inputPanel to the rightPanel, so it under the scrollPane / textArea


        rightPanel.setPreferredSize(new Dimension(width / 5, height));
        // ===================== Property  Panes ============================
        BoxLayout boxlayout2 = new BoxLayout(penPane, BoxLayout.Y_AXIS);
        penPane.setLayout(boxlayout2);
        //=================
        PsizeSlider.setMajorTickSpacing(5);
        PsizeSlider.setPaintTicks(true);
        penPane.add(Psize);
        penPane.add(PsizeSlider);
        PsizeSlider.addChangeListener(e);
        //=================
        EsizeSlider.setMajorTickSpacing(5);
        EsizeSlider.setPaintTicks(true);
        penPane.add(Esize);
        penPane.add(EsizeSlider);
        EsizeSlider.addChangeListener(e);
        //=================
        PpowerSlider.setMajorTickSpacing(5);
        PpowerSlider.setPaintTicks(true);
        penPane.add(Ppower);
        penPane.add(PpowerSlider);
        PpowerSlider.addChangeListener(e);
        //====================
        EpowerSlider.setMajorTickSpacing(5);
        EpowerSlider.setPaintTicks(true);
        penPane.add(Epower);
        penPane.add(EpowerSlider);
        EpowerSlider.addChangeListener(e);
        //====================
        BoxLayout boxLayout5 = new BoxLayout(shapePane,BoxLayout.Y_AXIS);
        shapePane.setLayout(boxLayout5);

        rectangle = new JButton("Rectangle");
        rectangle.addActionListener(actionListener);
        oval = new JButton("Oval");
        oval.addActionListener(actionListener);

        straightLine = new JButton("Straight Line");
        straightLine.addActionListener(actionListener);

        fill = new JRadioButton("Fill");
        fill.addActionListener(actionListener);


        shapePane.add(straightLine);
        shapePane.add(rectangle);
        shapePane.add(oval);
        shapePane.add(fill);


        //================= Navigation Panel =======================


        //================= Layer Panel =============================

        GridLayout gridLayout = new GridLayout(0,1);
        gridLayout.setVgap(8);

        buttonForeground = new JButton("*** Foreground");
        buttonForeground.addActionListener(actionListener);
        frontVisible  = new JButton();
        frontVisible.setIcon(new ImageIcon("img/icons/eye.png"));
        frontVisible.addActionListener(actionListener);
      //  frontHide = new JButton();
     //   frontHide.setIcon(new ImageIcon("img/icons/hide.png"));

        buttonMidground = new JButton("Midground");
        buttonMidground.addActionListener(actionListener);
        midVisible  = new JButton();
        midVisible.setIcon(new ImageIcon("img/icons/eye.png"));
        midVisible.addActionListener(actionListener);
     //   midHide = new JButton();
     //   midHide.setIcon(new ImageIcon("img/icons/hide.png"));

        buttonBackground = new JButton("Background");
        buttonBackground.addActionListener(actionListener);
        backVisible  = new JButton();
        backVisible.setIcon(new ImageIcon("img/icons/eye.png"));
        backVisible.addActionListener(actionListener);
   //     backHide = new JButton();
  //      backHide.setIcon(new ImageIcon("img/icons/hide.png"));

        JPanel visiblePane = new JPanel();
        visiblePane.setLayout(gridLayout);
        visiblePane.add(frontVisible);
        visiblePane.add(midVisible);
        visiblePane.add(backVisible);

        JPanel LP = new JPanel();
        LP.setLayout(gridLayout);
        LP.add(buttonForeground);
        LP.add(buttonMidground);
        LP.add(buttonBackground);

        //layerPanel.setLayout(flowLayout1);
        layerPanel.add(visiblePane);
        layerPanel.add(LP);

        //================= TOOL PANEL ====================================================
        BoxLayout boxlayout3 = new BoxLayout(toolPanel, BoxLayout.Y_AXIS);
        toolPanel.setLayout(boxlayout3);
        toolPanel.add(buttonPen);           // and right the "send" button
        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        toolPanel.add(buttonEraser);
        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        toolPanel.add(buttonPaintBucket);
        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        toolPanel.add(buttonCrop);
        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        toolPanel.add(buttonSelect);
        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        toolPanel.add(buttonMove);
        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        toolPanel.add(buttonShapes);
        toolPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        toolPanel.add(buttonftColor);
        toolPanel.add(buttonbcColor);
        toolPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        toolPanel.add(clear);
        toolPanel.setBorder(BorderFactory.createTitledBorder("Tools"));

        //================= Canvas PANEL ====================================================

         layers= new JLayeredPane();
         /*
          cl = new CardLayout();
        centerPanel.setLayout(cl);

        centerPanel.add(background,"1");
        centerPanel.add(midground,"2");
        centerPanel.add(foreground,"3");

        cl.show(centerPanel,"1");
        */
        layers.add(background,new Integer(1));
        layers.add(midground,new Integer(2));
        layers.add(foreground, new Integer(3));

        this.getContentPane().add(layers,BorderLayout.CENTER);
        //this.getContentPane().add(canvas.get(layer_index), BorderLayout.CENTER);
        this.getContentPane().add(toolPanel, BorderLayout.WEST);
        this.getContentPane().add(rightPanel, BorderLayout.EAST);
        //pack();   // calling pack() at the end, will ensure that every layout and size we just defined gets applied before the stuff becomes visible
    }

    ActionListener actionListener = new ActionListener() {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == clear) {
                System.out.println(layer_index);
                canvas.get(layer_index).clear();
            }

            else if (e.getSource() == buttonPen) {
                System.out.println(layer_index);
                propertyPanel.removeAll();
                propertyPanel.add(penPane);
                revalidate();
                // canvas.get(layer_index).penSize = penSize;

                canvas.get(layer_index).pen();

            }


            else if (e.getSource() == buttonEraser) {
                propertyPanel.removeAll();
                propertyPanel.add(penPane);
                revalidate();
                //canvas.get(layer_index).eraserSize = eraserSize;
                System.out.println(canvas.get(layer_index).eraserSize);
                canvas.get(layer_index).bgColor = bgColor;
                canvas.get(layer_index).eraser();
            }



            else if (e.getSource() == buttonPaintBucket) {
                canvas.get(layer_index).frontColor = frontColor;
                canvas.get(layer_index).paintBucket();
            } else if (e.getSource() == buttonCrop) {
                canvas.get(layer_index).frontColor = frontColor;
                canvas.get(layer_index).bgColor = bgColor;
                canvas.get(layer_index).crop();
            } else if (e.getSource() == buttonMove) {
                canvas.get(layer_index).moveSelected();
            } else if (e.getSource() == buttonSelect) {
                canvas.get(layer_index).select();
            } else if (e.getSource() == buttonShapes) {
                propertyPanel.removeAll();
                propertyPanel.add(shapePane);
                revalidate();


            } else if (e.getSource() == buttonftColor) {
                System.out.println("Select front color now");
                Color c = JColorChooser.showDialog(null, "Choose Front Color", getBackground());
                frontColor = c;
                buttonftColor.setBackground(c);
                canvas.get(0).frontColor = frontColor;
                canvas.get(1).frontColor = frontColor;
                canvas.get(2).frontColor = frontColor;
            } else if (e.getSource() == buttonbcColor) {
                System.out.println("Select background color now");
                Color c = JColorChooser.showDialog(null, "Choose Background Color", getBackground());
                bgColor = c;
                buttonbcColor.setBackground(c);
                canvas.get(0).bgColor = bgColor;
                canvas.get(1).bgColor = bgColor;
                canvas.get(2).bgColor = bgColor;
            }

            // =========== Shapes
            else if (e.getSource() == oval) {
                canvas.get(layer_index).drawOval();
            } else if (e.getSource() == rectangle) {

                canvas.get(layer_index).drawRect();
            }  else if(e.getSource() == straightLine){
                System.out.println("Draw Line");
                canvas.get(layer_index).drawLine();
            }

            // ============= Layers
            else if(e.getSource() == buttonBackground) {
                if (layer_index != 0) {
                    buttonBackground.setText("*** Background");
                    buttonForeground.setText("Foreground");
                    buttonMidground.setText("Midground");
                    //background.setOpaque(!background.isOpaque());
                    layer_index = 0;
                    layers.repaint();
                }

            }
            else if(e.getSource() == buttonMidground) {
                //cl.show(centerPanel,"2");
                if (layer_index != 1) {
                    //midground.setOpaque(!midground.isOpaque());
                    buttonMidground.setText("*** Midground");
                    buttonBackground.setText("Background");
                    buttonForeground.setText("Foreground");
                    layer_index = 1;
                    layers.repaint();
                }

            }
            else if(e.getSource() == buttonForeground){
                //cl.show(centerPanel,"3");
                if(layer_index!=2) {
                    layer_index = 2;
                    //foreground.setOpaque(!foreground.isOpaque());
                    buttonForeground.setText("*** Foreground");
                    buttonBackground.setText("Background");
                    buttonMidground.setText("Midground");
                    layers.repaint();
                }
            }
            else if(e.getSource() == frontVisible){
                canvas.get(2).setVisible(!canvas.get(2).isVisible());
                System.out.println("Hide/show background");
                if(canvas.get(2).isVisible()){
                    frontVisible.setIcon(new ImageIcon("img/icons/eye.png"));
                }
                else{
                    frontVisible.setIcon(new ImageIcon("img/icons/hide.png"));
                }
                layers.repaint();
            }
            else if(e.getSource() == midVisible){
                canvas.get(1).setVisible(!canvas.get(1).isVisible());
                System.out.println("Hide/show background");
                if(canvas.get(1).isVisible()){
                    midVisible.setIcon(new ImageIcon("img/icons/eye.png"));
                }
                else{
                    midVisible.setIcon(new ImageIcon("img/icons/hide.png"));
                }
                layers.repaint();
            }
            else if(e.getSource() == backVisible){
                canvas.get(0).setVisible(!canvas.get(0).isVisible());
                System.out.println("Hide/show background");
                if(canvas.get(0).isVisible()){
                    backVisible.setIcon(new ImageIcon("img/icons/eye.png"));
                }
                else{
                    backVisible.setIcon(new ImageIcon("img/icons/hide.png"));
                }
                layers.repaint();
            }
            else if(e.getSource() == fill){
                canvas.get(layer_index).isFilled = !(canvas.get(layer_index).isFilled);
            }

        }
        };

        private void addMenu() {
            JMenu fileMenu = new JMenu("File");
            JMenu editMenu = new JMenu("Edit");
            JMenu filterMenu = new JMenu("Filter");

            // =========================== File =====================
            // ---New canvas
            JMenuItem createCanvas = new JMenuItem("New canvas");
            createCanvas.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    createNewCanvas();
                }
            });
            fileMenu.add(createCanvas);

            // ---Open
            JMenuItem openItem = new JMenuItem("Open image");
            openItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    open();
                }
            });
            fileMenu.add(openItem);

            // --- Save image
            JMenuItem saveImage = new JMenuItem("Save Image");
            saveImage.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    saveImage();
                }
            });
            fileMenu.add(saveImage);

            // --- Exit
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    System.exit(0);
                }
            });
            fileMenu.add(exitItem);

            // =========================== Edit  =====================
            // ---New canvas
            // ---Copy
            JMenuItem copyItem = new JMenuItem("Copy");
            copyItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    open();
                }
            });
            editMenu.add(copyItem);

            // ---Cut
            JMenuItem cutItem = new JMenuItem("Cut");
            cutItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    open();
                }
            });
            editMenu.add(cutItem);

            // ---Paste
            JMenuItem pasteItem = new JMenuItem("Paste");
            pasteItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    open();
                }
            });
            editMenu.add(pasteItem);

            // ---Free Transform
            JMenuItem freeTransform = new JMenuItem("Free transform");
            freeTransform.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    open();
                }
            });
            editMenu.add(freeTransform);

            //attach menu to a menu bar
            JMenuBar menuBar = new JMenuBar();
            menuBar.add(fileMenu);
           // menuBar.add(editMenu);
           // menuBar.add(filterMenu);
            this.setJMenuBar(menuBar);
        }

        private void createNewCanvas() {
            JTextField field1 = new JTextField();
            JTextField field2 = new JTextField();

            Object[] fields = {
                    "Height", field1,
                    "Width", field2
            };

            JOptionPane.showConfirmDialog(null, fields, "Create New Canvas", JOptionPane.OK_CANCEL_OPTION);
            canvasHeight = Integer.parseInt(field1.getText());
            canvasWidth = Integer.parseInt(field2.getText());
            System.out.println("Height = " + canvasHeight + "Width = " + canvasWidth);
            //this.remove(canvas.get(layer_index));
            switch (layer_index) {
                case 0:
                   // layers.remove(background);
                    background.clear(bgColor);
                    background.setSize(canvasWidth,canvasHeight);
                 //   canvas.set(0,background);
                   // layers.add(background,new Integer(1));
                    //this.getContentPane().add(layers,BorderLayout.CENTER);
                    repaint();
                    break;
                case 1:
                    midground.clear(bgColor);
                    midground.setSize(canvasWidth,canvasHeight);
                    repaint();
                    break;
                case 2:
                    foreground.clear(bgColor);
                    foreground.setSize(canvasWidth,canvasHeight);
                    repaint();
                    break;
            }
           // this.getContentPane().add(canvas.get(layer_index), BorderLayout.CENTER);
            //revalidate();
        }

        // ================ SaveImage
        private void saveImage() {
            try {
                File outputfile = new File("image.png");
              combineLayers();
                    javax.imageio.ImageIO.write(combined, "png", outputfile);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainPanel.this,
                        "Error saving file",
                        "oops!",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        //==========================================================
        //open() - choose a file, load, and display the image

        private void open() {
            File file = null;
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                file = chooser.getSelectedFile();
            }
            if (file != null) {
                displayFile(file);
            }
        }

        // -------------------------------------------------------------
        // Display specified file in the frame

        private void displayFile(File file) {
            try {
                displayBufferedImage(ImageIO.read(file));

            } catch (IOException exception) {
                JOptionPane.showMessageDialog(this, exception);
            }
        }

        // ======= Display final image
        // ---------------------------------------------------------------
        //Display BufferedImage
        public void displayBufferedImage(BufferedImage image) {
            ImageIcon img = new ImageIcon(image);
            Image image1 = img.getImage();
            double imageW = image.getWidth();
            double imageH = image.getHeight();
            double ratio = imageW / imageH;
            System.out.println(ratio + "  " + imageW + "  " + imageH);
            if (imageW > canvas.get(layer_index).getWidth()) {
                imageW = canvas.get(layer_index).getWidth();
                imageH = imageW / ratio;
            }
            if (imageH > canvas.get(layer_index).getHeight()) {
                imageH = canvas.get(layer_index).getHeight();
                imageW = imageH * ratio;
            }

            System.out.println(ratio + "  " + imageW + "  " + imageH);
            canvas.get(layer_index).g2d.drawImage(image1, 0, 0, (int) imageW, (int) imageH, null);
            repaint();
        }

        public class event implements ChangeListener {
            public void stateChanged(ChangeEvent e) {
                penSize = PsizeSlider.getValue();
                canvas.get(0).setPenStroke(penSize);
                canvas.get(1).setPenStroke(penSize);
                canvas.get(2).setPenStroke(penSize);

                eraserSize = EsizeSlider.getValue();
                canvas.get(0).setEraserSize(eraserSize);
                canvas.get(1).setEraserSize(eraserSize);
                canvas.get(2).setEraserSize(eraserSize);

                penPower = PpowerSlider.getValue();
                eraserPower = EpowerSlider.getValue();

                Psize.setText("Pen Size: " + penSize);
                Esize.setText("Eraser Size: " + eraserSize);
                Ppower.setText("Pen Power: " + penPower);
                Epower.setText("Eraser Power: " + eraserPower);
            }
        }

        public void combineLayers(){
            int maxW = Math.max(canvas.get(0).getWidth(),canvas.get(1).getWidth());
            maxW = Math.max(maxW,canvas.get(2).getWidth());
            int maxH = Math.max(canvas.get(0).getHeight(),canvas.get(1).getHeight());
            maxH = Math.max(maxH,canvas.get(2).getHeight());
            combined = new BufferedImage(maxW,maxH, BufferedImage.TYPE_INT_ARGB);
            Graphics g = combined.getGraphics();
            g.drawImage(canvas.get(0).image,0,0,null);
            g.drawImage(canvas.get(1).image,0,0,null);
            g.drawImage(canvas.get(2).image,0,0,null);

        }

    }

