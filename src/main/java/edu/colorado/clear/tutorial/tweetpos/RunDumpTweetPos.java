package edu.colorado.clear.tutorial.tweetpos;

import java.io.File;
import java.util.Arrays;

import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.cleartk.util.ae.UriToDocumentTextAnnotator;
import org.cleartk.util.cr.UriCollectionReader;
import org.uimafit.component.ViewCreatorAnnotator;
import org.uimafit.factory.AggregateBuilder;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.pipeline.SimplePipeline;

public class RunDumpTweetPos {

	public static void main(String[] args) throws Exception {
		File file = new File("src/main/resources/twpos-data-v0.2.1/train");
		CollectionReader reader = UriCollectionReader
				.getCollectionReaderFromFiles(Arrays.asList(file));
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				ViewCreatorAnnotator.class,
				ViewCreatorAnnotator.PARAM_VIEW_NAME,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
		builder.add(UriToDocumentTextAnnotator.getDescription(),
				CAS.NAME_DEFAULT_SOFA,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW);
		builder.add(AnalysisEngineFactory.createPrimitiveDescription(
				TweetPosReaderAnnotator.class,
				TweetPosReaderAnnotator.PARAM_INPUT_VIEW,
				TweetPosReaderAnnotator.TWEET_POS_CONLL_VIEW));
		builder.add(AnalysisEngineFactory
				.createPrimitiveDescription(DumpTweetPos.class));
		SimplePipeline.runPipeline(reader, builder.createAggregate());
	}

}
