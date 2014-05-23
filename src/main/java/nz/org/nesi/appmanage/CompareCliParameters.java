package nz.org.nesi.appmanage;

import com.beust.jcommander.Parameter;

import java.util.List;

abstract class CompareCliParameters extends AppManageModule {

    @Parameter(names = { "-i", "--import-folder" }, description = "the path to where the modules are", required = true)
    private String input;

    @Parameter(names = { "--show-only-in-applications" }, description = "shows folders that are only in the applications repository")
    private boolean onlyInApps = true;

    @Parameter(names = { "--show-only-in-modules" }, description = "shows folders that are only in the import (modules) folder")
    private boolean onlyInModules = true;

    public String getInput() {
        return input;
    }

    public boolean showOnlyInApps() {
        return onlyInApps;
    }

    public boolean showOnlyInModules() {
        return onlyInModules;
    }

}
