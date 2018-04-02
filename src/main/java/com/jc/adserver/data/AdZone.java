package com.jc.adserver.data;

public class AdZone {

   private Integer id;

   private String title;

   private Integer impressions;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Integer getImpressions() {
      return impressions;
   }

   public void setImpressions(Integer impressions) {
      this.impressions = impressions;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((impressions == null) ? 0 : impressions.hashCode());
      result = prime * result + ((title == null) ? 0 : title.hashCode());
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
      if (!(obj instanceof AdZone)) {
         return false;
      }
      AdZone other = (AdZone) obj;
      if (id == null) {
         if (other.id != null) {
            return false;
         }
      }
      else if (!id.equals(other.id)) {
         return false;
      }
      if (impressions == null) {
         if (other.impressions != null) {
            return false;
         }
      }
      else if (!impressions.equals(other.impressions)) {
         return false;
      }
      if (title == null) {
         if (other.title != null) {
            return false;
         }
      }
      else if (!title.equals(other.title)) {
         return false;
      }
      return true;
   }
}
