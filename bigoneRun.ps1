# setting some Paths
$JDKPATHLINUX='/usr/lib/jvm/java-1.25.0-openjdk-amd64'
$JDKPATHWINDOWS='C:\somewereelse'

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

Write-Host '...running Bigone...'
java `@bigoneRunArgs
