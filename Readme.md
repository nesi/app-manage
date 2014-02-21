External templates can be referenced by name up until the first . in the filename (to lowercase)

create doc for one application, write to stdout
java -jar target/app-manage-binary.jar  -a /home/markus/src/config/applications create-doc --applications Abaqus --template /home/markus/src/Utils/app-manage/application.md.vm

