/*     */ package mainForm;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Graphics;
/*     */ import java.io.PrintStream;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.TimeZone;
/*     */ import javax.print.PrintService;
/*     */ import javax.print.PrintServiceLookup;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextArea;
/*     */ 
/*     */ public class POSLoadSettings
/*     */ {
/*  24 */   public static JPanel sItemPanel = new JPanel();
/*     */   public static Color wColPreview;
/*  26 */   public static JComboBox lItemList = new JComboBox();
/*  27 */   public static JComboBox pList = new JComboBox();
/*  28 */   public static JLabel dPrinter = new JLabel();
/*  29 */   public static JTextArea rMessage = new JTextArea();
/*  30 */   public static JComboBox yearList = new JComboBox();
/*  31 */   public static JComboBox tList = new JComboBox();
/*  32 */   private static final Long MAX_LOAD_TIME = Long.valueOf(2700L);
/*  33 */   private int firstLoad = 0;
/*     */ 
/*     */   public POSLoadSettings()
/*     */   {
/*  37 */     JFrame win = new JFrame();
/*  38 */     JLabel label = new JLabel();
/*  39 */     label.setIcon(new ImageIcon(getClass().getResource("/posloader.gif")));
/*  40 */     win.getContentPane().add(label);
/*     */ 
/*  42 */     win.pack();
/*  43 */     win.setVisible(true);
/*     */ 
/*  45 */     Long loadScreenStart = Long.valueOf(System.currentTimeMillis());
/*     */ 
/*  47 */     Graphics g = label.getGraphics();
/*  48 */     ImageIcon imger = new ImageIcon(getClass().getResource("/posloader.gif"));
/*     */ 
/*  50 */     g.drawImage(imger.getImage(), 0, 0, Color.yellow, null);
/*     */ 
/*  52 */     g.setColor(Color.CYAN);
/*  53 */     g.drawRect(0, imger.getIconHeight() - 50, imger.getIconWidth() - 1, 49);
/*     */ 
/*  56 */     POSLogin.centerWindow(win);
/*     */ 
/*  58 */     int count = 0;
/*     */ 
/*  63 */     POSDatabase database = new POSDatabase();
/*     */ 
/*  65 */     String color = ((String)database.getSettings().get("color")).trim();
/*     */     Color pick;

/*  68 */     if (color.length() == 0) {
/*  69 */       pick = Color.LIGHT_GRAY;
/*     */     }
/*     */     else {
/*  72 */       String[] RGB = color.split("/");
/*  73 */       pick = new Color(Integer.parseInt(RGB[0]), Integer.parseInt(RGB[1]), Integer.parseInt(RGB[2]));
/*     */     }
/*     */ 
/*  77 */     wColPreview = pick;
/*     */ 
/*  79 */     count++;
/*  80 */     g.fillRect(0, imger.getIconHeight() - 50, count * 50, 49);
/*     */ 
/*  86 */     List<String[]> items = database.getTableData();
/*     */ 
/*  88 */     for (String[] item : items) {
/*  89 */       lItemList.addItem(item[0]);
/*     */     }
/*  91 */     count++;
/*  92 */     g.fillRect(0, imger.getIconHeight() - 50, count * 50, 49);
/*     */ 
/*  98 */     String tabsString = (String)database.settings.get("tabs");
/*     */ 
/* 100 */     for (String tab : tabsString.split("/")) {
/* 101 */       tList.addItem(tab.trim());
/*     */     }
/*     */ 
/* 108 */     PrintService[] printer = PrintServiceLookup.lookupPrintServices(null, null);
/*     */ 
/* 110 */     for (PrintService printer1 : printer)
/*     */     {
/* 112 */       pList.addItem(printer1.getName());
/*     */     }
/*     */ 
/* 115 */     count++;
/* 116 */     g.fillRect(0, imger.getIconHeight() - 50, count * 50, 49);
/*     */ 
/* 122 */     String tmp = database.getReceiptMsg();
/* 123 */     tmp = tmp.replace(System.lineSeparator(), "");
/*     */ 
/* 125 */     rMessage.setText(tmp);
/*     */ 
/* 127 */     count++;
/* 128 */     g.fillRect(0, imger.getIconHeight() - 50, count * 50, 49);
/*     */ 
/* 134 */     Calendar currentDate = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.ENGLISH);
/*     */ 
/* 136 */     int year = currentDate.get(1);
/*     */ 
/* 138 */     for (int i = 0; i < 3; i++) {
/* 139 */       yearList.addItem(Integer.valueOf(year - i));
/*     */     }
/* 141 */     count++;
/* 142 */     g.fillRect(0, imger.getIconHeight() - 50, count * 50, 49);
/*     */ 
/* 144 */     Long duration = Long.valueOf(System.currentTimeMillis() - loadScreenStart.longValue());
/* 145 */     duration = Long.valueOf(MAX_LOAD_TIME.longValue() - duration.longValue());
/*     */ 
/* 147 */     if (duration.longValue() > 0L)
/*     */     {
/*     */       try
/*     */       {
/* 151 */         Thread.sleep(duration.longValue());
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/* 155 */         System.err.println("Thread sleep error POSLoadSettings (140): " + e);
/*     */       }
/*     */     }
/*     */ 
/* 159 */     win.dispose();
/*     */ 
/* 161 */     EventQueue.invokeLater(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/* 166 */         new POSSettings(POSLoadSettings.this.firstLoad).setVisible(true);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/clubs/Downloads/YFSPOS.jar
 * Qualified Name:     mainForm.POSLoadSettings
 * JD-Core Version:    0.6.2
 */