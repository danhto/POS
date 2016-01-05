/*     */ package mainForm;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.awt.Font;
/*     */ import java.awt.FontMetrics;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.print.PageFormat;
/*     */ import java.awt.print.Paper;
/*     */ import java.awt.print.Printable;
/*     */ import java.awt.print.PrinterException;
/*     */ import java.awt.print.PrinterJob;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TimeZone;
/*     */ import java.util.TreeMap;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.print.Doc;
/*     */ import javax.print.DocFlavor;
/*     */ import javax.print.DocFlavor.INPUT_STREAM;
/*     */ import javax.print.DocPrintJob;
/*     */ import javax.print.PrintException;
/*     */ import javax.print.PrintService;
/*     */ import javax.print.PrintServiceLookup;
/*     */ import javax.print.SimpleDoc;
/*     */ import javax.print.attribute.HashPrintRequestAttributeSet;
/*     */ import javax.print.attribute.PrintRequestAttributeSet;
/*     */ import javax.swing.ImageIcon;
/*     */ import javax.swing.JOptionPane;
/*     */ 
/*     */ public class POSPrinter
/*     */ {
/*     */   private static int DATE_START_POINT;
/*     */   private static int TRANSACTION_START_POINT;
/*     */   private static int RECEIPT_WIDTH;
/*     */   private static int LOGO_HEIGHT;
/*     */   private static int FONT_SIZE;
/*     */   private static int FONT_HEIGHT;
/*     */   private static int RECEIPT_HEIGHT;
/*     */   private static int RECEIPT_HEIGHT_THRESHOLD;
/*     */   private static int LOGO_WIDTH;
/*     */   private static int font;
/*     */   private static final int WINDOWS_OS = 1;
/*     */   private static final int MAC_OS = 0;
/*     */   private static int osIdentifier;
/*     */   private static ImageIcon msgIcon;
/*     */   private static final byte OPEN_DRAWER = 7;
/*     */   private static final double CM_PER_INCH = 0.393700787D;
/*     */   private static final double INCH_PER_CM = 2.545D;
/*     */   private static final double INCH_PER_MM = 25.449999999999999D;
/* 314 */   private static final String newLine = System.lineSeparator();
/*     */   private static final int SHIFT_SALE_INFO_INDEX = 3;
/*     */   private static final int YEAR_WORTH = 372;
/*     */   private static final int MONTH_WORTH = 31;
/*     */   private static final int MAX_YEAR_DIFFERENCE = 10;
/*     */ 
/*     */   private static void initPrintValues()
/*     */   {
/*  70 */     String os = System.getProperty("os.name").toLowerCase();
/*     */ 
/*  72 */     if (os.contains("mac"))
/*     */     {
/*  74 */       DATE_START_POINT = 350;
/*  75 */       TRANSACTION_START_POINT = 370;
/*  76 */       RECEIPT_WIDTH = 600;
/*  77 */       LOGO_HEIGHT = 300;
/*  78 */       FONT_SIZE = 35;
/*  79 */       FONT_HEIGHT = 42;
/*  80 */       RECEIPT_HEIGHT = 800;
/*  81 */       RECEIPT_HEIGHT_THRESHOLD = 500;
/*  82 */       LOGO_WIDTH = 530;
/*  83 */       font = 0;
/*  84 */       osIdentifier = 0;
/*     */     }
/*  86 */     else if (os.contains("win"))
/*     */     {
/*  88 */       DATE_START_POINT = 158;
/*  89 */       TRANSACTION_START_POINT = 157;
/*  90 */       RECEIPT_WIDTH = 200;
/*  91 */       LOGO_HEIGHT = 145;
/*  92 */       FONT_SIZE = 13;
/*  93 */       FONT_HEIGHT = 20;
/*  94 */       RECEIPT_HEIGHT = 300;
/*  95 */       RECEIPT_HEIGHT_THRESHOLD = 100;
/*  96 */       LOGO_WIDTH = 190;
/*  97 */       font = 0;
/*  98 */       osIdentifier = 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void PrintReceipt(String path, String transaction, String message, String printTo)
/*     */   {
/* 105 */     initPrintValues();
/*     */ 
/* 107 */     PrintService[] printers = PrinterJob.lookupPrintServices();
/* 108 */     PrintService service = null;
/*     */ 
/* 110 */     Calendar date = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);
/*     */ 
/* 112 */     int transactionSize = transaction.split(System.lineSeparator()).length * FONT_HEIGHT;
/* 113 */     int messageSize = message.split(System.lineSeparator()).length * FONT_HEIGHT;
/*     */ 
/* 115 */     if (transactionSize + TRANSACTION_START_POINT + messageSize >= RECEIPT_HEIGHT_THRESHOLD)
/* 116 */       transactionSize = RECEIPT_HEIGHT + transactionSize + messageSize;
/*     */     else {
/* 118 */       transactionSize = RECEIPT_HEIGHT;
/*     */     }
/* 120 */     for (PrintService printer : printers) {
/* 121 */       if (printer.getName().equals(printTo)) {
/* 122 */         service = printer;
/*     */       }
/*     */     }
/*     */ 
/* 126 */     PrinterJob job = PrinterJob.getPrinterJob();
/*     */ 
/* 131 */     PageFormat pageFormat = job.defaultPage();
/* 132 */     Paper paper = new Paper();
/* 133 */     paper.setSize(RECEIPT_WIDTH, transactionSize);
/* 134 */     paper.setImageableArea(0.0D, 0.0D, RECEIPT_WIDTH, transactionSize);
/* 135 */     pageFormat.setPaper(paper);
/*     */     try
/*     */     {
/* 139 */       job.setPrintService(service);
/*     */     }
/*     */     catch (PrinterException e1)
/*     */     {
/* 144 */       System.err.println(new StringBuilder().append("Error finding printer: ").append(e1).toString());
/*     */     }
/*     */ 
/* 147 */     job.setPrintable(new PrintTask(path, transaction, date.getTime().toString(), transactionSize, message), pageFormat);
/*     */     try
/*     */     {
/* 151 */       job.print();
/*     */     }
/*     */     catch (PrinterException e)
/*     */     {
/* 155 */       System.err.println(new StringBuilder().append("Error printing receipt: ").append(e).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public static double pixelsToCms(double pixels, double dpi)
/*     */   {
/* 175 */     return inchesToCms(pixels / dpi);
/*     */   }
/*     */ 
/*     */   public static double cmsToPixel(double cms, double dpi)
/*     */   {
/* 186 */     return cmToInches(cms) * dpi;
/*     */   }
/*     */ 
/*     */   public static double cmToInches(double cms)
/*     */   {
/* 196 */     return cms * 0.393700787D;
/*     */   }
/*     */ 
/*     */   public static double inchesToCms(double inch)
/*     */   {
/* 206 */     return inch * 2.545D;
/*     */   }
/*     */ 
/*     */   public static void printLogItem(String key, String goDate, String stopDate, String printTo)
/*     */   {
/* 322 */     POSDatabase database = new POSDatabase();
/* 323 */     database.readSalesByItem(goDate, stopDate);
/* 324 */     Map prices = database.readPrices();
/* 325 */     Calendar date = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);
/* 326 */     StringBuilder printString = new StringBuilder();
/* 327 */     String space = " ";
/*     */ 
/* 330 */     StringBuilder drawLine = new StringBuilder("");
/*     */ 
/* 332 */     for (int i = 0; i < 80; i++) {
/* 333 */       drawLine.append("-");
/*     */     }
/*     */ 
/* 336 */     printString.append(String.format("%52s%s%62s%s", new Object[] { "Sales item report by item", new StringBuilder().append(newLine).append(newLine).toString(), new StringBuilder().append("Report printed on ").append(date.getTime()).toString(), new StringBuilder().append(newLine).append(newLine).toString() }));
/* 337 */     printString.append(String.format("%56s%s%s", new Object[] { "Report printed for the following items: ", key, new StringBuilder().append(newLine).append(newLine).toString() }));
/* 338 */     printString.append(String.format("%56s%s - %s%s", new Object[] { "For sale period between: ", goDate, stopDate, new StringBuilder().append(newLine).append(newLine).toString() }));
/* 339 */     printString.append(String.format("%11s %15s %2s %12s %6s %6s %11s%s", new Object[] { space, "Product", space, "Quantity", space, "Amount ($)", space, newLine }));
/* 340 */     printString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/*     */ 
/* 342 */     Iterator priceKeys = prices.keySet().iterator();
/* 343 */     double quant = Double.parseDouble(((String)database.itemSalesFormat.get(key)).split(",")[0]);
/* 344 */     double prodPrice = 0.0D;
/*     */ 
/* 346 */     while (priceKeys.hasNext())
/*     */     {
/* 348 */       String priceKey = priceKeys.next().toString().trim();
/* 349 */       String[] innards = (String[])prices.get(priceKey);
/*     */ 
/* 351 */       if (innards[0].trim().equals(key)) {
/* 352 */         prodPrice = Double.parseDouble(innards[1]);
/*     */       }
/*     */     }
/* 355 */     printString.append(String.format("%11s %15s %6s %-12.2f %4s %-6.2f %11s%s", new Object[] { space, key, space, Double.valueOf(quant), space, Double.valueOf(quant * prodPrice), space, newLine }));
/*     */ 
/* 359 */     printString.append(newLine);
/* 360 */     printString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/*     */ 
/* 363 */     PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
/* 364 */     PrintService printer = null;
/*     */ 
/* 366 */     for (PrintService service : services)
/*     */     {
/* 368 */       if (service.getName().equals(printTo)) {
/* 369 */         printer = service;
/*     */       }
/*     */     }
/* 372 */     DocPrintJob job = null;
/*     */ 
/* 374 */     if (printer != null) {
/* 375 */       job = printer.createPrintJob();
/*     */     }
/* 377 */     InputStream in = new ByteArrayInputStream(printString.toString().getBytes());
/* 378 */     DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
/* 379 */     Doc doc = new SimpleDoc(in, flavor, null);
/*     */     try
/*     */     {
/* 384 */       if (job != null)
/* 385 */         job.print(doc, null);
/*     */     }
/*     */     catch (PrintException e)
/*     */     {
/* 389 */       System.err.println(new StringBuilder().append("Print error: ").append(e).toString());
/*     */     }
/*     */ 
/* 392 */     JOptionPane.showMessageDialog(null, "Print job sent...", "Printing", 0, msgIcon);
/*     */   }
/*     */ 
/*     */   public static void printLogUser(String key, String printTo)
/*     */   {
/* 399 */     POSDatabase database = new POSDatabase();
/* 400 */     database.readSalesByUser();
/* 401 */     Map prices = database.readPrices();
/* 402 */     Calendar date = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);
/* 403 */     StringBuilder printString = new StringBuilder();
/* 404 */     String space = " ";
/* 405 */     double totalAmount = 0.0D;
/* 406 */     StringBuilder noData = new StringBuilder("These users have no sales record: ");
/* 407 */     int origNoDataSize = noData.length();
/* 408 */     boolean printFlag = true;
/*     */ 
/* 411 */     StringBuilder drawLine = new StringBuilder("");
/*     */ 
/* 413 */     for (int i = 0; i < 80; i++) {
/* 414 */       drawLine.append("-");
/*     */     }
/*     */ 
/* 417 */     printString.append(String.format("%52s%s%60s%s", new Object[] { "Sales item report by user", new StringBuilder().append(newLine).append(newLine).toString(), new StringBuilder().append("Report printed on ").append(date.getTime()).toString(), new StringBuilder().append(newLine).append(newLine).toString() }));
/* 418 */     printString.append(String.format("%56s%s%s", new Object[] { "Report printed for the following users: ", key, new StringBuilder().append(newLine).append(newLine).toString() }));
/* 419 */     printString.append(String.format("%11s %15s %2s %12s %6s %6s %11s%s", new Object[] { space, "Product", space, "Quantity", space, "Amount ($)", space, newLine }));
/* 420 */     printString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/*     */ 
/* 422 */     int i = 0;
/* 423 */     int allPrint = 1;
/* 424 */     StringBuilder tmpString = new StringBuilder();
/*     */ 
/* 426 */     if (key.trim().equals("All"))
/*     */     {
/* 428 */       allPrint = database.readCredentials().size();
/*     */     }
/*     */ 
/* 431 */     while (i < allPrint)
/*     */     {
/* 433 */       printFlag = true;
/*     */ 
/* 436 */       if (allPrint > 1)
/*     */       {
/* 438 */         key = database.readCredentials().keySet().toArray()[i].toString().trim();
/* 439 */         totalAmount = 0.0D;
/*     */       }
/*     */ 
/* 443 */       Map byUser = null;
/* 444 */       int byUserAssignCt = 0;
/*     */ 
/* 448 */       while (byUser == null)
/*     */       {
/* 451 */         if ((i > allPrint) || (byUserAssignCt > allPrint) || (byUserAssignCt > database.userSalesFormat.size()))
/*     */         {
/* 453 */           printFlag = false;
/* 454 */           break;
/*     */         }
/*     */ 
/* 457 */         byUser = (Map)database.userSalesFormat.get(key);
/*     */ 
/* 459 */         if ((byUser == null) && (allPrint > 1))
/*     */         {
/* 461 */           i++;
/* 462 */           noData.append(key);
/* 463 */           noData.append(" ");
/* 464 */           key = database.readCredentials().keySet().toArray()[i].toString().trim();
/*     */         }
/*     */ 
/* 468 */         byUserAssignCt++;
/*     */       }
/*     */ 
/* 472 */       if (byUser != null)
/*     */       {
/* 475 */         Iterator prodKeys = byUser.keySet().iterator();
/*     */ 
/* 478 */         while ((prodKeys.hasNext()) && (printFlag))
/*     */         {
/* 480 */           Iterator priceKeys = prices.keySet().iterator();
/* 481 */           String prod = prodKeys.next().toString().trim();
/* 482 */           double quant = Double.parseDouble(byUser.get(prod).toString());
/* 483 */           double prodPrice = 0.0D;
/*     */ 
/* 488 */           while (priceKeys.hasNext())
/*     */           {
/* 490 */             String pKey = priceKeys.next().toString().trim();
/* 491 */             String[] innards = (String[])prices.get(pKey);
/*     */ 
/* 493 */             if (innards[0].trim().equals(prod)) {
/* 494 */               prodPrice = Double.parseDouble(innards[1].trim());
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 500 */           tmpString.append(String.format("%11s %15s %6s %-12.2f %4s %-6.2f %11s%s", new Object[] { space, prod, space, Double.valueOf(quant), space, Double.valueOf(quant * prodPrice), space, newLine }));
/*     */ 
/* 503 */           totalAmount += quant * prodPrice;
/*     */         }
/*     */       }
/*     */ 
/* 507 */       i++;
/*     */ 
/* 510 */       tmpString.append(newLine);
/* 511 */       tmpString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/* 512 */       tmpString.append(String.format("%20s Total sale amount for %s to date: %6.2f%s", new Object[] { space, key, Double.valueOf(totalAmount), newLine }));
/* 513 */       tmpString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/*     */ 
/* 516 */       printString.append(tmpString);
/*     */ 
/* 519 */       tmpString.setLength(0);
/*     */     }
/*     */ 
/* 522 */     if (noData.length() > origNoDataSize) {
/* 523 */       printString.append(noData);
/*     */     }
/*     */ 
/* 529 */     InputStream in = new ByteArrayInputStream(printString.toString().getBytes());
/* 530 */     DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
/*     */ 
/* 532 */     PrintService[] printers = PrintServiceLookup.lookupPrintServices(null, null);
/* 533 */     PrintService service = null;
/*     */ 
/* 536 */     for (PrintService print : printers)
/*     */     {
/* 538 */       if (print.getName().trim().equals(printTo)) {
/* 539 */         service = print;
/*     */       }
/*     */     }
/* 542 */     DocPrintJob job = null;
/*     */ 
/* 544 */     if (service != null) {
/* 545 */       job = service.createPrintJob();
/*     */     }
/* 547 */     PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
/*     */ 
/* 549 */     Doc doc = new SimpleDoc(in, flavor, null);
/*     */ 
/* 553 */     if (printFlag)
/*     */     {
/*     */       try
/*     */       {
/* 557 */         if (job != null)
/* 558 */           job.print(doc, pras);
/*     */       }
/*     */       catch (PrintException e)
/*     */       {
/* 562 */         System.err.println(new StringBuilder().append("Print error: ").append(e).toString());
/*     */       }
/*     */ 
/* 565 */       JOptionPane.showMessageDialog(null, "Print job sent...", "Printing.", 0);
/*     */     }
/*     */ 
/* 568 */     if (!printFlag)
/* 569 */       JOptionPane.showMessageDialog(null, "The selected user has no sales record.", "No data", 0, msgIcon);
/*     */   }
/*     */ 
/*     */   public static void printLogDate(String startDate, String endDate, String printTo)
/*     */   {
/* 575 */     POSDatabase database = new POSDatabase();
/* 576 */     database.readSalesByDate();
/* 577 */     Map prices = database.readPrices();
/* 578 */     Calendar date = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);
/* 579 */     StringBuilder printString = new StringBuilder();
/* 580 */     String space = " ";
/*     */ 
/* 583 */     StringBuilder drawLine = new StringBuilder("");
/*     */ 
/* 585 */     for (int i = 0; i < 80; i++) {
/* 586 */       drawLine.append("-");
/*     */     }
/* 588 */     List<String> goodDates = new ArrayList();
/* 589 */     Map byDate = database.dateSalesFormat;
/*     */ 
/* 592 */     printString.append(String.format("%52s%s%60s%s", new Object[] { "Sales item report by date", new StringBuilder().append(newLine).append(newLine).toString(), new StringBuilder().append("Report printed on ").append(date.getTime()).toString(), new StringBuilder().append(newLine).append(newLine).toString() }));
/* 593 */     printString.append(String.format("%34s %12s to %12s%s", new Object[] { "Time span of report: ", startDate, endDate, new StringBuilder().append(newLine).append(newLine).toString() }));
/* 594 */     printString.append(String.format("%11s %15s %2s %12s %6s %6s %11s%s", new Object[] { space, "Product", space, "Quantity", space, "Amount ($)", space, newLine }));
/* 595 */     printString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/*     */ 
/* 598 */     Iterator keyDates = byDate.keySet().iterator();
/*     */ 
/* 601 */     while (keyDates.hasNext())
/*     */     {
/* 603 */       String keyDate = keyDates.next().toString().trim();
/*     */ 
/* 605 */       if ((compDates(startDate, keyDate) == -1) && (compDates(endDate, keyDate) == 1))
/*     */       {
/* 607 */         goodDates.add(keyDate);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 612 */     List<Map> goodMaps = new ArrayList();
/*     */ 
/* 614 */     for (String goodDate : goodDates)
/*     */     {
/* 616 */       Map item = (Map)byDate.get(goodDate);
/* 617 */       goodMaps.add(item);
/*     */     }
/*     */ 
/* 621 */     Map newGoodMap = new TreeMap();
/*     */ 
/* 624 */     for (Map goodMap : goodMaps)
/*     */     {
/* 627 */       Iterator userKeys = goodMap.keySet().iterator();
/*     */ 
/* 630 */       while (userKeys.hasNext())
/*     */       {
/* 633 */         Map priceMap = (Map)goodMap.get(userKeys.next());
/*     */ 
/* 637 */         Iterator priceKeys = priceMap.keySet().iterator();
/*     */ 
/* 640 */         while (priceKeys.hasNext())
/*     */         {
/* 642 */           String pKey = priceKeys.next().toString().trim();
/* 643 */           double quantity = Double.parseDouble(priceMap.get(pKey).toString().trim());
/*     */ 
/* 646 */           if (newGoodMap.containsKey(pKey))
/*     */           {
/* 648 */             quantity += Double.parseDouble(((String)newGoodMap.get(pKey)).trim());
/* 649 */             newGoodMap.remove(pKey);
/* 650 */             newGoodMap.put(pKey, String.format("%.2f", new Object[] { Double.valueOf(quantity) }));
/*     */           }
/*     */           else {
/* 653 */             newGoodMap.put(pKey, String.format("%.2f", new Object[] { Double.valueOf(quantity) }));
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 659 */     Iterator newMapKeys = newGoodMap.keySet().iterator();
/* 660 */     double totalAmount = 0.0D;
/*     */ 
/* 663 */     while (newMapKeys.hasNext())
/*     */     {
/* 665 */       String prodKey = newMapKeys.next().toString().trim();
/* 666 */       double quantity = Double.parseDouble(((String)newGoodMap.get(prodKey)).trim());
/* 667 */       Iterator priceKeys = prices.keySet().iterator();
/* 668 */       double prodPrice = 0.0D;
/*     */ 
/* 671 */       while (priceKeys.hasNext())
/*     */       {
/* 673 */         String pKey = priceKeys.next().toString().trim();
/* 674 */         String[] innards = (String[])prices.get(pKey);
/*     */ 
/* 676 */         if (innards[0].trim().equals(prodKey)) {
/* 677 */           prodPrice = Double.parseDouble(innards[1].trim());
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 682 */       printString.append(String.format("%11s %15s %6s %-12.2f %4s %-6.2f %11s%s", new Object[] { space, prodKey, space, Double.valueOf(quantity), space, Double.valueOf(quantity * prodPrice), space, newLine }));
/*     */ 
/* 684 */       totalAmount += quantity * prodPrice;
/*     */     }
/*     */ 
/* 688 */     printString.append(newLine);
/* 689 */     printString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/* 690 */     printString.append(String.format("%16s Total sale amount for %s to %s: %6.2f%s", new Object[] { space, startDate, endDate, Double.valueOf(totalAmount), newLine }));
/* 691 */     printString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/*     */ 
/* 695 */     PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
/* 696 */     PrintService service = null;
/*     */ 
/* 698 */     for (PrintService printer : services)
/*     */     {
/* 700 */       if (printer.getName().trim().equals(printTo)) {
/* 701 */         service = printer;
/*     */       }
/*     */     }
/*     */ 
/* 705 */     InputStream in = new ByteArrayInputStream(printString.toString().getBytes());
/* 706 */     DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
/*     */ 
/* 708 */     Doc doc = new SimpleDoc(in, flavor, null);
/* 709 */     DocPrintJob job = null;
/*     */ 
/* 711 */     if (service != null) {
/* 712 */       job = service.createPrintJob();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 717 */       if (job != null)
/* 718 */         job.print(doc, null);
/*     */     }
/*     */     catch (PrintException e)
/*     */     {
/* 722 */       System.err.println(new StringBuilder().append("Printer error: ").append(e).toString());
/*     */     }
/*     */ 
/* 725 */     JOptionPane.showMessageDialog(null, "Print job sent...", "Printing", 0, msgIcon);
/*     */   }
/*     */ 
/*     */   public static void printLogShift(ArrayList<String> shiftInfo, String printTo)
/*     */   {
/* 738 */     Calendar date = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);
/* 739 */     StringBuilder printString = new StringBuilder();
/* 740 */     String space = " ";
/*     */ 
/* 743 */     StringBuilder drawLine = new StringBuilder("");
/*     */ 
/* 745 */     for (int i = 0; i < 80; i++) {
/* 746 */       drawLine.append("-");
/*     */     }
/*     */ 
/* 749 */     printString.append(String.format("%52s%s%62s%s", new Object[] { "Sales item report by Shift", new StringBuilder().append(newLine).append(newLine).toString(), new StringBuilder().append("Report printed on ").append(date.getTime()).toString(), new StringBuilder().append(newLine).append(newLine).toString() }));
/* 750 */     printString.append(String.format("%42s%s%s", new Object[] { "Date of shift: ", shiftInfo.get(0), new StringBuilder().append(newLine).append(newLine).toString() }));
/* 751 */     printString.append(String.format("%58s%s", new Object[] { new StringBuilder().append("Report printed for ").append((String)shiftInfo.get(1)).append(" using till code ").append((String)shiftInfo.get(2)).toString(), new StringBuilder().append(newLine).append(newLine).toString() }));
/* 752 */     printString.append(String.format("%11s %15s %2s %12s %6s %6s %11s%s", new Object[] { space, "Product", space, "Quantity", space, "Amount ($)", space, newLine }));
/* 753 */     printString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/*     */ 
/* 756 */     for (int i = 3; i < shiftInfo.size(); i += 3)
/*     */     {
/* 758 */       printString.append(String.format("%11s %15s %6s %-12d %4s %-6.2f %11s%s", new Object[] { space, shiftInfo.get(i), space, Integer.valueOf(Integer.parseInt((String)shiftInfo.get(i + 1))), space, Double.valueOf(Double.parseDouble((String)shiftInfo.get(i + 2))), space, newLine }));
/*     */     }
/*     */ 
/* 762 */     printString.append(newLine);
/* 763 */     printString.append(String.format("%56s%s", new Object[] { drawLine, newLine }));
/*     */ 
/* 766 */     PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
/* 767 */     PrintService printer = null;
/*     */ 
/* 769 */     for (PrintService service : services)
/*     */     {
/* 771 */       if (service.getName().equals(printTo)) {
/* 772 */         printer = service;
/*     */       }
/*     */     }
/* 775 */     DocPrintJob job = null;
/*     */ 
/* 777 */     if (printer != null) {
/* 778 */       job = printer.createPrintJob();
/*     */     }
/* 780 */     InputStream in = new ByteArrayInputStream(printString.toString().getBytes());
/* 781 */     DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
/* 782 */     Doc doc = new SimpleDoc(in, flavor, null);
/*     */     try
/*     */     {
/* 787 */       if (job != null)
/* 788 */         job.print(doc, null);
/*     */     }
/*     */     catch (PrintException e)
/*     */     {
/* 792 */       System.err.println(new StringBuilder().append("Print error: ").append(e).toString());
/*     */     }
/*     */ 
/* 795 */     JOptionPane.showMessageDialog(null, "Print job sent...", "Printing", 0, msgIcon);
/*     */   }
/*     */ 
/*     */   public static int compDates(String date1, String date2)
/*     */   {
/* 808 */     Calendar date = Calendar.getInstance(TimeZone.getTimeZone("EST"), Locale.CANADA);
/* 809 */     int yearDiff1 = 10 - (date.get(1) - Integer.parseInt(date1.split("/")[2]));
/* 810 */     int yearDiff2 = 10 - (date.get(1) - Integer.parseInt(date2.split("/")[2]));
/*     */ 
/* 812 */     int value1 = Integer.parseInt(date1.split("/")[0]) * 31 + Integer.parseInt(date1.split("/")[1]) + yearDiff1 * 372;
/*     */ 
/* 816 */     int value2 = Integer.parseInt(date2.split("/")[0]) * 31 + Integer.parseInt(date2.split("/")[1]) + yearDiff2 * 372;
/*     */ 
/* 820 */     if (value1 > value2)
/* 821 */       return 1;
/* 822 */     if (value1 < value2) {
/* 823 */       return -1;
/*     */     }
/* 825 */     return 0;
/*     */   }
/*     */ 
/*     */   public static class PrintTask
/*     */     implements Printable
/*     */   {
/*     */     private BufferedImage img;
/*     */ 
/*     */     public PrintTask(String logoPath, String transaction, String date, int height, String message)
/*     */     {
/* 217 */       this.img = new BufferedImage(POSPrinter.RECEIPT_WIDTH, height, 2);
/* 218 */       Graphics2D g2d = this.img.createGraphics();
/*     */       try
/*     */       {
/* 223 */         if (logoPath.length() == 0)
/*     */         {
/* 225 */           POSDatabase database = new POSDatabase();
/*     */ 
/* 227 */           g2d.setColor(Color.ORANGE);
/* 228 */           g2d.draw3DRect(POSPrinter.LOGO_WIDTH / 4, POSPrinter.LOGO_HEIGHT / 4, POSPrinter.LOGO_WIDTH / 2, POSPrinter.LOGO_HEIGHT / 2, true);
/* 229 */           g2d.fill3DRect(POSPrinter.LOGO_WIDTH / 4, POSPrinter.LOGO_HEIGHT / 4, POSPrinter.LOGO_WIDTH / 2, POSPrinter.LOGO_HEIGHT / 2, true);
/* 230 */           g2d.setColor(Color.BLACK);
/* 231 */           g2d.setFont(new Font("Arial Black", 0, 24));
/* 232 */           g2d.drawString((String)database.getSettings().get("title"), POSPrinter.LOGO_WIDTH / 4 + 10, POSPrinter.LOGO_HEIGHT / 2);
/*     */         }
/*     */         else {
/* 235 */           g2d.drawImage(ImageIO.read(new File(logoPath)), 0, 0, POSPrinter.LOGO_WIDTH, POSPrinter.LOGO_HEIGHT, null);
/*     */         }
/*     */       }
/*     */       catch (IOException e) {
/* 239 */         System.err.println("Error opening logo file for receipt: " + e);
/*     */       }
/*     */ 
/* 243 */       g2d.setFont(new Font(null, POSPrinter.font, POSPrinter.FONT_SIZE));
/* 244 */       g2d.setColor(Color.BLACK);
/* 245 */       g2d.drawString(date, 0, POSPrinter.DATE_START_POINT);
/*     */ 
/* 248 */       POSDatabase database = new POSDatabase();
/* 249 */       String title = String.format("%25s", new Object[] { ((String)database.getSettings().get("title")).trim() });
/*     */ 
/* 251 */       g2d.drawString(title, 0, POSPrinter.DATE_START_POINT + g2d.getFontMetrics().getHeight());
/*     */ 
/* 254 */       int transactionNewLinePoint = POSPrinter.TRANSACTION_START_POINT + g2d.getFontMetrics().getHeight();
/*     */ 
/* 256 */       for (String line : transaction.split(System.lineSeparator()))
/*     */       {
/* 258 */         g2d.drawString(line, 0, transactionNewLinePoint += g2d.getFontMetrics().getHeight());
/*     */       }
/*     */ 
/* 262 */       int messageNewLinePoint = transactionNewLinePoint + POSPrinter.FONT_HEIGHT;
/* 263 */       String fixedMsg = message.replace(System.lineSeparator() + System.lineSeparator(), System.lineSeparator());
/*     */ 
/* 265 */       for (String line : fixedMsg.split(System.lineSeparator()))
/*     */       {
/* 268 */         g2d.drawString(line, 0, messageNewLinePoint += g2d.getFontMetrics().getHeight());
/*     */       }
/*     */ 
/* 271 */       g2d.dispose();
/*     */     }
/*     */ 
/*     */     public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
/*     */       throws PrinterException
/*     */     {
/* 277 */       int result = 1;
/*     */ 
/* 279 */       if (pageIndex < 1)
/*     */       {
/* 281 */         Graphics2D g2d = (Graphics2D)graphics;
/* 282 */         double width = pageFormat.getImageableWidth();
/* 283 */         double height = pageFormat.getImageableHeight();
/*     */ 
/* 285 */         g2d.translate((int)pageFormat.getImageableX(), (int)pageFormat.getImageableY());
/*     */ 
/* 288 */         double x = 0.0D;
/* 289 */         double y = 0.0D;
/*     */ 
/* 291 */         if (POSPrinter.osIdentifier == 0)
/*     */         {
/* 293 */           x = POSPrinter.cmsToPixel(1.0D, 72.0D);
/* 294 */           y = POSPrinter.cmsToPixel(1.0D, 72.0D);
/*     */         }
/* 297 */         else if (POSPrinter.osIdentifier == 1)
/*     */         {
/* 299 */           x = 5.0D;
/* 300 */           y = 5.0D;
/*     */         }
/*     */ 
/* 303 */         g2d.drawRect(0, 0, (int)width - 1, (int)height - 1);
/* 304 */         g2d.drawImage(this.img, (int)x, (int)y, null);
/*     */ 
/* 306 */         result = 0;
/*     */       }
/*     */ 
/* 309 */       return result;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/clubs/Downloads/YFSPOS.jar
 * Qualified Name:     mainForm.POSPrinter
 * JD-Core Version:    0.6.2
 */