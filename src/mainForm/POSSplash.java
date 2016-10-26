/*     */ package mainForm;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Container;
/*     */ import java.awt.EventQueue;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.Calendar;
/*     */ import java.util.Locale;
/*     */ import java.util.Scanner;
/*     */ import java.util.TimeZone;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JWindow;
/*     */ 
/*     */ public class POSSplash extends JPanel
/*     */ {
/*     */   private static Calendar date;
/*     */   private static long startTime;
/*     */   private static final long MAX_TIME = 2700L;
/*     */   public static final int LOAD_LOGIN = 0;
/*     */   public static final int LOAD_SETTINGS = 1;
/*     */   public static JWindow win;
/*     */   public static URL imgAddSplash;
/*     */   private final ImageIcon img;
/*  32 */   public static int cX = 0;
/*  33 */   public static int cY = 0;
/*  34 */   private final int firstLoad = 0;
/*  35 */   private static int OS = 0;
/*     */   private static final int OS_MAC = 0;
/*     */   private static final int OS_WIN = 1;
/*  38 */   public static boolean logLimit = false;
/*     */ 
/* 125 */   public static String settings = "";
/* 126 */   public static String prices = "";
/* 127 */   public static String layout = "";
/* 128 */   public static String receipt = "";
/* 129 */   public static String sales = "";
/* 130 */   public static String creds = "";
/*     */ 
/* 136 */   private static final String defaultPrices = "B&W Let SS, bwletss, 0.05" + System.lineSeparator() + "B&W Let DS, bwletds, 0.05" + System.lineSeparator() + "Col Let SS, cletss, 0.1" + System.lineSeparator() + "Col Let DS, cletds, 0.2" + System.lineSeparator() + "B&W Leg SS, bwlegss, 0.05" + System.lineSeparator() + "B&W Leg DS, bwlegds, 0.1" + System.lineSeparator() + "Col Leg SS, clegss, 0.1" + System.lineSeparator() + "Col Leg DS, clegds, 0.2" + System.lineSeparator() + "B&W Tab SS, bwtabss, 0.15" + System.lineSeparator() + "B&W Tab DS, bwtabds, 0.2" + System.lineSeparator() + "Col Tab SS, ctabss, 0.15" + System.lineSeparator() + "Col Tab DS, ctabds, 0.25" + System.lineSeparator() + "B&W Col SS, bwcolss, 0.1" + System.lineSeparator() + "B&W Col DS, bwcolds, 0.1" + System.lineSeparator() + "Col Col SS, ccolss, 0.15" + System.lineSeparator() + "Col Col DS, ccolds, 0.2" + System.lineSeparator();
/*     */ 
/*     */   public POSSplash()
/*     */   {
/*  48 */     date = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.ENGLISH);
/*     */ 
/*  50 */     imgAddSplash = getClass().getResource("/posloader.gif");
/*  51 */     this.img = new ImageIcon(imgAddSplash);
/*     */ 
/*  53 */     startTime = date.getTimeInMillis();
/*  54 */     setSize(300, 300);
/*  55 */     win = new JWindow();
/*  56 */     win.setSize(300, 300);
/*  57 */     win.getContentPane().setBackground(new Color(0, 255, 0, 0));
/*  58 */     win.getContentPane().add(this);
/*  59 */     POSLogin.centerWindow(win);
/*  60 */     win.setVisible(true);
/*     */ 
/*  62 */     cX = win.getX();
/*  63 */     cY = win.getY();
/*     */ 
/*  65 */     String opSys = System.getProperty("os.name").toLowerCase();
/*     */ 
/*  67 */     if (opSys.contains("mac"))
/*  68 */       OS = 0;
/*     */     else
/*  70 */       OS = 1;
/*     */   }
/*     */ 
/*     */   public void runSplash(int flag)
/*     */   {
/*  77 */     if (flag == 0) {
/*  78 */       firstRunCheck();
/*     */     }
/*  80 */     long currTime = date.getTimeInMillis();
/*     */     try
/*     */     {
/*  84 */       Thread.sleep(2700L - (currTime - startTime));
/*     */     }
/*     */     catch (InterruptedException ex)
/*     */     {
/*  88 */       System.err.println("Thread sleep error POSSplash (75): " + ex);
/*     */     }
/*     */ 
/*  91 */     win.dispose();
/*     */ 
/*  93 */     if (flag == 0)
/*     */     {
/*  95 */       EventQueue.invokeLater(new Runnable()
/*     */       {
/*     */         public void run()
/*     */         {
/* 100 */           new POSLogin().setVisible(true);
/*     */         }
/*     */       });
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void paintComponent(Graphics g)
/*     */   {
/* 116 */     super.paintComponents(g);
/* 117 */     Graphics2D g2 = (Graphics2D)g;
/*     */ 
/* 119 */     g2.drawImage(this.img.getImage(), null, this);
/*     */ 
/* 121 */     g2.dispose();
/*     */   }
/*     */ 
/*     */   private static void firstRunCheck()
/*     */   {
/* 160 */     String[] fileNames = { "settings.txt", "prices.txt", "layout.txt", "receiptMsg.txt", "sales.txt", "credentials.txt" };
/*     */     try
/*     */     {
/* 165 */       for (String fname : fileNames)
/*     */       {
/* 167 */         File file = new File(fname);
/*     */ 
/* 176 */         if (!file.exists()) {
/* 177 */           file.createNewFile();
/*     */         }
/* 179 */         if (fname.equals("settings.txt"))
/* 180 */           settings = file.getAbsolutePath();
/* 181 */         if (fname.equals("prices.txt"))
/* 182 */           prices = file.getAbsolutePath();
/* 183 */         if (fname.equals("layout.txt"))
/* 184 */           layout = file.getAbsolutePath();
/* 185 */         if (fname.equals("receiptMsg.txt"))
/* 186 */           receipt = file.getAbsolutePath();
/* 187 */         if (fname.equals("sales.txt"))
/* 188 */           sales = file.getAbsolutePath();
/* 189 */         if (fname.equals("credentials.txt")) {
/* 190 */           creds = file.getAbsolutePath();
/*     */         }
/* 192 */         if ((file.getName().equals("credentials.txt")) && (file.length() == 0L))
/*     */         {
/* 194 */           BufferedWriter br = new BufferedWriter(new FileWriter(file));
/*     */ 
/* 196 */           String username = PassCoder.encryptText("Admin");
/* 197 */           String password = PassCoder.encryptText("12345");
/*     */ 
/* 199 */           br.write(username + ", " + password + System.lineSeparator());
/*     */ 
/* 201 */           br.close();
/*     */         }
/*     */ 
/* 204 */         if ((file.getName().equals("prices.txt")) && (file.length() == 0L) && false)
/*     */         {
/* 206 */           BufferedWriter br = new BufferedWriter(new FileWriter(file));
/*     */ 
/* 208 */           br.write(defaultPrices);
/*     */ 
/* 210 */           br.close();
/*     */         }
/*     */ 
/* 213 */         if ((file.getName().equals("settings.txt")) && (file.length() == 0L))
/*     */         {
/* 215 */           BufferedWriter br = new BufferedWriter(new FileWriter(file));
/* 216 */           String gray = Color.LIGHT_GRAY.getRed() + "/" + Color.LIGHT_GRAY.getGreen() + "/" + Color.LIGHT_GRAY.getBlue();
/*     */ 
/* 218 */           br.write("icon, " + System.lineSeparator());
/* 219 */           br.write("printer, " + System.lineSeparator());
/* 220 */           br.write("color, " + gray + System.lineSeparator());
/* 221 */           br.write("title, " + System.lineSeparator());
/* 222 */           br.write("tillCode, 0" + System.lineSeparator());
/* 223 */           br.write("tabs, Default Tab" + System.lineSeparator());
/*     */ 
/* 225 */           br.flush();
/* 226 */           br.close();
/*     */         }
/*     */ 
/* 231 */         if ((file.getName().equals("sales.txt")) && (file.length() > 200L))
/*     */         {
/* 233 */           Scanner read = new Scanner(file);
/*     */ 
/* 235 */           int lineCt = 0;
/* 236 */           String firstDate = "";
/* 237 */           String lastDate = "";
/*     */ 
/* 239 */           while (read.hasNextLine())
/*     */           {
/* 241 */             if (lineCt == 0) {
/* 242 */               firstDate = read.nextLine().split(",")[1];
/*     */             }
/*     */ 
/* 245 */             lastDate = read.nextLine().split(",")[1];
/*     */ 
/* 247 */             lineCt++;
/*     */           }
/*     */ 
/* 251 */           int firstDateYr = Integer.parseInt(firstDate.split("/")[2].trim()) * 372;
/* 252 */           int lastDateYr = Integer.parseInt(lastDate.split("/")[2].trim()) * 372;
/* 253 */           int firstDateMonth = Integer.parseInt(firstDate.split("/")[0].trim()) * 31;
/* 254 */           int lastDateMonth = Integer.parseInt(lastDate.split("/")[0].trim()) * 31;
/*     */ 
/* 256 */           if (Math.abs(firstDateYr + firstDateMonth - (lastDateMonth + lastDateYr)) >= 372) {
/* 257 */             logLimit = true;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 267 */       System.err.println("File error: " + e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/* 275 */     new POSSplash().runSplash(0);
/*     */   }
/*     */ }

/* Location:           /Users/clubs/Downloads/YFSPOS.jar
 * Qualified Name:     mainForm.POSSplash
 * JD-Core Version:    0.6.2
 */