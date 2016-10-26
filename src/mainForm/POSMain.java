package mainForm;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.accessibility.AccessibleContext;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author IT
 */
public class POSMain extends javax.swing.JFrame {

      private final String currUser = POSLogin.user;
  private final ImageIcon msgIcon;
  public final int os;
  public final int OS_WIN = 0;
  public final int OS_MAC = 1;
  
  StringBuffer calculation = new StringBuffer("");
  StringBuffer subStep = new StringBuffer("");

  List<Double> calcNums = new ArrayList();
  List<String> calcOps = new ArrayList();
  Color bgColor;

  ArrayList<String> orderOfEntry = new ArrayList();

  double result = 0.0D;
  List<Double> calcTotal = new ArrayList();
  List<String[]> itemList = new ArrayList();
  int itemCt = 0;
  String lastTransaction = "";
  Calendar date = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);

  StringBuffer currentSale = new StringBuffer("");

  Map<String, String[]> itemData = new POSDatabase().readPrices();
  Map<String, Double> itemPriceByName = initializeValues();
  String itemName = "none";

  boolean payMode = false;
  BigDecimal payTotal;
  double cashTendered = 0.0D;
  boolean validTender = false;
  private JTextField totalCashTendered = null;
  private int totalConfirm;
  private final JPanel inputPayPanel = new JPanel();
  JTextField totalDiscountAmount = null;
  JLabel totalInfo = null;
  private boolean multiPayment = false;

  boolean printerOn = true;
  JTextArea reprintSalesInfo;
  private final int SALE_ITEM_START_INDEX = 5;

  boolean discountApplied = false;

  boolean totalDiscountApplied = false;

  JDialog activePayScreen = null;

  private int payType = 0;
  private final int CASH_TENDERED = 0;
  private final int DEBIT_CREDIT_TENDERED = 1;
  private final int GIFT_CARD_TENDERED = 2;

  boolean saleVoided = false;

  boolean opsLock = true;

  FlowLayout lay = new FlowLayout();

    public POSMain()
  {
    String opSys = System.getProperty("os.name").toLowerCase();

    if (opSys.contains("mac"))
      this.os = 1;
    else {
      this.os = 0;
    }

    initComponents();
    initComponents2();
    POSLogin.centerWindow(this);
    setDefaultCloseOperation(0);
    
    setIconImage(new ImageIcon("loaderS.png").getImage());
    this.msgIcon = new ImageIcon(getClass().getResource("/alert.png"));
    try
    {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (ClassNotFoundException ex)
    {
      Logger.getLogger(POSMain.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (InstantiationException ex)
    {
      Logger.getLogger(POSMain.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (IllegalAccessException ex)
    {
      Logger.getLogger(POSMain.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (UnsupportedLookAndFeelException ex)
    {
      Logger.getLogger(POSMain.class.getName()).log(Level.SEVERE, null, ex);
    }

    addWindowListener(new WindowAdapter()
    {
      public void windowClosing(WindowEvent event)
      {
        POSMain.this.closeConfirm();
      }
    });
    POSDatabase database = new POSDatabase();
    String colorCode = (String)database.getSettings().get("color");
    String[] RGB = colorCode.split("/");
    bgColor = new Color(Integer.parseInt(RGB[0].trim()), Integer.parseInt(RGB[1].trim()), Integer.parseInt(RGB[2].trim()));
    
    //Set background colour to bg color in file for all panels
    getContentPane().setBackground(bgColor);
    setTitle(((String)database.settings.get("title")).trim());
    this.productArea.setBackground(bgColor);
    this.calcPanel.setBackground(bgColor);
    this.buttonPanel.setBackground(bgColor);
    this.bwPanel.setBackground(bgColor);
    setIconImage(new ImageIcon(getClass().getResource("/frameIcon.png")).getImage());
    this.discountTotal.setVisible(false);
    this.cancelSale.setVisible(false);
    this.mainLogo.setBackground(bgColor);
    this.jPanel1.setBackground(bgColor);    //panel holding main logo panel
    
    if (!database.settings.get("icon").trim().isEmpty()) {
        this.mainLogo.setIcon(new StretchIcon("./" + database.getSettings().get("icon").trim()));
    }
    //this.mainDHTlogo.setIcon(new StretchIcon(getClass().getResource("/dhtlogo.png")));
    
    UIManager UI=new UIManager();
    UI.put("OptionPane.background",new Color(0xC31B1B));
    UI.put("OptionPane.border", new LineBorder(Color.black, 1));
    UI.put("OptionPane.buttonAreaBorder", new BevelBorder(0));        
    UI.put("OptionPane.warningDialog.titlePane.background", new Color(0xC31B1B));
    UI.put("OptionPane.errorDialog.titlePane.background", new Color(0xC31B1B));
    
    initCalcListeners();
    
    if (!POSLogin.isPrintOffice)
    {
        this.productArea.removeTabAt(0);
        POSLogin.isPrintOffice = false;
    }    
       
    
    this.setPreferredSize(POSLogin.screenSize);
    this.setSize(POSLogin.screenSize);

    /*
    if (this.os == OS_MAC)
    {
        this.setPreferredSize(new Dimension(1154, 855));
        this.setSize(new Dimension(1154, 855));
    }
    else {
        this.setPreferredSize(new Dimension(1154, 850));
        this.setSize(new Dimension(1154, 860));
    }*/
    
    this.discountTotal.setVisible(false);   //hide discount buttons 
    this.discountItem.setVisible(false);
    
  }

  private boolean checkOpenWindows()
  {
    Window[] w = Window.getWindows();

    for (Window a : w)
    {
      if ((a instanceof JDialog))
      {
        JDialog j = (JDialog)a;

        if ((j.getTitle().equals("Pay Screen")) && (j.isVisible()))
        {
          j.setDefaultCloseOperation(0);
          return true;
        }

        if ((j.getTitle().equals("Reprint receipt")) && (j.isVisible()))
        {
          j.pack();
          a.pack();
          return false;
        }
      }

    }

    return false;
  }

  private void initCalcListeners()
  {
    Action performZero = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcZero.doClick();
      }
    };
    this.calcZero.getInputMap(2).put(KeyStroke.getKeyStroke(96, 0), "keyPress");

    this.calcZero.getActionMap().put("keyPress", performZero);

    this.calcZero.getInputMap(2).put(KeyStroke.getKeyStroke(48, 0), "keyPress");

    Action performOne = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcOne.doClick();
      }
    };
    this.calcOne.getInputMap(2).put(KeyStroke.getKeyStroke(97, 0), "keyPress");

    this.calcOne.getActionMap().put("keyPress", performOne);

    this.calcOne.getInputMap(2).put(KeyStroke.getKeyStroke(49, 0), "keyPress");

    Action performTwo = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcTwo.doClick();
      }
    };
    this.calcTwo.getInputMap(2).put(KeyStroke.getKeyStroke(98, 0), "keyPress");

    this.calcTwo.getActionMap().put("keyPress", performTwo);

    this.calcTwo.getInputMap(2).put(KeyStroke.getKeyStroke(50, 0), "keyPress");

    Action performThree = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcThree.doClick();
      }
    };
    this.calcThree.getInputMap(2).put(KeyStroke.getKeyStroke(99, 0), "keyPress");

    this.calcThree.getActionMap().put("keyPress", performThree);

    this.calcThree.getInputMap(2).put(KeyStroke.getKeyStroke(51, 0), "keyPress");

    Action performFour = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcFour.doClick();
      }
    };
    this.calcFour.getInputMap(2).put(KeyStroke.getKeyStroke(100, 0), "keyPress");

    this.calcFour.getActionMap().put("keyPress", performFour);

    this.calcFour.getInputMap(2).put(KeyStroke.getKeyStroke(52, 0), "keyPress");

    Action performFive = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcFive.doClick();
      }
    };
    this.calcFive.getInputMap(2).put(KeyStroke.getKeyStroke(101, 0), "keyPress");

    this.calcFive.getActionMap().put("keyPress", performFive);

    this.calcFive.getInputMap(2).put(KeyStroke.getKeyStroke(53, 0), "keyPress");

    Action performSix = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcSix.doClick();
      }
    };
    this.calcSix.getInputMap(2).put(KeyStroke.getKeyStroke(102, 0), "keyPress");

    this.calcSix.getActionMap().put("keyPress", performSix);

    this.calcSix.getInputMap(2).put(KeyStroke.getKeyStroke(54, 0), "keyPress");

    Action performSeven = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcSeven.doClick();
      }
    };
    this.calcSeven.getInputMap(2).put(KeyStroke.getKeyStroke(103, 0), "keyPress");

    this.calcSeven.getActionMap().put("keyPress", performSeven);

    this.calcSeven.getInputMap(2).put(KeyStroke.getKeyStroke(55, 0), "keyPress");

    Action performEight = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcEight.doClick();
      }
    };
    this.calcEight.getInputMap(2).put(KeyStroke.getKeyStroke(104, 0), "keyPress");

    this.calcEight.getActionMap().put("keyPress", performEight);

    this.calcEight.getInputMap(2).put(KeyStroke.getKeyStroke(56, 0), "keyPress");

    Action performNine = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcNine.doClick();
      }
    };
    this.calcNine.getInputMap(2).put(KeyStroke.getKeyStroke(105, 0), "keyPress");

    this.calcNine.getActionMap().put("keyPress", performNine);

    this.calcNine.getInputMap(2).put(KeyStroke.getKeyStroke(57, 0), "keyPress");

    Action performEnter = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcEnter.doClick();
      }
    };
    this.calcEnter.getInputMap(2).put(KeyStroke.getKeyStroke(10, 0), "keyPress");

    this.calcEnter.getActionMap().put("keyPress", performEnter);

    Action performClear = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcClear.doClick();
      }
    };
    this.calcClear.getInputMap(2).put(KeyStroke.getKeyStroke(127, 0), "keyPress");

    this.calcClear.getActionMap().put("keyPress", performClear);

    Action performTimes = new AbstractAction()
    {
      public void actionPerformed(ActionEvent e)
      {
        POSMain.this.calcQuantity.doClick();
      }
    };
    this.calcQuantity.getInputMap(2).put(KeyStroke.getKeyStroke(106, 0), "keyPress");

    this.calcQuantity.getActionMap().put("keyPress", performTimes);
  }

  private void closeConfirm()
  {
    int confirm = JOptionPane.showConfirmDialog(this.rootPane, "Are you sure you want to exit the POS system?", "Exit Confirmation", 0, 1, this.msgIcon);

    if (confirm == 0)
    {
      System.exit(0);
      dispose();
    }
  }
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        productArea = new javax.swing.JTabbedPane();
        bwPanel = new javax.swing.JPanel();
        BWSSLet = new javax.swing.JButton();
        BWDSLet = new javax.swing.JButton();
        BWSSLeg = new javax.swing.JButton();
        BWDSLeg = new javax.swing.JButton();
        BWSSTab = new javax.swing.JButton();
        BWDSTab = new javax.swing.JButton();
        BWSSCol = new javax.swing.JButton();
        BWDSCol = new javax.swing.JButton();
        COLSSLet = new javax.swing.JButton();
        COLDSLet = new javax.swing.JButton();
        COLSSLeg = new javax.swing.JButton();
        COLSSTab = new javax.swing.JButton();
        COLDSLeg = new javax.swing.JButton();
        COLDSTab = new javax.swing.JButton();
        COLSSCol = new javax.swing.JButton();
        COLDSCol = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        mainLogo = new javax.swing.JLabel();
        calcPanel = new javax.swing.JPanel();
        jpanel3 = new javax.swing.JPanel();
        calcSubstep = new javax.swing.JLabel();
        calcDisplay = new javax.swing.JTextField();
        calcPad = new javax.swing.JPanel();
        calcOne = new javax.swing.JButton();
        calcTwo = new javax.swing.JButton();
        calcThree = new javax.swing.JButton();
        calcFour = new javax.swing.JButton();
        calcSeven = new javax.swing.JButton();
        calcFive = new javax.swing.JButton();
        calcEight = new javax.swing.JButton();
        calcSix = new javax.swing.JButton();
        calcNine = new javax.swing.JButton();
        errorCorrect = new javax.swing.JButton();
        calcQuantity = new javax.swing.JButton();
        calcClear = new javax.swing.JButton();
        total = new javax.swing.JButton();
        calcEnter = new javax.swing.JButton();
        calcZero = new javax.swing.JButton();
        calcDecimal = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();
        voidLastSale = new javax.swing.JButton();
        discountItem = new javax.swing.JButton();
        receiptToggle = new javax.swing.JButton();
        printReciept = new javax.swing.JButton();
        closeDaySales = new javax.swing.JButton();
        logout = new javax.swing.JButton();
        discountTotal = new javax.swing.JButton();
        cancelSale = new javax.swing.JButton();
        calcDebCred = new javax.swing.JButton();
        returnSale = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        calcResults = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1161, 845));
        setResizable(false);

        bwPanel.setPreferredSize(new java.awt.Dimension(730, 485));

        BWSSLet.setText("<html>BW SS<br> Letter");

        BWDSLet.setText("<html>BW DS<br>Letter");

        BWSSLeg.setText("<html>BW SS<br>Legal");
        BWSSLeg.setMaximumSize(new java.awt.Dimension(108, 29));
        BWSSLeg.setMinimumSize(new java.awt.Dimension(108, 29));
        BWSSLeg.setPreferredSize(new java.awt.Dimension(108, 29));

        BWDSLeg.setText("<html>BW DS<br>Legal");
        BWDSLeg.setMaximumSize(new java.awt.Dimension(108, 29));
        BWDSLeg.setPreferredSize(new java.awt.Dimension(108, 29));

        BWSSTab.setText("<html>BW SS<br>Tab");

        BWDSTab.setText("<html>BW DS<br>Tab");

        BWSSCol.setText("<html>BW SS<br> Col");

        BWDSCol.setText("<html>BW DS<br> Col");

        COLSSLet.setText("<html>Col SS<br> Let");

        COLDSLet.setText("<html>Col DS<br> Let");

        COLSSLeg.setText("<html>Col SS<br> Leg");

        COLSSTab.setText("<html>Col SS<br> Tab");

        COLDSLeg.setText("<html>Col DS<br> Leg");

        COLDSTab.setText("<html>Col DS<br> Tab");

        COLSSCol.setText("<html>Col SS<br> Col");

        COLDSCol.setText("<html>Col DS<br> Col");

        javax.swing.GroupLayout bwPanelLayout = new javax.swing.GroupLayout(bwPanel);
        bwPanel.setLayout(bwPanelLayout);
        bwPanelLayout.setHorizontalGroup(
            bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bwPanelLayout.createSequentialGroup()
                .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(COLSSTab, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLSSLet, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BWSSTab, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BWSSLet, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(bwPanelLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(BWDSLet, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, bwPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BWDSTab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(COLDSLet, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(COLDSTab, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(70, 70, 70)
                .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(COLSSCol, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLSSLeg, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BWSSCol, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BWSSLeg, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(70, 70, 70)
                .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BWDSLeg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BWDSCol, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLDSLeg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLDSCol, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        bwPanelLayout.setVerticalGroup(
            bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(bwPanelLayout.createSequentialGroup()
                .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BWSSLet, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BWDSLet, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BWSSLeg, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BWDSLeg, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25)
                .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BWSSTab, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BWDSTab, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BWSSCol, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BWDSCol, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25)
                .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(COLSSLet, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLDSLet, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLSSLeg, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLDSLeg, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(bwPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(COLSSTab, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLDSTab, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLSSCol, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COLDSCol, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );

        BWSSCol.getAccessibleContext().setAccessibleName("<html>BW SS <br>Col");

        productArea.addTab("Print Center", bwPanel);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        mainLogo.setPreferredSize(jPanel1.getSize()
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainLogo, javax.swing.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(mainLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        mainLogo.setPreferredSize(jPanel1.getSize());

        calcPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        calcPanel.setPreferredSize(new java.awt.Dimension(424, 430));

        jpanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        javax.swing.GroupLayout jpanel3Layout = new javax.swing.GroupLayout(jpanel3);
        jpanel3.setLayout(jpanel3Layout);
        jpanel3Layout.setHorizontalGroup(
            jpanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpanel3Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(calcSubstep, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        jpanel3Layout.setVerticalGroup(
            jpanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(calcSubstep, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        calcPad.setBorder(javax.swing.BorderFactory.createTitledBorder("Key Pad"));

        calcOne.setText("1");

        calcTwo.setText("2");

        calcThree.setText("3");

        calcFour.setText("4");

        calcSeven.setText("7");

        calcFive.setText("5");

        calcEight.setText("8");

        calcSix.setText("6");

        calcNine.setText("9");

        errorCorrect.setText("Error Correct");

        calcQuantity.setText("Quantity (x)");

        calcClear.setText("Clear");

        total.setText("<html><center><b>TOTAL SALE</b></center></html>");

        calcEnter.setText("ENTER");

        calcZero.setText("0");

        calcDecimal.setText(".");

        javax.swing.GroupLayout calcPadLayout = new javax.swing.GroupLayout(calcPad);
        calcPad.setLayout(calcPadLayout);
        calcPadLayout.setHorizontalGroup(
            calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, calcPadLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(calcSeven, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(calcFour, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(calcOne, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calcDecimal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(calcTwo, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calcFive, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(calcEight, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(calcZero, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(calcPadLayout.createSequentialGroup()
                        .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(calcThree, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(calcSix, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(calcNine, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addGap(6, 6, 6)
                        .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(calcQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(calcClear, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(errorCorrect, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(calcPadLayout.createSequentialGroup()
                        .addComponent(calcEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        calcPadLayout.setVerticalGroup(
            calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calcPadLayout.createSequentialGroup()
                .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(calcOne, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcTwo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcThree, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(errorCorrect, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(calcQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calcSix, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcFive, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcFour, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(calcSeven, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcEight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcNine, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcClear, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(calcPadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(total, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calcEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calcZero, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcDecimal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout calcPanelLayout = new javax.swing.GroupLayout(calcPanel);
        calcPanel.setLayout(calcPanelLayout);
        calcPanelLayout.setHorizontalGroup(
            calcPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calcPanelLayout.createSequentialGroup()
                .addGroup(calcPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jpanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(calcPad, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(calcDisplay, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(0, 0, 0))
        );
        calcPanelLayout.setVerticalGroup(
            calcPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(calcPanelLayout.createSequentialGroup()
                .addComponent(jpanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(calcDisplay, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcPad, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        voidLastSale.setText("<html><b>VOID</b> Last Sale</html>");

        discountItem.setText("Discount Item");

        receiptToggle.setText("Receipt ON/OFF");

        printReciept.setText("Reprint Receipt");

        closeDaySales.setText("Close Sales");
        closeDaySales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeDaySalesActionPerformed(evt);
            }
        });

        logout.setText("<html><font color=\"red\">LOGOUT</font></html>");

        discountTotal.setText("discountTotal HIDDEN");

        cancelSale.setText("cancel sale");
        cancelSale.setEnabled(false);

        calcDebCred.setText("deb cred");

        returnSale.setText("Return");
        returnSale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnSaleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(returnSale, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(voidLastSale, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(receiptToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(printReciept, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeDaySales, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(discountTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelSale, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(buttonPanelLayout.createSequentialGroup()
                        .addComponent(calcDebCred)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(discountItem, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonPanelLayout.createSequentialGroup()
                .addComponent(voidLastSale, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(returnSale, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(receiptToggle, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(printReciept, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeDaySales, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logout, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(discountTotal)
                    .addComponent(cancelSale))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(calcDebCred, 0, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(discountItem, javax.swing.GroupLayout.PREFERRED_SIZE, 6, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEtchedBorder(), javax.swing.BorderFactory.createTitledBorder("Transaction")));

        calcResults.setEditable(false);
        calcResults.setColumns(20);
        calcResults.setLineWrap(true);
        calcResults.setRows(5);
        calcResults.setWrapStyleWord(true);
        calcResults.setMargin(new java.awt.Insets(6, 2, 2, 2));
        jScrollPane1.setViewportView(calcResults);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addComponent(productArea, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(calcPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(productArea, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(calcPanel, 434, 434, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        productArea.setPreferredSize(POSLogin.screenSize);
        productArea.setSize(POSLogin.screenSize);
        productArea.setPreferredSize(POSLogin.screenSize);
        productArea.setSize(POSLogin.screenSize);

        pack();
    }// </editor-fold>//GEN-END:initComponents

        JTextField csTodaysTillAmt;
        JPanel csEnterTillAmtPanel;
        JPanel csEndOfDayTotalsPanel;
        JTextPane csEndOfDaySalesDisplay;
        int csTillPanelResult;
    
    private void closeDaySalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeDaySalesActionPerformed
        
        // Inquiring about today's till amount not required
        JLabel closeSaleInfo = makeHTMLLabels("Enter starting cash amount used in today's till:", 150);
        this.csTodaysTillAmt = new JTextField();
        csEnterTillAmtPanel = new JPanel();
        JButton csTillNext = new JButton("Next");
        closeSaleInfo.setPreferredSize(new Dimension(100, 20));
        csTodaysTillAmt.setPreferredSize(new Dimension(100, 20));
        csTillNext.setPreferredSize(new Dimension(100, 20));
        
        Dimension size = csTillNext.getPreferredSize();
        Border empty = new EmptyBorder(0, 3, 0, 0);
        this.csEnterTillAmtPanel.setPreferredSize(new Dimension((int)(size.getWidth() * 3.0D), 80));
        this.csEnterTillAmtPanel.setBackground(POSLogin.backgroundColor);
        this.csEnterTillAmtPanel.setBorder(new CompoundBorder(new BevelBorder(0), empty));
        
        this.csTodaysTillAmt.setBorder(new CompoundBorder(new LineBorder(Color.red), empty));
        closeSaleInfo.setBorder(new BevelBorder(1));
        this.csTodaysTillAmt.setMargin(new Insets(0, 10, 0, 0));

        this.csEnterTillAmtPanel.setLayout(new GridLayout(0, 1, 1, 5));
        this.csEnterTillAmtPanel.add(closeSaleInfo);
        this.csEnterTillAmtPanel.add(this.csTodaysTillAmt);
        
        csEnterTillAmtPanel.getInputMap(2).put(KeyStroke.getKeyStroke(10, 0), "keyPress");

        csEnterTillAmtPanel.getActionMap().put("keyPress", null);       
        
        //this.csTillPanelResult = JOptionPane.showConfirmDialog(this.rootPane, this.csEnterTillAmtPanel, "Close Sales", 2, -1);
        this.csTillPanelResult = JOptionPane.YES_OPTION;
        csTodaysTillAmt.setText("0");
        
        if (csTillPanelResult == JOptionPane.YES_OPTION || csTillPanelResult == JOptionPane.OK_OPTION)
        { 
            if ((!csTodaysTillAmt.getText().trim().isEmpty()) || !POSSettings.checkValidAlphaNumeric(csTodaysTillAmt.getText().trim()))
            {
                csEndOfDayTotalsPanel = generateGenericPanel("End of Day Totals");
                csEndOfDayTotalsPanel.setPreferredSize(new Dimension(800, 650));
                this.csEndOfDayTotalsPanel.setBackground(POSLogin.backgroundColor);
                
                csEndOfDaySalesDisplay = new JTextPane();
                csEndOfDaySalesDisplay.setPreferredSize(new Dimension(790, 600));
                csEndOfDaySalesDisplay.setBorder(new CompoundBorder(new LineBorder(Color.red, 1), new BevelBorder(0)));
                
                csEndOfDayTotalsPanel.add(csEndOfDaySalesDisplay);
                
                Calendar dateRaw = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                
                String dateElements[] =  dateFormat.format(dateRaw.getTime()).split(" ")[0].split("/");
                String dateProperFormat = dateElements[1] + "/" + dateElements[2] + "/" + dateElements[0];

                String csDaysSaleResults = POSPrinter.displayLogDate(dateProperFormat, dateProperFormat, csTodaysTillAmt.getText().trim());
                csEndOfDaySalesDisplay.setText(csDaysSaleResults);
                
                StyledDocument doc = csEndOfDaySalesDisplay.getStyledDocument();
                SimpleAttributeSet center = new SimpleAttributeSet();
                StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                doc.setParagraphAttributes(0, doc.getLength(), center, false);
                
                JOptionPane.showConfirmDialog(this.rootPane, csEndOfDayTotalsPanel, "End Of Day Totals", 2, -1);
                
            }    
            else
            {
                JOptionPane.showConfirmDialog(this.rootPane, "Please correctly enter today's till amount.", "Missing or Incorrect Parameter", 0, 1, this.msgIcon);
                csTillPanelResult = JOptionPane.showConfirmDialog(this.rootPane, this.csEnterTillAmtPanel, "Close Sales", 2, -1);
            }
        }    
        
    }//GEN-LAST:event_closeDaySalesActionPerformed

    private boolean saleReturn = false;
    
    private void returnSaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnSaleActionPerformed

        boolean hasPermission = getPermission();
        
        if (hasPermission) {
            //Open up the reprint receipt function to display today's receipts
            saleReturn = true;
            String sale = printRecieptActionPerformed(null);
            String saleByLine[] = sale.split(System.lineSeparator());
            ArrayList<String[]> returnItems = new ArrayList<String[]>();
            int First_Occurance_Of_Dash_Separator = 30;

            //If cancel is chosen no output is returned
            if (!sale.isEmpty()) {
                //If receipt is a return receipt it will have a "-" before the separator
                if (sale.indexOf("-") > First_Occurance_Of_Dash_Separator) {
                    //Find the total amount and replace it with negative value for return
                    for (String s : saleByLine) {
                        sale = "";

                        //If line is the total line, negate the total for sales calculation
                        if (s.contains("Total:")) {
                            String value = s.substring(s.lastIndexOf(" "), s.length()).trim();          
                            String newValue = String.format("%.2f", Double.parseDouble(value)*-1);
                            cashTendered = Double.parseDouble(newValue);
                            payType = 0;

                            s = s.replace(value, newValue);
                        }

                        //Negate also the item quantities so item sale totals correspond
                        if (s.contains("x")) {
                            String line = s.substring(0, s.lastIndexOf("="));
                            String negQuantity = "-"+line.split("x")[1].trim();
                            String newItem[] = {line.split("x")[0].trim(), negQuantity};
                            returnItems.add(newItem);
                        }

                        //Recreate sale file string
                        sale = sale + s + System.lineSeparator();
                    }
                    
                    //Update global sale item list to return list of items
                    itemList = returnItems;
                    //Write the negative sale to file
                    writeSale("");
                    
                    calcResults.setText("Return Processed");
                }
                else {
                    JOptionPane.showMessageDialog(this.rootPane, "You have selected a return receipt instead of a sale receipt.", "Invalid return", JOptionPane.ERROR_MESSAGE, msgIcon);
                }
            }
            
            
            saleReturn = false;
        }
    }//GEN-LAST:event_returnSaleActionPerformed

    private JPanel generateGenericPanel(String title)
    {
        JPanel newPanel = new JPanel();
        newPanel.setName(title);
        
        Dimension size = new Dimension(100, 60);
        Border empty = new EmptyBorder(0, 3, 0, 0);
        newPanel.setPreferredSize(new Dimension((int)(size.getWidth() * 3.0D), 80));
        newPanel.setSize(new Dimension((int)(size.getWidth() * 3.0D), 80));
        newPanel.setBackground(POSLogin.backgroundColor);
        newPanel.setBorder(new CompoundBorder(new BevelBorder(0), empty));

        newPanel.setLayout(new GridLayout(0, 1, 1, 5));

        return newPanel;
    }        
    
    private JLabel makeHTMLLabels(String msg, int width)
    {
        final String html1 = "<html><center><body style='width: ";
        final String html2 = "px'>";

        return new JLabel(html1 + width + html2 + msg);

    }
    
    private void initComponents2()
    {

        this.BWSSLet.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.BWSSLetActionPerformed(evt);
          }
        });
        this.BWDSLet.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.BWDSLetActionPerformed(evt);
          }
        });
        this.BWSSLeg.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.BWSSLegActionPerformed(evt);
          }
        });
        this.BWDSLeg.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.BWDSLegActionPerformed(evt);
          }
        });
        this.BWSSTab.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.BWSSTabActionPerformed(evt);
          }
        });
        this.BWDSTab.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.BWDSTabActionPerformed(evt);
          }
        });
        this.BWSSCol.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.BWSSColActionPerformed(evt);
          }
        });
        this.BWDSCol.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.BWDSColActionPerformed(evt);
          }
        });
        this.calcDebCred.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcDebCredActionPerformed(evt);
          }
        });
        this.calcDebCred.setVisible(false);

        this.discountTotal.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.discountTotalActionPerformed(evt);
          }
        });
        
        this.COLSSLet.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSMain.this.COLSSLetActionPerformed(evt);
        }
      });

      this.COLDSLet.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSMain.this.COLDSLetActionPerformed(evt);
        }
      });

      this.COLSSLeg.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSMain.this.COLSSLegActionPerformed(evt);
        }
      });

      this.COLDSLeg.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSMain.this.COLDSLegActionPerformed(evt);
        }
      });

      this.COLSSTab.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSMain.this.COLSSTabActionPerformed(evt);
        }
      });

      this.COLDSTab.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSMain.this.COLDSTabActionPerformed(evt);
        }
      });

      this.COLSSCol.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSMain.this.COLSSColActionPerformed(evt);
        }
      });

      this.COLDSCol.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSMain.this.COLDSColActionPerformed(evt);
        }
      });
      
      initTabs();
      setupExtItems();

        this.cancelSale.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.cancelSaleActionPerformed(evt);
          }
        });

        this.discountItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.discountItemActionPerformed(evt);
          }
        });

        this.printReciept.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.printRecieptActionPerformed(evt);
          }
        });

        this.receiptToggle.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.receiptToggleActionPerformed(evt);
          }
        });

        this.voidLastSale.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.voidLastSaleActionPerformed(evt);
          }
        });

        this.total.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.totalActionPerformed(evt);
          }
        });

        this.logout.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.logoutActionPerformed(evt);
          }
        });
        
        this.calcTwo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSMain.this.calcTwoActionPerformed(evt);
         }
         });

        this.calcThree.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcThreeActionPerformed(evt);
          }
        });

        this.calcFour.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcFourActionPerformed(evt);
          }
        });

        this.calcFive.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcFiveActionPerformed(evt);
          }
        });

        this.calcSix.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcSixActionPerformed(evt);
          }
        });

        this.calcSeven.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcSevenActionPerformed(evt);
          }
        });

        this.calcEight.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcEightActionPerformed(evt);
          }
        });

        this.calcNine.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcNineActionPerformed(evt);
          }
        });

        this.calcDecimal.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcDecimalActionPerformed(evt);
          }
        });

        this.calcZero.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcZeroActionPerformed(evt);
          }
        });

        this.calcEnter.setToolTipText("Press this after selecting product, 'quantity', and amount.");
        this.calcEnter.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcEnterActionPerformed(evt);
          }
        });

        this.calcClear.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcClearActionPerformed(evt);
          }
        });

        this.calcQuantity.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcQuantityActionPerformed(evt);
          }
        });

        this.calcOne.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.calcOneActionPerformed(evt);
          }
        });

        this.errorCorrect.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            POSMain.this.errorCorrectActionPerformed(evt);
          }
        });
        
        
    }


    private void initTabs()
    {
        POSDatabase database = new POSDatabase();
        Map<String, String> settings = database.getSettings();
        
        String tabs = settings.get("tabs");
        productArea.setPreferredSize(POSLogin.screenSize);
        productArea.setSize(POSLogin.screenSize);
        this.repaint();
        
        for (String tabName : tabs.split("/"))
        {
            JPanel tmpPanel = new JPanel();
            tmpPanel.setName(tabName);
            //tmpPanel.setPreferredSize(new Dimension(735, 440));
            tmpPanel.setPreferredSize(POSLogin.screenSize);
            tmpPanel.setBackground(bgColor);
            productArea.add(tmpPanel);
        }
        
    }
    
  private void calcButtonPress(String key, String type)
  {
    if (type.equals("value"))
    {
      this.calculation.append(key);
      this.calcSubstep.setText(new StringBuilder().append("  ").append(this.calculation.toString()).toString());
      this.subStep.append(key);
      this.calcDisplay.setText(this.subStep.toString());
      this.orderOfEntry.add(type);
    }
    else if (type.equals("op"))
    {
      this.calculation.append(new StringBuilder().append(" ").append(key).append(" ").toString());
      this.calcOps.add(key);
      this.calcNums.add(Double.valueOf(Double.parseDouble(this.subStep.toString())));
      this.subStep.setLength(0);
      this.calcDisplay.setText("");
    }
    else if (type.equals("prod"))
    {
      try
      {
        if (!this.orderOfEntry.isEmpty())
        {
          this.itemName = ((String[])this.itemData.get(key))[0];
          this.calculation.append(((String[])this.itemData.get(key))[1]);
          this.calcSubstep.setText(this.calculation.toString());
          this.subStep.append(((String[])this.itemData.get(key))[1]);
          this.calcDisplay.setText(this.subStep.toString());
          this.orderOfEntry.add(type);
        }
        else
        {
          this.itemName = ((String[])this.itemData.get(key))[0];
          this.calculation.append(new StringBuilder().append(((String[])this.itemData.get(key))[1]).append(" x ").append("1").toString());
          this.orderOfEntry.add(type);
          this.calcNums.add(Double.valueOf(Double.parseDouble(((String[])this.itemData.get(key))[1])));
          this.calcNums.add(Double.valueOf(1.0D));
          this.calcOps.add("x");
          this.calcEnter.doClick();
        }
      }
      catch (NullPointerException e)
      {
        System.err.println(new StringBuilder().append("Error: ").append(this.itemData.entrySet()).toString());
      }
    }
  }

  private void writeSale(String saleInfo)
  {
    BufferedWriter br = null;
    try
    {
      String tender = "";
      String tenderType = "";
      
      if (multiplePayment.isEmpty()) {
          tender = String.format("%.2f", cashTendered);
      }
      else {
          for (int i = 0; i < multiplePayment.size(); i = i + 2) {
              tender = tender + String.format("%.2f", multiplePayment.get(i)) +"/";
              tenderType = tenderType + String.format("%.0f", multiplePayment.get(i + 1)) + "/";
          }
          
          tender = tender + String.format("%.2f", cashTendered);
          tenderType = tenderType + String.format("%d", payType);
      }
        
      if (saleInfo == null)
      {
        File salesFile = new File(POSSplash.sales);

        salesFile.delete();
        salesFile.createNewFile();
      }
      else if (saleInfo.length() == 0)
      {
        br = new BufferedWriter(new FileWriter(new File(POSSplash.sales), true));
        br.flush();

        br.append(new StringBuilder().append(this.currUser).append(", ").append(this.date.get(2) + 1).append("/").append(this.date.get(5)).append("/").append(this.date.get(1)).append(", ").append(tender).append(", ").append(tenderType).append(", ").append(POSLogin.currentTill).append(", ").toString());

        for (String[] item : this.itemList)
        {
          br.append(new StringBuilder().append(item[0].trim()).append(", ").append(item[1].trim()).append(", ").toString());
        }

        br.append(System.lineSeparator());
      }
      else
      {
        br = new BufferedWriter(new FileWriter(new File(POSSplash.sales)));

        br.write(saleInfo);
      }

      if (br != null) {
        br.close();
      }
    }
    catch (IOException e)
    {
      System.err.println(new StringBuilder().append("File error POSMain (1142): ").append(e).toString());
    }
  }

  private void updateDisplay(String newText)
  {
    this.currentSale.append(System.lineSeparator());
    this.currentSale.append(newText);
    this.calcResults.setText(this.currentSale.toString());
    this.calcSubstep.setText("");
  }

  private Map<String, Double> initializeValues()
  {
    Map temp = new TreeMap();

    for (String key : this.itemData.keySet()) {
      temp.put(((String[])this.itemData.get(key))[0].trim(), Double.valueOf(Double.parseDouble(((String[])this.itemData.get(key))[1].trim())));
    }
    return temp;
  }

  private void clearValues(int level)
  {
    this.subStep.setLength(0);
    this.calcSubstep.setText("");
    this.calculation.setLength(0);
    this.calcDisplay.setText(this.subStep.toString());
    
    if (level >= 1)
    {
      this.calcNums.clear();
      this.calcOps.clear();
      this.orderOfEntry.clear();
    }
    if ((level >= 2))
    {
      this.totalDiscountApplied = false;
      this.currentSale.setLength(0);
      this.calcTotal.clear();
      this.itemList.clear();
      this.payType = 0;
      
      if (level != 3) {
        calcResults.setText("");
      }
    }
  }

  private void totalActionPerformed(ActionEvent evt)
  {
    if (this.calcTotal.size() > 0)
    {
      if (!this.payMode)
      {
        this.cancelSale.setEnabled(true);
        this.discountItem.setEnabled(false);

        double totalCost = 0.0D;

        for (int i = 0; i < this.calcTotal.size(); i++) {
          totalCost += ((Double)this.calcTotal.get(i)).doubleValue();
        }
        this.payTotal = new BigDecimal(String.format("%.2f", new Object[] { Double.valueOf(totalCost) }));
        this.payTotal = roundToProperCents(this.payTotal);

        StringBuilder dash = new StringBuilder("");

        for (int i = 0; i < 30; i++) {
          dash.append("-");
        }
        updateDisplay(String.format("%36s", new Object[] { dash }));
        //updateDisplay(String.format("%-28s %-5s %-6.2f", new Object[] { "Total: ", "", this.payTotal }));
        updateDisplay(String.format("%-26s %-5s %-6.2f", new Object[] { "Total: ", "", this.payTotal }));

        this.payMode = true;
        toggleButtonLock("ops");

        createPayDialog(totalCost);
        
        clearValues(3);
      }
      else
      {
        JOptionPane.showMessageDialog(null, "A total has already been calculated.", "Invalid Entry", 0, this.msgIcon);
      }
    }
    else JOptionPane.showMessageDialog(null, "No items have been entered.", "Item Error", 0, this.msgIcon);
  }

  private BigDecimal activeTotal; // keeps track of total to pay in multi payments
  
  private void createPayDialog(double total)
  {
    JPanel payDialogContainer = new JPanel();
    payDialogContainer.setLayout(new GridLayout(1, 2));
      
    this.totalInfo = new JLabel(new StringBuilder().append("  Total to pay = ").append(String.format("%.2f", new Object[] { Double.valueOf(total) })).toString());
    Font f = new Font(Font.SANS_SERIF, 3, 24);
    totalInfo.setFont(f);
    this.totalCashTendered = new JTextField();
    totalCashTendered.setFont(f);
    JButton totalDebOrCred = new JButton("Debit");
    totalDebOrCred.setPreferredSize(new Dimension(200,75));
    JButton totalCash = new JButton("Cash");
    totalCash.setPreferredSize(new Dimension(200,75));
    JButton totalGiftCard = new JButton("Gift Card");
    totalGiftCard.setPreferredSize(new Dimension(200,75));
    JButton totalDiscount = new JButton("Total Discount");
    totalDiscount.setPreferredSize(new Dimension(200,75));
    this.totalDiscountAmount = new JTextField("Enter Discount Percent Here If Applicable");
    totalDiscountAmount.setFont(f);
    final String initTotal = String.format("%.2f", total);
    activeTotal = new BigDecimal(initTotal);
    
    totalDiscountAmount.addFocusListener(new java.awt.event.FocusAdapter() {
    public void focusGained(java.awt.event.FocusEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                totalDiscountAmount.setText("");
            }
            });
        }
    });
    
    Dimension size = totalDebOrCred.getPreferredSize();
    Border empty = new EmptyBorder(0, 3, 0, 0);
    this.inputPayPanel.setPreferredSize(new Dimension((int)(size.getWidth() * 3.0D), (int)(size.getHeight() * 10.0D)));
    this.inputPayPanel.setBackground(POSLogin.backgroundColor);
    this.inputPayPanel.setBorder(new CompoundBorder(new BevelBorder(0), empty));
    
    this.totalInfo.setName("totalInfo");
    this.totalCashTendered.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), empty));
    this.totalDiscountAmount.setBorder(new CompoundBorder(new LineBorder(Color.BLACK), empty));
    this.totalInfo.setBorder(new BevelBorder(1));
    this.totalCashTendered.setMargin(new Insets(0, 10, 0, 0));
    this.totalDiscountAmount.setMargin(new Insets(0, 10, 0, 0));

    this.inputPayPanel.setLayout(new GridLayout(0 ,1, 0, 3));
    this.inputPayPanel.add(this.totalInfo);
    this.inputPayPanel.add(this.totalCashTendered);
    this.inputPayPanel.add(totalDebOrCred);
    this.inputPayPanel.add(totalCash);
    this.inputPayPanel.add(totalGiftCard);
    this.inputPayPanel.add(totalDiscount);
    this.inputPayPanel.add(this.totalDiscountAmount);
    
    this.inputPayPanel.getInputMap(2).put(KeyStroke.getKeyStroke(10, 0), "keyPress");

    this.inputPayPanel.getActionMap().put("keyPress", null);
    
    totalDebOrCred.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          if (POSMain.this.totalCashTendered.getText().isEmpty())
          {
              POSMain.this.totalCashTendered.setText(String.format("%.2f",activeTotal));
          } 
          
            POSMain.this.cashTendered = Double.parseDouble(POSMain.this.totalCashTendered.getText().trim());

            if (new BigDecimal(String.format("%.2f", new Object[] { Double.valueOf(POSMain.this.cashTendered) })).compareTo(POSMain.this.payTotal) >= 0) {
              POSMain.this.calcDebCred.doClick();
            }
            else
            {
              POSMain.this.multiPayment = true;
              POSMain.this.calcDebCred.doClick();
            }
          
        }
        catch (NumberFormatException ex) {
          JOptionPane.showConfirmDialog(POSMain.this.rootPane, "Please enter a valid number", "Invalid Input", 2, 1, POSMain.this.msgIcon);
          System.err.println("Number Format Error: " + ex);
        }
      }
    });
    totalCash.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        try
        {
          if (POSMain.this.totalCashTendered.getText().isEmpty())
          {
              POSMain.this.totalCashTendered.setText(String.format("%.2f",activeTotal));
          }    
            POSMain.this.cashTendered = Double.parseDouble(POSMain.this.totalCashTendered.getText().trim());

            if (new BigDecimal(String.format("%.2f", new Object[] { Double.valueOf(POSMain.this.cashTendered) })).compareTo(POSMain.this.payTotal) >= 0) {
              POSMain.this.calcEnter.doClick();
            }
            else
            {
              POSMain.this.multiPayment = true;
              POSMain.this.calcEnter.doClick();
            }
          
        }
        catch (NumberFormatException ex) {
          JOptionPane.showConfirmDialog(POSMain.this.rootPane, "Please enter a valid number", "Invalid Input", 2, 1, POSMain.this.msgIcon);
          System.err.println("Number format error: " + ex);
        }
      }
    });
    totalGiftCard.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
          if (POSMain.this.totalCashTendered.getText().isEmpty())
          {
              POSMain.this.totalCashTendered.setText(String.format("%.2f",activeTotal));
          }
          try
          {
            POSMain.this.cashTendered = Double.parseDouble(POSMain.this.totalCashTendered.getText().trim());

            if (new BigDecimal(String.format("%.2f", new Object[] { Double.valueOf(POSMain.this.cashTendered) })).compareTo(POSMain.this.payTotal) >= 0)
            {
              POSMain.this.payType = 2;
              POSMain.this.calcPayment();
            }
            else
            {
              POSMain.this.multiPayment = true;
              POSMain.this.payType = 2;
              POSMain.this.calcPayment();
            }

          }
          catch (NumberFormatException ex)
          {
            JOptionPane.showConfirmDialog(POSMain.this.rootPane, "Please enter a valid number", "Invalid Input", 2, 1, POSMain.this.msgIcon);
          }
      } 
    });
    totalDiscount.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        if (!POSMain.this.totalDiscountAmount.getText().isEmpty())
        {
          if (!POSMain.this.totalDiscountApplied)
          {
            try
            {
              POSMain.this.discountTotal.doClick();
            }
            catch (NumberFormatException ex)
            {
              JOptionPane.showConfirmDialog(POSMain.this.rootPane, "Please enter a discount percentage", "Invalid Input", 2, 1, POSMain.this.msgIcon);
            }
          }
          else
            JOptionPane.showConfirmDialog(POSMain.this.rootPane, "Discount can only be applied to sale once.", "Incorrect Input", 0, 1, POSMain.this.msgIcon);
        }
        else
          JOptionPane.showConfirmDialog(POSMain.this.rootPane, "Discount field cannot be empty", "Incorrect Input", 0, 1, POSMain.this.msgIcon);
      }
    });
    this.totalCashTendered.addAncestorListener(new RequestFocusListener());

    // Create button key pad for pay dialog
    JPanel payDialogKeyPad = new JPanel();
    payDialogKeyPad.setLayout(new GridLayout(4, 3));
    payDialogKeyPad.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(50, 50, 50, 50), new TitledBorder("Keypad")));
    payDialogKeyPad.setBackground(bgColor);
    
    JButton b0 = new JButton("0");
    b0.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b0.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "0");
        }
    });
    JButton b1 = new JButton("1");
    b1.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b1.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "1");
        }
    });
    JButton b2 = new JButton("2");
    b2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b2.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "2");
        }
    });
    JButton b3 = new JButton("3");
    b3.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b3.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "3");
        }
    });
    JButton b4 = new JButton("4");
    b4.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b4.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "4");
        }
    });
    JButton b5 = new JButton("5");
    b5.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b5.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "5");
        }
    });
    JButton b6 = new JButton("6");
    b6.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b6.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "6");
        }
    });
    JButton b7 = new JButton("7");
    b7.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b7.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "7");
        }
    });
    JButton b8 = new JButton("8");
    b8.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b8.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "8");
        }
    });
    JButton b9 = new JButton("9");
    b9.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    b9.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            totalCashTendered.setText(totalCashTendered.getText() + "9");
        }
    });
    JButton bdelete = new JButton("Delete");
    bdelete.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
    bdelete.addActionListener(new ActionListener()
    {
        public void actionPerformed (ActionEvent e){
            if (!totalCashTendered.getText().isEmpty()) {
                totalCashTendered.setText(totalCashTendered.getText().substring(0, totalCashTendered.getText().length() - 1));
            }
        }
    });
    
    // Add number keys to panel
    payDialogKeyPad.add(b1);
    payDialogKeyPad.add(b2);
    payDialogKeyPad.add(b3);
    payDialogKeyPad.add(b4);
    payDialogKeyPad.add(b5);
    payDialogKeyPad.add(b6);
    payDialogKeyPad.add(b7);
    payDialogKeyPad.add(b8);
    payDialogKeyPad.add(b9);
    payDialogKeyPad.add(b0);
    payDialogKeyPad.add(bdelete);
    
    // Add panels to container and display in JOptionPane
    payDialogContainer.add(inputPayPanel);
    payDialogContainer.add(payDialogKeyPad);
    this.totalConfirm = JOptionPane.showConfirmDialog(this.rootPane, payDialogContainer, "Pay Screen", 2, -1);
    //this.totalConfirm = JOptionPane.showConfirmDialog(this.rootPane, this.inputPayPanel, "Pay Screen", 2, -1);

    while ((this.cashTendered == 0.0D) || (this.multiPayment))
    {
      if (!checkOpenWindows() && totalConfirm != 2) {
        this.totalConfirm = JOptionPane.showConfirmDialog(this.rootPane, this.inputPayPanel, "Pay Screen", 2, -1);
      }

      if (this.totalConfirm == 2)
      {
        int cancelSaleConfirm = JOptionPane.showConfirmDialog(this.rootPane, "Are you sure you wish to cancel the transaction?", "Confirm cancel", 2, 1, this.msgIcon);
        
        if (cancelSaleConfirm == 0)
        {
          this.cancelSale.doClick();
          break;
        }

        this.totalConfirm = 0;
      }

    }

    this.inputPayPanel.removeAll();
    this.totalConfirm = 0;
    this.cashTendered = 0.0D;
  }

  private void receiptToggleActionPerformed(ActionEvent evt)
  {
    this.printerOn = (!this.printerOn);

    if (this.printerOn)
      this.receiptToggle.setText("Receipt On");
    else
      this.receiptToggle.setText("Receipt Off");
  }

  private void COLSSLetActionPerformed(ActionEvent evt) {
    calcButtonPress("cletss", "prod");
  }

  private void BWSSLetActionPerformed(ActionEvent evt) {
    calcButtonPress("bwletss", "prod");
  }

  private void BWDSLetActionPerformed(ActionEvent evt) {
    calcButtonPress("bwletds", "prod");
  }

  private void BWSSLegActionPerformed(ActionEvent evt) {
    calcButtonPress("bwlegss", "prod");
  }

  private void BWDSLegActionPerformed(ActionEvent evt) {
    calcButtonPress("bwlegds", "prod");
  }

  private void BWSSTabActionPerformed(ActionEvent evt) {
    calcButtonPress("bwtabss", "prod");
  }

  private void BWDSTabActionPerformed(ActionEvent evt) {
    calcButtonPress("bwtabds", "prod");
  }

  private void BWSSColActionPerformed(ActionEvent evt) {
    calcButtonPress("bwcolss", "prod");
  }

  private void BWDSColActionPerformed(ActionEvent evt) {
    calcButtonPress("bwcolds", "prod");
  }

  private void COLDSLetActionPerformed(ActionEvent evt) {
    calcButtonPress("cletss", "prod");
  }

  private void COLSSLegActionPerformed(ActionEvent evt) {
    calcButtonPress("clegss", "prod");
  }

  private void COLDSLegActionPerformed(ActionEvent evt) {
    calcButtonPress("clegds", "prod");
  }

  private void COLSSTabActionPerformed(ActionEvent evt) {
    calcButtonPress("ctabss", "prod");
  }

  private void COLDSTabActionPerformed(ActionEvent evt) {
    calcButtonPress("ctabds", "prod");
  }

  private void COLSSColActionPerformed(ActionEvent evt) {
    calcButtonPress("ccolss", "prod");
  }

  private void COLDSColActionPerformed(ActionEvent evt) {
    calcButtonPress("ccolds", "prod");
  }

  private String printRecieptActionPerformed(ActionEvent evt)
  {
    Calendar todayDate = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.ENGLISH);
    final Map<String, String[]> todaySales = new TreeMap();
    Scanner read = null;
    try
    {
      read = new Scanner(new File(POSSplash.sales));
    } catch (FileNotFoundException e) {
      System.err.println(new StringBuilder().append("File not found error POSMain (1712): ").append(e).toString());
    }
    int receiptCount = 0;

    if (read != null)
    {
      while (read.hasNextLine())
      {
        String[] saleSplit = read.nextLine().split(",");

        if (saleSplit[1].split("/")[1].trim().equals(String.format("%d", new Object[] { Integer.valueOf(todayDate.get(5)) })))
        {
          receiptCount++;
          todaySales.put(new StringBuilder().append("Receipt ").append(receiptCount).toString(), saleSplit);
        }

      }

      JPanel inputPanel = new JPanel();
      JLabel comboInfo = makeHTMLLabels("Today's Receipts:", 40);
      comboInfo.setAlignmentX(1);
      final JComboBox salesList = new JComboBox();
      JLabel receiptPreview = makeHTMLLabels("Receipt Preview:", 40);
      receiptPreview.setAlignmentX(1);
      this.reprintSalesInfo = new JTextArea();
      reprintSalesInfo.setBorder(new CompoundBorder(new LineBorder(Color.black, 1), new BevelBorder(0)));

      inputPanel.setLayout(new GridLayout(0, 2, 1, 1));
      inputPanel.setBackground(POSLogin.backgroundColor);

      ItemListener itemListener = new ItemListener()
      {
        public void itemStateChanged(ItemEvent itemEvent)
        {
          POSMain.this.displayReceipt(salesList.getSelectedItem(), todaySales);
        }
      };
      salesList.addItemListener(itemListener);

      for (String receiptNumber : todaySales.keySet()) {
        salesList.addItem(receiptNumber);
      }
      inputPanel.add(comboInfo);
      inputPanel.add(salesList);
      inputPanel.add(receiptPreview);
      inputPanel.add(this.reprintSalesInfo);

      String confirmMsg = "";   //Change display msg of confirm window based on receipt or return
      
      if (saleReturn) {
          confirmMsg = "Return sale";
      }
      else {
          confirmMsg = "Reprint receipt";
      }
      
      int reprintConfirm = JOptionPane.showConfirmDialog(this.rootPane, inputPanel, confirmMsg, 2, -1);
      
      //Show receipts for selection for printing or a return
      if (!saleReturn) {
        
        if (reprintConfirm == 0)
        {
          this.lastTransaction = this.reprintSalesInfo.getText();
          printReceipt();
        }
      }
      else {
            if (reprintConfirm == 0)
            {
                return this.reprintSalesInfo.getText();
            }
      }
      
      this.lastTransaction = "";
    }
    else {
      JOptionPane.showConfirmDialog(this.rootPane, "Error reading sales file", "File error", 2, 2, this.msgIcon);
    }
    
    return "";
  }

  private String displayReceipt(Object item, Map<String, String[]> data)
  {
    StringBuilder saleData = new StringBuilder();
    int SaleItemsIndex = 5;
    
    String[] saleString = (String[])data.get(item.toString());
    double totalCost = 0.0D;

    for (int i = SaleItemsIndex; i < saleString.length - 1; i += 2)
    {
      if (i <= saleString.length)
      {
        double cost = ((Double)this.itemPriceByName.get(saleString[i].trim())).doubleValue() * Integer.parseInt(saleString[(i + 1)].trim());

        saleData.append(String.format("%10s%3s%10d%3s%8.2f", new Object[] { saleString[i].trim(), " x ", Integer.valueOf(Integer.parseInt(saleString[(i + 1)].trim())), "=", Double.valueOf(cost) }));
        saleData.append(System.lineSeparator());
        
        totalCost += cost;
      }

    }

    StringBuilder dash = new StringBuilder();

    for (int i = 0; i < 30; i++) {
      dash.append("-");
    }
    
    saleData.append(String.format("%28s", new Object[] { dash }));
    saleData.append(System.lineSeparator());
    saleData.append(String.format("%-20s %-5s %6.2f", new Object[] { "Total:", "", Double.valueOf(totalCost) }));
    saleData.append(System.lineSeparator());

    String payMethod = saleString[3].trim();
    String payAmount = saleString[2].trim();
    
    if (payMethod.contains("/")) {
        
        String payMethods[] = payMethod.split("/");
        String payAmounts[] = payAmount.split("/");
        double payTotal = 0.0;
        
        for (int i = 0; i < payMethods.length; i++) {
            
            payTotal = payTotal + Double.parseDouble(payAmounts[i].trim());
            
            if (payMethods[i].equals("1"))
              saleData.append(String.format("%-26s %-5s %-6.2f", new Object[] { "Debit: ", "", Double.valueOf(payAmounts[i]) }));
            else if (payMethods[i].equals("0"))
              saleData.append(String.format("%-18s %-5s %-6.2f", new Object[] { "Cash Tendered: ", "", Double.valueOf(payAmounts[i]) }));
            else {
              saleData.append(String.format("%-20s %-5s %-6.2f", new Object[] { "Gift Card: ", "", Double.valueOf(payAmounts[i]) }));
            }
            
            saleData.append(System.lineSeparator());
            
        }
        saleData.append(String.format("%-20s %-5s %-6.2f", new Object[] { "Change: ", "", Double.valueOf(payTotal) - Double.valueOf(totalCost) }));
    }
    else {
            if (payMethod.equals("1"))
              saleData.append(String.format("%-26s %-5s %-6.2f", new Object[] { "Debit: ", "", Double.valueOf(payAmount) }));
            else if (payMethod.equals("0"))
              saleData.append(String.format("%-18s %-5s %-6.2f", new Object[] { "Cash Tendered: ", "", Double.valueOf(payAmount) }));
            else {
              saleData.append(String.format("%-20s %-5s %-6.2f", new Object[] { "Gift Card: ", "", Double.valueOf(payAmount) }));
            }
            saleData.append(System.lineSeparator());
            saleData.append(String.format("%-20s %-5s %-6.2f", new Object[] { "Change: ", "", Double.valueOf(payAmount) - Double.valueOf(totalCost) }));
    }
    
    this.reprintSalesInfo.setText(saleData.toString());
    
    checkOpenWindows();
    
    return saleData.toString();
  }

  private void printReceipt()
  {
    POSDatabase database = new POSDatabase();
    String printerName = ((String)database.getSettings().get("printer")).trim();

    if (printerName.length() != 0 && printerName != null) {
      int prtRes = POSPrinter.PrintReceipt(((String)database.getSettings().get("icon")).trim(), this.lastTransaction, database.getReceiptMsg(), printerName);
      
      if (prtRes == POSPrinter.PRINT_ERROR) {
          this.receiptToggle.doClick();
      }
    }
    else {
      JOptionPane.showMessageDialog(this, "<html>There is no printer choosen in settings. Please select a printer  <br>in settings if you wish to print a receipt. For your convenience <br>receipt printing has been turned off.", "No printer specified.", 0, this.msgIcon);

      this.receiptToggle.doClick();
    }
  }

  private void cancelSaleActionPerformed(ActionEvent evt)
  {
    this.payMode = false;
    clearValues(4);
    toggleButtonLock("ops");
    this.calcResults.setText("");
    this.discountItem.setEnabled(true);
    this.cancelSale.setEnabled(false);
  }

  public boolean getPermission()
  {
      JPanel permissionWin = generateGenericPanel("Permission Panel");
      JPasswordField permissionPass = new JPasswordField();
      JLabel permissionInfo = makeHTMLLabels("Enter Administrator Password:", 200);
      
      permissionWin.add(permissionInfo);
      permissionWin.add(permissionPass);
      
      int chkPermission = JOptionPane.showConfirmDialog(this.rootPane, permissionWin, "Permission Required", -1);
      
      if (chkPermission == JOptionPane.YES_OPTION)
      {
          POSDatabase data = new POSDatabase();
          data.readCredentials();
          Map<String, String> creds = data.creds;
          String pass = new String(permissionPass.getPassword());
          
          if (creds.get("Admin").equals(pass))
          {
              return true;
          }    
          else
          {
              return false;
          }
      }    
      else
      {
          return false;
      }
  }        
  
  private void discountItemActionPerformed(ActionEvent evt)
  {
      
      boolean hasPermission = getPermission();

      if (hasPermission)
      {    
        if (this.subStep.length() != 0)
        {
          if ((!this.calcTotal.isEmpty()) && (!this.discountApplied))
          {
            if ((Integer.parseInt(this.subStep.toString().trim()) < 100) && (Integer.parseInt(this.subStep.toString().trim()) > 0))
            {
              double lastItemTotal = ((Double)this.calcTotal.get(this.calcTotal.size() - 1)).doubleValue();

              BigDecimal discountPercent = new BigDecimal(this.subStep.toString());

              if (discountPercent.doubleValue() > 1.0D) {
                discountPercent = discountPercent.divide(new BigDecimal(100));
              }
              BigDecimal reduceAmount = new BigDecimal(discountPercent.doubleValue() * lastItemTotal);
              BigDecimal newTotal = new BigDecimal(String.format("%.2f", new Object[] { new BigDecimal(lastItemTotal).subtract(reduceAmount).abs() }));

              newTotal = roundToProperCents(newTotal);

              this.calcTotal.set(this.calcTotal.size() - 1, Double.valueOf(newTotal.doubleValue()));

              updateDisplay(String.format("%20s%.2s%%%s%30s%.2f", new Object[] { "Discount @ ", discountPercent.multiply(new BigDecimal(100)), System.lineSeparator(), " New price = ", newTotal }));

              clearValues(1);
              this.discountApplied = true;
            }
            else {
              JOptionPane.showConfirmDialog(this.rootPane, "Please enter a valid percent between 0-100", "Incorrect Input", 0, 1, this.msgIcon);
            }
          }
          else
          {
            JOptionPane.showMessageDialog(this.printReciept, "Please enter an item first", "No item entered", 0, this.msgIcon);
            clearValues(2);
          }
        }
        else
          JOptionPane.showMessageDialog(this.printReciept, "Please enter a discount amount in decimal/percent.", "No discount entered", 0, this.msgIcon);
  
      }
      else
      {
          JOptionPane.showMessageDialog(this.printReciept, "Please get Administrator permission.", "Incorrect password", 0, this.msgIcon);
  
      }
  }
  
  private void discountTotalActionPerformed(ActionEvent evt)
  {
        boolean hasPermission = getPermission();
        
        if (hasPermission)
        {    
            if (this.payMode)
            {
              if ((Integer.parseInt(this.totalDiscountAmount.getText().trim()) < 100) && (Integer.parseInt(this.totalDiscountAmount.getText().trim()) > 0))
              {
                BigDecimal discountPercent = new BigDecimal(this.totalDiscountAmount.getText().trim());

                if (discountPercent.doubleValue() > 1.0D) {
                  discountPercent = discountPercent.divide(new BigDecimal(100));
                }
                BigDecimal reducePrice = new BigDecimal(discountPercent.doubleValue() * this.payTotal.doubleValue());
                BigDecimal newPayTotal = new BigDecimal(String.format("%.2f", new Object[] { this.payTotal.subtract(reducePrice) }));

                this.payTotal = roundToProperCents(newPayTotal);
                activeTotal = this.payTotal;
                
                updateDisplay(String.format("%20s%.2f%%%s%30s%6.2f", new Object[] { "Sale discount @ ", discountPercent.multiply(new BigDecimal(100)), System.lineSeparator(), " New total = ", this.payTotal }));
                this.totalInfo.setText(new StringBuilder().append("Discounted @ ").append(String.format("%.2f%%", new Object[] { discountPercent.multiply(new BigDecimal(100)) })).append(" new total to pay = ").append(String.format("%.2f", new Object[] { this.payTotal })).toString());

                this.totalDiscountApplied = true;
              }
              else {
                JOptionPane.showConfirmDialog(this.rootPane, "Please enter a valid percent between 0-100", "Incorrect Input", 0, 1, this.msgIcon);
              }
            }
            else
              JOptionPane.showConfirmDialog(this.rootPane, "Sale discount can only be performed after a total is calculated", "Incorrect Input", 0, 1, this.msgIcon);
  
        }

  }
  
  private BigDecimal roundToProperCents(BigDecimal num)
  {
    StringBuilder sNum = new StringBuilder(num.toString());

    String lastDigitAsString = new StringBuilder().append(sNum.charAt(sNum.length() - 1)).append("").toString();
    int lastDigit = Integer.parseInt(lastDigitAsString);

    if (lastDigit >= 8)
    {
      String newNums = new StringBuilder().append("").append(Integer.parseInt(String.format("%c", new Object[] { Character.valueOf(sNum.charAt(sNum.length() - 2)) })) + 1).append("").append(0).toString();

      sNum.replace(sNum.length() - 2, sNum.length(), newNums);
    }
    else if ((lastDigit <= 2) && (lastDigit > 0))
    {
      sNum.replace(sNum.length() - 1, sNum.length(), "0");
    }
    else if ((lastDigit > 2) && (lastDigit < 8) && (lastDigit != 0))
    {
      sNum.replace(sNum.length() - 1, sNum.length(), "5");
    }

    return new BigDecimal(sNum.toString());
  }

  private void calcDebCredActionPerformed(ActionEvent evt)
  {
    if ((this.payMode) && (this.cashTendered > 5000.0D) && (this.cashTendered < 0.0D))
    {
      JOptionPane.showMessageDialog(this.rootPane, "Please enter an amount between 0 and 5000", "Unexpected amount", 0, this.msgIcon);
      this.validTender = false;
    }
    else {
      this.validTender = true;
    }
    if ((this.payMode) && (this.validTender) && (this.totalConfirm != 2))
    {
      this.payType = 1;
      calcPayment();
    }
  }

  private void closePayScreen()
  {
    Window[] w = Window.getWindows();

    for (Window a : w)
    {
      if ((a instanceof JDialog))
      {
        JDialog j = (JDialog)a;

        if (j != null)
        {
          if ((j.getTitle().equals("Pay Screen")) && (!this.multiPayment))
            a.dispose();
          else if ((j.getTitle().equals("Pay Screen")) && (this.multiPayment))
            this.activePayScreen = j;
        }
      }
    }
  }
  
  ArrayList<Double> multiplePayment = new ArrayList();
  
  private void calcPayment()
  {
    closePayScreen();

    BigDecimal pay = new BigDecimal(String.format("%.2f", new Object[] { Double.valueOf(this.cashTendered) }));

    BigDecimal change = this.payTotal.subtract(pay);

    if (this.payType == 1)
      updateDisplay(String.format("%-26s %-5s %-6.2f", new Object[] { "Debit: ", "", pay }));
    else if (this.payType == 0)
      updateDisplay(String.format("%-18s %-5s %-6.2f", new Object[] { "Cash Tendered: ", "", pay }));
    else {
      updateDisplay(String.format("%-20s %-5s %-6.2f", new Object[] { "Gift Card: ", "", pay }));
    }
    if (this.multiPayment)
    {
      this.payTotal = change;
      activeTotal = change;
      multiplePayment.add(cashTendered); // if multiple payments are made record each of them and their pay type
      multiplePayment.add((double) payType);
      
      if (this.activePayScreen != null) {
        this.totalInfo.setText(new StringBuilder().append("  Total to pay = ").append(String.format("%.2f", new Object[] { this.payTotal })).toString());
      }
      this.multiPayment = false;
      this.cashTendered = 0.0D;
    }
    else
    {
      if (change.compareTo(BigDecimal.ZERO) < 0) {
        change = change.abs();
      }
      updateDisplay(String.format("%-24s %-5s %-6.2f", new Object[] { "Change: ", "", change }));

      this.lastTransaction = this.calcResults.getText();

      this.payMode = (!this.payMode);

      if (this.printerOn) {
        printReceipt();
      }
      writeSale("");

      this.cancelSale.setEnabled(false);

      //clearValues(4);
      toggleButtonLock("ops");
      this.discountItem.setEnabled(true);
      this.totalDiscountApplied = false;
      
      multiplePayment.clear();  // clear payments at end of transaction
      
      closePayScreen();
    }
    
    this.totalCashTendered.setText(""); // clear pay amount after each entry
  }

  private void calcQuantityActionPerformed(ActionEvent evt)
  {
    if ((!this.orderOfEntry.isEmpty()) && (((String)this.orderOfEntry.get(0)).equals("value")))
      calcButtonPress("x", "op");
    else
      JOptionPane.showMessageDialog(this.rootPane, "Please enter an amount or select a product first.", "Invalid entry", 0, this.msgIcon);
  }

  private void calcClearActionPerformed(ActionEvent evt)
  {
    if (this.saleVoided)
    {
      this.saleVoided = false;
      this.calcResults.setText("");
    }
    else {
      clearValues(2);
    }
  }

  private void calcEnterActionPerformed(ActionEvent evt)
  {
    double tmpRes = 0.0D;

    if (this.saleVoided)
    {
      this.currentSale.setLength(0);
      this.calcResults.setText("");
      this.saleVoided = false;
    }

    if ((this.payMode) && (this.cashTendered > 5000.0D) && (this.cashTendered < 0.0D))
    {
      JOptionPane.showMessageDialog(this.rootPane, "Please enter an amount between 0 and 5000", "Unexpected amount", 0, this.msgIcon);
      this.validTender = false;
    }
    else {
      this.validTender = true;
    }

    if ((this.payMode) && (this.validTender))
    {
      this.payType = 0;
      calcPayment();
    }
    else if ((this.subStep.length() != 0) && (!this.calcOps.isEmpty()))
    {
      if ((!this.subStep.toString().equals("+")) && (!this.subStep.toString().equals("*"))) {
          if (subStep.toString().lastIndexOf(" ") > 1) {
              subStep = new StringBuffer(subStep.substring(0, subStep.lastIndexOf(" ")));
          }
        this.calcNums.add(Double.valueOf(Double.parseDouble(this.subStep.toString())));
      }
      this.subStep.setLength(0);
    }

    if ((this.calcNums.size() > 1) && (!this.payMode))
    {
      double quantityValue = 0.0D;
      this.itemCt += 1;

      this.calcOps.add(this.calcOps.size(), "=");

      for (int i = 0; i < this.calcNums.size(); i++)
      {
        if (((String)this.calcOps.get(i)).equals("=")) {
          this.result = ((Double)this.calcNums.get(i)).doubleValue();
        }
        else {
          if (((String)this.calcOps.get(i)).equals("+"))
          {
            tmpRes = ((Double)this.calcNums.get(i)).doubleValue() + ((Double)this.calcNums.get(i + 1)).doubleValue();
          }
          else if (((String)this.calcOps.get(i)).equals("x"))
          {
            tmpRes = ((Double)this.calcNums.get(i)).doubleValue() * ((Double)this.calcNums.get(i + 1)).doubleValue();
          }

          if (((String)this.orderOfEntry.get(0)).equals("prod"))
            quantityValue = 1.0D;
          else {
            quantityValue = ((Double)this.calcNums.get(i)).doubleValue();
          }
          this.calcNums.remove(i + 1);
          this.calcNums.add(i + 1, Double.valueOf(tmpRes));
        }
      }

      this.calcTotal.add(Double.valueOf(this.result));
      this.discountApplied = false;

      updateDisplay(String.format("%-18s%-2s%-4.0f%-1s%6.2f", this.itemName, this.calcOps.get(0), Double.valueOf(quantityValue), "=", Double.valueOf(this.result) ));

      String[] tmpString = { this.itemName, String.format("%.0f", new Object[] { Double.valueOf(quantityValue) }) };
      this.itemList.add(tmpString);

      clearValues(1);
    }
  }

  private void calcAddActionPerformed(ActionEvent evt)
  {
  }

  private void calcZeroActionPerformed(ActionEvent evt)
  {
    calcButtonPress("0", "value");
  }

  private void calcDecimalActionPerformed(ActionEvent evt)
  {
    calcButtonPress(".", "value");
  }

  private void calcNineActionPerformed(ActionEvent evt)
  {
    calcButtonPress("9", "value");
  }

  private void calcEightActionPerformed(ActionEvent evt)
  {
    calcButtonPress("8", "value");
  }

  private void calcSevenActionPerformed(ActionEvent evt)
  {
    calcButtonPress("7", "value");
  }

  private void calcSixActionPerformed(ActionEvent evt)
  {
    calcButtonPress("6", "value");
  }

  private void calcFiveActionPerformed(ActionEvent evt)
  {
    calcButtonPress("5", "value");
  }

  private void calcFourActionPerformed(ActionEvent evt)
  {
    calcButtonPress("4", "value");
  }

  private void calcThreeActionPerformed(ActionEvent evt)
  {
    calcButtonPress("3", "value");
  }

  private void calcTwoActionPerformed(ActionEvent evt)
  {
    calcButtonPress("2", "value");
  }

  private void calcOneActionPerformed(ActionEvent evt) {
    calcButtonPress("1", "value");
  }

  private void voidLastSaleActionPerformed(ActionEvent evt)
  {
    JPanel inputPanel = new JPanel();
    JLabel voidUser = new JLabel("Please enter Administrator password:");
    JPasswordField voidPass = new JPasswordField();
    inputPanel.setLayout(new GridLayout(0, 1));
    inputPanel.add(voidUser);
    inputPanel.add(voidPass);

    int confirmVoid = JOptionPane.showConfirmDialog(this.rootPane, inputPanel, "Admin only option", 2, 2, this.msgIcon);

    if (confirmVoid == 0)
    {
      this.saleVoided = true;
      POSDatabase database = new POSDatabase();
      String password = (String)database.readCredentials().get("Admin");
      File salesFile = new File(POSSplash.sales);

      if (salesFile.length() != 0L)
      {
        if (password.equals(voidPass.getText().trim()))
        {
          Scanner read = null;
          try
          {
            read = new Scanner(salesFile);
          }
          catch (FileNotFoundException ex)
          {
            System.err.println(new StringBuilder().append("File not found error (POSMain 1669): ").append(ex).toString());
          }

          StringBuilder salesData = new StringBuilder();

          while (read.hasNext())
          {
            salesData.append(read.nextLine());
            salesData.append(System.lineSeparator());
          }

          salesData.delete(salesData.lastIndexOf(System.lineSeparator()), salesData.length());

          int stopIndex = salesData.lastIndexOf(System.lineSeparator()) + 1;

          if (stopIndex == -1) {
            stopIndex = 0;
          }
          StringBuilder voidedSale = new StringBuilder();

          for (int i = salesData.length() - 1; i >= stopIndex; i--)
          {
            voidedSale.append(salesData.charAt(i));
            salesData.deleteCharAt(i);
          }

          read.close();

          String data = salesData.toString();

          if (data.length() != 0)
            writeSale(data);
          else {
            writeSale(null);
          }
          voidedSale.reverse();
          String[] voidedSaleItems = voidedSale.toString().split(",");
          
          if (calcResults.getText().length() > 0)
          {    
              clearValues(2);
              calcResults.setText("");
          }
          
          updateDisplay(String.format("%34s", new Object[] { "--Sale Voided--" }));
          double completeVoidTotal = 0.0D;
          final int VOID_QUANTITY_POSITION = 1;

          for (int i = SALE_ITEM_START_INDEX; i < voidedSaleItems.length; i += 2)
          {
            if (!voidedSaleItems[i].trim().isEmpty())
            {
              String voidItemName = voidedSaleItems[i].trim();
              double price = ((Double)this.itemPriceByName.get(voidItemName)).doubleValue();
              double quantity = Double.parseDouble(voidedSaleItems[i+VOID_QUANTITY_POSITION].trim());
              double totalVoidSale = quantity * price;
              updateDisplay(String.format("%10s%3s%10.0f%3s%8.2f", voidItemName, "x", quantity, "=", totalVoidSale));
              completeVoidTotal += totalVoidSale;
            }
          }

          StringBuilder dash = new StringBuilder();

          for (int i = 0; i < 43; i++) {
            dash.append("-");
          }
          updateDisplay(String.format("%28s", new Object[] { dash }));
          //updateDisplay(String.format("%20s %.2f", new Object[] { "Total: ", Double.valueOf(completeVoidTotal) }));
          updateDisplay(String.format("%-24s %-5s %-6.2f", new Object[] { "Total: ", "", Double.valueOf(completeVoidTotal) }));
          updateDisplay(String.format("%34s", new Object[] { "--Sale Voided--" }));
        }
        else {
          JOptionPane.showMessageDialog(this.rootPane, "Incorrect password. Please contact your Administrator.", "Invalid password", 0, this.msgIcon);
        }
      }
      else JOptionPane.showMessageDialog(this.rootPane, "There are no sales records to void.", "Empty sales record", 0, this.msgIcon);
    }
  }

  private void errorCorrectActionPerformed(ActionEvent evt)
  {
    if (!this.payMode)
    {
      if (!calcTotal.isEmpty())
      {
        String[] currentItems = this.currentSale.toString().split(System.lineSeparator());
        double lastItemPrice = ((Double)this.calcTotal.get(this.calcTotal.size() - 1)).doubleValue();
        StringBuilder correctLastItem = new StringBuilder();
        ArrayList<String> correctedItems = new ArrayList();
        ArrayList<Boolean> notYetCorrected = new ArrayList();
        
        for (int i = 1; i < currentItems.length; i++)
        {
            
            correctLastItem = new StringBuilder(currentItems[(currentItems.length - i)]); 
            
            if (correctLastItem.toString().contains("-"))
            {
                correctedItems.add(correctLastItem.substring(0, correctLastItem.indexOf("=")).trim());    
            }    
            else 
            {    
                if (!correctedItems.isEmpty())
                {    
                    for (String cItems : correctedItems)
                    {
                        if (!correctLastItem.toString().contains(cItems))    
                            notYetCorrected.add(true);
                        else
                            notYetCorrected.add(false);
                    }    
                }
                else
                {
                    notYetCorrected.add(true);
                }
            }
            
            if (!notYetCorrected.contains(false) && notYetCorrected.contains(true))
            {
                notYetCorrected.clear();
                correctedItems.clear();
                
                if (calcTotal.isEmpty())
                {
                    clearValues(2);    
                }    

                break;
            }    
            else
            {
                notYetCorrected.clear();
            }
        }    
        
        correctLastItem.replace(correctLastItem.indexOf(String.format("%.2f", new Object[] { Double.valueOf(lastItemPrice) })), correctLastItem.length(), String.format("%.2f", new Object[] { Double.valueOf(lastItemPrice * -1.0D) }));

        updateDisplay(String.format(new StringBuilder().append(correctLastItem.toString()).toString(), new Object[0]));
        this.calcTotal.remove(this.calcTotal.size() - 1);
      }
      else
      {
        JOptionPane.showMessageDialog(this.rootPane, "No items present in transaction.", "No item to void", 0, this.msgIcon);
      }
    }
    else JOptionPane.showMessageDialog(this.rootPane, "Error correct cannot be performed after a total has been calculated.", "Invalid Input", 0, this.msgIcon);
  }

  private void logoutActionPerformed(ActionEvent evt)
  {
    new POSLogin().setVisible(true);
    setVisible(false);
    dispose();
  }

  private void toggleButtonLock(String group)
  {
    if (group.equals("ops"))
    {
      this.calcQuantity.setEnabled(!this.opsLock);
      this.opsLock = (!this.opsLock);
    }
  }

  private void getLogoImg()
  {
    POSDatabase database = new POSDatabase();
    database.getSettings();

    String path = ((String)database.settings.get("icon")).trim();

    StretchIcon icon = new StretchIcon(path, "Logo");

    this.mainLogo.setIcon(icon);
  }

  private void setupExtItems()
  {
    POSDatabase database = new POSDatabase();
    //productArea.setLayout(this.lay);
    ArrayList data = database.getExtLayout();
    //this.lay.preferredLayoutSize(this.productArea);
    //productArea.setPreferredSize(new Dimension(359, 390));
    //productArea.setSize(359, 390);
    Component tabsInPane[] = productArea.getComponents();
    
    
    for (int i = 0; i < data.size(); i++)
    {
      String name = ((Object[])data.get(i))[0].toString();
      JButton button;

      if (name.contains("/"))
      {
        String[] fullName = name.split("/");
        button = new JButton(new StringBuilder().append("<html>").append(fullName[0].trim()).append("<br>").append(fullName[1].trim()).toString());
      }
      else {
        button = new JButton(name.trim());

      }
      
      button.setSize(120, 80);
      button.setPreferredSize(new Dimension(120, 80));
      final String code = ((Object[])data.get(i))[1].toString().trim();
      String targetTab = ((Object[]) data.get(i))[2].toString().trim();
      JPanel targetTabPanel = null;
      
      for (Component c : tabsInPane)
      {
          JPanel tmpPanel = (JPanel) c;
          
          if (tmpPanel.getName() != null)
          {    
            if (tmpPanel.getName().trim().equals(targetTab))
            {
                targetTabPanel = tmpPanel;
            }
          }  
      }
      
      button.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          POSMain.this.calcButtonPress(code, "prod");
        }
      });

      targetTabPanel.add(button);
    }

    //this.extraSaleItemsTab.setName(itemName);
    this.productArea.revalidate();
    this.productArea.repaint();
  }

  private static String mkText(int width, String unit, String papsize, String layout)
  {
    String content1 = "<html><body style='background-color: grey; width: ";

    String content2 = "'><p>";

    String content3 = " <br>Sided <br>";
    String content4 = "</p></html>";
    return new StringBuilder().append(content1).append(width).append(unit).append(content2).append(layout).append(content3).append(papsize).append(content4).toString();
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BWDSCol;
    private javax.swing.JButton BWDSLeg;
    private javax.swing.JButton BWDSLet;
    private javax.swing.JButton BWDSTab;
    private javax.swing.JButton BWSSCol;
    private javax.swing.JButton BWSSLeg;
    private javax.swing.JButton BWSSLet;
    private javax.swing.JButton BWSSTab;
    private javax.swing.JButton COLDSCol;
    private javax.swing.JButton COLDSLeg;
    private javax.swing.JButton COLDSLet;
    private javax.swing.JButton COLDSTab;
    private javax.swing.JButton COLSSCol;
    private javax.swing.JButton COLSSLeg;
    private javax.swing.JButton COLSSLet;
    private javax.swing.JButton COLSSTab;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel bwPanel;
    private javax.swing.JButton calcClear;
    private javax.swing.JButton calcDebCred;
    private javax.swing.JButton calcDecimal;
    private javax.swing.JTextField calcDisplay;
    private javax.swing.JButton calcEight;
    private javax.swing.JButton calcEnter;
    private javax.swing.JButton calcFive;
    private javax.swing.JButton calcFour;
    private javax.swing.JButton calcNine;
    private javax.swing.JButton calcOne;
    private javax.swing.JPanel calcPad;
    private javax.swing.JPanel calcPanel;
    private javax.swing.JButton calcQuantity;
    private javax.swing.JTextArea calcResults;
    private javax.swing.JButton calcSeven;
    private javax.swing.JButton calcSix;
    private javax.swing.JLabel calcSubstep;
    private javax.swing.JButton calcThree;
    private javax.swing.JButton calcTwo;
    private javax.swing.JButton calcZero;
    private javax.swing.JButton cancelSale;
    private javax.swing.JButton closeDaySales;
    private javax.swing.JButton discountItem;
    private javax.swing.JButton discountTotal;
    private javax.swing.JButton errorCorrect;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jpanel3;
    private javax.swing.JButton logout;
    private javax.swing.JLabel mainLogo;
    private javax.swing.JButton printReciept;
    private javax.swing.JTabbedPane productArea;
    private javax.swing.JButton receiptToggle;
    private javax.swing.JButton returnSale;
    private javax.swing.JButton total;
    private javax.swing.JButton voidLastSale;
    // End of variables declaration//GEN-END:variables
}
