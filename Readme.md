# Requirments

 * a populated application repository, with modules in the appropriate place (e.g. https://github.com/nesi/applications)
 * Java (version 6, version 7 not tested)
 * git (optional)
 * custom templates for application pages & application-list pages

# Notes

 * Documentation on how to create templates and which values are available, check out the comments in the 2 default templates
   * application page template: https://github.com/nesi/app-manage/blob/develop/application.md.vm
   * application list page template: https://github.com/nesi/app-manage/blob/develop/list_template.md.vm

# Usage

## Import modules into application repository (if not already there)

    app-manage -v import-modules -t Auckland/pan -i /share/apps/Modules/modulefiles/Apps

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

