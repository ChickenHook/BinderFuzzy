import argparse
import json
import os
import os.path
from pathlib import Path
import subprocess


ADB_PATH = "adb"
PACKAGE_NAME = "org.chickenhook.binderfuzzy"
ACTIVITY_NAME = ".fuzzer.ui.FuzzerConsole"
SCRIPT_PATH_ON_DEVICE = "/sdcard/Android/data/" + PACKAGE_NAME + "/files/script.bf"
LOGS_PATH_ON_DEVICE = "/sdcard/Android/data/" + PACKAGE_NAME + "/files/logs"


def executeAdbCommand(cmd):
    out = subprocess.Popen(cmd.split(),
                           stdout=subprocess.PIPE,
                           stderr=subprocess.STDOUT)
    stdout,stderr = out.communicate()
    print(stdout)
    print(stderr)

def pullLogs():
    executeAdbCommand(ADB_PATH + " pull " + LOGS_PATH_ON_DEVICE)

def pushScript(script_path):
    executeAdbCommand(ADB_PATH + " push " + script_path + " " + SCRIPT_PATH_ON_DEVICE)

def installApp(apk_path):
    executeAdbCommand(ADB_PATH + " install -t " + apk_path)

def launchApp():
    executeAdbCommand(ADB_PATH + " shell am start -S -W -n " + PACKAGE_NAME+"/"+ACTIVITY_NAME)


def checkScript(script_path):
    script_file = Path(script_path)
    if not os.path.exists(script_file):
        print("Script file not found: " + script_path)
        quit()
    # just for validation
    with open(script_path, 'r') as f:
        script_content = json.load(f)
    # for distro in distros_dict:
    #     print(distro['Name'])


def main():
    print("Launching BinderFuzzy version: 1.0")
    parser = argparse.ArgumentParser(description='Process paths.')
    parser.add_argument('--fuzzy-apk', nargs='?', default='../apps/release/app-release.apk',
                        dest='apk_path',
                        help='path to binderfuzzy-release.apk')
    parser.add_argument('--script', nargs='?', dest='script_path',
                        help='path to action script')

    parser.add_argument('--pull-logs', dest='just_pull_logs',action="store_true",
                        help='just pull the test results')
    args = parser.parse_args()
    if args.just_pull_logs:
        pullLogs()
        quit()

    print("Using binderfuzzy: " + args.apk_path)
    print("Using script: " + args.script_path)
    checkScript(args.script_path)
    pushScript(args.script_path)
    installApp(args.apk_path)
    launchApp()

if __name__ == "__main__":
    main()
