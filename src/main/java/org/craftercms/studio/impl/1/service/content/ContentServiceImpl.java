/*******************************************************************************
 * Crafter Studio Web-content authoring solution
 *     Copyright (C) 2007-2013 Crafter Software Corporation.
 * 
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package org.craftercms.cstudio.impl.service.content;

import java.net.*;
import java.io.*;
import java.io.InputStream;
import org.dom4j.io.SAXReader;
import java.lang.reflect.Method;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentException;

import org.apache.commons.io.IOUtils;

import org.craftercms.cstudio.api.log.*;
import org.craftercms.cstudio.api.service.content.*;
import org.craftercms.cstudio.api.to.ContentItemTO;
import org.craftercms.cstudio.api.repository.RepositoryItem;
import org.craftercms.cstudio.api.repository.ContentRepository;
import org.craftercms.cstudio.api.to.VersionTO;

/**
 * Content Services that other services may use
 * @author russdanner
 */
public class ContentServiceImpl implements ContentService {

    protected static final String MSG_ERROR_IO_CLOSE_FAILED = "err_io_closed_failed";

    private static final Logger logger = LoggerFactory.getLogger(ContentServiceImpl.class);

    /**
     * @return true if site has content object at path
     */
    public boolean contentExists(String site, String path) {
        return this._contentRepository.contentExists(expandRelativeSitePath(site, path));
    }

    /**
     * get document from wcm content
     *
     * @param path
     * @return document
     * @throws ServiceException
     */
    public InputStream getContent(String path) {
       return this._contentRepository.getContent(path);
    }

   /**
     * get document from wcm content
     *
     * @param path
     * @pram site
     * @return document
     * @throws ServiceException
     */
    public InputStream getContent(String site, String path) {
       return this._contentRepository.getContent(expandRelativeSitePath(site, path));
    }

    /**
     * get document from wcm content
     * @param path
     * @return document
     * @throws ServiceException
     */
    public String getContentAsString(String path)  {
        String content = null;

        try {
            content = IOUtils.toString(_contentRepository.getContent(path));
        }
        catch(Exception err) {
            logger.error("Failed to get content as string for path '{0}'", err, path);
        }

        return content;
    }

    /**
     * get document from wcm content
     *
     * @param path
     * @return document
     * @throws ServiceException
     */
    public Document getContentAsDocument(String path)
    throws DocumentException {
        Document retDocument = null;
        InputStream is = this.getContent(path);

        if(is != null) {
            try {
                SAXReader saxReader = new SAXReader();          
                retDocument = saxReader.read(is);
            } 
            finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } 
                catch (IOException err) {
                    logger.error(MSG_ERROR_IO_CLOSE_FAILED, err, path);
                }
            }       
        }

        return retDocument;
    }


    /**
     * write content
     * @param path path to content
     * @param content stream of content to write
     */
    public void writeContent(String path, InputStream content) {

    }

    /**
     * write content
     * @param path path to content
     * @param site 
     * @param content stream of content to write
     */
    public void writeContent(String site, String path, InputStream content){
        writeContent(expandRelativeSitePath(site, path), content);
    }

    /**
     * get a content item for a given site and path
     * @param site - the site
     * @param path = the path of content to get
     */
    public ContentItemTO getContentItem(String site, String path) {
        ContentItemTO item = null;

        try {
            // this may be faster to get evenything from on of the other services?
            // the idea heare is that repo does not know enough to get an item,
            // this requires either a different servivice/subsystem or a combination of them
            if(path.endsWith(".xml")) {
                Document contentDoc = this.getContentAsDocument(expandRelativeSitePath(site, path));
                if(contentDoc != null) {  
                    item = new ContentItemTO();

                    Element rootElement = contentDoc.getRootElement();
                    item.internalName = rootElement.valueOf("internal-name");
                    item.contentType = rootElement.valueOf("content-type"); 
                    item.disabled = ( (rootElement.valueOf("disabled") != null) && rootElement.valueOf("disabled").equals("true") ); 
                    item.floating = ( (rootElement.valueOf("placeInNav") != null) && rootElement.valueOf("placeInNav").equals("true") ); 
                    item.hideInAuthoring = ( (rootElement.valueOf("hideInAuthoring") != null) && rootElement.valueOf("hideInAuthoring").equals("true") ); 

                    item.uri = path;
                    item.path = path.substring(0, path.lastIndexOf("/")-1);
                    item.name = path.substring(path.lastIndexOf("/")+1);
                    item.page = (item.contentType.indexOf("/page") != -1);
                    item.previewable = item.page;
                    item.component = (item.contentType.indexOf("/component") != -1);
                    item.document = false;
                    item.asset = (item.component == false && item.page == false);
                    item.browserUri = (item.page) ? path.replace("/site/website", "").replace(".xml", "") : null;

                    // populate with workflow states and other metadata

                    item.isNew = true;
                    item.submitted = false;
                    item.scheduled = false;
                    item.deleted = false;
                    item.submittedForDeletion = false;
                    item.inProgress = true;
                    item.live = false;

                    item.lockOwner = "";
                    item.user = "";
                    item.userFirstName = "";
                    item.userLastName = "";
                    item.nodeRef = "";
                    item.metaDescription = ""; 

                    // duplicate properties
                    item.isDisabled = item.disabled;
                    item.isInProgress = item.inProgress;
                    item.isLive = item.live;
                    item.isSubmittedForDeletion = item.submittedForDeletion;
                    item.isScheduled = item.scheduled;
                    item.isNavigation = item.navigation;
                    item.isDeleted = item.deleted;
                    item.isSubmitted = item.submitted;
                    item.isFloating = item.floating;
                    item.isPage = item.page;
                    item.isPreviewable = item.previewable;
                    item.isComponent = item.component;
                    item.isDocument = item.document;
                    item.isAsset = item.asset;
                }
                else {
                     logger.error("no xml document could be loaded for path '{0}'", path);    
                }
            }
            else {
                if (this.contentExists(site, path)) {
                    item = new ContentItemTO();
                    item.uri = path;
                    item.path = path.substring(0, path.lastIndexOf("/")-1);
                    item.name = path.substring(path.lastIndexOf("/")+1);
                    item.isAsset = true;

                    // populate with workflow states and other metadata
                    item.isNew = true;
                    item.isSubmitted = false;
                    item.isScheduled = false;
                    item.isNavigation = false; 
                    item.isDeleted = false;
                    item.isInProgress = true;
                }                 
            }
                
        }
        catch(Exception err) {
            logger.error("error constructing item for object at path '{0}'", err, path);            
        }

        return item;
    }

    /**
     * get the tree of content items (metadata) beginning at a root
     * @param site - the project ID
     * @param path - the path to root at
     */
    public ContentItemTO[] getContentItemTree(String site, String path) {
        ContentItemTO[] children = new ContentItemTO[0];

        RepositoryItem[] childRepoItems = _contentRepository.getContentChildren(expandRelativeSitePath(site, path));

        children = new ContentItemTO[childRepoItems.length];
        for(int i=0; i<childRepoItems.length; i++) {
            RepositoryItem repoItem = childRepoItems[i];
            ContentItemTO contentItem = getContentItem(site, repoItem.path);
            children[i] = contentItem;
        }

        return children;
    }

    /** 
     * get the version history for an item
     * @param site - the project ID
     * @param path - the path of the item 
     */
    public VersionTO[] getContentItemVersionHistory(String site, String path) {
        return _contentRepository.getContentVersionHistory(expandRelativeSitePath(site, path));
    }

    /** 
     * revert a version (create a new version based on an old version)
     * @param site - the project ID
     * @param path - the path of the item to "revert"
     * @param version - old version ID to base to version on
     */
    public boolean revertContentItem(String site, String path, String version, boolean major, String comment) {
        boolean success = false;

        success = _contentRepository.revertContent(expandRelativeSitePath(site, path), version, major, comment);

        if(success) {
            // publish item udated event or push to preview
        }

        return success;
    }

    /**
     * take a path like /sites/website/index.xml and root it properly with a fully expanded repo path
     */
    protected String expandRelativeSitePath(String site, String relativePath) {
        return "/wem-projects/" + site + "/" + site + "/work-area" + relativePath;
    }

    private ContentRepository _contentRepository;
    public ContentRepository getContentRepository() { return _contentRepository; }
    public void setContentRepository(ContentRepository contentRepository) { this._contentRepository = contentRepository; }
}
