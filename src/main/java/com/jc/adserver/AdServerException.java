package com.jc.adserver;

public class AdServerException extends Exception {

   private static final long serialVersionUID = 539874220822539942L;

   private final AdServerErrorCode errorCode;

   public AdServerException(AdServerErrorCode errorCode) {
      super(errorCode.toString());
      this.errorCode = errorCode;
   }

   public AdServerException(AdServerErrorCode errorCode, String message) {
      super(message);
      this.errorCode = errorCode;
   }

   public AdServerErrorCode getErrorCode() {
      return errorCode;
   }

}
