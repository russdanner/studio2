
/*
 * Crafter Studio Web-content authoring solution
 * Copyright (C) 2007-2016 Crafter Software Corporation.
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

/**
 * @author Dejan Brkic
 */
package scripts.api.impl.content;

/**
 * content type services
 */
class SpringContentTypeServices {

    static CONTENT_TYPE_SERVICES_BEAN = "cstudioContentTypeService"

    def context = null

    def SpringContentTypeServices(context) {
        this.context = context
    }

    def getContentTypes(site, searchable) {
        def springBackedService = this.context.applicationContext.get(CONTENT_TYPE_SERVICES_BEAN)
        return springBackedService.getAllContentTypes(site, searchable)
    }

    def getContentType(site, type) {
        def springBackedService = this.context.applicationContext.get(CONTENT_TYPE_SERVICES_BEAN)
        return springBackedService.getContentType(site, type)
    }

    def changeContentType(site, path, type) {
        def springBackedService = this.context.applicationContext.get(CONTENT_TYPE_SERVICES_BEAN)
        return springBackedService.changeContentType(site, path, type)
    }

    def getContentTypeByPath(site, path) {
        def springBackedService = this.context.applicationContext.get(CONTENT_TYPE_SERVICES_BEAN)
        return springBackedService.getContentTypeByRelativePath(site, path)
    }

    def getAllowedContentTypesForPath(site, path) {
        def springBackedService = this.context.applicationContext.get(CONTENT_TYPE_SERVICES_BEAN)
        return springBackedService.getAllowedContentTypesForPath(site, path);
    }
}
