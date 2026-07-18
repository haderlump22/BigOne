Param (
    [switch] $WithDoc,
    [switch] $WithJar,
    [switch] $WithSigning,
    [switch] $CreateAppCli,
    [switch] $CreateAppServer,
    [switch] $CreateAppServerInstaller
)

# setting some Paths
$JDKPATHLINUX='/usr/lib/jvm/java-1.25.0-openjdk-amd64'
$JDKPATHWINDOWS='C:\Users\Public\D\jdk-24.0.1'

# for Timemesure
$startTime = Get-Date

Write-Host '...setting the JAVA ENVs...'
if ($IsLinux) {
    if (Test-Path -Path $JDKPATHLINUX) {
        $env:JAVA_HOME = $JDKPATHLINUX
        #$env:JAVA_HOME
        $env:PATH = $env:JAVA_HOME + "/bin:" + $env:PATH
        #$env:PATH
    } else {
        Write-Host '...JDK Path '$JDKPATHLINUX' not found'
        Exit
    }

} else {
    if (Test-Path -Path $JDKPATHWINDOWS) {
        $env:JAVA_HOME = $JDKPATHWINDOWS
        #$env:JAVA_HOME
        $env:PATH = $env:JAVA_HOME + "\bin:" + $env:PATH
        #$env:PATH
    } else {
        Write-Host '...JDK Path '$JDKPATHWINDOWS' not found'
        Exit
    }
}

if ($null -eq $env:JAVA_HOME) {
    Write-Host '..setting first the JAVA_HOME ENV!!!'
    Exit
}

Write-host '...cleaning...'
if (Test-Path -Path 'mods') {
    Write-Host '..remove mods..'
    Remove-Item mods -Recurse -Force -ProgressAction SilentlyContinue
}

if (Test-Path -Path 'jar') {
    Write-Host '..remove jar..'
    Remove-Item jar -Recurse -Force -ProgressAction SilentlyContinue
}

if (Test-Path -Path 'documentation') {
    Write-Host '..remove documentation..'
    Remove-Item documentation -Recurse -Force -ProgressAction SilentlyContinue
}

if (Test-Path -Path 'customjre') {
    Write-Host '..remove customjre..'
    Remove-Item customjre -Recurse -Force -ProgressAction SilentlyContinue
}

if (Test-Path -Path 'RachelCsvParser') {
    Write-Host '..remove old CLI App..'
    Remove-Item RachelCsvParser -Recurse -Force -ProgressAction SilentlyContinue
}

if (Test-Path -Path 'RachelCsvParserServer') {
    Write-Host '..remove old Server App..'
    Remove-Item RachelCsvParserServer -Recurse -Force -ProgressAction SilentlyContinue
}

Write-Host '...create keystore...'
if (!(Test-Path -Path 'keys.jks')) {
    Start-Process -NoNewWindow -FilePath keytool -ArgumentList '-genkeypair', '-alias jarkey', '-keyalg Ed25519', '-keystore keys.jks', '-validity 365', '-storepass pupupu', '-keypass pupupu', '-dname "cn=Normen Rachel, ou=CS, o=isp-insoft GmbH, c=DE"' -Wait
} else {
    Write-Host '...keyfile still exist...nothing to do...'
}

if ($WithDoc) {
    Write-Host '...create documentation...'
    javadoc `@docArgs
}

Write-Host '...building Modules...'
javac `@libArgs
javac `@cliArgs
javac `@serverArgs

if ($WithJar) {
    Write-Host '..create jar''s...'
    jar `@libJarArgs
    jar `@cliJarArgs
    jar `@serverJarArgs
}

if ($WithSigning) {
    Write-Host '..signing jar''s...'
    jarsigner -verbose -keystore keys.jks -storepass pupupu -keypass pupupu jar/de.rachel.cli.jar jarkey
    jarsigner -verbose -keystore keys.jks -storepass pupupu -keypass pupupu jar/de.rachel.lib.jar jarkey
    jarsigner -verbose -keystore keys.jks -storepass pupupu -keypass pupupu jar/de.rachel.server.jar jarkey

    Write-Host '...verifying jars''s...'
    # java -XshowSettings is returned de for Language, but i has to use en for this call
    # but i don't know why this is nessesary, the certs was build on the same system
    # without this option....
    # where came this nessesary for verifiy from?
    jarsigner -verify "-J-Duser.language=en" -verbose -certs jar/de.rachel.cli.jar
    jarsigner -verify "-J-Duser.language=en" -verbose -certs jar/de.rachel.lib.jar
    jarsigner -verify "-J-Duser.language=en" -verbose -certs jar/de.rachel.server.jar
}

#Write-Host '...creating custom jre...'
#jlink `@cliLinkArgs

if ($CreateAppCli) {
    Write-Host '...creating CLI App...'
    if ($IsLinux) {
        Write-Host '...for Linux...'
        jpackage `@jpackArgs `@jpackLinuxArgs
    } else {
        Write-Host '...for Windows...'
        jpackage `@jpackArgs `@jpackWindowsArgs
    }
}

if ($CreateAppServer) {
    Write-Host '...creating Server App...'
    if ($IsLinux) {
        Write-Host '...for Linux...'
        jpackage `@serverJpackArgs `@serverJpackLinuxArgs
    } else {
        Write-Host '...for Windows...'
        jpackage `@serverJpackArgs `@serverJpackWindowsArgs
    }
}

if ($CreateAppServerInstaller) {
    Write-Host '...creating Installer...'
    if ($IsLinunx) {
        Write-Host '...for Linux...'
        jpackage `@installerJpackArgs `@installerJpackLinuxArgs
    } else {
        Write-Host '...for Windows...'
        jpackage `@installerJpackArgs `@installerJpackWinArgs
    }
# make only sense when jpackage creates a setup not an app-image
#Write-Host '####### WIN INFO#########"'
#Write-Host 'Under run the Setup and follow the instruktions'
#Write-Host 'To uninstall it use your Software Dashboard'
#Write-Host '#########################"'
#Write-Host '####### LINUX INFO#######"'
#Write-Host 'Under Linux you can install the Package with "sudo dpkg -i <packagefilename>"'
#Write-Host 'and uninstall with "sudo dpkg -r <packagename>"'
}
#Write-Host '..running from jars...'
#java `@runArgs


# show the used time for the Buildprocess
$endTime = Get-Date
$duration = New-TimeSpan -Start $startTime -End $endTime
Write-Host '===Ausführungszeit: '$($duration.TotalSeconds)' Sekunden==='
