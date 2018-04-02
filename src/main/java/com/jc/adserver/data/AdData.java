package com.jc.adserver.data;

import java.util.Date;

public class AdData {

   private Integer id;

   private Integer zone;

   private String creative;

   private Integer priority;

   private Integer goal;

   private Date start;

   private Date end;

   public Integer getId() {
      return id;
   }

   public void setId(Integer id) {
      this.id = id;
   }

   public Integer getZone() {
      return zone;
   }

   public void setZone(Integer zone) {
      this.zone = zone;
   }

   public String getCreative() {
      return creative;
   }

   public void setCreative(String creative) {
      this.creative = creative;
   }

   public Integer getPriority() {
      return priority;
   }

   public void setPriority(Integer priority) {
      this.priority = priority;
   }

   public Integer getGoal() {
      return goal;
   }

   public void setGoal(Integer goal) {
      this.goal = goal;
   }

   public Date getStart() {
      return start;
   }

   public void setStart(Date start) {
      this.start = start;
   }

   public Date getEnd() {
      return end;
   }

   public void setEnd(Date end) {
      this.end = end;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((creative == null) ? 0 : creative.hashCode());
      result = prime * result + ((end == null) ? 0 : end.hashCode());
      result = prime * result + ((goal == null) ? 0 : goal.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((priority == null) ? 0 : priority.hashCode());
      result = prime * result + ((start == null) ? 0 : start.hashCode());
      result = prime * result + ((zone == null) ? 0 : zone.hashCode());
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
      if (!(obj instanceof AdData)) {
         return false;
      }
      AdData other = (AdData) obj;
      if (creative == null) {
         if (other.creative != null) {
            return false;
         }
      }
      else if (!creative.equals(other.creative)) {
         return false;
      }
      if (end == null) {
         if (other.end != null) {
            return false;
         }
      }
      else if (!end.equals(other.end)) {
         return false;
      }
      if (goal == null) {
         if (other.goal != null) {
            return false;
         }
      }
      else if (!goal.equals(other.goal)) {
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
      if (priority == null) {
         if (other.priority != null) {
            return false;
         }
      }
      else if (!priority.equals(other.priority)) {
         return false;
      }
      if (start == null) {
         if (other.start != null) {
            return false;
         }
      }
      else if (!start.equals(other.start)) {
         return false;
      }
      if (zone == null) {
         if (other.zone != null) {
            return false;
         }
      }
      else if (!zone.equals(other.zone)) {
         return false;
      }
      return true;
   }
}
