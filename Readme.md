# Requirments

 * a populated application repository, with modules in the appropriate place (e.g. https://github.com/nesi/applications)
 * Java (version 6, version 7 not tested)
 * git (optional)
 * custom templates for application pages & application-list pages

# Usage

## Import modules into application repository (if not already there)

    app-manage -v import-modules -t Auckland/pan -i /share/apps/Modules/modulefiles/Apps

## Generate documentation

### For one application, write to stdout

    app-manage -a <path_to_app_repo> create-doc --applications <appname> --template <path_to_template>

For example, use the [default template](

### For all applications, write files to directory

    app-manage -a <path_to_app_repo> create-doc --template <path_to_template> -o <path_to_output_dir>

### Create summary page, write to stdout

    app-manage



External templates can be referenced by name up until the first . in the filename (to lowercase)

create doc for one application, write to stdout
java -jar target/app-manage-binary.jar  -a /home/markus/src/config/applications create-doc --applications Abaqus --template /home/markus/src/Utils/app-manage/application.md.vm

create list
-a /home/markus/src/config/applications create-list --template /home/markus/src/Utils/app-manage/list_template.md.vm


Create app pages, then summary
java -jar target/app-manage-binary.jar  -a /home/markus/src/config/applications create-doc --template /home/markus/src/Utils/app-manage/application.md.vm -o /home/markus/doc/test/apps

java -jar target/app-manage-binary.jar  -a /home/markus/src/config/applications create-list --template /home/markus/src/Utils/app-manage/list_template.md.vm -o /home/markus/doc/test/list.md
or
java -jar target/app-manage-binary.jar  -a /home/markus/src/config/applications create-list -a /home/markus/src/config/applications create-list --template /home/markus/src/Utils/app-manage/list_template.md.vm --tags top_app,Programming