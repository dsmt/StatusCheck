package utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.*;

public final class ExcelStreamReader {

    private static final DecimalFormat DECIMAL_FORMAT
            = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private Workbook workbook = null;

    private static Map<String, List<Row>> DEVICE_LIST = new HashMap<>();
    private static Map<String, List<Row>> RMA_LIST = new HashMap<>();
    private static Set<String> MODELS = new TreeSet<>();

    private static final String SHEET_SERVICE = "общий";

    private static final int COLUMN_MODEL = 2;
    private static final int COLUMN_ID = 9;

    private static final int COLUMN_RMA = 0;

    private static final int COLUMN_STATUS = 1;
    private static final int COLUMN_CUSTOMER = 6;
    private static final int COLUMN_ENGINEER = 19;

    private static final int COLUMN_PART_NO = 23;
    private static final int COLUMN_PART_ORDERED = 24;

    private static final int COLUMN_IS_EUROPE = 34;
    private static final int COLUMN_EUROPE_DATE = 33;

    private static final int COLUMN_SERVICE_1 = 12;
    private static final int COLUMN_SERVICE_2 = 13;
    private static final int COLUMN_SERVICE_3 = 14;

    private static final int COLUMN_WORK_STARTED = 21;
    private static final int COLUMN_WORK_ENDED = 3;

    private static final ExcelStreamReader INSTANCE = new ExcelStreamReader();

    private long lastUpdate = 0;
    private long lastSuccess = 0;

    private ExcelStreamReader() {
    }

    public static ExcelStreamReader getInstance() {
        return INSTANCE;
    }

    public void updateData() {
        lastUpdate = System.currentTimeMillis();

        final Optional<FileInputStream> fis = DropBoxClient.getInstance().getExcelFile();

        DECIMAL_FORMAT.setMaximumFractionDigits(340);

        fis.ifPresent(fileInputStream -> workbook = StreamingReader.builder()
                .rowCacheSize(100)
                .bufferSize(4096)
                .open(fileInputStream));

        if (isLoaded()) {
            collectServiceRecords();
            lastSuccess = System.currentTimeMillis();
        }
    }

    public boolean isLoaded() {
        return workbook != null;
    }

    public int getRecords() {
        return DEVICE_LIST.size();
    }

    private void collectServiceRecords() {
        final Sheet sheet = workbook.getSheet(SHEET_SERVICE);

        if (sheet != null) {
            final Map<String, List<Row>> tempServiceHistory = new HashMap<>();
            final Set<String> tempModels = new TreeSet<>();
            final Map<String, List<Row>> tempRMA = new HashMap<>();

            final Iterator<Row> iterator = sheet.iterator();
            if (iterator.hasNext()) iterator.next();
            iterator.forEachRemaining(
                    r -> {
                        Optional<String> status = getCellValueAsString(r.getCell(COLUMN_STATUS));
                        Optional<String> model = getCellValueAsString(r.getCell(COLUMN_MODEL));
                        Optional<String> id = getCellValueAsString(r.getCell(COLUMN_ID));
                        Optional<String> rma = getCellValueAsString(r.getCell(COLUMN_RMA));

                        if (status.isPresent() && model.isPresent() && id.isPresent() && rma.isPresent()
                                && !model.get().equals("Модель")) {
                            final String key = (model.get() + id.get()).toLowerCase();
                            if (tempServiceHistory.containsKey(key)) {
                                tempServiceHistory.get(key).add(r);
                            } else {
                                tempServiceHistory.put(key, new ArrayList<>(Collections.singletonList(r)));
                            }

                            tempModels.add(model.get());

                            final String rmaKey = rma.get().split("-")[0];
                            if (tempRMA.containsKey(rmaKey)) {
                                tempRMA.get(rmaKey).add(r);
                            } else {
                                tempRMA.put(rmaKey, new ArrayList<>(Collections.singletonList(r)));
                            }
                        }
                    }
            );

            DEVICE_LIST = tempServiceHistory;
            MODELS = tempModels;
            RMA_LIST = tempRMA;
        }
    }

    private static Optional<String> getCellValueAsString(final Cell cell) {
        if (cell == null) return Optional.empty();
        String result = null;

        switch (cell.getCellType()) {
            case FORMULA:
                if (cell.getCachedFormulaResultType() == STRING)
                    result = cell.getStringCellValue();
                else if (cell.getCachedFormulaResultType() == NUMERIC)
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {
                        result = DATE_FORMAT.format(cell.getDateCellValue());
                    } else {
                        result = DECIMAL_FORMAT.format(cell.getNumericCellValue());
                    }
                break;
            case STRING:
                result = cell.getStringCellValue();
                break;
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    result = DATE_FORMAT.format(cell.getDateCellValue());
                } else {
                    result = DECIMAL_FORMAT.format(cell.getNumericCellValue());
                }
                break;
            default:
                break;
        }

        return result == null || result.isEmpty() ? Optional.empty() : Optional.of(result.trim());
    }

    private ImmutableMap<String, String> setData(final Row row) {
        return ImmutableMap.<String, String>builder()
                .put("status", getCellValueAsString(row.getCell(COLUMN_STATUS)).orElse(""))
                .put("rma", getCellValueAsString(row.getCell(COLUMN_RMA)).orElse(""))
                .put("model", getCellValueAsString(row.getCell(COLUMN_MODEL)).orElse(""))
                .put("id", getCellValueAsString(row.getCell(COLUMN_ID)).orElse(""))
                .put("customer", getCellValueAsString(row.getCell(COLUMN_CUSTOMER)).orElse(""))
                .put("engineer", getCellValueAsString(row.getCell(COLUMN_ENGINEER)).orElse(""))
                .put("work_started", getCellValueAsString(row.getCell(COLUMN_IS_EUROPE)).isPresent()
                        ? getCellValueAsString(row.getCell(COLUMN_EUROPE_DATE)).orElse("")
                        : getCellValueAsString(row.getCell(COLUMN_WORK_STARTED)).orElse(""))
                .put("service", getService(row))
                .put("part_no", getCellValueAsString(row.getCell(COLUMN_PART_NO)).orElse(""))
                .put("part_ordered", getCellValueAsString(row.getCell(COLUMN_PART_ORDERED)).orElse(""))
                .put("work_ended", getCellValueAsString(row.getCell(COLUMN_WORK_ENDED)).orElse(""))
                .build();
    }

    @SuppressWarnings("unchecked")
    private String getService(final Row row) {
        final ImmutableList.Builder listBuilder = ImmutableList.<String>builder();

        if (getCellValueAsString(row.getCell(COLUMN_SERVICE_1)).isPresent())
            listBuilder.add(getCellValueAsString(row.getCell(COLUMN_SERVICE_1)).get());
        if (getCellValueAsString(row.getCell(COLUMN_SERVICE_2)).isPresent())
            listBuilder.add(getCellValueAsString(row.getCell(COLUMN_SERVICE_2)).get());
        if (getCellValueAsString(row.getCell(COLUMN_SERVICE_3)).isPresent())
            listBuilder.add(getCellValueAsString(row.getCell(COLUMN_SERVICE_3)).get());

        return String.join(", ", listBuilder.build());
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public long getLastSuccess() {
        return lastSuccess;
    }

    public Set<String> getModels() {
        return MODELS;
    }

    public List<ImmutableMap<String, String>> getByRMA(final String rma) {
        if (rma == null) return ImmutableList.of();
        final List<ImmutableMap<String, String>> result = new ArrayList<>();
        if (RMA_LIST.containsKey(rma)) {
            RMA_LIST.get(rma).forEach(
                    r -> result.add(setData(r))
            );
        }

        return result;
    }

    public List<ImmutableMap<String, String>> getDevice(final String model, final String id) {
        if (model == null || id == null) return ImmutableList.of();
        final List<ImmutableMap<String, String>> result = new ArrayList<>();
        final String key = (model + id).toLowerCase();
        if (DEVICE_LIST.containsKey(key)) {
            DEVICE_LIST.get(key).forEach(
                    r -> result.add(setData(r))
            );
        }

        return result;
    }
}
