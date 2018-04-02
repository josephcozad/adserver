package com.jc.adserver.data;

public class AdForecast {

   private Integer id;

   private Integer adId;

   private Integer adZoneId;

   private Double impressionRate;

   public AdForecast(Integer adId, Integer adZoneId, Double impressionRate) {
      this.adId = adId;
      this.adZoneId = adZoneId;
      this.impressionRate = impressionRate;
   }

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getAdId() {
      return adId;
   }

   public void setAdId(Integer adId) {
      this.adId = adId;
   }

   public Integer getAdZoneId() {
      return adZoneId;
   }

   public void setAdZoneId(Integer adZoneId) {
      this.adZoneId = adZoneId;
   }

   public Double getImpressionRate() {
      return impressionRate;
   }

   public void setImpressionRate(Double impressionRate) {
      this.impressionRate = impressionRate;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((adId == null) ? 0 : adId.hashCode());
      result = prime * result + ((adZoneId == null) ? 0 : adZoneId.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((impressionRate == null) ? 0 : impressionRate.hashCode());
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
      if (!(obj instanceof AdForecast)) {
         return false;
      }
      AdForecast other = (AdForecast) obj;
      if (adId == null) {
         if (other.adId != null) {
            return false;
         }
      }
      else if (!adId.equals(other.adId)) {
         return false;
      }
      if (adZoneId == null) {
         if (other.adZoneId != null) {
            return false;
         }
      }
      else if (!adZoneId.equals(other.adZoneId)) {
         return false;
      }
      if (id == null) {
         if (other.id != null) {
            return false;
         }
      }
      else if (!id.equals(other.id)) {
         return false;
      }
      if (impressionRate == null) {
         if (other.impressionRate != null) {
            return false;
         }
      }
      else if (!impressionRate.equals(other.impressionRate)) {
         return false;
      }
      return true;
   }
}
