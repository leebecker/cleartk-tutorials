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
* Use the CollectionReaderFactory to create a collection reader.
  - We are adopting the path of using the UriCollectionReader as the reader and using Annotators to populate the CAS
  Hint 1: Look at the static methods in UriCollectionReader 
  Hint 2: Arrays.asList

* Build an aggregate engine.
  There are several reasons to use aggregate engines, that are beyond the scope of this lesson.  For now start by creating a new
  AggregateBuilder
* You will need to add descriptions of the following analysis engines (Annotators):
  - UriToDocumentTextAnnotator
  - TweetPosReaderAnnotator
  - DumpTweetPos 

