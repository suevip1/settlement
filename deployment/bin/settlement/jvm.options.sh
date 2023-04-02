#!/bin/zsh

# java path
export java_home="/usr/local/jdk1.8.0_65"
export jre_home="${java_home}"/jre

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

GC_LOG="$service_home/logs/gc.$(data "+%Y%m%d%H%M%S".%N).log"