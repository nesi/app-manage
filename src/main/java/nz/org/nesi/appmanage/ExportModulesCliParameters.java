package nz.org.nesi.appmanage;

import com.beust.jcommander.Parameter;

abstract class ExportModulesCliParameters  extends AppManageModule {

	@Parameter(names = { "-o", "--output-folder" }, description = "the path to where the modules are supposed to end up", required = true)
	private String output;

    @Parameter(names = {"-t", "--token"}, description = "the token under which the modules are to be found, e.g. auckland/pan, needs to match the path (ignore-case)")
    private String token = "";
	public String getOutput() {
		return output;
	}


    public String getToken() {
        return token;
    }




}
