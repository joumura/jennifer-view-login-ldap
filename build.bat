@echo off
rem set your own JAVA_HOME
set JAVA_HOME=C:\Programs\Oracle\jdk-8u77-windows-x64

rem create the .mvn/jvm.config file if you set your http.proxy.
rem [[ .mvn/jvm.config ]]
rem -Dhttp.proxyHost=your.proxy.host
rem -Dhttp.proxyPort=8080
rem -Dhttps.proxyHost=your.proxy.host
rem -Dhttps.proxyPort=8080

rem create the .mvn/settings.xml file if you use your own maven repository.
rem [[ .mvn/settings.xml ]]
rem   <localRepository>/path/to/local/repo</localRepository>
rem
rem mvnw.cmd package -s .mvn/settings.xml

mvnw.cmd package
pause
