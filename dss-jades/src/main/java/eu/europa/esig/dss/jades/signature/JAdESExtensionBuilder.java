package eu.europa.esig.dss.jades.signature;

import eu.europa.esig.dss.enumerations.JWSSerializationType;
import eu.europa.esig.dss.jades.DSSJsonUtils;
import eu.europa.esig.dss.jades.JWSJsonSerializationObject;
import eu.europa.esig.dss.jades.validation.JWS;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.utils.Utils;

import java.util.List;

/**
 * The abstract class allowing the signature extension
 */
public abstract class JAdESExtensionBuilder {

	/**
	 * Checks if the type of etsiU components is consistent
	 *
	 * @param jws {@link JWS} to check
	 * @param isBase64UrlEtsiUComponents if the new component shall be base64url encoded
	 */
	protected void assertEtsiUComponentsConsistent(JWS jws, boolean isBase64UrlEtsiUComponents) {
		List<Object> etsiU = DSSJsonUtils.getEtsiU(jws);
		if (Utils.isCollectionNotEmpty(etsiU)) {
			if (!DSSJsonUtils.checkComponentsUnicity(etsiU)) {
				throw new DSSException("Extension is not possible, because components of the 'etsiU' header have "
						+ "not common format! Shall be all Strings or Objects.");
			}
			if (DSSJsonUtils.areAllBase64UrlComponents(etsiU) != isBase64UrlEtsiUComponents) {
				throw new DSSException(String.format("Extension is not possible! The encoding of 'etsiU' "
						+ "components shall match! Use jadesSignatureParameters.setBase64UrlEncodedEtsiUComponents(%s)",
						!isBase64UrlEtsiUComponents));
			}
		}
	}

	/**
	 * Parses the provided {@code document} to {@link JWSJsonSerializationObject}
	 * Throws an exception if the document cannot be extended
	 *
	 * @param document {@link DSSDocument} original document
	 * @return {@link JWSJsonSerializationObject}
	 */
	protected JWSJsonSerializationObject toJWSJsonSerializationObjectToExtend(DSSDocument document) {
		JWSJsonSerializationObject jwsJsonSerializationObject = DSSJsonUtils.toJWSJsonSerializationObject(document);
		if (jwsJsonSerializationObject == null) {
			throw new DSSException("The provided document is not a valid JAdES signature! Unable to extend.");
		}
		if (Utils.isCollectionEmpty(jwsJsonSerializationObject.getSignatures())) {
			throw new DSSException("There is no signature to extend!");
		}
		return jwsJsonSerializationObject;
	}

	/**
	 * Checks if the given signature document type is allowed for the extension
	 *
	 * @param jwsSerializationType {@link JWSSerializationType} to check
	 */
	protected void assertIsJSONSerializationType(JWSSerializationType jwsSerializationType) {
		if (!JWSSerializationType.JSON_SERIALIZATION.equals(jwsSerializationType) &&
				!JWSSerializationType.FLATTENED_JSON_SERIALIZATION.equals(jwsSerializationType)) {
			throw new DSSException("The extended signature shall have JSON Serialization (or Flattened) type! " +
					"Use JWSConverter to convert the signature.");
		}
	}

}
