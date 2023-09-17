sha1-native:
	scala-cli --power package --native --native-mode release --force . --main-class sha1 -o target/sha1-native

sha1-jvm:
	scala-cli --power package --assembly --force . --main-class sha1 -o target/sha1-jvm

sha1-graalvm:
	scala-cli --power package --native-image --force . --main-class sha1 -o target/sha1-graalvm

perf-native:
	/usr/bin/time -pl target/sha1-native $(file_name)

run-jvm:
	/usr/bin/time -pl target/sha1-jvm $(max_heap_size) $(file_name)

run-graalvm:
	/usr/bin/time -pl target/sha1-graalvm $(max_heap_size) $(file_name)

run-shasum:
	/usr/bin/time -pl shasum -a 1 $(file_name)

server-native:
	scala-cli --power package --native --native-mode release-fast --force http --main-class server -o target/server-native

server-graalvm:
	scala-cli --power package --native-image --force http --main-class server -o target/server-graalvm

server-jvm:
	scala-cli --power package --assembly --force http --main-class server -o target/server-jvm

run-server-native:
	/usr/bin/time -pl target/server-native

run-server-jvm:
	/usr/bin/time -pl target/server-jvm $(max_heap_size)

client-native:
	scala-cli --power package --native --native-mode release-fast --force http --main-class client -o target/client-native

client-jvm:
	scala-cli --power package --assembly --force http --main-class client -o target/client-jvm

run-client-native:
	/usr/bin/time -pl target/client-native "Hello World"

run-client-jvm:
	/usr/bin/time -pl target/client-jvm $(max_heap_size) "Hello World"

k8s-client-native:
	scala-cli --power package --native --native-mode release-fast --force k8s --main-class client -o target/k8s-client-native


file_name = /Users/vitaly/Downloads/file.mkv
max_heap_size = -J-Xmx100m

