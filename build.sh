#!/bin/zsh
set -e

mvn clean

# 当前 module 模块名
MODULE_NAME=settlement

# build.sh 所在文件的目录创建一个 output 目录
SCRIPT_DIR="${BASH_SOURCE-$0}"
SCRIPT_DIR="$(dirname "${SCRIPT_DIR}")"
SCRIPT_DIR="$(cd "${SCRIPT_DIR}"; pwd)"
OUTPUT_DIR="${SCRIPT_DIR}/output"
SHELL_DIR="${OUTPUT_DIR}/bin"

export JAVA_HOME="/usr/local/jdk1.8.0_65"
export MAVEN_OPTS="-Xms256m -Xmx1g -Xss20m"


# -U:强制检查所有 SNAPSHOT 依赖
# -e:构建异常是，打印完整的 stack trace
# -B:批处理模式构建项目
BUILD_COMMAND="mvn package -Dmaven.test.skip=true -B -e -U -am -pl ${MODULE_NAME}"

${BUILD_COMMAND}

rm -rf ${OUTPUT_DIR}
mkdir -p ${OUTPUT_DIR}
mkdir -p ${SHELL_DIR}
cp -rf ${SCRIPT_DIR}/target ${OUTPUT_DIR}
cp ${SCRIPT_DIR}/deployment/bin/control.sh ${OUTPUT_DIR}/
cp ${SCRIPT_DIR}/deployment/bin/jvm_options.sh ${SHELL_DIR}/
cp ${SCRIPT_DIR}/deployment/bin/jvm_options_sim.sh ${SHELL_DIR}/
cp ${SCRIPT_DIR}/deployment/bin/${MODULE_NAME}/project_env.sh ${SHELL_DIR}/
cp ${SCRIPT_DIR}/target/${MODULE_NAME}.jar ${OUTPUT_DIR}/
cp ${SCRIPT_DIR}/check_status.sh ${OUTPUT_DIR}/




