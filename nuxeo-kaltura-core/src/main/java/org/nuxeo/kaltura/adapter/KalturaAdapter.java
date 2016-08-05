/*
 * (C) Copyright 2015-2016 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *
 */

package org.nuxeo.kaltura.adapter;

import org.nuxeo.ecm.core.api.DocumentModel;


public class KalturaAdapter {

  protected final DocumentModel doc;

  public static final String KALTURA_FACET = "Kaltura";
  public static final String idXpath = "kaltura:kalturaId";
  public static final String thumbnailUrlXpath = "kaltura:thumbnailUrl";
  public static final String searchTextXpath = "kaltura:searchText";
  public static final String modifiedXpath = "kaltura:modified";

  long modified;

  public KalturaAdapter(DocumentModel doc) {
    this.doc = doc;
  }

  public String getId() {
    return (String) doc.getPropertyValue(idXpath);
  }

  public void setId(String id) {
    doc.setPropertyValue(idXpath,id);
  }

  public String getThumbnailUrl() {
    return (String) doc.getPropertyValue(thumbnailUrlXpath);
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    doc.setPropertyValue(thumbnailUrlXpath,thumbnailUrl);
  }

  public String getTitle() {
    return (String) doc.getPropertyValue("dc:title");
  }

  public void setTitle(String title) {
    doc.setPropertyValue("dc:title",title);
  }

  public String getDescription() {
    return (String) doc.getPropertyValue("dc:description");
  }

  public void setDescription(String description) {
    doc.setPropertyValue("dc:description",description);
  }

  public String getSearchtext() {
    return (String) doc.getPropertyValue(searchTextXpath);
  }

  public void setSearchtext(String searchtext) {
    doc.setPropertyValue(searchTextXpath,searchtext);
  }

  public long getModified() {
    return (long) doc.getPropertyValue(modifiedXpath);
  }

  public void setModified(long modified) {
    doc.setPropertyValue(modifiedXpath,modified);
  }
}
