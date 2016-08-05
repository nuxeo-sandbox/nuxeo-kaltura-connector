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
 *     Michael Vachette
 */

package org.nuxeo.kaltura.service;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("configuration")
public class KalturaAccountDescriptor {

    @XNode("basePath")
    protected String basePath;

    @XNode("adminSecret")
    protected String adminSecret;

    @XNode("username")
    protected String username;

    @XNode("partnerId")
    protected String partnerId;

    public String getBasePath() {
        return basePath != null ? basePath : System.getProperty("KbaseUrl");
    }

    public String getAdminSecret() {
        return adminSecret != null ? adminSecret : System.getProperty("KadminSecret");
    }

    public String getUsername() {
        return username != null ? username : System.getProperty("Kusername");
    }

    public String getPartnerId() {
        return partnerId != null ? partnerId: System.getProperty("KpartnerId");
    }
}
