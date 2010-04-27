/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.xml.namespace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.springframework.util.Assert;

/**
 * Simple <code>javax.xml.namespace.NamespaceContext</code> implementation. Follows the standard
 * <code>NamespaceContext</code> contract, and is loadable via a <code>java.util.Map</code> or
 * <code>java.util.Properties</code> object
 *
 * @author Arjen Poutsma
 * @since 1.0.0
 */
public class SimpleNamespaceContext implements NamespaceContext {

    private Map prefixToNamespaceUri = new HashMap();

    /** Maps a <code>String</code> namespaceUri to a <code>List</code> of prefixes */
    private Map namespaceUriToPrefixes = new HashMap();

    private String defaultNamespaceUri = "";

    public String getNamespaceURI(String prefix) {
        Assert.notNull(prefix, "prefix is null");
        if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
            return XMLConstants.XML_NS_URI;
        }
        else if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        }
        else if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
            return defaultNamespaceUri;
        }
        else if (prefixToNamespaceUri.containsKey(prefix)) {
            return (String) prefixToNamespaceUri.get(prefix);
        }
        return "";
    }

    public String getPrefix(String namespaceUri) {
        List prefixes = getPrefixesInternal(namespaceUri);
        return prefixes.isEmpty() ? null : (String) prefixes.get(0);
    }

    public Iterator getPrefixes(String namespaceUri) {
        return getPrefixesInternal(namespaceUri).iterator();
    }

    /**
     * Sets the bindings for this namespace context. The supplied map must consist of string key value pairs.
     *
     * @param bindings the bindings
     */
    public void setBindings(Map bindings) {
        for (Iterator iterator = bindings.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            bindNamespaceUri((String) entry.getKey(), (String) entry.getValue());
        }
    }

    /**
     * Binds the given namespace as default namespace.
     *
     * @param namespaceUri the namespace uri
     */
    public void bindDefaultNamespaceUri(String namespaceUri) {
        bindNamespaceUri(XMLConstants.DEFAULT_NS_PREFIX, namespaceUri);
    }

    /**
     * Binds the given prefix to the given namespace.
     *
     * @param prefix       the namespace prefix
     * @param namespaceUri the namespace uri
     */
    public void bindNamespaceUri(String prefix, String namespaceUri) {
        Assert.notNull(prefix, "No prefix given");
        Assert.notNull(namespaceUri, "No namespaceUri given");
        if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
            defaultNamespaceUri = namespaceUri;
        } else if (XMLConstants.XML_NS_PREFIX.equals(prefix)) {
            Assert.isTrue(XMLConstants.XML_NS_URI.equals(namespaceUri), "Prefix \"" + prefix +
                    "\" bound to namespace \"" + namespaceUri + "\" (should be \"" + XMLConstants.XML_NS_URI + "\")");
        } else if (XMLConstants.XMLNS_ATTRIBUTE.equals(prefix)) {
            Assert.isTrue(XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceUri), "Prefix \"" + prefix +
                    "\" bound to namespace \"" + namespaceUri + "\" (should be \"" +
                    XMLConstants.XMLNS_ATTRIBUTE_NS_URI + "\")");
        }
        else {
            prefixToNamespaceUri.put(prefix, namespaceUri);
            getPrefixesInternal(namespaceUri).add(prefix);
        }
    }

    /** Removes all declared prefixes. */
    public void clear() {
        defaultNamespaceUri = "";
        prefixToNamespaceUri.clear();
        namespaceUriToPrefixes.clear();
    }

    /**
     * Returns all declared prefixes.
     *
     * @return the declared prefixes
     */
    public Iterator getBoundPrefixes() {
        return prefixToNamespaceUri.keySet().iterator();
    }

    private List getPrefixesInternal(String namespaceUri) {
        if (defaultNamespaceUri.equals(namespaceUri)) {
            return Collections.singletonList(XMLConstants.DEFAULT_NS_PREFIX);
        }
        else if (XMLConstants.XML_NS_URI.equals(namespaceUri)) {
            return Collections.singletonList(XMLConstants.XML_NS_PREFIX);
        }
        else if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(namespaceUri)) {
            return Collections.singletonList(XMLConstants.XMLNS_ATTRIBUTE);
        }
        else {
            List list = (List) namespaceUriToPrefixes.get(namespaceUri);
            if (list == null) {
                list = new ArrayList();
                namespaceUriToPrefixes.put(namespaceUri, list);
            }
            return list;
        }
    }

    /**
     * Removes the given prefix from this context.
     *
     * @param prefix the prefix to be removed
     */
    public void removeBinding(String prefix) {
        if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
            defaultNamespaceUri = "";
        }
        else {
            String namespaceUri = (String) prefixToNamespaceUri.get(prefix);
            List prefixes = getPrefixesInternal(namespaceUri);
            prefixes.remove(prefix);
        }
    }

    public boolean hasBinding(String prefix) {
        if (XMLConstants.DEFAULT_NS_PREFIX.equals(prefix)) {
            return !defaultNamespaceUri.equals("");
        }
        else {
            return prefixToNamespaceUri.containsKey(prefix);
        }
    }
}