#!/bin/zsh

# default service name
export SERVICE_HOME="$(cd "${SCRIPT_DIR}/.."; pwd)"

# java path
export JAVA_HOME="/usr/local/jdk1.8.0_65"
export JRE_HOME="${JAVA_HOME}"/jre

# java options
# 初始堆大小
export XMS="-Xms2g"
# 最大堆大小
export XMX="-Xmx4g"
# 年轻代大小
export XMN="-Xmn512m"
# 持久代大小
export X_PERM_SIZE="-XX:MetaspaceSize=64m"
# 持久代最大值
export X_MAX_PERM_SIZE="-XX:MaxMetaspaceSize=128m"
# Eden 区与 Survivor 区的大小比值
export X_SURVIVOR_PATIO="-XX:SurvivorRatio=4"

GC_LOG="$SERVICE_HOME/logs/gc.$(data "+%Y%m%d%H%M%S".%N).log"

JVM_OPTS="$XMS $XMX $XMN $X_PERM_SIZE $X_MAX_PERM_SIZE $X_SURVIVOR_RATIO -XX:+UseConcMarkSweepGC -XX:+ParallelRefProcEnabled -XX:+UseParNewGC -verbose:gc -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:$GC_LOG -XX:CMSInitiatingOccupancyFraction=40 -XX:+UseCMSInitiatingOccupancyOnly -XX:+CMSClassUnloadingEnabled -XX:+DisableExplicitGC -XX:+PrintPromotionFailure -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=$SERVICE_HOME/logs/oom.dump"
