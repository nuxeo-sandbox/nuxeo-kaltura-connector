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

package org.nuxeo.kaltura;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.kaltura.adapter.KalturaAdapter;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({"org.nuxeo.kaltura.core"})
public class TestKalturaAdapter {
  @Inject
  CoreSession session;

  @Test
  public void shouldCallTheAdapter() {
    String id = "123";
    String thmbnailUrl = "http://nuxeo.com";

    DocumentModel doc = session.createDocumentModel("/", "test-adapter", "File");
    doc.addFacet(KalturaAdapter.KALTURA_FACET);
    doc = session.createDocument(doc);
    KalturaAdapter adapter = doc.getAdapter(KalturaAdapter.class);
    adapter.setId(id);
    adapter.setThumbnailUrl(thmbnailUrl);
    session.saveDocument(doc);
    session.save();

    doc = session.getDocument(doc.getRef());
    adapter = doc.getAdapter(KalturaAdapter.class);
    Assert.assertEquals("Kaltura Id",id,adapter.getId());
    Assert.assertEquals("Kaltura Thumbnail URL",thmbnailUrl,adapter.getThumbnailUrl());
  }
}
