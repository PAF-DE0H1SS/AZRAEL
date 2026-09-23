cmake_minimum_required(VERSION 3.22.1)

project("app")

add_library(${CMAKE_PROJECT_NAME} SHARED
        native-lib.cpp
        features/Json.cpp
        features/Net.cpp)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)