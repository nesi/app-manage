package nz.org.nesi.appmanage.model;

import com.beust.jcommander.internal.Lists;
import nz.org.nesi.appmanage.exceptions.AppFileException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Project: Applications
 * <p/>
 * Written by: Markus Binsteiner
 * Date: 15/11/13
 * Time: 3:18 PM
 */
public class Job {

    public static final String LL_JOB_DESCRIPTION_FILE = "job.ll";
    public static final String SLURM_JOB_DESCRIPTION_FILE = "job.sl";
    public static final String GRISU_JOB_DESCRIPTION_FILE = "job.grisu";
    public static final String INPUT_FILES_DIRECTORY_NAME = "files";

    private final String job_base_name;
    private final File jobFolder;
    private final File appRoot;
    private final File inputFilesFolder;

    private final File grisuJobDescription;
    private final File llJobDescription;
    private final File slurmJobDescription;

    private final List<File> inputFiles = Lists.newArrayList();

    public Job(File jobFolder, File appRoot) {

        if (appRoot == null || !appRoot.exists() || !appRoot.isDirectory()) {
            throw new AppFileException("Application root '"+appRoot.getAbsolutePath()+"' not valid");
        }
        if (jobFolder == null || !jobFolder.isDirectory() || ".git".equals(jobFolder.getName())
                || "scripts".equals(jobFolder.getName())|| "logs".equals(jobFolder.getName())) {
            throw new AppFileException("Application folder '"+jobFolder.getAbsolutePath()+"' not valid");
        }

        this.jobFolder = jobFolder;
        this.appRoot = appRoot;
        this.job_base_name = jobFolder.getName();
        File temp = new File(jobFolder, LL_JOB_DESCRIPTION_FILE);
        if ( temp.exists() && temp.isFile() ) {
            llJobDescription = temp;
        } else {
            llJobDescription = null;
        }
        temp = new File(jobFolder, GRISU_JOB_DESCRIPTION_FILE);
        if ( temp.exists() && temp.isFile() ) {
            grisuJobDescription = temp;
        } else {
            grisuJobDescription = null;
        }
        temp = new File(jobFolder, SLURM_JOB_DESCRIPTION_FILE);
        if ( temp.exists() && temp.isFile() ) {
            slurmJobDescription = temp;
        } else {
            slurmJobDescription = null;
        }
        temp = new File(jobFolder, INPUT_FILES_DIRECTORY_NAME);
        if ( temp.exists() && temp.isDirectory() ) {
            inputFilesFolder = temp;
            Collection<File> temp2 = FileUtils.listFiles(inputFilesFolder, null, true);
            for ( File f : temp2 ) {
                inputFiles.add(f);
            }
        } else {
            inputFilesFolder = null;
        }

    }

    public File getJobFolder() {
        return jobFolder;
    }

    public String getJobBaseName() {
        return job_base_name;
    }

    public File getGrisuJobDescription() {
        return grisuJobDescription;
    }

    public File getLoadlevelerJobDescription() {
        return llJobDescription;
    }

    public File getSlurmJobDescription() {
        return slurmJobDescription;
    }

    public String getLl() {
        try {
            if ( getLoadlevelerJobDescription() != null ) {
                return FileUtils.readFileToString(getLoadlevelerJobDescription());
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSlurm() {
        try {
            if ( getSlurmJobDescription() != null ) {
                return FileUtils.readFileToString(getSlurmJobDescription());
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getGrisu() {
        try {
            if ( getGrisuJobDescription() != null ) {
                return FileUtils.readFileToString(getGrisuJobDescription());
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<File> getInputFiles() {
        return inputFiles;
    }

}
