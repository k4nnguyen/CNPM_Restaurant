$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectRoot

$jdbcJar = Join-Path $projectRoot "lib\mssql-jdbc-12.8.1.jre11.jar"

if (-not (Test-Path $jdbcJar)) {
    throw "Khong tim thay JDBC driver tai $jdbcJar"
}

New-Item -ItemType Directory -Force -Path target\classes | Out-Null
Remove-Item -Recurse -Force target\classes\* -ErrorAction SilentlyContinue
$files = Get-ChildItem src\main\java -Recurse -Filter *.java | ForEach-Object { $_.FullName }
& javac --release 17 -cp $jdbcJar -d target\classes $files
if ($LASTEXITCODE -ne 0) { throw "Compile that bai" }

& java -cp "target\classes;$jdbcJar" com.mycompany.restaurant_module.Restaurant_Module
