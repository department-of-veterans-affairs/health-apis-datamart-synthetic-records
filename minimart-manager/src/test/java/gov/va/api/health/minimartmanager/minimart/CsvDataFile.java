package gov.va.api.health.minimartmanager.minimart;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Slf4j
@Value
public class CsvDataFile {

  Stream<CSVRecord> records;

  Integer rows;

  @Builder
  @SneakyThrows
  public CsvDataFile(File directory, String fileName) {
    File csvFile = new File(directory + "/" + fileName);
    CSVParser parser =
        CSVParser.parse(
            csvFile, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader());
    var allRecords = parser.getRecords();
    rows = allRecords.size();
    log.info("Found {} records in {}", allRecords.size(), csvFile.getAbsolutePath());
    records = allRecords.stream().filter(Objects::nonNull).distinct();
  }
}
