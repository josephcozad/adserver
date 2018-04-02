package com.jc.adserver;

import java.util.HashMap;
import java.util.Map;

public class AdServerErrorCode {

   private static Map<Integer, AdServerErrorCode> ERROR_CODE_XREF = new HashMap<>();

   private int code = 0;
   private String codeDesc = "UKNOWN_ERROR_CODE";

   public final static AdServerErrorCode SYSTEM_ERROR = new AdServerErrorCode(9999, "SYSTEM_ERROR");

   public final static AdServerErrorCode ADDATA_NOT_FOUND_FOR_ZONE = new AdServerErrorCode(1000, "ADDATA_NOT_FOUND_FOR_ZONE");
   public final static AdServerErrorCode ADZONE_DATA_NOT_FOUND = new AdServerErrorCode(1001, "ADZONE_DATA_NOT_FOUND");
   public final static AdServerErrorCode MISSING_REQUIRED_DATA = new AdServerErrorCode(1002, "MISSING_REQUIRED_DATA");
   public final static AdServerErrorCode ADDATA_START_END_DATE_ERROR = new AdServerErrorCode(1003, "ADDATA_START_END_DATE_ERROR");
   public final static AdServerErrorCode DATA_LESS_THAN_ONE = new AdServerErrorCode(1004, "DATA_LESS_THAN_ONE");
   public final static AdServerErrorCode INVALID_DATE_FORMAT = new AdServerErrorCode(1005, "INVALID_DATE_FORMAT");
   public final static AdServerErrorCode DATA_LESS_THAN_ZERO = new AdServerErrorCode(1006, "DATA_LESS_THAN_ZERO");

   protected AdServerErrorCode(int code, String codeDesc) {
      this.code = code;
      this.codeDesc = codeDesc;

      if (!ERROR_CODE_XREF.containsKey(code)) {
         ERROR_CODE_XREF.put(code, this);
      }
      else {
         System.err.println("Unable to create ErrorCode " + code + " for " + codeDesc + "; " + code + " already exists as a code value.");
         throw new IllegalArgumentException("Unable to create ErrorCode " + code + " for " + codeDesc + "; " + code + " already exists as a code value.");
      }
   }

   // ----------------------------------------------------------

   public int getCode() {
      return code;
   }

   public static AdServerErrorCode fromCode(final int code) {
      AdServerErrorCode errorCodeObj = null;
      if (ERROR_CODE_XREF.containsKey(code)) {
         errorCodeObj = ERROR_CODE_XREF.get(code);
      }
      return errorCodeObj;
   }

   @Override
   public String toString() {
      return codeDesc.toUpperCase();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + code;
      result = prime * result + ((codeDesc == null) ? 0 : codeDesc.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (!(obj instanceof AdServerErrorCode)) {
         return false;
      }
      AdServerErrorCode other = (AdServerErrorCode) obj;
      if (code != other.code) {
         return false;
      }
      if (codeDesc == null) {
         if (other.codeDesc != null) {
            return false;
         }
      }
      else if (!codeDesc.equals(other.codeDesc)) {
         return false;
      }
      return true;
   }

}
