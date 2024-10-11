# a script to fix messages_es.properties encoding if Transifex converts it to utf-8
#
# check if you need to run this by seeing if the following command:
# file -I src/main/resources/messages_es.properties
#
# returns:
# src/main/resources/messages_es.properties: text/html; charset=utf-8
#
# if it does, run this script and then re-run the command above. It should return:
# src/main/resources/messages_es.properties: text/html; charset=iso-8859-1
#
# add, commit, and push your properly encoded messages_es.properties file!

iconv -f utf-8 -t iso-8859-1 < src/main/resources/messages_es.properties > src/main/resources/messages_es.properties.new
rm src/main/resources/messages_es.properties
mv src/main/resources/messages_es.properties.new src/main/resources/messages_es.properties