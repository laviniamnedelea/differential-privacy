package(default_visibility = [
    "//visibility:public",
])

java_plugin(
    name = "autovalue_plugin",
    processor_class = "com.google.auto.value.processor.AutoValueProcessor",
    deps = [
        "@maven//:com_google_auto_value_auto_value",
    ],
)

java_library(
    name = "autovalue",
    exported_plugins = [
        ":autovalue_plugin",
    ],
    exports = [
        "@maven//:com_google_auto_value_auto_value",
    ],
)

java_library(
    name = "differentialprivacy",
    srcs = glob(["*.java"]),
    deps = [
        ":autovalue",
        "@com_google_differential_privacy//proto:data_java_proto",
        "@com_google_differential_privacy//proto:summary_java_proto",
        "@maven//:com_google_auto_value_auto_value",
        "@maven//:com_google_auto_value_auto_value_annotations",
        "@maven//:com_google_code_findbugs_jsr305",
        "@maven//:com_google_errorprone_error_prone_annotations",
        "@maven//:com_google_guava_guava",
        "@maven//:com_google_protobuf_protobuf_java",
        "@maven//:org_apache_commons_commons_math3",
    ],
)
