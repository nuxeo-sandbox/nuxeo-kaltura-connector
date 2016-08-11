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

package org.nuxeo.kaltura.service;

import com.kaltura.client.KalturaApiException;
import com.kaltura.client.KalturaClient;
import com.kaltura.client.KalturaConfiguration;
import com.kaltura.client.enums.KalturaEntryType;
import com.kaltura.client.enums.KalturaSessionType;
import com.kaltura.client.types.KalturaBaseEntry;
import com.kaltura.client.types.KalturaBaseEntryListResponse;
import com.kaltura.client.types.KalturaFlavorAsset;
import com.kaltura.client.types.KalturaMediaEntry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.*;
import org.nuxeo.ecm.core.blob.BlobManager;
import org.nuxeo.ecm.core.blob.BlobProvider;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.tag.TagService;
import org.nuxeo.ecm.platform.thumbnail.ThumbnailConstants;
import org.nuxeo.kaltura.adapter.KalturaAdapter;
import org.nuxeo.kaltura.worker.KalturaImporterWorker;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class KalturaImporterImpl extends DefaultComponent implements KalturaImporter {

    private static final Log log = LogFactory.getLog(KalturaImporterImpl.class);

    protected static final String CONFIG_EXT_POINT = "configuration";

    protected KalturaAccountDescriptor config = null;

    protected KalturaClient client = null;

    /**
     * Component activated notification.
     * Called when the component is activated. All component dependencies are resolved at that moment.
     * Use this method to initialize the component.
     *
     * @param context the component context.
     */
    @Override
    public void activate(ComponentContext context) {
        super.activate(context);
    }

    /**
     * Component deactivated notification.
     * Called before a component is unregistered.
     * Use this method to do cleanup if any and free any resources held by the component.
     *
     * @param context the component context.
     */
    @Override
    public void deactivate(ComponentContext context) {
        super.deactivate(context);
    }

    /**
     * Application started notification.
     * Called after the application started.
     * You can do here any initialization that requires a working application
     * (all resolved bundles and components are active at that moment)
     *
     * @param context the component context. Use it to get the current bundle context
     * @throws Exception
     */
    @Override
    public void applicationStarted(ComponentContext context) {
        // do nothing by default. You can remove this method if not used.
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (CONFIG_EXT_POINT.equals(extensionPoint)) {
            config = (KalturaAccountDescriptor) contribution;
        }
    }

    @Override
    public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        // Logic to do when unregistering any contribution
    }

    @Override
    public void schedule(DocumentModel root) {
        CoreSession session = root.getCoreSession();
        WorkManager wm = Framework.getService(WorkManager.class);
        KalturaImporterWorker worker = new KalturaImporterWorker();
        worker.setDocument(root.getRepositoryName(), root.getId());
        worker.setOriginatingUsername(session.getPrincipal().getName());
        wm.schedule(worker);
    }

    @Override
    public void doImport(DocumentModel root) throws NuxeoException {
        KalturaClient client = getClient();
        try {
            KalturaBaseEntryListResponse list = client.getBaseEntryService().list();
            for (KalturaBaseEntry entry :list.objects) {
                processEntry(entry,root);
            }
        } catch (Exception e) {
            throw new NuxeoException(e);
        }
    }

    protected KalturaClient getClient() throws NuxeoException {
        if (client != null) return client;

        KalturaConfiguration configuration = new KalturaConfiguration();
        configuration.setEndpoint(config.getBasePath());
        this.client = new KalturaClient(configuration);

        try {
            String ks = client.generateSession(
                    config.getAdminSecret(),
                    config.getUsername(),
                    KalturaSessionType.ADMIN,
                    Integer.parseInt(config.getPartnerId()));
            this.client.setKs(ks);
            return this.client;
        } catch (Exception e) {
            throw new NuxeoException(e);
        }
    }


    protected void processEntry(KalturaBaseEntry entry, DocumentModel root) {
        //Check Media Type
        if (!KalturaEntryType.MEDIA_CLIP.equals(entry.type)) return;

        KalturaMediaEntry media = (KalturaMediaEntry) entry;
        String docType;
        switch (media.mediaType) {
            case VIDEO : docType = "Video";break;
            case IMAGE : docType = "Picture";break;
            case AUDIO : docType = "Audio";break;
            default: docType = null;
        }

        if (docType == null) return;

        DocumentModel doc = getOrCreateFileDocument(root,docType,entry.id);
        doc.setPropertyValue("dc:source","Kaltura");
        KalturaAdapter adapter = doc.getAdapter(KalturaAdapter.class);
        adapter.setTitle(entry.name);
        adapter.setDescription(entry.description);
        adapter.setThumbnailUrl(entry.thumbnailUrl);
        adapter.setSearchtext(entry.searchText);
        adapter.setModified(entry.updatedAt);
        adapter.setPlays(media.plays);
        adapter.setViews(media.views);

        //Add type specific metadata
        switch (media.mediaType) {
            case VIDEO : getVideoMetadata(doc,media);break;
            case IMAGE : getPictureMetadata(doc,media);break;
            case AUDIO : getAudioMetadata(doc,media);break;
            default: break;
        }

        //Add Tags
        String tags = media.tags;
        if (tags!=null) {
            String tagArray[] = tags.split(",");
            TagService tagService = Framework.getService(TagService.class);
            for (String tag:tagArray) {
                tag = tag.trim().replaceAll(" ","+");
                tagService.tag(doc.getCoreSession(),doc.getId(),tag,doc.getCoreSession().getPrincipal().getName());
            }
        }

        CoreSession session = doc.getCoreSession();
        session.saveDocument(doc);
    }

    protected DocumentModel getOrCreateFileDocument(DocumentModel root, String docType, String itemId){
        CoreSession session = root.getCoreSession();
        String query = String.format(
                "Select * From Document Where %s='%s'",KalturaAdapter.idXpath,itemId);
        DocumentModelList list = session.query(query);
        if (list.size()>0) return list.get(0);

        DocumentModel file =
                session.createDocumentModel(
                        root.getPathAsString(),itemId,docType);

        file.addFacet(ThumbnailConstants.THUMBNAIL_FACET);
        file.addFacet(KalturaAdapter.KALTURA_FACET);
        KalturaAdapter adapter = file.getAdapter(KalturaAdapter.class);
        adapter.setId(itemId);
        return session.createDocument(file);
    }

    protected void getVideoMetadata(DocumentModel doc, KalturaMediaEntry media) {
        KalturaClient client = getClient();
        List<KalturaFlavorAsset> flavors;
        try {
            flavors = client.getFlavorAssetService().getByEntryId(media.id);
        } catch (KalturaApiException e) {
            throw new NuxeoException(e);
        }

        for(KalturaFlavorAsset flavorAsset: flavors) {
            if (flavorAsset.isOriginal) {
                Map<String,Serializable> info = (Map<String, Serializable>) doc.getPropertyValue("vid:info");
                info.put("duration",media.duration);
                info.put("width",flavorAsset.width);
                info.put("height",flavorAsset.height);
                info.put("format",flavorAsset.fileExt);
                info.put("frameRate",flavorAsset.frameRate);
                doc.setPropertyValue("vid:info", (Serializable) info);
            }
        }
    }

    protected void getAudioMetadata(DocumentModel doc, KalturaMediaEntry media) {
        doc.setPropertyValue("aud:duration",media.duration);
    }

    protected void getPictureMetadata(DocumentModel doc, KalturaMediaEntry media) {
        BlobProvider blobProvider = Framework.getService(BlobManager.class).getBlobProvider("kaltura");
        BlobManager.BlobInfo blobInfo = new BlobManager.BlobInfo();
        blobInfo.key = "kaltura:"+media.dataUrl;
        blobInfo.filename = media.name+".jpg";
        blobInfo.mimeType = "image/jpeg";
        try {
            Blob blob = blobProvider.readBlob(blobInfo);
            doc.setPropertyValue("file:content", (Serializable) blob);
        } catch (IOException e) {
            throw new NuxeoException(e);
        }
    }

}
