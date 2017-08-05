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

package org.nuxeo.kaltura.thumbnail;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.blob.BlobInfo;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.platform.video.adapter.ThumbnailVideoFactory;
import org.nuxeo.kaltura.adapter.KalturaAdapter;
import org.nuxeo.runtime.api.Framework;

import java.io.IOException;


public class KalturaVideoThumbnailFactory extends ThumbnailVideoFactory {

    @Override
    public Blob getThumbnail(DocumentModel doc, CoreSession session) {
        if (!doc.hasFacet(KalturaAdapter.KALTURA_FACET)) return super.getThumbnail(doc,session);
        KalturaAdapter adapter = doc.getAdapter(KalturaAdapter.class);
        BlobProvider blobProvider = Framework.getService(BlobManager.class).getBlobProvider("kaltura");
        BlobInfo blobInfo = new BlobInfo();
        blobInfo.key = "kaltura:"+adapter.getThumbnailUrl();
        blobInfo.filename = "thumbnail"+adapter.getId()+".jpg";
        blobInfo.mimeType = "image/jpeg";
        try {
            return blobProvider.readBlob(blobInfo);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

    @Override
    public Blob computeThumbnail(DocumentModel doc, CoreSession session) {
        return null;
    }
}
