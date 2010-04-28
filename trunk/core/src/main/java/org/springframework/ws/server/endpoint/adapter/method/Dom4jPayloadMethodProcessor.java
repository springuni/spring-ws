/*
 * Copyright 2005-2010 the original author or authors.
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

package org.springframework.ws.server.endpoint.adapter.method;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;

import org.springframework.core.MethodParameter;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.DOMReader;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

/**
 * Implementation of {@link MethodArgumentResolver} and {@link MethodReturnValueHandler} that supports dom4j
 * {@linkplain Element elements}.
 *
 * @author Arjen Poutsma
 * @since 2.0
 */
public class Dom4jPayloadMethodProcessor extends AbstractPayloadMethodProcessor {

    @Override
    protected boolean supportsRequestPayloadParameter(MethodParameter parameter) {
        return supports(parameter);
    }

    @Override
    protected Object resolveRequestPayloadArgument(Source requestPayload, MethodParameter parameter)
            throws TransformerException {
        if (requestPayload instanceof DOMSource) {
            org.w3c.dom.Node node = ((DOMSource) requestPayload).getNode();
            if (node.getNodeType() == org.w3c.dom.Node.DOCUMENT_NODE) {
                DOMReader domReader = new DOMReader();
                Document document = domReader.read((org.w3c.dom.Document) node);
                return document.getRootElement();
            }
        }
        // we have no other option than to transform
        DocumentResult dom4jResult = new DocumentResult();
        transform(requestPayload, dom4jResult);
        return dom4jResult.getDocument().getRootElement();
    }

    @Override
    protected boolean supportsResponsePayloadReturnType(MethodParameter returnType) {
        return supports(returnType);
    }

    @Override
    protected Source createResponsePayload(MethodParameter returnType, Object returnValue) {
        Element returnedNode = (Element) returnValue;
        return new DocumentSource(returnedNode);
    }

    private boolean supports(MethodParameter parameter) {
        return Element.class.equals(parameter.getParameterType());
    }

}