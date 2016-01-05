/*    */ package mainForm;
/*    */ 
/*    */ public class PassCoder
/*    */ {
/*    */   private static final int MODIFIER_VALUE = 5;
/*    */ 
/*    */   public static String encryptText(String str)
/*    */   {
/* 17 */     StringBuilder tmpStr = new StringBuilder();
/*    */ 
/* 19 */     for (char e : str.toCharArray())
/*    */     {
/* 21 */       int mod = '\005' + e;
/*    */ 
/* 23 */       tmpStr.append(Character.toChars(mod));
/*    */     }
/*    */ 
/* 26 */     return tmpStr.toString();
/*    */   }
/*    */ 
/*    */   public static String decryptText(String str)
/*    */   {
/* 32 */     StringBuilder tmpStr = new StringBuilder();
/*    */ 
/* 34 */     for (char e : str.toCharArray())
/*    */     {
/* 36 */       int mod = e - '\005';
/*    */ 
/* 38 */       tmpStr.append(Character.toChars(mod));
/*    */     }
/*    */ 
/* 41 */     return tmpStr.toString();
/*    */   }
/*    */ }

/* Location:           /Users/clubs/Downloads/YFSPOS.jar
 * Qualified Name:     mainForm.PassCoder
 * JD-Core Version:    0.6.2
 */