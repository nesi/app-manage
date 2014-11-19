# Requirements

 * a populated application repository, with modules in the appropriate place (e.g. https://github.com/nesi/applications)
 * Java
 * maven
 * git (optional)
 * custom templates for application pages & application-list pages

# Notes

 * Documentation on how to create templates and which values are available, check out the comments in the 2 default templates
   * application page template: https://github.com/nesi/app-manage/blob/develop/application.md.vm
   * application list page template: https://github.com/nesi/app-manage/blob/develop/list_template.md.vm

# Building

    # checkout
    git clone git@github.com:nesi/app-manage.git

    # build
    mvn clean install   # creates binary in target/app-manage-binary.jar

    # create deb & rpm packages
    mvn clean install -Pdeb,rpm # creates target/app-manage-xxx.deb & target/rpm/app-manage/RPMS/noarch/app-manage-xxx.noarch.rpm

# Usage

## Import modules into application repository (if not already there)

    app-manage -v import-modules -t Auckland/pan -i /share/apps/Modules/modulefiles/Apps
    
If you have several locations where modules are stored and you want to tag each of those with different tags, you can do so via:

    app-manage -v import-modules -t Auckland/pan -i /share/apps/Modules/modulefiles/Apps --ensure-tags app[,otherTag]
    app-manage -v import-modules -t Auckland/pan -i /share/apps/Modules/modulefiles/Compilers --ensure-tags compiler[,otherTag]

### Delete modules that are in the application repository but not in the modules folder (anymore)

    app-manage -v import-modules -t Auckland/pan -i /share/apps/Modules/modulefiles/Apps --sync
    
This will delete all the modules under '<application>/modules/Auckland/pan' for which no equivalent in the modules folder exists. If the modules folder is empty, all modules will be deleted. If no folder for an application exists in the module folder, nothing will be deleted.

## Generate documentation

### For one application, write to stdout

    app-manage -a <path_to_app_repo> create-doc --applications <appname> --template <path_to_template>

For example, download the [default template](https://github.com/nesi/app-manage/blob/develop/application.md.vm) into
your home directory, to create the documentation page for MATLAB, you'd issue:

    app-manage -a /home/markus/applications create-doc --applications MATLAB --template /home/markus/applications.md.vm

### For all applications, write files to directory

    app-manage -a <path_to_app_repo> create-doc --template <path_to_template> -o <path_to_output_dir>

Example with the default template in the home directory:

    app-manage -a /home/markus/applications create-doc --template /home/markus/applications.md.vm -o /home/markus/applications.wiki/apps

### Create summary page, write to stdout or file

This will create a page that lists (and links to) all applications, and writes to stdout (or, if you specify '-o', to a file).

    app-manage -a <path_to_app_repo> create-list --template <path_to_list_template> [-o <path_to_list_page_file>]

For example, download the [default list template](https://github.com/nesi/app-manage/blob/develop/list_template.md.vm) into
your home directory, to create the list page:

    app-manage -a /home/markus/applications create-list --template /home/markus/list_template.md.vm

Or, to write the same thing to a file:

    app-manage -a /home/markus/applications create-list --template /home/markus/list_template.md.vm -o /home/markus/applications.wiki/list.md

### Create summary page, only consider applications with a certain tag

This will create a page that lists (and links to) applications that are marked with one or more of the specified tags,
then writes to stdout (or, if you specify '-o', to a file).

    app manage -a <path_to_app_repo> create-list --template <path_to_template> --tags <comma-seperated-list-of-tags-to-include>

For example, download the [default list template](https://github.com/nesi/app-manage/blob/develop/list_template.md.vm) into
your home directory, to create the list page for 'top_app' applications:

    app manage -a /home/markus/src/config/applications create-list --template /home/markus/doc/list_template.md.vm --tags top_app

