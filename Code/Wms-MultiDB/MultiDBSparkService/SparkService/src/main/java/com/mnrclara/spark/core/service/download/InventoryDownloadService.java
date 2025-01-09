package com.mnrclara.spark.core.service.download;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

@Service
@Slf4j
public class InventoryDownloadService {

    Properties connProp = new Properties();
    SparkSession sparkSession = null;

    private final String outPutPath;

    public InventoryDownloadService() {
        //connection properties
        connProp.setProperty("driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        connProp.put("user", "sa");
        connProp.put("password", "Do1cavIFK4^$pQ^zZYsX");
        sparkSession = SparkSession.builder().master("local[*]")
                .appName("Inventory.com")
                .config("spark.executor.memory", "4g")
                .config("spark.executor.cores", "4")
                .getOrCreate();

        //Read from Sql Table
        val df2 = sparkSession.read()
                .option("fetchSize", "10000")
                .jdbc("jdbc:sqlserver://3.109.20.248;databaseName=WMS_ALMDEV", "tblinventory", connProp)
                .repartition(16);
        df2.createOrReplaceTempView("tblinventoryv5");

        // File Save Location
        outPutPath = "/home/ubuntu/project/root/Classic\\ WMS/Code/Project/inventory/";
    }

    public void downloadInventoryInCSV() throws ParseException {

        Dataset<Row> inventoryV2Query = sparkSession.sql("Select "
                + "INV_ID as inventoryId, "
                + "LANG_ID as languageId, "
                + "C_ID as companyCodeId, "
                + "PLANT_ID as plantId, "
                + "WH_ID as warehouseId, "
                + "PACK_BARCODE as packBarcodes, "
                + "ITM_CODE as itemCode, "
                + "ST_BIN as storageBin, "
                + "SP_ST_IND_ID as specialStockIndicatorId, "
                + "MFR_CODE as manufacturerCode, "
                + "BARCODE_ID as barcodeId, "
                + "INV_QTY as inventoryQuantity, "
                + "ALLOC_QTY as allocatedQuantity, "
                + "IS_DELETED as deletionIndicator, "
                + "REF_FIELD_5 as referenceField5, "
                + "REF_FIELD_6 as referenceField6, "
                + "REF_FIELD_9 as referenceField9, "
                + "REF_FIELD_10 as referenceField10 "

                + "FROM tblinventoryv5 WHERE IS_DELETED = 0 ");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(new Date());
        // FileName
        String fileName = "inventory_" + currentDate + ".csv";
        String finalOutputPath = outPutPath + fileName;

        inventoryV2Query.repartition(1)
                .write()
                .mode(SaveMode.Overwrite)
                .option("header", "true")
                .csv(finalOutputPath);

        //Get CSV File
        String tmpCsvFileNamePattern = "part-00000-[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}-c000.csv";
        String tmpCsvFilePath = finalOutputPath + "/" + tmpCsvFileNamePattern;

        log.info("temp file location: " + tmpCsvFilePath);

        File[] matchingFiles = new File(finalOutputPath).listFiles((dir, name) -> name.matches(tmpCsvFileNamePattern));

        if (matchingFiles != null && matchingFiles.length > 0) {
            File firstPartFile = matchingFiles[0];

            // Extract the UUID from the file name
            String uuid = firstPartFile.getName().replaceAll(".*-(\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12})-.*", "$1");

            // Construct the new file name with the UUID
            String newCsvFileName = outPutPath + "inventory_" + currentDate + "_" + uuid + ".csv";
            boolean renamed = firstPartFile.renameTo(new File(newCsvFileName));

            if (!renamed) {
                log.error("Failed to rename the temporary file.");
            }
        } else {
            log.error("No matching files found for the given pattern: " + tmpCsvFilePath);
        }

// Repeat deletion logic based on condition
        boolean condition = true;  // Set your condition here
        if (condition) {
            deleteFilesWithRegex(outPutPath, tmpCsvFileNamePattern);
            deleteFilesWithWildcard(outPutPath, "_SUCCESS");
            deleteFilesWithWildcard(outPutPath, ".part-00000*.crc");
            deleteFilesWithWildcard(outPutPath, "._SUCCESS.crc");
        }
    }

    private void deleteFilesWithRegex(String directoryPath, String regexPattern) {
        File directory = new File(directoryPath.replace("\\", "/"));
        File[] filesToDelete = directory.listFiles((dir, name) -> {
            String fileName = new File(name).getName();
            return fileName.matches(regexPattern);
        });

        if (filesToDelete != null) {
            for (File file : filesToDelete) {
                FileUtils.deleteQuietly(file);
            }
        }
    }
    // Delete files in the folder based on wildcard pattern
    private void deleteFilesWithWildcard(String directoryPath, String wildcardPattern) {
        File directory = new File(directoryPath);
        File[] filesToDelete = directory.listFiles((dir, name) -> name.matches(wildcardPattern));

        if (filesToDelete != null) {
            for (File file : filesToDelete) {
                FileUtils.deleteQuietly(file);
            }
        }
    }
}

//        // Get the current date in a specific format (e.g., "yyyyMMdd")
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
//        String currentDate = dateFormat.format(new Date());
//
//        // Append the current date to the file name
//        String fileName = "inventory_" + currentDate + ".csv";
//        // Construct the output path with the current date and the desired file name
//        String outputPath = "/home/ubuntu/project/root/Classic\\ WMS/Code/Project/inventory/" + fileName;
//
//        // Repartition the DataFrame to a single partition and save as CSV
//        inventoryV2Query.repartition(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .format("csv")
//                .option("path", outputPath)
//                .save();
//        String tmpCsvFilePath = new File(outputPath, "part-00000").getPath();
//        File outputFile = new File(outputPath);
//        new File(tmpCsvFilePath).renameTo(outputFile);
//
//    }
//}


//// Get the temporary directory path
//        String tempDirectory = "/home/ubuntu/project/root/Classic WMS/Code/Project/inventory/_temporary/";
//
//        try {
//            // Check if the temporary directory exists
//            if (Files.exists(Paths.get(tempDirectory))) {
//                // Find the temporary directory using a glob pattern
//                Path tempDirPath = Paths.get(tempDirectory);
//                PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + tempDirectory + "*");
//                Files.walk(tempDirPath)
//                        .filter(pathMatcher::matches)
//                        .forEach(tempDir -> {
//                            try {
//                                // Find all CSV files in the temporary directory
//                                List<Path> csvFiles = Files.list(tempDir)
//                                        .filter(file -> file.getFileName().toString().startsWith("part-"))
//                                        .collect(Collectors.toList());
//
//                                // Move each CSV file to the desired location with a timestamp
//                                for (Path csvFile : csvFiles) {
//                                    // Extract the filename without the partition identifier
//                                    String fileName = csvFile.getFileName().toString();
//                                    String newName = fileName.substring(0, fileName.lastIndexOf("-")) + ".csv";
//
//                                    // Move the CSV file to the desired location with the new name
//                                    Files.move(csvFile, Paths.get("/home/ubuntu/project/root/Classic WMS/Code/Project/inventory/" + newName));
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        });
//            } else {
//                System.err.println("Temporary directory does not exist.");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//}

//        inventoryV2Query.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv("/home/ubuntu/project/root/Classic WMS/Code/Project/inventory");
//
//        // Get the temporary directory path
//        String tempDirectory = "/home/ubuntu/project/root/Classic WMS/Code/Project/inventory/inventory_*/_temporary/0/";
//
//        try {
//            // Find the temporary directory using a glob pattern
//            Path tempDirPath = Paths.get(tempDirectory);
//            PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + tempDirectory);
//            Files.walk(tempDirPath)
//                    .filter(pathMatcher::matches)
//                    .forEach(tempDir -> {
//                        // Get the CSV file in the temporary directory
//                        Path csvFileInTempDir = tempDir.resolve("part-00000");
//                        // Move the CSV file to the desired location with a timestamp
//                        try {
//                            Files.move(csvFileInTempDir, Paths.get("/home/ubuntu/project/root/Classic WMS/Code/Project/inventory/inventory_" + System.currentTimeMillis() + ".csv"));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        String timestamp = dateFormat.format(new Date());
//
//        String directoryPath = "/home/ubuntu/project/root/Classic WMS/Code/Project/inventory/";
//        String fileNameFormat = "inventory_%s.csv"; // Format for the file name
//
//        String fileName = String.format(fileNameFormat, timestamp);
//        String filePath = directoryPath + fileName;
//
//        inventoryV2Query.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv(filePath);
//    }
//}
//        inventoryV2Query.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv("/home/ubuntu/project/root/Classic WMS/Code/Project/inventory");

//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
//        String timestamp = dateFormat.format(new Date());
//
//        String filePath = "/home/ubuntu/project/root/Classic WMS/Code/Project/inventory/inventory" + timestamp;
//
//        inventoryV2Query.coalesce(1)
//                .write()
//                .mode(SaveMode.Overwrite)
//                .option("header", "true")
//                .csv(filePath);

// Move and rename the resulting file to "inventory.csv"
//        try {
//            Process process = Runtime.getRuntime().exec("mv /home/ubuntu/project/root/Classic WMS/Code/Project/inventory/part-*.csv /home/ubuntu/project/root/Classic WMS/Code/Project/inventory.csv");
//            process.waitFor();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//}
//}
// Specify the folder path for the output CSV file
//        String folderPath = "D:/inventory.csv";
//
//        // Create the directory if it doesn't exist
//        try {
//            Files.createDirectories(Path.of(folderPath));
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Error creating directory: " + e.getMessage());
//        }

//        // Specify the file path for the output CSV file
//        String outputPath = folderPath + "/output.csv";
//
//        inventoryV2Query.repartition(1)
//                .write()
//                .mode("overwrite")
//                .option("header", true)
//                .csv(outputPath);

// Specify the path where you want to save the CSV file
//        String outputPath = "/path/to/save/inventory.csv";

// Write the DataFrame to CSV
//        inventoryV2Query.write()
//                .format("csv")
//                .option("header", "true") // Include header in the CSV file
//                .mode("overwrite") // Overwrite the file if it already exists
//                .option("chmod", "0644") // Set the desired permissions
//                .save(outputPath);

// Stop the Spark session
//        sparkSession.stop();
//    }


//    @Bean
//    public FlatFileItemWriter<Inventory> csvFileItemWriter() {
//        FlatFileItemWriter<Inventory> writer = new FlatFileItemWriter<>();
//        writer.setResource(new FileSystemResource("inventory.csv"));
//        writer.setAppendAllowed(false);
//
//        LineAggregator<Inventory> lineAggregator = createInventoryLineAggregator();
//        writer.setLineAggregator(lineAggregator);
//
//        return writer;
//}