#!/bin/bash

source gradle.properties

_version=${version}

_base=/opt
_openbaton_base="${_base}/openbaton"
_message_queue_base="apache-activemq-5.11.1"
_openbaton_config_file=/etc/openbaton/openbaton.properties

function start_activemq_linux {
    sudo ${_openbaton_base}/${_message_queue_base}/bin/activemq start
}

function start_activemq_osx {
    sudo ${_openbaton_base}/${_message_queue_base}/bin/macosx/activemq start
}

function check_activemq {
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
	ps -a | grep -v grep | grep activemq > /dev/null
    	result=$?
        if [ "${result}" -eq "0" ]; then
         	echo "activemq service running, everything is fine"
        else
          	echo "activemq is not running, starting it:"
            	start_activemq_linux
        fi
    elif [[ "$OSTYPE" == "darwin"* ]]; then
	ps aux | grep -v grep | grep activemq > /dev/null
        result=$?
         if [ "${result}" -eq "0" ]; then
          	echo "activemq service running, everything is fine"
         else
           	echo "activemq is not running, starting it:"
            	start_activemq_osx
         fi
    fi
}

function start_mysql_osx {
    sudo /usr/local/mysql/support-files/mysql.server start
}

function start_mysql_linux {
    sudo service mysql start
}


function check_mysql {
    if [[ "$OSTYPE" == "linux-gnu" ]]; then
	resul=$(pgrep mysql | wc -l);
        if [ "${result}" -eq "0" ]; then
                read -p "mysql is down, would you like to start it ([y]/n):" yn
		case $yn in
			[Yy]* ) start_mysql_linux ; break;;
			[Nn]* ) echo "you can't proceed withuot having mysql up and running" 
				exit;;
			* ) start_mysql_linux;;
		esac
        else
                echo "mysql is already running.."
        fi
    elif [[ "$OSTYPE" == "darwin"* ]]; then
	mysqladmin status
	result=$?
        if [ "${result}" -eq "0" ]; then
                echo "mysql service running..."
        else
                read -p "mysql is down, would you like to start it ([y]/n):" yn
                case $yn in
                        [Yy]* ) start_mysql_osx ; break;;
                        [Nn]* ) exit;;
                        * ) start_mysql_osx;;
                esac
        fi
    fi
}

function start {

    if [ ! -d build/  ]
        then
            compile
    fi

    check_activemq
    check_mysql
    if [ 0 -eq $? ]
        then
	    screen -c .screenrc -d -m -S openbaton -t nfvo java -jar "$_openbaton_base/nfvo/build/libs/openbaton-$_version.jar" --spring.config.location=file:${_openbaton_config_file}
#            screen -d -m -S openbaton -p 0 -X screen -t nfvo java -jar "$_openbaton_base/nfvo/build/libs/openbaton-$_version.jar" --spring.config.location=file:${_openbaton_config_file}
	    screen -c .screenrc -r -p 0
    fi
}

function stop {
    if screen -list | grep "openbaton"; then
	    screen -S openbaton -p 0 -X stuff "exit$(printf \\r)"
    fi
}

function kill {
    if screen -list | grep "openbaton"; then
	    screen -X -S openbaton kill
    fi
}


function compile {
    ./gradlew build -x test 
}

function tests {
    ./gradlew test
}

function clean {
    ./gradlew clean
}

function end {
    exit
}
function usage {
    echo -e "Open-Baton\n"
    echo -e "Usage:\n\t ./openbaton.sh <option>\n\t"
    echo -e "where option is"
    echo -e "\t\t * compile"
    echo -e "\t\t * start"
    echo -e "\t\t * stop"
    echo -e "\t\t * test"
    echo -e "\t\t * kill"
    echo -e "\t\t * clean"
}

##
#   MAIN
##

if [ $# -eq 0 ]
   then
        usage
        exit 1
fi

declare -a cmds=($@)
for (( i = 0; i <  ${#cmds[*]}; ++ i ))
do
    case ${cmds[$i]} in
        "clean" )
            clean ;;
        "sc" )
            clean
            compile
            start ;;
        "start" )
            start ;;
        "stop" )
            stop ;;
        "compile" )
            compile ;;
        "kill" )
            kill ;;
        "test" )
            tests ;;
        * )
            usage
            end ;;
    esac
    if [[ $? -ne 0 ]]; 
    then
	    exit 1
    fi
done

