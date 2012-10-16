Lesson 0: Configuration and Setup
---------------------------------

Step 1: Create a new Maven project

File->New Project->Maven Project
click "Next"
check "Create a simple project (skip archetype selection)"

for Group Id, enter "edu.colorado.clear.tutorial"
for Artifact Id, enter "ClearTkTutorial"

under Parent Project
Group Id: org.cleartk
Artifact Id: cleartk-release
Version: 1.2.0


Step 2: Configure project

edit pom.xml in the root directory of ClearTkTutorial

Click on the dependencies tab and add the following:

groupId: org.cleartk, artifactId: cleartk-util
groupId: org.cleartk, artifactId: cleartk-typesystem
groupId: org.cleartk, artifactId: cleartk-ml
groupId: org.cleartk, artifactId: cleartk-ml-opennlp-maxent
groupId: org.cleartk, artifactId: cleartk-ml-opennlp-eval

Step 3: Download and copy necessary files:

* Create package in src/main/java called edu.colorado.clear.tutorial
* Copy TweetPosAnnotatorReader to edu.colorado.clear.tutorial package

* Download tweet-pos data from http://code.google.com/p/ark-tweet-nlp/downloads/detail?name=twpos-data-v0.2.1.tar.gz
* Unzip contents into the src/main/resources directory of your ClearTkTutorial project





Step 1: Create a new Maven project

File->New Project->Maven Project
click "Next"
check "Create a simple project (skip archetype selection)"

for Group Id, enter "edu.colorado.clear.tutorial"
for Artifact Id, enter "ClearTkTutorial"

under Parent Project
Group Id: org.cleartk
Artifact Id: cleartk-release
Version: 1.2.0


Step 2: Configure project

edit pom.xml in the root directory of ClearTkTutorial

Click on the dependencies tab and add the following:

groupId: org.cleartk, artifactId: cleartk-util
groupId: org.cleartk, artifactId: cleartk-typesystem
groupId: org.cleartk, artifactId: cleartk-ml
groupId: org.cleartk, artifactId: cleartk-ml-opennlp-maxent
groupId: org.cleartk, artifactId: cleartk-ml-opennlp-eval

Right click on your project -> Maven -> Update Project Configuration
Right click on your project -> Maven -> Update Dependencies

Step 3: Download and copy necessary files:

* Create package in src/main/java called edu.colorado.clear.tutorial.tweetpos
* Copy TweetPosAnnotatorReader to edu.colorado.clear.tutorial.tweetpos package

* Download tweet-pos data from http://code.google.com/p/ark-tweet-nlp/downloads/detail?name=twpos-data-v0.2.1.tar.gz
* Unzip contents into the src/main/resources directory of your ClearTkTutorial project


Lesson 1: Querying CAS Contents
-------------------------------

This lesson should teach you how to query the CAS for Annotations and their associated properties (known as Features)


* Create a new class called DumpTweetPos
* Make it inherit from org.uimafit.component.JCasAnnotator_ImplBase
* Now create a method with the following signature:

    @Override
	public void process(JCas jCas) throws AnalysisEngineProcessException

* This method should iterate over and print out all of the "Sentences" in the CAS.  Hint: Try playing with JCasUtil.select(...)
* Once you have dumped out sentences, dump out all of the Tokens and their corresponding POS tags.
  Hint 1: Try playing with JCasUtil.selectCovered(...)
  Hint 2: Look at the methods within the Token class
  

Lesson 2: Running a pipeline
----------------------------
This lesson should teach you how to create a pipeline that reads in the tweet-pos data into the CAS and then subsequently calls
your DumpTweetPos annotator.


* Create a new class called RunDumpTweetPos, check public static void(main String[] args) before clicking Finish
* Look at the signatures for SimplePipeline.runPipeline(...).  
  - What do you need to call this method?
* Create a CollectionReaderDescription object
  Hint 1: UriCollectionReader.getDescriptionFromFiles(...)
  Hint 2: Arrays.asList

* Build an aggregate analysis engine.
  There are several reasons to use aggregate engines, that are beyond the scope of this lesson.  For now start by creating a new
  AggregateBuilder
* You will need to add descriptions of the following analysis engines (Annotators):
  - UriToDocumentTextAnnotator
  - TweetPosReaderAnnotator
  - DumpTweetPos 


Lesson 3: Building a ClearTK Annotator
--------------------------------------

This lesson should teach you to build your own classifier based annotators, by using CleartkAnnotator, Features, and Instances.

* Create a new class called TweetPosTagger. Inherit from CleartkAnnotator<String>
* Implement the process method

* Things to know
Instance<String> instance = new Instance<String>()
instance.addAll(...)
new Feature(name, value)
this.isTraining()
this.dataWriter.write(instance)
this.classifier.classify(instance)

* Advanced stuff
CleartkExtractor
FeatureFunctionExtractor


Lesson 4: Evaluation
--------------------

This lesson will teach you how to organize your code and pipelines for evaluation of NLP components

* Create a new class called TweetPosEval.  Inherit from Evaluation_ImplBase<File, AnnotationStatistics<String>>

* Implement getCollectionReader
  Hint: Look at your RunDumpTweetPosPipeline and combine it with CollectionReaderFactory.createCollectionReader

* Implement your training pipeline in the train() method
  Hint 1: This will look very similar to RunDumpTweetPosPipeline
  Hint 2: Train.main() is what actually packages your extracted data into a model.jar file

  * Things to know:
    - DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME
    - MaxenStringOutcomeDataWriter.class
    - DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,

* Implement your testing/evaluation pipeine in the test() method

* Implement a main method that class your new evaluation class.

