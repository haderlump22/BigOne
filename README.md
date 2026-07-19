# BigOne
Tool to manage your Financials and calculate what you can save per Month (or not...O_o)

## Buildinfos
I use the openJDK 25. You need to adjust the path in the build and run script to match your environment.

```pwsh
# setting some Paths
$JDKPATHLINUX='/usr/lib/jvm/java-1.25.0-openjdk-amd64'
$JDKPATHWINDOWS='C:\somewereelse'
```

To build correctly, you need to create a *lib* Folder. Search in Web for the jar's that you find in the --class-path line of File *bigoneBuildArgs*. If you found them copy it in the *lib* Folder.
