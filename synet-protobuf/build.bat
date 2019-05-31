SET SRC_DIR=proto
"protoc-3.7.0-win64/bin/protoc.exe" -I=%SRC_DIR% --java_out=src/main/java/ %SRC_DIR%/syprotocol.proto
"protoc-3.7.0-win64/bin/protoc.exe" -o %SRC_DIR%/syprotocol.pb %SRC_DIR%/syprotocol.proto