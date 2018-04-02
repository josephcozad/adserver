package com.jc.adserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.jc.adserver.data.AdData;
import com.jc.adserver.data.AdDataComparitor;
import com.jc.adserver.data.AdForecast;
import com.jc.adserver.data.AdZone;
import com.jc.adserver.data.DataStore;

public class AdServerHelper {

   public static synchronized List<AdForecast> forecast(Integer adZoneId, Date toDate) throws Exception {

      AdZone adZone = DataStore.getAdZone(adZoneId);
      if (adZone != null) {
         List<AdData> adDataList = DataStore.getAdsByZone(adZoneId);
         if (adDataList != null && !adDataList.isEmpty()) {
            // sort by priority...
            Collections.sort(adDataList, new AdDataComparitor(AdDataComparitor.ORDER_BY_PRIORITY_DESC));

            int remainingTotalDailyImpressions = adZone.getImpressions();

            List<AdForecast> forecastDataList = new ArrayList<>();
            for (AdData adData : adDataList) {

               // num_run_days = number of days ad has been running from start inclusive of today.
               int numRunDays = calculateNumDaysRun(adData.getStart(), adData.getEnd());

               int dailyAdImpressions = (int) ((adData.getGoal()) / ((double) numRunDays)); // round down...

               double impressionRate = 0.0d; // calculate the impression rate
               if (remainingTotalDailyImpressions > 0) {
                  if (remainingTotalDailyImpressions < dailyAdImpressions) {
                     impressionRate = ((double) remainingTotalDailyImpressions) / ((double) dailyAdImpressions);
                  }
                  else {
                     impressionRate = 1.0d;
                  }
               }

               // set the remaining total daily impressions...
               if (remainingTotalDailyImpressions > 0) {
                  remainingTotalDailyImpressions -= dailyAdImpressions;
                  if (remainingTotalDailyImpressions < 0) {
                     remainingTotalDailyImpressions = 0;
                  }
               }

               AdForecast forecastData = new AdForecast(adData.getId(), adZone.getId(), impressionRate);
               forecastDataList.add(forecastData);
            }

            return forecastDataList;
         }
         else {
            throw new AdServerException(AdServerErrorCode.ADDATA_NOT_FOUND_FOR_ZONE, adZoneId.toString());
         }
      }
      else {
         throw new AdServerException(AdServerErrorCode.ADZONE_DATA_NOT_FOUND, adZoneId.toString());
      }
   }

   private static int calculateNumDaysRun(Date startDate, Date toDate) throws Exception {
      int numDays = (int) ((toDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)); // millisecs * seconds * minutes * hours
      return numDays + 1;
   }
}
