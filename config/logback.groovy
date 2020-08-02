import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.ThresholdFilter

def APP_NAME = "dob-bot-port"
def PATH_LOG = "logs"
def FILE_PATTERN = "%d{yyyy-MM-dd}_%i.log.gz"
def MAX_FILE_SIZE = "100MB"
def ENCODER_PATTERN = "%date{dd-MM-yy HH:mm:ss:SSS} %level [%thread] %logger{10} [%file:%line] %X{GUID} %msg%n"
int MAX_HISTORY = 1

appender("COMMANDS", RollingFileAppender) {
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${PATH_LOG}/${APP_NAME}-commands-${FILE_PATTERN}"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = MAX_FILE_SIZE
        }
        maxHistory = MAX_HISTORY
    }
    encoder(PatternLayoutEncoder) {
        pattern = "${ENCODER_PATTERN}"
    }
    filter(ThresholdFilter) {
        level = DEBUG
    }
}

appender("APP-INFO", RollingFileAppender) {
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = "${PATH_LOG}/${APP_NAME}-app-info-${FILE_PATTERN}"
        timeBasedFileNamingAndTriggeringPolicy(SizeAndTimeBasedFNATP) {
            maxFileSize = MAX_FILE_SIZE
        }
        maxHistory = MAX_HISTORY
    }
    encoder(PatternLayoutEncoder) {
        pattern = "${ENCODER_PATTERN}"
    }
    filter(ThresholdFilter) {
        level = INFO
    }
}

appender("STDOUT", ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        pattern = "${ENCODER_PATTERN}"
    }
}

logger("bot.telegram.service.command", DEBUG, ["COMMANDS"], false)
logger("bot.telegram.service.initialization", DEBUG, ["COMMANDS"], false)
logger("bot.telegram.controller", DEBUG, ["COMMANDS"], false)
logger("bot.telegram.service.delay", DEBUG, ["COMMANDS"], false)
logger("bot.App", INFO, ["APP-INFO", "STDOUT"], false)
root(INFO, ["APP-INFO", "STDOUT"])