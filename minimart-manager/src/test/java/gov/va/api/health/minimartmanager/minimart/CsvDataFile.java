package gov.va.api.health.minimartmanager.minimart;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Slf4j
public class CsvDataFile {
  List<CSVRecord> allRecords;

  @Builder
  @SneakyThrows
  public CsvDataFile(File directory, String fileName) {
    File csvFile = new File(directory + "/" + fileName);
    CSVParser parser =
        CSVParser.parse(
            csvFile, StandardCharsets.UTF_8, CSVFormat.DEFAULT.withFirstRecordAsHeader());
    allRecords = parser.getRecords();
    log.info("Found {} records in {}", allRecords.size(), csvFile.getAbsolutePath());
  }

  public Stream<CSVRecord> records() {
    return allRecords.stream().filter(Objects::nonNull).distinct();
  }

  public int rows() {
    return allRecords.size();
  }
}
