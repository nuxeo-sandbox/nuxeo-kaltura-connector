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
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.thumbnail.ThumbnailService;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.kaltura.adapter.KalturaAdapter;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertNotNull;


@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({
    "org.nuxeo.kaltura.core",
    "org.nuxeo.ecm.platform.picture.core",
    "org.nuxeo.ecm.platform.video.core",
    "org.nuxeo.ecm.platform.audio.core"})
public class TestKulturaThumbnailFactory {

    @Inject
    CoreSession session;

    @Inject
    protected ThumbnailService thumbnailService;

    @Test
    public void testFactory() throws IOException {
        assertNotNull(thumbnailService);

        String id = "123";
        String thmbnailUrl = "http://explorer.nuxeo.com/nuxeo/site/skin/distribution/images/nuxeo.png";

        DocumentModel doc = session.createDocumentModel("/", "test-thumbnail", "Video");
        doc.addFacet(KalturaAdapter.KALTURA_FACET);
        doc = session.createDocument(doc);
        KalturaAdapter adapter = doc.getAdapter(KalturaAdapter.class);
        adapter.setId(id);
        adapter.setThumbnailUrl(thmbnailUrl);

        Blob thumbnailBlob = thumbnailService.getThumbnail(doc,session);
        Assert.assertNotNull(thumbnailBlob);

        InputStream stream = thumbnailBlob.getStream();
        Assert.assertNotNull(stream);
    }

}
