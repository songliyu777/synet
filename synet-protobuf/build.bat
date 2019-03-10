SET SRC_DIR=proto
"protoc-3.7.0-win64/bin/protoc.exe" -I=%SRC_DIR% --java_out=Java/ %SRC_DIR%/test.proto