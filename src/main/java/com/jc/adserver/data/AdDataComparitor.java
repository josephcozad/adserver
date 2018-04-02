package com.jc.adserver.data;

import java.util.Comparator;

public class AdDataComparitor implements Comparator<AdData> {

   public final static int ORDER_BY_PRIORITY_ASC = 0;
   public final static int ORDER_BY_PRIORITY_DESC = 1;

   private final static int ASC = 0;
   private final static int DESC = 1;

   private final int orderBy;

   public AdDataComparitor(int orderBy) {
      this.orderBy = orderBy;
   }

   @Override
   public int compare(AdData o1, AdData o2) {

      int value = 0;
      switch (orderBy) {
         case ORDER_BY_PRIORITY_ASC:
            value = compareByPriority(o1, o2, ASC);
            break;
         case ORDER_BY_PRIORITY_DESC:
            value = compareByPriority(o1, o2, DESC);
            break;
         default:
            break;
      }

      return value;
   }

   private int compareByPriority(AdData o1, AdData o2, int order) {
      int priority1 = o1.getPriority();
      int priority2 = o2.getPriority();

      int value = priority1 - priority2;
      if (order == DESC) {
         value = priority2 - priority1;
      }
      return value;
   }
}
