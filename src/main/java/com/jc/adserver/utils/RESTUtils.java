package com.jc.adserver.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class RESTUtils {

   public final static String NEWLINE = System.getProperty("line.separator");

   /**
    * Date format complies with ISO-8601 using UTC such that yyyy-MM-dd'T'HH:mm:ss'Z'
    */
   public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

   public static String format(JSONArray jsonArray) throws Exception {
      return format(jsonArray, 1);
   }

   public static String format(JSONObject jsonObj) throws Exception {
      return format(jsonObj, 1);
   }

   public static JSONObject createJSONObjectFromMap(Map<String, ?> jsonData) throws Exception {
      JSONObject jsonObject = new JSONObject();

      for (Map.Entry<String, ?> entry : jsonData.entrySet()) {
         String key = entry.getKey();
         Object dataItem = entry.getValue();
         if ((key != null && !key.isEmpty()) && dataItem != null) {
            if (dataItem instanceof Map) {
               JSONObject jsonObj = createJSONObjectFromMap((Map<String, Object>) dataItem);
               jsonObject.put(key, jsonObj);
            }
            else if (dataItem instanceof List) {
               JSONArray jsonObj = createJSONArrayFromList((List<Object>) dataItem);
               jsonObject.put(key, jsonObj);
            }
            else {
               if ((dataItem instanceof String) || (dataItem instanceof Number) || (dataItem instanceof Boolean)) {
                  jsonObject.put(key, dataItem);
               }
               else {
                  JSONObject jsonObj = createJSONObjectFromObject(dataItem);
                  jsonObject.put(key, jsonObj);
               }
            }
         }
      }

      return jsonObject;
   }

   public static JSONArray createJSONArrayFromList(List<?> jsonData) throws Exception {
      JSONArray jsonArray = new JSONArray();
      for (Object dataItem : jsonData) {
         if (dataItem != null) {
            if (dataItem instanceof Map) {
               JSONObject jsonObj = createJSONObjectFromMap((Map<String, Object>) dataItem);
               jsonArray.put(jsonObj);
            }
            else if (dataItem instanceof List) {
               JSONArray jsonObj = createJSONArrayFromList((List<Object>) dataItem);
               jsonArray.put(jsonObj);
            }
            else {
               if ((dataItem instanceof String) || (dataItem instanceof Number) || (dataItem instanceof Boolean) || (dataItem instanceof JSONObject)
                     || (dataItem instanceof JSONArray)) {
                  jsonArray.put(dataItem);
               }
               else {
                  JSONObject jsonObj = createJSONObjectFromObject(dataItem);
                  jsonArray.put(jsonObj);
               }
            }
         }
      }
      return jsonArray;
   }

   public static JSONObject createJSONObjectFromObject(Object object) throws Exception {
      if (object != null) {
         JSONObject jsonObject = new JSONObject();

         boolean collapsed = false;
         if (!collapsed) {
            // Create field info for each field in the entity...
            List<Field> fieldList = new ArrayList<>();
            Class<?> classObj = object.getClass();
            while (classObj != null) {
               Field[] fields = classObj.getDeclaredFields();
               fieldList.addAll(Arrays.asList(fields));
               classObj = classObj.getSuperclass();
            }

            for (Field aField : fieldList) {
               int modfiers = aField.getModifiers();
               if (!Modifier.isStatic(modfiers)) {

                  Transient transientAnn = aField.getAnnotation(Transient.class);
                  boolean includeInJSON = false;
                  if (aField.isAnnotationPresent(Transient.class)) {
                     includeInJSON = transientAnn.includeInJson().equals(Transient.BOOLEAN.YES);
                  }

                  if (!aField.isAnnotationPresent(Transient.class) || includeInJSON) { // Transient Annotation...
                     aField.setAccessible(true);
                     Object value = aField.get(object);
                     if (value != null) {
                        String fieldName = aField.getName();
                        Class<?> fieldClass = aField.getType();
                        String fieldClassName = fieldClass.getName();
                        if (value instanceof Map) {
                           JSONObject jsonObj = createJSONObjectFromMap((Map<String, Object>) value);
                           jsonObject.put(fieldName, jsonObj);
                        }
                        else if (value instanceof List) {
                           JSONArray jsonObj = createJSONArrayFromList((List<Object>) value);
                           jsonObject.put(fieldName, jsonObj);
                        }
                        else if (value instanceof Date) {
                           Date dateObj = (Date) value;
                           String formattedDate = DATE_FORMAT.format(dateObj);
                           jsonObject.put(fieldName, formattedDate);
                        }
                        else if (value instanceof Class) {
                           jsonObject.put(fieldName, ((Class) value).getName());
                        }
                        else if (fieldClassName.contains("com.jc")) {
                           JSONObject jsonObj = createJSONObjectFromObject(value);
                           jsonObject.put(fieldName, jsonObj);
                        }
                        else {
                           jsonObject.put(fieldName, value);
                        }
                     }
                  }
                  // else field marked transient, ignore.
               }
               // else static field, ignore.
            }
         }
         return jsonObject;
      }
      else {
         throw new Exception("Supplied object was null.");
      }
   }

   private static <T> List<T> createObjectFromJSONArray(JSONArray jsonArray, ParameterizedType type) throws Exception {
      List<T> valueList = new ArrayList<>();

      Type[] actualTypes = type.getActualTypeArguments();
      if (actualTypes.length == 1) {
         if (actualTypes[0] instanceof ParameterizedType) {
            // make a parameterized call to inflate jsonArray
            valueList = createObjectFromJSONArray(jsonArray, (ParameterizedType) actualTypes[0]);
         }
         else { // not ParameterizedType
            // make class call to inflate jsonArray
            valueList = createObjectFromJSONArray(jsonArray, (Class<T>) actualTypes[0]);
         }
      }
      else if (actualTypes.length == 2) {
         // jsonArray contains one or more json objects that represent Map objects.
         valueList = new ArrayList<>();
         for (int i = 0; i < jsonArray.length(); i++) {
            Object valueObj = jsonArray.get(i);
            if (valueObj instanceof JSONObject) {
               valueObj = createObjectMapFromJSONObject((JSONObject) valueObj, (Class<T>) actualTypes[0], (Class<T>) actualTypes[1]);
            }
            else if (valueObj instanceof JSONArray) {
               throw new Exception("Error in JSON, this value pulled from the jsonArray should support a map object: " + valueObj);
            }
            valueList.add((T) valueObj);
         }
      }
      else {
         throw new Exception("Unknown datatype object to inflate.");
      }

      return valueList;
   }

   public static <T> List<T> createObjectFromJSONArray(JSONArray jsonArray, Class<T> classObj) throws Exception {
      List<T> valueList = new ArrayList<>();
      for (int i = 0; i < jsonArray.length(); i++) {
         Object valueObj = jsonArray.get(i);
         if (valueObj instanceof JSONObject) {
            valueObj = createObjectFromJSONObject((JSONObject) valueObj, classObj);
         }
         else if (valueObj instanceof JSONArray) {
            valueObj = createObjectFromJSONArray((JSONArray) valueObj, classObj);
         }
         valueList.add((T) valueObj);
      }
      return valueList;
   }

   public static <T> Map<String, T> createObjectMapFromJSONObject(JSONObject jsonObject, Class<?> keyClassObj, Class<T> valueClassObj) throws Exception {
      Map<String, T> valueList = new LinkedHashMap<>();
      String[] names = JSONObject.getNames(jsonObject);
      if (names != null && names.length > 0) {
         for (String name : names) {
            Object valueObj = jsonObject.get(name);
            if (valueObj instanceof JSONObject) {
               valueObj = createObjectFromJSONObject((JSONObject) valueObj, valueClassObj);
            }
            else if (valueObj instanceof JSONArray) {
               valueObj = createObjectFromJSONArray((JSONArray) valueObj, valueClassObj);
            }
            valueList.put(name, (T) valueObj);
         }
      }
      return valueList;
   }

   /*
    * This method will take the supplied JSONObject and attempt to create an Object of type
    * supplied. If the supplied type implements constructFieldObject(String fieldName, JSONObject jsonObj)
    * returning an Object, this method will attempt to call the type's implementation of
    * that method to "inflate" any type fields that are themselves an object. This allows
    * the implementing type to provide customized "inflating" code for the field object
    * in question. See the method 'getFieldInflatorMethod()' for more information.
    */
   public static <T> T createObjectFromJSONObject(JSONObject jsonObj, Class<T> classType) throws Exception {

      Object object = null;
      if (implementsInterface(classType, Map.class)) {
         Constructor<T> constructor = classType.getDeclaredConstructor();
         constructor.setAccessible(true);
         object = constructor.newInstance();

         Iterator<String> itr = jsonObj.keys();
         while (itr.hasNext()) {
            String key = itr.next();
            Object value = jsonObj.get(key);
            ((Map) object).put(key, value);
         }
      }
      else {
         Constructor<T> constructor = classType.getDeclaredConstructor();
         constructor.setAccessible(true);
         object = constructor.newInstance();

         // Create field info for each field in the entity...
         List<Field> fieldList = new ArrayList<>();
         Class<?> classObj = object.getClass();
         while (classObj != null) {
            Field[] fields = classObj.getDeclaredFields();
            fieldList.addAll(Arrays.asList(fields));
            classObj = classObj.getSuperclass();
         }

         for (Field field : fieldList) {
            int modfiers = field.getModifiers();
            if (!Modifier.isStatic(modfiers)) {
               if (!field.isAnnotationPresent(Transient.class)) { // Transient Annotation...
                  String fieldName = field.getName();
                  if (jsonObj.has(fieldName)) {
                     field.setAccessible(true);

                     Object jsonValue = jsonObj.get(fieldName);
                     if (jsonValue instanceof JSONObject) {
                        Class<?> fieldType = field.getType();
                        if (!fieldType.equals(Map.class)) {
                           Method inflatorMethod = getFieldInflatorMethod(object.getClass());
                           if (inflatorMethod != null) {
                              jsonValue = inflatorMethod.invoke(object, field.getName(), jsonValue);
                           }
                           else {
                              jsonValue = createObjectFromJSONObject((JSONObject) jsonValue, (Class<T>) fieldType);
                           }
                        }
                        else {
                           ParameterizedType fieldPType = (ParameterizedType) field.getGenericType();
                           Type[] typesArray = fieldPType.getActualTypeArguments();
                           jsonValue = createObjectMapFromJSONObject((JSONObject) jsonValue, (Class<T>) typesArray[0], (Class<T>) typesArray[1]);
                        }
                        field.set(object, jsonValue);
                     }
                     else if (jsonValue instanceof JSONArray) {
                        if (((JSONArray) jsonValue).length() > 0) {
                           Type fieldType = field.getGenericType();
                           if (fieldType instanceof ParameterizedType) {
                              jsonValue = createObjectFromJSONArray((JSONArray) jsonValue, (ParameterizedType) fieldType);
                           }
                           else {
                              jsonValue = createObjectFromJSONArray((JSONArray) jsonValue, List.class);
                           }
                        }
                        else {
                           jsonValue = new ArrayList();
                        }
                        field.set(object, jsonValue);
                     }
                     else {
                        Class<?> fieldType = field.getType();
                        Class<? extends Object> jsonValueClass = jsonValue.getClass();
                        if (!fieldType.equals(jsonValueClass)) {
                           if (fieldType.equals(Long.class) && jsonValueClass.equals(Integer.class)) {
                              // convert value from Integer to Long...
                              long longValue = ((Integer) jsonValue).longValue();
                              jsonValue = new Long(longValue);
                           }
                           else if (fieldType.equals(Double.class) && jsonValueClass.equals(Integer.class)) {
                              // convert value from Integer to Double...
                              double doubleValue = ((Integer) jsonValue).doubleValue();
                              jsonValue = new Double(doubleValue);
                           }
                           else if (fieldType.equals(Float.class) && jsonValueClass.equals(Integer.class)) {
                              // convert value from Integer to Float...
                              float floatValue = ((Integer) jsonValue).floatValue();
                              jsonValue = new Float(floatValue);
                           }
                           else if (fieldType.equals(Date.class) && jsonValueClass.equals(String.class)) {
                              // convert value from String to Date...
                              jsonValue = createDateObject((String) jsonValue);
                           }
                           else if (fieldType.equals(Long.class) && jsonValueClass.equals(BigInteger.class)) {
                              // convert value from BigInteger to Long...
                              long longValue = ((BigInteger) jsonValue).longValue();
                              jsonValue = new Long(longValue);
                           }
                           else if (fieldType.equals(BigInteger.class) && jsonValueClass.equals(Long.class)) {
                              // convert value from Long to BigInteger...
                              jsonValue = new BigInteger(((Long) jsonValue).toString());
                           }
                           else if (fieldType.equals(Class.class) && jsonValueClass.equals(String.class)) {
                              // convert value from String to Class...
                              jsonValue = Class.forName((String) jsonValue);
                           }
                           else if (fieldType.equals(Boolean.TYPE) && jsonValueClass.equals(Boolean.class)) {
                              // convert value from Boolean to boolean...
                              field.set(object, ((Boolean) jsonValue).booleanValue());
                              jsonValue = null; // don't try to set it later...
                           }
                           else if (fieldType.equals(Integer.TYPE) && jsonValueClass.equals(Integer.class)) {
                              // convert value from Integer to int...
                              field.set(object, ((Integer) jsonValue).intValue());
                              jsonValue = null; // don't try to set it later...
                           }
                           else if (fieldType.equals(Long.TYPE) && jsonValueClass.equals(Long.class)) {
                              // convert value from Long to long...
                              field.set(object, ((Long) jsonValue).longValue());
                              jsonValue = null; // don't try to set it later...
                           }
                           else if (fieldType.equals(Double.TYPE) && jsonValueClass.equals(Double.class)) {
                              // convert value from Double to double...
                              field.set(object, ((Double) jsonValue).doubleValue());
                              jsonValue = null; // don't try to set it later...
                           }
                           else if (fieldType.equals(Float.TYPE) && jsonValueClass.equals(Float.class)) {
                              // convert value from Float to float...
                              field.set(object, ((Float) jsonValue).floatValue());
                              jsonValue = null; // don't try to set it later...
                           }
                           else {
                              // unsupported conversion...
                              String fieldTypeName = fieldType.getSimpleName();
                              if (Number.class.isAssignableFrom(fieldType)) {
                                 fieldTypeName = "Number";
                              }
                              throw new Exception("The '" + fieldName + "' field with a datatype of '" + fieldTypeName
                                    + "', did not match the supplied JSON value type of '" + jsonValueClass.getSimpleName() + "'.");
                           }
                        }
                        // they are the same, don't worry about it.

                        if (jsonValue != null) {
                           field.set(object, jsonValue);
                        }
                     }
                  }
                  // else ignore it....
               }
               // else ignore it....
            }
            // else ignore it....
         }

      }
      return (T) object;
   }

   // this is ISO-8601 compliant using UTC.
   public static Date createDateObject(String dateStr) throws Exception {
      Date date = null;
      String regex = "(\\d{4})-(\\d{2})-(\\d{2})";
      if (dateStr.matches(regex)) {
         date = DATE_FORMAT.parse(dateStr);
      }
      return date;
   }

   private static Method getFieldInflatorMethod(Class<?> fieldClassObj) throws Exception {
      Method method = null;

      while (fieldClassObj != null) {
         try {
            Class<?>[] methodParams = new Class[2];
            methodParams[0] = String.class;
            methodParams[1] = JSONObject.class;
            method = fieldClassObj.getDeclaredMethod("constructFieldObject", methodParams);
            method.setAccessible(true);
            break;
         }
         catch (NoSuchMethodException nsmex) {
            // class doesn't have one... try the parent class if there is one.
         }
         fieldClassObj = fieldClassObj.getSuperclass();
      }
      return method;
   }

   // ---------------------------------------- PRIVATE METHODS ----------------------------------------

   private static String format(JSONArray jsonArray, int level) throws Exception {
      StringBuilder sb = new StringBuilder();

      if (jsonArray.length() == 0) {
         sb.append("[]");
      }
      else {
         sb.append("[" + NEWLINE);

         String spacer = getSpaces(level * 3);

         int numItems = jsonArray.length();
         for (int i = 0; i < numItems; i++) {
            Object item = jsonArray.get(i);
            String output = "";
            if (item instanceof JSONObject) {
               output = format((JSONObject) item, level + 1);
            }
            else if (item instanceof JSONArray) {
               output = format((JSONArray) item, level + 1);
            }
            //         else {
            //            System.out.println(item);
            //         }

            sb.append(output);
            if (i + 1 < numItems) {
               sb.append(",");
            }
            sb.append(NEWLINE);
         }

         spacer = getSpaces((level * 3) - 3);
         sb.append(spacer + "]");
      }

      return sb.toString();
   }

   private static String format(JSONObject jsonObj, int level) throws Exception {
      String spacer = getSpaces((level * 3) - 3);

      StringBuilder sb = new StringBuilder(spacer + "{}" + NEWLINE);

      String[] names = JSONObject.getNames(jsonObj);
      if (names != null && names.length > 0) {
         sb = new StringBuilder(spacer + "{" + NEWLINE);
         spacer = getSpaces(level * 3);

         for (int i = 0; i < names.length; i++) {
            String name = names[i];
            Object value = jsonObj.get(name);
            String output = "";
            if (value instanceof JSONObject) {
               output = "\"" + name + "\":" + format((JSONObject) value, level + 1);
            }
            else if (value instanceof JSONArray) {
               output = "\"" + name + "\":" + format((JSONArray) value, level + 1);
            }
            else if (value instanceof String) {
               output = "\"" + name + "\":\"" + value + "\"";
            }
            else {
               output = "\"" + name + "\":" + value;
            }

            sb.append(spacer + output);
            if (i + 1 < names.length) {
               sb.append(",");
            }
            sb.append(NEWLINE);
         }
         spacer = getSpaces((level * 3) - 3);
         sb.append(spacer + "}");
      }

      return sb.toString();
   }

   private static String getSpaces(int count) {
      String spaceValue = " ";
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < count; i++) {
         sb.append(spaceValue);
      }
      return sb.toString();
   }

   private static boolean implementsInterface(Class<?> srcClassObj, Class<?> destClassObj) {
      boolean value = false;
      Class<?>[] srcInterfaceClasses = srcClassObj.getInterfaces();
      for (Class<?> srcInterfaceClass : srcInterfaceClasses) {
         if (srcInterfaceClass.equals(destClassObj)) {
            value = true;
            break;
         }
      }
      return value;
   }
}
