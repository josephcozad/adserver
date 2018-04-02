package com.jc.adserver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jc.adserver.data.AdData;
import com.jc.adserver.data.AdForecast;
import com.jc.adserver.data.AdZone;
import com.jc.adserver.data.DataStore;
import com.jc.adserver.utils.RESTUtils;

@Path("/")
public class AdServerService {

   @GET
   @Path("forecast/{zoneId}")
   @Produces(MediaType.APPLICATION_JSON)
   public Response forecast(@Context UriInfo info, @PathParam("zoneId") Integer zoneId, @QueryParam("toDate") String dateStr) {
      try {

         Date toDate = new Date();
         if (dateStr != null && !dateStr.isEmpty()) {
            validateInputDateFormat(dateStr, "toDate");
            toDate = RESTUtils.createDateObject(dateStr);
         }

         List<AdForecast> forecastDataList = AdServerHelper.forecast(zoneId, toDate);

         JSONArray jsonArray = RESTUtils.createJSONArrayFromList(forecastDataList);
         JSONObject jsonObj = new JSONObject();
         jsonObj.put("items", jsonArray);

         return Response.ok(jsonObj.toString(), MediaType.APPLICATION_JSON).build();
      }
      catch (Exception ex) {
         AdServerException asexc = null;
         if (ex instanceof AdServerException) {
            asexc = (AdServerException) ex;
         }
         else {
            asexc = new AdServerException(AdServerErrorCode.SYSTEM_ERROR, ex.getMessage());
         }

         JSONObject jsonErrorObj = new JSONObject();
         try {
            jsonErrorObj.put("error", asexc.getErrorCode());
            jsonErrorObj.put("code", asexc.getErrorCode().getCode());
            String message = asexc.getMessage();
            if (message != null && !message.isEmpty()) {
               jsonErrorObj.put("message", asexc.getMessage());
            }
         }
         catch (Exception jex) {
            asexc = new AdServerException(AdServerErrorCode.SYSTEM_ERROR, jex.getMessage());
            jsonErrorObj.put("error", asexc.getErrorCode());
            jsonErrorObj.put("code", asexc.getErrorCode().getCode());
            String message = asexc.getMessage();
            if (message != null && !message.isEmpty()) {
               jsonErrorObj.put("message", asexc.getMessage());
            }
         }

         Status status = Status.INTERNAL_SERVER_ERROR;
         String type = MediaType.TEXT_PLAIN;
         String payload = jsonErrorObj.toString();

         ResponseBuilder builder = Response.status(status);
         builder.type(type);
         builder.entity(payload);
         return builder.build();
      }
   }

   @POST
   @Path("add")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public Response createAd(@Context UriInfo info, String jsonParamStr) {

      Status status = null;
      String type = null;
      String payload = null;

      try {

         // GET DATA FROM REQUEST AND VALIDATE -----------------------------------------------------------------

         JSONObject parameters = new JSONObject(jsonParamStr);

         JSONArray jsonAdsArray = new JSONArray();
         if (!parameters.has("items")) { // there's only one...
            jsonAdsArray.put(parameters);
         }
         else { // there's more than one...
            jsonAdsArray = parameters.getJSONArray("items");
         }

         // PREPARE REQUEST FOR PROCESSING  -------------------------------------------------------------------

         List<AdData> adDataList = new ArrayList<>();
         for (int i = 0; i < jsonAdsArray.length(); i++) {

            // Convert JSON input to RestData object...
            JSONObject jsonAdObj = jsonAdsArray.getJSONObject(i);

            validateInputDateFormat(jsonAdObj, "start");
            validateInputDateFormat(jsonAdObj, "end");

            AdData adData = RESTUtils.createObjectFromJSONObject(jsonAdObj, AdData.class);

            // VALIDATE INPUTS -----------------------------------------------------------------------------

            validateAdData(adData);

            adDataList.add(adData);
         }

         // PROCESS REQUEST ------------------------------------------------------------------------------------

         adDataList = DataStore.addAdData(adDataList);

         // PREPARE RESULT TO SEND BACK -------------------------------------------------------------------------

         JSONArray jsonArray = RESTUtils.createJSONArrayFromList(adDataList);
         JSONObject jsonObj = new JSONObject();
         jsonObj.put("items", jsonArray);

         // SEND RESPONSE ----------------------------------------------------------------------------------

         status = Status.CREATED;
         type = MediaType.TEXT_PLAIN;
         payload = jsonObj.toString();
      }
      catch (Exception ex) {

         AdServerException asexc = null;
         if (ex instanceof AdServerException) {
            asexc = (AdServerException) ex;
         }
         else {
            asexc = new AdServerException(AdServerErrorCode.SYSTEM_ERROR, ex.getMessage());
         }

         JSONObject jsonErrorObj = new JSONObject();
         try {
            jsonErrorObj.put("error", asexc.getErrorCode());
            jsonErrorObj.put("code", asexc.getErrorCode().getCode());
            String message = asexc.getMessage();
            if (message != null && !message.isEmpty()) {
               jsonErrorObj.put("message", asexc.getMessage());
            }
         }
         catch (Exception jex) {
            asexc = new AdServerException(AdServerErrorCode.SYSTEM_ERROR, jex.getMessage());
            jsonErrorObj.put("error", asexc.getErrorCode());
            jsonErrorObj.put("code", asexc.getErrorCode().getCode());
            String message = asexc.getMessage();
            if (message != null && !message.isEmpty()) {
               jsonErrorObj.put("message", asexc.getMessage());
            }
         }

         status = Status.INTERNAL_SERVER_ERROR;
         type = MediaType.TEXT_PLAIN;
         payload = jsonErrorObj.toString();
      }

      ResponseBuilder builder = Response.status(status);
      builder.type(type);
      builder.entity(payload);
      return builder.build();

   }

   @POST
   @Path("add/zone")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public Response createZone(@Context UriInfo info, String jsonParamStr) {

      Status status = null;
      String type = null;
      String payload = null;

      try {

         // GET DATA FROM REQUEST AND VALIDATE -----------------------------------------------------------------

         JSONObject parameters = new JSONObject(jsonParamStr);

         JSONArray jsonAdsArray = new JSONArray();
         if (!parameters.has("items")) { // there's only one...
            jsonAdsArray.put(parameters);
         }
         else { // there's more than one...
            jsonAdsArray = parameters.getJSONArray("items");
         }

         // PREPARE REQUEST FOR PROCESSING  -------------------------------------------------------------------

         List<AdZone> zoneDataList = new ArrayList<>();
         for (int i = 0; i < jsonAdsArray.length(); i++) {

            // Convert JSON input to RestData object...
            JSONObject jsonAdObj = jsonAdsArray.getJSONObject(i);

            AdZone zoneData = RESTUtils.createObjectFromJSONObject(jsonAdObj, AdZone.class);

            // VALIDATE INPUTS -----------------------------------------------------------------------------

            validateZoneData(zoneData);

            zoneDataList.add(zoneData);
         }

         // PROCESS REQUEST ------------------------------------------------------------------------------------

         zoneDataList = DataStore.addAdZone(zoneDataList);

         // PREPARE RESULT TO SEND BACK -------------------------------------------------------------------------

         JSONArray jsonArray = RESTUtils.createJSONArrayFromList(zoneDataList);
         JSONObject jsonObj = new JSONObject();
         jsonObj.put("items", jsonArray);

         // SEND RESPONSE ----------------------------------------------------------------------------------

         status = Status.CREATED;
         type = MediaType.TEXT_PLAIN;
         payload = jsonObj.toString();
      }
      catch (Exception ex) {

         AdServerException asexc = null;
         if (ex instanceof AdServerException) {
            asexc = (AdServerException) ex;
         }
         else {
            asexc = new AdServerException(AdServerErrorCode.SYSTEM_ERROR, ex.getMessage());
         }

         JSONObject jsonErrorObj = new JSONObject();
         try {
            jsonErrorObj.put("error", asexc.getErrorCode());
            jsonErrorObj.put("code", asexc.getErrorCode().getCode());
            String message = asexc.getMessage();
            if (message != null && !message.isEmpty()) {
               jsonErrorObj.put("message", asexc.getMessage());
            }
         }
         catch (Exception jex) {
            asexc = new AdServerException(AdServerErrorCode.SYSTEM_ERROR, jex.getMessage());
            jsonErrorObj.put("error", asexc.getErrorCode());
            jsonErrorObj.put("code", asexc.getErrorCode().getCode());
            String message = asexc.getMessage();
            if (message != null && !message.isEmpty()) {
               jsonErrorObj.put("message", asexc.getMessage());
            }
         }

         status = Status.INTERNAL_SERVER_ERROR;
         type = MediaType.TEXT_PLAIN;
         payload = jsonErrorObj.toString();
      }

      ResponseBuilder builder = Response.status(status);
      builder.type(type);
      builder.entity(payload);
      return builder.build();
   }

   // ------------------------------------------------------------

   private void validateAdData(AdData data) throws Exception {

      Integer id = data.getId();
      if (id == null) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "id");
      }

      String creative = data.getCreative();
      if (creative == null || creative.isEmpty()) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "creative");
      }

      Date startDate = data.getStart();
      if (startDate == null) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "start");
      }

      Date endDate = data.getEnd();
      if (endDate == null) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "end");
      }

      if (endDate.getTime() <= startDate.getTime()) { // end date cannot be before start date....
         throw new AdServerException(AdServerErrorCode.ADDATA_START_END_DATE_ERROR, "End date cannot be before or the same as the start date.");
      }

      Integer goalValue = data.getGoal();
      if (goalValue == null) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "goal");
      }
      else if (goalValue < 1) {
         throw new AdServerException(AdServerErrorCode.DATA_LESS_THAN_ONE, "goal");
      }

      Integer priority = data.getPriority();
      if (priority == null) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "priority");
      }
      else if (priority < 0) {
         throw new AdServerException(AdServerErrorCode.DATA_LESS_THAN_ZERO, "priority");
      }

      Integer zoneId = data.getZone();
      if (zoneId == null) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "zone");
      }
      else {
         AdZone adZone = DataStore.getAdZone(zoneId);
         if (adZone == null) {
            throw new AdServerException(AdServerErrorCode.ADZONE_DATA_NOT_FOUND, zoneId.toString());
         }
      }
   }

   private void validateZoneData(AdZone data) throws Exception {

      Integer id = data.getId();
      if (id == null) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "id");
      }

      Integer impressions = data.getImpressions();
      if (impressions == null) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "impression");
      }
      else if (impressions < 1) {
         throw new AdServerException(AdServerErrorCode.DATA_LESS_THAN_ONE, "impression");
      }

      String title = data.getTitle();
      if (title == null || title.isEmpty()) {
         throw new AdServerException(AdServerErrorCode.MISSING_REQUIRED_DATA, "title");
      }
   }

   private JSONObject validateInputDateFormat(JSONObject jsonAdObj, String fieldName) throws Exception {
      if (jsonAdObj.has(fieldName)) {
         String dateStr = jsonAdObj.getString(fieldName);
         validateInputDateFormat(dateStr, fieldName);
      }
      return jsonAdObj;
   }

   private void validateInputDateFormat(String dateStr, String fieldName) throws Exception {
      String regex = "(\\d{4})-(\\d{2})-(\\d{2})";
      if (!dateStr.matches(regex)) {
         throw new AdServerException(AdServerErrorCode.INVALID_DATE_FORMAT, fieldName);
      }

   }

}
