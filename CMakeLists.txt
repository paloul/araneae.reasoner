cmake_minimum_required(VERSION 3.19)
project(araneae_reasoner)

set(CMAKE_CXX_STANDARD 17)

add_executable(araneae_reasoner main.cpp)

# Import library target 'caf'
find_package(CAF COMPONENTS core io REQUIRED)
if (CAF_FOUND)
    message(STATUS "CAF FOUND")
    message(STATUS ${CAF_DIR})
    target_link_libraries(araneae_reasoner CAF::core CAF::io)
else()
    message(FATAL_ERROR "CAF NOT FOUND!")
endif (CAF_FOUND)