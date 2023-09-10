native:
	scala-cli --power package --native --native-mode release --force . --main-class sha1 -o target/sha1-native

jvm:
	scala-cli --power package --force . --main-class sha1 -o target/sha1-jvm

graalvm:
	scala-cli --power package --native-image --force . --main-class sha1 -o target/sha1-graalvm

perf-native:
	/usr/bin/time -pl target/sha1-native $(file_name)

perf-jvm:
	/usr/bin/time -pl target/sha1-jvm $(max_heap_size) $(file_name)

perf-graalvm:
	/usr/bin/time -pl target/sha1-graalvm $(max_heap_size) $(file_name)

perf-shasum:
	/usr/bin/time -pl shasum -a 1 $(file_name)

file_name = /Users/vitaly/Downloads/file.mkv
max_heap_size = -J-Xmx100m

build-stats:
	scala-cli --power package --native --force . --main-class stats -o target/stats

