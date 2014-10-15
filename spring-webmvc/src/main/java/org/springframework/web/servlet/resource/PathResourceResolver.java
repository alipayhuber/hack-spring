/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.web.servlet.resource;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;

/**
 * A simple {@code ResourceResolver} that tries to find a resource under the given
 * locations matching to the request path.
 *
 * <p>This resolver does not delegate to the {@code ResourceResolverChain} and is
 * expected to be configured at the end in a chain of resolvers.
 *
 * @author Jeremy Grelle
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 4.1
 */
public class PathResourceResolver extends AbstractResourceResolver {


	@Override
	protected Resource resolveResourceInternal(HttpServletRequest request, String requestPath,
			List<? extends Resource> locations, ResourceResolverChain chain) {

		return getResource(requestPath, locations);
	}

	@Override
	protected String resolveUrlPathInternal(String resourcePath, List<? extends Resource> locations,
			ResourceResolverChain chain) {

		return (getResource(resourcePath, locations) != null ? resourcePath : null);
	}

	private Resource getResource(String resourcePath, List<? extends Resource> locations) {
		for (Resource location : locations) {
			try {
				if (logger.isTraceEnabled()) {
					logger.trace("Checking location=[" + location + "]");
				}
				Resource resource = getResource(resourcePath, location);
				if (resource != null) {
					if (logger.isTraceEnabled()) {
						logger.trace("Found match");
					}
					return resource;
				}
				else if (logger.isTraceEnabled()) {
					logger.trace("No match");
				}
			}
			catch (IOException ex) {
				logger.trace("Failure checking for relative resource. Trying next location.", ex);
			}
		}
		return null;
	}

	/**
	 * Find the resource under the given location.
	 * <p>The default implementation checks if there is a readable
	 * {@code Resource} for the given path relative to the location.
	 * @param resourcePath the path to the resource
	 * @param location the location to check
	 * @return the resource or {@code null}
	 */
	protected Resource getResource(String resourcePath, Resource location) throws IOException {
		Resource resource = location.createRelative(resourcePath);
		return (resource.exists() && resource.isReadable() ? resource : null);
	}

}
