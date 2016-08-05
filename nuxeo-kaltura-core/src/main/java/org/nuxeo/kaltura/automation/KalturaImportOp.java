/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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

package org.nuxeo.kaltura.automation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.kaltura.service.KalturaImporter;


@Operation(
        id = KalturaImportOp.ID,
        category = Constants.CAT_SERVICES,
        label = "Import Document from Kaltura",
        description = "Import Documents From Kaltura in the input folder.")
public class KalturaImportOp {

    public static final String ID = "KalturaImportOp";

    @Context
    protected KalturaImporter service;

    @OperationMethod
    public DocumentModel run(DocumentModel doc) throws Exception {
        service.schedule(doc);
        return doc;
    }
}