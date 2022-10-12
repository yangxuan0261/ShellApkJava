#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
import sys

SelfPath: str = os.path.abspath(os.path.dirname(__file__))

if __name__ == "__main__":
    envName = "My_Python"
    PythonPath = os.environ.get(envName)  # 从环境变量中获取
    if PythonPath is None:
        raise Exception("Error: 配置 python 工具目录 环境变量 {}".format(envName))

    sys.path.append(PythonPath)  # 引入 python 脚本目录

    from tool.git_util import GitUtil
    os.chdir(SelfPath)
    GitUtil().safeUpdate("origin", "master")
    os.system("pause")
    sys.exit(0)
