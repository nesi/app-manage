package nz.org.nesi.appmanage;

import com.beust.jcommander.JCommander;
import grisu.frontend.control.login.LoginManager;
import grisu.frontend.view.cli.GrisuCliClient;

/**
 * Project: grisu
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 30/10/13
 * Time: 10:52 AM
 */
public class AppManage extends GrisuCliClient<MainCliParameters> {

    public static void main(String[] args) throws Exception{
        // basic housekeeping
        LoginManager.initGrisuClient("app-manage");

        // helps to parse commandline arguments, if you don't want to create
        // your own parameter class, just use DefaultCliParameters
        MainCliParameters params = new MainCliParameters();
        ExportModule expParams = new ExportModule();
        ImportModule impParams = new ImportModule();

        // create the client
        AppManage client = null;
        try {
            client = new AppManage(params, expParams, impParams, args);
        } catch(Exception e) {
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

    private final AppManageModule commandClass;

    public AppManage(MainCliParameters params, ExportModule expParams, ImportModule impParams, String[] args) throws Exception {
        super(params, args);
        this.mainParams = params;
        this.exportModulesCliParameters = expParams;
        this.importModulesCliParameters = impParams;
        jc = new JCommander(params);
        jc.setProgramName("app-manage");
        jc.addCommand("export-modules", expParams);
        jc.addCommand("import-modules", impParams);

        jc.parse(args);
        String command = jc.getParsedCommand();

        if ( "export-modules".equals(command) ) {
            commandClass = exportModulesCliParameters;
        }  else if ( "import-modules".equals(command) ) {
            commandClass = importModulesCliParameters;
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

    @Override
    public void run() {

        this.exportModulesCliParameters.setClient(this);
        this.importModulesCliParameters.setClient(this);

        commandClass.validate();
        commandClass.execute();
    }
}
