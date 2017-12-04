import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    public static final String COUNTY_PATH = "temp/county.ttl";
    public static final String SCHOOL_CSV_PATH = "temp/school.csv";
    public static final String ONTOLOGY_PATH = "temp/ontology.ttl";

    private static final String COUNTY_URL = "http://data.geohive.ie/dumps/county/default.ttl";
    private static final String SCHOOL_URL = "http://airo.maynoothuniversity.ie/files/dDATASTORE/education/csv/primary_schools_2013_2014.csv";

    public static void fetchData() {
        Path countyPath = Paths.get(COUNTY_PATH);
        Path schoolCSVPath = Paths.get(SCHOOL_CSV_PATH);

        if(!Files.exists(countyPath)) {
            try {
                Files.createDirectories(countyPath.getParent());
                download(COUNTY_URL, countyPath.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!Files.exists(schoolCSVPath)) {
            try {
                Files.createDirectories(schoolCSVPath.getParent());
                download(SCHOOL_URL, schoolCSVPath.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void download(String url, String file) throws Exception {
        Path path = Paths.get(file);
        URI u = URI.create(url);
        try (InputStream in = u.toURL().openStream()) {
            Files.copy(in, path);
        }
    }

}
