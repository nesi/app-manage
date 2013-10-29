package nz.org.nesi.appmanage;

import com.beust.jcommander.Parameter;

abstract class ImportModulesCliParameters extends AppManageModule {

	@Parameter(names = { "-i", "--import-folder" }, description = "the path to where the modules are", required = true)
	private String input;

    @Parameter(names = { "--update-info"}, description = "whether to update information on the backend")
    private boolean updateGrisu = false;

    @Parameter(names = {"-t", "--token"}, description = "the token under which the modules are to be copied, e.g. auckland/pan")
    private String token = "";
	public String getInput() {
		return input;
	}

    public boolean isUpdateGrisu() {
        return updateGrisu;
    }

    public String getToken() {
        return token;
    }

}
