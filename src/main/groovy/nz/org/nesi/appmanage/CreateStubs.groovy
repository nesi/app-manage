package nz.org.nesi.appmanage
import com.google.common.collect.Maps
import nz.org.nesi.appmanage.exceptions.AppFileException
import nz.org.nesi.appmanage.model.Documentation
import org.apache.commons.io.FileUtils
/**
 * Project: grisu
 *
 * Written by: Markus Binsteiner
 * Date: 6/06/13
 * Time: 10:53 AM
 */
class CreateStubs extends nz.org.nesi.appmanage.CreateStubsCliParameters {



    def appsToProcess = [:]

    public CreateStubs() {

    }

    public static void createStub() {

        def args = ["-v", "-a",
                "/home/markus/src/config/applications", "create-doc", "--applications",
                "Abaqus,Java", "--output-dir", "/home/markus/doc/applications.wiki"];

        // helps to parse commandline arguments, if you don't want to create
        // your own parameter class, just use DefaultCliParameters
        MainCliParameters params = new MainCliParameters();
        ExportModule expParams = new ExportModule();
        ImportModule impParams = new ImportModule();
        CreateDocumentationCliParameters createDocParams = new CreateDoc();
        CreateStubsCliParameters createStubsParams = new CreateStubs();
        // create the client
        AppManage client = null;
        try {
            client = new AppManage(params, expParams, impParams, createDocParams, args as String[]);
        } catch (Exception e) {
            System.err.println("Could not start app-manage: "
                    + e.getLocalizedMessage());
            return;
        }


    }

    public void validate() {

    }

    public List<File> createStubs() {


        def properties = [:]

        def ignore = getIgnore()

        if ( ignore ) {
            def temp = new File(ignore)
            if ( temp.exists() ) {
                ignore = FileUtils.readLines(temp)
            } else {
                ignore = ignore.split(",")
            }
        } else {
            ignore = []
        }

        ignore.collect() { it ->
            it = it.toLowerCase().trim()
        }

        def createdFiles = []
        Map<String, Map<String, Object>> applications = Maps.newTreeMap()
        appsToProcess.each { appName, doc ->
            if ( ! ignore.contains(appName) ) {
                applications.put(appName, doc)
                if ( isCreateDoc() ) {
                    createdFiles.addAll(doc.createMissingFiles())
                }
            }
        }

        return createdFiles

    }

    public void execute() {

        if (getApplications()) {
            for (String app : getApplications().split(",")) {
                def appFolder = Utils.getApplicationFolder(getAppRoot(), app)
                try {
                    Documentation temp = new Documentation(appFolder, getAppRoot())
                    appsToProcess.put(temp.getApplicationName(), temp)
                } catch (AppFileException afe) {
                    printMessage("Ignoring folder " + appFolder + ": " + afe.getLocalizedMessage(), true)
                }

            }
        } else {
            getAppRoot().listFiles().sort { it.name }.each { appFolder ->
                try {
                    Documentation temp = new Documentation(appFolder, getAppRoot())
                    appsToProcess.put(temp.getApplicationName(), temp)
                } catch (AppFileException afe) {
                    printMessage("Ignoring folder " + appFolder + ": " + afe.getLocalizedMessage(), true)
                }

            }
        }

        String result = createStubs().join(", ")

        println result
    }


}
