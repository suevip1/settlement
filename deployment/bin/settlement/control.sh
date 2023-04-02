#!/bin/zsh

# 获取脚本地址
service_home="${BASH_SOURCE-$0}"
service_home="$(dirname "${service_home}")"
service_home="$(cd "${service_home}"; pwd)"

# 实际项目配置
# 文件在项目中的位置：${project-name}/deployment/bin/xxx/project_env.sh
source "${service_home}"/settlement/project_env.sh

# jvm 配置
# 文件在项目中的位置：${project-name}/deployment/bin/xxx/jvm_options.sh
source "${service_home}"/jvm_options.sh

echo "${service_home}"/jvm_options.sh



