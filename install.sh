mvn install:install-file \
   -Dfile=lib/ruleLearn.jar \
   -DgroupId=org.rulelearn \
   -DartifactId=rule-learn \
   -Dversion=0.14.2 \
   -Dpackaging=jar \
   -DgeneratePom=true

mvn clean install