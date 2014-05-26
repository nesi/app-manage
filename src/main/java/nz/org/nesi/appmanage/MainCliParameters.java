package nz.org.nesi.appmanage;

import com.beust.jcommander.Parameter;
import grisu.jcommons.git.GitRepoUpdater;

import java.io.File;

public class MainCliParameters {

	@Parameter(names = { "-a", "--applications" }, description = "the path to the applications repository")
	private String root = new File(".").getAbsolutePath();

    @Parameter(names = {"-v", "--verbose"}, description = "verbose output")
    private boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

	public String getApplicationsRoot() {
		return root;
	}

    public  File getApplicationsRootFile() {

        if ( root.startsWith("git") ) {
            return GitRepoUpdater.ensureUpdated(root);
        } else {
            return new File(root);
        }
    }

}
