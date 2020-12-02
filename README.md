# BA2020_resources
For some reason I was not able to push the rulewerk code the rulewerk-clingo package is part of.
So in order to use this code you first need to clone this repo https://github.com/knowsys/rulewerk.git
Then add the the clingo-rulewerk folder into the just cloned repo.
After wards add <module>rulewerk-clingo</module> into the modules tag of the pom.xml file of the rulewerk project.

In order to test the just the code on the benchmark, that is contained in this repo,
first add the ABox and TBox folder to the rulewerk-examples/input folder and then add the clingo folder,
which is contained in the example folder of this repo into the rulewerk-examples/src/main/java/org/semanticweb/rulewerk/examples folder,
of the rulewerk repo.

Then you can run the main method of the ClingoVsVLog class. 
