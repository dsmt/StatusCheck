package utils;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.ListFolderResult;

import java.io.*;
import java.util.Optional;

public final class DropBoxClient {

    private static final String ACCESS_TOKEN = "<access_token>";
    private static final String FOLDER_NAME = "<folder_name>";
    private static final String WORK_BOOK_NAME = "<work_book_name>";

    private static DbxClientV2 client;

    private static final DropBoxClient INSTANCE = new DropBoxClient();

    private DropBoxClient() {
        final DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/" + FOLDER_NAME).build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
    }

    public static DropBoxClient getInstance() {
        return INSTANCE;
    }

    public Optional<FileInputStream> getExcelFile() {
        ListFolderResult result = null;

        try {
            result = client.files().listFolder("");

            while (result.getHasMore()) {
                result = client.files().listFolderContinue(result.getCursor());
            }
        } catch (final DbxException e) {
            e.printStackTrace();
        }

        if (result.getEntries().stream()
                .noneMatch(e -> e.getName().equals(WORK_BOOK_NAME))) return Optional.empty();

        try {
            final OutputStream outputStream = new FileOutputStream(WORK_BOOK_NAME);

            client.files()
                    .downloadBuilder(result.getEntries().get(0).getPathDisplay())
                    .download(outputStream);

            return Optional.of(new FileInputStream(WORK_BOOK_NAME));
        }  catch (final IOException | DbxException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }
}
