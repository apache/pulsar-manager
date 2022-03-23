/**
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
 */
package org.apache.pulsar.manager.utils;
import com.google.common.collect.Maps;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.List;

import java.util.Map;
import java.util.concurrent.TimeoutException;
import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;

import org.opensaml.Configuration;
import org.opensaml.saml2.core.AuthnStatement;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.Response;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.Signature;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Data
public class SAMLParser {

    private static final Logger logger = LoggerFactory
            .getLogger(SAMLParser.class);

    private String certificateString;

    public SAMLParser(String certificate) {
        super();
        this.certificateString = certificate;
    }

    private org.opensaml.saml2.core.Response validateRequestTimeoutAndSignature(String saml) throws Exception {

        /*
         * bootstrap the opensaml stuff
         */
        org.opensaml.DefaultBootstrap.bootstrap();

        org.opensaml.saml2.core.Response samlResponse = (Response) unmarshall(saml);
        List<AuthnStatement> authnStatements = samlResponse.getAssertions().get(0).getAuthnStatements();

        for (AuthnStatement authnStatement : authnStatements) {

            DateTime samlCreationTime = authnStatement.getAuthnInstant();
            DateTime currentTime = new DateTime(DateTimeZone.UTC);
            Duration duration = new Duration(samlCreationTime, currentTime);

            Long requestDuration = duration.getStandardSeconds();

            /*
             * verify request time out in seconds
             */
            if (requestDuration > 60) {
                throw new TimeoutException("Saml Request is older than 60 seconds");
            }
        }

        Signature signature = samlResponse.getSignature();
        validateSignature(this.getCertificateString(), signature);

        return samlResponse;
    }

    /**
     * Unmarshall XML to POJOs (These POJOs will be OpenSAML objects.)
     *
     * @return The root OpenSAML object.
     */
    private XMLObject unmarshall(String samlResponse) throws Exception {

        BasicParserPool parser = new BasicParserPool();
        parser.setNamespaceAware(true);

        /*
         * string is passed as base64, decode it and then unmarshal the response
         */
        byte[] base64Decoded = Base64.decodeBase64(samlResponse);

        /*
         * uncomment following to see the SAML response received
         */

        StringReader reader = new StringReader(new String(base64Decoded));

        Document doc = parser.parse(reader);
        Element samlElement = doc.getDocumentElement();

        UnmarshallerFactory unmarshallerFactory = Configuration
                .getUnmarshallerFactory();
        Unmarshaller unmarshaller = unmarshallerFactory
                .getUnmarshaller(samlElement);
        if (unmarshaller == null) {

            logger.error("failed to unmarshal the saml response recieved");
            throw new Exception("Failed to unmarshal");
        }

        return unmarshaller.unmarshall(samlElement);
    }

    private void validateSignature(String idpCertificateString,
                                   Signature signature) throws CertificateException,
            NoSuchAlgorithmException, InvalidKeySpecException,
            ValidationException {

        byte[] decoded = org.opensaml.xml.util.Base64
                .decode(idpCertificateString);

        /*
         * generate certificate from certificate string
         */
        X509Certificate certificate = (X509Certificate) CertificateFactory
                .getInstance("X.509").generateCertificate(
                        new ByteArrayInputStream(decoded));

        /*
         * pull out the public key part of the certificate into a KeySpec
         */
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(certificate
                .getPublicKey().getEncoded());

        /*
         * generate public key from the public key part
         */
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        BasicX509Credential publicCredential = new BasicX509Credential();
        publicCredential.setPublicKey(publicKey);

        // create SignatureValidator
        org.opensaml.xml.signature.SignatureValidator signatureValidator = new SignatureValidator(
                publicCredential);

        /*
         * try to validate will throw ValidationException if signature is
         * invalid
         */
        signatureValidator.validate(signature);
    }

    private Map<String,String> getUserDetailsFromSAMLResponse(
            Response samlResponse) {
        Map<String,String> userDetails = Maps.newHashMap();
        NameID nameID = samlResponse.getAssertions()
                .get(0).getSubject().getNameID();
        userDetails.put("email",nameID.getValue());
        return userDetails;
    }

    public Map<String,String> getUserDetailsFromSAMLResponse(String samlResponse) throws Exception {
        Response saml = validateRequestTimeoutAndSignature(samlResponse);
        return getUserDetailsFromSAMLResponse(saml);
    }


}

