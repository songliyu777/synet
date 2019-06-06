cd proto
"../protoc-3.7.0-win64/bin/protoc.exe" -I=. --java_out=../src/main/java/ syprotocol.proto sydatabase.proto
"../protoc-3.7.0-win64/bin/protoc.exe" -o sy.pb --include_imports syprotocol.proto sydatabase.proto
cd..