package osm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import org.apache.commons.cli.*;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import osm.dao.NodeDao;
import osm.dao.NodeDaoImpl;
import osm.dao.TagDao;
import osm.dao.TagDaoImpl;

public class Main {
    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {
        Options options = new Options();
        options.addRequiredOption("f", "file", true, "File path");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException pe) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "Task1", options );
        }

        assert cmd != null;
        if (!cmd.hasOption("f")) {
            LOG.error("Hasn't file");
        } else {
            LOG.info("Start decompress file");
            try (InputStream inputStream = new BZip2CompressorInputStream(new FileInputStream(cmd.getOptionValue("file")))) {
                LOG.info("Finish decompress file");
                Connection connection = DbUtils.init();
                LOG.info("Init Database");
                NodeDao nodeDao = new NodeDaoImpl();
                TagDao tagDao = new TagDaoImpl();
                NodeService nodeService = new NodeService(nodeDao, tagDao);
                OSM osm = new OSM(nodeService, 0);
                Instant start = null;
                Instant finish = null;
                start = Instant.now();
                osm.process(inputStream);
                DbUtils.commitAndCloseStatement();
                finish = Instant.now();

                long elapsed = Duration.between(start, finish).toMillis();
                System.out.println("Прошло времени, мс: " + elapsed);
                System.out.println("Прошло времени, с: " + elapsed/1000);
            } catch (FileNotFoundException e) {
                LOG.error("File not found", e);
            } catch (IOException e) {
                LOG.error("File read error", e);
            } catch (JAXBException | XMLStreamException e) {
                LOG.error("File processing error", e);
            } catch (SQLException e) {
                LOG.error("Failed to initialize database", e);
            } finally {
                DbUtils.closeConnection();
            }
        }
    }
}