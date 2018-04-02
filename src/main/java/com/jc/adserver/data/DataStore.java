package com.jc.adserver.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jc.adserver.AdServerErrorCode;
import com.jc.adserver.AdServerException;

public class DataStore {

   private static List<AdData> AD_DATA_STORE = new ArrayList<>();
   private static Map<Integer, Integer> AD_DATA_XREF = new HashMap<>();

   private static List<AdZone> AD_ZONE_STORE = new ArrayList<>();
   private static Map<Integer, Integer> AD_ZONE_XREF = new HashMap<>();

   private static Map<Integer, List<Integer>> ADZONE_ADDATA_XREF = new HashMap<>();

   public static synchronized List<AdData> addAdData(List<AdData> dataList) throws Exception {
      List<AdData> dataAddedList = new ArrayList<>();
      for (AdData data : dataList) {
         AdData addedData = addAdData(data);
         dataAddedList.add(addedData);
      }

      return dataAddedList;
   }

   public static synchronized AdData addAdData(AdData data) throws Exception {
      Integer adZoneId = data.getZone();
      if (ADZONE_ADDATA_XREF.containsKey(adZoneId)) {
         // add ad data...

         Integer adId = data.getId();
         Integer index = AD_DATA_STORE.size();
         AD_DATA_STORE.add(data);
         AD_DATA_XREF.put(adId, index);

         List<Integer> indexList = ADZONE_ADDATA_XREF.get(adZoneId);
         indexList.add(index);
         ADZONE_ADDATA_XREF.put(adZoneId, indexList);

         return data;
      }
      else {
         throw new AdServerException(AdServerErrorCode.ADZONE_DATA_NOT_FOUND, adZoneId.toString());
      }
   }

   public static synchronized AdData getAdData(Integer adId) throws Exception {
      AdData data = null;
      if (AD_DATA_XREF.containsKey(adId)) {
         Integer index = AD_DATA_XREF.get(adId);
         data = AD_DATA_STORE.get(index);
      }
      return data;
   }

   public static synchronized List<AdZone> addAdZone(List<AdZone> dataList) throws Exception {
      List<AdZone> dataAddedList = new ArrayList<>();
      for (AdZone data : dataList) {
         AdZone addedData = addAdZone(data);
         dataAddedList.add(addedData);
      }

      return dataAddedList;
   }

   public static synchronized AdZone addAdZone(AdZone data) throws Exception {
      Integer adZoneId = data.getId();
      Integer index = AD_ZONE_STORE.size();
      AD_ZONE_STORE.add(data);
      AD_ZONE_XREF.put(adZoneId, index);

      // create an empty list to story ad store indexes as ads are added...
      List<Integer> adIndexList = new ArrayList<>();
      ADZONE_ADDATA_XREF.put(adZoneId, adIndexList);
      return data;
   }

   public static synchronized AdZone getAdZone(Integer adZoneId) throws Exception {
      AdZone data = null;
      if (AD_ZONE_XREF.containsKey(adZoneId)) {
         Integer index = AD_ZONE_XREF.get(adZoneId);
         data = AD_ZONE_STORE.get(index);
      }
      // else doesn't have a reference... ignore it.

      return data;
   }

   public static synchronized List<AdData> getAdsByZone(Integer adZoneId) throws Exception {

      List<AdData> adDataList = new ArrayList<>();
      if (ADZONE_ADDATA_XREF.containsKey(adZoneId)) {
         List<Integer> indexList = ADZONE_ADDATA_XREF.get(adZoneId);
         for (Integer index : indexList) {
            AdData data = AD_DATA_STORE.get(index);
            adDataList.add(data);
         }
      }
      // else doesn't have a reference... ignore it.

      return adDataList;
   }

}
