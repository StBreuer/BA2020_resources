# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.16

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /home/steffen/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/bin/cmake/linux/bin/cmake

# The command to remove a file.
RM = /home/steffen/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/bin/cmake/linux/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug

# Utility rule file for jClingo.

# Include the progress variables for this target.
include CMakeFiles/jClingo.dir/progress.make

CMakeFiles/jClingo: jClingo.jar


jClingo.jar: CMakeFiles/jClingo.dir/resources/libclingo-java.so
jClingo.jar: CMakeFiles/jClingo.dir/java_class_filelist
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold --progress-dir=/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Creating Java archive jClingo.jar"
	cd /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles/jClingo.dir && /usr/bin/jar -cf /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/jClingo.jar resources/libclingo-java.so @java_class_filelist
	cd /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles/jClingo.dir && /home/steffen/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/bin/cmake/linux/bin/cmake -D_JAVA_TARGET_DIR=/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug -D_JAVA_TARGET_OUTPUT_NAME=jClingo.jar -D_JAVA_TARGET_OUTPUT_LINK= -P /home/steffen/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/bin/cmake/linux/share/cmake-3.16/Modules/UseJavaSymlinks.cmake

CMakeFiles/jClingo.dir/resources/libclingo-java.so: ../resources/libclingo-java.so
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold --progress-dir=/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Copying resources/libclingo-java.so to the build directory"
	/home/steffen/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/bin/cmake/linux/bin/cmake -E copy_if_different /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/resources/libclingo-java.so /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles/jClingo.dir/resources/libclingo-java.so

CMakeFiles/jClingo.dir/java_class_filelist: CMakeFiles/jClingo.dir/java_compiled_jClingo
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold --progress-dir=/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Generating CMakeFiles/jClingo.dir/java_class_filelist"
	cd /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface && /home/steffen/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/bin/cmake/linux/bin/cmake -DCMAKE_JAVA_CLASS_OUTPUT_PATH=/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles/jClingo.dir -DCMAKE_JAR_CLASSES_PREFIX="" -P /home/steffen/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/bin/cmake/linux/share/cmake-3.16/Modules/UseJavaClassFilelist.cmake

CMakeFiles/jClingo.dir/java_compiled_jClingo: ../src/clingo/java/rulewerk/clingo/Clingo.java
CMakeFiles/jClingo.dir/java_compiled_jClingo: ../src/clingo/java/rulewerk/clingo/Rule.java
CMakeFiles/jClingo.dir/java_compiled_jClingo: ../src/clingo/java/rulewerk/clingo/Term.java
CMakeFiles/jClingo.dir/java_compiled_jClingo: ../src/clingo/java/rulewerk/clingo/TermQueryResultIterator.java
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --blue --bold --progress-dir=/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Building Java objects for jClingo.jar"
	cd /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface && /usr/bin/javac -classpath :/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface:/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug -d /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles/jClingo.dir -h /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/src/clingo/java/native/ @/home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles/jClingo.dir/java_sources
	cd /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface && /home/steffen/.local/share/JetBrains/Toolbox/apps/CLion/ch-0/201.6668.126/bin/cmake/linux/bin/cmake -E touch /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles/jClingo.dir/java_compiled_jClingo

jClingo: CMakeFiles/jClingo
jClingo: jClingo.jar
jClingo: CMakeFiles/jClingo.dir/resources/libclingo-java.so
jClingo: CMakeFiles/jClingo.dir/java_class_filelist
jClingo: CMakeFiles/jClingo.dir/java_compiled_jClingo
jClingo: CMakeFiles/jClingo.dir/build.make

.PHONY : jClingo

# Rule to build all files generated by this target.
CMakeFiles/jClingo.dir/build: jClingo

.PHONY : CMakeFiles/jClingo.dir/build

CMakeFiles/jClingo.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/jClingo.dir/cmake_clean.cmake
.PHONY : CMakeFiles/jClingo.dir/clean

CMakeFiles/jClingo.dir/depend:
	cd /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug /home/steffen/Documents/Uni/sem6/BA/pushWontWork/BA2020_resources/ClingoInterface/cmake-build-debug/CMakeFiles/jClingo.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/jClingo.dir/depend

