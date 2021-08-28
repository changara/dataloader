#/usr/bin/env python3

import requests, zipfile, io, shutil, os, sys
import subprocess
from bs4 import BeautifulSoup
from os.path import expanduser

####################################################################
# Usage:
# python3 swtinstall.py <git clone root>
# 
# Prerequisites:
# - Directory containing mvn command in PATH environment variable.
# - Python BeautifulSoup installed locally. Run 'pip3 install beautifulsoup4'
# - Zip contents are extracted in ~/Downloads directory.
#
#
# Outline of the steps taken:
# - Start at https://download.eclipse.org/eclipse/downloads/
# - Go to "Latest Release" section
# - Click on the first link in the "Build Name" column
# - Go to "SWT Binary and Source" section
# - Click on the links next to "Windows (64 bit version)", "Mac OSX (64 bit version)", and "Mac OSX (64 bit version for Arm64/AArch64)"
# - Extract the contents of the zip file
# - Go to the extraction folder and run mvn install:install-file command
#
####################################################################

def is_exe(fpath):
    return os.path.isfile(fpath) and os.access(fpath, os.X_OK)

def which(program):
    fpath, fname = os.path.split(program)
    if fpath:
        if is_exe(program):
            return program
    else:
        for path in os.environ["PATH"].split(os.pathsep):
            exe_file = os.path.join(path, program)
            if is_exe(exe_file):
                return exe_file

    return None

def getSWTDownloadLinkForPlatform(soup, platformString):
    results = soup.find(id="SWT").find_next("td").string
    while results != None and results != platformString :
        results = results.find_next("td").string

    if results == platformString :
        results = results.find_next("a")['href']

    return results

######## end of getSWTDownloadLinkForPlatform ##########

def downloadAndExtractZip(url):
    zipfileName = url.split('=',1)[1]

    home = expanduser("~")
    unzippedDirName = home + "/Downloads/" + zipfileName.removesuffix('.zip') + "/"
    print(unzippedDirName)

    page = requests.get(url)
    soup = BeautifulSoup(page.content, "html.parser")
    zipURL = soup.find("meta").find_next("a")['href']
#    print(zipURL)

    page = requests.get(zipURL)
    soup = BeautifulSoup(page.content, "html.parser")
    zipURL = soup.find(id="novaContent").find_next("a").find_next("a")['href']
    zipURL = "https://www.eclipse.org/downloads/" + zipURL
#    print(zipURL)

    # navigate the redirect to the actual mirror
    page = requests.get(zipURL)
    soup = BeautifulSoup(page.content, "html.parser")
    zipURL = soup.find('meta', attrs={'http-equiv': 'Refresh'})['content'].split(';')[1].split('=')[1]
#    print(zipURL)
    
    # delete existing content
    if os.path.exists(unzippedDirName) and os.path.isdir(unzippedDirName):
        shutil.rmtree(unzippedDirName)
    response = requests.get(zipURL, stream=True)
    z = zipfile.ZipFile(io.BytesIO(response.content))
    z.extractall(unzippedDirName)

    return unzippedDirName

######## end of downloadAndExtractZip ##########

def installInLocalMavenRepo(unzippedSWTDir, mvnArtifactId, gitCloneRootDir):
#     command to execute
    swtVersion = unzippedSWTDir.split('-')[1]
#    print(swtVersion)

    if which("mvn") == None :
        print("did not find mvn command in the execute path")
        sys.exit(2)
        
    mavenCommand = "mvn install:install-file " \
                    + "-Dfile=" + unzippedSWTDir + "swt.jar " \
                    + "-DgroupId=local.swt " \
                    + "-DartifactId=" + mvnArtifactId + " " \
                    + "-Dversion=" + swtVersion + " " \
                    + "-Dpackaging=jar " \
                    + "-Dmaven.repo.local=" + gitCloneRootDir + "/local-proj-repo"
#    print(mavenCommand)
    subprocess.run(mavenCommand, shell=True)

######## end of installInLocalMavenRepo  ##########

if (len(sys.argv) < 2):
   print('Usage: python3 swtinstall.py <git clone root>')
   sys.exit(2)

URL = "https://download.eclipse.org/eclipse/downloads/"
page = requests.get(URL)

soup = BeautifulSoup(page.content, "html.parser")
results = soup.find(id="Latest_Release").find_next("a")['href']

downloadsPage = URL + results
page = requests.get(downloadsPage)
soup = BeautifulSoup(page.content, "html.parser")
results = getSWTDownloadLinkForPlatform(soup, "Windows (64 bit version)")
unzippedDir = downloadAndExtractZip(downloadsPage + results)
installInLocalMavenRepo(unzippedDir, "swtwin32_x86_64", sys.argv[1])

results = getSWTDownloadLinkForPlatform(soup, "Mac OSX (64 bit version)")
downloadAndExtractZip(downloadsPage + results)
installInLocalMavenRepo(unzippedDir, "swtmacx86_64", sys.argv[1])

results = getSWTDownloadLinkForPlatform(soup, "Mac OSX (64 bit version for Arm64/AArch64)")
downloadAndExtractZip(downloadsPage + results)
installInLocalMavenRepo(unzippedDir, "swtmacarm64", sys.argv[1])

