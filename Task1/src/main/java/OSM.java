import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;

public class OSM {
    private static final Logger LOG = LogManager.getLogger(OSM.class);

    private static final QName USER_ATTR_NAME = new QName("user");
    private static final QName ID_ATTR_NAME = new QName("id");
    private static final QName KEY_ATTR_NAME = new QName("k");
    private static final String NODE = "node";
    private static final String TAG = "tag";

    public static OsmResult createStatistic(InputStream inputStream) throws XMLStreamException {
        LOG.info("OSM createStatistic start");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader = null;
        OsmResult result = new OsmResult();

        try {
            eventReader = factory.createXMLEventReader(inputStream);
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();

                if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    StartElement startElement = event.asStartElement();
                    if (NODE.equals(startElement.getName().getLocalPart())) {
                        Attribute userAttribute = startElement.getAttributeByName(USER_ATTR_NAME);
                        result.userEditsCount(userAttribute.getValue());

                        Attribute idAttribute = startElement.getAttributeByName(ID_ATTR_NAME);
                        LOG.debug("Creating tags statistic for node with id" + idAttribute + "start");
                        statisticTags(result, eventReader);
                        LOG.debug("Tags statistic finish");
                    }
                }
            }
        } finally {
            assert eventReader != null;
            eventReader.close();
        }
        LOG.info("OSM createStatistic finish");
        return result;
    }

    private static void statisticTags(OsmResult result, XMLEventReader eventReader) throws XMLStreamException {
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.getEventType() == XMLStreamConstants.END_ELEMENT &&
                    NODE.equals(event.asEndElement().getName().getLocalPart())) {
                return;
            }
            if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
                StartElement startElement = event.asStartElement();
                if (TAG.equals(startElement.getName().getLocalPart())) {
                    Attribute key = startElement.getAttributeByName(KEY_ATTR_NAME);
                    result.tagCount(key.getValue());
                }
            }
        }
        throw new XMLStreamException("Unexpected end of stream");
    }
}
