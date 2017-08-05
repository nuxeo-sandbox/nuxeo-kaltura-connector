/*
 * (C) Copyright 2017 Nuxeo SA (http://nuxeo.com/) and others.
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

import com.kaltura.client.types.KalturaBaseEntry;
import com.kaltura.client.types.KalturaMediaEntry;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.FileBlob;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.kaltura.service.KalturaService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import javax.inject.Inject;
import java.io.File;
import java.io.Serializable;

import static org.junit.Assert.assertNotNull;


@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({
        "org.nuxeo.kaltura.core",
        "org.nuxeo.ecm.platform.picture.core",
        "org.nuxeo.ecm.platform.video.core"})
@LocalDeploy({
        "nuxeo-kaltura-core:kaltura-config.xml"})
public class TestKalturaPublish {

    @Inject
    CoreSession session;

    @Inject
    protected KalturaService kalturaService;

    @Test
    public void testPublish() {
        assertNotNull(kalturaService);
        DocumentModel doc = session.createDocumentModel("/","TestUpload","Video");
        doc.setPropertyValue("dc:title","TestUpload");
        File file = new File(getClass().getResource("/files/test.mp4").getPath());
        Blob blob = new FileBlob(file);
        doc.setPropertyValue("file:content", (Serializable) blob);
        doc = session.createDocument(doc);
        doc = session.saveDocument(doc);
        KalturaBaseEntry result = (KalturaBaseEntry) kalturaService.publish(doc);
        Assert.assertNotNull(result);
        Assert.assertTrue(result instanceof KalturaMediaEntry);
        //KalturaMediaEntry mediaEntry = (KalturaMediaEntry) result;
        //Assert.assertNotNull(mediaEntry.dataUrl);
        //Assert.assertEquals("TestUpload",mediaEntry.name);
    }
}
