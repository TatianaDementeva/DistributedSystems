import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import javax.xml.stream.XMLStreamException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Main {
    private static final String STATISTIC_FORMAT = "%s : %d%n";
    private static final Logger LOG = LogManager.getLogger(Main.class);

    public static void main (String[] args) {
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
            try (InputStream inputStream = new BZip2CompressorInputStream(new FileInputStream(cmd.getOptionValue("file") ))) {
                LOG.info("Finish decompress file");
                OsmResult resultContainer = OSM.createStatistic(inputStream);
                System.out.println("Changes statistic:");
                printStatistic(resultContainer.getUsers());
                System.out.println("Tags statistic:");
                printStatistic(resultContainer.getTags());

            } catch (FileNotFoundException e) {
                LOG.error("File not found", e);
            } catch (XMLStreamException e) {
                LOG.error("XML reading error", e);
            } catch (IOException e) {
                LOG.error("File read error", e);
            }
        }

    }

    private static void printStatistic(Map<String, Integer> statistic) {
        statistic.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .forEach(e -> System.out.printf(STATISTIC_FORMAT, e.getKey(), e.getValue()));
    }
}
