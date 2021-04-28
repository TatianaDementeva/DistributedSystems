package osm;

import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import lombok.AllArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import osm.model.NodeDb;
import osm.model.generated.Node;

@AllArgsConstructor
public class OSM {
    private static final Logger LOG = LogManager.getLogger(OSM.class);
    private static final String NODE = "node";

    private final NodeService nodeService;
    private int nodeCount = 0;

    public void process(InputStream inputStream) throws JAXBException, XMLStreamException {
        LOG.info("OSM processing start");
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = null;
        JAXBContext jaxbContext = JAXBContext.newInstance(Node.class);
        Instant start = null;
        Instant finish = null;
        try {
            reader = factory.createXMLStreamReader(inputStream);
            start = Instant.now();
            while (reader.hasNext() && nodeCount < 20000) {
                int event = reader.next();
                if (XMLStreamConstants.START_ELEMENT == event && NODE.equals(reader.getLocalName())) {
                    processNode(jaxbContext, reader);
                }
            }
            nodeService.flush();
            finish = Instant.now();
        } finally {
            assert reader != null;
            reader.close();

            long elapsed = Duration.between(start, finish).toMillis();
            System.out.println("Прошло времени, мс: " + elapsed);
            System.out.println("Прошло времени, с: " + elapsed/1000);
        }
        LOG.info("OSM processing finish");
    }

    private void processNode(JAXBContext jaxbContext, XMLStreamReader reader) throws JAXBException {
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        Node node = (Node) unmarshaller.unmarshal(reader);
        nodeCount++;
//        nodeService.putNode(NodeDb.convert(node));
//        nodeService.putNodeWithPreparedStatement(NodeDb.convert(node));
        nodeService.putNodeBuffered(NodeDb.convert(node));
    }

}
