package mainForm;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author IT
 */
public class POSSettings extends javax.swing.JFrame {
    
    public static boolean loadStatus;
    private final ImageIcon msgIcon;
    private int firstLoad = 0;

    Map<String, String> newSettings = null;

    Component[] cList = null;

    FlowLayout lay = new FlowLayout(1, 1, 1);
    private final int MAX_NAME_LENGTH = 16;

    List<String> imgExts = Arrays.asList(new String[] { "png", "jpg", "jpeg", "bmp", "gif" });

    private final int MAX_RECEIPT_CHARS_PER_LINE = 30;
    StringBuilder msg = null;

    int initialItemCount = 0;
    List<Object> tableCodes = new ArrayList();

    boolean initTabAdd = false;
    /**
     * Creates new form POSSettings
     */
    public POSSettings(int loadFlag)
    {
        this.firstLoad = loadFlag;
        this.msgIcon = new ImageIcon(getClass().getResource("/alert.png"));
        loadStatus = true;
        initComponents();
        initComponents2();
        POSLogin.centerWindow(this);
        setTitle("POS Settings Menu");
        setIconImage(new ImageIcon(getClass().getResource("/frameIcon.png")).getImage());
        
        //not yet implemented
        byItemViewLog.setVisible(false);
        byShiftViewLog.setVisible(false);
        byUserViewLog.setVisible(false);
        byDateViewLog.setVisible(false);
    }
    
    private void populateTableActionPerformed(ActionEvent evt)
  {
    setUpTable();

    this.populateTable.setEnabled(false);
  }

  private void priceTableSaveActionPerformed(ActionEvent evt)
  {
    if (JOptionPane.showConfirmDialog(null, "Please note that any rows missing data will be deleted. Are you sure you wish to save?", "Confirm save.", 0, 1, this.msgIcon) == 0)
    {
      boolean goodSave = tableContentCheck();

      if (goodSave)
      {
        if (this.initialItemCount >= this.priceDataTable.getRowCount())
        {
          delData("price", 0);
        }
        else {
          addData("price", null);
        }
      }
      else
        JOptionPane.showMessageDialog(null, "Please ensure product names are less than 16 characters", "Entry too long", 0, this.msgIcon);
    }
  }

  private void delUserButtonActionPerformed(ActionEvent evt)
  {
    if (this.delCurrentUsers.getSelectedIndex() == 0)
      JOptionPane.showMessageDialog(null, "Cannot delete Admin user.", "Selection Error.", 0);
    else if (this.delCurrentUsers.getSelectedIndex() != -1)
      delData("user", this.delCurrentUsers.getSelectedIndex());
    else
      JOptionPane.showMessageDialog(null, "Please select a user to delete.", "Invalid selection", 0, this.msgIcon);
  }

  private void addUserButtonActionPerformed(ActionEvent evt)
  {
    POSDatabase database = new POSDatabase();

    if ((this.addUserName.getText().trim().isEmpty()) || (this.addUserPass.getText().trim().isEmpty())) {
      JOptionPane.showMessageDialog(null, "Please enter a username and a password.", "Input missing.", 0, this.msgIcon);
    } else {
      database.readCredentials();

      if (database.creds.containsKey(this.addUserName.getText())) {
        JOptionPane.showMessageDialog(null, "Please provide an unused and unique username.", "Username already exists.", 0, this.msgIcon);
      }
      else if (checkValidAlphaNumeric(this.addUserName.getText()))
        addData("user", null);
      else
        JOptionPane.showMessageDialog(null, "Please use only number and/or letters", "Invalid entry.", 0, this.msgIcon);
    }
  }

  private void fillItemCodes()
  {
    POSDatabase database = new POSDatabase();
    Map items = database.readPrices();
    Object[] codes = items.keySet().toArray();

    if (this.currCodeList.getSize().height != 0) {
      this.currCodeList.removeAllItems();
    }

    for (int i = 0; i < items.size(); i++)
      this.currCodeList.addItem(codes[i]);
  }

  private void addTableRowActionPerformed(ActionEvent evt)
  {
    DefaultTableModel model = (DefaultTableModel)this.priceDataTable.getModel();
    Object[] blankRow = { "", "", "" };

    model.addRow(blankRow);

    this.priceDataTable.revalidate();
    this.priceDataTable.repaint();
  }

  private void delTableRowActionPerformed(ActionEvent evt)
  {
    DefaultTableModel model = (DefaultTableModel)this.priceDataTable.getModel();

    if (this.priceDataTable.getSelectedRow() == -1) {
      JOptionPane.showMessageDialog(null, "Please select the row to delete.", "No selection made.", 0, this.msgIcon);
    } else {
      model.removeRow(this.priceDataTable.getSelectedRow());
      this.priceTableSave.doClick();
    }
  }

  private void addPrinterActionPerformed(ActionEvent evt)
  {
    POSDatabase database = new POSDatabase();

    this.newSettings = database.settings;

    this.newSettings.remove("printer");
    this.newSettings.put("printer", this.printerList.getSelectedItem().toString());

    addData("settings", null);
    initCurrPrinter();
  }

  private void printByUserActionPerformed(ActionEvent evt)
  {
    if ((this.logPrinterList.getSelectedIndex() != -1) || (this.logUserList.getSelectedIndex() != -1))
      POSPrinter.printLogUser(this.logUserList.getSelectedItem().toString(), this.logPrinterList.getSelectedItem().toString());
    else
      JOptionPane.showMessageDialog(this.rootPane, "Please select a printer to print to.", "Invalid selection.", 0, this.msgIcon);
  }

  private void returnToLoginActionPerformed(ActionEvent evt)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run() {
        new POSLogin().setVisible(true);
      }
    });
    dispose();
  }

  private void imgFileBrowseActionPerformed(ActionEvent evt)
  {
    JFileChooser fc = new JFileChooser();

    int returnVal = fc.showOpenDialog(this.imgFilePreview);

    if (returnVal == 0) {
      File file = fc.getSelectedFile();
      String path = file.getPath();
      StringBuilder ext = new StringBuilder();

      int index = 0;

      POSDatabase database = new POSDatabase();
      String oldPath = ((String)database.getSettings().get("icon")).trim();

      if (oldPath.length() != 0) {
        File oldLogo = new File(oldPath);
        oldLogo.delete();
      }
      char letter;
      while ((letter = path.charAt(path.length() - 1 - index)) != '.') {
        ext.append(letter);
        index++;
      }

      ext.reverse();

      if (this.imgExts.contains(ext.toString()))
      {
        File dest = new File(new StringBuilder().append("logo.").append(ext.toString()).toString());
        try
        {
          dest.createNewFile();

          InputStream in = Files.newInputStream(file.toPath(), new OpenOption[] { StandardOpenOption.READ });
          Files.copy(in, dest.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
          try
          {
            BufferedImage img = ImageIO.read(dest);

            ImageIcon icon = new StretchIcon(img, "Logo");
            this.imgFilePreview.setIcon(icon);

            this.newSettings = database.getSettings();

            this.newSettings.remove("icon");
            this.newSettings.put("icon", dest.getPath());
            addData("settings", null);
          }
          catch (IOException e) {
            System.err.println(new StringBuilder().append("Image error: ").append(e).toString());
          }
        }
        catch (IOException e) {
          System.err.println(new StringBuilder().append("File error: ").append(e).toString());
        }
      }
      else {
        JOptionPane.showMessageDialog(this.currCompList, new StringBuilder().append("Please select an image file. (eg. ").append(this.imgExts.toString()).append(")").toString(), "Invalid image file.", 0, this.msgIcon);
      }
    }
  }

  private void saveReceiptMsgActionPerformed(ActionEvent evt)
  {
    String[] orgMsg = this.receiptMessage.getText().trim().split(" ");
    this.msg = new StringBuilder();
    int i;
    if (this.receiptMessage.getText().length() > 30) {
      for (i = 0; i < orgMsg.length; ) {
        int lineTotal = 0;

        while (lineTotal < 30) {
          if (i < orgMsg.length) {
            if (lineTotal + orgMsg[i].length() < 30) {
              this.msg.append(orgMsg[i]);
              this.msg.append(" ");
              lineTotal = lineTotal + orgMsg[i].length() + 1;
              i++;
            } else {
              lineTotal = 30;
            }
          }
          else lineTotal = 30;

        }

        this.msg.append(System.lineSeparator());
      }
    }
    else
    {
      this.msg.append(this.receiptMessage.getText());
    }

    addData("receiptMessage", null);

    this.msg.setLength(0);
  }

  private void changeAdminPassActionPerformed(ActionEvent evt)
  {
    POSDatabase database = new POSDatabase();

    Map data = database.readCredentials();

    if ((this.oldPass.getText().length() != 0) && (this.newPass.getText().length() != 0)) {
      if (((String)data.get("Admin")).trim().equals(this.oldPass.getText().trim()))
      {
        if (checkValidAlphaNumeric(this.newPass.getText()))
        {
          data.remove("Admin");
          data.put("Admin", this.newPass.getText());

          delData("user", 0);

          JOptionPane.showMessageDialog(this.currCompList, "Password changed.");
        }
        else {
          JOptionPane.showMessageDialog(this.currCompList, "Please use only numbers and/or letters.", "Invalid entry.", 0, this.msgIcon);
        }
      }
      else JOptionPane.showMessageDialog(this.currCompList, "Old password entered is incorrect.", "Password error.", 0, this.msgIcon);
    }
    else
      JOptionPane.showMessageDialog(this.currCompList, "One of the required fields is missing.", "Input error.", 0, this.msgIcon);
  }

  private void printByDateActionPerformed(ActionEvent evt)
  {
    if (((this.logDateStartMth.getSelectedIndex() != -1) && (this.logDateStartDay.getSelectedIndex() != -1)) || ((this.logDateEndMth.getSelectedIndex() != -1) && (this.logDateEndDay.getSelectedIndex() != 1))) {
      String start = new StringBuilder().append(this.logDateStartMth.getSelectedIndex() + 1).append("/").append(this.logDateStartDay.getSelectedItem()).append("/").append(this.logDateStartYear.getSelectedItem()).toString();
      String end = new StringBuilder().append(this.logDateEndMth.getSelectedIndex() + 1).append("/").append(this.logDateEndDay.getSelectedItem()).append("/").append(this.logDateEndYear.getSelectedItem()).toString();

      POSPrinter.printLogDate(start, end, this.logPrinterList.getSelectedItem().toString());
    } else {
      JOptionPane.showMessageDialog(this.currCompList, "Please select a start month/day and end month/day.", "Invalid selection", 0, this.msgIcon);
    }
  }

  private void printByItemActionPerformed(ActionEvent evt)
  {
    if (this.logItemList.getSelectedIndex() != -1) {
      POSDatabase database = new POSDatabase();

      String beginDate = new StringBuilder().append(this.byItemStartMonth.getSelectedItem()).append("/").append(this.byItemStartDay.getSelectedItem()).append("/").append(this.byItemStartYear.getSelectedItem()).toString();
      String doneDate = new StringBuilder().append(this.byItemEndMonth.getSelectedItem()).append("/").append(this.byItemEndMonth.getSelectedItem()).append("/").append(this.byItemEndMonth.getSelectedItem()).toString();

      database.readSalesByItem(beginDate, doneDate);

      if (database.itemSalesFormat.containsKey(this.logItemList.getSelectedItem().toString()))
        POSPrinter.printLogItem(this.logItemList.getSelectedItem().toString(), beginDate, doneDate, this.logPrinterList.getSelectedItem().toString());
      else
        JOptionPane.showMessageDialog(this, "The selected item has no sales record.", "No records", 0, this.msgIcon);
    }
    else {
      JOptionPane.showMessageDialog(this.currCompList, "Please select a valid product item.", "Invalid selection", 0, this.msgIcon);
    }
  }

  private void changeWinColActionPerformed(ActionEvent evt)
  {
    Color pick = JColorChooser.showDialog(this.rootPane, null, Color.gray);
    String colorChoice = new StringBuilder().append(pick.getRed()).append("/").append(pick.getGreen()).append("/").append(pick.getBlue()).toString();

    this.winColPreview.setBackground(pick);

    POSDatabase database = new POSDatabase();
    this.newSettings = database.getSettings();

    this.newSettings.remove("color");
    this.newSettings.put("color", colorChoice);

    addData("settings", "");
  }

  private void saveCompanyNameActionPerformed(ActionEvent evt)
  {
    POSDatabase database = new POSDatabase();
    this.newSettings = database.getSettings();

    if (!this.companyName.getText().trim().isEmpty())
    {
      if (checkValidAlphaNumeric(this.companyName.getText()))
      {
        this.newSettings.remove("title");

        this.newSettings.put("title", this.companyName.getText().trim());

        addData("settings", "");
        this.titleSaveState.setText("SAVED");
      }
      else {
        JOptionPane.showMessageDialog(this.rootPane, "Please use only numbers or letters.", "Invalid entry", 0, this.msgIcon);
      }
    }
    else JOptionPane.showMessageDialog(this.rootPane, "Company title cannot be empty.", "Invalid entry", 0, this.msgIcon);
  }

  private void clearLogsActionPerformed(ActionEvent evt)
  {
    clearLogs();
  }

  public static void clearLogs()
  {
    File logs = new File(POSSplash.sales);
    logs.delete();
    try
    {
      logs.createNewFile();
    } catch (IOException e) {
      System.err.println(new StringBuilder().append("Error clearing logs unable to recreate file: ").append(e).toString());
    }
  }

  private void addTillCodeActionPerformed(ActionEvent evt)
  {
    if (!this.newTillCode.getText().trim().isEmpty())
    {
      this.newSettings = new POSDatabase().getSettings();

      String codes = new StringBuilder().append((String)this.newSettings.get("tillCode")).append("/").append(this.newTillCode.getText().trim()).toString();

      if (!((String)this.newSettings.get("tillCode")).contains(this.newTillCode.getText().trim()))
      {
        this.newSettings.remove("tillCode");
        this.newSettings.put("tillCode", codes);

        addData("settings", null);

        initTillCodeList();

        this.newTillCode.setText("");
      }
      else {
        JOptionPane.showMessageDialog(this.rootPane, "The entered till code already exists. Please enter a unique code.", "Invalid Till Code", 0, this.msgIcon);
      }
    } else {
      JOptionPane.showMessageDialog(this.rootPane, "Please enter a valid till code", "Invalid Till Code", 0, this.msgIcon);
    }
  }

  private void delTillCodeActionPerformed(ActionEvent evt)
  {
    if (this.tillCodeList.getSelectedIndex() != -1)
    {
      this.newSettings = new POSDatabase().getSettings();

      String codes = (String)this.newSettings.get("tillCode");
      String newCodes = codes.replace(new StringBuilder().append("/").append(this.tillCodeList.getSelectedItem().toString().trim()).toString(), "");

      this.newSettings.remove("tillCode");
      this.newSettings.put("tillCode", newCodes);

      delData("till", 0);

      initTillCodeList();
    }
    else
    {
      JOptionPane.showMessageDialog(this.rootPane, "Please select a till code to delete", "Invalid Selection", 0, this.msgIcon);
    }
  }

  private void printLogByShiftActionPerformed(ActionEvent evt)
  {
    if ((this.logByShiftMonth.getSelectedIndex() != -1) && (this.logByShiftYear.getSelectedIndex() != -1) && (this.logByShiftYear.getSelectedIndex() != -1))
    {
      if (this.logByShiftUser.getSelectedIndex() != -1)
      {
        if (this.logByShiftTill.getSelectedIndex() != -1)
        {
          String shiftDate = new StringBuilder().append(this.logByShiftMonth.getSelectedIndex() + 1).append("/").append(this.logByShiftDay.getSelectedItem()).append("/").append(this.logByShiftYear.getSelectedItem()).toString();
          String shiftTill = this.logByShiftTill.getSelectedItem().toString().trim();
          String shiftUser = this.logByShiftUser.getSelectedItem().toString().trim();

          POSDatabase database = new POSDatabase();

          POSPrinter.printLogShift(database.readSalesByShift(shiftUser, shiftTill, shiftDate), this.logPrinterList.getSelectedItem().toString());
        }
        else {
          JOptionPane.showMessageDialog(this.rootPane, "Please select a valid user", "Invalid Selection", 0, this.msgIcon);
        }
      }
      else
        JOptionPane.showMessageDialog(this.rootPane, "Please select a valid user", "Invalid Selection", 0, this.msgIcon);
    }
    else
      JOptionPane.showMessageDialog(this.rootPane, "Please select a valid date", "Invalid Selection", 0, this.msgIcon);
  }

  private void delTabActionPerformed(ActionEvent evt)
  {
    POSDatabase database = new POSDatabase();
    this.newSettings = new POSDatabase().getSettings();
    String tabs[] = database.getSettings().get("tabs").split("/");
    
    if (this.tabsList.getSelectedIndex() == -1) {
      JOptionPane.showMessageDialog(this.rootPane, "Please select a tab to delete", "Invalid Selection", 0, this.msgIcon);
    }
    else if (this.tabsList.getSelectedItem().toString().trim().equals(tabs[0].trim())) {
      JOptionPane.showMessageDialog(this.rootPane, "Cannot delete the default tab "+ quoteString(tabs[0]), "Invalid Selection", 0, this.msgIcon);
    }
    else {
      JPanel selectedTab = null;

      for (Component c : this.extItemTabPane.getComponents())
      {
        if (c.getName().trim().equals(this.tabsList.getSelectedItem().toString().trim())) {
          selectedTab = (JPanel)c;
        }
      }
      if (selectedTab.getComponentCount() == 0)
      {
        
        String tabList = (String)this.newSettings.get("tabs");
        
        String newTabs = tabList.replace(new StringBuilder().append("/").append(this.tabsList.getSelectedItem().toString().trim()).toString(), "");

        this.newSettings.remove("tabs");
        this.newSettings.put("tabs", newTabs);

        delData("till", 0);

        initTabList(0);

        this.extItemTabPane.remove(selectedTab);
      }
      else {
        JOptionPane.showMessageDialog(this.rootPane, "Tabs must be cleared of items before they can be deleted", "Tab is not empty", 0, this.msgIcon);
      }
    }
  }

  private void addNewTabActionPerformed(ActionEvent evt)
  {
    addNewTabDo(this.newTabTitle.getText().trim());
  }

  private void delExtItemActionPerformed(ActionEvent evt)
  {
    String tabName = this.tabsList.getSelectedItem().toString().trim();
    JPanel tabPanel = null;

    for (Component c : this.extItemTabPane.getComponents())
    {
        String cName;
        
        if (!(c.getName() == null))
        {
            cName = c.getName().trim();
        
            if (cName.equals(tabName)) {
              tabPanel = (JPanel)c;
            }
        }
    }
    
    if (tabPanel != null)
    {    
        this.cList = tabPanel.getComponents();

        if (this.cList == null)
        {
          JOptionPane.showMessageDialog(this.currCompList, "There are no items available.", "No items.", 0, this.msgIcon);
        } else if (this.currCompList.getSelectedIndex() == -1)
        {
          JOptionPane.showMessageDialog(this.currCompList, "Please select an item to delete from the list.", "Invalid Selection", 0, this.msgIcon);
        }
        else if (this.cList.length != 0)
        {
          for (int i = 0; i < tabPanel.getComponentCount(); i++)
          {
            if (this.cList[i].getName().trim().equals(this.currCompList.getSelectedItem().toString().trim()))
            {
              tabPanel.remove(i);
              delData("item", 0);
              //System.out.println(this.cList[i].getName().trim().equals(this.currCompList.getSelectedItem().toString().trim()));
            }
          }

          tabPanel.revalidate();
          tabPanel.repaint();
          setDelExtItemsList();

//          if (tabPanel.getComponentCount() == 0)
//          {
//            this.delExtItem.setEnabled(false);
//            this.currCompList.removeAllItems();
//          }
        }
        else {
          JOptionPane.showMessageDialog(this.currCompList, "Components list is empty!", "Invalid state", 0, this.msgIcon);
        }
    }
    else
    {
        JOptionPane.showMessageDialog(this.currCompList, "The item " + quoteString(this.currCompList.getSelectedItem().toString().trim()) 
                + " is not present in the tab " + quoteString(tabName), "Invalid selection", 0, this.msgIcon);
    }    
  }
  
  public static String quoteString(String word)
  {
      return "\"" + word + "\"";
  }        

  private void addNewItemActionPerformed(ActionEvent evt)
  {
    int extraLength;

    if (this.newItemNameExtra.getText().isEmpty())
      extraLength = 0;
    else {
      extraLength = this.newItemNameExtra.getText().length();
    }

    if ((this.currCodeList.getSelectedIndex() == -1) || (this.newItemName.getText().isEmpty()) || (this.tabsList.getSelectedIndex() == -1)) {
      JOptionPane.showMessageDialog(null, "Please fill out the name box, select an item code, and select a tab.", "Missing parameters.", 0, this.msgIcon);
    }
    else if (extraLength + this.newItemName.getText().length() < 16)
    {
      if (checkValidAlphaNumeric(this.newItemName.getText()))
      {
        JButton button;
        if (extraLength != 0)
        {
          button = new JButton(new StringBuilder().append("<html>").append(this.newItemName.getText()).append("<br>").append(this.newItemNameExtra.getText()).append("</html>").toString());
          button.setName(new StringBuilder().append(this.newItemName.getText()).append("/").append(this.newItemNameExtra.getText()).toString());
        }
        else
        {
          button = new JButton(this.newItemName.getText().trim());
          button.setName(this.newItemName.getText().trim());
        }
        
        button.setSize(80, 80); 
        button.setPreferredSize(new Dimension(80, 80));
        
        JPanel panel = null;

        for (Component c : this.extItemTabPane.getComponents())
        {
            if (c.getName() != null)
            {
                if (c.getName().trim().equals(this.tabsList.getSelectedItem().toString().trim())) {
                   panel = (JPanel)c;
                   break;
                }
            }    
            
        }
        
        panel.setLayout(new GridLayout(0, 6));
        panel.add(button, panel.getComponentCount());
        //panel.add(button);
        panel.revalidate();
        panel.repaint();

        addData("item", new StringBuilder().append(this.currCodeList.getSelectedItem()).append(", ").append(this.tabsList.getSelectedItem()).toString());

        setDelExtItemsList();

        if (!this.delExtItem.isEnabled()) {
          this.delExtItem.setEnabled(true);
        }

        this.newItemName.setText("");
        this.newItemNameExtra.setText("");
      }
      else {
        JOptionPane.showMessageDialog(null, "Please use only letters and/or numbers", "Invalid characters", 0, this.msgIcon);
      }
    }
    else JOptionPane.showMessageDialog(null, "Please ensure item name does not exceed 15 letters", "Item name too long", 0, this.msgIcon);
  }

  private void addNewTabDo(String name)
  {
    if ((!name.isEmpty()) || (this.initTabAdd))
    {
      if (checkValidAlphaNumeric(name))
      {
        POSDatabase database = new POSDatabase();

        JPanel newTab = new JPanel();
        newTab.setName(name.trim());

        this.extItemTabPane.add(name, newTab);

        String currTabs = (String)database.settings.get("tabs");

        this.newSettings = database.settings;

        this.newSettings.remove("tabs");
  
        this.newSettings.put("tabs", new StringBuilder().append(currTabs).append("/").append(name).toString());

        if (!this.initTabAdd) {
          addData("settings", "");
        }
        initTabList(0);

        this.initTabAdd = false;

        this.newTabTitle.setText("");
      }
      else {
        JOptionPane.showMessageDialog(this.rootPane, "Please use only numbers and/or letters", "Invalid characters", 0, this.msgIcon);
      }
    }
    else JOptionPane.showMessageDialog(this.rootPane, "Please enter a valid title name", "Invalid Tab Name", 0, this.msgIcon);
  }

  private void initTillCodeList()
  {
    if ((this.tillCodeList.getItemCount() != 0) || (this.logByShiftTill.getItemCount() != 0))
    {
      this.tillCodeList.removeAllItems();
      this.logByShiftTill.removeAllItems();
    }

    POSDatabase database = new POSDatabase();
    String codes = (String)database.getSettings().get("tillCode");
    String[] codeArray = codes.trim().split("/");

    for (String s : codeArray)
    {
      if (!s.trim().equals("0"))
      {
        this.tillCodeList.addItem(s);
        this.logByShiftTill.addItem(s);
      }
    }
  }

  private StretchIcon getLogoImg()
  {
    POSDatabase database = new POSDatabase();
    database.getSettings();

    String path = ((String)database.settings.get("icon")).trim();

    StretchIcon icon = new StretchIcon("./logo.png");
    
    if (icon != null) {
        return icon;
    }
    else {
        return null;
    }
  }

  private void setDelExtItemsList()
  {
    POSDatabase database = new POSDatabase();
    ArrayList<Object[]> components = database.getExtLayout();

    if (!components.isEmpty())
    {
      if (this.currCompList.getSize().height != 0) {
        this.currCompList.removeAllItems();
      }

      for (Object[] component : components)
      {
          String itemInfo[] = (String[]) component;
          this.currCompList.addItem(itemInfo[0]);
      }    
        
    }
  }

  private boolean tableContentCheck()
  {
    DefaultTableModel model = (DefaultTableModel)this.priceDataTable.getModel();

    for (int i = 0; i < model.getRowCount(); i++) {
      if ((model.getValueAt(i, 0).toString().trim().equals("")) || (model.getValueAt(i, 1).toString().trim().equals("")) || (model.getValueAt(i, 2).toString().trim().equals(""))) {
        model.removeRow(i);
      }

      if (model.getValueAt(i, 0).toString().trim().length() > 15) {
        return false;
      }
    }
    return true;
  }

  boolean defaultTabChange = false;
  ArrayList<Object[]> newItemLayout;
  
  private void addData(String addType, String item)
  {
    BufferedWriter br = null;
    int flag = 0;
    try
    {
      if (addType.equals("user")) {
        br = new BufferedWriter(new FileWriter(new File(POSSplash.creds), true));
        flag = 1;
      } else if (addType.equals("item")) {

          if (defaultTabChange)
          {
              br = new BufferedWriter(new FileWriter(new File(POSSplash.layout)));
          }
          else
          {
              br = new BufferedWriter(new FileWriter(new File(POSSplash.layout), true));
          }
        
          flag = 2;
      } else if (addType.equals("price")) {
        br = new BufferedWriter(new FileWriter(new File(POSSplash.prices), true));
        flag = 3;
      } else if (addType.equals("settings")) {
        br = new BufferedWriter(new FileWriter(new File(POSSplash.settings)));
        flag = 4;
      } else if (addType.equals("receiptMessage")) {
        br = new BufferedWriter(new FileWriter(new File(POSSplash.receipt)));
        flag = 5;
      }
    } catch (IOException e) {
      System.err.println(e);
    }

    if (br != null)
    {
      try
      {
        if (flag == 1)
        {
          String username = PassCoder.encryptText(this.addUserName.getText().trim());
          String password = PassCoder.encryptText(this.addUserPass.getText().trim());

          br.write(new StringBuilder().append(username).append(", ").append(password).append(System.lineSeparator()).toString());
          this.addUserName.setText("");
          this.addUserPass.setText("");
        }
        else if (flag == 2)
        {
            if (!defaultTabChange)
            {
                if (!this.newItemNameExtra.getText().trim().isEmpty())
                    br.write(new StringBuilder().append(this.newItemName.getText().trim()).append("/").append(this.newItemNameExtra.getText().trim()).append(", ").append(item).append(System.lineSeparator()).toString());
                else
                    br.write(new StringBuilder().append(this.newItemName.getText().trim()).append(", ").append(item).append(System.lineSeparator()).toString());
            }    
            else
            {
                POSDatabase database = new POSDatabase();
                newItemLayout = database.getExtLayout();
                
                for (Object[] itm : newItemLayout)
                {
                    String itemInfo = itm[0]+","+itm[1]+","+itm[2]+System.lineSeparator();
                    br.write(itemInfo);
                }
            }
        }
        else if (flag == 3)
        {
          DefaultTableModel model = (DefaultTableModel)this.priceDataTable.getModel();

          int start = 0;
          
          if (this.initialItemCount > 0) {
              start = this.initialItemCount - 1;
          }
          
          for (int i = start; i < model.getRowCount(); i++) {
            br.write(new StringBuilder().append(model.getValueAt(i, 0).toString().trim()).append(", ").append(model.getValueAt(i, 1).toString().trim()).append(", ").append(model.getValueAt(i, 2).toString().trim()).append(", ").append(System.lineSeparator()).toString());
          }

        }
        else if (flag == 4)
        {
          Object[] key = this.newSettings.keySet().toArray();
          Object[] value = this.newSettings.values().toArray();

          for (int i = 0; i < this.newSettings.size(); i++) {
            br.write(new StringBuilder().append(key[i]).append(", ").append(value[i].toString().trim()).append(System.lineSeparator()).toString());
          }
        }
        else if (flag == 5)
        {
          br.write(this.msg.toString());
        }

        br.close();
      }
      catch (IOException e)
      {
        System.err.println(new StringBuilder().append("Error writing data to file POSSettings (1590): ").append(e).toString());
      }

      if (flag == 1) {
        setUpCurrentUserList(0);
      } else if (flag == 3) {
        fillItemCodes();
        this.initialItemCount = this.priceDataTable.getModel().getRowCount();
      }
    }
  }

  private void delData(String delType, int index)
  {
    FileWriter fr = null;
    BufferedWriter br = null;
    POSDatabase database = new POSDatabase();
    Map dataOld = null;
    List<Object[]> compOld = null;
    int flag = 0;
    try
    {
      if (delType.equals("user")) {
        dataOld = database.readCredentials();
        File fout = new File(POSSplash.creds);
        fr = new FileWriter(fout);
        br = new BufferedWriter(fr);
        flag = 1;
      } else if (delType.equals("price")) {
        File fout = new File(POSSplash.prices);
        fout.delete();
        fout.createNewFile();
        fr = new FileWriter(fout);
        br = new BufferedWriter(fr);
        flag = 2;
      } else if (delType.equals("item")) {
        compOld = database.getExtLayout();
        File fout = new File(POSSplash.layout);
        fr = new FileWriter(fout);
        br = new BufferedWriter(fr);
        flag = 3;
      } else if (delType.equals("till")) {
        File fout = new File(POSSplash.settings);
        fr = new FileWriter(fout);
        br = new BufferedWriter(fr);
        flag = 4;
      }

      if (br != null)
      {
        if (flag == 1)
        {
          Object[] keys = null;
          Object[] values = null;

          if (dataOld != null)
          {
            keys = dataOld.keySet().toArray();
            values = dataOld.values().toArray();
          }

          if (index == 0) {
            String username = PassCoder.encryptText("Admin");
            String password = PassCoder.encryptText(this.newPass.getText().trim());

            br.write(new StringBuilder().append(username).append(", ").append(password).append(System.lineSeparator()).toString());
          }

          for (int i = 0; i < dataOld.size(); i++)
          {
            if (i != index) {
              String username = PassCoder.encryptText(keys[i].toString());
              String password = PassCoder.encryptText(values[i].toString());

              br.write(new StringBuilder().append(username).append(", ").append(password).append(System.lineSeparator()).toString());
            }
          }
        }
        else if (flag == 2) {
          DefaultTableModel model = (DefaultTableModel)this.priceDataTable.getModel();

          for (int i = 0; i < model.getRowCount(); i++) {
            br.write(new StringBuilder().append(model.getValueAt(i, 0).toString().trim()).append(", ").append(model.getValueAt(i, 1).toString().trim()).append(", ").append(model.getValueAt(i, 2).toString().trim()).append(System.lineSeparator()).toString());
          }
        }
        else if (flag == 3) {
          for (int i = 0; i < compOld.size(); i++) {
            if (!((Object[])compOld.get(i))[0].toString().trim().equals(this.currCompList.getSelectedItem().toString().trim())) {
              br.write(new StringBuilder().append(((Object[])compOld.get(i))[0].toString().trim()).append(", ").append(((Object[])compOld.get(i))[1].toString().trim()).append(", ").append(compOld.get(i)[2].toString()).append(System.lineSeparator()).toString());
            }

          }

          setDelExtItemsList();
        } else if (flag == 4)
        {
          Object[] keys = this.newSettings.keySet().toArray();
          Object[] values = this.newSettings.values().toArray();

          for (int i = 0; i < this.newSettings.size(); i++) {
            br.write(new StringBuilder().append(keys[i]).append(", ").append(values[i]).append(System.lineSeparator()).toString());
          }

        }

        br.close();

        if (fr != null)
          fr.close();
      }
    }
    catch (IOException e) {
      System.err.println(new StringBuilder().append("Error writing data to file POSSettings (1661) ").append(e).toString());
    }

    if (flag == 1) {
      setUpCurrentUserList(0);
    } else if (flag == 2) {
      this.initialItemCount = this.priceDataTable.getModel().getRowCount();
      fillItemCodes();
    }
  }

  private void setUpCurrentUserList(int flag)
  {
    POSDatabase dataUsers = new POSDatabase();
    Map creds = dataUsers.readCredentials();
    StringBuilder allUsers = new StringBuilder();
    Object[] keys = creds.keySet().toArray();

    for (int i = 0; i < creds.size(); i++) {
      String user = keys[i].toString();
      allUsers.append(user);
      allUsers.append(",");
    }

    Object[] users = allUsers.toString().split(",");

    this.delCurrentUsers.setListData(users);

    if (flag == 1)
    {
      for (Object user : users) {
        this.logUserList.addItem(user.toString());
      }

      this.logUserList.addItem("All");
    }

    if (flag == 2)
    {
      for (Object user : users)
      {
        this.logByShiftUser.addItem(user.toString());
      }
    }
  }

  private void setUpTable()
  {
    POSDatabase dataPrices = new POSDatabase();

    List priceData = dataPrices.getTableData();

    DefaultTableModel model = (DefaultTableModel)this.priceDataTable.getModel();

    for (int i = 0; i < priceData.size(); i++) {
      Object[] row = (Object[])priceData.get(i);

      if (!this.tableCodes.contains(row[1])) {
        model.addRow(row);
        this.tableCodes.add(row[1]);
      }
    }

    this.initialItemCount = model.getRowCount();
  }

  private void initExtLayout()
  {
    POSDatabase database = new POSDatabase();
    ArrayList<Object[]> comps = database.getExtLayout();
    JPanel panel = null;
    
    for (String name : ((String)database.getSettings().get("tabs")).split("/"))
    {
      JPanel j = new JPanel(this.lay);
      j.setName(name.trim());  
          
      if (!name.trim().isEmpty() && !name.trim().equals(database.getSettings().get("tabs").split("/")[0].trim()))
      {
          this.extItemTabPane.addTab(name.trim(), j);
      }    
        
    }
    
    //set the name of the default tab to the one on file
    newDefaultTabName.setText(database.getSettings().get("tabs").split("/")[0].trim());
    changeDefaultTabName.doClick();

    for (Object[] c : comps)
    {
      String tabTitle = c[2].toString().trim();
      int tabIndex = 0;
      boolean tabExists = false;

      for (int i = 0; i < this.extItemTabPane.getTabCount(); i++)
      {
          
        if (this.extItemTabPane.getComponentAt(i) != null)
        {
          if (tabTitle.equals(this.extItemTabPane.getComponentAt(i).getName()))
          {
            tabExists = true;
            tabIndex = i;
          }
        }
      }

      if (tabExists)
      {
        panel = (JPanel)this.extItemTabPane.getComponentAt(tabIndex);
        panel.setName(tabTitle.trim());

        JButton button = new JButton("");

        if (c[0].toString().contains("/"))
        {
          button.setName(c[0].toString());
          button.setText(new StringBuilder().append(c[0].toString().split("/")[0].trim()).append(" ").append(c[0].toString().split("/")[1].trim()).toString());
        }
        else
        {
          button.setName(c[0].toString().trim());
          button.setText(c[0].toString().trim());
        }

        button.setPreferredSize(new Dimension(80, 80));
        button.setSize(80, 80);
        panel.setLayout(this.lay);
        panel.add(button);
      }
      else
      {
        panel = new JPanel();
        panel.setName(tabTitle);

        JButton button = new JButton("");

        if (c[0].toString().contains("/"))
        {
          button.setName(c[0].toString());
          button.setText(new StringBuilder().append(c[0].toString().split("/")[0].trim()).append(" ").append(c[0].toString().split("/")[1].trim()).toString());
        }
        else
        {
          button.setName(c[0].toString().trim());
          button.setText(c[0].toString().trim());
        }

        panel.setLayout(this.lay);
        panel.add(button);

        this.extItemTabPane.addTab(tabTitle, panel);
      }
    }
  }

  private void initWinCol()
  {
    this.winColPreview.setOpaque(true);

    this.winColPreview.setBackground(POSLoadSettings.wColPreview);
  }

  private void initLogItemList()
  {
    this.logItemList.setModel(POSLoadSettings.lItemList.getModel());
  }

  private void initTabList(int load)
  {
    if (this.tabsList.getItemCount() != 0) {
      this.tabsList.removeAllItems();
    }
    if (load == 1) {
      this.tabsList.setModel(POSLoadSettings.tList.getModel());
    }
    else {
      POSDatabase database = new POSDatabase();

      String tabsString = ((String)database.settings.get("tabs")).trim();

      if (tabsString.isEmpty()) {
        this.tabsList.removeAllItems();
      }
      else
        for (String tab : tabsString.split("/"))
          this.tabsList.addItem(tab.trim());
    }
  }

  private void initPrintList()
  {
    this.printerList.setModel(POSLoadSettings.pList.getModel());
  }

  private void initCurrPrinter()
  {
    POSDatabase database = new POSDatabase();
    String name = (String)database.settings.get("printer");

    if (name.trim().equals("")) {
      name = "<html><i>none";
    }

    this.displayPrinter.setText(new StringBuilder().append("<html><i>").append(name).toString());
  }

  private void initCurrReceiptMsg()
  {
    this.receiptMessage.setText(POSLoadSettings.rMessage.getText());
  }

  private void initLogYears() {
    this.logDateStartYear.setModel(POSLoadSettings.yearList.getModel());
    this.logDateEndYear.setModel(POSLoadSettings.yearList.getModel());
    this.byItemStartYear.setModel(POSLoadSettings.yearList.getModel());
    this.byItemEndYear.setModel(POSLoadSettings.yearList.getModel());
    this.logByShiftYear.setModel(POSLoadSettings.yearList.getModel());
  }

  public static boolean checkValidAlphaNumeric(String word)
  {
    for (char c : word.toCharArray())
    {
      if (((c < '0') || (c > '9')) && ((c < 'A') || (c > 'Z')) && ((c < 'a') || (c > 'z')) && (c != ' ') && (c != '!'))
      {
        return false;
      }
    }

    return true;
  }
  
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        frameSettings = new javax.swing.JTabbedPane();
        tabGenSettings = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        addUserName = new javax.swing.JTextField();
        addUserPass = new javax.swing.JTextField();
        addUserButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        delCurrentUsers = new javax.swing.JList();
        delUserButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        oldPass = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        newPass = new javax.swing.JTextField();
        changeAdminPass = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        displayPrinter = new javax.swing.JLabel();
        printerList = new javax.swing.JComboBox();
        addPrinter = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        imgFilePreview = new javax.swing.JLabel();
        imgFileBrowse = new javax.swing.JButton();
        returnToLogin = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        saveReceiptMsg = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        receiptMessage = new javax.swing.JTextArea();
        jPanel6 = new javax.swing.JPanel();
        winColPreview = new javax.swing.JLabel();
        changeWinCol = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        companyName = new javax.swing.JTextField();
        saveCompanyName = new javax.swing.JButton();
        titleSaveState = new javax.swing.JLabel();
        tabPriceSettings = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        priceDataTable = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        populateTable = new javax.swing.JButton();
        addTableRow = new javax.swing.JButton();
        delTableRow = new javax.swing.JButton();
        priceTableSave = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        tabItemSettings = new javax.swing.JPanel();
        extItemTabPane = new javax.swing.JTabbedPane();
        defaultExtItemTab = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        currCompList = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        tabsList = new javax.swing.JComboBox();
        jPanel13 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        newItemName = new javax.swing.JTextField();
        newItemNameExtra = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        currCodeList = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        addNewItem = new javax.swing.JButton();
        jPanel14 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        delExtItem = new javax.swing.JButton();
        jPanel15 = new javax.swing.JPanel();
        newTabTitle = new javax.swing.JTextField();
        addNewTab = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        delTab = new javax.swing.JButton();
        jPanel24 = new javax.swing.JPanel();
        newDefaultTabName = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        changeDefaultTabName = new javax.swing.JButton();
        tabPrintSettings = new javax.swing.JPanel();
        jPanel17 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        logUserList = new javax.swing.JComboBox();
        printByUser = new javax.swing.JButton();
        byUserViewLog = new javax.swing.JButton();
        jPanel18 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        logDateStartDay = new javax.swing.JComboBox();
        logDateStartMth = new javax.swing.JComboBox();
        logDateStartYear = new javax.swing.JComboBox();
        logDateEndDay = new javax.swing.JComboBox();
        logDateEndMth = new javax.swing.JComboBox();
        logDateEndYear = new javax.swing.JComboBox();
        jLabel19 = new javax.swing.JLabel();
        printByDate = new javax.swing.JButton();
        byDateViewLog = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        logItemList = new javax.swing.JComboBox();
        jLabel21 = new javax.swing.JLabel();
        byItemStartDay = new javax.swing.JComboBox();
        byItemStartMonth = new javax.swing.JComboBox();
        byItemStartYear = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        byItemEndDay = new javax.swing.JComboBox();
        byItemEndMonth = new javax.swing.JComboBox();
        byItemEndYear = new javax.swing.JComboBox();
        printByItem = new javax.swing.JButton();
        byItemViewLog = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        logByShiftDay = new javax.swing.JComboBox();
        logByShiftMonth = new javax.swing.JComboBox();
        logByShiftYear = new javax.swing.JComboBox();
        jLabel24 = new javax.swing.JLabel();
        logByShiftUser = new javax.swing.JComboBox();
        jLabel25 = new javax.swing.JLabel();
        logByShiftTill = new javax.swing.JComboBox();
        printLogByShift = new javax.swing.JButton();
        byShiftViewLog = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        logPrinterList = new javax.swing.JComboBox();
        jLabel26 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        clearLogs = new javax.swing.JButton();
        autoClrLogsChkbx = new javax.swing.JCheckBox();
        tabTillSettings = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        newTillCode = new javax.swing.JTextField();
        addTillCode = new javax.swing.JButton();
        jPanel23 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        tillCodeList = new javax.swing.JComboBox();
        delTillCode = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Add POS User"));
        jPanel1.setPreferredSize(new java.awt.Dimension(767, 159));

        jLabel1.setText("Username:");

        jLabel2.setText("Password:");

        addUserButton.setText("Add New User");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addUserName)
                    .addComponent(addUserPass)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(addUserButton, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addUserPass, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addUserButton, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Delete User"));

        jScrollPane1.setViewportView(delCurrentUsers);

        delUserButton.setText("Delete User");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(delUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Change Admin Password"));

        jLabel3.setText("Enter current password:");

        oldPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oldPassActionPerformed(evt);
            }
        });

        jLabel4.setText("Enter new password:");

        changeAdminPass.setText("Change Password");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(oldPass, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(newPass, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(changeAdminPass, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(oldPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(changeAdminPass))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Receipt Printer"));

        jLabel9.setText("Printer to print to:");

        jLabel10.setText("Current printer:");

        addPrinter.setText("Select Printer");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(printerList, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(printerList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addPrinter, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(displayPrinter, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Logo/Image"));

        jLabel6.setText("Select image for POS and receipt:");

        jLabel7.setText("Preview:");

        imgFileBrowse.setText("Browse");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(imgFileBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(imgFilePreview, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imgFileBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(imgFilePreview, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        returnToLogin.setBackground(new java.awt.Color(204, 204, 204));
        returnToLogin.setForeground(new java.awt.Color(255, 0, 0));
        returnToLogin.setText("Return to Login");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("End of Receipt Message"));

        saveReceiptMsg.setText("Save Receipt Message");

        receiptMessage.setColumns(40);
        receiptMessage.setLineWrap(true);
        receiptMessage.setRows(10);
        receiptMessage.setWrapStyleWord(true);
        jScrollPane3.setViewportView(receiptMessage);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(saveReceiptMsg, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(0, 3, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveReceiptMsg, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Window Color"));

        winColPreview.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        winColPreview.setText("<html><center> Colour Preview </center></html>");

        changeWinCol.setText("Select Colour");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(changeWinCol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(winColPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(changeWinCol, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(winColPreview, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Set Receipt/POS Title"));

        saveCompanyName.setText("Save Title");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(titleSaveState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveCompanyName, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(companyName, javax.swing.GroupLayout.PREFERRED_SIZE, 346, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(companyName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(saveCompanyName)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(titleSaveState, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout tabGenSettingsLayout = new javax.swing.GroupLayout(tabGenSettings);
        tabGenSettings.setLayout(tabGenSettingsLayout);
        tabGenSettingsLayout.setHorizontalGroup(
            tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabGenSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabGenSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabGenSettingsLayout.createSequentialGroup()
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(returnToLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(tabGenSettingsLayout.createSequentialGroup()
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 365, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(199, 199, 199))
                    .addGroup(tabGenSettingsLayout.createSequentialGroup()
                        .addGroup(tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(tabGenSettingsLayout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        tabGenSettingsLayout.setVerticalGroup(
            tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabGenSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabGenSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabGenSettingsLayout.createSequentialGroup()
                        .addGroup(tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabGenSettingsLayout.createSequentialGroup()
                                .addGap(163, 163, 163)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabGenSettingsLayout.createSequentialGroup()
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(1, 1, 1)))
                        .addGroup(tabGenSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(tabGenSettingsLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(returnToLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        frameSettings.addTab("General", tabGenSettings);

        priceDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "Product Code", "Price"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        priceDataTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(priceDataTable);

        jPanel9.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        populateTable.setText("Populate Table");

        addTableRow.setText("Add New Row");

        delTableRow.setText("Delete Selected Row");

        priceTableSave.setText("Save Table");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(populateTable, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(addTableRow, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(priceTableSave, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(delTableRow, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(populateTable, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addTableRow, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delTableRow, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(177, 177, 177)
                .addComponent(priceTableSave, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel5.setText("<html>Note: To view or edit product data press the populate table button first.<br><br> Note*: Add new row inserts a row beneath the last item in the table even if <br> the grid lines are not visible. Simply click beneath the last item to access the row's <br> cells. Enter (on the keyboard) must be pressed after every entry is made. <br><br>Note**: All product names must <b> NOT </b> be longer than 15 characters including spaces.");
        jLabel5.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        javax.swing.GroupLayout tabPriceSettingsLayout = new javax.swing.GroupLayout(tabPriceSettings);
        tabPriceSettings.setLayout(tabPriceSettingsLayout);
        tabPriceSettingsLayout.setHorizontalGroup(
            tabPriceSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPriceSettingsLayout.createSequentialGroup()
                .addGroup(tabPriceSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(tabPriceSettingsLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 805, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tabPriceSettingsLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        tabPriceSettingsLayout.setVerticalGroup(
            tabPriceSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPriceSettingsLayout.createSequentialGroup()
                .addGroup(tabPriceSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        frameSettings.addTab("Prices", tabPriceSettings);

        javax.swing.GroupLayout defaultExtItemTabLayout = new javax.swing.GroupLayout(defaultExtItemTab);
        defaultExtItemTab.setLayout(defaultExtItemTabLayout);
        defaultExtItemTabLayout.setHorizontalGroup(
            defaultExtItemTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 365, Short.MAX_VALUE)
        );
        defaultExtItemTabLayout.setVerticalGroup(
            defaultExtItemTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 354, Short.MAX_VALUE)
        );

        extItemTabPane.addTab("Default Tab", defaultExtItemTab);

        jPanel11.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createTitledBorder("Item Selection")));

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(currCompList, 0, 171, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(currCompList, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createTitledBorder("Tab Selection")));

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsList, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tabsList, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel13.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createTitledBorder("Add New Item")));

        jLabel8.setText("<html>Enter item name below. <br>The second line is <br>OPTIONAL.");

        jLabel11.setText("<html> Select a code for the <br>new product below:");

        jLabel12.setText("<html>Make sure to select<br>the tab you wish the<br>item to be added to<br>from the <b> Tab Selection </b><br> box.");

        addNewItem.setText("Add Item");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(currCodeList, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(addNewItem, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(newItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(newItemNameExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newItemName, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newItemNameExtra, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currCodeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addNewItem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel14.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createTitledBorder("Delete Item")));

        jLabel13.setText("<html>Select item to delete from <br>the <b>Item Selection</b> box.");

        delExtItem.setText("Delete Item");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(delExtItem, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delExtItem, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel15.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createTitledBorder("Add New Tab")));

        addNewTab.setText("Add Tab");

        jLabel14.setText("Enter new tab title:");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(newTabTitle)
            .addComponent(addNewTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(newTabTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addNewTab, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel16.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createTitledBorder("Delete Tab")));

        jLabel15.setText("<html>Select tab to delete from<br>the <b>Tab Selection</b> box.<br>Tabs with items on them<br><b>CANNOT</b> be deleted.</html>");

        delTab.setText("Delete Tab");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(delTab, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delTab, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel24.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createTitledBorder("Rename Default Tab")));

        jLabel30.setText("<html>Enter new name ofdefault<br>tab:");

        changeDefaultTabName.setText("Change Tab Name");
        changeDefaultTabName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeDefaultTabNameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(newDefaultTabName, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
            .addComponent(changeDefaultTabName, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newDefaultTabName, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(changeDefaultTabName, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout tabItemSettingsLayout = new javax.swing.GroupLayout(tabItemSettings);
        tabItemSettings.setLayout(tabItemSettingsLayout);
        tabItemSettingsLayout.setHorizontalGroup(
            tabItemSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabItemSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabItemSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tabItemSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(extItemTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabItemSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(tabItemSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        tabItemSettingsLayout.setVerticalGroup(
            tabItemSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabItemSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabItemSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabItemSettingsLayout.createSequentialGroup()
                        .addComponent(extItemTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(tabItemSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(tabItemSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabItemSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(65, Short.MAX_VALUE))
        );

        frameSettings.addTab("Modify Items", tabItemSettings);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Print Log By Username:"));

        jLabel16.setText("Select user to print logs for:");

        printByUser.setText("Print Log by User");

        byUserViewLog.setText("View Log by User");

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(printByUser, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(byUserViewLog, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(logUserList, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logUserList, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(printByUser, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(byUserViewLog, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Print Log By Date:"));

        jLabel17.setText("Select date range to print by:");

        jLabel18.setText("Start Date:");

        logDateStartDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        logDateStartMth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec" }));

        logDateEndDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        logDateEndMth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec" }));

        jLabel19.setText("End Date:");

        printByDate.setText("Print Log by Date");

        byDateViewLog.setText("View Log by Date");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(logDateStartDay, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logDateEndDay, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(logDateEndMth, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logDateStartMth, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(logDateEndYear, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logDateStartYear, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(printByDate, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(byDateViewLog, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(logDateStartDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logDateStartMth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logDateStartYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(logDateEndDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(logDateEndMth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(logDateEndYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(byDateViewLog, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(printByDate, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Print Log By Item:"));

        jLabel20.setText("Select item to print by:");

        jLabel21.setText("Start Date:");

        byItemStartDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        byItemStartMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec" }));

        jLabel22.setText("End Date:");

        byItemEndDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        byItemEndMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec" }));

        printByItem.setText("Print Log by Item");

        byItemViewLog.setText("View Log by Item");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel19Layout.createSequentialGroup()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(21, 21, 21)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(byItemEndDay, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(byItemStartDay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(logItemList, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(byItemStartMonth, 0, 96, Short.MAX_VALUE)
                                    .addComponent(byItemEndMonth, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(byItemStartYear, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(byItemEndYear, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel19Layout.createSequentialGroup()
                        .addComponent(printByItem, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(byItemViewLog, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logItemList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(byItemStartDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(byItemStartMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(byItemStartYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(byItemEndDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(byItemEndMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(byItemEndYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel22))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(printByItem, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(byItemViewLog, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Print By Shift"));

        jLabel23.setText("Select Date:");

        logByShiftDay.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31" }));

        logByShiftMonth.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec" }));

        jLabel24.setText("Select User:");

        jLabel25.setText("Select Till:");

        printLogByShift.setText("Print Log by Shift");

        byShiftViewLog.setText("View Log by Shift");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logByShiftDay, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logByShiftMonth, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(logByShiftYear, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel20Layout.createSequentialGroup()
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel24)
                                    .addComponent(jLabel25))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(logByShiftTill, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(logByShiftUser, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel20Layout.createSequentialGroup()
                        .addComponent(printLogByShift, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(byShiftViewLog, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(logByShiftDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logByShiftMonth, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(logByShiftYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(logByShiftUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(logByShiftTill, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(printLogByShift, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(byShiftViewLog, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Printer to Print Logs to:"));

        jLabel26.setText("<html>Log reports were formatted to print on letter sized paper.<br>Printing on different sized media may produce<br>unreadable results.");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(logPrinterList, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel21Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logPrinterList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder("Log Maintenance"));

        jLabel27.setText("<html> To keep performance maximized, please maintain logs<br>regularly. Logs can automatically maintained by the system<br>by checking the box below. Logs maintained by the system<br>will automatically be cleared every 2 years from the first<br>entry date in the logs.");

        clearLogs.setText("<html><font color=\"red\">Clear Logs Now");

        autoClrLogsChkbx.setText("Automatically maintain logs");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel22Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(clearLogs, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57)
                        .addComponent(autoClrLogsChkbx))
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clearLogs, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(autoClrLogsChkbx))
                .addContainerGap())
        );

        javax.swing.GroupLayout tabPrintSettingsLayout = new javax.swing.GroupLayout(tabPrintSettings);
        tabPrintSettings.setLayout(tabPrintSettingsLayout);
        tabPrintSettingsLayout.setHorizontalGroup(
            tabPrintSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPrintSettingsLayout.createSequentialGroup()
                .addGroup(tabPrintSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(tabPrintSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        tabPrintSettingsLayout.setVerticalGroup(
            tabPrintSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabPrintSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabPrintSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tabPrintSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tabPrintSettingsLayout.createSequentialGroup()
                        .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(25, 25, 25))
        );

        frameSettings.addTab("Print and View Logs", tabPrintSettings);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Add New Till Code:"));

        jLabel28.setText("Enter New Till Code:");

        addTillCode.setText("Add Till Code");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(newTillCode, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(addTillCode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(newTillCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addTillCode, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("Delete Till Code"));

        jLabel29.setText("Select Till Code to Delete:");

        delTillCode.setText("Delete Till Code");

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tillCodeList, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel23Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(delTillCode, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(tillCodeList, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(delTillCode, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout tabTillSettingsLayout = new javax.swing.GroupLayout(tabTillSettings);
        tabTillSettings.setLayout(tabTillSettingsLayout);
        tabTillSettingsLayout.setHorizontalGroup(
            tabTillSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabTillSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tabTillSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(482, Short.MAX_VALUE))
        );
        tabTillSettingsLayout.setVerticalGroup(
            tabTillSettingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tabTillSettingsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(368, Short.MAX_VALUE))
        );

        frameSettings.addTab("Till Codes", tabTillSettings);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 817, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(frameSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initComponents2()
    {
        initPrintList();
        setUpCurrentUserList(0);
        initCurrPrinter();
        POSDatabase database = new POSDatabase();
        
        // checks if icon has been set if it has been set check that is retrieves an image
        if (((String)database.getSettings().get("icon")).trim().length() != 0) {
            StretchIcon imgIcon = getLogoImg();
            
            if (imgIcon != null) {
                this.imgFilePreview.setIcon(imgIcon);
            }
        }

        
        fillItemCodes();
        setDelExtItemsList();
        setDefaultCloseOperation(3);
        
        this.addUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            POSSettings.this.addUserButtonActionPerformed(evt);
        }
        });
        
        this.delUserButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              POSSettings.this.delUserButtonActionPerformed(evt);
            }
          });
        
        this.addPrinter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              POSSettings.this.addPrinterActionPerformed(evt);
            }
          });
        
        this.returnToLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              POSSettings.this.returnToLoginActionPerformed(evt);
            }
          });
    
        this.imgFileBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              POSSettings.this.imgFileBrowseActionPerformed(evt);
            }
          });
        
        initCurrReceiptMsg();
        
        this.saveReceiptMsg.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              POSSettings.this.saveReceiptMsgActionPerformed(evt);
            }
          });
        
        this.changeAdminPass.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              POSSettings.this.changeAdminPassActionPerformed(evt);
            }
          });
        
        this.changeWinCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              POSSettings.this.changeWinColActionPerformed(evt);
            }
          });
        
        initWinCol();
        
        this.saveCompanyName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
              POSSettings.this.saveCompanyNameActionPerformed(evt);
            }
          });
        
        if (this.priceDataTable.getColumnModel().getColumnCount() > 0) {
            this.priceDataTable.getColumnModel().getColumn(2).setResizable(false);
          }
        
        this.populateTable.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.populateTableActionPerformed(evt);
        }
      });

      this.priceTableSave.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSSettings.this.priceTableSaveActionPerformed(evt);
        }
      });

      this.addTableRow.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSSettings.this.addTableRowActionPerformed(evt);
        }
      });

      this.delTableRow.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent evt) {
          POSSettings.this.delTableRowActionPerformed(evt);
        }
      });
      
      this.addNewItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.addNewItemActionPerformed(evt);
        }
      });
      
      this.delExtItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.delExtItemActionPerformed(evt);
        }
      });
        
      this.addNewTab.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
              POSSettings.this.addNewTabActionPerformed(evt);
          }
      });
      
      this.delTab.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
              POSSettings.this.delTabActionPerformed(evt);
          }
      });
        
     initExtLayout();
     initTabList(0);
     
     PrintService[] printers = PrintServiceLookup.lookupPrintServices(null, null);

    for (PrintService printer : printers)
    {
      this.logPrinterList.addItem(printer.getName());
    }
    
    this.printByUser.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.printByUserActionPerformed(evt);
      }
    });
    
    setUpCurrentUserList(1);
    
    this.printByDate.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.printByDateActionPerformed(evt);
      }
    });
    initLogYears();
    
    initLogItemList();
    
    this.printByItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.printByItemActionPerformed(evt);
      }
    });
    
    this.clearLogs.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.clearLogsActionPerformed(evt);
      }
    });
    
    this.addTillCode.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.addTillCodeActionPerformed(evt);
      }
    });
    
    initTillCodeList();
    
    this.delTillCode.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.delTillCodeActionPerformed(evt);
      }
    });
    
    this.printLogByShift.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent evt) {
        POSSettings.this.printLogByShiftActionPerformed(evt);
      }
    });
    
    setUpCurrentUserList(2);
    
      
    }
              
    
    private void oldPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oldPassActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_oldPassActionPerformed

    private void changeDefaultTabNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeDefaultTabNameActionPerformed
        
        String oldTabName;
        
        if (!newDefaultTabName.getText().trim().isEmpty())
        {
            oldTabName = extItemTabPane.getTitleAt(0).trim();
            extItemTabPane.setTitleAt(0, newDefaultTabName.getText().trim());
            
            extItemTabPane.getComponentAt(0).setName(newDefaultTabName.getText().trim());
            POSDatabase database = new POSDatabase();
            this.newSettings = database.getSettings();
            String tabs = database.getSettings().get("tabs");
            StringBuilder newTabList = new StringBuilder();
            
            newTabList.append(newDefaultTabName.getText().trim());
            String tabNames[] = tabs.split("/");
            
            for (int i = 1; i < tabNames.length; i++)
            {
                newTabList.append("/");
                newTabList.append(tabNames[i]);
            }    
            
            this.newSettings.remove("tabs");
            
            this.newSettings.put("tabs", newTabList.toString());

            if (!this.initTabAdd) {
              addData("settings", "");
            }
            
            defaultExtItemTab.revalidate();
            defaultExtItemTab.repaint();
            
            initTabList(0);
            
            changeExtItemsAssociation(oldTabName, newDefaultTabName.getText().trim());
            
            newDefaultTabName.setText("");
            System.out.println(extItemTabPane.getComponentAt(0).getName());

        }
        else
        {
            JOptionPane.showMessageDialog(this.rootPane, "Please enter a valid tab name", "Invalid Tab Name", 0, this.msgIcon);
        }
        
    }//GEN-LAST:event_changeDefaultTabNameActionPerformed

    private void changeExtItemsAssociation(String oldTabName, String newTabName)
    {
        POSDatabase database = new POSDatabase();
        ArrayList<Object[]> currentLayout = database.getExtLayout();
        boolean tabsChanged = false;
        ArrayList<Object[]> removeList = new ArrayList(); 
        
        for (Object[] obj : currentLayout)
        {
            String itemInfo[]= (String[]) obj;
                
            if (!itemInfo[2].trim().equals(oldTabName))
            {
                String tmpInfo[] = {itemInfo[0], itemInfo[1], newTabName};
                currentLayout.remove(obj);
                currentLayout.add(tmpInfo);
                tabsChanged = true;
            }
            
            if (tabsChanged)
            {    
                defaultTabChange = true;
                addData("item", "");
                defaultTabChange = false;
            }    
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addNewItem;
    private javax.swing.JButton addNewTab;
    private javax.swing.JButton addPrinter;
    private javax.swing.JButton addTableRow;
    private javax.swing.JButton addTillCode;
    private javax.swing.JButton addUserButton;
    private javax.swing.JTextField addUserName;
    private javax.swing.JTextField addUserPass;
    private javax.swing.JCheckBox autoClrLogsChkbx;
    private javax.swing.JButton byDateViewLog;
    private javax.swing.JComboBox byItemEndDay;
    private javax.swing.JComboBox byItemEndMonth;
    private javax.swing.JComboBox byItemEndYear;
    private javax.swing.JComboBox byItemStartDay;
    private javax.swing.JComboBox byItemStartMonth;
    private javax.swing.JComboBox byItemStartYear;
    private javax.swing.JButton byItemViewLog;
    private javax.swing.JButton byShiftViewLog;
    private javax.swing.JButton byUserViewLog;
    private javax.swing.JButton changeAdminPass;
    private javax.swing.JButton changeDefaultTabName;
    private javax.swing.JButton changeWinCol;
    private javax.swing.JButton clearLogs;
    private javax.swing.JTextField companyName;
    private javax.swing.JComboBox currCodeList;
    private javax.swing.JComboBox currCompList;
    private javax.swing.JPanel defaultExtItemTab;
    private javax.swing.JList delCurrentUsers;
    private javax.swing.JButton delExtItem;
    private javax.swing.JButton delTab;
    private javax.swing.JButton delTableRow;
    private javax.swing.JButton delTillCode;
    private javax.swing.JButton delUserButton;
    private javax.swing.JLabel displayPrinter;
    private javax.swing.JTabbedPane extItemTabPane;
    private javax.swing.JTabbedPane frameSettings;
    private javax.swing.JButton imgFileBrowse;
    private javax.swing.JLabel imgFilePreview;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JComboBox logByShiftDay;
    private javax.swing.JComboBox logByShiftMonth;
    private javax.swing.JComboBox logByShiftTill;
    private javax.swing.JComboBox logByShiftUser;
    private javax.swing.JComboBox logByShiftYear;
    private javax.swing.JComboBox logDateEndDay;
    private javax.swing.JComboBox logDateEndMth;
    private javax.swing.JComboBox logDateEndYear;
    private javax.swing.JComboBox logDateStartDay;
    private javax.swing.JComboBox logDateStartMth;
    private javax.swing.JComboBox logDateStartYear;
    private javax.swing.JComboBox logItemList;
    private javax.swing.JComboBox logPrinterList;
    private javax.swing.JComboBox logUserList;
    private javax.swing.JTextField newDefaultTabName;
    private javax.swing.JTextField newItemName;
    private javax.swing.JTextField newItemNameExtra;
    private javax.swing.JTextField newPass;
    private javax.swing.JTextField newTabTitle;
    private javax.swing.JTextField newTillCode;
    private javax.swing.JTextField oldPass;
    private javax.swing.JButton populateTable;
    private javax.swing.JTable priceDataTable;
    private javax.swing.JButton priceTableSave;
    private javax.swing.JButton printByDate;
    private javax.swing.JButton printByItem;
    private javax.swing.JButton printByUser;
    private javax.swing.JButton printLogByShift;
    private javax.swing.JComboBox printerList;
    private javax.swing.JTextArea receiptMessage;
    private javax.swing.JButton returnToLogin;
    private javax.swing.JButton saveCompanyName;
    private javax.swing.JButton saveReceiptMsg;
    private javax.swing.JPanel tabGenSettings;
    private javax.swing.JPanel tabItemSettings;
    private javax.swing.JPanel tabPriceSettings;
    private javax.swing.JPanel tabPrintSettings;
    private javax.swing.JPanel tabTillSettings;
    private javax.swing.JComboBox tabsList;
    private javax.swing.JComboBox tillCodeList;
    private javax.swing.JLabel titleSaveState;
    private javax.swing.JLabel winColPreview;
    // End of variables declaration//GEN-END:variables
}
