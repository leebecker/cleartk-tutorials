package edu.colorado.clear.tutorial.tweetpos;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.cleartk.classifier.jar.DefaultDataWriterFactory;
import org.cleartk.classifier.jar.DirectoryDataWriterFactory;
import org.cleartk.classifier.jar.GenericJarClassifierFactory;
import org.cleartk.classifier.jar.Train;
import org.cleartk.classifier.opennlp.MaxentStringOutcomeDataWriter;
import org.cleartk.eval.AnnotationStatistics;
import org.cleartk.eval.Evaluation_ImplBase;
import org.cleartk.token.type.Token;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.cr.UriCollectionReader;
import org.uimafit.component.ViewCreatorAnnotator;
import org.uimafit.component.ViewTextCopierAnnotator;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.JCasIterable;
import org.uimafit.pipeline.SimplePipeline;
import org.uimafit.util.JCasUtil;

import com.google.common.base.Function;

public class TweetPosEval extends Evaluation_ImplBase<File, AnnotationStatistics<String>> {
	
	
	private static final String GOLD_VIEW_NAME = "TweetPosGoldView";
	
	private static final String SYSTEM_VIEW_NAME = CAS.NAME_DEFAULT_SOFA;


	public TweetPosEval(File baseDirectory) {
		super(baseDirectory);
	}

	@Override
	protected CollectionReader getCollectionReader(List<File> files)
			throws Exception {
		return CollectionReaderFactory.createCollectionReader(UriCollectionReader.getDescriptionFromFiles(files));
	}

	@Override
	protected AnnotationStatistics<String> test(CollectionReader reader, File directory)
			throws Exception {
		
	    AggregateBuilder builder = new AggregateBuilder();
	    
	    // Read contents of file in CONLL view
	    builder.add(UriToDocumentTextAnnotator.getDescriptionForView(TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
	    
	    
	    // Ensure views are created
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(
	    				ViewCreatorAnnotator.class, 
	    				ViewCreatorAnnotator.PARAM_VIEW_NAME, 
	    				GOLD_VIEW_NAME));

	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(
	    				ViewCreatorAnnotator.class, 
	    				ViewCreatorAnnotator.PARAM_VIEW_NAME, 
	    				SYSTEM_VIEW_NAME));


	    // Parse CONLL text with POS tags into Gold View 
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(TweetPosReaderAnnotator.class), 
	    		CAS.NAME_DEFAULT_SOFA,
	    		GOLD_VIEW_NAME
	    		);

	    // Copy text from gold view into the system view
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(ViewTextCopierAnnotator.class, 
	    				ViewTextCopierAnnotator.PARAM_SOURCE_VIEW_NAME,
	    				GOLD_VIEW_NAME,
	    				ViewTextCopierAnnotator.PARAM_DESTINATION_VIEW_NAME,
	    				SYSTEM_VIEW_NAME));


	    // Copy sentences and tokens from the gold view into the system view
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(CopySentenceAndTokenAnnotations.class, 
	    				CopySentenceAndTokenAnnotations.PARAM_SOURCE_VIEW_NAME,
	    				GOLD_VIEW_NAME,
	    				CopySentenceAndTokenAnnotations.PARAM_DESTINATION_VIEW_NAME,
	    				SYSTEM_VIEW_NAME,
	    				CopySentenceAndTokenAnnotations.PARAM_COPY_POS_TAGS,
	    				false));
	    
	    
	    // Create POS tagger configured for classification
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(TweetPosTagger.class, 
	            GenericJarClassifierFactory.PARAM_CLASSIFIER_JAR_PATH,
	            new File(directory, "model.jar").getPath()));

	    // Create an object to keep track of accuracy, precision, recall, etc.
	    AnnotationStatistics<String> stats = new AnnotationStatistics<String>();
	    Function<Token, ?> getSpan = AnnotationStatistics.annotationToSpan();
	    Function<Token, String> getPos = AnnotationStatistics.annotationToFeatureValue("pos");
	    
	    // Run aggregate engine over data from reader, and collect evaluation stats
	    for (JCas jCas : new JCasIterable(reader, builder.createAggregate())) {
	    	JCas goldView;
	    	JCas systemView;
	    	try {
	    		goldView = jCas.getView(GOLD_VIEW_NAME);
	    		systemView = jCas.getView(SYSTEM_VIEW_NAME);
	    	} catch (CASException e) {
	    		throw new AnalysisEngineProcessException(e);
	    	}

	    	Collection<Token> goldTokens = JCasUtil.select(goldView, Token.class);
	    	Collection<Token> systemTokens = JCasUtil.select(systemView, Token.class);
	    	stats.add(goldTokens, systemTokens, getSpan, getPos);
	    }
	
	    return stats;
	}

	@Override
	protected void train(CollectionReader reader, File directory) throws Exception {
		AggregateBuilder builder = new AggregateBuilder();
		
		// Read text from URI location
	    builder.add(UriToDocumentTextAnnotator.getDescriptionForView(TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
	    
	    // Parse tweet POS CONLL format into plain text within
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(TweetPosReaderAnnotator.class));

	    // Create POS tagger configured for training
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(TweetPosTagger.class, 
	            DefaultDataWriterFactory.PARAM_DATA_WRITER_CLASS_NAME,
	    		MaxentStringOutcomeDataWriter.class,
	            DirectoryDataWriterFactory.PARAM_OUTPUT_DIRECTORY,
	            directory.getPath())
	    );
	    
	    // Run the pipeline.  This should output your training data in the output directory
	    SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
	    
	    
	    // Train and package the model
	    Train.main(directory);
	}

	
	public static void main(String[] args) throws Exception {
		
		File dataDir = new File("twpos-data-v0.2.1");
	    List<File> trainFiles = Arrays.asList(new File(dataDir, "train"));
	    List<File> testFiles = Arrays.asList(new File(dataDir, "dev"));
	    
	    TweetPosEval evaluation = new TweetPosEval(new File("target/models"));
	    
	    // Run and Evaluate on Holdout Set
	    AnnotationStatistics<String> holdoutStats = evaluation.trainAndTest(trainFiles, testFiles);
	    System.err.println("Holdout Set Results:");
	    System.err.print(holdoutStats);
	    System.err.println();
	    System.err.println(holdoutStats.confusions());
		
	}
}
