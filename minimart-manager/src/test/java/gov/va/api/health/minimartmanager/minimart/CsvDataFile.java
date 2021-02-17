package gov.va.api.health.minimartmanager.minimart;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Slf4j
public class CsvDataFile {
  File csvFile;

  @Builder
  @SneakyThrows
  public CsvDataFile(File directory, String fileName) {
    csvFile = new File(directory + "/" + fileName);
  }

  @SneakyThrows
  public List<CSVRecord> records() {
    CSVParser parser =
        CSVParser.parse(
            csvFile, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader());
    return parser.getRecords();
  }
}
