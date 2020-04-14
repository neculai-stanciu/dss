/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.pades.extension.suite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.junit.jupiter.api.Test;

import eu.europa.esig.dss.crl.CRLBinary;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.MimeType;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.PAdESTimestampParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.pades.validation.PDFDocumentValidator;
import eu.europa.esig.dss.pdf.PdfDssDict;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.test.AbstractPkiFactoryTestValidation;
import eu.europa.esig.dss.validation.AdvancedSignature;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.validationreport.jaxb.SADSSType;
import eu.europa.esig.validationreport.jaxb.SAFilterType;
import eu.europa.esig.validationreport.jaxb.SASubFilterType;
import eu.europa.esig.validationreport.jaxb.SAVRIType;

public class DSS1523Test extends AbstractPkiFactoryTestValidation<PAdESSignatureParameters, PAdESTimestampParameters> {

	@Test
	public void validation() {
		// <</Type /DSS/Certs [20 0 R]/CRLs [21 0 R]/OCSPs [22 0 R]>>
		DSSDocument doc = new InMemoryDocument(DSS1523Test.class.getResourceAsStream("/validation/PAdES-LTA.pdf"), "PAdES-LTA.pdf", MimeType.PDF);
		
		verify(doc);
		
		PDFDocumentValidator validator = new PDFDocumentValidator(doc);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		List<AdvancedSignature> signatures = validator.getSignatures();
		assertEquals(1, signatures.size());
		
		List<PdfDssDict> dssDictionaries = validator.getDssDictionaries();
		assertEquals(1, dssDictionaries.size());
		PdfDssDict pdfDssDict = dssDictionaries.get(0);

		Map<Long, CertificateToken> certificateMap = pdfDssDict.getCERTs();
		assertEquals(1, certificateMap.size());
		assertNotNull(certificateMap.get(20L));

		Map<Long, BasicOCSPResp> ocspMap = pdfDssDict.getOCSPs();
		assertEquals(1, ocspMap.size());
		assertNotNull(ocspMap.get(22L));

		Map<Long, CRLBinary> crlMap = pdfDssDict.getCRLs();
		assertEquals(1, crlMap.size());
		assertNotNull(crlMap.get(21L));
	}

	@Test
	public void extension() throws IOException {
		DSSDocument doc = new InMemoryDocument(DSS1523Test.class.getResourceAsStream("/validation/PAdES-LTA.pdf"), "PAdES-LTA.pdf", MimeType.PDF);
		CertificateVerifier certificateVerifier = getCompleteCertificateVerifier();
		certificateVerifier.getTrustedCertSources().get(0).addCertificate(DSSUtils.loadCertificateFromBase64EncodedString(
				"MIID6TCCAtGgAwIBAgICEsMwDQYJKoZIhvcNAQELBQAwRDELMAkGA1UEBhMCTFUxFjAUBgNVBAoTDUx1eFRydXN0IHMuYS4xHTAbBgNVBAMTFEx1eFRydXN0IEdsb2JhbCBSb290MB4XDTE0MDUyODEzMDkxOFoXDTIwMDUyODEzMDkxOFowTjELMAkGA1UEBhMCTFUxFjAUBgNVBAoTDUx1eFRydXN0IFMuQS4xJzAlBgNVBAMTHkx1eFRydXN0IEdsb2JhbCBRdWFsaWZpZWQgQ0EgMjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMRaetIHqOXl0cMdtVuB3StPlDTeneHMgenp4W/5u4GimFETER3Msj9C6JeAg9H63Az/JpPdGjVWpREZ4goR29Y2Ys1S0kyW9ONFKwyDHm2tU6nyTnx9hVNDDkA4DMNZuf3UIo3J7xaTae1u5ALlIh+g4aPeiYtB4XZlJPGvy3mmQ6020jqQWgeCRVCl91p1HLu6oPZ6xp+wy2qWzhzn7jo81Y8S2g+cD/qen2jphIae8PRLtEjuMLREcu/Rt03PDfxxi2usnSb1djQImYEL/R6I7VgK+UkNXYy+vasXOqWclZ7oMeA6iMt4WkjEKWsKf60eFlVW8J66vA//IdY7IUsCAwEAAaOB2jCB1zAPBgNVHRMECDAGAQH/AgEAMEMGA1UdIAQ8MDowOAYIK4ErAQEBCgMwLDAqBggrBgEFBQcCARYeaHR0cHM6Ly9yZXBvc2l0b3J5Lmx1eHRydXN0Lmx1MAsGA1UdDwQEAwIBBjAfBgNVHSMEGDAWgBQXFYWJCS8kh28/HRvk8pZ5g0gTzjAyBgNVHR8EKzApMCegJaAjhiFodHRwOi8vY3JsLmx1eHRydXN0Lmx1L0xUR1JDQS5jcmwwHQYDVR0OBBYEFO+Wv31lOlW00nD4DOxK4vMnBppSMA0GCSqGSIb3DQEBCwUAA4IBAQCBkOXfYtTOMb853+Oq49NENBFTcjqohYLyvc/w8gisbbe8OPdRfLam+PAkYKfyoy77R78E8Ypg5R9ASxqFt5lEgFADE022+lqs5GNpOVIoit+WCtC4k19SkyOvypqZZApEEfc1VxadqhwwsdJRTt+aVhuItuUo4GGNGTub+y/bs6IGUpNnuWibrqevc2jaG9YYQPGfu9WUtj5znQ+0VdH6wPXfumhHag4Ipl8aBh5kXYEDFpgINdIfbBNq3ULKHNdzdCZj5bJZVxxbW1qC0BQ1UD1o2KoiLQy9G15UErAQHx1BbVGA+eSbZe7Fpy1Va3K0z76usMSSOBf7YlJ7e0Hq"));
		certificateVerifier.getTrustedCertSources().get(0).addCertificate(DSSUtils.loadCertificateFromBase64EncodedString(
				"MIID/zCCAuegAwIBAgIQP8umE0YUpE/yhLiMgaeopDANBgkqhkiG9w0BAQsFADB3MQswCQYDVQQGEwJGUjEgMB4GA1UEChMXQ3J5cHRvbG9nIEludGVybmF0aW9uYWwxHDAaBgNVBAsTEzAwMDIgNDM5MTI5MTY0MDAwMjYxKDAmBgNVBAMTH1VuaXZlcnNpZ24gVGltZXN0YW1waW5nIENBIDIwMTUwHhcNMTUwMTI5MTQwMzE1WhcNMjUwMTI5MTQwMzE1WjB3MQswCQYDVQQGEwJGUjEgMB4GA1UEChMXQ3J5cHRvbG9nIEludGVybmF0aW9uYWwxHDAaBgNVBAsTEzAwMDIgNDM5MTI5MTY0MDAwMjYxKDAmBgNVBAMTH1VuaXZlcnNpZ24gVGltZXN0YW1waW5nIENBIDIwMTUwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDYc1VJ69W70ojewtKbCLZ+P8bDAVJ1qujzgIZEvm15GYX7Jp+Hl9rwxBdswSZ8S5A/x+0j6YMOHH0Z+iGl649+0GGX1gdAuovQKShsvLSzD/waINxkXXTVXpAW3V4dnCgcb3qaV/pO9NTk/sdRJxM8lUtWuD7TEAfLzz7Ucl6gBjDTA0Gz+AtUkNWPcofCWuDfiSDOOpyKwSxovde6SRwHdTXXIiC2Dphffjrr74MvLb0La5JAUwmJLIH42j/frgZeWk148wLMwBW+lvrIJtPz7eHNtTlNfQLrmmJHW4l+yvTsdJJDs7QYtfzBTNg1zqV8eo/hHxFTFJ8/T9wTmENJAgMBAAGjgYYwgYMwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAQYwQQYDVR0gBDowODA2BgorBgEEAftLBQEBMCgwJgYIKwYBBQUHAgEWGmh0dHA6Ly9kb2NzLnVuaXZlcnNpZ24uZXUvMB0GA1UdDgQWBBT6Te1XO70/85Ezmgs5pH9dEt0HRjANBgkqhkiG9w0BAQsFAAOCAQEAc7ud6793wgdjR8Xc1L47ufdVTamI5SHfOThtROfn8JL0HuNHKdRgv6COpdjtt6RwQEUUX/km7Q+Pn+A2gA/XoPfqD0iMfP63kMMyqgalEPRv+lXbFw3GSC9BQ9s2FL7ScvSuPm7VDZhpYN5xN6H72y4z7BgsDVNhkMu5AiWwbaWF+BHzZeiuvYHX0z/OgY2oH0hluovuRAanQd4dOa73bbZhTJPFUzkgeIzOiuYS421IiAqsjkFwu3+k4dMDqYfDKUSITbMymkRDszR0WGNzIIy2NsTBcKYCHmbIV9S+165i8YjekraBjTTSbpfbty87A1S53CzA2EN1qnmQPwqFfg=="));

		PAdESService service = new PAdESService(certificateVerifier);
		service.setTspSource(getGoodTsa());

		PAdESSignatureParameters parameters = new PAdESSignatureParameters();
		parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_LTA);
		DSSDocument extendDocument = service.extendDocument(doc, parameters);
		
		verify(extendDocument);

		// extendDocument.save("target/extended.pdf");

		PDFDocumentValidator validator = new PDFDocumentValidator(extendDocument);
		validator.setCertificateVerifier(new CommonCertificateVerifier());
		List<AdvancedSignature> signatures = validator.getSignatures();
		assertEquals(1, signatures.size());
		
		List<PdfDssDict> dssDictionaries = validator.getDssDictionaries();
		assertEquals(2, dssDictionaries.size());
		
		PdfDssDict pdfDssDict = dssDictionaries.get(1); // the last dictionary

		Map<Long, CertificateToken> certificateMap = pdfDssDict.getCERTs();
		CertificateToken certificateToken = certificateMap.get(20L);
		assertNotNull(certificateToken);
		int certAppearence = 0;
		for (CertificateToken certToken : certificateMap.values()) {
			if (certificateToken.equals(certToken)) {
				++certAppearence;
			}
		}
		assertEquals(1, certAppearence);;

		Map<Long, BasicOCSPResp> ocspMap = pdfDssDict.getOCSPs();
		BasicOCSPResp basicOCSPResp = ocspMap.get(22L);
		assertNotNull(basicOCSPResp);
		int ocspAppearence = 0;
		for (BasicOCSPResp ocsp : ocspMap.values()) {
			if (basicOCSPResp.equals(ocsp)) {
				++ocspAppearence;
			}
		}
		assertEquals(1, ocspAppearence);

		Map<Long, CRLBinary> crlMap = pdfDssDict.getCRLs();
		CRLBinary crlBinary = crlMap.get(21L);
		assertNotNull(crlBinary);
		int crlAppearence = 0;
		for (CRLBinary crl : crlMap.values()) {
			if (crlBinary.equals(crl)) {
				++crlAppearence;
			}
		}
		assertEquals(1, crlAppearence);
	}
	
	@Override
	protected void checkSigningCertificateValue(DiagnosticData diagnosticData) {
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		assertTrue(signature.isDigestValuePresent());
		assertTrue(signature.isDigestValueMatch());
		assertTrue(signature.isIssuerSerialMatch());
	}
	
	@Override
	protected void checkSignatureLevel(DiagnosticData diagnosticData) {
		assertFalse(diagnosticData.isTLevelTechnicallyValid(diagnosticData.getFirstSignatureId()));
		assertTrue(diagnosticData.isALevelTechnicallyValid(diagnosticData.getFirstSignatureId()));
	}
	
	@Override
	protected void checkSignatureIdentifier(DiagnosticData diagnosticData) {
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		assertNotNull(signature.getSignatureValue());
	}
	
	@Override
	protected void validateETSIDSSType(SADSSType dss) {
		assertNotNull(dss);
	}
	
	@Override
	protected void validateETSIVRIType(SAVRIType vri) {
		assertNotNull(vri);
	}
	
	@Override
	protected void validateETSIFilter(SAFilterType filterType) {
		assertNotNull(filterType);
	}
	
	@Override
	protected void validateETSISubFilter(SASubFilterType subFilterType) {
		assertNotNull(subFilterType);
	}
	
	@Override
	protected void checkReportsSignatureIdentifier(Reports reports) {
		// do nothing
	}

	@Override
	protected String getSigningAlias() {
		return null;
	}

}
