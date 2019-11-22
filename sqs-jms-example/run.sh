#!/usr/bin/env bash

AWS_PROFILE=default
DEBUG=
LOG_LEVEL=

while (( "$#" )); do
    case "$1" in
        -p|--profile)
            AWS_PROFILE=$2
            shift 2
            ;;
        -d|--debug)
            DEBUG=-Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005"
            LOG_LEVEL=-Dlogging.level.com.sheerid=DEBUG
            shift
            ;;
        -*|--*=) # unsupported flags
            echo "Error: Unsupported option $1" >&2
            exit 1
            ;;
    esac
done

export AWS_PROFILE

mvn spring-boot:run $DEBUG $LOG_LEVEL

