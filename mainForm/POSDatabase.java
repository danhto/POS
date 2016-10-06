/*     */ package mainForm;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Scanner;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class POSDatabase
/*     */ {
/*     */   Map<String, String[]> prices;
/*     */   Map<String, String> creds;
/*     */   List<String[]> tableData;
/*     */   ArrayList<Object[]> extItemLayout;
/*     */   Map<String, String> settings;
/*     */   Map<String, Map<String, String>> userSalesFormat;
/*     */   Map<String, Map<String, Map<String, String>>> dateSalesFormat;
/*     */   Map<String, String> itemSalesFormat;
/*     */   List<String> shiftSalesFormat;
/*     */   public static final int SALE_ITEM_START_INDEX = 5;
/* 287 */   private final int SALE_DATE_INDEX = 1;
/*     */ 
/* 560 */   private final int SALE_TILL_INDEX = 4;
/* 561 */   private final int SALE_USER_INDEX = 0;
/*     */ 
/*     */   public POSDatabase()
/*     */   {
/*  35 */     this.prices = new TreeMap();
/*  36 */     this.creds = new TreeMap();
/*  37 */     this.tableData = new ArrayList();
/*  38 */     this.extItemLayout = new ArrayList();
/*  39 */     this.settings = getSettings();
/*  40 */     this.userSalesFormat = new TreeMap();
/*  41 */     this.dateSalesFormat = new TreeMap();
/*  42 */     this.itemSalesFormat = new TreeMap();
/*     */   }
/*     */ 
/*     */   public Map<String, String[]> readPrices()
/*     */   {
/*     */     try
/*     */     {
/*  59 */       FileReader fin = new FileReader(POSSplash.prices);
/*     */ 
/*  62 */       Scanner read = new Scanner(fin);
/*     */ 
/*  64 */       while (read.hasNext())
/*     */       {
/*  66 */         String line = read.nextLine();
/*     */ 
/*  69 */         String[] words = line.split(",");
/*     */ 
/*  72 */         if (!words[0].trim().equals("Product"))
/*     */         {
/*  74 */           String[] innards = { words[0], words[2] };
/*  75 */           this.prices.put(words[1].trim(), innards);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  80 */       fin.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/*  87 */       System.err.println("Unable to read from file");
/*  88 */       System.exit(-1);
/*     */     }
/*     */ 
/*  91 */     return this.prices;
/*     */   }
/*     */ 
/*     */   public Map<String, String> readCredentials()
/*     */   {
/*     */     try
/*     */     {
/* 108 */       FileReader fin = new FileReader(POSSplash.creds);
/*     */ 
/* 111 */       Scanner read = new Scanner(fin);
/*     */ 
/* 113 */       while (read.hasNext())
/*     */       {
/* 115 */         String line = read.nextLine();
/*     */ 
/* 118 */         String[] words = line.split(",");
/*     */ 
/* 121 */         if (!words[0].trim().equals("Username"))
/*     */         {
/* 123 */           String username = PassCoder.decryptText(words[0].trim()).trim();
/* 124 */           String password = PassCoder.decryptText(words[1].trim()).trim();
/*     */ 
/* 127 */           this.creds.put(username, password);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 132 */       fin.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 139 */       System.err.println("Unable to read from file");
/* 140 */       System.exit(-1);
/*     */     }
/*     */ 
/* 143 */     return this.creds;
/*     */   }
/*     */ 
/*     */   public List<String[]> getTableData()
/*     */   {
/*     */     try
/*     */     {
/* 155 */       FileReader fin = new FileReader(POSSplash.prices);
/*     */ 
/* 157 */       Scanner reader = new Scanner(fin);
/*     */ 
/* 161 */       while (reader.hasNext())
/*     */       {
/* 164 */         String line = reader.nextLine();
/*     */ 
/* 166 */         String[] row = line.split(",");
/*     */ 
/* 168 */         this.tableData.add(row);
/*     */       }
/*     */ 
/* 172 */       fin.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 177 */       System.err.println("File Not Found");
/*     */     }
/*     */ 
/* 180 */     return this.tableData;
/*     */   }
/*     */ 
/*     */   public ArrayList<Object[]> getExtLayout()
/*     */   {
/*     */     try
/*     */     {
/* 196 */       FileReader fin = new FileReader(POSSplash.layout);
/*     */ 
/* 198 */       Scanner reader = new Scanner(fin);
/*     */ 
/* 201 */       while (reader.hasNext())
/*     */       {
/* 203 */         String line = reader.nextLine();
/* 204 */         String[] row = line.split(",");
/* 205 */         this.extItemLayout.add(row);
/*     */       }
/*     */ 
/* 208 */       fin.close();
/*     */     }
/*     */     catch (IOException e)
/*     */     {
/* 213 */       System.err.println(e);
/*     */     }
/*     */ 
/* 217 */     return this.extItemLayout;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getSettings()
/*     */   {
/* 227 */     Map data = new TreeMap();
/*     */     try
/*     */     {
/* 232 */       FileReader fin = new FileReader(POSSplash.settings);
/*     */ 
/* 234 */       Scanner read = new Scanner(fin);
/*     */ 
/* 237 */       while (read.hasNext())
/*     */       {
/* 239 */         String line = read.nextLine();
/* 240 */         data.put(line.split(",")[0], line.split(",")[1]);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 246 */       System.err.println(new StringBuilder().append("File error: ").append(e).toString());
/*     */     }
/*     */ 
/* 249 */     return data;
/*     */   }
/*     */ 
/*     */   public String getReceiptMsg()
/*     */   {
/* 260 */     StringBuilder msg = new StringBuilder();
/*     */     try
/*     */     {
/* 264 */       FileReader fin = new FileReader(POSSplash.receipt);
/* 265 */       Scanner read = new Scanner(fin);
/*     */ 
/* 267 */       while (read.hasNext())
/*     */       {
/* 269 */         msg.append(read.nextLine());
/* 270 */         msg.append(System.lineSeparator());
/*     */       }
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 275 */       System.err.println(new StringBuilder().append("File error: ").append(e).toString());
/*     */     }
/*     */ 
/* 278 */     return msg.toString();
/*     */   }
/*     */ 
/*     */   public void readSalesByItem(String inStartDate, String inEndDate)
/*     */   {
/*     */     try
/*     */     {
/* 293 */       Scanner read = new Scanner(new FileReader(POSSplash.sales));
/*     */ 
/* 295 */       while (read.hasNext())
/*     */       {
/* 297 */         String check = read.nextLine();
/* 298 */         String[] line = check.split(",");
/*     */ 
/* 302 */         for (int i = 5; i < line.length; i += 2)
/*     */         {
/* 304 */           if ((POSPrinter.compDates(inStartDate, ((String)this.itemSalesFormat.get(line[i].trim())).split(",")[1]) >= 0) && (POSPrinter.compDates(inEndDate, ((String)this.itemSalesFormat.get(line[i].trim())).split(",")[1]) <= 0))
/*     */           {
/* 307 */             if (this.itemSalesFormat.containsKey(line[i].trim()))
/*     */             {
/* 309 */               double quantity = Double.parseDouble((String)this.itemSalesFormat.get(line[i].trim())) + Double.parseDouble(line[(i + 1)].trim());
/* 310 */               this.itemSalesFormat.remove(line[i].trim());
/*     */ 
/* 312 */               this.itemSalesFormat.put(line[i].trim(), String.format("%.2f, %s", new Object[] { Double.valueOf(quantity), line[1] }));
/*     */             }
/* 317 */             else if (line[i].trim().length() != 0) {
/* 318 */               this.itemSalesFormat.put(line[i].trim(), new StringBuilder().append(line[(i + 1)].trim()).append(", ").append(line[1]).toString());
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 327 */       System.err.println(new StringBuilder().append("File error: ").append(e).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readSalesByUser()
/*     */   {
/*     */     try
/*     */     {
/* 341 */       Scanner read = new Scanner(new FileReader(POSSplash.sales));
/*     */ 
/* 343 */       while (read.hasNext())
/*     */       {
/* 345 */         String line = read.nextLine();
/* 346 */         String[] fields = line.split(",");
/* 347 */         String user = fields[0].trim();
/* 348 */         String date = fields[1].trim();
/*     */ 
/* 351 */         if (this.userSalesFormat.containsKey(user))
/*     */         {
/* 353 */           Map inner = (Map)this.userSalesFormat.get(user);
/* 354 */           this.userSalesFormat.remove(user);
/*     */ 
/* 357 */           for (int i = 5; i < fields.length; i += 2)
/*     */           {
/* 362 */             if (inner.containsKey(fields[i].trim()))
/*     */             {
/* 364 */               double quantitySum = Double.parseDouble((String)inner.get(fields[i].trim())) + Double.parseDouble(fields[(i + 1)].trim());
/* 365 */               inner.remove(fields[i].trim());
/* 366 */               inner.put(fields[i].trim(), String.format("%.2f", new Object[] { Double.valueOf(quantitySum) }));
/*     */             }
/* 370 */             else if (fields[i].trim().length() != 0) {
/* 371 */               inner.put(fields[i].trim(), fields[(i + 1)].trim());
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 379 */           this.userSalesFormat.put(user, inner);
/*     */         }
/*     */         else
/*     */         {
/* 384 */           Map inner = new TreeMap();
/*     */ 
/* 387 */           for (int i = 5; i < fields.length; i += 2)
/*     */           {
/* 390 */             if (inner.containsKey(fields[i].trim()))
/*     */             {
/* 392 */               double quantity = Double.parseDouble((String)inner.get(fields[i].trim())) + Double.parseDouble(fields[(i + 1)].trim());
/* 393 */               inner.remove(fields[i].trim());
/* 394 */               inner.put(fields[i].trim(), String.format("%.2f", new Object[] { Double.valueOf(quantity) }));
/*     */             }
/* 399 */             else if (fields[i].trim().length() != 0) {
/* 400 */               inner.put(fields[i].trim(), fields[(i + 1)].trim());
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 406 */           this.userSalesFormat.put(user, inner);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 415 */       System.err.println(new StringBuilder().append("File not found: ").append(e).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void readSalesByDate()
/*     */   {
/*     */     try
/*     */     {
/* 427 */       Scanner read = new Scanner(new FileReader(POSSplash.sales));
/*     */ 
/* 429 */       while (read.hasNext())
/*     */       {
/* 431 */         String line = read.nextLine();
/* 432 */         String[] fields = line.split(",");
/* 433 */         String user = fields[0].trim();
/* 434 */         String date = fields[1].trim();
/*     */ 
/* 438 */         if (this.dateSalesFormat.containsKey(date))
/*     */         {
/* 440 */           Map userMap = (Map)this.dateSalesFormat.get(date);
/*     */ 
/* 442 */           if (userMap.containsKey(user))
/*     */           {
/* 444 */             Map prodMap = (Map)userMap.get(user);
/*     */ 
/* 446 */             for (int i = 5; i < fields.length; i += 2)
/*     */             {
/* 449 */               if (prodMap.containsKey(fields[i].trim()))
/*     */               {
/* 451 */                 double quantity = Double.parseDouble((String)prodMap.get(fields[i].trim())) + Double.parseDouble(fields[(i + 1)].trim());
/* 452 */                 prodMap.remove(fields[i].trim());
/* 453 */                 prodMap.put(fields[i].trim(), String.format("%.2f", new Object[] { Double.valueOf(quantity) }));
/*     */               }
/* 458 */               else if (fields[i].trim().length() != 0) {
/* 459 */                 prodMap.put(fields[i].trim(), fields[(i + 1)].trim());
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 464 */             userMap.remove(user);
/* 465 */             userMap.put(user, prodMap);
/*     */           }
/*     */           else
/*     */           {
/* 469 */             Map prodMap = new TreeMap();
/*     */ 
/* 471 */             for (int i = 5; i < fields.length; i += 2)
/*     */             {
/* 474 */               if (prodMap.containsKey(fields[i].trim()))
/*     */               {
/* 476 */                 double quantity = Double.parseDouble((String)prodMap.get(fields[i].trim())) + Double.parseDouble(fields[(i + 1)].trim());
/* 477 */                 prodMap.remove(fields[i].trim());
/* 478 */                 prodMap.put(fields[i].trim(), String.format("%.2f", new Object[] { Double.valueOf(quantity) }));
/*     */               }
/* 483 */               else if (fields[i].trim().length() != 0) {
/* 484 */                 prodMap.put(fields[i].trim(), fields[(i + 1)].trim());
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 489 */             userMap.put(user, prodMap);
/*     */           }
/*     */ 
/* 492 */           this.dateSalesFormat.remove(date);
/* 493 */           this.dateSalesFormat.put(date, userMap);
/*     */         }
/*     */         else
/*     */         {
/* 497 */           Map userMap = new TreeMap();
/*     */ 
/* 499 */           if (userMap.containsKey(user))
/*     */           {
/* 501 */             Map prodMap = (Map)userMap.get(user);
/*     */ 
/* 503 */             for (int i = 5; i < fields.length; i += 2)
/*     */             {
/* 506 */               if (prodMap.containsKey(fields[i].trim()))
/*     */               {
/* 508 */                 double quantity = Double.parseDouble((String)prodMap.get(fields[i].trim())) + Double.parseDouble(fields[(i + 1)].trim());
/* 509 */                 prodMap.remove(fields[i].trim());
/* 510 */                 prodMap.put(fields[i].trim(), String.format("%.2f", new Object[] { Double.valueOf(quantity) }));
/*     */               }
/* 515 */               else if (fields[i].trim().length() != 0) {
/* 516 */                 prodMap.put(fields[i].trim(), fields[(i + 1)].trim());
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 521 */             userMap.remove(user);
/* 522 */             userMap.put(user, prodMap);
/*     */           }
/*     */           else
/*     */           {
/* 526 */             Map prodMap = new TreeMap();
/*     */ 
/* 528 */             for (int i = 5; i < fields.length; i += 2)
/*     */             {
/* 531 */               if (prodMap.containsKey(fields[i].trim()))
/*     */               {
/* 533 */                 double quantity = Double.parseDouble((String)prodMap.get(fields[i].trim())) + Double.parseDouble(fields[(i + 1)].trim());
/* 534 */                 prodMap.remove(fields[i].trim());
/* 535 */                 prodMap.put(fields[i].trim(), String.format("%.2f", new Object[] { Double.valueOf(quantity) }));
/*     */               }
/* 540 */               else if (fields[i].trim().length() != 0) {
/* 541 */                 prodMap.put(fields[i].trim(), fields[(i + 1)].trim());
/*     */               }
/*     */ 
/*     */             }
/*     */ 
/* 546 */             userMap.put(user, prodMap);
/*     */           }
/*     */ 
/* 549 */           this.dateSalesFormat.put(date, userMap);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (FileNotFoundException e)
/*     */     {
/* 556 */       System.err.println(new StringBuilder().append("File not found: ").append(e).toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   public ArrayList<String> readSalesByShift(String user, String till, String date)
/*     */   {
/* 565 */     Scanner read = null;
/*     */     try
/*     */     {
/* 568 */       read = new Scanner(new File(POSSplash.sales));
/*     */     } catch (FileNotFoundException ex) {
/* 570 */       System.err.println(new StringBuilder().append("File error (POSDatabase 562): ").append(ex).toString());
/*     */     }
/*     */ 
/* 573 */     ArrayList tmpShiftInfo = new ArrayList();
/*     */ 
/* 575 */     tmpShiftInfo.add(date);
/* 576 */     tmpShiftInfo.add(user);
/* 577 */     tmpShiftInfo.add(till);
/*     */ 
/* 579 */     while (read.hasNext())
/*     */     {
/* 581 */       String[] line = read.nextLine().split(",");
/*     */ 
/* 584 */       if (POSPrinter.compDates(line[1].trim(), date.trim()) == 0)
/*     */       {
/* 588 */         if (line[0].trim().equals(user))
/*     */         {
/* 592 */           if (line[4].trim().equals(till))
/*     */           {
/* 594 */             readPrices();
/*     */ 
/* 597 */             for (int i = 5; i < line.length - 1; i += 2)
/*     */             {
/* 600 */               BigDecimal price = BigDecimal.ZERO;
/*     */ 
/* 602 */               for (String[] itemInfo : this.prices.values())
/*     */               {
/* 604 */                 if (itemInfo[0].trim().equals(line[i].trim())) {
/* 605 */                   price = new BigDecimal(itemInfo[1].trim());
/*     */                 }
/*     */               }
/*     */ 
/* 609 */               if (!tmpShiftInfo.contains(line[i].trim()))
/*     */               {
/* 612 */                 BigDecimal cost = price.multiply(new BigDecimal(line[(i + 1)].trim()));
/*     */ 
/* 614 */                 tmpShiftInfo.add(line[i].trim());
/* 615 */                 tmpShiftInfo.add(line[(i + 1)].trim());
/* 616 */                 tmpShiftInfo.add(String.format("%.2f", new Object[] { cost }));
/*     */               }
/*     */               else
/*     */               {
/* 621 */                 int itemLocation = tmpShiftInfo.indexOf(line[i].trim());
/*     */ 
/* 623 */                 BigDecimal cost = new BigDecimal((String)tmpShiftInfo.get(itemLocation + 2)).add(price.multiply(new BigDecimal(line[(i + 1)].trim())));
/* 624 */                 int quantity = Integer.parseInt(line[(i + 1)].trim()) + Integer.parseInt((String)tmpShiftInfo.get(itemLocation + 1));
/*     */ 
/* 626 */                 tmpShiftInfo.set(itemLocation + 1, String.format("%d", new Object[] { Integer.valueOf(quantity) }));
/* 627 */                 tmpShiftInfo.set(itemLocation + 2, String.format("%.2f", new Object[] { cost }));
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 635 */     return tmpShiftInfo;
/*     */   }
/*     */ }

/* Location:           /Users/clubs/Downloads/YFSPOS.jar
 * Qualified Name:     mainForm.POSDatabase
 * JD-Core Version:    0.6.2
 */