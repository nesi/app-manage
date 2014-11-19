package nz.org.nesi.appmanage;

import com.beust.jcommander.JCommander;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 30/10/13
 * Time: 10:52 AM
 */
public class AppManage {

    public static final String MODULE_DIR_NAME = "modules";

    public static void main(String[] args) throws Exception{
        // basic housekeeping
        // helps to parse commandline arguments, if you don't want to create
        // your own parameter class, just use DefaultCliParameters
        MainCliParameters params = new MainCliParameters();
        ExportModule expParams = new ExportModule();
        ImportModule impParams = new ImportModule();
        CreateDocumentationCliParameters createDocParams = new CreateDoc();
        CreateStubsCliParameters createStubsParams = new CreateStubs();
        CreateAppListCliParameters createListParams = new CreateAppList();
        CompareCliParameters compareCliParams = new Compare();
        // create the client
        AppManage client = null;
        try {
            client = new AppManage(params, expParams, impParams, createDocParams, createStubsParams, createListParams, compareCliParams, args);
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Could not start app-manage: "
                    + e.getLocalizedMessage());
            System.exit(1);
        }

        // finally:
        // execute the "run" method below
        client.run();

        // exit properly
        System.exit(0);
    }

    final JCommander jc;
    final MainCliParameters mainParams;
    final ExportModule exportModulesCliParameters;
    final ImportModulesCliParameters importModulesCliParameters;
    final CreateDocumentationCliParameters createDocumentationCliParameters;
    final CreateStubsCliParameters createStubsCliParameters;
    final CreateAppListCliParameters createListParameters;
    final CompareCliParameters compareCliParameters;

    private final AppManageModule commandClass;

    public AppManage(MainCliParameters params, ExportModule expParams, ImportModule impParams, CreateDocumentationCliParameters createDocParams, CreateStubsCliParameters createStubsParams, CreateAppListCliParameters createListParams, CompareCliParameters compareCliParameters, String[] args) throws Exception {
        this.mainParams = params;
        this.exportModulesCliParameters = expParams;
        this.importModulesCliParameters = impParams;
        this.createDocumentationCliParameters = createDocParams;
        this.createStubsCliParameters = createStubsParams;
        this.createListParameters = createListParams;
        this.compareCliParameters = compareCliParameters;
        jc = new JCommander(params);
        jc.setProgramName("app-manage");
        jc.addCommand("export-modules", expParams);
        jc.addCommand("import-modules", impParams);
        jc.addCommand("create-doc", createDocParams);
        jc.addCommand("create-stubs", createStubsParams);
        jc.addCommand("create-list", createListParams);
        jc.addCommand("create-list", createListParams);
        jc.addCommand("compare", compareCliParameters);

        try {
            jc.parse(args);
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            jc.usage();
            System.exit(1);
        }
        String command = jc.getParsedCommand();

        if ( "export-modules".equals(command) ) {
            commandClass = exportModulesCliParameters;
        }  else if ( "import-modules".equals(command) ) {
            commandClass = importModulesCliParameters;
        } else if ( "create-doc".equals(command) ) {
            commandClass = createDocumentationCliParameters;
        } else if ( "create-stubs".equals(command) ) {
            commandClass = createStubsCliParameters;
        } else if ( "create-list".equals(command) ) {
            commandClass = createListParams;
        } else if ( "compare".equals(command) ) {
            commandClass = compareCliParameters;
        } else {
            commandClass = null;
            System.err.println( "No command '"+command+"'" );
            jc.usage();
            System.exit(1);
        }
    }

    public MainCliParameters getMainParams() {
        return mainParams;
    }

    public void run() {

        this.exportModulesCliParameters.setClient(this);
        this.importModulesCliParameters.setClient(this);
        this.createDocumentationCliParameters.setClient(this);
        this.createStubsCliParameters.setClient(this);
        this.createListParameters.setClient(this);
        this.compareCliParameters.setClient(this);

        commandClass.validate();
        commandClass.execute();
    }
}
