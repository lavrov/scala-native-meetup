hello-native:
	scala-cli --power package --native --native-mode release-fast --force hello.scala -o target/hello-native

hello-jvm:
	scala-cli --power package --assembly --force hello.scala -o target/hello-jvm

hello-graalvm:
	scala-cli --power package --jvm 17 --native-image --force hello.scala -o target/hello-graalvm

run-hello-native:
	/usr/bin/time -pl target/hello-native

run-hello-jvm:
	/usr/bin/time -pl target/hello-jvm $(j_max_heap_size)

run-hello-graalvm:
	/usr/bin/time -pl target/hello-graalvm

sha1-native:
	scala-cli --power package --native --native-mode release-fast --force sha1 -o target/sha1-native

sha1-jvm:
	scala-cli --power package --assembly --force sha1 -o target/sha1-jvm

sha1-graalvm:
	scala-cli --power package --native-image --force sha1  -o target/sha1-graalvm

run-sha1-native:
	/usr/bin/time -pl target/sha1-native $(file_name)

run-sha1-jvm:
	/usr/bin/time -pl target/sha1-jvm $(j_max_heap_size) $(file_name)

run-sha1-graalvm:
	/usr/bin/time -pl target/sha1-graalvm $(max_heap_size) $(file_name)

run-shasum:
	/usr/bin/time -pl shasum -a 1 $(file_name)

http-server-native:
	scala-cli --power package --native --native-mode release-fast --force http --main-class server -o target/http-server-native

http-server-graalvm:
	scala-cli --power package --native-image --force http --main-class server -o target/http-server-graalvm

http-server-jvm:
	scala-cli --power package --assembly --force http --main-class server -o target/http-server-jvm

run-http-server-native:
	/usr/bin/time -pl target/http-server-native

run-http-server-jvm:
	/usr/bin/time -pl target/http-server-jvm $(j_max_heap_size)

http-client-native:
	scala-cli --power package --native --native-mode release-fast --force http --main-class client -o target/http-client-native

http-client-jvm:
	scala-cli --power package --assembly --force http --main-class client -o target/http-client-jvm

http-run-client-native:
	/usr/bin/time -pl target/http-client-native "Hello World"

http-run-client-jvm:
	/usr/bin/time -pl target/http-client-jvm $(j_max_heap_size) "Hello World"

k8s-client-native:
	scala-cli --power package --native --native-mode release-fast --force k8s --main-class client -o target/k8s-client-native

run-grpc-server-native:
	/usr/bin/time -pl grpc/target/scala-3.3.0/grpc-out


file_name = /Users/vitaly/Downloads/file.mkv
max_heap_size = -Xmx100m
j_max_heap_size = -J$(max_heap_size)

