package edu.colorado.clear.tweetpos;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.cr.UriCollectionReader;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.CollectionReaderFactory;
import org.uimafit.pipeline.SimplePipeline;

public class RunTweetPos {

	
	public static void main(String[] args) throws UIMAException, IOException  {
		
		CollectionReader reader = CollectionReaderFactory.createCollectionReader(
				UriCollectionReader.getDescriptionFromFiles(Arrays.asList(new File("twpos-data-v0.2.1/train"))));
		
	    AggregateBuilder builder = new AggregateBuilder();
	    
	    builder.add(UriToDocumentTextAnnotator.getDescriptionForView(TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(TweetPosReaderAnnotator.class)); 
	    builder.add(AnalysisEngineFactory.createPrimitiveDescription(DumpTweetPos.class)); 
	    
	    SimplePipeline.runPipeline(reader, builder.createAggregateDescription());
	}
	
}
