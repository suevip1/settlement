#!/bin/zsh

# 获取脚本地址
SERVICE_HOME="${BASH_SOURCE-$0}"
SERVICE_HOME="$(dirname "${SERVICE_HOME}")"
SERVICE_HOME="$(cd "${SERVICE_HOME}"; pwd)"

# 实际项目配置
# 文件在项目中的位置：${project-name}/deployment/bin/xxx/project_env.sh
source "${SERVICE_HOME}"/settlement/project_env.sh

# jvm 配置
# 文件在项目中的位置：${project-name}/deployment/bin/xxx/jvm_options.sh
source "${SERVICE_HOME}"/jvm_options.sh

# 获取机器所处的环境
pay_module_cluster_file="${SERVICE_HOME}/.deploy/service.cluster.txt" # use01-v
pay_module_cluster=""
config_env="env/local"

# 获取命令行参数
action="$@"

function checkParam(){
    if [[ -z "${action}" ]]; then
        echo "no action[start|restart|stop]"
        exit 1
    else
      echo "${action}"
    fi
}

function checkEnv() {
    if [[ ! -f "${pay_module_cluster_file}" ]]; then
      echo "cluster ${pay_module_cluster_file} is not file"
      exit 1
    fi
}

function init() {
    getEnv
}

function getEnv() {
    pay_module_cluster=`cat ${pay_module_cluster_file}`
}

function getPid() {
    pidInfo=($(ps -ax|grep -v 'grep'|grep java |grep ${PROJECT_JAR_NAME}))
    if [ ! -n "$pidInfo" ]; then
      echo 0;
    else
      echo $pidInfo|awk '{print $1}'
    fi
}

# todo: shell 语法
function stopActionHandler() {
    local pid=`getPid`
    if [ ${pid} -ne 0 ]; then
      kill -9 ${pid}
      echo "${pid} is stopped"
    else
      echo "can not find ${pid}, maybe it is already stopped"
    fi
}

function selectEnvDir() {
    case ${pay_module_cluster} in
      "us01-sim"* )
        source ${SERVICE_HOME}/bin/jvm_options_sim.sh
        config_env="env/us01-sim"
        ;;
      "us01-v" | "us01-small-v" )
        source ${SERVICE_HOME}/bin/jvm_options.sh
        config_env="env/us01-production"
        ;;
      "us01-pre-v" )
        source ${SERVICE_HOME}/bin/jvm_options.sh
        config_env="env/us01-pre"
        ;;
      "de01-v" | "de01-pre-v" | "de01-small-v")
        source ${SERVICE_HOME}/bin/jvm_options.sh
        config_env="env/production-de01"
        ;;
      *)
        echo "unknown pay cluster ${pay_module_cluster}"
        exit 1
    esac
}

function startActionHandler() {
    local pid=`getPid`
    if [ $pid -ne 0 ]; then
      echo "warn: $service_name already start! (pid=$pid)"
    else
      selectEnvDir
      # todo fastjson 远程代码执行（RCE） 安全漏洞
      startCmd="$JAVA_HOME/bin/java $JVM_OPTS -jar ${PROJECT_JAR_NAME} -Dfastjson.parser.safeMode=true --spring.config.additional-location=classpath:/${config_env}"
      echo "startCmd="$startCmd
      # todo 2>&1 错误重定向
      nohup $JAVA_HOME/bin/java $JVM_OPTS -DCLUSTER=${pay_module_cluster} -jar ${PROJECT_JAR_NAME} -Dfastjson.parser.safeMode=true --spring.config.additional-location=classpath:/${config_env}/ >> server.out.$(date "+%Y%m%d%H") 2>&1 &
      pid= `getPid`
      if [ $pid -ne 0 ]; then
        echo "(pid=${pid}) [OK]"
      else
        echo "[Failed]"
      fi
    fi
}

# 启动命令
checkParam
checkEnv
init
if [[ ${action} == "stop" ]]; then
  stopActionHandler
elif [[ ${action} == "start" ]]; then
  startActionHandler
elif [[ ${action} == "restart" ]]; then
  stopActionHandler
  startActionHandler
fi






