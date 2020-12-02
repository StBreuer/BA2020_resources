cmake_minimum_required(VERSION 3.1)

project(clingo-in-rulewerk VERSION "1.0" LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(Clingo_DIR "/home/steffen/anaconda3/lib/cmake/Clingo")

find_package(Clingo REQUIRED)

#Java and JNI
#IF (JAVA)
find_package(Java REQUIRED)
include(UseJava)
find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})
#ENDIF()

#include_directories(${JNI_INCLUDE_DIRS} ${_classDir} ${_stubDir})


include_directories(${CMAKE_SOURCE_DIR}/include/)
file( GLOB cppSRC "src/clingo/java/native/*.cpp")
add_library(clingo-java SHARED ${cppSRC})
target_link_libraries(clingo-java ${JNI_LIBRARIES} libclingo)


set(COMPILER_FLAGS "${COMPILER_FLAGS} -fPIC")
set_target_properties(clingo-java PROPERTIES COMPILE_FLAGS "${COMPILE_FLAGS}")

#make .jar
file(GLOB javaSRC "src/clingo/java/rulewerk/clingo/*.java" )
#need to add javadoc
target_link_libraries(clingo-java)

#copy hacky trick
#IF(Java)
add_custom_command(TARGET clingo-java COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_SOURCE_DIR}/resources)
add_custom_command(TARGET clingo-java COMMAND ${CMAKE_COMMAND} -E copy $<TARGET_FILE:clingo-java> ${CMAKE_SOURCE_DIR}/resources)

add_jar(jClingo ${javaSRC} resources/${CMAKE_SHARED_LIBRARY_PREFIX}clingo-java${CMAKE_SHARED_LIBRARY_SUFFIX} GENERATE_NATIVE_HEADERS headers DESTINATION ${CMAKE_CURRENT_SOURCE_DIR}/src/clingo/java/native/)
#add_custom_command(TARGET jClingo POST_BUILD COMMAND ${CMAKE_COMMAND} -E remove_directory ${CMAKE_SOURCE_DIR}/resources)
#add_custom_command(TARGET jClingo POST_BUILD COMMAND ${CMAKE_COMMAND} -E remove $<TARGET_FILE:clingo-java>)
add_dependencies(jClingo clingo-java)
#ENDIF()



