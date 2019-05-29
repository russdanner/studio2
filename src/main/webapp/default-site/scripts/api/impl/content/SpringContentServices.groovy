/*
 * Copyright (C) 2007-2019 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package scripts.api.impl.content

import org.apache.commons.lang3.StringUtils
import org.craftercms.studio.api.v1.constant.StudioConstants
import scripts.api.impl.search.SolrSearch

import static org.craftercms.studio.api.v1.constant.StudioConstants.FILE_SEPARATOR
import static org.craftercms.studio.api.v1.constant.StudioConstants.PATTERN_ENVIRONMENT
import static org.craftercms.studio.api.v1.util.StudioConfiguration.CONFIGURATION_ENVIRONMENT_ACTIVE
import static org.craftercms.studio.api.v1.util.StudioConfiguration.CONFIGURATION_ENVIRONMENT_ACTIVE
import static org.craftercms.studio.api.v1.util.StudioConfiguration.CONFIGURATION_SITE_CONFIG_BASE_PATH
import static org.craftercms.studio.api.v1.util.StudioConfiguration.CONFIGURATION_SITE_MUTLI_ENVIRONMENT_CONFIG_BASE_PATH

/**
 * content services
 */
class SpringContentServices {

	static CONTENT_SERVICES_BEAN = "cstudioContentService"
	static ASSET_PROCESSING_SERVICE_BEAN = "studioAssetProcessingService"
    static STUDIO_CONFIGURATION_BEAN = "studioConfiguration"

	def context = null

	def SpringContentServices(context) {
		this.context = context
	}

	/**
	 * Write content
	 * @param site - the project ID
	 * @param path - the path to wrtie the content
	 * @param content - the content to write
	 */
	def writeContent(site, path, content){
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.writeContent(site, path, content)
	}

    /**
     * create folder
     * @param site - the project ID
     * @param path - the path to create the folder in
     * @param name - the folder name to create
     */
    def createFolder(site, path, name){
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.createFolder(site, path, name)
    }

    /**
	 * Write asset
	 * @param site - the project ID
	 * @param path - the path to wrtie the content
	 * @param content - the content to write
	 */
	def writeAsset(site, path, content){
		throw new Exception("NOT USED")
	}

	/**
	 * get the actual content at a given path
	 * @param site - the project ID
	 * @param path - the path of the content to get
	 */
	def getContent(site, path) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.getContentAsString(site, path)
	}

	/**
	 * get the actual content at a given path
	 * @param site - the project ID
	 * @param path - the path of the content to get
	 */
	def getContentAsStream(site, path) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.getContent(site, path)
	}

  	/**
  	 * check if content at path exits
  	 * @param site - the project ID
  	 * @param path - the path to check
  	 */
	def doesContentItemExist(site, path) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.contentExists(site, path)
	}

	/**
	 * get the tree of content items (metadata) beginning at a root
	 * @param site - the project ID
	 * @param rootPath - the path to root at
	 */
	def getContentItemTree(site, path, depth){
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.getContentItemTree(site, path, depth)
	}

	/**
	 * get the content item (metadata) at a specific path
	 * @param site - the project ID
	 * @param path - the path of the content item
	 */
	def getContentItem(site, path) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.getContentItem(site, path, 0)
	}

	/**
	 * get all the content dependencies for a given path
	 * @param site - the project ID
	 * @param path - the path of the item
	 * @param filters - filters to apply to the dependencies
	 */
	def getContentDependencies(site, path, filters) {

	}

	/**
	 * get content orders for a given path (usually used for navigation)
  	 * @param site - the project ID
  	 * @param path - the parent path containing the ordered objects
  	 */
	def getItemOrders(site, path) {
		def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
		return springBackedService.getItemOrders(site, path);
	}

	/**
	 * Get the next value in the sequence for an order at a given path
	 * @param site - the project ID
	 * @param path - the path of the parent
	 */
	def getNextOrderInSequence(site, path) {

	}

	/**
	 * set the order of a group of items
	 * @param site - the project ID
	 * @param path - the list of paths for the items
	 * @param orders - the orders object
	 */
	def orderItems(site, path, orders) {

	}

	/**
	 * unlock a given item
	 * @param site - the project ID
	 * @param path - the path of the item to unlock
	 */
	def unlockContentItem(site, path) {
		def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
		return springBackedService.unLockContent(site, path)
	}

	/**
	 * get the version history for an item
	 * @param site - the project ID
	 * @param path - the path of the item
	 */
	def getContentItemVersionHistory(site, path) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.getContentItemVersionHistory(site, path)
	}

	/**
	 *  Get the content for a specific version
	 * @param site - the project ID
	 * @param path - the path of the item to retrieve
	 * @param version - old version ID to base to version on
	 */
	def getContentVersionAtPath(site, path, version) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.getContentVersionAsString(site, path, version)
	}

	/**
	 * revert a version (create a new version based on an old version)
	 * @param site - the project ID
	 * @param path - the path of the item to "revert"
	 * @param version - old version ID to base to version on
	 */
	def revertContentItem(site, path, version, major, comment){
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.revertContentItem(site, path, version, major, comment)
	}

	/**
	 * search the repository
	 * @param site - the project ID
	 * @param keywords - keywords
	 * @param filters - Filters object (document based)
	 * @param sort - sort object
	 * @param page - page to start on
	 * @param resultsPerPage - items to return per page
	 */
	def search(site, keywords, searchParams, sort, page, resultsPerPage) {
		return SolrSearch.search(site, keywords, searchParams, sort, page, resultsPerPage, this.context);
	}

	def writeContent(site, path, fileName, contentType, input, createFolders, edit, unlock) {
		def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN);
		return springBackedService.writeContent(site, path, fileName, contentType, input, createFolders, edit, unlock);
	}

    def writeContentAndRename(site, oldPath, targetPath, fileName, contentType, input, createFolders, edit, unlock, createFolder) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN);
        springBackedService.writeContentAndRename(site, oldPath, targetPath, fileName, contentType, input, createFolders, edit, unlock, createFolder);
    }

	def getContentAtPath(site, path) {
		def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        def springBackedConfigurationBean = this.context.applicationContext.get(STUDIO_CONFIGURATION_BEAN)

        def configBasePath = springBackedConfigurationBean.getProperty(CONFIGURATION_SITE_CONFIG_BASE_PATH)
        def calculatedPath = path
        if (path.startsWith(configBasePath)) {
            if (!StringUtils.isEmpty(springBackedConfigurationBean.getProperty(CONFIGURATION_ENVIRONMENT_ACTIVE))) {
                System.out.println(1)
                calculatedPath = path.replace(configBasePath, springBackedConfigurationBean.getProperty(CONFIGURATION_SITE_MUTLI_ENVIRONMENT_CONFIG_BASE_PATH).replaceAll(PATTERN_ENVIRONMENT,springBackedConfigurationBean.getProperty(CONFIGURATION_ENVIRONMENT_ACTIVE)))
                if (!springBackedService.contentExists(site, calculatedPath )) {
                    calculatedPath = path
                    System.out.println(2)
                }
            }
        }
        System.out.println(calculatedPath)
		return springBackedService.getContent(site, calculatedPath)
	}

	def lockContent(site, path) {
		def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN);
		springBackedService.lockContent(site, path);
	}

	def writeContentAsset(site, path, fileName, content, isImage, allowedWidth, allowedHeight, allowLessSize, draft, unlock, systemAsset) {
		def springBackendService = this.context.applicationContext.get(ASSET_PROCESSING_SERVICE_BEAN);
		return springBackendService.processAsset(site, path, fileName, content, isImage, allowedWidth, allowedHeight, allowLessSize, draft, unlock, systemAsset);
	}

    def reorderItems(site, path, before, after) {
        def springBackendService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN);
        return springBackendService.reorderItems(site, path, before, after, "default");
    }

    /**
     * rename folder
     * @param site - the project ID
     * @param path - the folder path to rename
     * @param name - the new folder name
     */
    def renameFolder(site, path, name){
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.renameFolder(site, path, name)
    }

    def pushToRemote(siteId, remoteName, remoteBranch) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.pushToRemote(siteId, remoteName, remoteBranch)
    }

    def pullFromRemote(siteId, remoteName, remoteBranch) {
        def springBackedService = this.context.applicationContext.get(CONTENT_SERVICES_BEAN)
        return springBackedService.pullFromRemote(siteId, remoteName, remoteBranch)
    }
}
