package eu.europa.esig.dss.jaxb.simplereport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import eu.europa.esig.dss.jaxb.parsers.MarshallerBuilder;

public class SimpleReportFacade {

	public static SimpleReportFacade newFacade() {
		return new SimpleReportFacade();
	}

	public String marshall(XmlSimpleReport simpleReport, boolean validate) throws JAXBException, IOException, SAXException {
		MarshallerBuilder marshallerBuilder = new MarshallerBuilder(SimpleReportXmlDefiner.getJAXBContext(), SimpleReportXmlDefiner.getSchema());
		marshallerBuilder.setIndent(true);
		marshallerBuilder.setValidate(validate);
		Marshaller marshaller = marshallerBuilder.buildMarshaller();

		try (StringWriter writer = new StringWriter()) {
			marshaller.marshal(SimpleReportXmlDefiner.OBJECT_FACTORY.createSimpleReport(simpleReport), writer);
			return writer.toString();
		}
	}

	public XmlSimpleReport unmarshall(File file) throws JAXBException, XMLStreamException, IOException, SAXException {
		try (FileInputStream fis = new FileInputStream(file)) {
			return unmarshall(new FileInputStream(file));
		}
	}

	@SuppressWarnings("unchecked")
	public XmlSimpleReport unmarshall(InputStream is) throws JAXBException, XMLStreamException, IOException, SAXException {

		MarshallerBuilder builder = new MarshallerBuilder(SimpleReportXmlDefiner.getJAXBContext(), SimpleReportXmlDefiner.getSchema());
		builder.setValidate(true);
		Unmarshaller unmarshaller = builder.buildUnmarshaller();

		JAXBElement<XmlSimpleReport> unmarshal = (JAXBElement<XmlSimpleReport>) unmarshaller.unmarshal(avoidXXE(is));
		return unmarshal.getValue();
	}

	private XMLStreamReader avoidXXE(InputStream is) throws XMLStreamException {
		XMLInputFactory xif = XMLInputFactory.newFactory();
		xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
		xif.setProperty(XMLInputFactory.SUPPORT_DTD, false);
		return xif.createXMLStreamReader(new StreamSource(is));
	}

	public String generateHtmlReport(XmlSimpleReport simpleReport) throws IOException, TransformerException, JAXBException {
		try (StringWriter stringWriter = new StringWriter()) {
			generateHtmlReport(simpleReport, new StreamResult(stringWriter));
			return stringWriter.toString();
		}
	}

	public void generateHtmlReport(XmlSimpleReport simpleReport, Result result) throws IOException, TransformerException, JAXBException {
		Transformer transformer = SimpleReportXmlDefiner.getHtmlBootstrap3Templates().newTransformer();
		transformer.transform(new JAXBSource(SimpleReportXmlDefiner.getJAXBContext(), SimpleReportXmlDefiner.OBJECT_FACTORY.createSimpleReport(simpleReport)),
				result);
	}

	public String generateHtmlReport(String marshalledSimpleReport) throws IOException, TransformerException {
		try (StringWriter stringWriter = new StringWriter()) {
			generateHtmlReport(marshalledSimpleReport, new StreamResult(stringWriter));
			return stringWriter.toString();
		}
	}

	public void generateHtmlReport(String marshalledSimpleReport, Result result) throws IOException, TransformerException {
		Transformer transformer = SimpleReportXmlDefiner.getHtmlBootstrap3Templates().newTransformer();
		transformer.transform(new StreamSource(new StringReader(marshalledSimpleReport)), result);
	}

	public void generatePdfReport(XmlSimpleReport simpleReport, Result result) throws IOException, TransformerException, JAXBException {
		Transformer transformer = SimpleReportXmlDefiner.getPdfTemplates().newTransformer();
		transformer.transform(new JAXBSource(SimpleReportXmlDefiner.getJAXBContext(), SimpleReportXmlDefiner.OBJECT_FACTORY.createSimpleReport(simpleReport)),
				result);
	}

	public void generatePdfReport(String marshalledSimpleReport, Result result) throws IOException, TransformerException {
		Transformer transformer = SimpleReportXmlDefiner.getPdfTemplates().newTransformer();
		transformer.transform(new StreamSource(new StringReader(marshalledSimpleReport)), result);
	}

}
