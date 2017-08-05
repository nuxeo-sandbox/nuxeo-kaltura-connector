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
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.kaltura.service.KalturaService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import javax.inject.Inject;

import static org.junit.Assert.assertNotNull;


@RunWith(FeaturesRunner.class)
@Features({ PlatformFeature.class })
@Deploy({
    "org.nuxeo.kaltura.core",
    "org.nuxeo.ecm.platform.picture.core",
    "org.nuxeo.ecm.platform.video.core",
    "org.nuxeo.ecm.platform.audio.core",
    "org.nuxeo.ecm.platform.thumbnail",
    "org.nuxeo.ecm.platform.tag"})
@LocalDeploy({
    "nuxeo-kaltura-core:kaltura-config.xml"})
public class TestKulturaImporter {

    @Inject
    CoreSession session;

    @Inject
    protected KalturaService kulturaImporter;

    @Test
    public void testService() {
        assertNotNull(kulturaImporter);
        DocumentModel root = session.getRootDocument();
        kulturaImporter.doImport(root);
        session.save();
        //Get Documents
        DocumentModelList list = session.query("Select * From Document Where ecm:mixinType = 'Kaltura'");
        Assert.assertTrue(list.size()>0);
        System.out.println("Imported Documents: "+list.size());
    }
}
