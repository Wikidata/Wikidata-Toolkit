package org.wikidata.wdtk.datamodel.implementation;

/*-
 * #%L
 * Wikidata Toolkit Data Model
 * %%
 * Copyright (C) 2014 - 2024 Wikidata Toolkit Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.wikidata.wdtk.datamodel.interfaces.DatatypeIdValue;


public class DatatypeJsonUtils{

  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_ITEM} in JSON.
   */
  public static final String JSON_DT_ITEM = "wikibase-item";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_PROPERTY} in JSON.
   */
  public static final String JSON_DT_PROPERTY = "wikibase-property";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_GLOBE_COORDINATES} in JSON.
   */
  public static final String JSON_DT_GLOBE_COORDINATES = "globe-coordinate";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_ITEM} in JSON.
   */
  public static final String JSON_DT_URL = "url";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_COMMONS_MEDIA} in JSON.
   */
  public static final String JSON_DT_COMMONS_MEDIA = "commonsMedia";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_TIME} in JSON.
   */
  public static final String JSON_DT_TIME = "time";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_QUANTITY} in JSON.
   */
  public static final String JSON_DT_QUANTITY = "quantity";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_STRING} in JSON.
   */
  public static final String JSON_DT_STRING = "string";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_MONOLINGUAL_TEXT} in JSON.
   */
  public static final String JSON_DT_MONOLINGUAL_TEXT = "monolingualtext";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_EXTERNAL_ID} in JSON.
   */
  public static final String JSON_DT_EXTERNAL_ID = "external-id";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_MATH} in JSON.
   */
  public static final String JSON_DT_MATH = "math";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_GEO_SHAPE} in JSON.
   */
  public static final String JSON_DT_GEO_SHAPE = "geo-shape";
  /**
   * String used to refer to the property datatype
   * {@link DatatypeIdValue#DT_EDTF} in JSON.
   */
  public static final String JSON_DT_EDTF = "edtf";
  private static final Pattern DATATYPE_ID_PATTERN = Pattern.compile("^http://wikiba\\.se/ontology#([a-zA-Z]+)$");

  /**
   * Returns the WDTK datatype IRI for the property datatype as represented by
   * the given JSON datatype string.
   *
   * @param jsonDatatype
   *            the JSON datatype string; case-sensitive
   * @throws IllegalArgumentException
   *             if the given datatype string is not known
   */
  public String getDatatypeIriFromJsonDatatype(String jsonDatatype) {
    switch (jsonDatatype) {
      case JSON_DT_ITEM:
        return DatatypeIdValue.DT_ITEM;
      case JSON_DT_PROPERTY:
        return DatatypeIdValue.DT_PROPERTY;
      case JSON_DT_GLOBE_COORDINATES:
        return DatatypeIdValue.DT_GLOBE_COORDINATES;
      case JSON_DT_URL:
        return DatatypeIdValue.DT_URL;
      case JSON_DT_COMMONS_MEDIA:
        return DatatypeIdValue.DT_COMMONS_MEDIA;
      case JSON_DT_TIME:
        return DatatypeIdValue.DT_TIME;
      case JSON_DT_QUANTITY:
        return DatatypeIdValue.DT_QUANTITY;
      case JSON_DT_STRING:
        return DatatypeIdValue.DT_STRING;
      case JSON_DT_MONOLINGUAL_TEXT:
        return DatatypeIdValue.DT_MONOLINGUAL_TEXT;
      case JSON_DT_EDTF:
        return DatatypeIdValue.DT_EDTF;
      default:

        String[] parts = jsonDatatype.split("-");
        for(int i = 0; i < parts.length; i++) {
          parts[i] = StringUtils.capitalize(parts[i]);
        }
        return "http://wikiba.se/ontology#" + StringUtils.join(parts);
    }
  }

  /**
   * Returns the JSON datatype for the property datatype as represented by
   * the given WDTK datatype IRI string.
   *
   * @param datatypeIri
   *            the WDTK datatype IRI string; case-sensitive
   * @throws IllegalArgumentException
   *             if the given datatype string is not known
   * @deprecated this method is unreliable and will be removed in a future release.
   */
  public String getJsonDatatypeFromDatatypeIri(String datatypeIri) {
    switch (datatypeIri) {
      case DatatypeIdValue.DT_ITEM:
        return DatatypeIdImpl.JSON_DT_ITEM;
      case DatatypeIdValue.DT_GLOBE_COORDINATES:
        return DatatypeIdImpl.JSON_DT_GLOBE_COORDINATES;
      case DatatypeIdValue.DT_URL:
        return DatatypeIdImpl.JSON_DT_URL;
      case DatatypeIdValue.DT_COMMONS_MEDIA:
        return DatatypeIdImpl.JSON_DT_COMMONS_MEDIA;
      case DatatypeIdValue.DT_TIME:
        return DatatypeIdImpl.JSON_DT_TIME;
      case DatatypeIdValue.DT_QUANTITY:
        return DatatypeIdImpl.JSON_DT_QUANTITY;
      case DatatypeIdValue.DT_STRING:
        return DatatypeIdImpl.JSON_DT_STRING;
      case DatatypeIdValue.DT_MONOLINGUAL_TEXT:
        return DatatypeIdImpl.JSON_DT_MONOLINGUAL_TEXT;
      case DatatypeIdValue.DT_PROPERTY:
        return DatatypeIdImpl.JSON_DT_PROPERTY;
      case DatatypeIdValue.DT_EDTF:
        return DatatypeIdImpl.JSON_DT_EDTF;
      default:
        //We apply the reverse algorithm of JacksonDatatypeId::getDatatypeIriFromJsonDatatype
        StringBuilder jsonDatatypeBuilder = defaultJsonDatatypeFromDatatypeIri(datatypeIri);
        return jsonDatatypeBuilder.toString();
    }
  }

  public static StringBuilder defaultJsonDatatypeFromDatatypeIri(String datatypeIri)
  {
    Matcher matcher = DATATYPE_ID_PATTERN.matcher(datatypeIri);
    if(!matcher.matches()) {
      throw new IllegalArgumentException("Unknown datatype: " + datatypeIri);
    }

    StringBuilder jsonDatatypeBuilder = new StringBuilder();
    for(char ch : StringUtils.uncapitalize(matcher.group(1)).toCharArray()) {
      if(Character.isUpperCase(ch)) {
        jsonDatatypeBuilder
            .append('-')
            .append(Character.toLowerCase(ch));
      } else {
        jsonDatatypeBuilder.append(ch);
      }
    }
    return jsonDatatypeBuilder;
  }
}
