package nz.org.nesi.appmanage

import com.beust.jcommander.JCommander
import grisu.frontend.control.login.LoginManager
import grisu.frontend.view.cli.GrisuCliClient

class AppManage extends GrisuCliClient<MainCliParameters> {

	static void main(String[] args) throws Exception{
		// basic housekeeping
		LoginManager.initGrisuClient("app-manage");

		// helps to parse commandline arguments, if you don't want to create
		// your own parameter class, just use DefaultCliParameters
		MainCliParameters params = new MainCliParameters();
        ExportModulesCliParameters expParams = new ExportModulesCliParameters()

		// create the client
		AppManage client = null;
		try {
			client = new AppManage(params, expParams, args);
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

    final JCommander jc
    final MainCliParameters mainParams
    final ExportModulesCliParameters exportModulesCliParameters

    private final def commandClass

	public AppManage(MainCliParameters params, ExportModulesCliParameters expParams, String[] args) throws Exception {
		super(params, args)
        this.mainParams = params
        this.exportModulesCliParameters = expParams
        jc = new JCommander(params)
        jc.setProgramName("app-manage")
        jc.addCommand("export-modules", expParams)

        jc.parse(args)
        commandClass = null;
        def command = jc.getParsedCommand()
        switch ( command ) {
            case "export-modules":
                commandClass = new ExportModule(this)
                break;
            default:
                println "No command '"+command+"'"
                jc.usage()
                System.exit(1)
        }
    }

	@Override
	public void run() {

        commandClass.execute()
	}
}
